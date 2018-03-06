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

public class MsaDisk implements BlockDevice {

    private File imageFile;
    private ByteBuffer msaFileData;
    private ByteBuffer rawData;

    public enum TYPE {
        ONE_SIDED,
        TWO_SIDED
    }

    private int sectorsPerTrack;
    private TYPE sides;
    private int startingTrack;
    private int endingTrack;

    private Map<Integer, Track> tracksSideOne = new HashMap<>();
    private Map<Integer, Track> tracksSideTwo = new HashMap<>();

    public MsaDisk(File imageFile) {
        this.imageFile = imageFile;
    }

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

            System.out.println("ID marker: " + Integer.toHexString(idMarker));
            System.out.println("Sectors per track: " + this.sectorsPerTrack);
            System.out.println("Sides: " + this.sides);
            System.out.println("Starting track: " + this.startingTrack);
            System.out.println("Ending track: " + this.endingTrack);

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
                byte[] srcData = this.tracksSideOne.get(trackNo).data;
                this.rawData.put(srcData, 0, srcData.length);
                if(this.sides == TYPE.TWO_SIDED) {
                    srcData = this.tracksSideTwo.get(trackNo).data;
                    this.rawData.put(srcData, 0, srcData.length);
                }
            }
            this.rawData.position(0);
        }
    }

    private int readTrack(int trackNo, Map<Integer, Track> trackMap) {
        int trackDataLength = this.msaFileData.getShort();
        boolean compressed = false;
        if(trackDataLength != this.sectorsPerTrack * 512) {
            compressed = true;
        }
        //if(!compressed) {
            byte[] trackData = new byte[trackDataLength];
            this.msaFileData.get(trackData);
            trackMap.put(trackNo, new Track(false, trackData));
        //}
        return trackData.length;
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

    class Track {

        boolean compressed;
        byte[] data;

        Track(boolean compressed, byte[] data) {
            this.compressed = compressed;
            this.data = data;
        }
    }
}
