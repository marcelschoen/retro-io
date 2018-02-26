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

    private void extractDirectory(VirtualDirectory virtualVirtualDirectory, File targetDirectory) throws VirtualDiskException {
        Iterator<VirtualFile> files = virtualVirtualDirectory.iterator();
        while(files.hasNext()) {
            VirtualFile file = files.next();
            if(file.isDirectory()) {
                File newTargetDirectory = new File(targetDirectory, file.getName());
                newTargetDirectory.mkdirs();
                extractDirectory((VirtualDirectory)file, newTargetDirectory);
            } else {
                extractFile(file, targetDirectory);
            }
        }
    }

    private void extractFile(VirtualFile file, File targetDirectory) throws VirtualDiskException {
        File targetFile = new File(targetDirectory, file.getName());
        System.out.println("extract file, size: " + file.getContent().capacity());
        InputStream in = new ByteArrayInputStream(file.getContent().array());
        try {
            IOUtils.copy(in, new FileOutputStream(targetFile));
        } catch(IOException e) {
            throw new VirtualDiskException("Failed to extract file to: " + targetFile.getAbsolutePath()
                    + ", reason: " + e, e);
        }
    }
}
