/*
 * (C) Copyright ${year} retro-io (https://github.com/marcelschoen/retro-io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.retro.common.impl.atarist;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Reads an MSA floppy disk image file.
 *
 * @author Marcel Schoen
 */
public class StxDisk extends AbstractBaseStDisk {

    private enum TYPE {

        /** Discovery Cartridge generated */
        DC,

        /** Generated on a normal ST */
        PUBLIC
    }

    private int tracks = -1;
    private boolean doubleSided = false;
    private TYPE type = null;
    private File imageFile;
    private ByteBuffer stxFileData;
    private ByteBuffer rawData;

    // Maps which store the tracks with their data
    private Map<Integer, byte[]> tracksSideOne = new HashMap<>();
    private Map<Integer, byte[]> tracksSideTwo = new HashMap<>();

    /**
     * Creates an STX disk object.
     *
     * @param imageFile The STX floppy disk image file to read.
     */
    public StxDisk(File imageFile) {
        this.imageFile = imageFile;
    }

    /**
     * Loads the MSA image; reads the MSA header, and then all
     * the tracks, decompressing them if necessary. After that,
     * the resulting data is basically an ".ST" image (raw disk data),
     * so it can be read with the "fat32"-lib code.
     *
     * @throws IOException If the MSA image could not be processed.
     */
    public void load() throws IOException {
        byte[] rawData = new byte[(int) this.imageFile.length()];
        try (FileInputStream in = new FileInputStream(this.imageFile)) {
            in.read(rawData);
            stxFileData = ByteBuffer.wrap(rawData);
            stxFileData.order(ByteOrder.LITTLE_ENDIAN);

            // ================================================================
            // Process File Header
            // ================================================================

            // Read file header
            byte[] fileId = new byte[4];
            stxFileData.get(fileId);
            System.out.println("FileID: " + new String(fileId));

            // Skip version
            int version = stxFileData.getShort() & 0xffff;
            System.out.println("Version: " + version);
            if(version != 3) {
                throw new IOException("Unknown / invalid STX file format version: " + version);
            }

            // Read type (public aka generated on ST, or Discovery Cartridge aka DC).
            int type = stxFileData.getShort();
            if( (type & 0x0001) > 0) {
                System.out.println("Type: public");
                this.type = TYPE.PUBLIC;
            } else {
                System.out.println("Type: DC");
                this.type = TYPE.DC;
            }

            // Skip unknown word
            stxFileData.getShort();

            // Read number of tracks
            this.tracks = stxFileData.get() & 0xFF;
            System.out.println("Tracks: " + tracks);
            if(this.tracks == 160) {
                this.doubleSided = true;
                System.out.println("Is double sided.");
            }

            // Skip file revision number and reserved value
            stxFileData.position(stxFileData.position() + 5);

            int totalSize = 0;
            for(int track = 0; track < this.tracks; track ++) {
                totalSize += readTrack(track);
            }

            // Now merge all the data from all the tracks into one raw data block
            System.out.println("================================");
            System.out.println("Number of tracks: " + this.tracks);
            System.out.println("================================");
            this.rawData = ByteBuffer.allocate(totalSize);
            for(int trackNo = 0; trackNo < this.tracks / 2; trackNo ++) {
//                System.out.println(">>>>>>>>>>> collect data of track " + trackNo);
                byte[] srcData = this.tracksSideOne.get(trackNo);
                this.rawData.put(srcData, 0, srcData.length);
                if(this.doubleSided) {
                    srcData = this.tracksSideTwo.get(trackNo);
                    this.rawData.put(srcData, 0, srcData.length);
                }
            }
            this.rawData.position(0);
        }
    }

