//start of PreLh3Decoder.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * PreLh3Decoder.java
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
import jp.gr.java_conf.dangan.io.NotEnoughBitsException;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

//import exceptions

/**
 * -lh3- �𓀗p�� PreLzssDecoder�B
 *
 * <pre>
 * -- revision history --
 * $Log: PreLh3Decoder.java,v $
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
 *     �^�u�p�~
 *     ���C�Z���X���̏C��
 *
 * </pre>
 *
 * @author $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class PreLh3Decoder implements PreLzssDecoder {


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
    private static final int DictionarySize = 8192;

    /** �ő��v�� */
    private static final int MaxMatch = 256;

    /** �ŏ���v�� */
    private static final int Threshold = 3;


    //------------------------------------------------------------------
    //  class field
    //------------------------------------------------------------------
    //  private static final int CodeSize
    //------------------------------------------------------------------
    /**
     * code���̃n�t�}���؂̃T�C�Y 
     * code��������ȏ�̒l�������ꍇ�͗]�v�ȃr�b�g���o�͂��ĕ₤�B
     */
    private static final int CodeSize = 286;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  source
    //------------------------------------------------------------------
    //  private BitInputStream in
    //------------------------------------------------------------------
    /**
     * -lh3- �̈��k�f�[�^���������� BitInputStream
     */
    private BitInputStream in;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  huffman decoder
    //------------------------------------------------------------------
    //  private int blockSize
    //  private int[] codeLen
    //  private short[] codeTable
    //  private int codeTableBits
    //  private short[][] codeTree
    //  private int[] offHiLen
    //  private short[] offHiTable
    //  private int offHiTableBits
    //  private short[][] offHiTree
    //------------------------------------------------------------------
    /**
     * ���ݏ������̃u���b�N�̎c��T�C�Y�������B
     */
    private int blockSize;

    /**
     * code ���̃n�t�}���������̕\
     */
    private int[] codeLen;

    /**
     * code �������p�̃e�[�u��
     * ���̏ꍇ�� codeTree ��index�������B
     * ���̏ꍇ�� code ��S�r�b�g���]�������́B 
     */
    private short[] codeTable;

    /**
     * codeTable ���������߂ɕK�v��bit���B
     */
    private int codeTableBits;

    /**
     * codeTable �Ɏ��܂肫��Ȃ��f�[�^�̕����p�̖�
     * ���̏ꍇ�� codeTree ��index�������B
     * ���̏ꍇ�� code ��S�r�b�g���]�������́B 
     */
    private short[][] codeTree;

    /**
     * offHi ���̃n�t�}���������̕\
     */
    private int[] offHiLen;

    /**
     * offHi �������p�̃e�[�u��
     * ���̏ꍇ�� offHi ��index�������B
     * ���̏ꍇ�� code ��S�r�b�g���]�������́B 
     */
    private short[] offHiTable;

    /**
     * offHiTable ���������߂ɕK�v��bit���B
     */
    private int offHiTableBits;

    /**
     * offHiTable �Ɏ��܂肫��Ȃ��f�[�^�̕����p�̖�
     * ���̏ꍇ�� offHi ��index�������B
     * ���̏ꍇ�� code ��S�r�b�g���]�������́B 
     */
    private short[][] offHiTree;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  backup for mark/reset
    //------------------------------------------------------------------
    //  private int markBlockSize
    //  private int[] markCodeLen
    //  private short[] markCodeTable
    //  private short[][] markCodeTree
    //  private int[] markOffHiLen
    //  private short[] markOffHiTable
    //  private short[][] markOffHiTree
    //------------------------------------------------------------------
    /** blockSize�̃o�b�N�A�b�v�p */
    private int markBlockSize;
    /** codeLen �̃o�b�N�A�b�v�p */
    private int[] markCodeLen;
    /** codeTable �̃o�b�N�A�b�v�p */
    private short[] markCodeTable;
    /** codeTree �̃o�b�N�A�b�v�p */
    private short[][] markCodeTree;
    /** offHiLen �̃o�b�N�A�b�v�p */
    private int[] markOffHiLen;
    /** offHiTable �̃o�b�N�A�b�v�p */
    private short[] markOffHiTable;
    /** offHiTree �̃o�b�N�A�b�v�p */
    private short[][] markOffHiTree;


    //------------------------------------------------------------------
    //  constructer
    //------------------------------------------------------------------
    //  private PreLh3Decoder()
    //  public PreLh3Decoder( InputStream in )
    //  public PreLh3Decoder( InputStream in, 
    //                        int CodeTableBits, int OffHiTableBits )
    //------------------------------------------------------------------

    /**
     * �f�t�H���g�R���X�g���N�^�B
     * �g�p�s�B
     */
    private PreLh3Decoder() {
    }

    /**
     * -lh3- �𓀗p PreLzssDecoder ���\�z����B<br>
     * �e�[�u���T�C�Y�ɂ� �f�t�H���g�l���g�p����B
     *
     * @param in ���k�f�[�^������������̓X�g���[��
     */
    public PreLh3Decoder(InputStream in) {
        this(in, 12, 8);
    }

    /**
     * -lh3- �𓀗p PreLzssDecoder ���\�z����B<br>
     *
     * @param in             ���k�f�[�^������������̓X�g���[��
     * @param CodeTableBits  code ���𕜍����邽�߂Ɏg�p����
     *                       �e�[�u���̃T�C�Y���r�b�g���Ŏw�肷��B 
     *                       12 ���w�肷��� 4096 �̃��b�N�A�b�v�e�[�u���𐶐�����B 
     * @param OffHiTableBits offHi ���𕜍����邽�߂Ɏg�p����
     *                       �e�[�u���̃T�C�Y���r�b�g���Ŏw�肷��B
     *                       8 ���w�肷��� 256 �̃��b�N�A�b�v�e�[�u���𐶐�����B 
     *
     * @exception IllegalArgumentException
     *                       CodeTableBits, OffHiTableBits �� 0�ȉ��̏ꍇ
     */
    public PreLh3Decoder(InputStream in,
                         int CodeTableBits,
                         int OffHiTableBits) {
        if (in != null
                && 0 < CodeTableBits
                && 0 < OffHiTableBits) {
            if (in instanceof BitInputStream) {
                this.in = (BitInputStream) in;
            } else {
                this.in = new BitInputStream(in);
            }
            this.blockSize = 0;
            this.codeTableBits = CodeTableBits;
            this.offHiTableBits = OffHiTableBits;
        } else if (in == null) {
            throw new NullPointerException("in");
        } else if (CodeTableBits <= 0) {
            throw new IllegalArgumentException("CodeTableBits too small. CodeTableBits must be larger than 1.");
        } else {
            throw new IllegalArgumentException("OffHiTableBits too small. OffHiTableBits must be larger than 1.");
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
     * -lh3- �� offset�f�R�[�h�pStaticHuffman��
     * �n�t�}�����������X�g�𐶐�����B
     *
     * @return -lh3- �� offset�f�R�[�h�pStaticHuffman��
     *         �n�t�}�����������X�g
     */
    private static int[] createConstOffHiLen() {
        final int length = PreLh3Decoder.DictionarySize >> 6;
        final int[] list = {2, 0x01, 0x01, 0x03, 0x06, 0x0D, 0x1F, 0x4E, 0};

        int[] LenList = new int[length];
        int index = 0;
        int len = list[index++];

        for (int i = 0; i < length; i++) {
            while (list[index] == i) {
                len++;
                index++;
            }
            LenList[i] = len;
        }
        return LenList;
    }

    /**
     * -lh3- �ň��k���ꂽ
     * 1byte ��LZSS�����k�̃f�[�^�A
     * �������͈��k�R�[�h�̂�����v����ǂݍ��ށB<br>
     *
     * @return 1byte �� �����k�̃f�[�^�������́A
     *         ���k���ꂽ���k�R�[�h�̂�����v��
     *
     * @exception IOException  ���o�̓G���[�����������ꍇ
     * @exception EOFException EndOfStream�ɒB�����ꍇ
     * @exception BadHuffmanTableException
     *                         �n�t�}���؂��\�����邽�߂�
     *                         �n�t�}���������̕\���s���Ȃ��߁A
     *                         �n�t�}�������킪�����ł��Ȃ��ꍇ
     */
    public int readCode() throws IOException {
        if (this.blockSize <= 0) {
            this.readBlockHead();
        }
        this.blockSize--;

        int code;
        try {
            int node = this.codeTable[this.in.peekBits(this.codeTableBits)];
            if (node < 0) {
                code = ~node;
                this.in.skipBits(this.codeLen[code]);
            } else {
                this.in.skipBits(this.codeTableBits);
                do {
                    node = this.codeTree[this.in.readBit()][node];
                } while (0 <= node);
                code = ~node;
            }
        } catch (NotEnoughBitsException exception) {
            int avail = exception.getAvailableBits();
            int bits = this.in.peekBits(avail);
            bits = bits << (this.codeTableBits - avail);
            int node = this.codeTable[bits];

            if (node < 0) {
                code = ~node;
                if (this.in.skipBits(this.codeLen[code]) < this.codeLen[code]) {
                    throw new EOFException();
                }
            } else {
                this.in.skipBits(avail);
                throw new EOFException();
            }
        } catch (ArrayIndexOutOfBoundsException exception) {
            throw new EOFException();
        }

        final int CodeMax = PreLh3Decoder.CodeSize - 1;
        if (code == CodeMax) {
            code += this.in.readBits(8);
        }
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
     * -lh3- �ň��k���ꂽ
     * LZSS���k�R�[�h�̂�����v�ʒu��ǂݍ��ށB<br>
     *
     * @return -lh3- �ň��k���ꂽ���k�R�[�h�̂�����v�ʒu
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public int readOffset() throws IOException {
        int offHi;
        try {
            int node = this.offHiTable[this.in.peekBits(this.offHiTableBits)];
            if (node < 0) {
                offHi = ~node;
                this.in.skipBits(this.offHiLen[offHi]);
            } else {
                this.in.skipBits(this.offHiTableBits);
                do {
                    node = this.offHiTree[this.in.readBit()][node];
                } while (0 <= node);
                offHi = ~node;
            }
        } catch (NotEnoughBitsException exception) {
            int avail = exception.getAvailableBits();
            int bits = this.in.peekBits(avail);
            bits = bits << (this.offHiTableBits - avail);
            int node = this.offHiTable[bits];

            if (node < 0) {
                offHi = ~node;
                if (this.offHiLen[offHi] <= avail) {
                    this.in.skipBits(this.offHiLen[offHi]);
                } else {
                    this.in.skipBits(avail);
                    throw new EOFException();
                }
            } else {
                this.in.skipBits(avail);
                throw new EOFException();
            }
        } catch (ArrayIndexOutOfBoundsException exception) {
            throw new EOFException();
        }

        return (offHi << 6) | this.in.readBits(6);
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
        readLimit = readLimit * StaticHuffman.LimitLen / 8;
        if (this.blockSize < readLimit) {
            readLimit += 245;
        }
        this.in.mark(readLimit);

        this.markBlockSize = this.blockSize;
        this.markCodeLen = this.codeLen;
        this.markCodeTable = this.codeTable;
        this.markCodeTree = this.codeTree;
        this.markOffHiLen = this.offHiLen;
        this.markOffHiTable = this.offHiTable;
        this.markOffHiTree = this.offHiTree;
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

        this.blockSize = this.markBlockSize;
        this.codeLen = this.markCodeLen;
        this.codeTable = this.markCodeTable;
        this.codeTree = this.markCodeTree;
        this.offHiLen = this.markOffHiLen;
        this.offHiTable = this.markOffHiTable;
        this.offHiTree = this.markOffHiTree;
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
     * �ڑ����ꂽ���̓X�g���[���� mark() �� reset() ��
     * �T�|�[�g���邩�𓾂�B<br>
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
        int avail = this.in.available() * 8 / StaticHuffman.LimitLen;
        if (this.blockSize < avail) {
            avail -= 245;
        }
        return Math.max(avail, 0);
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
     * ���̃X�g���[������A�g�p���Ă����S�Ă̎������������B
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void close() throws IOException {
        this.in.close();
        this.in = null;

        this.blockSize = 0;
        this.codeLen = null;
        this.codeTable = null;
        this.codeTree = null;
        this.offHiLen = null;
        this.offHiTable = null;
        this.offHiTree = null;

        this.markBlockSize = 0;
        this.markCodeLen = null;
        this.markCodeTable = null;
        this.markCodeTree = null;
        this.markOffHiLen = null;
        this.markOffHiTable = null;
        this.markOffHiTree = null;
    }

    /**
     * -lh3-�`���� LZSS�����̃T�C�Y�𓾂�B
     *
     * @return -lh3-�`���� LZSS�����̃T�C�Y
     */
    public int getDictionarySize() {
        return PreLh3Decoder.DictionarySize;
    }

    /**
     * -lh3-�`���� LZSS�̍ő��v���𓾂�B
     *
     * @return -lh3-�`���� LZSS�̍ő��v��
     */
    public int getMaxMatch() {
        return PreLh3Decoder.MaxMatch;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  read block head
    //------------------------------------------------------------------
    //  private void readBlockHead()
    //  private int[] readCodeLen()
    //  private int[] readOffHiLen()
    //------------------------------------------------------------------

    /**
     * -lh3-�`���� LZSS�̈��k�A�񈳏k��臒l�𓾂�B
     *
     * @return -lh3-�`���� LZSS�̈��k�A�񈳏k��臒l
     */
    public int getThreshold() {
        return PreLh3Decoder.Threshold;
    }

    /**
     * �n�t�}���u���b�N�̐擪�ɂ���
     * �u���b�N�T�C�Y��n�t�}���������̃��X�g��ǂݍ��ށB
     *
     * @exception IOException  ���o�̓G���[�����������ꍇ
     * @exception EOFException EndOfStream�ɒB�����ꍇ
     * @exception BadHuffmanTableException
     *                         �n�t�}���؂��\�����邽�߂�
     *                         �n�t�}���������̕\���s���Ȃ��߁A
     *                         �n�t�}�������킪�����ł��Ȃ��ꍇ
     * @exception BitDataBrokenException
     *                         �\�����ʌ����Ńf�[�^�ǂ݂��݂�
     *                         ���f���ꂽ���ߗv�����ꂽ�r�b�g��
     *                         �̃f�[�^�������Ȃ������ꍇ
     */
    private void readBlockHead() throws IOException {
        //�u���b�N�T�C�Y�ǂݍ���
        //����ȃf�[�^�̏ꍇ�A���̕����� EndOfStream �ɓ��B����B
        try {
            this.blockSize = this.in.readBits(16);                            //throws BitDataBrokenException, EOFException, IOException
        } catch (BitDataBrokenException exception) {
            if (exception.getCause() instanceof EOFException) {
                throw (EOFException) exception.getCause();
            } else {
                throw exception;
            }
        }

        //code ���̏���
        this.codeLen = this.readCodeLen();
        if (1 < this.codeLen.length) {
            short[][] tableAndTree =
                    StaticHuffman.createTableAndTree(this.codeLen, this.codeTableBits);
            this.codeTable = tableAndTree[0];
            this.codeTree = new short[][]{tableAndTree[1], tableAndTree[2]};
        } else {
            int code = this.codeLen[0];
            this.codeLen = new int[PreLh3Decoder.CodeSize];
            this.codeTable = new short[1 << this.codeTableBits];
            for (int i = 0; i < this.codeTable.length; i++) {
                this.codeTable[i] = ((short) ~code);
            }
            this.codeTree = new short[][]{new short[0], new short[0]};
        }

        //offHi ���̏���
        this.offHiLen = this.readOffHiLen();
        if (1 < this.offHiLen.length) {
            short[][] tableAndTree =
                    StaticHuffman.createTableAndTree(this.offHiLen, this.offHiTableBits);
            this.offHiTable = tableAndTree[0];
            this.offHiTree = new short[][]{tableAndTree[1], tableAndTree[2]};
        } else {
            int offHi = this.offHiLen[0];
            this.offHiLen = new int[PreLh3Decoder.DictionarySize >> 6];
            this.offHiTable = new short[1 << this.offHiTableBits];
            for (int i = 0; i < this.offHiTable.length; i++) {
                this.offHiTable[i] = ((short) ~offHi);
            }
            this.offHiTree = new short[][]{new short[0], new short[0]};
        }
    }

    /**
     * code�� �̃n�t�}���������̃��X�g��ǂ݂��ށB
     *
     * @return �n�t�}���������̃��X�g�B
     *         �������� ���� 1 �̗B��̃R�[�h
     *
     * @exception IOException  ���o�̓G���[�����������ꍇ
     * @exception EOFException EndOfStream�ɒB�����ꍇ
     * @exception BitDataBrokenException
     *                         �\�����ʌ����Ńf�[�^�ǂ݂��݂�
     *                         ���f���ꂽ���ߗv�����ꂽ�r�b�g��
     *                         �̃f�[�^�������Ȃ������ꍇ
     */
    private int[] readCodeLen() throws IOException {
        int[] codeLen = new int[PreLh3Decoder.CodeSize];

        for (int i = 0; i < codeLen.length; i++) {
            if (this.in.readBoolean())
                codeLen[i] = this.in.readBits(4) + 1;

            if (i == 2 && codeLen[0] == 1 && codeLen[1] == 1 && codeLen[2] == 1) {
                return new int[]{this.in.readBits(9)};
            }
        }
        return codeLen;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  constant huffman tree
    //------------------------------------------------------------------
    //  private static int[] createConstOffHiLen()
    //------------------------------------------------------------------

    /**
     * offHi���̃n�t�}���������̃��X�g��ǂ݂���
     *
     * @return �n�t�}���������̃��X�g�B
     *         �������� ���� 1 �̗B��̃R�[�h
     *
     * @exception IOException  ���o�̓G���[�����������ꍇ
     * @exception EOFException EndOfStream�ɒB�����ꍇ
     * @exception BitDataBrokenException
     *                         �\�����ʌ����Ńf�[�^�ǂ݂��݂�
     *                         ���f���ꂽ���ߗv�����ꂽ�r�b�g��
     *                         �̃f�[�^�������Ȃ������ꍇ
     */
    private int[] readOffHiLen() throws IOException {
        if (this.in.readBoolean()) {
            int[] offHiLen = new int[PreLh3Decoder.DictionarySize >> 6];

            for (int i = 0; i < offHiLen.length; i++) {
                offHiLen[i] = this.in.readBits(4);

                if (i == 2 && offHiLen[0] == 1 && offHiLen[1] == 1 && offHiLen[2] == 1) {
                    return new int[]{this.in.readBits(7)};
                }
            }
            return offHiLen;
        } else {
            return PreLh3Decoder.createConstOffHiLen();
        }
    }

}
//end of PreLh3Decoder.java
