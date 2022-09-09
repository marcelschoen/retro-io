package com.retroio.tools.impl.atarixl;

import com.retroio.tools.ImageType;
import com.retroio.tools.impl.AbstractBaseVirtualDisk;

public class AtariXLVirtualDisk extends AbstractBaseVirtualDisk {
    public AtariXLVirtualDisk(String name) {
        super(ImageType.atarixl_ATR, name);
    }
}
