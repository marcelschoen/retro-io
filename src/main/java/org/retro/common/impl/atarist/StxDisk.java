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
package org.retro.common.impl.atarist;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Reads an MSA floppy disk image file.
 *
 * @author Marcel Schoen
 */
public class StxDisk extends AbstractBaseStDisk {

    private File imageFile;
    private ByteBuffer buffer;

    /**
     * Creates an STX disk object.
     *
     * @param imageFile The STX floppy disk image file to read.
     */
    public StxDisk(File imageFile) {
        this.imageFile = imageFile;
    }

    /**
     * Loads the MSA image; reads the MSA header, and then all
     * the tracks, decompressing them if necessary. After that,
     * the resulting data is basically an ".ST" image (raw disk data),
     * so it can be read with the "fat32"-lib code.
     *
     * @throws IOException If the MSA image could not be processed.
     */
    public void load() throws IOException {
        byte[] rawData = new byte[(int) this.imageFile.length()];
        try (FileInputStream in = new FileInputStream(this.imageFile)) {
            in.read(rawData);
            buffer = ByteBuffer.wrap(rawData);

            // Read file header
            byte[] fileId = new byte[4];
            buffer.get(fileId);
            System.out.println("FileID: " + new String(fileId));

            buffer.position(buffer.position() + 2); // skip version number

            int type = buffer.getShort();
            if( (type & 0x0100) > 0) {
                System.out.println("Type: public");
            } else if( (type & 0xCC00) > 0) {
                System.out.println("Type: DC");
            } else {
                System.out.println("Type: unknown: " + Integer.toHexString(type));
            }
        }
    }

    @Override
    protected ByteBuffer getData() {
        return this.buffer;
    }

}
