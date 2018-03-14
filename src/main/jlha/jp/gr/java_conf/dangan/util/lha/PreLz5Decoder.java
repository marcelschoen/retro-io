//start of PreLz5Decoder.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * PreLz5Decoder.java
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

import jp.gr.java_conf.dangan.io.CachedInputStream;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

//import exceptions

/**
 * -lz5- �𓀗p PreLzssDecoder�B<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: PreLz5Decoder.java,v $
 * Revision 1.1  2002/12/01 00:00:00  dangan
 * [maintenance]
 *     �\�[�X����
 *
 * Revision 1.0  2002/08/05 00:00:00  dangan
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
public class PreLz5Decoder implements PreLzssDecoder{


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
    private static final int MaxMatch       = 18;

    /** �ŏ���v�� */
    private static final int Threshold      = 3;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  source
    //------------------------------------------------------------------
    //  private InputStream in
    //------------------------------------------------------------------
    /**
     * -lz5- �`���̈��k�f�[�^����������X�g���[��
     */
    private InputStream in;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  current position
    //------------------------------------------------------------------
    //  private int position
    //  private int matchPos
    //  private int matchLen
    //------------------------------------------------------------------
    /** 
     * ���ݏ����ʒu�B
     * larc �̈�v�ʒu���� lha �̈�v�ʒu�ւ̕ϊ��ɕK�v
     */
    private int position;

    /** Lzss���k���̂��� ��v�ʒu(larc�̈�v�ʒu) */
    private int matchPos;

    /** Lzss���k�����̂��� ��v�� */
    private int matchLen;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  flag
    //------------------------------------------------------------------
    //  private int flagByte
    //  private int flagBit
    //------------------------------------------------------------------
    /** 8��Lzss���k�A�񈳏k�������t���O���܂Ƃ߂����� */
    private int flagByte;

    /** Lzss���k�A�񈳏k�������t���O */
    private int flagBit;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  backup for mark/reset
    //------------------------------------------------------------------
    //  private int markPosition
    //  private int markMatchPos
    //  private int markMatchLen
    //  private int markFlagByte
    //  private int markFlagBit
    //------------------------------------------------------------------
    /** position�̃o�b�N�A�b�v�p */
    private int markPosition;

    /** matchOffset�̃o�b�N�A�b�v�p */
    private int markMatchPos;

    /** matchLength�̃o�b�N�A�b�v�p */
    private int markMatchLen;

    /** flagByte�̃o�b�N�A�b�v�p�B*/
    private int markFlagByte;

    /** flagCount�̃o�b�N�A�b�v�p�B */
    private int markFlagBit;


    //------------------------------------------------------------------
    //  constructer
    //------------------------------------------------------------------
    //  private PreLz5Decoder()
    //  public PreLz5Decoder( InputStream in )
    //------------------------------------------------------------------
    /**
     * �f�t�H���g�R���X�g���N�^�B
     * �g�p�s��
     */
    private PreLz5Decoder(){ }

    /**
     * -lz5- �𓀗p PreLzssDecoder ���\�z����B
     * 
     * @param in ���k�f�[�^������������̓X�g���[��
     */
    public PreLz5Decoder( InputStream in ){
        if( in != null ){
            if( in instanceof CachedInputStream ){
                this.in = (CachedInputStream)in;
            }else{
                this.in = new CachedInputStream( in );
            }

            this.position     = 0;
            this.matchPos     = 0;
            this.matchLen     = 0;
            this.flagByte     = 0;
            this.flagBit      = 0x100;

            this.markPosition = 0;
            this.markMatchPos = 0;
            this.markMatchLen = 0;
            this.markFlagByte = 0;
            this.markFlagBit  = 0;
        }else{
            throw new NullPointerException( "in" );
        }
    }

    //------------------------------------------------------------------
    //  method of jp.gr.java_conf.dangan.util.PreLzssDecoder
    //------------------------------------------------------------------
    //  read
    //------------------------------------------------------------------
    //  public int readCode()
    //  public int readOffset()
    //------------------------------------------------------------------
    /**
     * -lz5- �ň��k���ꂽ 
     * 1byte �� LZSS�����k�̃f�[�^�A
     * �������͈��k�R�[�h�̂�����v����ǂݍ��ށB<br>
     * 
     * @return 1byte �� �����k�̃f�[�^�������́A
     *         ���k���ꂽ���k�R�[�h�̂�����v��
     * 
     * @exception IOException  ���o�̓G���[�����������ꍇ
     * @exception EOFException EndOfStream�ɒB�����ꍇ
     */
    public int readCode() throws IOException {
        if( this.flagBit == 0x100 ){
            this.flagByte  = this.in.read();                                    //throws IOException

            if( 0 <= this.flagByte ){
                this.flagBit = 0x01;
            }else{
                throw new EOFException();
            }
        }

        if( 0 != ( this.flagByte & this.flagBit ) ){
            this.flagBit <<= 1;
            this.position++;
            int ret = this.in.read();                                           //throws IOException
            if( 0 <= ret ) return ret;
            else           throw new EOFException();
        }else{
            this.flagBit <<= 1;
            int c1   = this.in.read();                                          //throws IOException
            int c2   = this.in.read();                                          //throws IOException

            if( 0 <= c1 ){
                this.matchPos = ( ( c2 & 0xF0 ) << 4 ) | c1;
                this.matchLen = c2 & 0x0F;
                return this.matchLen | 0x100;
            }else{
                throw new EOFException();
            }
        }
    }

    /**
     * -lz5- �ň��k���ꂽ
     * ���k�R�[�h�̂�����v�ʒu��ǂݍ��ށB<br>
     * 
     * @return -lz5- �ň��k���ꂽ���k�R�[�h�̂�����v�ʒu
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public int readOffset() throws IOException {
        int offset  = ( this.position - this.matchPos - 1
                      - PreLz5Decoder.MaxMatch )
                    & ( PreLz5Decoder.DictionarySize - 1 );

        this.position += this.matchLen + PreLz5Decoder.Threshold;

        return offset;
    }


    //------------------------------------------------------------------
    //  method of jp.gr.java_conf.dangan.util.PreLzssDecoder
    //------------------------------------------------------------------
    //  mark/reset
    //------------------------------------------------------------------
    //  public void mark( int readLimit )
    //  public void reset()
    //  public boolean markSupported()
    //------------------------------------------------------------------
    /**
     * �ڑ����ꂽ���̓X�g���[���̌��݈ʒu�Ƀ}�[�N��ݒ肵�A
     * reset() ���\�b�h�Ń}�[�N�������_�� �ǂݍ��݈ʒu��
     * �߂��悤�ɂ���B<br>
     * 
     * @param readLimit �}�[�N�ʒu�ɖ߂����E�̃o�C�g���B
     *                  ���̃o�C�g���𒴂��ăf�[�^��ǂ�
     *                  ���񂾏ꍇ reset()�ł��Ȃ��Ȃ��
     *                  �\��������B<br>
     *
     * @see PreLzssDecoder#mark(int)
     */
    public void mark( int readLimit ){
        this.in.mark( ( readLimit * 9 + 7 ) / 8 + 2 );
        this.markPosition = this.position;
        this.markMatchLen = this.matchLen;
        this.markMatchPos = this.matchPos;
        this.markFlagByte = this.flagByte;
        this.markFlagBit  = this.flagBit;
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
        //mark() ���Ă��Ȃ��̂� reset() ���悤�Ƃ����ꍇ�A
        //�ڑ����ꂽ�X�g���[����mark/reset���T�|�[�g���Ȃ��ꍇ��
        //CachedInputStream �� IOException �𓊂���B
        this.in.reset();                                                        //throws IOException

        this.position = this.markPosition;
        this.matchLen = this.markMatchLen;
        this.matchPos = this.markMatchPos;
        this.flagByte = this.markFlagByte;
        this.flagBit  = this.markFlagBit;
    }

    /**
     * �ڑ����ꂽ�X�g���[���� mark() �� reset()
     * ���T�|�[�g���邩��Ԃ��B
     * 
     * @return �ڑ����ꂽ�X�g���[���� mark,reset ���T�|�[�g����Ȃ�true,
     *         �T�|�[�g���Ȃ��Ȃ� false
     */
    public boolean markSupported(){
        return this.in.markSupported();
    }


    //------------------------------------------------------------------
    //  method of jp.gr.java_conf.dangan.util.PreLzssDecoder
    //------------------------------------------------------------------
    //  other
    //------------------------------------------------------------------
    //  public int available()
    //  public void close()
    //------------------------------------------------------------------
    /**
     * �u���b�N�����ɓǂݏo�����Ƃ̏o����Œ�o�C�g���𓾂�B<br>
     * ���̒l�͕ۏ؂����B
     * 
     * @return �u���b�N���Ȃ��œǂݏo����Œ�o�C�g���B<br>
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     * 
     * @see PreLzssDecoder#available()
     */
    public int available() throws IOException {
        return Math.max( in.available() * 8 / 9 - 2, 0 );                       //throws IOException
    }

    /**
     * ���̃X�g���[������A�g�p���Ă����S�Ă̎������������B
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void close() throws IOException {
        this.in.close();                                                        //throws IOException

        this.in = null;
    }


    //------------------------------------------------------------------
    //  method of jp.gr.java_conf.dangan.util.PreLzssDecoder
    //------------------------------------------------------------------
    //  get LZSS parameter
    //------------------------------------------------------------------
    //  public int getDictionarySize()
    //  public int getMaxMatch()
    //  public int getThreshold()
    //------------------------------------------------------------------
    /**
     * -lz5-�`���� LZSS�����̃T�C�Y�𓾂�B
     * 
     * @return -lz5-�`���� LZSS�����̃T�C�Y
     */
    public int getDictionarySize(){
        return PreLz5Decoder.DictionarySize;
    }

    /**
     * -lz5-�`���� LZSS�̍ő��v���𓾂�B
     * 
     * @return -lz5-�`���� LZSS�̍ő��v��
     */
    public int getMaxMatch(){
        return PreLz5Decoder.MaxMatch;
    }

    /**
     * -lz5-�`���� LZSS�̈��k�A�񈳏k��臒l�𓾂�B
     * 
     * @return -lz5-�`���� LZSS�̈��k�A�񈳏k��臒l
     */
    public int getThreshold(){
        return PreLz5Decoder.Threshold;
    }

}
//end of PreLz5Decoder.java
