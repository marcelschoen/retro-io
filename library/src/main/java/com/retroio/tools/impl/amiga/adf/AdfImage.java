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
package com.retroio.tools.impl.amiga.adf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.retroio.tools.impl.amiga.adf.Adf.SECTOR_SIZE;

/**
 * Reads and extracts data from Amiga ADF images. Uses Java ByteBuffer to process
 * the floppy image data.
 * <p>
 * ByteBuffer.get() -> read signed byte (need to AND for unsigned value checks)
 * ByteBuffer.getShort() -> read Amiga "word" (2 bytes)
 * ByteBuffer.getInt() -> read Amiga "long" (4 bytes)
 *
 * @author Marcel Schoen
 */
public class AdfImage {

    /**
     * Stores the image data.
     */
    private ByteBuffer imageData;

    /**
     * Stores meta image info.
     */
    private AdfImageInfo imageInfo;

    /**
     * Stores the root folder info.
     */
    private AdfDirectory rootFolder;

    /**
     * Creates an ADF image.
     *
     * @param imageFile The image file.
     * @throws IOException
     */
    public AdfImage(File imageFile) throws IOException {
        byte[] fileData = new byte[(int) imageFile.length()];
        try (FileInputStream in = new FileInputStream(imageFile)) {
            in.read(fileData);
        }
        this.imageData = ByteBuffer.wrap(fileData);
        readBootBlock();

        // Read root folder
        this.rootFolder = readFolderAtSector(880);
        processSubDirectories(this.rootFolder);
    }

    /**
     * Copies data from one byte array to another.
     *
     * @param name   The name of the current file.
     * @param src    The source byte array.
     * @param dest   The destination byte array.
     * @param offset The offset in the destination array.
     */
    private static void copyData(String name, byte[] src, byte[] dest, int offset) {
        try {
            System.arraycopy(src, 0, dest, offset, src.length);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Failed to copy data for file " + name + ": " + e);
        }
    }

    /**
     * Reads one 4-byte value with a sector number.
     *
     * @param imageData The image data from which to read.
     * @return The sector number.
     */
    private static int readSector(ByteBuffer imageData) {
        int sector = imageData.getInt();
        return sector;
    }

    private void processSubDirectories(AdfDirectory folder) {
        List<AdfDirectory> updatedSubdirectories = new ArrayList<>();
        for (AdfDirectory subFolder : folder.getSubDirectories()) {
            updatedSubdirectories.add(readFolderAtSector(subFolder.getSector()));
        }
        folder.getSubDirectories().clear();
        folder.getSubDirectories().addAll(updatedSubdirectories);
        for (AdfDirectory subFolder : folder.getSubDirectories()) {
            processSubDirectories(subFolder);
        }
    }

    /**
     * Returns the root folder of this image.
     *
     * @return The root directory.
     */
    public AdfDirectory getRootFolder() {
        return this.rootFolder;
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
        if (!imageFormat.equals("DOS")) {
            throw new IOException("Unsupported image format '" + imageFormat
                    + "', only AmigaDOS images supported");
        }

        byte diskFlags = imageData.get();
        imageInfo.setDiskType(Adf.isBitSet(diskFlags, 0) ? AdfImageInfo.DISK_TYPE.FFS : AdfImageInfo.DISK_TYPE.OFS);
        imageInfo.setINTL(Adf.isBitSet(diskFlags, 1) ? true : false);
        imageInfo.setDIRC(Adf.isBitSet(diskFlags, 2) ? true : false);

        imageData.getInt(); // skip an ignore checksum

        // Read root sector information
        imageInfo.setRootBlock(imageData.getInt());

        imageData.position((SECTOR_SIZE * 880) + SECTOR_SIZE - 80);
        byte nameLength = imageData.get();
        imageInfo.setLabel(Adf.readString(imageData, nameLength));
        this.imageInfo = imageInfo;
    }

