package org.retro.common.impl;

import org.apache.commons.io.IOUtils;
import org.retro.common.*;

import java.io.*;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
    @Override
    public void extractVirtualDisk(VirtualDisk disk, java.io.File targetDirectory) throws VirtualDiskException {
        targetDirectory.mkdirs();
        if(!targetDirectory.exists() || targetDirectory.isFile() || !targetDirectory.canWrite()) {
            throw new VirtualDiskException("Failed to create / write to directory: " + targetDirectory.getAbsolutePath());
        }
        VirtualDirectory root = disk.getRootContents();
        extractDirectory(root, targetDirectory);
    }

    @Override
    public void extractInZipArchive(VirtualDisk disk, File targetDirectory) throws VirtualDiskException {
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");

        targetDirectory.mkdirs();
        System.out.println("Target directory: " + targetDirectory.getAbsolutePath());
        String targetFileName = disk.getName() + ".zip";
        System.out.println("Target zip file name: " + targetFileName);
        File targetZipFile = new File(targetDirectory, targetFileName);
        System.out.println("Target zip file: " + targetZipFile.getAbsolutePath());
        try {
            String zipUriName = targetZipFile.getCanonicalPath().replace(File.separatorChar, '/');
            URI uri = URI.create("jar:file:///" + zipUriName);
            try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
                Path pathInZipfile = zipfs.getPath(disk.getName());
                Files.createDirectories(pathInZipfile);
                extractToZip(zipfs, disk.getRootContents(), disk.getName());
            }
        } catch(IOException e) {
            throw new VirtualDiskException("Failed to extract file to ZIP: " + targetZipFile.getAbsolutePath()
                    + ", reason: " + e, e);
        }
    }

    /**
     * Extracts the virtual disk contents into a ZIP archive file.
     *
     * @param zipfs The ZIP filesystem to extract the contents to.
     * @param directory The directory with the files to extract.
     * @param zipRootPrefix The root directory in the ZIP; usually the original disk image's filename without suffix.
     * @throws IOException If the ZIP file could not be created.
     */
    private void extractToZip(FileSystem zipfs, VirtualDirectory directory, String zipRootPrefix) throws IOException {
        for (VirtualFile entry : directory.getContents()) {
            Path pathInZipfile = zipfs.getPath("/" + zipRootPrefix + entry.getFullName());
            if (entry.isFile()) {
                Files.copy(new ByteArrayInputStream(entry.getContent().array()), pathInZipfile,
                        StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.createDirectories(pathInZipfile);
                extractToZip(zipfs, (VirtualDirectory)entry, zipRootPrefix);
            }
        }
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
