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
package org.retro.common.impl.atarist;

import de.waldheinz.fs.FsDirectory;
import de.waldheinz.fs.fat.FatFileSystem;
import org.retro.common.ImageType;
import org.retro.common.VirtualDirectory;
import org.retro.common.VirtualDisk;
import org.retro.common.VirtualDiskException;

import java.io.File;
import java.io.IOException;

/**
 * Handles STX (Pasti) Atari ST floppy images.
 *
 * @author Marcel Schoen
 */
public class StxImageHandler extends AbstractBaseStImageHandler {

    @Override
    public VirtualDisk loadImage(File imageFile) throws VirtualDiskException {

        FatFileSystem fs = null;
        try {
            StxDisk disk = new StxDisk(imageFile);

            disk.load();

            // Read raw image data as FAT file system
            fs = FatFileSystem.read(disk, true);

        } catch (IOException e) {
            throw new VirtualDiskException("Failed to read STX files from image; reason: " + e.toString(), e);
        }

        StVirtualDisk virtualDisk = new StVirtualDisk(ImageType.atarist_MSA, imageFile.getName());
        try {
            FsDirectory rootDirectory = fs.getRoot();
            VirtualDirectory root = new VirtualDirectory("/");

            System.out.println("read root...");
            iterateReadDirectory(0, root, rootDirectory);
            System.out.println("contents read.");

            virtualDisk.setRootContent(root);
        } catch(IOException e) {
            throw new VirtualDiskException("Failed to read ST files from image; reason: " + e.toString(), e);
        }

        return virtualDisk;
    }
}