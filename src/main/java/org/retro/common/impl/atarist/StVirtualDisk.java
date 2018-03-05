package org.retro.common.impl.atarist;

import org.retro.common.ImageType;
import org.retro.common.impl.AbstractBaseVirtualDisk;

public class StVirtualDisk extends AbstractBaseVirtualDisk {

    public StVirtualDisk(String name) {
        super(ImageType.atarist_ST, name);
    }

}