    /**
     * Reads a file at the given sector.
     *
     * @param sector         The sector where the file data starts.
     * @param includeContent True if the actual file contents should be read too.
     * @return The file information holder.
     */
    private AdfFile readFileAtSector(int sector, boolean includeContent) {
        AdfFile file = new AdfFile(sector);

        AdfBlock block = readHeaderBlock(sector);
        file.setItem(block.getItem());

        if (includeContent) {
            byte[] newContent = new byte[file.getSize()];
            file.setContent(newContent);
            int index = 0;

            // there are 2 ways to read a file in OFS:
            // 1 is to read the list of datablock pointers and collect each datablock
            // 2 is to follow the linked list of datablocks

            // the second one seems somewhat easier to implement
            // because otherwise we have to collect each extention block first

            int maxSize = file.getSize();
            if (getImageInfo().getDiskType() == AdfImageInfo.DISK_TYPE.FFS) {
                List<Integer> sectors = block.getPointers();
                while (block.getItem().getDataBlockExtention() > 0 && sectors.size() < 2000) {
                    block = readExtentionBlock(block.getItem().getDataBlockExtention());
                    List<Integer> newSctors = block.getPointers();
                    sectors.addAll(newSctors);
                }
                for (Integer sectorEntry : sectors) {
                    block = readDataBlock(sectorEntry, maxSize);
                    copyData(file.getName(), block.getContent(), newContent, index);
                    index += block.getDataSize();
                    maxSize -= block.getDataSize();
                }
            } else {
                int nextBlock = block.getFirstDataBlock();
                while (nextBlock != 0) {
                    block = readDataBlock(nextBlock, SECTOR_SIZE);
                    copyData(file.getName(), block.getContent(), newContent, index);
                    index += block.getDataSize();
                    nextBlock = block.getFirstDataBlock();
                }
            }
        }

        return file;
    }

    /**
     * Reads a folder at a certain sector.
     *
     * @param sector The sector where the folder starts.
     * @return The directory folder.
     */
    private AdfDirectory readFolderAtSector(int sector) {
        AdfDirectory directory = new AdfDirectory(sector);

        AdfBlock headerBlock = readHeaderBlock(sector);
        directory.setItem(headerBlock.getItem());

        List<PointerEntry> entries = new ArrayList<>();
        for (int sectorPointer : headerBlock.getPointers()) {
            PointerEntry entry = new PointerEntry();
            entry.sector = sectorPointer;
            entry.name = getFileNameAtSector(sectorPointer);
            entry.type = getFileTypeAtSector(sectorPointer);
            entries.add(entry);
        }

        List<PointerEntry> moreEntries = new ArrayList<>();
        for (PointerEntry entry : entries) {
            if (entry.type == ENTRY_TYPE.FILE) {
                AdfFile file = readFileAtSector(entry.sector, false);
                if (file.getLinkedSector() != 0) {
                    PointerEntry newEntry = new PointerEntry();
                    newEntry.sector = file.getLinkedSector();
                    newEntry.name = getFileNameAtSector(file.getLinkedSector());
                    newEntry.type = getFileTypeAtSector(file.getLinkedSector());
                    moreEntries.add(newEntry);
                }
            }
        }
        entries.addAll(moreEntries);

        for (PointerEntry entry : entries) {
            if (entry.type == ENTRY_TYPE.FILE) {
                AdfFile file = readFileAtSector(entry.sector, true);
                directory.getFileEntries().add(file);
            } else {
                if (!entry.name.isEmpty()) { // TODO - WHY ARE MANY ENTRIES EMPTY ?????
                    AdfDirectory adfDirectory = new AdfDirectory(entry.sector);
                    adfDirectory.setName(entry.name);
                    directory.getSubDirectories().add(adfDirectory);
                }
            }
        }

        return directory;
    }

    /**
     * Reads a filename at a certain header sector.
     *
     * @param sector The header sector from where to read the filename.
     * @return The filename.
     */
    private String getFileNameAtSector(int sector) {
        imageData.position((SECTOR_SIZE * sector) + SECTOR_SIZE - 80);
        int nameLength = imageData.get();
        return Adf.readString(imageData, nameLength);

    }

    /**
     * Reads a file type at a certain header sector.
     *
     * @param sector The header sector from where to read the filename.
     * @return The file type.
     */
    private ENTRY_TYPE getFileTypeAtSector(int sector) {
        imageData.position((SECTOR_SIZE * sector) + SECTOR_SIZE - 4);
        long type = imageData.getInt();
        return ENTRY_TYPE.toType(type);
    }

