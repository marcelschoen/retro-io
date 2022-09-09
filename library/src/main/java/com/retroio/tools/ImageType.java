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

import java.io.File;
import java.util.Arrays;

/**
 * Lists all supported image types. The lowercase prefix part
 * of the name specifies the computer platform, the suffix
 * the disk image type suffix.
 *
 * @author Marcel Schoen
 */
public enum ImageType {

    // Unknown image type
    unknown("-", "unknown"),

    // MS-DOS image
    dos_IMG("MS DOS", "IMG"),

    // Atari ST uncompressed image
    atarist_ST("Atari ST", "ST"),

    // Atari XL/XE image
    atarixl_ATR("Atari XL/XE", "ATR"),

    // Atari ST RLE-compressed image
    atarist_MSA("Atari ST", "MSA"),

    // Amiga image
    amiga_ADF("Commodore Amiga", "ADF"),

    // Commodore 64 floppy disk image
    c64_D64("Commodore 64", "D64");

    // The file suffix of this image type.
    private String fileSuffix;

    // The name of the computer platform this image type is used for.
    private String platform;

    /**
     * Creates an image type for a given suffix.
     *
     * @param platform   The computer platform name.
     * @param fileSuffix The file suffix of this type.
     */
    ImageType(String platform, String fileSuffix) {
        this.platform = platform;
        this.fileSuffix = fileSuffix;
    }

    /**
     * Returns the matching type for the given suffix.
     * Will return "unknown" if the suffix could not be matched.
     *
     * @param suffix The filename suffix (with or without ".").
     * @return The matching type (may be "unknown").
     */
    public static ImageType getTypeFromFileSuffix(String suffix) {
        return Arrays.stream(ImageType.values())
                .filter(t -> suffix.toUpperCase().endsWith(t.getFileSuffix()))
                .findFirst()
                .orElse(unknown);
    }

    /**
     * Returns the matching type for the given file, based on its filename suffix.
     * Will return "unknown" if the suffix could not be matched.
     *
     * @param file The image file.
     * @return The matching type (may be "unknown").
     */
    public static ImageType getTypeFromFile(File file) {
        if(file.getName().contains(".")) {
            String suffix = file.getName().substring(file.getName().indexOf(".") + 1);
            return getTypeFromFileSuffix(suffix);
        }
        return unknown;
    }

    /**
     * Get the file suffix of this image type.
     *
     * @return The file suffix (without ".").
     */
    public String getFileSuffix() {
        return this.fileSuffix;
    }

    /**
     * Returns the name of the computer platform this image type
     * is used for (e.g. "MS DOS" or "Atari ST").
     *
     * @return The name of the computer platform.
     */
    public String getPlatform() {
        return this.platform;
    }
}
