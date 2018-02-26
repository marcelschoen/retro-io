package org.retro.common;

public interface ImageHandler {

    VirtualDisk loadImage(java.io.File imageFile) throws VirtualDiskException;

    void writeImage(VirtualDisk virtualDisk, java.io.File imageFile) throws VirtualDiskException;

    void extractVirtualDisk(VirtualDisk disk, java.io.File targetDirectory) throws VirtualDiskException;
}
