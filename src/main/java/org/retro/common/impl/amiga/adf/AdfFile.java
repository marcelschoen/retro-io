package org.retro.common.impl.amiga.adf;

public class AdfFile {
    private int sector;
    private byte[] content;
    private int size;
    private String name;

    public AdfFile() {
    }

    public AdfFile(int sector) {
        this.sector = sector;
    }

    public int getSector() {
        return sector;
    }

    public void setSector(int sector) {
        this.sector = sector;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
