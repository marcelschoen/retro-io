package org.retro.common;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public interface VirtualDisk {

    String getName();

    VirtualDirectory getRootContents();

    ImageType getType();

    ImageHandler getHandler();

    void exportToDirectory(File targetDirectory) throws IOException;

    void exportAsZip(File zipFile) throws IOException;

    void exportAsZip(OutputStream target) throws IOException;
}
