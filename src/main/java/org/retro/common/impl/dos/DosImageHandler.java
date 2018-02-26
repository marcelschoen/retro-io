package org.retro.common.impl.dos;

import de.waldheinz.fs.FsDirectory;
import de.waldheinz.fs.FsDirectoryEntry;
import de.waldheinz.fs.FsFile;
import de.waldheinz.fs.fat.FatFileSystem;
import de.waldheinz.fs.util.FileDisk;
import org.retro.common.VirtualDirectory;
import org.retro.common.VirtualDisk;
import org.retro.common.VirtualDiskException;
import org.retro.common.VirtualFile;
import org.retro.common.impl.AbstractBaseImageHandler;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;

/**
 * Reads and writes DOS RAW images.
 *
 * Uses the fat32-lib library by Matthias Treydte:
 * https://github.com/waldheinz/fat32-lib
 */
public class DosImageHandler extends AbstractBaseImageHandler {

    @Override
    public VirtualDisk loadImage(File imageFile) throws VirtualDiskException {
        String diskName = imageFile.getName();

        FatFileSystem fs = null;
        FileDisk fileDisk = null;
        try {
            fileDisk = new FileDisk(imageFile, false);
            fs = FatFileSystem.read(fileDisk, true);
        } catch(IOException e) {
            throw new VirtualDiskException("Failed to read DOS image; reason: " + e.toString()
                    + ", image: " + imageFile.getAbsolutePath(), e);
        }

        DosVirtualDisk virtualDisk = new DosVirtualDisk(fs);
        try {
            FsDirectory rootDirectory = fs.getRoot();
            VirtualDirectory root = new VirtualDirectory("/");

            iterateReadDirectory(root, rootDirectory);

            virtualDisk.setRootContent(root);
            virtualDisk.setCapacity(fileDisk.getSize());
            virtualDisk.setSectorSize(fileDisk.getSectorSize());
            virtualDisk.setFreeSpace(fs.getFreeSpace());
            virtualDisk.setUsableSpace(fs.getUsableSpace());

        } catch(IOException e) {
            throw new VirtualDiskException("Failed to read DOS files from image; reason: " + e.toString(), e);
        }

        return virtualDisk;
    }

    private void iterateReadDirectory(VirtualDirectory parent, FsDirectory directory) throws IOException {
        Iterator<FsDirectoryEntry> iterator = directory.iterator();
        while(iterator.hasNext()) {
            FsDirectoryEntry entry = iterator.next();
            if(entry.isFile()) {
                try {
                    VirtualFile virtualFile = new VirtualFile(parent, entry.getName(), entry.getLastModified());
                    FsFile fsFile = entry.getFile();
                    ByteBuffer bb = ByteBuffer.allocate((int) fsFile.getLength());
                    fsFile.read(0, bb);
                    virtualFile.setContent(bb);
                } catch(IOException e) {
                    e.printStackTrace();
                }
            } else {
                VirtualDirectory newParent = new VirtualDirectory(parent, entry.getName());
                iterateReadDirectory(newParent, entry.getDirectory());
            }
        }
    }

    @Override
    public void writeImage(VirtualDisk floppyDisk, File imageFile) throws VirtualDiskException {
//        FileDisk fileDisk = new FileDisk(imageFile, false);
//        FatFileSystem fs = FatFileSystem.read(fileDisk, true);


    }
}
