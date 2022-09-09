/*
 * (C) Copyright ${year} retro-io (https://github.com/marcelschoen/retro-io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.retroio.tools;

import com.retroio.tools.impl.amiga.AmigaImageHandler;
import com.retroio.tools.impl.atarist.MsaImageHandler;
import com.retroio.tools.impl.atarist.StImageHandler;
import com.retroio.tools.impl.atarixl.AtariXLImageHandler;
import com.retroio.tools.impl.c64.C64ImageHandler;
import com.retroio.tools.impl.dos.DosImageHandler;

/**
 * This factory creates instances of image handlers for
 * certain types of floppy disk images.
 *
 * @author Marcel Schoen
 */
public class ImageHandlerFactory {

    /**
     * Get the image handler for a certain image type.
     * Get the matching type for a certain given file
     * by using
     * <pre>
     *     ImageType type = ImageType.getTypeFromFileSuffix(...);
     *     ImageHandler handler = ImageHandler.get(type);
     * </pre>
     *
     * @param type The type of disk image to load.
     * @return The handler (defaults to DOS image handler).
     */
    public static ImageHandler get(ImageType type) {
        // TODO - IMPLEMENT DIFFERENT TYPES
        if (type == ImageType.atarist_ST) {
            return new StImageHandler();
        } else if (type == ImageType.atarist_MSA) {
            return new MsaImageHandler();
        } else if (type == ImageType.c64_D64) {
            return new C64ImageHandler();
        } else if (type == ImageType.atarixl_ATR) {
            return new AtariXLImageHandler();
        } else if (type == ImageType.amiga_ADF) {
            return new AmigaImageHandler();
        }
        return new DosImageHandler();
    }
}
