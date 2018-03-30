package org.retro.common.impl;

import org.apache.commons.io.IOUtils;
import org.retro.common.ImageHandler;
import org.retro.common.VirtualDisk;
import org.retro.common.VirtualDiskException;
import org.retro.common.VirtualFile;

import java.io.*;

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
     * Extracts the given file to the target directory.
     *
     * @param file The file that must be extracted.
     * @param targetDirectory The target directory for the extracted data.
     * @throws VirtualDiskException If the extraction failed.
     */
    private void extractFile(VirtualFile file, File targetDirectory) throws VirtualDiskException {
        String filename = replaceBadChars(file.getName());
        File targetFile = new File(targetDirectory, filename);
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
