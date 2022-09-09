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
package com.retroio.tools.impl.c64;

import java.util.Vector;

/**
 * Simulates a C64 floppy directory.
 *
 * @author Thomas Frauenknecht
 */
public class VC1541Directory {

    public static final byte DEL_FILE_TYPE = (byte) 0x80;
    public static final byte SEQ_FILE_TYPE = (byte) 0x81;
    public static final byte PRG_FILE_TYPE = (byte) 0x82;
    public static final byte USR_FILE_TYPE = (byte) 0x83;
    public static final byte REL_FILE_TYPE = (byte) 0x84;

    private Vector<VC1541File> fileList;

    public VC1541Directory(byte[] block) {

        fileList = new Vector<VC1541File>();

        int next_dir_track = VC1541.toUnsignedInt(block[0]);
        int next_dir_sector = VC1541.toUnsignedInt(block[1]);

        for (int i = 2; i < 256 - 2; i = i + 32) {
            int progType = block[i];
            // test if we have an entry at current position
            if (progType != 0x00) {
                int startTrack = block[i + 1];
                int startSector = block[i + 2];
                String fileName = "";
                for (int n = 0; n < 16; n++) {
                    if (block[i + 2 + n + 1] != 0) {
                        fileName = fileName + (char) block[i + 2 + n + 1];
                    }
                }
                int sizeLow = VC1541.toUnsignedInt(block[i + 28]);
                int sizeHigh = VC1541.toUnsignedInt(block[i + 29]);

                VC1541File file = new VC1541File(startTrack, startSector, progType, fileName, sizeHigh * 256 + sizeLow);
                fileList.add(file);
            }
        }
    }

    public int getSize() {
        return fileList.size();
    }

    public VC1541File getEntry(int index) {
        return fileList.elementAt(index);
    }
}

