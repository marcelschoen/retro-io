package org.retro.common.impl.dos;

import de.waldheinz.fs.FileSystem;
import org.retro.common.ImageType;
import org.retro.common.impl.AbstractBaseVirtualDisk;

public class DosVirtualDisk extends AbstractBaseVirtualDisk {

    private FileSystem dosFileSystem;

    public DosVirtualDisk(FileSystem dosFileSystem) {
        super(ImageType.dos_IMG);
        this.dosFileSystem = dosFileSystem;
    }

}
