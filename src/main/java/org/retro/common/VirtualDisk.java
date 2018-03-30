package org.retro.common;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents a virtual floppy disk image.
 */
public interface VirtualDisk {

    /**
     * Returns the name of the image; usually it's original
     * filename, without the suffix.
     *
     * @return The floppy disk image name.
     */
    String getName();

    /**
     * Returns the root directory folder of this virtual
     * floppy disk image.
     *
     * @return The root directory contents.
     */
    VirtualDirectory getRootContents();

    /**
     * Returns the type of this floppy disk image.
     *
     * @return The floppy disk image type.
     */
    ImageType getType();

    /**
     * Exports the contents of this floppy disk image to a
     * local file system directory, retaining the relative
     * directory structure of the original image.
     *
     * NOTE 1: In case of problems with the extraction caused by
     * weird file names causing issues with the rules of the local
     * file system, try exporting the contents as .zip archive
     * instead. That may work in cases where a plain file export
     * does not.
     *
     * NOTE 2: If some file entries cannot be extracted, the extraction
     * will still be carried out for everything else.
     *
     * @param targetDirectory The target directory where to extract the contents.
     * @throws IOException If the extraction failed.
     */
    void exportToDirectory(File targetDirectory) throws IOException;

    void exportAsZip(File zipFile) throws IOException;

    void exportAsZip(OutputStream target) throws IOException;
}
