package com.retroio.tools.impl.atarixl;

import com.retroio.tools.VirtualDirectory;
import com.retroio.tools.VirtualDisk;
import com.retroio.tools.VirtualDiskException;
import com.retroio.tools.impl.AbstractBaseImageHandler;

import java.io.File;

/**
 * Handler for Atari XL ATR disk images.
 * <p>
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
