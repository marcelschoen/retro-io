package org.retro.common.impl;

import org.apache.commons.io.IOUtils;
import org.retro.common.*;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Base class for virtual disk implementations.
 *
 * @author Marcel Schoen
 */
public abstract class AbstractBaseVirtualDisk implements VirtualDisk {

    // Characters that cannot be used in filenames when extracting data.
    private static final String BAD_CHARS = "/\\:*?\"<>|";

    /** The root directory of the disk. */
    private VirtualDirectory rootContent;

    /** The disk image type. */
    private ImageType type;

    /** The disk name (usually image name minus suffix). */
    private String name;

    /** Reference to the handler that created this disk. */
    private ImageHandler handler;

    /**
     * Creates a virtual disk for a certain type.
     *
     * @param type The disk image type.
     */
    protected AbstractBaseVirtualDisk(ImageType type, String name) {
        this.type = type;
        this.name = name;
        if(this.name.toUpperCase().endsWith(this.type.getFileSuffix())) {
            this.name = this.name.substring(0, this.name.lastIndexOf("."));
        }
    }

    @Override
    public ImageHandler getHandler() {
        return handler;
    }

    @Override
    public void exportToDirectory(File targetDirectory) throws IOException {
        targetDirectory.mkdirs();
        exportDirectory(getRootContents(), targetDirectory);
    }

    private void exportDirectory(VirtualDirectory directory, File targetDirectory)
        throws IOException {
        for (VirtualFile entry : directory.getContents()) {
            if(entry.isDirectory()) {
                exportDirectory((VirtualDirectory)entry, targetDirectory);
            } else {
                System.out.println("Entry: " + entry.getFullName());
                String filename = replaceBadChars(entry.getFullName())
                        .replace('/', File.separatorChar);
                File file = new File(targetDirectory, filename);
                System.out.println("Extract to file: " + file.getAbsolutePath());
                file.getParentFile().mkdirs();
                try (FileOutputStream fout = new FileOutputStream(file)) {
                        IOUtils.copy(new ByteArrayInputStream(entry.getContent().array()), fout);
                } catch(IOException e) {
                    System.err.println("Failed to extract file: " + file.getAbsolutePath() + ": " + e);
                }
            }
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

    @Override
    public void exportAsZip(File zipFile) throws IOException {
        zipFile.getParentFile().mkdirs();
        try (FileOutputStream fout = new FileOutputStream(zipFile)) {
            exportAsZip(fout);
        }
    }

    @Override
    public void exportAsZip(OutputStream target) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(target)) {
            zipDirectory(getRootContents(), zos);
        }
    }

    /**
     * Recursively zip thie given virtual directory into the target zip output stream.
     *
     * @param directory The directory to zip.
     * @param zipOutputStream The target zip output stream.
     * @throws IOException If creating the archive failed.
     */
    private void zipDirectory(VirtualDirectory directory, ZipOutputStream zipOutputStream)
            throws IOException {
        for (VirtualFile entry : directory.getContents()) {
            if(entry.isDirectory()) {
                zipDirectory((VirtualDirectory)entry, zipOutputStream);
            } else {
                addToZipFile(entry, zipOutputStream);
            }
        }
    }

    /**
     * Adds an extra file to the zip archive, copying in the created
     * date and a comment.
     * @param zipStream archive to contain the file.
     */
    private void addToZipFile(VirtualFile fileEntry, ZipOutputStream zipStream) throws IOException {
        String inputFileName = fileEntry.getFullName();
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(fileEntry.getContent().array())) {
System.out.println("Zip entry name: " + fileEntry.getFullName());
            // create a new ZipEntry, which is basically another file
            // within the archive. We omit the path from the filename
            ZipEntry zipEntry = new ZipEntry(fileEntry.getFullName());
//            zipEntry.setCreationTime(FileTime.fromMillis(file.toFile().lastModified()));
            zipStream.putNextEntry(zipEntry);

            // Now we copy the existing file into the zip archive. To do
            // this we write into the zip stream, the call to putNextEntry
            // above prepared the stream, we now write the bytes for this
            // entry. For another source such as an in memory array, you'd
            // just change where you read the information from.
            IOUtils.copy(inputStream, zipStream);
        }
    }

    /**
     * Sets the image handler on this virtual disk.
     *
     * @param handler The handler that created this disk.
     */
    public void setHandler(ImageHandler handler) {
        this.handler = handler;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public VirtualDirectory getRootContents() {
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
