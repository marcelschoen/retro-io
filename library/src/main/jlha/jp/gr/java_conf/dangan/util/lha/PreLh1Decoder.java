//start of PreLh1Decoder.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * PreLh1Decoder.java
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

import jp.gr.java_conf.dangan.io.BitDataBrokenException;
import jp.gr.java_conf.dangan.io.BitInputStream;
import jp.gr.java_conf.dangan.io.Bits;
import jp.gr.java_conf.dangan.io.NotEnoughBitsException;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

//import exceptions

/**
 * -lh1- �𓀗p�� PreLzssDecoder�B<br>
 *
 * <pre>
 * -- revision history --
 * $Log: PreLh1Decoder.java,v $
 * Revision 1.1  2002/12/01 00:00:00  dangan
 * [maintenance]
 *     �\�[�X����
 *
 * Revision 1.0  2002/08/05 00:00:00  dangan
 * add to version control
 * [bug fix]
 *     available �̌v�Z���Â������̂��C���B
 * [maintenance]
 *     �\�[�X����
 *
 * </pre>
 *
 * @author $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class PreLh1Decoder implements PreLzssDecoder {


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
    private static final int MaxMatch = 60;

    /** �ŏ���v�� */
    private static final int Threshold = 3;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  source
    //------------------------------------------------------------------
    //  BitInputStream in
    //------------------------------------------------------------------
    /**
     * -lh1- �̈��k�f�[�^���������� BitInputStream
     */
    BitInputStream in;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  huffman decoder
    //------------------------------------------------------------------
    //  DynamicHuffman huffman
    //  int[] offHiLen
    //  short[] offHiTable
    //  int offHiTableBits
    //------------------------------------------------------------------
    /**
     * code�������p�̓��I�n�t�}����
     */
    DynamicHuffman huffman;

    /**
     * �I�t�Z�b�g���̏��6bit�����p 
     * �n�t�}�����������X�g�B
     */
    int[] offHiLen;

    /**
     * �I�t�Z�b�g���̏��6bit�����p�e�[�u���B
     */
    short[] offHiTable;

    /**
     * �I�t�Z�b�g���̏��6bit�����p�e�[�u���������̂ɕK�v��bit���B
     */
    int offHiTableBits;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  backup for mark/reset
    //------------------------------------------------------------------
    //  DynamicHuffman markHuffman
    //------------------------------------------------------------------
    /** huffman �̃o�b�N�A�b�v�p */
    DynamicHuffman markHuffman;


    //------------------------------------------------------------------
    //  constructers
    //------------------------------------------------------------------
    //  public PreLh1Decoder( InputStream in )
    //------------------------------------------------------------------

    /**
     * -lh1- �𓀗p PreLzssDecoder ���\�z����B
     *
     * @param in -lh1- �ň��k���ꂽ�f�[�^������������̓X�g���[��
     */
    public PreLh1Decoder(InputStream in) {
        if (in != null) {
            if (in instanceof BitInputStream) {
                this.in = (BitInputStream) in;
            } else {
                this.in = new BitInputStream(in);
            }
            this.huffman = new DynamicHuffman(314);
            this.markHuffman = null;

            this.offHiLen = PreLh1Decoder.createLenList();
            try {
                this.offHiTable = StaticHuffman.createTable(this.offHiLen);
            } catch (BadHuffmanTableException exception) {
            }
            this.offHiTableBits = Bits.len(this.offHiTable.length - 1);
        } else {
            throw new NullPointerException("in");
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
     * -lh1- �� offset�f�R�[�h�pStaticHuffman��
     * �n�t�}�����������X�g�𐶐�����B
     *
     * @return -lh1- �� offset�f�R�[�h�pStaticHuffman��
     *         �n�t�}�����������X�g
     */
    private static int[] createLenList() {
        final int length = 64;
        final int[] list = {3, 0x01, 0x04, 0x0C, 0x18, 0x30, 0};

        int[] LenList = new int[length];
        int index = 0;
        int len = list[index++];

        for (int i = 0; i < length; i++) {
            if (list[index] == i) {
                len++;
                index++;
            }
            LenList[i] = len;
        }
        return LenList;
    }

    /**
     * -lh1- �ň��k���ꂽ
     * 1byte ��LZSS�����k�̃f�[�^�A
     * �������͈��k�R�[�h�̂�����v����ǂݍ��ށB<br>
     *
     * @return 1byte �� �����k�̃f�[�^�������́A
     *         ���k���ꂽ���k�R�[�h�̂�����v��
     *
     * @exception IOException  ���o�̓G���[�����������ꍇ
     * @exception EOFException EndOfStream�ɒB�����ꍇ
     */
    public int readCode() throws IOException {
        int node = this.huffman.childNode(DynamicHuffman.ROOT);
        while (0 <= node) {
            node = this.huffman.childNode(node - (in.readBoolean() ? 1 : 0));//throws EOFException,IOException
        }
        int code = ~node;
        this.huffman.update(code);
        return code;
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
     * -lh1- �ň��k���ꂽ
     * LZSS���k�R�[�h�̂�����v�ʒu��ǂݍ��ށB<br>
     *
     * @return -lh1- �ň��k���ꂽ���k�R�[�h�̂�����v�ʒu
     *
     * @exception IOException  ���o�̓G���[�����������ꍇ�B
     * @exception EOFException �f�[�^���r���܂ł����Ȃ�����
     *                         �\������ EndOfStream �ɓ��B�����ꍇ�B
     * @exception BitDataBrokenException
     *                         �f�[�^���r���܂ł����Ȃ�����
     *                         �\������ EndOfStream �ɓ��B�������A
     *                         ���̓��o�̓G���[�����������B
     * @exception NotEnoughBitsException
     *                         �f�[�^���r���܂ł����Ȃ�����
     *                         �\������ EndOfStream �ɓ��B�������A
     *                         ���̓��o�̓G���[�����������B
     */
    public int readOffset() throws IOException {
        //offHi������킷�̂ɍŒZ�̏ꍇ�� 0 �� 3bit ��
        //offHiTableBits �� 8bit�� ���҂̍��� 5bit�B
        //���̂��߁A����6bit��ǂݍ��ގ������������
        //����ȃf�[�^�ł� peekBits ��
        //NotEnoughBitsException �𓊂��邱�Ƃ͖����B
        int offHi = this.offHiTable[this.in.peekBits(this.offHiTableBits)]; //throws NotEnoughBitsException IOException
        this.in.skipBits(this.offHiLen[offHi]);                             //throws IOException

        return (offHi << 6) | this.in.readBits(6);                          //throws BitDataBrokenException NotEnoughBitsException IOException
    }

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
    public void mark(int readLimit) {
        this.in.mark(readLimit * 18 / 8 + 4);
        this.markHuffman = (DynamicHuffman) this.huffman.clone();
    }

    /**
     * �ڑ����ꂽ���̓X�g���[���̓ǂݍ��݈ʒu���Ō��
     * mark() ���\�b�h���Ăяo���ꂽ�Ƃ��̈ʒu�ɐݒ肷��B<br>
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void reset() throws IOException {
        //mark()���Ȃ��� reset() ���悤�Ƃ����ꍇ�A
        //readLimit �𒴂��� reset() ���悤�Ƃ����ꍇ�A
        //�ڑ����ꂽ InputStream �� markSupported() �� false ��Ԃ��ꍇ��
        //BitInputStream �� IOException �𓊂���B
        this.in.reset();                                                        //throws IOException

        this.huffman = (DynamicHuffman) this.markHuffman.clone();
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
     * �ڑ����ꂽ���̓X�g���[���� mark() �� reset() ���T�|�[�g���邩�𓾂�B<br>
     *
     * @return �X�g���[���� mark() �� reset() ��
     *         �T�|�[�g����ꍇ�� true�B<br>
     *         �T�|�[�g���Ȃ��ꍇ�� false�B<br>
     */
    public boolean markSupported() {
        return this.in.markSupported();
    }

    /**
     * �u���b�N�����ɓǂݏo�����Ƃ̏o����Œ�o�C�g���𓾂�B<br>
     * InputStream �� available() �ƈႢ�A
     * ���̍Œ�o�C�g���͕K�������ۏႳ��Ă��Ȃ����ɒ��ӂ��邱�ƁB<br>
     *
     * @return �u���b�N���Ȃ��œǂݏo����Œ�o�C�g���B<br>
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     *
     * @see PreLzssDecoder#available()
     */
    public int available() throws IOException {
        return Math.max(this.in.availableBits() / 18 - 4, 0);                 //throws IOException
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
     * ���̃X�g���[������A�g�p���Ă����S�Ă̎������������B
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void close() throws IOException {
        this.in.close();                                                        //throws IOException

        this.in = null;
        this.huffman = null;
        this.markHuffman = null;

        this.offHiLen = null;
        this.offHiTable = null;
        this.offHiTableBits = 0;
    }

    /**
     * -lh1-�`����LZSS�����̃T�C�Y�𓾂�B
     *
     * @return -lh1-�`����LZSS�����̃T�C�Y
     */
    public int getDictionarySize() {
        return PreLh1Decoder.DictionarySize;
    }

    /**
     * -lh1-�`����LZSS�̍ő��v���𓾂�B
     *
     * @return -lh1-�`����LZSS�̍ő��v��
     */
    public int getMaxMatch() {
        return PreLh1Decoder.MaxMatch;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  constant huffman tree
    //------------------------------------------------------------------
    //  private static int[] createLenList()
    //------------------------------------------------------------------

    /**
     * -lh1-�`����LZSS�̈��k�A�񈳏k��臒l�𓾂�B
     *
     * @return -lh1-�`����LZSS�̈��k�A�񈳏k��臒l
     */
    public int getThreshold() {
        return PreLh1Decoder.Threshold;
    }

}
//end of PreLh1Decoder.java
