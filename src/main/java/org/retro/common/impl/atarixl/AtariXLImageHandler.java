package org.retro.common.impl.atarixl;

import org.retro.common.VirtualDirectory;
import org.retro.common.VirtualDisk;
import org.retro.common.VirtualDiskException;
import org.retro.common.impl.AbstractBaseImageHandler;

import java.io.File;

/**
 * Handler for Atari XL ATR disk images.
 *
 * NOT IMPLEMENTED YET
 *
 * @author Marcel Schoen
 */
public class AtariXLImageHandler extends AbstractBaseImageHandler {

    @Override
    public VirtualDisk loadImage(File imageFile) throws VirtualDiskException {
        String diskName = imageFile.getName();

        AtariXLVirtualDisk virtualDisk = new AtariXLVirtualDisk(imageFile.getName());
        VirtualDirectory root = new VirtualDirectory("/");
        virtualDisk.setRootContent(root);

        return virtualDisk;
    }
}
