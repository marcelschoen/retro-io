package org.retro.common.impl.amiga.adf;

import java.nio.ByteBuffer;

public class Adf {

    /** Size of a disk sector in bytes. */
    public static final int SECTOR_SIZE = 512;

    public static boolean isBitSet(byte value, int bit) {
        return ((value & bit^2) == 1);
    }

    public static String readString(ByteBuffer buffer, int stringLength) {
        byte[] formatData = new byte[stringLength];
        buffer.get(formatData);
        return new String(formatData);
    }
}
