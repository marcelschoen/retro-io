//start of PostLzsEncoder.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * PostLzsEncoder.java
 * 
 * Copyright (C) 2002  Michel Ishizuka  All rights reserved.
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

package jp.gr.java_conf.dangan.util.lha;

//import classes and interfaces

import jp.gr.java_conf.dangan.io.BitOutputStream;
import jp.gr.java_conf.dangan.io.Bits;

import java.io.IOException;
import java.io.OutputStream;

//import exceptions


/**
 * -lzs- ���k�p PostLzssEncoder�B
 * 
 * <pre>
 * -- revision history --
 * $Log: PostLzsEncoder.java,v $
 * Revision 1.1  2002/12/01 00:00:00  dangan
 * [maintenance]
 *     �\�[�X����
 *
 * Revision 1.0  2002/07/31 00:00:00  dangan
 * add to version control
 * [bug fix]
 *     -lzs- �� MaxMatch �� 16 �łȂ� 17 �������̂��C���B
 * [maintenance]
 *     �\�[�X����
 *     �^�u�̔p�~
 *     ���C�Z���X���̏C��
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class PostLzsEncoder implements PostLzssEncoder {


    //------------------------------------------------------------------
    //  class field
    //------------------------------------------------------------------
    //  LZSS parameter
    //------------------------------------------------------------------
    //  private static final int DictionarySize
    //  private static final int MaxMatch
    //  private static final int Threshold
    //------------------------------------------------------------------
    /** �����T�C�Y */
    private static final int DictionarySize = 2048;

    /** �ő��v�� */
    private static final int MaxMatch       = 17;

    /** �ŏ���v�� */
    private static final int Threshold      = 2;


    //------------------------------------------------------------------
    //  class field
    //------------------------------------------------------------------
    //  length of LZSS code
    //------------------------------------------------------------------
    //  private static final int PositionBits
    //  private static final int LengthBits
    //------------------------------------------------------------------
    /** ��v�ʒu�̃r�b�g�� */
    private static final int PositionBits = Bits.len( PostLzsEncoder.DictionarySize - 1 );

    /** ��v���̃r�b�g�� */
    private static final int LengthBits = Bits.len( PostLzsEncoder.MaxMatch - PostLzsEncoder.Threshold );


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  private BitOutputStream out
    //  private int position
    //  private int matchLength
    //------------------------------------------------------------------
    /**
     * -lzs- �`���̃f�[�^���o�͂���r�b�g�o�̓X�g���[��
     */
    private BitOutputStream out;

    /**
     * �X�g���[�������ݏ����ʒu
     */
    private int position;

    /**
     * ���ݏ�������LZSS���k�R�[�h
     */
    private int matchLength;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private PostLzsEncoder()
    //  public PostLzsEncoder( OutputStream out )
    //------------------------------------------------------------------
    /**
     * �f�t�H���g�R���X�g���N�^�B
     * �g�p�s�B
     */
    private PostLzsEncoder(){   }

    /**
     * -lzs- ���k�p PostLzssEncoder ���\�z����B
     * 
     * @param out -lzs- �`���̈��k�f�[�^���o�͂���X�g���[��
     */
    public PostLzsEncoder( OutputStream out ){
        if( out != null ){
            if( out instanceof BitOutputStream ){
                this.out = (BitOutputStream)out;
            }else{
                this.out = new BitOutputStream( out );
            }
            this.position    = 0;
            this.matchLength = 0;
        }else{
            throw new NullPointerException( "out" );
        }
    }


    //------------------------------------------------------------------
    //  method of PostLzssEncoder
    //------------------------------------------------------------------
    //  write
    //------------------------------------------------------------------
    //  public void writeCode( int code )
    //  public void writeOffset( int offset )
    //------------------------------------------------------------------
    /**
     * 1byte �� LZSS�����k�̃f�[�^�������́A
     * LZSS �ň��k���ꂽ���k�R�[�h�̂�����v�����������ށB<br>
     * 
     * @param code 1byte �� LZSS�����k�̃f�[�^�������́A
     *             LZSS �ň��k���ꂽ���k�R�[�h�̂�����v��
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void writeCode( int code ) throws IOException {
        if( code < 0x100 ){
            this.out.writeBit( 1 );                                             //throws IOException
            this.out.writeBits( 8, code );                                      //throws IOException
            this.position++;
        }else{
            // close() ��� writeCode() ��
            // NullPointerException �𓊂��邱�Ƃ����҂��Ă���B
            this.out.writeBit( 0 );                                             //throws IOException
            this.matchLength = code - 0x100;
        }
    }

    /**
     * LZSS �ň��k���ꂽ���k�R�[�h�̂�����v�ʒu���������ށB<br>
     * 
     * @param offset LZSS �ň��k���ꂽ���k�R�[�h�̂�����v�ʒu
     */
    public void writeOffset( int offset ) throws IOException {
        int pos = ( this.position - offset - 1
                  - PostLzsEncoder.MaxMatch )
                & ( PostLzsEncoder.DictionarySize - 1 );

        this.position += this.matchLength + PostLzsEncoder.Threshold;

        this.out.writeBits( this.PositionBits, pos );                           //throws IOException
        this.out.writeBits( this.LengthBits,   this.matchLength );              //throws IOException
    }


    //------------------------------------------------------------------
    //  method of PostLzssEncoder
    //------------------------------------------------------------------
    //  other
    //------------------------------------------------------------------
    //  public void flush()
    //  public void close()
    //------------------------------------------------------------------
    /**
     * ���� PostLzssEncoder �Ƀo�b�t�@�����O����Ă���
     * �S�Ă� 8�r�b�g�P�ʂ̃f�[�^���o�͐�� OutputStream �ɏo�͂��A 
     * �o�͐�� OutputStream �� flush() ����B<br>
     * ���̃��\�b�h�͈��k����ω������Ȃ��B 
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     *
     * @see PostLzssEncoder#flush()
     * @see BitOutputStream#flush()
     */
    public void flush() throws IOException {
        this.out.flush();                                                       //throws IOException
    }

    /**
     * ���̏o�̓X�g���[���ƁA�ڑ����ꂽ�o�̓X�g���[������A
     * �g�p���Ă������\�[�X���������B<br>
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void close() throws IOException {
        this.out.close();                                                       //throws IOException

        this.out = null;
    }


    //------------------------------------------------------------------
    //  method of PostLzssEncoder
    //------------------------------------------------------------------
    //  get LZSS patameter
    //------------------------------------------------------------------
    //  public int getDictionarySize()
    //  public int getMaxMatch()
    //  public int getThreshold()
    //------------------------------------------------------------------
    /**
     * -lzs-�`���� LZSS�����̃T�C�Y�𓾂�B
     * 
     * @return -lzs-�`���� LZSS�����̃T�C�Y
     */
    public int getDictionarySize(){
        return PostLzsEncoder.DictionarySize;
    }

    /**
     * -lzs-�`���� LZSS�̍ő��v���𓾂�B
     * 
     * @return -lzs-�`���� LZSS�̍ő��v��
     */
    public int getMaxMatch(){
        return PostLzsEncoder.MaxMatch;
    }

    /**
     * -lzs-�`���� LZSS�̈��k�A�񈳏k��臒l�𓾂�B
     * 
     * @return -lzs-�`���� LZSS�̈��k�A�񈳏k��臒l
     */
    public int getThreshold(){
        return PostLzsEncoder.Threshold;
    }

}
//end of PostLzsEncoder.java
