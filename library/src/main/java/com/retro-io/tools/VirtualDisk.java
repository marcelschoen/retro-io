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
package org.retro.common;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents a virtual floppy disk image.
 *
 * @author Marcel Schoen
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
     * Allows to retrieve one of the virtual files in this
     * virtual disk, based on its UUID.
     *
     * @param uuid The UUID value.
     * @return The file, or null, if no file with that UUID exists.
     */
    VirtualFile getFile(String uuid);

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
