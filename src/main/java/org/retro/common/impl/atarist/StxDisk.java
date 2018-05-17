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

    private enum TYPE {

        /** Discovery Cartridge generated */
        DC,

        /** Generated on a normal ST */
        PUBLIC
    }

    private int tracks = -1;
    private boolean doubleSided = false;
    private TYPE type = null;
    private File imageFile;
    private ByteBuffer stxFileData;
    private ByteBuffer rawData;

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
            stxFileData = ByteBuffer.wrap(rawData);

            // ================================================================
            // Process File Header
            // ================================================================

            // Read file header
            byte[] fileId = new byte[4];
            stxFileData.get(fileId);
            System.out.println("FileID: " + new String(fileId));

            // Skip version
            stxFileData.position(stxFileData.position() + 2);

            // Read type (public aka generated on ST, or Discovery Cartridge aka DC).
            int type = stxFileData.getShort();
            if( (type & 0x0100) > 0) {
                System.out.println("Type: public");
                this.type = TYPE.PUBLIC;
            } else if( (type & 0xCC00) > 0) {
                System.out.println("Type: DC");
                this.type = TYPE.DC;
            } else {
                System.out.println("Type: unknown: " + Integer.toHexString(type));
            }

            // Skip unknown word
            stxFileData.position(stxFileData.position() + 2);

            // Read number of tracks
            this.tracks = stxFileData.get() & 0xFF;
            System.out.println("Tracks: " + tracks);
            if(this.tracks == 160) {
                this.doubleSided = true;
                System.out.println("Is double sided.");
            }
        }
    }

    @Override
    protected ByteBuffer getData() {
        return this.rawData;
    }

}
