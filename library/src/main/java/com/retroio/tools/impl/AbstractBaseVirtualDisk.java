/*
 * (C) Copyright ${year} retro-io (https://github.com/marcelschoen/retro-io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.retroio.tools.impl;

import com.retroio.tools.ImageType;
import com.retroio.tools.VirtualDirectory;
import com.retroio.tools.VirtualDisk;
import com.retroio.tools.VirtualFile;
import org.apache.commons.io.IOUtils;

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
    private static final String BAD_CHARS = ":*?\"<>|";

    /**
     * The root directory of the disk.
     */
    private VirtualDirectory rootContent;

    /**
     * The disk image type.
     */
    private ImageType type;

    /**
     * The disk name (usually image name minus suffix).
     */
    private String name;

    /**
     * Creates a virtual disk for a certain type.
     *
     * @param type The disk image type.
     */
    protected AbstractBaseVirtualDisk(ImageType type, String name) {
        this.type = type;
        this.name = name;
        if (this.name.toUpperCase().endsWith(this.type.getFileSuffix())) {
            this.name = this.name.substring(0, this.name.lastIndexOf("."));
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
        for (char character : BAD_CHARS.toCharArray()) {
            newName = newName.replace(character, '_');
        }
        return newName;
    }

    @Override
    public void exportToDirectory(File targetDirectory) throws IOException {
        targetDirectory.mkdirs();
        exportDirectory(getRootContents(), targetDirectory);
    }

    /**
     * Exports a virtual directory into a target directory.
     *
     * @param directory       The virtual directory to extract.
     * @param targetDirectory The target directory.
     * @throws IOException If there was an extraction problem.
     */
    private void exportDirectory(VirtualDirectory directory, File targetDirectory)
            throws IOException {
        IOException error = null;
        for (VirtualFile entry : directory.getContents()) {
            if (entry.isDirectory()) {
                exportDirectory((VirtualDirectory) entry, targetDirectory);
            } else {
                String filename = replaceBadChars(entry.getFullName().replace('/', File.separatorChar));
                System.out.println("-> filename: " + filename + ", target dir: " + targetDirectory);
                File file = new File(targetDirectory, filename);
                file.getParentFile().mkdirs();
                try (FileOutputStream fout = new FileOutputStream(file)) {
                    IOUtils.copy(new ByteArrayInputStream(entry.getContent().array()), fout);
                } catch (IOException e) {
                    System.err.println("Failed to extract file: " + file.getAbsolutePath() + ": " + e);
                    // Do not immediately throw an exception. We try to extract as much
                    // as we can. But if there was an error, then there should be an
                    // exception at the end, to indicate it.
                    error = e;
                }
            }
        }
        if (error != null) {
            throw error;
        }
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
     * @param directory       The directory to zip.
     * @param zipOutputStream The target zip output stream.
     * @throws IOException If creating the archive failed.
     */
    private void zipDirectory(VirtualDirectory directory, ZipOutputStream zipOutputStream)
            throws IOException {
        IOException error = null;
        for (VirtualFile entry : directory.getContents()) {
            try {
                if (entry.isDirectory()) {
                    zipDirectory((VirtualDirectory) entry, zipOutputStream);
                } else {
                    addToZipFile(entry, zipOutputStream);
                }
            } catch (IOException e) {
                System.err.println("Failed to zip entry: " + entry.getFullName() + ": " + e);
                // Do not immediately throw an exception. We try to extract as much
                // as we can. But if there was an error, then there should be an
                // exception at the end, to indicate it.
                error = e;
            }
        }
        if (error != null) {
            throw error;
        }
    }

    /**
     * Adds an extra file to the zip archive, copying in the created
     * date and a comment.
     *
     * @param zipStream archive to contain the file.
     */
    private void addToZipFile(VirtualFile fileEntry, ZipOutputStream zipStream) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(fileEntry.getContent().array())) {
            // create a new ZipEntry, which is basically another file
            // within the archive. We omit the path from the filename
            String zipEntryName = fileEntry.getFullName();
            if (zipEntryName.startsWith("/")) {
                zipEntryName = zipEntryName.substring(1);
            }
            System.out.println("Add entry: " + zipEntryName);
            ZipEntry zipEntry = new ZipEntry(zipEntryName);
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

    @Override
    public VirtualFile getFile(String uuid) {
        return findFileWithUuid(getRootContents(), uuid);
    }

    private VirtualFile findFileWithUuid(VirtualDirectory directory, String uuid) {
        for (VirtualFile entry : directory.getContents()) {
            if (entry.isFile() && entry.getUuid().equalsIgnoreCase(uuid)) {
                return entry;
            } else if (entry.isDirectory()) {
                VirtualFile file = findFileWithUuid((VirtualDirectory) entry, uuid);
                if (file != null) {
                    return file;
                }
            }
        }
        return null;
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
