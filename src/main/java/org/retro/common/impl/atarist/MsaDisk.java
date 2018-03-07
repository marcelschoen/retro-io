package org.retro.common.impl.atarist;

import de.waldheinz.fs.BlockDevice;
import de.waldheinz.fs.ReadOnlyException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static de.waldheinz.fs.util.FileDisk.BYTES_PER_SECTOR;

/**
 * Reads an MSA floppy disk image file.
 *
 * @author Marcel Schoen
 */
public class MsaDisk implements BlockDevice {

    private File imageFile;
    private ByteBuffer msaFileData;
    private ByteBuffer rawData;

    // Constant for run-length encoding marker
    private static final int RLE_MARKER = 0xe5;

    // Constant for sector size in bytes
    private static final int SECTOR_SIZE = 512;

    public enum TYPE {
        ONE_SIDED,
        TWO_SIDED
    }

    // MSA header fields
    private int sectorsPerTrack;
    private TYPE sides;
    private int startingTrack;
    private int endingTrack;

    // Maps which store the tracks with their data
    private Map<Integer, byte[]> tracksSideOne = new HashMap<>();
    private Map<Integer, byte[]> tracksSideTwo = new HashMap<>();

    /**
     * Creates an MSA disk object.
     *
     * @param imageFile The MSA floppy disk image file to read.
     */
    public MsaDisk(File imageFile) {
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
        byte[] rawData = new byte[(int)this.imageFile.length()];
        try(FileInputStream in = new FileInputStream(this.imageFile)) {
            in.read(rawData);
            this.msaFileData = ByteBuffer.wrap(rawData);

            // Read disk header
            int idMarker = this.msaFileData.getShort();
            if(idMarker != 0x0e0f) {
                throw new IOException("Invalid header; not a valid MSA image: " + this.imageFile.getAbsolutePath());
            }
            this.sectorsPerTrack = this.msaFileData.getShort();
            int sideValue = this.msaFileData.getShort();
            if(sideValue == 0) {
                this.sides = TYPE.ONE_SIDED;
            } else {
                this.sides = TYPE.TWO_SIDED;
            }
            this.startingTrack = this.msaFileData.getShort();
            this.endingTrack = this.msaFileData.getShort();

            // Read all tracks
            int totalSize = 0;
            for(int trackNo = this.startingTrack; trackNo <= this.endingTrack; trackNo ++) {
                totalSize += readTrack(trackNo, this.tracksSideOne);
                if(this.sides == TYPE.TWO_SIDED) {
                    totalSize += readTrack(trackNo, this.tracksSideTwo);
                }
            }

            // Now merge all the data from all the tracks into one raw data block
            this.rawData = ByteBuffer.allocate(totalSize);
            for(int trackNo = this.startingTrack; trackNo <= this.endingTrack; trackNo ++) {
                byte[] srcData = this.tracksSideOne.get(trackNo);
                this.rawData.put(srcData, 0, srcData.length);
                if(this.sides == TYPE.TWO_SIDED) {
                    srcData = this.tracksSideTwo.get(trackNo);
                    this.rawData.put(srcData, 0, srcData.length);
                }
            }
            this.rawData.position(0);
        }
    }

    /**
     * Reads a single track and stores it in the map.
     *
     * @param trackNo The number of the track to read.
     * @param trackMap The map where the track will be stored.
     * @return The number of bytes read
     */
    private int readTrack(int trackNo, Map<Integer, byte[]> trackMap) {
        int trackDataLength = this.msaFileData.getShort();
        boolean compressed = false;
        if(trackDataLength != this.sectorsPerTrack * SECTOR_SIZE) {
            compressed = true;
        }
        // Prepare buffer to receive plain, decompressed track data
        byte[] trackData = new byte[this.sectorsPerTrack * SECTOR_SIZE];
        if(!compressed) {
            // Just copy 1:1 raw data from disk data buffer to track data buffer
            this.msaFileData.get(trackData);
        } else {
            // Perform RLE decoding on compressed data
            runLengthDecode(this.msaFileData, trackData);
        }
        trackMap.put(trackNo, trackData);
        return trackData.length;
    }

    /**
     * Decodes the run-length encoded compressed data.
     *
     * @param srcData The encoded source data.
     * @param destData The target buffer to receive the decoded data.
     */
    private static void runLengthDecode(ByteBuffer srcData, byte[] destData) {
        int destPointer = 0;
        // Process until target track buffer is filled
        while(destPointer < destData.length) {
            byte srcByte = srcData.get();  // get next byte from disk data buffer
            if( (srcByte & 0xff) != RLE_MARKER) {
                destData[destPointer++] = srcByte; // not an RLE marker, so just store
            } else {
                // RLE marker found
                srcByte = srcData.get();  // get actual data byte value from disk buffer
                short count = srcData.getShort(); // get WORD-count (n x data byte)
                // Write data byte n times to track buffer
                for(int i = 0; i < count; i++) {
                    destData[destPointer++] = srcByte;
                }
            }
        }
    }

    @Override
    public long getSize() throws IOException {
        return this.rawData.capacity();
    }

    @Override
    public int getSectorSize() throws IOException {
        return BYTES_PER_SECTOR;
    }

    @Override
    public void read(long devOffset, ByteBuffer dest) throws IOException {
        int toRead = dest.remaining();
        if ((devOffset + toRead) > getSize()) throw new IOException(
                "reading past end of device");
        dest.put(this.rawData.array(), (int)devOffset, toRead);
    }

    @Override
    public void write(long devOffset, ByteBuffer src) throws ReadOnlyException, IOException, IllegalArgumentException {
        throw new IOException("* NOT IMPLEMENTED *");
    }

    @Override
    public void flush() throws IOException {
        // NOP
    }

    @Override
    public void close() throws IOException {
        // NOP
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public boolean isReadOnly() {
        // TODO - write support
        return true;
    }
}
