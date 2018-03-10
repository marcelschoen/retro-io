package org.retro.common.impl.amiga.adf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.retro.common.impl.amiga.adf.Adf.SECTOR_SIZE;
import static org.retro.common.impl.amiga.adf.AdfImageInfo.DISK_TYPE;

/**
 * Reads and extracts data from Amiga ADF images. Uses Java ByteBuffer to process
 * the floppy image data.
 *
 * ByteBuffer.get() -> read signed byte (need to AND for unsigned value checks)
 * ByteBuffer.getShort() -> read Amiga "word" (2 bytes)
 * ByteBuffer.getInt() -> read Amiga "long" (4 bytes)
 *
 * @author Marcel Schoen
 */
public class AdfImage {

    private ByteBuffer imageData;

    private AdfImageInfo imageInfo;

    public enum ENTRY_TYPE {
        ROOT(1),
        FILE(-3),
        USERDIR(2),
        SOFTLINK(3),
        LINKFILE(-4),
        LINKDIR(4);

        private int type;

        ENTRY_TYPE(int type) {
            this.type = type;
        }

        public int getType() {
            return this.type;
        }

        public static ENTRY_TYPE toType(long type) {
            return Arrays.stream(values())
                    .filter(v -> v.getType() == type)
                    .findFirst()
                    .orElse(null);
        }
    }

    public AdfImage(File imageFile) throws IOException {
        byte[] fileData = new byte[(int)imageFile.length()];
        try (FileInputStream in = new FileInputStream(imageFile)) {
            in.read(fileData);
        }
        this.imageData = ByteBuffer.wrap(fileData);
        readBootBlock();

        // Read root folder
        AdfDirectory directory = readFolderAtSector(880);
    }

    /**
     * Processes the boot block of the floppy image.
     *
     * @throws IOException
     */
    private void readBootBlock() throws IOException {
        AdfImageInfo imageInfo = new AdfImageInfo();
        imageData.position(0);
        String imageFormat = Adf.readString(imageData, 3);
        imageInfo.setDiskFormat(imageFormat);
        if(!imageFormat.equals("DOS")) {
            throw new IOException("Unsupported image format '" + imageFormat
                    + "', only AmigaDOS images supported");
        }

        byte diskFlags = imageData.get();
        imageInfo.setDiskType(Adf.isBitSet(diskFlags, 0) ? DISK_TYPE.FFS : DISK_TYPE.OFS);
        imageInfo.setINTL(Adf.isBitSet(diskFlags, 1) ? true : false);
        imageInfo.setDIRC(Adf.isBitSet(diskFlags, 2) ? true : false);

        imageData.getInt(); // skip an ignore checksum

        imageInfo.setRootBlock(imageData.getInt());

        // Read root sector information

        imageData.position((SECTOR_SIZE * 880) + SECTOR_SIZE - 80);
        byte nameLength = imageData.get();

        imageInfo.setLabel(Adf.readString(imageData, nameLength));

        this.imageInfo = imageInfo;
    }

    private AdfDirectory readFolderAtSector(int sector) {
        AdfDirectory directory = new AdfDirectory();

        AdfBlock headerBlock = readHeaderBlock(sector);

        List<PointerEntry> entries = new ArrayList<>();
        for(int sectorPointer : headerBlock.getPointers()) {
            PointerEntry entry = new PointerEntry();
            entry.sector = sectorPointer;
            entry.name = getFileNameAtSector(sectorPointer);
            entry.type = getFileTypeAtSector(sectorPointer);
            entries.add(entry);
        }

        for(PointerEntry entry : entries) {
            if(entry.type == ENTRY_TYPE.FILE) {
                System.out.println("> file: " + entry.name);
            } else {
                System.out.println("> dir: " + entry.name);
            }
        }

        return directory;
    }


    private String getFileNameAtSector(int sector){
        imageData.position((SECTOR_SIZE * sector) + SECTOR_SIZE - 80);
        int nameLength = imageData.get();
        return Adf.readString(imageData, nameLength);

    }

    private ENTRY_TYPE getFileTypeAtSector(int sector){
        imageData.position((SECTOR_SIZE * sector) + SECTOR_SIZE - 4);
        long type = imageData.getInt();
        return ENTRY_TYPE.toType(type);
    }

