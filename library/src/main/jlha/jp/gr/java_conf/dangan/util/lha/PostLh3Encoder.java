//start of PostLh3Encoder.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * PostLh3Encoder.java
 * <p>
 * Copyright (C) 2002  Michel Ishizuka  All rights reserved.
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

import jp.gr.java_conf.dangan.io.BitOutputStream;

import java.io.IOException;
import java.io.OutputStream;

//import exceptions


/**
 * -lh3- ���k�p PostLzssEncoder�B<br>
 *
 * <pre>
 * $Log: PostLh3Encoder.java,v $
 * Revision 1.2  2002/12/06 00:00:00  dangan
 * [maintenance]
 *     �\�[�X����
 *
 * Revision 1.1  2002/12/01 00:00:00  dangan
 * [change]
 *     flush() ����Ȃ������� 
 *     �ڑ����ꂽ OutputStream ��flush() ���Ȃ��悤�ɕύX�B
 * [maintenance]
 *     �\�[�X�����B
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
 * @author $Author: dangan $
 * @version $Revision: 1.2 $
 */
public class PostLh3Encoder implements PostLzssEncoder {

    //------------------------------------------------------------------
    //  class fields
    //------------------------------------------------------------------
    //  LZSS parameter
    //------------------------------------------------------------------
    //  private static final int DictionarySize
    //  private static final int MaxMatch
    //  private static final int Threshold
    //------------------------------------------------------------------
    /** �����T�C�Y */
    private static final int DictionarySize = 8192;

    /** �ő��v�� */
    private static final int MaxMatch = 256;

    /** �ŏ���v�� */
    private static final int Threshold = 3;


    //------------------------------------------------------------------
    //  class fields
    //------------------------------------------------------------------
    //  private static final int[] ConstOffHiLen
    //  private static final int CodeSize
    //------------------------------------------------------------------
    /**
     * OffHi�����̌Œ�n�t�}��������
     */
    private static final int[] ConstOffHiLen = PostLh3Encoder.createConstOffHiLen();

    /**
     * code���̃n�t�}���؂̃T�C�Y 
     * code��������ȏ�̒l�������ꍇ�͗]�v�ȃr�b�g���o�͂��ĕ₤�B
     */
    private static final int CodeSize = 286;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  sink
    //------------------------------------------------------------------
    //  private BitOutputStream out
    //------------------------------------------------------------------
    /**
     * -lh3- �`���̈��k�f�[�^�̏o�͐�� �r�b�g�o�̓X�g���[��
     */
    private BitOutputStream out;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  buffer of LZSS codes
    //------------------------------------------------------------------
    //  private byte[] buffer
    //  private int blockSize
    //  private int position
    //  private int flagBit
    //  private int flagPos
    //------------------------------------------------------------------
    /**
     * �ÓI�n�t�}�����k���邽�߂Ƀf�[�^���ꎞ�I�ɒ�����o�b�t�@
     */
    private byte[] buffer;

    /**
     * �o�b�t�@���ɂ��� code �f�[�^�̐��B
     */
    private int blockSize;

    /**
     * buffer���̌��ݏ����ʒu
     */
    private int position;

    /**
     * flag �o�C�g���̌��ݏ���bit
     */
    private int flagBit;

    /**
     * buffer���̌��݂�flag�o�C�g�̈ʒu
     */
    private int flagPos;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  frequancy counter for huffman
    //------------------------------------------------------------------
    //  private int[] codeFreq
    //  private int[] offHiFreq
    //------------------------------------------------------------------
    /**
     * code���̕p�x�\
     */
    private int[] codeFreq;

    /**
     * offHi���̕p�x�\
     */
    private int[] offHiFreq;


    //------------------------------------------------------------------
    //  constructers
    //------------------------------------------------------------------
    //  private PostLh3Encoder()
    //  public PostLh3Encoder( OutputStream out )
    //  public PostLh3Encoder( OutputStream out, int BufferSize )
    //------------------------------------------------------------------

    /**
     * �f�t�H���g�R���X�g���N�^�B
     * �g�p�s�B
     */
    private PostLh3Encoder() {
    }

    /**
     * -lh3- ���k�p PostLzssEncoder���\�z����B<br>
     * �o�b�t�@�T�C�Y�ɂ̓f�t�H���g�l���g�p�����B
     *
     * @param out ���k�f�[�^���󂯎��o�̓X�g���[��
     */
    public PostLh3Encoder(OutputStream out) {
        this(out, 16384);
    }

