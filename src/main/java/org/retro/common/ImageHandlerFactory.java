package org.retro.common;

import org.retro.common.impl.amiga.AmigaImageHandler;
import org.retro.common.impl.atarist.MsaImageHandler;
import org.retro.common.impl.atarist.StImageHandler;
import org.retro.common.impl.atarixl.AtariXLImageHandler;
import org.retro.common.impl.c64.C64ImageHandler;
import org.retro.common.impl.dos.DosImageHandler;

public class ImageHandlerFactory {

    public static ImageHandler get(ImageType type) {
        // TODO - IMPLEMENT DIFFERENT TYPES
        if(type == ImageType.atarist_ST) {
            return new StImageHandler();
        } else if(type == ImageType.atarist_MSA) {
            return new MsaImageHandler();
        } else if(type == ImageType.c64_D64) {
            return new C64ImageHandler();
        } else if(type == ImageType.atarixl_ATR) {
            return new AtariXLImageHandler();
        } else if(type == ImageType.amiga_ADF) {
            return new AmigaImageHandler();
        }
        return new DosImageHandler();
    }
}
