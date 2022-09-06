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
package org.retro.common.impl.amiga.adf;

/**
 * Stores meta information about the disk image.
 *
 * @author Marcel Schoen
 */
public class AdfImageInfo {

    /**
     * The disk image / filesystem type.
     *
     * FFS - Fast File System (AmigaOS 2.x)
     * OFS - Old File System (AmigaOS 1.x)
     */
    public enum DISK_TYPE {
        FFS,
        OFS,
        UNKNOWN
    }

    /** The disk image file system type. */
    private DISK_TYPE diskType = DISK_TYPE.UNKNOWN;

    /** The disk format (DOS) */
    private String diskFormat = "";

    /** The root block sector number. */
    private int rootBlock;

    /** The disk label. */
    private String label = "";

    /** INTL = International characters mode */
    private boolean INTL_mode_enabled = false;

    /** DIRC = Directory Cache Mode */
    private boolean DIRC_mode_enabled = false;

    /**
     * Returns the disk image file system type.
     *
     * @return The file system type.
     */
    public DISK_TYPE getDiskType() {
        return diskType;
    }

    /**
     * Sets the image disk type.
     *
     * @param diskType The image disk type.
     */
    public void setDiskType(DISK_TYPE diskType) {
        this.diskType = diskType;
    }

    /**
     * Determines if this image uses the international mode.
     *
     * @return True if it uses international mode.
     */
    public boolean isINTL() {
        return INTL_mode_enabled;
    }

    /**
     * Sets the international mode.
     *
     * @param enabled True if international mode is used.
     */
    public void setINTL(boolean enabled) {
        this.INTL_mode_enabled = enabled;
    }

    /**
     * Checks if this image uses directory cache mode.
     *
     * @return True if directory cache mode is used.
     */
    public boolean isDIRC() {
        return DIRC_mode_enabled;
    }

    /**
     * Sets directory cache mode.
     *
     * @param enabled True if directory cache mode is used.
     */
    public void setDIRC(boolean enabled) {
        this.DIRC_mode_enabled = enabled;
    }

    /**
     * Returns this disks format. So far, only
     * "DOS" is supported.
     *
     * @return The disk image format.
     */
    public String getDiskFormat() {
        return diskFormat;
    }

    /**
     * Sets this disk image's format.
     *
     * @param diskFormat The image format.
     */
    public void setDiskFormat(String diskFormat) {
        this.diskFormat = diskFormat;
    }

    /**
     * Returns the starting sector of the root block.
     *
     * @return The root block starting sector.
     */
    public int getRootBlock() {
        return rootBlock;
    }

    /**
     * Sets the root block starting sector.
     *
     * @param rootBlock The root block starting sector.
     */
    public void setRootBlock(int rootBlock) {
        this.rootBlock = rootBlock;
    }

    /**
     * Returns this disk's label.
     *
     * @return The disk label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets this disks label.
     *
     * @param label The disk label.
     */
    public void setLabel(String label) {
        this.label = label;
    }
}