    /**
     * Reads a file or directory data block.
     *
     * @param sector The sector where the block starts.
     * @param size   The size of the block.
     * @return The data block.
     */
    private AdfBlock readDataBlock(int sector, int size) {
        AdfBlock block = readBlock(sector);
        if (getImageInfo().getDiskType() == AdfImageInfo.DISK_TYPE.FFS) {
            block.setDataSize(Math.min(size, Adf.SECTOR_SIZE));
            byte[] blockContent = new byte[block.getDataSize()];
            for (int i = 0; i < block.getDataSize(); i++) {
                blockContent[i] = imageData.get();
            }
            block.setContent(blockContent);
        } else {
            if (block.getType() == 8) {
                byte[] blockContent = new byte[block.getDataSize()];
                imageData.position((sector * SECTOR_SIZE) + 24);
                for (int i = 0; i < block.getDataSize(); i++) {
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

    /**
     * Reads an extention block.
     *
     * @param sector The sector where the block starts.
     * @return The block.
     */
    private AdfBlock readExtentionBlock(int sector) {
        AdfBlock block = readBlock(sector);

        imageData.position(sector * Adf.SECTOR_SIZE + 24);
        List<Integer> pointers = new ArrayList<>();
        for (int i = 0; i < 72; i++) {
            int pointer = readSector(imageData);
            if (pointer > 0) {
                pointers.add(0, pointer);
            }
        }
        block.setPointers(pointers);

        imageData.position((sector * Adf.SECTOR_SIZE) + Adf.SECTOR_SIZE - 8);
        AdfBlockItem item = new AdfBlockItem();
        block.setItem(item);
        item.setDataBlockExtention(imageData.getInt());

        return block;
    }

    /**
     * Read the header block of a file or folder.
     *
     * @param sector The sector where the header block starts.
     * @return The header block.
     */
    private AdfBlock readHeaderBlock(int sector) {
        AdfBlock headerBlock = readBlock(sector);

        List<Integer> pointers = new ArrayList<>();
        for (int i = 0; i < 72; i++) {
            int pointer = readSector(imageData);
            if (pointer > 0) {
                pointers.add(0, pointer);
            }
        }
        headerBlock.setPointers(pointers);

        AdfBlockItem item = new AdfBlockItem();
        headerBlock.setItem(item);

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
        item.setLinkedSector(readSector(imageData));
        item.setParent(readSector(imageData));
        item.setDataBlockExtention(readSector(imageData));
        item.setType(imageData.getInt());

        return headerBlock;
    }

    /**
     * Reads the beginning of a block.
     *
     * @param sector The sector where the block begins.
     * @return The block.
     */
    private AdfBlock readBlock(int sector) {
        try {
            imageData.position(sector * Adf.SECTOR_SIZE);
            AdfBlock block = new AdfBlock();

            block.setType(imageData.getInt());
            block.setHeaderSector(imageData.getInt());
            block.setDataBlockCount(imageData.getInt());
            block.setDataSize(imageData.getInt());
            block.setFirstDataBlock(readSector(imageData));
            block.setChecksum(imageData.getInt());

            if (block.getFirstDataBlock() == 1914784865) {
                block.setFirstDataBlock(0);
            }
            return block;
        } catch (RuntimeException e) {
            System.out.println("----> INVALID SECTOR: " + sector);
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Returns meta information about this disk image.
     *
     * @return The disk image info.
     */
    public AdfImageInfo getImageInfo() {
        return this.imageInfo;
    }

    /**
     * The type of entry.
     */
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

        public static ENTRY_TYPE toType(long type) {
            return Arrays.stream(values())
                    .filter(v -> v.getType() == type)
                    .findFirst()
                    .orElse(null);
        }

        public int getType() {
            return this.type;
        }
    }

    /**
     * Utility holder for information about one hash
     * table entry
     */
    class PointerEntry {
        public int sector;
        public String name;
        public ENTRY_TYPE type;
    }
}
