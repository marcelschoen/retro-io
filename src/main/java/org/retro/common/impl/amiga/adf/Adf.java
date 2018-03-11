package org.retro.common.impl.amiga.adf;

import java.nio.ByteBuffer;

/**
 * Some ADF-relates utility stuff.
 *
 * @author Marcel Schoen
 */
public class Adf {

    /** Size of a disk sector in bytes. */
    public static final int SECTOR_SIZE = 512;

    /**
     * Checks if a certain bit is set in the given byte.
     *
     * @param value The byte in which to check the bits.
     * @param bit The number of the bit to check (0-7).
     * @return True if the bit is set (1).
     */
    public static boolean isBitSet(byte value, int bit) {
        return ((value & bit^2) == 1);
    }

    /**
     * Reads a string from the given byte buffer.
     *
     * @param buffer The source byte buffer (pointer must be set to start of string).
     * @param stringLength The length of the string in bytes.
     * @return The String (can be empty).
     */
    public static String readString(ByteBuffer buffer, int stringLength) {
        byte[] formatData = new byte[stringLength];
        buffer.get(formatData);
        return new String(formatData);
    }
}