    /**
     * -lh3- ���k�p PostLzssEncoder���\�z����B<br>
     *
     * @param out        ���k�f�[�^���󂯎��o�̓X�g���[��
     * @param BufferSize �ÓI�n�t�}�����k�p�̃o�b�t�@�T�C�Y
     *
     * @exception IllegalArgumentException
     *                   BufferSize ������������ꍇ
     */
    public PostLh3Encoder(OutputStream out, int BufferSize) {
        final int DictionarySizeByteLen = 2;
        final int MinCapacity = (DictionarySizeByteLen + 1) * 8 + 1;

        if (out != null
                && MinCapacity <= BufferSize) {

            if (out instanceof BitOutputStream) {
                this.out = (BitOutputStream) out;
            } else {
                this.out = new BitOutputStream(out);
            }
            this.codeFreq = new int[PostLh3Encoder.CodeSize];
            this.offHiFreq = new int[PostLh3Encoder.DictionarySize >> 6];
            this.buffer = new byte[BufferSize];
            this.blockSize = 0;
            this.position = 0;
            this.flagBit = 0;
            this.flagPos = 0;
        } else if (out == null) {
            throw new NullPointerException("out");
        } else {
            throw new IllegalArgumentException("BufferSize too small. BufferSize must be larger than " + MinCapacity);
        }
    }


    //------------------------------------------------------------------
    //  method of PostLzssEncoder
    //------------------------------------------------------------------
    //  write methods
    //------------------------------------------------------------------
    //  public void writeCode( int code )
    //  public void writeOffset( int offset )
    //------------------------------------------------------------------

    /**
     * �z����� 0�łȂ��v�f���𓾂�B
     *
     * @param array �z��
     *
     * @return �z����� 0�łȂ��v�f��
     */
    private static int countNoZeroElement(int[] array) {
        int count = 0;
        for (int i = 0; i < array.length; i++) {
            if (0 != array[i]) {
                count++;
            }
        }
        return count;
    }

    /**
     * �z����� 0�łȂ��ŏ��̗v�f�𓾂�B
     *
     * @param array �z��
     *
     * @return �z����� 0�łȂ��ŏ��̗v�f
     *         �S�Ă̗v�f��0�̏ꍇ�� 0��Ԃ��B
     */
    private static int getNoZeroElementIndex(int[] array) {
        for (int i = 0; i < array.length; i++) {
            if (0 != array[i]) {
                return i;
            }
        }
        return 0;
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
     * -lh3- �� offHi���f�R�[�h�p �n�t�}�����������X�g�𐶐�����B
     *
     * @return -lh3- �� offHi���f�R�[�h�p �n�t�}�����������X�g
     */
    private static int[] createConstOffHiLen() {
        final int length = PostLh3Encoder.DictionarySize >> 6;
        final int[] list = {2, 0x01, 0x01, 0x03, 0x06, 0x0D, 0x1F, 0x4E, 0};

        int[] offHiLen = new int[length];
        int index = 0;
        int len = list[index++];

        for (int i = 0; i < length; i++) {
            while (list[index] == i) {
                len++;
                index++;
            }
            offHiLen[i] = len;
        }
        return offHiLen;
    }

    /**
     * OffHiFreq���琶�����ꂽ �n�t�}���������̃��X�g��
     * �Œ�n�t�}���������̃��X�g���r���āA�o�̓r�b�g
     * ���̏��Ȃ����̂𓾂�B
     *
     * @param OffHiFreq offset���̏��6bit�̏o���p�x�̕\
     * @param OffHiLen  OffHiFreq���琶�����ꂽ�n�t�}����
     *                  �����̃��X�g
     *
     * @return �o�̓r�b�g���̏��Ȃ����̃n�t�}���������̃��X�g
     */
    private static int[] getBetterOffHiLen(int[] OffHiFreq,
                                           int[] OffHiLen) {
        boolean detect = false;
        for (int i = 0; i < OffHiLen.length; i++) {
            if (15 < OffHiLen[i]) { //15 ��writeOffHiLenList�ŏ������߂�ő�̃n�t�}�����������Ӗ�����B
                detect = true;
            }
        }

        if (!detect) {
            int origTotal = 1;
            int consTotal = 1;

            if (2 <= PostLh3Encoder.countNoZeroElement(OffHiFreq)) {
                origTotal += 4 * (PostLh3Encoder.DictionarySize >> 6);
            } else {
                origTotal += 4 * 3 + 7;
            }
            for (int i = 0; i < OffHiFreq.length; i++) {
                origTotal += OffHiFreq[i] * OffHiLen[i];
                consTotal += OffHiFreq[i] * PostLh3Encoder.ConstOffHiLen[i];
            }

            if (origTotal < consTotal) return OffHiLen;
            else return PostLh3Encoder.ConstOffHiLen;
        } else {
            return PostLh3Encoder.ConstOffHiLen;
        }
    }


    //------------------------------------------------------------------
    //  PostLzssEncoder methods
    //------------------------------------------------------------------
    //  get LZSS parameter
    //------------------------------------------------------------------
    //  public int getDictionarySize()
    //  public int getMaxMatch()
    //  public int getThreshold()
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
        final int CodeMax = PostLh3Encoder.CodeSize - 1;
        final int DictionarySizeByteLen = 2;
        final int Capacity = (DictionarySizeByteLen + 1) * 8 + 1;

        if (this.flagBit == 0) {
            if (this.buffer.length - this.position < Capacity
                    || (65536 - 8) <= this.blockSize) {
                this.writeOut();                                                //throws IOException
            }
            this.flagBit = 0x80;
            this.flagPos = this.position++;
            this.buffer[this.flagPos] = 0;
        }

        //�f�[�^�i�[
        this.buffer[this.position++] = (byte) code;

        //���1�r�b�g���t���O�Ƃ��Ċi�[
        if (0x100 <= code) this.buffer[this.flagPos] |= this.flagBit;
        this.flagBit >>= 1;

        //�p�x�\�X�V
        this.codeFreq[Math.min(code, CodeMax)]++;

        //�u���b�N�T�C�Y�X�V
        this.blockSize++;
    }

