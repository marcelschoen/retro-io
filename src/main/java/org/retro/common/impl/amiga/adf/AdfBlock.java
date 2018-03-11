package org.retro.common.impl.amiga.adf;

import java.util.List;

public class AdfBlock {
    private int type;
    private int headerSector;
    private int dataBlockCount;
    private int dataSize;
    private int firstDataBlock;
    private int nextDataBlock;
    private int checksum;
    private int number;
    private AdfBlockItem item = new AdfBlockItem();
    private byte[] content;
    
    private List<Integer> pointers;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNextDataBlock() {
        return nextDataBlock;
    }

    public void setNextDataBlock(int nextDataBlock) {
        this.nextDataBlock = nextDataBlock;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public AdfBlockItem getItem() {
        return item;
    }

    public void setItem(AdfBlockItem item) {
        this.item = item;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getHeaderSector() {
        return headerSector;
    }

    public void setHeaderSector(int headerSector) {
        this.headerSector = headerSector;
    }

    public int getDataBlockCount() {
        return dataBlockCount;
    }

    public void setDataBlockCount(int dataBlockCount) {
        this.dataBlockCount = dataBlockCount;
    }

    public int getDataSize() {
        return dataSize;
    }

    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }

    public int getFirstDataBlock() {
        return firstDataBlock;
    }

    public void setFirstDataBlock(int firstDataBlock) {
        this.firstDataBlock = firstDataBlock;
    }

    public int getChecksum() {
        return checksum;
    }

    public void setChecksum(int checksum) {
        this.checksum = checksum;
    }

    public List<Integer> getPointers() {
        return pointers;
    }

    public void setPointers(List<Integer> pointers) {
        this.pointers = pointers;
    }
}
