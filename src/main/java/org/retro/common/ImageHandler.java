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
}
