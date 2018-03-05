package org.retro.common;

import java.io.File;

/**
 * Interface for classes that can read (and potentially write)
 * a certain type of retro floppy disk image file.
 *
 * @author Marcel Schoen
 */
public interface ImageHandler {

    /**
     * Reads a retro floppy disk image file and returns the contents of the
     * disk image in a POJO.
     *
     * @param imageFile Points to the floppy disk image file.
     * @return The contents of the file in POJO form.
     * @throws VirtualDiskException If the image file could not be processed.
     */
    VirtualDisk loadImage(java.io.File imageFile) throws VirtualDiskException;

    /**
     * NOT IMPLEMENTED YET FOR ANY FILE TYPE
     *
     * Writes the contents of the given virtual disk into an image file.
     *
     * @param virtualDisk The contents of the image in POJO form.
     * @param imageFile The target floppy disk image file to create.
     * @throws VirtualDiskException If the image file could not be created.
     */
    void writeImage(VirtualDisk virtualDisk, java.io.File imageFile) throws VirtualDiskException;

    /**
     * Extracts the contents of a given virtual disk into a target directory.
     *
     * @param disk The virtual disk to extract.
     * @param targetDirectory The target directory (will be created, with all parents, if necessary).
     * @throws VirtualDiskException If the files could not be extracted.
     */
    void extractVirtualDisk(VirtualDisk disk, java.io.File targetDirectory) throws VirtualDiskException;

    /**
     * Extracts the contents of a given virtual disk into a target ZIP archive. The top-level directory
     * in the ZIP file usually has the same name as the original floppy disk image, minus its suffix.
     *
     * @param disk The virtual disk to extract.
     * @param target The target ZIP archive (target's directory will be created, with all parents, if necessary).
     * @throws VirtualDiskException If the files could not be extracted.
     */
    void extractInZipArchive(VirtualDisk disk, File target) throws VirtualDiskException;
}
