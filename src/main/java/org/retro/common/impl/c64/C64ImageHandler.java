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

        C64VirtualDisk virtualDisk = new C64VirtualDisk();
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
