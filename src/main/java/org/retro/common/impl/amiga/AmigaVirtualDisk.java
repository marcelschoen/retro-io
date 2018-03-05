package org.retro.common.impl.amiga;

import org.retro.common.ImageType;
import org.retro.common.impl.AbstractBaseVirtualDisk;

public class AmigaVirtualDisk extends AbstractBaseVirtualDisk {
    public AmigaVirtualDisk(String name) {
        super(ImageType.amiga_ADF, name);
    }
}
