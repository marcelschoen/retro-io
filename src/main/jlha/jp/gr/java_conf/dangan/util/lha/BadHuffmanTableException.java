//start of BadHuffmanTableException.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * BadHuffmanTableException.java
 * 
 * Copyright (C) 2001-2002  Michel Ishizuka  All rights reserved.
 */

package jp.gr.java_conf.dangan.util.lha;

//import classes and interfaces

//import exceptions
import java.io.IOException;

/**
 *
 * <pre>
 * -- revision history --
 * $Log: BadHuffmanTableException.java,v $
 * Revision 1.0  2002/07/24 00:00:00  dangan
 * add to version control
 * [maintenance]
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.0 $
 */
public class BadHuffmanTableException extends IOException{


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  public BadHuffmanTableException()
    //  public BadHuffmanTableException( String message )
    //------------------------------------------------------------------
    /**
     */
    public BadHuffmanTableException(){
        super();
    }

    /**
     */
    public BadHuffmanTableException( String message ){
        super( message );
    }
}
//end of BadHuffmanTableException.java
