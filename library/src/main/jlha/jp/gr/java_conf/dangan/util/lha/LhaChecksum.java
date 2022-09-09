//start of LhaChecksum.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * LhaChecksum.java
 * <p>
 * Copyright (C) 2001-2002  Michel Ishizuka  All rights reserved.
 */

package jp.gr.java_conf.dangan.util.lha;

//import classes and interfaces

import java.util.zip.Checksum;

//import exceptions

/**
 *
 * <pre>
 * -- revision history --
 * $Log: LhaChecksum.java,v $
 * Revision 1.0  2002/08/05 00:00:00  dangan
 * add to version control
 * [maintanance]
 *
 * </pre>
 *
 * @author $Author: dangan $
 * @version $Revision: 1.0 $
 */
public class LhaChecksum implements Checksum {


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  private int checksum
    //------------------------------------------------------------------
    /**
     */
    private int checksum;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  public LhaChecksum()
    //------------------------------------------------------------------

    /**
     */
    public LhaChecksum() {
        super();
        this.reset();
    }


    //------------------------------------------------------------------
    //  method of java.util.zip.Checksum method
    //------------------------------------------------------------------
    //  update
    //------------------------------------------------------------------
    //  public void update( int byte8 )
    //  public void update( byte[] buffer )
    //  public void update( byte[] buffer, int index, int length )
    //------------------------------------------------------------------

    /**
     */
    public void update(int byte8) {
        this.checksum += byte8;
    }

    /**
     *
     */
    public void update(byte[] buffer) {
        this.update(buffer, 0, buffer.length);
    }

    /**
     */
    public void update(byte[] buffer, int index, int length) {
        while (0 < length--)
            this.checksum += buffer[index++];
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
    public void reset() {
        this.checksum = 0;
    }

    /**
     */
    public long getValue() {
        return this.checksum & 0xFF;
    }

}
//end of LhaChecksum.java