    private int readTrack(int num) throws IOException {
        int trackSize = 0;

        // ===========================================================================
        // TRACK DESCRIPTOR
        // ===========================================================================

        System.out.println("-------------- TRACK " + num + " ------------------------");

        int trackStart = this.stxFileData.position();

        int trackRecordSize = this.stxFileData.getInt() & 0xffffffff;
        System.out.println("Track record size: " + trackRecordSize);

        int fuzzyCount = this.stxFileData.getInt() & 0xffffffff;
        if(fuzzyCount > 0) {
            // TODO - FUZZY NOT IMPLEMENTED
            throw new IOException("STX image with fuzzy bytes not supported.");
        }
        System.out.println("Fuzzy mask record: " + fuzzyCount);

        int sectorCount = this.stxFileData.getShort() & 0xffff;
        System.out.println("Sector count: " + sectorCount);

        int trackFlags = this.stxFileData.getShort() & 0xffff;
        boolean hasOptionalTrackImage = (trackFlags & 0x0040) > 0;
        System.out.println("Has optional track image: " + hasOptionalTrackImage);

        boolean fourByteTrackImageHeader = false;
        if(hasOptionalTrackImage) {
            if((trackFlags & 0x0080) > 0) {
                fourByteTrackImageHeader = true;
            }
        }
        System.out.println("4 byte track image header: " + fourByteTrackImageHeader);

        boolean hasSectorDescriptors = (trackFlags & 0x01) > 0;
        System.out.println("Has sector descriptors: " + hasSectorDescriptors);

        int trackLength = this.stxFileData.getShort() & 0xffff;
        System.out.println("Track length: " + trackLength);

        int trackInfo = this.stxFileData.get();
        int trackNumber = trackInfo & 0x7f;
        int side = (trackInfo & 0x80) >> 7;
        System.out.println("Track number: " + trackNumber + ", side: " + side);

        // skip track type (unused)
        this.stxFileData.get();

        int trackDataOffset = 16;

        // ===========================================================================
        // OPTIONAL SECROT DESCRIPTORS
        // ===========================================================================
        if(hasSectorDescriptors) {
            // Read optional sector descriptor headers
            trackDataOffset += sectorCount * 16;
            ArrayList<Sector> sectors = new ArrayList<>();
            int totalSectorDataSize = 0;
            // Read optional sector descriptors
            for(int s = 0; s < sectorCount; s ++) {
                long dataOffset = (this.stxFileData.getShort() & 0xffff) << 16;
                dataOffset += this.stxFileData.getShort() & 0xffff;
                System.out.println("------ sector " + s + " -----");
                System.out.println("data offset: " + dataOffset);
                int bitPosition = this.stxFileData.getShort() & 0xffff;
//                System.out.println("bit position: " + bitPosition);
                int readTime  = this.stxFileData.getShort() & 0xffff;
//                System.out.println("read time: " + readTime);

                // read address info
                int track = this.stxFileData.get() & 0xff;
                int head = this.stxFileData.get() & 0xff;
                int sectorNumber = this.stxFileData.get() & 0xff;
                int size = 128 << (this.stxFileData.get() & 0xff);
                totalSectorDataSize += size;
                System.out.println("track: " + track + ", side: " + head + ", sector: " + sectorNumber + ", size: " + size);
//                System.out.println("size: " + size);
                int crc = this.stxFileData.getShort() & 0xffff;

                int fdcFlags = this.stxFileData.get() & 0xff;
                boolean isDeletedData = (fdcFlags & 0x20) > 0;
                boolean recordNotFound = (fdcFlags & 0x10) > 0;
                boolean intraSectorBitWidthVariation = (fdcFlags & 0x01) > 0;
                if(intraSectorBitWidthVariation) {
                    throw new IOException("Copy protected STX images not supported");
                }

                // skip reserved byte
                this.stxFileData.get();

                // Read sector data
                sectors.add(new Sector(this.stxFileData, trackStart, sectorNumber, size));
            }
            byte[] totalSectorData = new byte[totalSectorDataSize];
            int offset = 0;
            for(Sector sector : sectors) {
                System.arraycopy(sector.getData(), 0, totalSectorData, offset, sector.getData().length);
                offset += sector.getData().length;
            }
            trackSize = totalSectorData.length;
            System.out.println("*** STORE TRACK " + trackNumber + ", SIDE " + side);
            if(side == 0) {
                this.tracksSideOne.put(trackNumber, totalSectorData);
            } else {
                this.tracksSideTwo.put(trackNumber, totalSectorData);
            }
        }

        if(hasOptionalTrackImage) {
            // ===========================================================================
            // OPTIONAL TRACK IMAGE RECORD HEADER
            // ===========================================================================

            System.out.println("------ Optional track image ------");
            // Read track image header
            if(fourByteTrackImageHeader) {
                int firstSyncOffset = this.stxFileData.getShort() & 0xffff;
                System.out.println("First sync offset: " + firstSyncOffset);
            }
            int trackImageSize = this.stxFileData.getShort() & 0xffff;

            byte[] totalSectorData = new byte[trackImageSize];
            this.stxFileData.get(totalSectorData);
            System.out.println("track image size: " + trackImageSize);
            if(side == 0) {
                this.tracksSideOne.put(trackNumber, totalSectorData);
            } else {
                this.tracksSideTwo.put(trackNumber, totalSectorData);
            }

            // ===========================================================================
            // OPTIONAL TRACK IMAGE RECORD DATA
            // ===========================================================================

        } else {

            // ===========================================================================
            // OPTIONAL SECTOR IMAGE DATA
            // ===========================================================================

        }

        this.stxFileData.position(trackStart + trackRecordSize);
        return trackSize;
    }

    private class Sector {
        private byte[] data;

        public Sector(ByteBuffer stxFileData, int trackStart, int sectorNumber, int size) {
            // Store original position
            int currentPosition = stxFileData.position();
            int trackDataOffset = 16 + sectorNumber * 16;
System.out.println("read sector " + sectorNumber + ", size: " + size);
            stxFileData.position(trackStart + trackDataOffset);
            data = new byte[size];
            stxFileData.get(data);

            // Reset to original position
            stxFileData.position(currentPosition);
        }
        public byte[] getData() {
            return this.data;
        }
    }

    private byte[] readTrackImage(boolean fourByteTrackImageHeader) {
        byte[] data = new byte[0];
        if(fourByteTrackImageHeader) {

        }
        return data;
    }

    @Override
    protected ByteBuffer getData() {
        return this.rawData;
    }

}
