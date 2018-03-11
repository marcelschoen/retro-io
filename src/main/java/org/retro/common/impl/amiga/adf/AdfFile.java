package org.retro.common.impl.amiga.adf;

public class AdfFile {
    private int sector;
    private byte[] content;
    private int size;
    private String name;

    private AdfBlockItem item = new AdfBlockItem();

    public AdfFile() {
    }

    public AdfFile(int sector) {
        this.sector = sector;
    }

    public void setItem(AdfBlockItem item) {
        this.item = item;
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
        return item.getSize();
    }

    public void setSize(int size) {
        item.setSize(size);
    }

    public String getComment() {
        return item.getComment();
    }

    public void setComment(String comment) {
        item.setComment(comment);
    }

    public int getLastChangeDays() {
        return item.getLastChangeDays();
    }

    public void setLastChangeDays(int lastChangeDays) {
        item.setLastChangeDays(lastChangeDays);
    }

    public int getLastChangeMinutes() {
        return item.getLastChangeMinutes();
    }

    public void setLastChangeMinutes(int lastChangeMinutes) {
        item.setLastChangeMinutes(lastChangeMinutes);
    }

    public int getLastChangeTicks() {
        return item.getLastChangeTicks();
    }

    public void setLastChangeTicks(int lastChangeTicks) {
        item.setLastChangeTicks(lastChangeTicks);
    }

    public String getName() {
        return item.getName();
    }

    public void setName(String name) {
        item.setName(name);
    }

    public int getLinkedSector() {
        return item.getLinkedSector();
    }

    public void setLinkedSector(int linkedSector) {
        item.setLinkedSector(linkedSector);
    }

    public int getParent() {
        return item.getParent();
    }

    public void setParent(int parent) {
        item.setParent(parent);
    }

    public int getDataBlockExtention() {
        return item.getDataBlockExtention();
    }

    public void setDataBlockExtention(int dataBlockExtention) {
        item.setDataBlockExtention(dataBlockExtention);
    }

    public int getType() {
        return item.getType();
    }

    public void setType(int type) {
        item.setType(type);
    }
}
