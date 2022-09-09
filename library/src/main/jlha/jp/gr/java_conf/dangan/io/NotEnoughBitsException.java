//start of NotEnoughBitsException.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * NotEnoughBitsException.java
 * <p>
 * Copyright (C) 2001-2002  Michel Ishizuka  All rights reserved.
 */

package jp.gr.java_conf.dangan.io;

//import classes and interfaces

//import exceptions

import java.io.IOException;

/**
 *
 * <pre>
 * -- revision history --
 * $Log: NotEnoughBitsException.java,v $
 * Revision 1.0  2002/07/24 00:00:00  dangan
 * add to version control
 * [maintenance]
 *
 * </pre>
 *
 * @author $Author: dangan $
 * @version $Revision: 1.0 $
 */
public class NotEnoughBitsException extends IOException {


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  private int availableBits
    //------------------------------------------------------------------
    /**
     */
    private int availableBits;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private NotEnoughBitsException()
    //  public NotEnoughBitsException( int availableBits )
    //  public NotEnoughBitsException( String message, int availableBits )
    //------------------------------------------------------------------

    /**
     */
    private NotEnoughBitsException() {
    }

    /**
     */
    public NotEnoughBitsException(int availableBits) {
        super();
        this.availableBits = availableBits;
    }

    /**
     */
    public NotEnoughBitsException(String message, int availableBits) {
        super(message);
        this.availableBits = availableBits;
    }


    //------------------------------------------------------------------
    //  access method
    //------------------------------------------------------------------
    //  public int getAvailableBits()
    //------------------------------------------------------------------

    /**
     */
    public int getAvailableBits() {
        return this.availableBits;
    }

}
//end of NotEnoughBitsException.java
