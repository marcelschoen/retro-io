//start of PostLzssEncoder.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * PostLzssEncoder.java
 * <p>
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
 * $Log: PostLzssEncoder.java,v $
 * Revision 1.0  2002/07/25 00:00:00  dangan
 * add to version control
 * [maintenance]
 *
 * </pre>
 *
 * @author $Author: dangan $
 * @version $Revision: 1.0 $
 */
public interface PostLzssEncoder {


    //------------------------------------------------------------------
    //  original method ( on the model of java.io.OutputStream )
    //------------------------------------------------------------------
    //  other
    //------------------------------------------------------------------
    //  public abstract void flush()
    //  public abstract void close()
    //------------------------------------------------------------------

    /**
     * <pre>
     * (1)
     *   PostLzssEncoder out = new ImplementedPostLzssEncoder();
     *   out.writeCode( 0 );
     *   out.writeCode( 0 );
     *   out.writeCode( 0 );
     *   out.close();
     *
     * (2)
     *   PostLzssEncoder out = new ImplementedPostLzssEncoder();
     *   out.writeCode( 0 );
     *   out.flush();
     *   out.writeCode( 0 );
     *   out.flush();
     *   out.writeCode( 0 );
     *   out.close();
     * </pre>
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public abstract void flush() throws IOException;

    /**
     */
    public abstract void close() throws IOException;


    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  write
    //------------------------------------------------------------------
    //  public abstract void writeCode( int code )
    //  public abstract void writeOffset( int offset )
    //------------------------------------------------------------------

    /**
     */
    public abstract void writeCode(int code) throws IOException;

    /**
     */
    public abstract void writeOffset(int offset) throws IOException;


    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  get LZSS parameter
    //------------------------------------------------------------------
    //  public abstract int getDictionarySize()
    //  public abstract int getMaxMatch()
    //  public abstract int getThreshold()
    //------------------------------------------------------------------

    /**
     */
    public abstract int getDictionarySize();

    /**
     */
    public abstract int getMaxMatch();

    /**
     */
    public abstract int getThreshold();

}
//end of PostLzssEncoder.java
