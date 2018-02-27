package org.retro.common.impl.atarist;

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
import org.retro.common.impl.dos.DosVirtualDisk;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;

/**
 * Handles Atari ST ".ST" images.
 *
 * @author Marcel Schoen
 */
public class StImageHandler extends AbstractBaseImageHandler {

    @Override
    public VirtualDisk loadImage(File imageFile) throws VirtualDiskException {
        String diskName = imageFile.getName();

        FileDisk fileDisk = null;
        FatFileSystem fs = null;
        try {
            fileDisk = new FileDisk(imageFile, false);
            fs = FatFileSystem.read(fileDisk, true);
        } catch(IOException e) {
            throw new VirtualDiskException("Failed to read ST image; reason: " + e.toString()
                    + ", image: " + imageFile.getAbsolutePath(), e);
        }

        DosVirtualDisk virtualDisk = new DosVirtualDisk(fs);
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

    private void iterateReadDirectory(int indent, VirtualDirectory parent, FsDirectory directory) throws IOException {
        Iterator<FsDirectoryEntry> iterator = directory.iterator();
        while(iterator.hasNext()) {
            FsDirectoryEntry entry = iterator.next();
            if(entry.isFile()) {
                try {
                    VirtualFile virtualFile = new VirtualFile(parent, entry.getName());
                    FsFile fsFile = entry.getFile();
                    ByteBuffer bb = ByteBuffer.allocate((int) fsFile.getLength());
                    fsFile.read(0, bb);
                    virtualFile.setContent(bb);
                } catch(IOException e) {
                    // ignore and hope that the following entries can still be read.
                    e.printStackTrace();
                }
            } else {
                if(!entry.getName().equals(".") && !entry.getName().equals("..")) {
                    VirtualDirectory newParent = new VirtualDirectory(parent, entry.getName());
                    iterateReadDirectory(indent + 4, newParent, entry.getDirectory());
                }
            }
        }
    }

    @Override
    public void writeImage(VirtualDisk floppyDisk, File imageFile) throws VirtualDiskException {
        throw new VirtualDiskException("*not implemented yet*");
        /*
        FileDisk fileDisk = new FileDisk(imageFile, false);
        FatFileSystem fs = FatFileSystem.read(fileDisk, true);
        */
    }
}
