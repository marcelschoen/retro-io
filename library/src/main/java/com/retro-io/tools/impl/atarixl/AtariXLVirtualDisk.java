package org.retro.common.impl.atarixl;

import org.retro.common.ImageType;
import org.retro.common.impl.AbstractBaseVirtualDisk;

public class AtariXLVirtualDisk extends AbstractBaseVirtualDisk {
    public AtariXLVirtualDisk(String name) {
        super(ImageType.atarixl_ATR, name);
    }
}
