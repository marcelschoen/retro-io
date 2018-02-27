package org.retro.common.impl;

import org.apache.commons.io.IOUtils;
import org.retro.common.*;

import java.io.*;
import java.util.Iterator;

/**
 * Abstract base class with some common functionality for
 * image handler implementations.
 *
 * @author Marcel Schoen
 */
public abstract class AbstractBaseImageHandler implements ImageHandler {

    // Characters that cannot be used in filenames when extracting data.
    private static final String BAD_CHARS = "/\\:*?\"<>|";

    @Override
    public VirtualDisk loadImage(File imageFile) throws VirtualDiskException {
        throw new VirtualDiskException("*not implemented*");
    }

    @Override
    public void writeImage(VirtualDisk virtualDisk, File imageFile) throws VirtualDiskException {
        throw new VirtualDiskException("*not implemented*");
    }

    /**
     * Extracts the contents of the given virtual disk into the
     * specified target directory. The target directory is created
     * (with all required parent directories) if it does not exist yet.
     *
     * @param disk The disk that should be extracted.
     * @param targetDirectory The target directory to receive the extracted content.
     * @throws VirtualDiskException If the extraction failed.
     */
    public void extractVirtualDisk(VirtualDisk disk, java.io.File targetDirectory) throws VirtualDiskException {
        targetDirectory.mkdirs();
        if(!targetDirectory.exists() || targetDirectory.isFile() || !targetDirectory.canWrite()) {
            throw new VirtualDiskException("Failed to create / write to directory: " + targetDirectory.getAbsolutePath());
        }
        VirtualDirectory root = disk.getRootContents();
        extractDirectory(root, targetDirectory);
    }

    /**
     * Extracts the given directory's contents to the target directory.
     *
     * @param virtualVirtualDirectory The directory that must be extracted.
     * @param targetDirectory The target directory for the extracted data.
     * @throws VirtualDiskException If the extraction failed.
     */
    private void extractDirectory(VirtualDirectory virtualVirtualDirectory, File targetDirectory) throws VirtualDiskException {
        Iterator<VirtualFile> files = virtualVirtualDirectory.iterator();
        while(files.hasNext()) {
            VirtualFile file = files.next();
            if(file.isDirectory()) {
                File newTargetDirectory = new File(targetDirectory, replaceBadChars(file.getName()));
                newTargetDirectory.mkdirs();
                extractDirectory((VirtualDirectory)file, newTargetDirectory);
            } else {
                extractFile(file, targetDirectory);
            }
        }
    }

    /**
     * Extracts the given file to the target directory.
     *
     * @param file The file that must be extracted.
     * @param targetDirectory The target directory for the extracted data.
     * @throws VirtualDiskException If the extraction failed.
     */
    private void extractFile(VirtualFile file, File targetDirectory) throws VirtualDiskException {
        String filename = replaceBadChars(file.getName());
        File targetFile = new File(targetDirectory, filename);
        System.out.println("extract file, size: " + file.getContent().capacity());
        InputStream in = new ByteArrayInputStream(file.getContent().array());
        try {
            IOUtils.copy(in, new FileOutputStream(targetFile));
        } catch(IOException e) {
            throw new VirtualDiskException("Failed to extract file to: " + targetFile.getAbsolutePath()
                    + ", reason: " + e, e);
        }
    }

    /**
     * Replaces characters that create problems in pretty much every modern file system
     * with an underscore; maybe improve this in the future, make it better tailored to
     * the current runtime platform (Windows, Linux, Mac...).
     *
     * @param filename The name of the file or directory to extract.
     * @return The "normalized" name without bad characters.
     */
    private static String replaceBadChars(String filename) {
        String newName = filename;
        for(char character : BAD_CHARS.toCharArray()) {
            newName = newName.replace(character, '_');
        }
        return newName;
    }
}
