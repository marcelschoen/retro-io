package org.retro.common.impl.dos;

import de.waldheinz.fs.FileSystem;
import org.retro.common.VirtualDiskException;
import org.retro.common.impl.AbstractBaseVirtualDisk;

import java.io.IOException;

public class DosVirtualDisk extends AbstractBaseVirtualDisk {

    private FileSystem dosFileSystem;

    public DosVirtualDisk(FileSystem dosFileSystem) {
        this.dosFileSystem = dosFileSystem;
    }

    @Override
    public long getCapacity() throws VirtualDiskException {
        try {
            return dosFileSystem.getTotalSpace();
        } catch(IOException e) {
            throw new VirtualDiskException(e);
        }
    }

    @Override
    public long getUsedSpace() throws VirtualDiskException {
        try {
            return dosFileSystem.getTotalSpace() - dosFileSystem.getFreeSpace();
        } catch(IOException e) {
            throw new VirtualDiskException(e);
        }
    }

    @Override
    public long getFreeSpace() throws VirtualDiskException {
        try {
            return dosFileSystem.getFreeSpace();
        } catch(IOException e) {
            throw new VirtualDiskException(e);
        }
    }

    @Override
    public long getUsableSpace() throws VirtualDiskException {
        try {
            return dosFileSystem.getUsableSpace();
        } catch(IOException e) {
            throw new VirtualDiskException(e);
        }
    }
}
