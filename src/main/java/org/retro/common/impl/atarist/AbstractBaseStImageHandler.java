package org.retro.common.impl.atarist;

import de.waldheinz.fs.FsDirectory;
import de.waldheinz.fs.FsDirectoryEntry;
import de.waldheinz.fs.FsFile;
import org.retro.common.VirtualDirectory;
import org.retro.common.VirtualFile;
import org.retro.common.impl.AbstractBaseImageHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;

public abstract class AbstractBaseStImageHandler extends AbstractBaseImageHandler {

    /**
     * Builds up the given VirtualDirectory by processing the
     * given FsDirectory.
     *
     * @param indent
     * @param parent
     * @param directory
     * @throws IOException
     */
    protected void iterateReadDirectory(int indent, VirtualDirectory parent, FsDirectory directory) throws IOException {
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
}
