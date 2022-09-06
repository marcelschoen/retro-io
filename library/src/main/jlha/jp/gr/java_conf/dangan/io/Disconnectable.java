//start of Disconnectable.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * Disconnectable.java
 * 
 * Copyright (C) 2001-2002  Michel Ishizuka  All rights reserved.
 *
 */

package jp.gr.java_conf.dangan.io;

//import classes and interfaces

//import exceptions
import java.io.IOException;

/**
 *
 * <pre>
 * -- revision history --
 * $Log: Disconnectable.java,v $
 * Revision 1.0  2002/07/24 00:00:00  dangan
 * add to version control
 * [maintenance]
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.0 $
 */
public interface Disconnectable{

    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  public abstract void disconnect()
    //------------------------------------------------------------------
    /**
     */
    public abstract void disconnect() throws IOException;

}
//end of Disconnectable.java
