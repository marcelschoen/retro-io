//start of CRC16.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * CRC16.java
 * <p>
 * Copyright (C) 2001-2002  Michel Ishizuka  All rights reserved.
 */

package jp.gr.java_conf.dangan.util.lha;

//import classes and interfaces

import java.util.zip.Checksum;

//import exceptions

/**
 * <pre>
 * </pre>
 *
 * <pre>
 * -- revision history --
 * $Log: CRC16.java,v $
 * Revision 1.0  2002/07/24 00:00:00  dangan
 * add to version control
 * [maintanance]
 *
 * </pre>
 *
 * @author $Author: dangan $
 * @version $Revision: 1.0 $
 */
public class CRC16 implements Checksum {


    //------------------------------------------------------------------
    //  class field
    //------------------------------------------------------------------
    //  public static final int CRC_ANSY_POLY
    //  public static final int CRC_ANSY_INIT
    //  public static final int CCITT_POLY
    //  public static final int CCITT_INIT
    //  public static final int DefaultPOLY
    //  public static final int DefaultINIT
    //------------------------------------------------------------------
    /**
     */
    public static final int CRC_ANSY_POLY = 0xA001;

    /**
     */
    public static final int CRC_ANSY_INIT = 0x0000;

    /**
     */
    public static final int CCITT_POLY = 0x8408;

    /**
     */
    public static final int CCITT_INIT = 0xFFFF;

    /**
     */
    public static final int DefaultPOLY = CRC16.CRC_ANSY_POLY;

    /**
     */
    public static final int DefaultINIT = CRC16.CRC_ANSY_INIT;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  private int crc
    //  private int init
    //  private int[] crcTable
    //------------------------------------------------------------------
    /**
     * CRC16�l 
     */
    private int crc;

    /**
     */
    private int init;

    /**
     */
    private int[] crcTable;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  public CRC16()
    //  public CRC16( int poly )
    //  public CRC16( int poly, int init )
    //  public CRC16( int[] crcTable, int init )
    //------------------------------------------------------------------

    /**
     */
    public CRC16() {
        this(DefaultPOLY, DefaultINIT);
    }

    /**
     *
     */
    public CRC16(int poly) {
        this(poly,
                (poly == CRC16.CCITT_POLY ?
                        CRC16.CCITT_INIT :
                        CRC16.DefaultINIT));
    }

    /**
     */
    public CRC16(int poly, int init) {
        this(CRC16.makeCrcTable(poly), init);
    }

    /**
     */
    public CRC16(int[] crcTable, int init) {
        final int BYTE_PATTERNS = 256;

        if (crcTable.length == BYTE_PATTERNS) {
            this.crcTable = crcTable;
            this.init = init;

            this.reset();
        } else {
            throw new IllegalArgumentException("crcTable.length must equals 256");
        }
    }


    //------------------------------------------------------------------
    //  method of java.util.zip.Checksum
    //------------------------------------------------------------------
    //  update
    //------------------------------------------------------------------
    //  public void update( int byte8 )
    //  public void update( byte[] buffer )
    //  public void update( byte[] buffer, int index, int length )
    //------------------------------------------------------------------

    /**
     */
    public static int[] makeCrcTable(int poly) {
        final int BYTE_PATTERNS = 256;
        final int BYTE_BITS = 8;
        int[] crcTable = new int[BYTE_PATTERNS];

        for (int i = 0; i < BYTE_PATTERNS; i++) {
            crcTable[i] = i;

            for (int j = 0; j < BYTE_BITS; j++) {
                if ((crcTable[i] & 1) != 0) {
                    crcTable[i] = (crcTable[i] >> 1) ^ poly;
                } else {
                    crcTable[i] >>= 1;
                }
            }
        }

        return crcTable;
    }

    /**
     */
    public void update(int byte8) {
        final int BYTE_BITS = 8;
        this.crc = (this.crc >> BYTE_BITS)
                ^ this.crcTable[(this.crc ^ byte8) & 0xFF];
    }

    /**
     */
    public void update(byte[] buffer) {
        this.update(buffer, 0, buffer.length);
    }


    //------------------------------------------------------------------
    //  method of java.util.zip.Checksum
    //------------------------------------------------------------------
    //  other
    //------------------------------------------------------------------
    //  public void reset()
    //  public long getValue()
    //------------------------------------------------------------------

    /**
     */
    public void update(byte[] buffer, int index, int length) {
        final int BYTE_BITS = 8;

        while (0 < (index & 0x03) && 0 < length--) {
            this.crc = (this.crc >> BYTE_BITS)
                    ^ this.crcTable[(this.crc ^ buffer[index++]) & 0xFF];
        }

        while (4 <= length) {
            int data = (buffer[index++] & 0xFF)
                    | ((buffer[index++] & 0xFF) << 8)
                    | ((buffer[index++] & 0xFF) << 16)
                    | (buffer[index++] << 24);

            this.crc = (this.crc >> BYTE_BITS)
                    ^ this.crcTable[(this.crc ^ data) & 0xFF];
            data >>>= BYTE_BITS;
            this.crc = (this.crc >> BYTE_BITS)
                    ^ this.crcTable[(this.crc ^ data) & 0xFF];
            data >>>= BYTE_BITS;
            this.crc = (this.crc >> BYTE_BITS)
                    ^ this.crcTable[(this.crc ^ data) & 0xFF];
            data >>>= BYTE_BITS;
            this.crc = (this.crc >> BYTE_BITS)
                    ^ this.crcTable[(this.crc ^ data) & 0xFF];
            length -= 4;
        }

        while (0 < length--) {
            this.crc = (this.crc >> BYTE_BITS)
                    ^ this.crcTable[(this.crc ^ buffer[index++]) & 0xFF];
        }
    }

    /**
     */
    public void reset() {
        this.crc = this.init;
    }


    //------------------------------------------------------------------
    //  shared method
    //------------------------------------------------------------------
    //  public static int[] makeCrcTable( int init )
    //------------------------------------------------------------------

    /**
     */
    public long getValue() {
        return this.crc & 0xFFFF;
    }

}
//end of CRC16.java
