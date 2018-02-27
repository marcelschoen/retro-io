package org.retro.common.impl.amiga;

import org.retro.common.VirtualDirectory;
import org.retro.common.VirtualDisk;
import org.retro.common.VirtualDiskException;
import org.retro.common.impl.AbstractBaseImageHandler;

import java.io.File;

/**
 * Handler for Commodore Amiga ADF disk images.
 *
 * NOT IMPLEMENTED YET
 *
 * @author Marcel Schoen
 */
public class AmigaImageHandler extends AbstractBaseImageHandler {

    @Override
    public VirtualDisk loadImage(File imageFile) throws VirtualDiskException {
        String diskName = imageFile.getName();

        AmigaVirtualDisk virtualDisk = new AmigaVirtualDisk();
        VirtualDirectory root = new VirtualDirectory("/");
        virtualDisk.setRootContent(root);

        return virtualDisk;
    }
}
