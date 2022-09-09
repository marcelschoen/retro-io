//start of Bits.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * Bits.java
 * <p>
 * Copyright (C) 2002  Michel Ishizuka  All rights reserved.
 */

package jp.gr.java_conf.dangan.io;

//import classes and interfaces

//import exceptions


/**
 *
 * <pre>
 * -- revision history --
 * $Log: Bits.java,v $
 * Revision 1.0  2002/12/05 00:00:00  dangan
 * first edition
 * add to version control
 *
 * </pre>
 *
 * @author $Author: dangan $
 * @version $Revision: 1.0 $
 */
public class Bits {


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private Bits()
    //------------------------------------------------------------------

    /**
     */
    private Bits() {
    }


    //------------------------------------------------------------------
    //  shared method
    //------------------------------------------------------------------
    //  length of bits
    //------------------------------------------------------------------
    //  public static int len( int val )
    //------------------------------------------------------------------

    /**
     */
    public static int len(int val) {
        int len = 0;
        while (0 != val) {
            val >>>= 1;
            len++;
        }
        return len;
    }

}
//end of Bits.java