    /**
     * LZSS �ň��k���ꂽ���k�R�[�h�̂�����v�ʒu���������ށB<br>
     *
     * @param offset LZSS �ň��k���ꂽ���k�R�[�h�̂�����v�ʒu
     */
    public void writeOffset(int offset) {
        //�f�[�^�i�[
        this.buffer[this.position++] = (byte) (offset >> 8);
        this.buffer[this.position++] = (byte) offset;

        //�p�x�\�X�V
        this.offHiFreq[(offset >> 6)]++;
    }

    /**
     * ���� PostLzssEncoder �Ƀo�b�t�@�����O����Ă���S�Ă�
     * 8�r�b�g�P�ʂ̃f�[�^���o�͐�� OutputStream �ɏo�͂��A
     * �o�͐�� OutputStream �� flush() ����B<br>
     * ���̃��\�b�h�͈��k����ω�������B
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     *
     * @see PostLzssEncoder#flush()
     * @see BitOutputStream#flush()
     */
    public void flush() throws IOException {
        this.writeOut();
        this.out.flush();
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  write huffman code
    //------------------------------------------------------------------
    //  private void writeOut()
    //------------------------------------------------------------------

    /**
     * ���̏o�̓X�g���[���ƁA�ڑ����ꂽ�o�̓X�g���[������A
     * �g�p���Ă������\�[�X���J������B<br>
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void close() throws IOException {
        this.writeOut();
        this.out.close();                                                       //throws IOException

        this.out = null;
        this.buffer = null;
        this.codeFreq = null;
        this.offHiFreq = null;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  write huffman list
    //------------------------------------------------------------------
    //  private void writeCodeLenList( int[] codeLen )
    //  private void writeOffHiLenList( int[] offHiLen )
    //------------------------------------------------------------------

    /**
     * -lh3-�`���� LZSS�����̃T�C�Y�𓾂�B
     *
     * @return -lh3-�`���� LZSS�����̃T�C�Y
     */
    public int getDictionarySize() {
        return PostLh3Encoder.DictionarySize;
    }

    /**
     * -lh3-�`���� LZSS�̍ő��v���𓾂�B
     *
     * @return -lh3-�`���� LZSS�̍ő��v��
     */
    public int getMaxMatch() {
        return PostLh3Encoder.MaxMatch;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  staff of huffman encoder
    //------------------------------------------------------------------
    //  private static int countNoZeroElement( int[] array )
    //  private static int getNoZeroElementIndex( int[] array )
    //------------------------------------------------------------------

    /**
     * -lh3-�`���� LZSS�̈��k�A�񈳏k��臒l�𓾂�B
     *
     * @return -lh3-�`���� LZSS�̈��k�A�񈳏k��臒l
     */
    public int getThreshold() {
        return PostLh3Encoder.Threshold;
    }

    /**
     * �o�b�t�@�����O���ꂽ�S�Ẵf�[�^�� this.out �ɏo�͂���B<br>
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    private void writeOut() throws IOException {
        final int CodeMax = PostLh3Encoder.CodeSize - 1;

        if (0 < this.blockSize) {
            //------------------------------------------------------------------
            //  �u���b�N�T�C�Y�o��
            this.out.writeBits(16, this.blockSize);                           //throws IOException

            //------------------------------------------------------------------
            //  �n�t�}�������\����
            int[] codeLen = StaticHuffman.FreqListToLenList(this.codeFreq);
            int[] codeCode = StaticHuffman.LenListToCodeList(codeLen);
            int[] offHiLen = PostLh3Encoder.getBetterOffHiLen(this.offHiFreq,
                    StaticHuffman.FreqListToLenList(this.offHiFreq));
            int[] offHiCode = StaticHuffman.LenListToCodeList(offHiLen);

            //------------------------------------------------------------------
            //  code���̃n�t�}�������\�o��
            if (2 <= PostLh3Encoder.countNoZeroElement(this.codeFreq)) {
                this.writeCodeLenList(codeLen);                               //throws IOException
            } else {
                this.out.writeBits(15, 0x4210);                               //throws IOException
                this.out.writeBits(9,
                        PostLh3Encoder.getNoZeroElementIndex(this.codeFreq)); //throws IOException
            }

            //------------------------------------------------------------------
            //  offHi���̃n�t�}�������\�o��
            if (offHiLen != PostLh3Encoder.ConstOffHiLen) {
                this.out.writeBit(1);                                         //throws IOException

                if (2 <= PostLh3Encoder.countNoZeroElement(this.offHiFreq)) {
                    this.writeOffHiLenList(offHiLen);                         //throws IOException
                } else {
                    this.out.writeBits(12, 0x0111);                           //throws IOException
                    this.out.writeBits(7,
                            PostLh3Encoder.getNoZeroElementIndex(this.offHiFreq));//throws IOException
                }
            } else {
                this.out.writeBit(0);                                         //throws IOException
            }

            //------------------------------------------------------------------
            //  �n�t�}�������o��
            this.position = 0;
            this.flagBit = 0;
            for (int i = 0; i < blockSize; i++) {
                if (this.flagBit == 0) {
                    this.flagBit = 0x80;
                    this.flagPos = this.position++;
                }

                if (0 == (this.buffer[this.flagPos] & this.flagBit)) {
                    int code = this.buffer[this.position++] & 0xFF;
                    this.out.writeBits(codeLen[code], codeCode[code]);    //throws IOException
                } else {
                    int code = (this.buffer[this.position++] & 0xFF) | 0x100;
                    int offset = ((this.buffer[this.position++] & 0xFF) << 8)
                            | (this.buffer[this.position++] & 0xFF);
                    int offHi = offset >> 6;
                    if (code < CodeMax) {
                        this.out.writeBits(codeLen[code], codeCode[code]);//throws IOException
                    } else {
                        this.out.writeBits(codeLen[CodeMax], codeCode[CodeMax]);//throws IOException
                        this.out.writeBits(8, code - CodeMax);                //throws IOException
                    }
                    this.out.writeBits(offHiLen[offHi], offHiCode[offHi]);//throws IOException
                    this.out.writeBits(6, offset);                            //throws IOException
                }
                this.flagBit >>= 1;
            }

            //------------------------------------------------------------------
            //  ���̃u���b�N�̂��߂̏���
            for (int i = 0; i < this.codeFreq.length; i++) {
                this.codeFreq[i] = 0;
            }

            for (int i = 0; i < this.offHiFreq.length; i++) {
                this.offHiFreq[i] = 0;
            }

            this.blockSize = 0;
            this.position = 0;
            this.flagBit = 0;

        }// if( 0 < this.blockSize )
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  calc the length of encoded data
    //------------------------------------------------------------------
    //  private static int[] createLenList()
    //  private static int[] getBetterOffHiLenList( int[] OffHiFreq, 
    //                                              int[] OffHiLen )
    //------------------------------------------------------------------

    /**
     * code���̃n�t�}���������̃��X�g�𕄍������Ȃ��珑���o���B
     *
     * @param codeLen code���̃n�t�}���������̃��X�g
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    private void writeCodeLenList(int[] codeLen) throws IOException {
        for (int i = 0; i < codeLen.length; i++) {
            if (0 < codeLen[i]) {
                this.out.writeBits(5, 0x10 | (codeLen[i] - 1));             //throws IOException
            } else {
                this.out.writeBit(0);                                         //throws IOException
            }
        }
    }

    /**
     * OffHi���̃n�t�}���������̃��X�g�𕄍������Ȃ��珑���o���B
     *
     * @param OffHiLenList CodeFreq �̃n�t�}���������̃��X�g
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    private void writeOffHiLenList(int[] offHiLen) throws IOException {
        for (int i = 0; i < offHiLen.length; i++) {
            this.out.writeBits(4, offHiLen[i]);                               //throws IOException
        }
    }

}
//end of PostLh3Encoder.java
