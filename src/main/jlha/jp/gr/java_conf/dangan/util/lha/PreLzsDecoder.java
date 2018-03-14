//start of PreLzsDecoder.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * PreLzsDecoder.java
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

package jp.gr.java_conf.dangan.util.lha;

//import classes and interfaces

import jp.gr.java_conf.dangan.io.BitDataBrokenException;
import jp.gr.java_conf.dangan.io.BitInputStream;
import jp.gr.java_conf.dangan.io.Bits;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

//import exceptions

/**
 * -lzs- �𓀗p PreLzssDecoder�B
 * 
 * <pre>
 * -- revision history --
 * $Log: PreLzsDecoder.java,v $
 * Revision 1.1  2002/12/06 00:00:00  dangan
 * [maintenance]
 *     �\�[�X����
 *
 * Revision 1.0  2002/08/05 00:00:00  dangan
 * add to version control
 * [bug fix]
 *     -lzs- �� MaxMatch �� 17 �ł���ׂ��� 16 �ƂȂ��Ă����̂��C���B
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
public class PreLzsDecoder implements PreLzssDecoder{


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
    //  bit length
    //------------------------------------------------------------------
    //  private static final int OffsetBits
    //  private static final int LengthBits
    //------------------------------------------------------------------
    /** ��v�ʒu�̃r�b�g�� */
    private static final int OffsetBits = Bits.len( PreLzsDecoder.DictionarySize - 1 );

    /** ��v���̃r�b�g�� */
    private static final int LengthBits = Bits.len( PreLzsDecoder.MaxMatch - PreLzsDecoder.Threshold );


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  source
    //------------------------------------------------------------------
    //  private BitInputStream in
    //------------------------------------------------------------------
    /**
     * -lzs- �`���̈��k�f�[�^���������� BitInputStream
     */
    private BitInputStream in;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  current position
    //------------------------------------------------------------------
    //  private int position
    //  private int matchOffset
    //  private int matchLength
    //------------------------------------------------------------------
    /** 
     * ���ݏ����ʒu�B
     * LzssInputStream�̓�����Ԃ��擾�ł��Ȃ����߂ɑ��݂���B
     * LzssInputStream�̓����N���X�Ƃ��ď����΁Aposition�͕K�v�����B
     */
    private int position;

    /** �ł��V����Lzss�R�[�h�̈�v�ʒu */
    private int matchOffset;

    /**
     * �ł��V����Lzss�R�[�h�̈�v��
     * LzssInputStream�̓�����Ԃ��擾�ł��Ȃ����߂ɑ��݂���B
     * LzssInputStream�̓����N���X�Ƃ��ď����΁AmatchLength�͕K�v�����B
     */
    private int matchLength;


    //------------------------------------------------------------------
    //  member values
    //------------------------------------------------------------------
    //  backup for mark/reset
    //------------------------------------------------------------------
    //  private int markPosition
    //  private int markMatchOffset
    //  private int markMatchLength
    //------------------------------------------------------------------
    /** matchPosition�̃o�b�N�A�b�v�p */
    private int markPosition;

    /** matchPosition�̃o�b�N�A�b�v�p */
    private int markMatchOffset;

    /** matchLength�̃o�b�N�A�b�v�p */
    private int markMatchLength;


    //------------------------------------------------------------------
    //  constructers
    //------------------------------------------------------------------
    //  public PreLzsDecoder( InputStream in )
    //------------------------------------------------------------------
    /**
     * -lzs- �𓀗p PreLzssDecoder ���\�z����B
     * 
     * @param in -lzs- �`���̈��k�f�[�^������������̓X�g���[��
     */
    public PreLzsDecoder( InputStream in ){
        if( in != null ){
            if( in instanceof BitInputStream ){
                this.in = (BitInputStream)in;
            }else{
                this.in = new BitInputStream( in );
            }
            this.position    = 0;
            this.matchOffset = 0;
            this.matchLength = 0;
        }else{
            throw new NullPointerException( "in" );
        }
    }


    //------------------------------------------------------------------
    //  method of PreLzssDecoder
    //------------------------------------------------------------------
    //  read
    //------------------------------------------------------------------
    //  public int readCode()
    //  public int readOffset()
    //------------------------------------------------------------------
    /**
     * -lzs- �ň��k���ꂽ
     * 1byte �� LZSS�����k�̃f�[�^�A
     * �������͈��k�R�[�h�̂�����v����ǂݍ��ށB<br>
     * 
     * @return 1byte �� �����k�̃f�[�^�������́A
     *         ���k���ꂽ���k�R�[�h�̂�����v��
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public int readCode() throws IOException {
        try{
            if( this.in.readBoolean() ){
                this.position++;
                return this.in.readBits( 8 );
            }else{
                this.matchOffset = this.in.readBits( this.OffsetBits );
                this.matchLength = this.in.readBits( this.LengthBits );
                return this.matchLength | 0x100;
            }
        }catch( BitDataBrokenException exception ){
            if( exception.getCause() instanceof EOFException )
                throw (EOFException)exception.getCause();
            else
                throw exception;
        }
    }

    /**
     * -lzs- �ň��k���ꂽ���k�R�[�h�̂���
     * ��v�ʒu��ǂݍ��ށB<br>
     * 
     * @return -lzs- �ň��k���ꂽ���k�R�[�h�̂�����v�ʒu
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public int readOffset() throws IOException {
        int offset = ( this.position - this.matchOffset - 1
                     - PreLzsDecoder.MaxMatch )
                   & ( PreLzsDecoder.DictionarySize - 1 );

        this.position += this.matchLength + PreLzsDecoder.Threshold;
        return offset;
    }


    //------------------------------------------------------------------
    //  method of PreLzssDecoder
    //------------------------------------------------------------------
    //  mark / reset 
    //------------------------------------------------------------------
    //  public void mark( int readLimit )
    //  public void reset()
    //  public boolean markSupported()
    //------------------------------------------------------------------
    /**
     * �ڑ����ꂽ���̓X�g���[���̌��݈ʒu�Ƀ}�[�N��ݒ肵�A
     * reset() ���\�b�h�Ń}�[�N�������_�� �ǂݍ��݈ʒu��
     * �߂��悤�ɂ���B<br>
     * InputStream �� mark() �ƈႢ�AreadLimit �Őݒ肵��
     * ���E�o�C�g�����O�Ƀ}�[�N�ʒu�������ɂȂ�\����
     * ���鎖�ɒ��ӂ��邱�ƁB<br>
     * 
     * @param readLimit �}�[�N�ʒu�ɖ߂����E�̃o�C�g���B
     *                  ���̃o�C�g���𒴂��ăf�[�^��ǂ�
     *                  ���񂾏ꍇ reset()�ł��Ȃ��Ȃ��
     *                  �\��������B<br>
     * 
     * @see PreLzssDecoder#mark(int)
     */
    public void mark( int readLimit ){
        this.in.mark( ( readLimit * 9 + 7 ) / 8 + 1 );
        this.markPosition    = this.position;
        this.markMatchOffset = this.matchOffset;
        this.markMatchLength = this.matchLength;
    }

    /**
     * �ڑ����ꂽ���̓X�g���[���̓ǂݍ��݈ʒu���Ō��
     * mark() ���\�b�h���Ăяo���ꂽ�Ƃ��̈ʒu�ɐݒ肷��B<br>
     * 
     * @exception IOException <br>
     * &emsp;&emsp; (1) mark() ������ reset() ���悤�Ƃ����ꍇ�B<br>
     * &emsp;&emsp; (2) �ڑ����ꂽ���̓X�g���[���� markSupported()��
     *                  false ��Ԃ��ꍇ�B<br>
     * &emsp;&emsp; (3) �ڑ����ꂽ���̓X�g���[����
     *                  ���o�̓G���[�����������ꍇ�B<br>
     * &emsp;&emsp; �̉��ꂩ�B
     */
    public void reset() throws IOException {
        //mark()���Ȃ��� reset()���悤�Ƃ����ꍇ�A
        //�ڑ����ꂽ InputStream �� mark/reset���T�|�[�g���Ȃ��ꍇ��
        //BitInputStream �� reset()�ɂ���� IOException����������B
        this.in.reset();                                                        //throws IOException

        this.position    = this.markPosition;
        this.matchOffset = this.markMatchOffset;
        this.matchLength = this.markMatchLength;
    }

    /**
     * �ڑ����ꂽ���̓X�g���[���� mark() �� reset() ��
     * �T�|�[�g���邩�𓾂�B<br>
     * 
     * @return �X�g���[���� mark() �� reset() ��
     *         �T�|�[�g����ꍇ�� true�B<br>
     *         �T�|�[�g���Ȃ��ꍇ�� false�B<br>
     */
    public boolean markSupported(){
        return this.in.markSupported();
    }


    //------------------------------------------------------------------
    //  method of PreLzssDecoder
    //------------------------------------------------------------------
    //  other
    //------------------------------------------------------------------
    //  public int available()
    //  public void close()
    //------------------------------------------------------------------
    /**
     * �u���b�N�����ɓǂݏo�����Ƃ̏o����Œ�o�C�g���𓾂�B<br>
     * InputStream �� available() �ƈႢ�A
     * ���̍Œ�o�C�g���͕ۏ؂����B<br>
     * 
     * @return �u���b�N���Ȃ��œǂݏo����Œ�o�C�g���B<br>
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     * 
     * @see PreLzssDecoder#available()
     */
    public int available() throws IOException {
        return Math.max( this.in.availableBits() / 9 - 2, 0 );
    }

    /**
     * ���̏o�͂ƃX�g���[����
     * �ڑ�����Ă����X�g���[������A
     * �g�p���Ă������\�[�X���������B
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void close() throws IOException {
        this.in.close();

        this.in = null;
    }

    //------------------------------------------------------------------
    //  method of PreLzssDecoder
    //------------------------------------------------------------------
    //  get LZSS parameter
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
        return PreLzsDecoder.DictionarySize;
    }

    /**
     * -lzs-�`���� LZSS�̍Œ���v���𓾂�B
     * 
     * @return -lzs-�`���� LZSS�̍Œ���v��
     */
    public int getMaxMatch(){
        return PreLzsDecoder.MaxMatch;
    }

    /**
     * -lzs-�`���� LZSS�̈��k�A�񈳏k��臒l�𓾂�B
     * 
     * @return -lzs-�`���� LZSS�̈��k�A�񈳏k��臒l
     */
    public int getThreshold(){
        return PreLzsDecoder.Threshold;
    }

}
//end of PreLzsDecoder.java
