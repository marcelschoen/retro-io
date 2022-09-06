//start of BitOutputStream.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * BitOutputStream.java
 * 
 * Copyright (C) 2001-2002  Michel Ishizuka  All rights reserved.
 * 
 * �ȉ��̏����ɓ��ӂ���Ȃ�΃\�[�X�ƃo�C�i���`���̍Ĕz�z�Ǝg�p��
 * �ύX�̗L���ɂ�����炸������B
 * 
 * �P�D�\�[�X�R�[�h�̍Ĕz�z�ɂ����Ē��쌠�\���� ���̏����̃��X�g
 *     ����щ��L�̐�������ێ����Ȃ��Ă͂Ȃ�Ȃ��B
 * 
 * �Q�D�o�C�i���`���̍Ĕz�z�ɂ����Ē��쌠�\���� ���̏����̃��X�g
 *     ����щ��L�̐��������g�p�������������� ���̑��̔z�z������
 *     �܂ގ����ɋL�q���Ȃ���΂Ȃ�Ȃ��B
 * 
 * ���̃\�t�g�E�F�A�͐Β˔���ڂɂ���Ė��ۏ؂Œ񋟂���A����̖�
 * �I��B���ł���Ƃ����ۏ؁A���i���l���L��Ƃ����ۏ؂ɂƂǂ܂炸�A
 * �����Ȃ閾���I����шÎ��I�ȕۏ؂����Ȃ��B
 * �Β˔���ڂ� ���̃\�t�g�E�F�A�̎g�p�ɂ�钼�ړI�A�ԐړI�A����
 * �I�A����ȁA�T�^�I�ȁA���邢�͕K�R�I�ȑ��Q(�g�p�ɂ��f�[�^��
 * �����A�Ɩ��̒��f�〈���܂�Ă������v�̈⎸�A��֐��i��������
 * �T�[�r�X�̓�������l�����邪�A�����Ă��ꂾ���Ɍ��肳��Ȃ�
 * ���Q)�ɑ΂��āA�����Ȃ鎖�Ԃ̌����ƂȂ����Ƃ��Ă��A�_���̐�
 * �C�△�ߎ��ӔC���܂� �����Ȃ�ӔC�����낤�Ƃ��A���Ƃ����ꂪ�s
 * ���s�ׂ̂��߂ł������Ƃ��Ă��A�܂��͂��̂悤�ȑ��Q�̉\������
 * ������Ă����Ƃ��Ă���؂̐ӔC�𕉂�Ȃ����̂Ƃ���B
 */

package jp.gr.java_conf.dangan.io;

//import classes and interfaces

import java.io.IOException;
import java.io.OutputStream;

//import exceptions


/**
 * �ڑ����ꂽ�o�̓X�g���[���Ƀr�b�g�f�[�^���o�͂��邽�߂�
 * �o�̓X�g���[���N���X�B<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: BitOutputStream.java,v $
 * Revision 1.1  2002/12/05 00:00:00  dangan
 * [maintenance]
 *     �\�[�X����
 *
 * Revision 1.0  2002/09/11 00:00:00  dangan
 * add to version control
 * [change]
 *     close() ��� write�n���\�b�h�� flush() ��
 *     ��O�𓊂���悤�ɏC��
 * [maintenance]
 *     �^�u�p�~
 *     ���C�Z���X���̏C��
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class BitOutputStream extends OutputStream{


    //------------------------------------------------------------------
    //  class field
    //------------------------------------------------------------------
    //  default
    //------------------------------------------------------------------
    //  private static final int DefaultCacheSize
    //------------------------------------------------------------------
    /**
     * �f�t�H���g���L���b�V���T�C�Y
     */
    private static final int DefaultCacheSize = 1024;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  sink
    //------------------------------------------------------------------
    //  private OutputStream out
    //------------------------------------------------------------------
    /**
     * �ڑ����ꂽ�o�̓X�g���[��
     */
    private OutputStream out;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  cache
    //------------------------------------------------------------------
    //  private byte[] cache
    //  private int    cachePosition
    //------------------------------------------------------------------
    /**
     * ���x�ቺ�}�~�p�o�C�g�z��
     */
    private byte[] cache;

    /**
     * cacheBuffer ���̌��ݏ����ʒu
     */
    private int    cachePosition;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  bit buffer
    //------------------------------------------------------------------
    //  private int bitBuffer
    //  private int bitCount
    //------------------------------------------------------------------
    /**
     * �r�b�g�o�b�t�@
     */
    private int bitBuffer;

    /**
     * bitBuffer �� �L���r�b�g��
     */
    private int bitCount;


    //------------------------------------------------------------------
    //  constructer
    //------------------------------------------------------------------
    //  private BitOutputStream()
    //  public BitOutputStream( OutputStream out )
    //  public BitOutputStream( OutputStream out, int CacheSize )
    //------------------------------------------------------------------
    /**
     * �f�t�H���g�R���X�g���N�^�B
     * �g�p�s�B
     */
    private BitOutputStream(){  }

    /**
     * �o�̓X�g���[�� out �� �f�[�^���r�b�g�P�ʂ�
     * �������߂�悤�ȃX�g���[�����\�z����B<br>
     * �L���b�V���T�C�Y�ɂ̓f�t�H���g�l���g�p�����B
     * 
     * @param out �o�̓X�g���[��
     */
    public BitOutputStream( OutputStream out ){
        this( out, BitOutputStream.DefaultCacheSize );

    }

    /**
     * �o�̓X�g���[�� out �� �f�[�^���r�b�g�P�ʂ�
     * �������߂�悤�ȃX�g���[�����\�z����B<br>
     * 
     * @param out       �o�̓X�g���[��
     * @param CacheSize �L���b�V���T�C�Y
     * 
     * @exception IllegalArgumentException
     *                   CacheSize �� 4�����̏ꍇ�A�܂���
     *                   CacheSize �� 4�̔{���Ŗ����ꍇ�B
     */
    public BitOutputStream( OutputStream out, int CacheSize ){
        if( out != null && 4 <= CacheSize && 0 == ( CacheSize & 0x03 ) ){
            this.out            = out;
            this.cache          = new byte[ CacheSize ];
            this.cachePosition  = 0;
            this.bitBuffer      = 0;
            this.bitCount       = 0;
        }else if( out == null ){
            throw new NullPointerException( "out" );
        }else if( CacheSize < 4 ){
            throw new IllegalArgumentException( "CacheSize must be 4 or more." );
        }else{
            throw new IllegalArgumentException( "CacheSize must be multiple of 4." );
        }
    }


    //------------------------------------------------------------------
    //  method of java.io.OutputStream
    //------------------------------------------------------------------
    //  write
    //------------------------------------------------------------------
    //  public void write( int data )
    //  public void write( byte[] buffer )
    //  public void write( byte[] buffer, int index, int length )
    //------------------------------------------------------------------
    /**
     * �ڑ����ꂽ�o�̓X�g���[���� 8�r�b�g�̃f�[�^���o�͂���B<br>
     * 
     * @param data 8�r�b�g�̃f�[�^�B<br>
     *             ���24�r�b�g�͖��������B<br>
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void write( int data ) throws IOException {
        this.writeBits( 8, data );
    }

    /**
     * �ڑ����ꂽ�o�̓X�g���[����buffer�̓��e��A������
     * 8�r�b�g�̃f�[�^�Ƃ��ďo�͂���B<br>
     * 
     * @param buffer �o�͂��ׂ��f�[�^���i�[�����o�C�g�z��<br>
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void write( byte[] buffer ) throws IOException {
        this.write( buffer, 0, buffer.length );                                 //throws IOException
    }

    /**
     * �ڑ����ꂽ�o�̓X�g���[����buffer��index����
     * length�o�C�g�̓��e��A������ 8�r�b�g�̃f�[�^
     * �Ƃ��ďo�͂���B<br>
     * 
     * @param buffer �o�͂��ׂ��f�[�^���i�[�����o�C�g�z��
     * @param index  buffer���̃f�[�^�J�n�ʒu
     * @param length �o�͂���f�[�^��(�o�C�g��)
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void write( byte[] buffer, int index, int length )
                                                           throws IOException {
        if( this.bitCount % 8 == 0 ){
            this.flush();                                                       //throws IOException
            this.out.write( buffer, index, length );                            //throws IOException
        }else{
            while( 0 < length-- )
                this.writeBits( 8, buffer[index++] );                           //throws IOException
        }
    }


    //------------------------------------------------------------------
    //  method of java.io.OutputStream
    //------------------------------------------------------------------
    //  other
    //------------------------------------------------------------------
    //  public void flush()
    //  public void close()
    //------------------------------------------------------------------
    /**
     * ���̃r�b�g�o�̓X�g���[���Ƀo�b�t�@�����O����Ă���
     * 8�r�b�g�P�ʂ̃f�[�^��S�ďo�͐�ɏo�͂���B
     * 8�r�b�g�ɖ����Ȃ��f�[�^�͏o�͂���Ȃ����Ƃɒ��ӁB<br>
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void flush() throws IOException {
        while( 8 <= this.bitCount ){
            this.cache[ this.cachePosition++ ] = (byte)( this.bitBuffer >> 24 );
            this.bitBuffer <<= 8;
            this.bitCount  -= 8;
        }

        this.out.write( this.cache, 0, this.cachePosition );                    //throws IOException
        this.cachePosition = 0;
        this.out.flush();                                                       //throws IOException
    }

    /**
     * ���̏o�̓X�g���[���ƁA�ڑ����ꂽ�o�̓X�g���[������A
     * �g�p���Ă������\�[�X���J������B<br>
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void close() throws IOException {
        while( 0 < this.bitCount ){
            this.cache[ this.cachePosition++ ] = (byte)( this.bitBuffer >> 24 );
            this.bitBuffer <<= 8;
            this.bitCount  -= 8;
        }

        this.out.write( this.cache, 0, this.cachePosition );                    //throws IOException
        this.cachePosition = 0;
        this.out.flush();                                                       //throws IOException
        this.out.close();                                                       //throws IOException

        this.out            = null;
        this.cache          = null;
        this.cachePosition  = 0;
        this.bitCount       = 128;
        this.bitBuffer      = 0;
    }


    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  bit write
    //------------------------------------------------------------------
    //  public void writeBit( int data )
    //  public void writeBoolean( boolean bool )
    //  public void writeBits( int count, int data )
    //------------------------------------------------------------------
    /**
     * �ڑ����ꂽ�o�̓X�g���[����1�r�b�g�̃f�[�^���o�͂���B<br>
     * 
     * @param data 1�r�b�g�̃f�[�^�B<br>
     *             ���31�r�b�g�͖��������B<br>
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void writeBit( int data ) throws IOException {
        this.bitBuffer |= ( data & 0x00000001 ) << 31 - this.bitCount;
        this.bitCount++;

        if( 32 <= this.bitCount ) this.writeOutBitBuffer();                        //throws IOException
    }

    /**
     * �^�U�l��ڑ����ꂽ�o�̓X�g���[����1�r�b�g��
     * �f�[�^�Ƃ��ďo�͂���B<br>
     * true �� 1�Afalse �� 0�Ƃ��ďo�͂���B<br>
     * java.io.DataOutput �� writeBoolean() �Ƃ�
     * �݊����������̂Œ��ӂ��邱�ƁB<br>
     * 
     * @param bool �^�U�l
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void writeBoolean( boolean bool ) throws IOException {
        if( bool )  this.bitBuffer |= 1 << 31 - this.bitCount;

        this.bitCount++;

        if( 32 <= this.bitCount ) this.writeOutBitBuffer();                     //throws IOException
    }

    /**
     * �ڑ����ꂽ�o�̓X�g���[���Ƀr�b�g�f�[�^���o�͂���B<br>
     * 
     * @param count data �̗L���r�b�g��
     * @param data  �r�b�g�f�[�^
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void writeBits( int count, int data ) throws IOException {
        while( 0 < count ){
            int available = 32 - this.bitCount;
            if( count < available ){
                this.bitBuffer   |= ( data & ( 0xFFFFFFFF >>> 32 - count ) )
                                                          << available - count;
                this.bitCount    += count;
                count            = 0;
            }else{
                count          -= available;
                this.bitBuffer |= data >> count
                                & ( 0xFFFFFFFF >>> 32 - available );
                this.writeOutBitBuffer();
            }
        }
    }

    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  private void writeOutBitBuffer()
    //------------------------------------------------------------------
    /**
     * �r�b�g�o�b�t�@�ɒ~����ꂽ�f�[�^��S�ăL���b�V����
     * �o�͂��A�L���b�V�����������ꍇ�̓L���b�V���̃f�[�^��
     * �ڑ����ꂽ�o�̓X�g���[���ɏo�͂���B<br>
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    private void writeOutBitBuffer() throws IOException {
        this.cache[ this.cachePosition++ ] = (byte)( this.bitBuffer >> 24 );
        this.cache[ this.cachePosition++ ] = (byte)( this.bitBuffer >> 16 );
        this.cache[ this.cachePosition++ ] = (byte)( this.bitBuffer >>  8 );
        this.cache[ this.cachePosition++ ] = (byte)this.bitBuffer;

        this.bitBuffer = 0;
        this.bitCount  = 0;

        if( this.cache.length <= this.cachePosition ){
            this.out.write( this.cache );
            this.cachePosition = 0;
        }
    }

}
//end of BitOutputStream.java
