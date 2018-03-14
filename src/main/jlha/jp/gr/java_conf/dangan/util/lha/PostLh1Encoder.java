//start of PostLh1Encoder.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * PostLh1Encoder.java
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

import java.io.IOException;
import java.io.OutputStream;

//import exceptions

/**
 * -lh1- ���k�p�� PostLzssEncoder�B <br>
 * 
 * <pre>
 * -- revision history --
 * $Log: PostLh1Encoder.java,v $
 * Revision 1.1  2002/12/01 00:00:00  dangan
 * [maintenance]
 *     �\�[�X����
 *
 * Revision 1.0  2002/07/31 00:00:00  dangan
 * add to version control
 * [maintenance]
 *     �\�[�X����
 *     �^�u�p�~
 *     ���C�Z���X���̏C��
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class PostLh1Encoder implements PostLzssEncoder{


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
    private static final int DictionarySize = 4096;

    /** �ő��v�� */
    private static final int MaxMatch       = 60;

    /** �ŏ���v�� */
    private static final int Threshold      = 3;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  sink
    //------------------------------------------------------------------
    //  private BitOutputStream out
    //------------------------------------------------------------------
    /**
     * -lh1- �`���̈��k�f�[�^�̏o�͐�� �r�b�g�o�̓X�g���[��
     */
    private BitOutputStream out;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  dynamic huffman tree
    //------------------------------------------------------------------
    //  private DynamicHuffman huffman
    //------------------------------------------------------------------
    /**
     * Code�����k�p�K���I�n�t�}����
     */
    private DynamicHuffman huffman;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  static huffman list
    //------------------------------------------------------------------
    //  private int[] offHiCode
    //  private int[] offHiLen
    //------------------------------------------------------------------
    /**
     * offset���̏��6bit���k�p�n�t�}�������̕\
     */
    private int[] offHiCode;

    /**
     * offset���̏��6bit���k�p�n�t�}���������̕\
     */
    private int[] offHiLen;


    //------------------------------------------------------------------
    //  constructer
    //------------------------------------------------------------------
    //  private PostLh1Encoder()
    //  public PostLh1Encoder( OutputStream out )
    //------------------------------------------------------------------
    /**
     * �f�t�H���g�R���X�g���N�^�B
     * �g�p�s�B
     */
    private PostLh1Encoder(){   }

    /**
     * -lh1- ���k�p PostLzssEncoder ���\�z����B
     * 
     * @param out ���k�f�[�^���󂯎��o�̓X�g���[��
     */
    public PostLh1Encoder( OutputStream out ){
        if( out != null ){
            if( out instanceof BitOutputStream ){
                this.out   = (BitOutputStream)out;
            }else{
                this.out   = new BitOutputStream( out );
            }
            this.huffman   = new DynamicHuffman( 314 );
            this.offHiLen  = PostLh1Encoder.createLenList();
            try{
                this.offHiCode = StaticHuffman.LenListToCodeList( this.offHiLen );
            }catch( BadHuffmanTableException exception ){
            }
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
        int node  = this.huffman.codeToNode( code );
        int hcode = 0;
        int hlen  = 0;
        do{
            hcode >>>= 1;
            hlen++;
            if( ( node & 1 ) != 0 ) hcode |= 0x80000000;

            node = this.huffman.parentNode( node );
        }while( node != DynamicHuffman.ROOT );

        this.out.writeBits( hlen, hcode >> ( 32 - hlen ) );                     //throws IOException
        this.huffman.update( code );
    }

    /**
     * LZSS �ň��k���ꂽ���k�R�[�h�̂�����v�ʒu���������ށB<br>
     * 
     * @param offset LZSS �ň��k���ꂽ���k�R�[�h�̂�����v�ʒu
     */
    public void writeOffset( int offset ) throws IOException {
        int offHi = ( offset >> 6 );
        this.out.writeBits( this.offHiLen[offHi], this.offHiCode[offHi] );      //throws IOException
        this.out.writeBits( 6, offset );                                        //throws IOException
    }


    //------------------------------------------------------------------
    //  method PostLzssEncoder
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

        this.out       = null;
        this.huffman   = null;
        this.offHiLen  = null;
        this.offHiCode = null;
    }


    //------------------------------------------------------------------
    //  method of PostLzssEncoder
    //------------------------------------------------------------------
    //  get LZSS parameter
    //------------------------------------------------------------------
    //  public int getDictionarySize()
    //  public int getMaxMatch()
    //  public int getThreshold()
    //------------------------------------------------------------------
    /**
     * -lh1-�`���� LZSS�����̃T�C�Y�𓾂�B
     * 
     * @return -lh1-�`���� LZSS�����̃T�C�Y
     */
    public int getDictionarySize(){
        return PostLh1Encoder.DictionarySize;
    }

    /**
     * -lh1-�`���� LZSS�̍ő��v���𓾂�B
     * 
     * @return -lz5-�`���� LZSS�̍ő��v��
     */
    public int getMaxMatch(){
        return PostLh1Encoder.MaxMatch;
    }

    /**
     * -lh1-�`���� LZSS�̈��k�A�񈳏k��臒l�𓾂�B
     * 
     * @return -lh1-�`���� LZSS�̈��k�A�񈳏k��臒l
     */
    public int getThreshold(){
        return PostLh1Encoder.Threshold;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  private static int[] createLenList()
    //------------------------------------------------------------------
    /**
     * -lh1- �� offset�f�R�[�h�pStaticHuffman��
     * �n�t�}�����������X�g�𐶐�����B
     * 
     * @return -lh1- �� offset�f�R�[�h�pStaticHuffman��
     *         �n�t�}�����������X�g
     */
    private static int[] createLenList(){
        final int length = 64;
        final int[] list = { 3, 0x01, 0x04, 0x0C, 0x18, 0x30, 0 };

        int[] LenList = new int[ length ];
        int index = 0;
        int len = list[ index++ ];

        for( int i = 0 ; i < length ; i++ ){
            if( list[index] == i ){
                len++;
                index++;
            }
            LenList[i] = len;
        }
        return LenList;
    }

}
//end of PostLh1Encoder.java
