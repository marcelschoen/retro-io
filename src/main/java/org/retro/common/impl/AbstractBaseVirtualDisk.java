package org.retro.common.impl;

import org.retro.common.ImageType;
import org.retro.common.VirtualDirectory;
import org.retro.common.VirtualDisk;
import org.retro.common.VirtualDiskException;

public abstract class AbstractBaseVirtualDisk implements VirtualDisk {

    private VirtualDirectory rootContent;

    private ImageType type;

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

    public void setRootContent(VirtualDirectory rootContent) {
        this.rootContent = rootContent;
    }

    public void setType(ImageType type) {
        this.type = type;
    }
}
