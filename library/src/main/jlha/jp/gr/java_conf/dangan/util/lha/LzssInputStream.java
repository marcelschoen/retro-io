//start of LzssInputStream.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * LzssInputStream.java
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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

//import exceptions

/**
 * LZSS ���k���ꂽ�f�[�^���𓀂��Ȃ��狟��������̓X�g���[���B<br>
 *
 * <pre>
 * -- revision history --
 * $Log: LzssInputStream.java,v $
 * Revision 1.1  2002/12/08 00:00:00  dangan
 * [bug fix]
 *     mark() ���Őڑ����ꂽ PreLzssDecoder �� 
 *     mark �ɗ^���� readLimit �̌v�Z���Â������̂��C���B
 *
 * Revision 1.0  2002/07/25 00:00:00  dangan
 * add to version control
 * [bug fix]
 *     available() �̃X�y���~�X���C���B
 *     skip() �ɂ����� decode() ���ĂԔ���������Ԉ���Ă����̂��C���B
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
public class LzssInputStream extends InputStream {


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  source
    //------------------------------------------------------------------
    //  private PreLzssDecoder decoder
    //------------------------------------------------------------------
    /**
     * LZSS���k�R�[�h��Ԃ����̓X�g���[��
     */
    private PreLzssDecoder decoder;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  LZSS parameter
    //------------------------------------------------------------------
    //  private int Threshold
    //  private int MaxMatch
    //  private long Length
    //------------------------------------------------------------------
    /**
     * LZSS���k�Ɏg�p�����臒l�B
     * ��v���� ���̒l�ȏ�ł���΁A���k�R�[�h���o�͂���B
     */
    private int Threshold;

    /**
     * LZSS���k�Ɏg�p�����l�B
     * �ő��v���������B
     */
    private int MaxMatch;

    /**
     * �𓀌�̃f�[�^�T�C�Y
     */
    private long Length;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  text buffer
    //------------------------------------------------------------------
    //  private byte[] TextBuffer
    //  private long TextPosition
    //  private long TextDecoded
    //------------------------------------------------------------------
    /**
     * LZSS���k��W�J���邽�߂̃o�b�t�@�B
     */
    private byte[] TextBuffer;

    /**
     * ���ݓǂݍ��݈ʒu�B
     * read() �ɂ���ĊO���ɓǂݏo���ꂽ�ʒu�������B
     */
    private long TextPosition;

    /**
     * ���ݓǂݍ��݈ʒu�B
     * LZSS���k�R�[�h��W�J���ē���ꂽ�ʒu�������B
     */
    private long TextDecoded;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  backup for mark/reset
    //------------------------------------------------------------------
    //  private byte[] MarkTextBuffer
    //  private long MarkTextPosition
    //  private long MarkTextDecoded
    //------------------------------------------------------------------
    /** TextBuffer �̃o�b�N�A�b�v�p */
    private byte[] MarkTextBuffer;

    /** TextPosition �̃o�b�N�A�b�v�p */
    private long MarkTextPosition;

    /** TextDecoded �̃o�b�N�A�b�v�p */
    private long MarkTextDecoded;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private LzssInputStream()
    //  public LzssInputStream( PreLzssDecoder decoder )
    //  public LzssInputStream( PreLzssDecoder decoder, long length )
    //------------------------------------------------------------------

    /**
     * �f�t�H���g�R���X�g���N�^�B
     * �g�p�s�B
     */
    private LzssInputStream() {
    }

    /**
     * in ���� LZSS���k�f�[�^ �̓��͂��󂯂āA
     * �𓀂��ꂽ�f�[�^��񋟂�����̓X�g���[�����\�z����B
     * ���̃R���X�g���N�^���琶�����ꂽ LzssInputStream��
     * -lh1-���̉𓀃f�[�^�̍Ō�̃f�[�^��ǂݍ��񂾌�A
     * ���̃f�[�^�̓ǂݎ��ŕK��EndOfStream�ɒB����Ƃ�
     * ����Ȃ��f�[�^�𐳏�ɕ����ł��Ȃ�(�I�[�ȍ~�ɃS�~
     * �f�[�^�����\��������)�B
     *
     * @param decoder LZSS���k�f�[�^�����X�g���[��
     */
    public LzssInputStream(PreLzssDecoder decoder) {
        this(decoder, Long.MAX_VALUE);
    }

    /**
     * in ���� LZSS���k�f�[�^ �̓��͂��󂯂āA
     * �𓀂��ꂽ�f�[�^��񋟂�����̓X�g���[�����\�z����B
     *
     *
     * @param decoder LZSS���k�f�[�^�����X�g���[��
     * @param length  �𓀌�̃T�C�Y
     */
    public LzssInputStream(PreLzssDecoder decoder,
                           long length) {
        this.MaxMatch = decoder.getMaxMatch();
        this.Threshold = decoder.getThreshold();
        this.Length = length;

        this.decoder = decoder;
        this.TextBuffer = new byte[decoder.getDictionarySize()];
        this.TextPosition = 0;
        this.TextDecoded = 0;

        if (this.decoder instanceof PreLz5Decoder)
            this.initLz5TextBuffer();
    }


    //------------------------------------------------------------------
    //  method of java.io.InputStream
    //------------------------------------------------------------------
    //  read
    //------------------------------------------------------------------
    //  public int read()
    //  public int read( byte[] buffer )
    //  public int read( byte[] buffer, int index, int length )
    //  public long skip( long length )
    //------------------------------------------------------------------

    /**
     * �R���X�g���N�^�Ŏw�肳�ꂽ PreLzssDecoder ��
     * ���k���ꂽ�f�[�^���𓀂��A1�o�C�g�̃f�[�^����������B
     *
     * @return �𓀂��ꂽ 1�o�C�g�̃f�[�^
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public int read() throws IOException {
        if (this.TextDecoded <= this.TextPosition) {
            try {
                this.decode();                                                  //throws EOFException IOException
            } catch (EOFException exception) {
                if (this.TextDecoded <= this.TextPosition)
                    return -1;
            }
        }

        return this.TextBuffer[(int) this.TextPosition++
                & (this.TextBuffer.length - 1)] & 0xFF;
    }

    /**
     * �R���X�g���N�^�Ŏw�肳�ꂽ PreLzssDecoder ��
     * ���k���ꂽ�f�[�^���𓀂��Abuffer�𖞂����悤��
     * �𓀂��ꂽ�f�[�^��ǂݍ��ށB
     *
     * @param buffer �f�[�^��ǂݍ��ރo�b�t�@
     *
     * @return �ǂ݂��񂾃f�[�^��
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public int read(byte[] buffer) throws IOException {
        return this.read(buffer, 0, buffer.length);
    }

    /**
     * �R���X�g���N�^�Ŏw�肳�ꂽ PreLzssDecoder ��
     * ���k���ꂽ�f�[�^���𓀂��Abuffer �� index ����
     * length �o�C�g�̃f�[�^��ǂݍ��ށB
     *
     * @param buffer �f�[�^��ǂݍ��ރo�b�t�@
     * @param index  buffer ���̃f�[�^�ǂ݂��݊J�n�ʒu
     * @param length �ǂݍ��ރf�[�^��
     *
     * @return �ǂ݂��񂾃f�[�^��
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public int read(byte[] buffer, int index, int length) throws IOException {
        int position = index;
        int end = index + length;
        try {
            while (position < end) {
                if (this.TextDecoded <= this.TextPosition)
                    this.decode();                                              //throws IOException

                position = this.copyTextBufferToBuffer(buffer, position, end);
            }
        } catch (EOFException exception) {
            position = this.copyTextBufferToBuffer(buffer, position, end);

            if (position == index) return -1;
        }

        return position - index;
    }

    /**
     * �𓀂��ꂽ�f�[�^�� length�o�C�g�ǂݔ�΂��B
     *
     * @param length �ǂݔ�΂��f�[�^��(�P�ʂ̓o�C�g)
     *
     * @return ���ۂɓǂݔ�΂����o�C�g��
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public long skip(long length) throws IOException {
        long end = this.TextPosition + length;
        try {
            while (this.TextPosition < end) {
                if (this.TextDecoded <= this.TextPosition)
                    this.decode();

                this.TextPosition = Math.min(end, this.TextDecoded);
            }
        } catch (EOFException exception) {
            this.TextPosition = Math.min(end, this.TextDecoded);
        }

        return length - (end - this.TextPosition);
    }


    //------------------------------------------------------------------
    //  method of java.io.InputStream
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
     * InputStream �� mark() �ƈႢ�A readLimit �Őݒ肵��
     * ���E�o�C�g�����O�Ƀ}�[�N�ʒu�������ɂȂ�\��������B
     * �������AreadLimit �𖳎����Ė����� reset() �\�� 
     * InputStream �Ɛڑ����Ă���ꍇ�� readLimit ��
     * �ǂ̂悤�Ȓl��ݒ肳��Ă�
     * reset() �ŕK���}�[�N�ʒu�ɕ����ł��鎖��ۏ؂���B<br>
     *
     * @param readLimit �}�[�N�ʒu�ɖ߂����E�̃o�C�g���B
     *                  ���̃o�C�g���𒴂��ăf�[�^��ǂ�
     *                  ���񂾏ꍇ reset()�ł��Ȃ��Ȃ��
     *                  �\��������B<br>
     *
     * @see PreLzssDecoder#mark(int)
     */
    public void mark(int readLimit) {
        readLimit -= (int) (this.TextDecoded - this.TextPosition);
        int Size = this.TextBuffer.length - this.MaxMatch;
        readLimit = (readLimit + Size - 1) / Size * Size;
        this.decoder.mark(Math.max(readLimit, 0));

        if (this.MarkTextBuffer == null) {
            this.MarkTextBuffer = (byte[]) this.TextBuffer.clone();
        } else {
            System.arraycopy(this.TextBuffer, 0,
                    this.MarkTextBuffer, 0,
                    this.TextBuffer.length);
        }
        this.MarkTextPosition = this.TextPosition;
        this.MarkTextDecoded = this.TextDecoded;
    }

    /**
     * �ڑ����ꂽ���̓X�g���[���̓ǂݍ��݈ʒu���Ō��
     * mark() ���\�b�h���Ăяo���ꂽ�Ƃ��̈ʒu�ɐݒ肷��B<br>
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void reset() throws IOException {
        if (this.MarkTextBuffer == null) {
            throw new IOException("not marked.");
        } else if (this.TextDecoded - this.MarkTextPosition
                <= this.TextBuffer.length) {
            this.TextPosition = this.MarkTextPosition;
        } else if (this.decoder.markSupported()) {
            //reset
            this.decoder.reset();                                               //throws IOException
            System.arraycopy(this.MarkTextBuffer, 0,
                    this.TextBuffer, 0,
                    this.TextBuffer.length);
            this.TextPosition = this.MarkTextPosition;
            this.TextDecoded = this.MarkTextDecoded;
        } else {
            throw new IOException("mark/reset not supported.");
        }
    }

    /**
     * �ڑ����ꂽ���̓X�g���[���� mark() �� reset() ��
     * �T�|�[�g���邩�𓾂�B<br>
     *
     * @return �X�g���[���� mark() �� reset() ��
     *         �T�|�[�g����ꍇ�� true�B<br>
     *         �T�|�[�g���Ȃ��ꍇ�� false�B<br>
     */
    public boolean markSupported() {
        return this.decoder.markSupported();
    }


    //------------------------------------------------------------------
    //  method of java.io.InputStream
    //------------------------------------------------------------------
    //  other methods
    //------------------------------------------------------------------
    //  public int available()
    //  public void close()
    //------------------------------------------------------------------

    /**
     * �ڑ����ꂽ���̓X�g���[������u���b�N���Ȃ���
     * �ǂݍ��ނ��Ƃ̂ł���o�C�g���𓾂�B<br>
     *
     * @return �u���b�N���Ȃ��œǂݏo����o�C�g���B<br>
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public int available() throws IOException {
        return (int) (this.TextDecoded - this.TextPosition)
                + this.decoder.available();
    }

    /**
     * ���̓��̓X�g���[������A�g�p���Ă���
     * �S�Ẵ��\�[�X���J������B<br>
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void close() throws IOException {
        this.decoder.close();
        this.decoder = null;
        this.TextBuffer = null;
        this.MarkTextBuffer = null;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  private void decode()
    //  private int copyTextBufferToBuffer( byte[] buffer, int position, int end )
    //  private void initLz5TextBuffer()
    //------------------------------------------------------------------

    /**
     * private�ϐ� this.in ���爳�k�f�[�^��ǂݍ���
     * �𓀂��Ȃ��� TextBuffer �Ƀf�[�^���������ށB
     *
     * @exception IOException  ���o�̓G���[�����������ꍇ
     * @exception EOFException �X�g���[���I�[�ɒB�����ꍇ
     */
    private void decode() throws IOException {
        if (this.TextDecoded < this.Length) {
            final int TextMask = this.TextBuffer.length - 1;
            final int TextStart = (int) this.TextDecoded & TextMask;
            int TextPos = TextStart;
            int TextLimit = (int) (Math.min(this.TextPosition
                            + this.TextBuffer.length
                            - this.MaxMatch,
                    this.Length)
                    - this.TextDecoded) + TextStart;
            try {
                while (TextPos < TextLimit) {
                    int Code = this.decoder.readCode();                             //throws EOFException IOException

                    if (Code < 0x100) {
                        this.TextBuffer[TextMask & TextPos++] = (byte) Code;
                    } else {
                        int MatchLength = (Code & 0xFF) + this.Threshold;
                        int MatchPosition = TextPos - this.decoder.readOffset() - 1;//throws IOException

                        while (0 < MatchLength--)
                            this.TextBuffer[TextMask & TextPos++]
                                    = this.TextBuffer[TextMask & MatchPosition++];
                    }
                }
            } finally {
                this.TextDecoded += TextPos - TextStart;
            }
        } else {
            throw new EOFException();
        }
    }

    /**
     * private �ϐ� this.TextBuffer ���� buffer�Ƀf�[�^��]������B
     *
     * @param buffer   TextBuffer�̓��e���R�s�[����o�b�t�@
     * @param position buffer���̏������݌��݈ʒu
     * @param end      buffer���̏������ݏI���ʒu
     *
     * @return buffer�̎��ɏ������݂��s����ׂ��ʒu
     */
    private int copyTextBufferToBuffer(byte[] buffer, int position, int end) {
        if ((this.TextPosition & ~(this.TextBuffer.length - 1))
                < (this.TextDecoded & ~(this.TextBuffer.length - 1))) {
            int length = Math.min(this.TextBuffer.length -
                            ((int) this.TextPosition
                                    & this.TextBuffer.length - 1),
                    end - position);

            System.arraycopy(this.TextBuffer,
                    (int) this.TextPosition
                            & this.TextBuffer.length - 1,
                    buffer, position, length);

            this.TextPosition += length;
            position += length;
        }

        if (this.TextPosition < this.TextDecoded) {
            int length = Math.min((int) (this.TextDecoded
                            - this.TextPosition),
                    end - position);

            System.arraycopy(this.TextBuffer,
                    (int) this.TextPosition
                            & this.TextBuffer.length - 1,
                    buffer, position, length);

            this.TextPosition += length;
            position += length;
        }

        return position;
    }

    /**
     * -lz5- �p�� TextBuffer ������������B
     */
    private void initLz5TextBuffer() {
        int position = 18;
        for (int i = 0; i < 256; i++)
            for (int j = 0; j < 13; j++)
                this.TextBuffer[position++] = (byte) i;

        for (int i = 0; i < 256; i++)
            this.TextBuffer[position++] = (byte) i;

        for (int i = 0; i < 256; i++)
            this.TextBuffer[position++] = (byte) (255 - i);

        for (int i = 0; i < 128; i++)
            this.TextBuffer[position++] = 0;

        while (position < this.TextBuffer.length)
            this.TextBuffer[position++] = (byte) ' ';
    }

}
//end of LzssInputStream.java
