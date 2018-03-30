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
package org.retro.common;

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
    unknown(""),

    // MS-DOS image
    dos_IMG("IMG"),

    // Atari ST uncompressed image
    atarist_ST("ST"),

    // Atari XL/XE image
    atarixl_ATR("ATR"),

    // Atari ST RLE-compressed image
    atarist_MSA("MSA"),

    // Amiga image
    amiga_ADF("ADF"),

    // Commodore 64 floppy disk image
    c64_D64("D64"),

    // Commodore 64 tape image
    c64_T64("T64");

    // The file suffix of this image type.
    private String fileSuffix;

    /**
     * Creates an image type for a given suffix.
     *
     * @param fileSuffix The file suffix of this type.
     */
    ImageType(String fileSuffix) {
        this.fileSuffix = fileSuffix;
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
     * Returns the matching type for the given suffix, or null,
     * if the suffix could not be matched.
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
}
