package org.retro.common.impl.amiga.adf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
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
        FILE,
        DIR
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

        AdfHeaderBlock headerBlock = readHeaderBlock(sector);

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
        return type == 4294967293L ? ENTRY_TYPE.FILE : ENTRY_TYPE.DIR;
    }





    private AdfHeaderBlock readHeaderBlock(int sector) {
        imageData.position(sector * Adf.SECTOR_SIZE);

        AdfHeaderBlock headerBlock = new AdfHeaderBlock();

        headerBlock.setType(imageData.getInt());
        headerBlock.setHeaderSector(imageData.getInt());
        headerBlock.setDataBlockCount(imageData.getInt());
        headerBlock.setDataSize(imageData.getInt());
        headerBlock.setFirstDataBlock(imageData.getInt());
        headerBlock.setChecksum(imageData.getInt());

        int[] pointers = new int[72];
        for(int i = 0; i < 72; i++) {
            pointers[i] = imageData.getInt();
        }
        headerBlock.setPointers(pointers);

        AdfBlockItem item = new AdfBlockItem();
        headerBlock.getItems().add(item);

        // Item size
        imageData.position((sector * Adf.SECTOR_SIZE) + Adf.SECTOR_SIZE - 188);
        item.setSize(imageData.getInt());


        // ... TODO
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
