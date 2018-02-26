package org.retro.common;

import org.retro.common.impl.atarist.StImageHandler;
import org.retro.common.impl.c64.C64ImageHandler;
import org.retro.common.impl.dos.DosImageHandler;

public class ImageHandlerFactory {

    public static ImageHandler get(ImageType type) {
        // TODO - IMPLEMENT DIFFERENT TYPES
        if(type == ImageType.atarist_ST) {
            return new StImageHandler();
        } else if(type == ImageType.c64_D64) {
            return new C64ImageHandler();
        }
        return new DosImageHandler();
    }
}
