//start of LzssSearchMethod.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * LzssSearchMethod.java
 * <p>
 * Copyright (C) 2002  Michel Ishizuka  All rights reserved.
 */

package jp.gr.java_conf.dangan.util.lha;

//import classes and interfaces

//import exceptions


/**
 *
 * <br>
 * <pre>
 * LzssSearchMethod( int    DictionarySize,
 *                   int    MaxMatch,
 *                   int    Threshold,
 *                   byte[] TextBuffer )
 *
 * <pre>
 * LzssSearchMethod( int    DictionarySize,
 *                   int    MaxMatch,
 *                   int    Threshold,
 *                   byte[] TextBuffer,
 *                   Object ExtraArgument1,
 *                   Object ExtraArgument2 )
 * </pre>
 * <br>
 *
 * <pre>
 * -- revision history --
 * $Log: LzssSearchMethod.java,v $
 * Revision 1.1  2002/12/04 00:00:00  dangan
 * [maintenance]
 *
 * Revision 1.0  2002/07/24 00:00:00  dangan
 * add to version control
 * [change]
 * [maintenance]
 *
 * </pre>
 *
 * @author $Author: dangan $
 * @version $Revision: 1.1 $
 */
public interface LzssSearchMethod {

    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  public abstract void put( int position )
    //  public abstract int searchAndPut( int position )
    //  public abstract int search( int position, int lastPutPos )
    //  public abstract void slide()
    //  public abstract int putRequires()
    //------------------------------------------------------------------

    /**
     */
    public abstract void put(int position);

    /**
     *
     * @see LzssOutputStream#createSearchReturn(int, int)
     * @see LzssOutputStream#NOMATCH
     */
    public abstract int searchAndPut(int position);

    /**
     *
     * @see LzssOutputStream#createSearchReturn(int, int)
     * @see LzssOutputStream#NOMATCH
     */
    public abstract int search(int position, int lastPutPos);

    /**
     */
    public abstract void slide();

    /**
     */
    public abstract int putRequires();

}
//end of LzssSearchMethod.java
