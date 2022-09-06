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
package org.retro.common.impl.amiga;

import org.retro.common.VirtualDirectory;
import org.retro.common.VirtualDisk;
import org.retro.common.VirtualDiskException;
import org.retro.common.VirtualFile;
import org.retro.common.impl.AbstractBaseImageHandler;
import org.retro.common.impl.amiga.adf.AdfDirectory;
import org.retro.common.impl.amiga.adf.AdfFile;
import org.retro.common.impl.amiga.adf.AdfImage;

import javax.script.Invocable;
import java.io.File;

/**
 * Handler for Commodore Amiga ADF disk images.
 * Based on JavaScript code from this project:
 *
 * https://github.com/steffest/ADF-reader
 *
 * ADF format documentation is also included in the
 * "src/doc-formats" subdirectory in this project.
 *
 * @author Marcel Schoen
 */
public class AmigaImageHandler extends AbstractBaseImageHandler {

    Invocable invocable;

    @Override
    public VirtualDisk loadImage(File imageFile) throws VirtualDiskException {
        String diskName = imageFile.getName();

        try {
            AdfImage adfImage = new AdfImage(imageFile);
            System.out.println("Disk format: " + adfImage.getImageInfo().getDiskFormat());
            System.out.println("Disk type: " + adfImage.getImageInfo().getDiskType().name());
            System.out.println("Disk INTL: " + adfImage.getImageInfo().isINTL());
            System.out.println("Disk DIRC: " + adfImage.getImageInfo().isDIRC());
            System.out.println("Disk root block: " + adfImage.getImageInfo().getRootBlock());
            System.out.println("Disk label: " + adfImage.getImageInfo().getLabel());

            AmigaVirtualDisk virtualDisk = new AmigaVirtualDisk(diskName);
            VirtualDirectory root = new VirtualDirectory("/");
            virtualDisk.setRootContent(root);

            processDir(root, adfImage.getRootFolder());

            return virtualDisk;

        } catch(Exception e) {
            throw new VirtualDiskException("Failed to process ADF image: " + e, e);
        }
    }

    private void processDir(VirtualDirectory parent, AdfDirectory directory) {
        VirtualDirectory newDir = new VirtualDirectory(parent, directory.getName());
        for(AdfFile file : directory.getFileEntries()) {
            VirtualFile newFile = new VirtualFile(newDir, file.getName());
            newFile.setContent(file.getContent());
        }
        for(AdfDirectory dir : directory.getSubDirectories()) {
            processDir(newDir, dir);
        }
    }
}