    private AdfBlock readDataBlock(int sector, int size) {
        AdfBlock block = readBlock(sector);
        if(getImageInfo().getDiskType() == DISK_TYPE.FFS) {
            block.setDataSize(Math.min(size, Adf.SECTOR_SIZE));
            byte[] blockContent = new byte[block.getDataSize()];
            for(int i = 0; i < block.getDataSize(); i++) {
                blockContent[i] = imageData.get();
            }
            block.setContent(blockContent);
        } else {
            block.setType(imageData.getInt());
            block.setHeaderSector(imageData.getInt()); // points to the file HEADER block this data block belongs to
            block.setNumber(imageData.getInt());
            block.setDataSize(imageData.getInt());
            block.setNextDataBlock(imageData.getInt());
            block.setChecksum(imageData.getInt());

            if(block.getType() == 8) {
                byte[] blockContent = new byte[block.getDataSize()];
                imageData.position((sector * SECTOR_SIZE) + 24);
                for(int i = 0; i < block.getDataSize(); i++) {
                    blockContent[i] = imageData.get();
                }
                block.setContent(blockContent);
            } else {
                // invalid file
                block.setContent(new byte[0]);
                block.setDataSize(0);
                block.setNextDataBlock(0);
            }
        }

        return block;
    }

    private AdfBlock readExtentionBlock(int sector) {
        AdfBlock block = readBlock(sector);

        imageData.position(sector * Adf.SECTOR_SIZE + 24);
        int[] pointers = new int[72];
        for(int i = 0; i < 72; i++) {
            // TODO - COULD BE WRONG ("unshift" in JS?)
            pointers[i] = imageData.getInt();
        }
        block.setPointers(pointers);

        imageData.position((sector * Adf.SECTOR_SIZE) + Adf.SECTOR_SIZE - 8);
        AdfBlockItem item = new AdfBlockItem();
        block.getItems().add(item);
        item.setDataBlockExtention(imageData.getInt());

        return block;
    }


    private AdfBlock readHeaderBlock(int sector) {
        AdfBlock headerBlock = readBlock(sector);

        int[] pointers = new int[72];
        for(int i = 0; i < 72; i++) {
            // TODO - COULD BE WRONG ("unshift" in JS?)
            pointers[i] = imageData.getInt();
        }
        headerBlock.setPointers(pointers);

        AdfBlockItem item = new AdfBlockItem();
        headerBlock.getItems().add(item);

        // Item size
        imageData.position((sector * Adf.SECTOR_SIZE) + Adf.SECTOR_SIZE - 188);
        item.setSize(imageData.getInt());
        byte dataLength = imageData.get();
        item.setComment(Adf.readString(imageData, dataLength));

        imageData.position((sector * Adf.SECTOR_SIZE) + Adf.SECTOR_SIZE - 92);
        item.setLastChangeDays(imageData.getInt());
        item.setLastChangeMinutes(imageData.getInt());
        item.setLastChangeTicks(imageData.getInt());

        dataLength = imageData.get();
        item.setName(Adf.readString(imageData, dataLength));

        imageData.position((sector * Adf.SECTOR_SIZE) + Adf.SECTOR_SIZE - 16);
        item.setLinkedSector(imageData.getInt());
        item.setParent(imageData.getInt());
        item.setDataBlockExtention(imageData.getInt());
        item.setType(imageData.getInt());

        return headerBlock;
    }

    private AdfBlock readBlock(int sector) {
        imageData.position(sector * Adf.SECTOR_SIZE);
        AdfBlock headerBlock = new AdfBlock();

        headerBlock.setType(imageData.getInt());
        headerBlock.setHeaderSector(imageData.getInt());
        headerBlock.setDataBlockCount(imageData.getInt());
        headerBlock.setDataSize(imageData.getInt());
        headerBlock.setFirstDataBlock(imageData.getInt());
        headerBlock.setChecksum(imageData.getInt());

        return headerBlock;

    }

    public AdfImageInfo getImageInfo() {
         return this.imageInfo;
    }


    class PointerEntry {
        public int sector;
        public String name;
        public ENTRY_TYPE type;
    }
}
