//start of PostLz5Encoder.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * PostLz5Encoder.java
 * <p>
 * Copyright (C) 2001-2002  Michel Ishizuka  All rights reserved.
 * <p>
 * �ȉ��̏����ɓ��ӂ���Ȃ�΃\�[�X�ƃo�C�i���`���̍Ĕz�z�Ǝg�p��
 * �ύX�̗L���ɂ�����炸������B
 * <p>
 * �P�D�\�[�X�R�[�h�̍Ĕz�z�ɂ����Ē��쌠�\���� ���̏����̃��X�g
 * ����щ��L�̐�������ێ����Ȃ��Ă͂Ȃ�Ȃ��B
 * <p>
 * �Q�D�o�C�i���`���̍Ĕz�z�ɂ����Ē��쌠�\���� ���̏����̃��X�g
 * ����щ��L�̐��������g�p�������������� ���̑��̔z�z������
 * �܂ގ����ɋL�q���Ȃ���΂Ȃ�Ȃ��B
 * <p>
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

import java.io.IOException;
import java.io.OutputStream;

//import exceptions

/**
 * -lz5- ���k�p PostLzssEncoder�B
 *
 * <pre>
 * -- revision history --
 * $Log: PostLz5Encoder.java,v $
 * Revision 1.1  2002/12/01 00:00:00  dangan
 * [maintenance]
 *     �\�[�X����
 *
 * Revision 1.0  2002/07/31 00:00:00  dangan
 * add to version control
 * [bug fix]
 *     -lz5- �� MaxMatch �� 16 �łȂ� 18 �������B
 *     flush() �ŏo�͂ł���f�[�^���o�͂��Ă��Ȃ������̂��C���B
 * [maintenance]
 *     �\�[�X����
 *     �^�u�p�~
 *     ���C�Z���X���̏C��
 *
 * </pre>
 *
 * @author $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class PostLz5Encoder implements PostLzssEncoder {


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
    private static final int MaxMatch = 18;

    /** �ŏ���v�� */
    private static final int Threshold = 3;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  sink
    //------------------------------------------------------------------
    //  private OutputStream out
    //------------------------------------------------------------------
    /**
     * -lz5- ���k�f�[�^���o�͂���X�g���[��
     */
    private OutputStream out;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  buffer
    //------------------------------------------------------------------
    //  private byte[] buf
    //  private int index
    //  private int flagIndex
    //  private int flagBit
    //------------------------------------------------------------------
    /** ���k�f�[�^�̈ꎞ�i�[�p�o�b�t�@ */
    private byte[] buf;

    /** buf���̌��ݏ����ʒu */
    private int index;

    /** buf���� Lzss���k�A�񈳏k�������t���O�̈ʒu������ */
    private int flagIndex;

    /** Lzss���k�A�񈳏k�������t���O */
    private int flagBit;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  current position
    //------------------------------------------------------------------
    //  private int position
    //------------------------------------------------------------------
    /**
     * �X�g���[�������ݏ����ʒu 
     * lha �� offset ���� larc �� offset �ւ̕ϊ��ɕK�v
     */
    private int position;


    //------------------------------------------------------------------
    //  constructer
    //------------------------------------------------------------------
    //  private PostLz5Encoder()
    //  public PostLz5Encoder( OutputStream out )
    //------------------------------------------------------------------

    /**
     * �f�t�H���g�R���X�g���N�^�B
     * �g�p�s��
     */
    private PostLz5Encoder() {
    }

    /**
     * -lz5- ���k�p PostLzssEncoder ���\�z����B<br>
     *
     * @param out ���k�f�[�^���o�͂���o�̓X�g���[��
     */
    public PostLz5Encoder(OutputStream out) {
        if (out != null) {
            this.out = out;
            this.position = 0;
            this.buf = new byte[1024];
            this.index = 0;
            this.flagIndex = 0;
            this.flagBit = 0x100;
        } else {
            throw new NullPointerException("out");
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
    public void writeCode(int code) throws IOException {
        if (this.flagBit == 0x100) {
            if (this.buf.length - (2 * 8 + 1) < this.index) {
                this.out.write(this.buf, 0, this.index);                      //throws IOException
                this.index = 0;
            }
            this.flagBit = 0x01;
            this.flagIndex = this.index++;
            this.buf[this.flagIndex] = 0;
        }

        if (code < 0x100) {
            this.buf[this.flagIndex] |= this.flagBit;
            this.buf[this.index++] = (byte) code;
            this.position++;
        } else {
            this.buf[this.index++] = (byte) code;
        }
        this.flagBit <<= 1;
    }

    /**
     * LZSS �ň��k���ꂽ���k�R�[�h�̂�����v�ʒu���������ށB<br>
     *
     * @param offset LZSS �ň��k���ꂽ���k�R�[�h�̂�����v�ʒu
     */
    public void writeOffset(int offset) {
        int pos = (this.position - offset - 1
                - PostLz5Encoder.MaxMatch)
                & (PostLz5Encoder.DictionarySize - 1);

        int matchlen = this.buf[--this.index] & 0x0F;
        this.buf[this.index++] = (byte) pos;
        this.buf[this.index++] = (byte) (((pos >> 4) & 0xF0) | matchlen);

        this.position += matchlen + this.Threshold;

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
     * �o�͉\�ȃf�[�^���o�͐�� OutputStream �ɏo�͂��A
     * �o�͐�� OutputStream �� flush() ����B<br>
     * ���̃��\�b�h�͏o�͕s�\�� �ő�15�o�C�g�̃f�[�^��
     * �o�b�t�@�����O�����܂� �o�͂��Ȃ��B<br>
     * ���̃��\�b�h�͈��k����ω������Ȃ��B<br>
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     *
     * @see PostLzssEncoder#flush()
     */
    public void flush() throws IOException {
        if (this.flagBit == 0x100) {
            this.out.write(this.buf, 0, this.index);                          //throws IOException
            this.out.flush();                                                   //throws IOException

            this.index = 0;
            this.flagBit = 0x01;
            this.flagIndex = this.index++;
            this.buf[this.flagIndex] = 0;
        } else {
            this.out.write(this.buf, 0, this.flagIndex);                      //throws IOException
            this.out.flush();                                                   //throws IOException

            System.arraycopy(this.buf, this.flagIndex,
                    this.buf, 0,
                    this.index - this.flagIndex);
            this.index -= this.flagIndex;
            this.flagIndex = 0;
        }
    }

    /**
     * ���̏o�̓X�g���[���ƁA�ڑ����ꂽ�o�̓X�g���[������A
     * �g�p���Ă������\�[�X���J������B<br>
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void close() throws IOException {
        this.out.write(this.buf, 0, this.index);                              //throws IOException
        this.out.close();                                                       //throws IOException

        this.out = null;
        this.buf = null;
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
     * -lz5-�`���� LZSS�����̃T�C�Y�𓾂�B
     *
     * @return -lz5-�`���� LZSS�����̃T�C�Y
     */
    public int getDictionarySize() {
        return PostLz5Encoder.DictionarySize;
    }

    /**
     * -lz5-�`���� LZSS�̍Œ���v���𓾂�B
     *
     * @return -lz5-�`���� LZSS�̍Œ���v��
     */
    public int getMaxMatch() {
        return PostLz5Encoder.MaxMatch;
    }

    /**
     * -lz5-�`���� LZSS�̈��k�A�񈳏k��臒l�𓾂�B
     *
     * @return -lz5-�`���� LZSS�̈��k�A�񈳏k��臒l
     */
    public int getThreshold() {
        return PostLz5Encoder.Threshold;
    }

}
//end of PostLz5Encoder.java
