package org.retro.common.impl;

import org.retro.common.ImageHandler;
import org.retro.common.VirtualDisk;
import org.retro.common.VirtualDiskException;

import java.io.File;

/**
 * Abstract base class with some common functionality for
 * image handler implementations.
 *
 * @author Marcel Schoen
 */
public abstract class AbstractBaseImageHandler implements ImageHandler {

    @Override
    public VirtualDisk loadImage(File imageFile) throws VirtualDiskException {
        throw new VirtualDiskException("*not implemented*");
    }

    @Override
    public void writeImage(VirtualDisk virtualDisk, File imageFile) throws VirtualDiskException {
        throw new VirtualDiskException("*not implemented*");
    }
}
