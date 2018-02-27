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

    /**
     * Creates a virtual disk for a certain type.
     *
     * @param type The disk image type.
     */
    protected AbstractBaseVirtualDisk(ImageType type) {
        this.type = type;
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
