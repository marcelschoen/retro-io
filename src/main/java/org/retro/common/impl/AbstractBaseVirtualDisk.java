package org.retro.common.impl;

import org.retro.common.ImageType;
import org.retro.common.VirtualDirectory;
import org.retro.common.VirtualDisk;
import org.retro.common.VirtualDiskException;

/**
 * Base class for virtual disk implementations.
 *
 * @author Marcel Schoen
 */
public abstract class AbstractBaseVirtualDisk implements VirtualDisk {

    /** The root directory of the disk. */
    private VirtualDirectory rootContent;

    /** The disk image type. */
    private ImageType type;

    /** The disk name (usually image name minus suffix). */
    private String name;

    /**
     * Creates a virtual disk for a certain type.
     *
     * @param type The disk image type.
     */
    protected AbstractBaseVirtualDisk(ImageType type, String name) {
        this.type = type;
        this.name = name;
        if(this.name.toUpperCase().endsWith(this.type.getFileSuffix())) {
            this.name = this.name.substring(0, this.name.lastIndexOf("."));
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public VirtualDirectory getRootContents() throws VirtualDiskException {
        return this.rootContent;
    }

    @Override
    public ImageType getType() {
        return this.type;
    }

    /**
     * Sets the root directory of this disk.
     *
     * @param rootContent The root directory.
     */
    public void setRootContent(VirtualDirectory rootContent) {
        this.rootContent = rootContent;
    }
}
