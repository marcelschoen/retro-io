package org.retro.common.impl.amiga.adf;

public class AdfBlockItem {
    private int size;
    private String comment;
    private int lastChangeDays;
    private int lastChangeMinutes;
    private int lastChangeTicks;
    private String name;
    private int linkedSector;
    private int parent;
    private int dataBlockExtention;
    private int type;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getLastChangeDays() {
        return lastChangeDays;
    }

    public void setLastChangeDays(int lastChangeDays) {
        this.lastChangeDays = lastChangeDays;
    }

    public int getLastChangeMinutes() {
        return lastChangeMinutes;
    }

    public void setLastChangeMinutes(int lastChangeMinutes) {
        this.lastChangeMinutes = lastChangeMinutes;
    }

    public int getLastChangeTicks() {
        return lastChangeTicks;
    }

    public void setLastChangeTicks(int lastChangeTicks) {
        this.lastChangeTicks = lastChangeTicks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLinkedSector() {
        return linkedSector;
    }

    public void setLinkedSector(int linkedSector) {
        this.linkedSector = linkedSector;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public int getDataBlockExtention() {
        return dataBlockExtention;
    }

    public void setDataBlockExtention(int dataBlockExtention) {
        this.dataBlockExtention = dataBlockExtention;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
