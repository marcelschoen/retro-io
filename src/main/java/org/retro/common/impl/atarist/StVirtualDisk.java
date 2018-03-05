package org.retro.common.impl.atarist;

import de.waldheinz.fs.FileSystem;
import org.retro.common.ImageType;
import org.retro.common.impl.AbstractBaseVirtualDisk;

public class StVirtualDisk extends AbstractBaseVirtualDisk {

    private FileSystem dosFileSystem;

    public StVirtualDisk(FileSystem dosFileSystem, String name) {
        super(ImageType.atarist_ST, name);
        this.dosFileSystem = dosFileSystem;
    }

}
