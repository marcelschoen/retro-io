package org.retro.common;

public interface VirtualDisk {

    VirtualDirectory getRootContents() throws VirtualDiskException;

    ImageType getType();
}
