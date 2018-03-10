package org.retro.common.impl.amiga.adf;

public class AdfImageInfo {

    public enum DISK_TYPE {
        FFS,
        OFS,
        UNKNOWN
    }

    private DISK_TYPE diskType = DISK_TYPE.UNKNOWN;

    private String diskFormat = "";

    private int rootBlock;

    private String label = "";

    /** INTL = International characters mode */
    private boolean INTL_mode_enabled = false;

    /** DIRC = Directory Cache Mode */
    private boolean DIRC_mode_enabled = false;

    /**
     *
     * @return
     */
    public DISK_TYPE getDiskType() {
        return diskType;
    }

    public void setDiskType(byte diskType) {
        if((diskType & 0xff) == 0) {
            this.diskType = DISK_TYPE.OFS;
        } else {
            this.diskType = DISK_TYPE.FFS;
        }
    }

    public boolean isINTL() {
        return INTL_mode_enabled;
    }

    public void setINTL(boolean enabled) {
        this.INTL_mode_enabled = enabled;
    }

    public boolean isDIRC() {
        return DIRC_mode_enabled;
    }

    public void setDIRC(boolean enabled) {
        this.DIRC_mode_enabled = enabled;
    }

    public void setDiskType(DISK_TYPE diskType) {
        this.diskType = diskType;
    }

    public String getDiskFormat() {
        return diskFormat;
    }

    public void setDiskFormat(String diskFormat) {
        this.diskFormat = diskFormat;
    }

    public int getRootBlock() {
        return rootBlock;
    }

    public void setRootBlock(int rootBlock) {
        this.rootBlock = rootBlock;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
