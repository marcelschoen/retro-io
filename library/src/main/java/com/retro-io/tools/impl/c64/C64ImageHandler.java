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
package org.retro.common.impl.c64;

import org.retro.common.VirtualDirectory;
import org.retro.common.VirtualDisk;
import org.retro.common.VirtualDiskException;
import org.retro.common.VirtualFile;
import org.retro.common.impl.AbstractBaseImageHandler;

import java.io.File;

/**
 * Handler for C64 disk images.
 *
 * @author Marcel Schoen
 */
public class C64ImageHandler extends AbstractBaseImageHandler {

    @Override
    public VirtualDisk loadImage(File imageFile) throws VirtualDiskException {

        VC1541 floppy = new VC1541(false);
        floppy.loadImage(imageFile);

        C64VirtualDisk virtualDisk = new C64VirtualDisk(imageFile.getName());
        VirtualDirectory root = new VirtualDirectory("/");
        virtualDisk.setRootContent(root);

        for(int i=0; i < floppy.getDirectory().getSize(); i++) {
            VC1541File file = floppy.getDirectory().getEntry(i);
            VirtualFile virtualFile = new VirtualFile(root, file.getFileName());
            virtualFile.setContent(floppy.loadFile(file));
        }
        return virtualDisk;
    }
}
