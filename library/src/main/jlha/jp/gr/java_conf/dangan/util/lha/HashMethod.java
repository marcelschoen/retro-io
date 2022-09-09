//start of HashMethod.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * HashMethod.java
 * <p>
 * Copyright (C) 2002  Michel Ishizuka  All rights reserved.
 */

package jp.gr.java_conf.dangan.util.lha;

//import classes and interfaces

//import exceptions


/**
 * <pre>
 * HashMethod( byte[] TextBuffer )
 *
 * <pre>
 * HashMethod( byte[] TextBuffer,
 *             Object ExtraData1,
 *             Object ExtraData2 )
 * </pre>
 *
 * <pre>
 * -- revision history --
 * $Log: HashMethod.java,v $
 * Revision 1.0  2002/08/05 00:00:00  dangan
 * add to version cotrol
 * [change]
 *
 * </pre>
 *
 * @author $Author: dangan $
 * @version $Revision: 1.0 $
 */
public interface HashMethod {


    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  public abstract int hash( int position )
    //  public abstract int hashRequires()
    //  public abstract int tableSize()
    //------------------------------------------------------------------

    /**
     *
     */
    public abstract int hash(int position);

    /**
     */
    public abstract int hashRequires();

    /**
     */
    public abstract int tableSize();

}
//end of HashMethod.java
