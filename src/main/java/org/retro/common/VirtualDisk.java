package org.retro.common;

public interface VirtualDisk {

    String getName();

    VirtualDirectory getRootContents() throws VirtualDiskException;

    ImageType getType();
}
