//start of PreLh5Decoder.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * PreLh5Decoder.java
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

import jp.gr.java_conf.dangan.io.BitDataBrokenException;
import jp.gr.java_conf.dangan.io.Bits;
import jp.gr.java_conf.dangan.io.NotEnoughBitsException;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

//import exceptions


/**
 * -lh4-, -lh5-, -lh6-, -lh7- �𓀗p�� PreLzssDecoder�B<br>
 *
 * <pre>
 * -- revision history --
 * $Log: PreLh5Decoder.java,v $
 * Revision 1.3  2002/12/08 00:00:00  dangan
 * [bug fix]
 *     readCode �Ńn�t�}�������ǂݍ��ݓr����
 *     EndOfStream �ɒB�����ꍇ�� EOFException �𓊂��Ă��Ȃ������B
 *
 * Revision 1.2  2002/12/08 00:00:00  dangan
 * [change]
 *     �N���X�� �� PreLh5DecoderFast ���� PreLh5Decoder �ɕύX�B
 *
 * Revision 1.1  2002/12/06 00:00:00  dangan
 * [maintenance]
 *     �\�[�X����
 *
 * Revision 1.0  2002/08/05 00:00:00  dangan
 * add to version control
 * [maintenance]
 *     �ŐV�� BitInputStream �� PreLh5Decoder ����\�[�X����荞�ށB
 *     �\�[�X����
 *     �^�u�p�~
 *     ���C�Z���X���̏C��
 *
 * </pre>
 *
 * @author $Author: dangan $
 * @version $Revision: 1.3 $
 */
public class PreLh5Decoder implements PreLzssDecoder {


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  source
    //------------------------------------------------------------------
    //  private InputStream in
    //------------------------------------------------------------------
    /**
     * �ڑ����ꂽ���̓X�g���[��
     */
    private InputStream in;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  staff of BitInputStream
    //------------------------------------------------------------------
    //  cache
    //------------------------------------------------------------------
    //  private byte[] cache
    //  private int    cacheLimit
    //  private int    cachePosition
    //------------------------------------------------------------------
    /**
     * ���x�ቺ�}�~�p�o�C�g�z��
     */
    private byte[] cache;

    /**
     * cache ���̗L���o�C�g��
     */
    private int cacheLimit;

    /**
     * cache ���̌��ݏ����ʒu
     */
    private int cachePosition;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  staff of BitInputStream
    //------------------------------------------------------------------
    //  bit buffer
    //------------------------------------------------------------------
    //  private int    bitBuffer
    //  private int    bitCount
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
    //  instance field
    //------------------------------------------------------------------
    //  huffman decoder
    //------------------------------------------------------------------
    //  private int blockSize
    //  private int[] codeLen
    //  private short[] codeTable
    //  private int codeTableBits
    //  private short[][] codeTree
    //  private short[] offLenTable
    //  private int offLenTableBits
    //  private short[][] offLenTree
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
     * offLen ���̃n�t�}���������̕\
     */
    private int[] offLenLen;

    /**
     * offLen �������p�̃e�[�u��
     * ���̏ꍇ�� offLenTree ��index�������B
     * ���̏ꍇ�� offLen ��S�r�b�g���]�������́B 
     */
    private short[] offLenTable;

    /**
     * offLenTable ���������߂ɕK�v��bit���B
     */
    private int offLenTableBits;

    /**
     * offLenTable �Ɏ��܂肫��Ȃ��f�[�^�̕����p�̖�
     * ���̏ꍇ�� offLenTree ��index�������B
     * ���̏ꍇ�� offLen ��S�r�b�g���]�������́B 
     */
    private short[][] offLenTree;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  LZSS parameter
    //------------------------------------------------------------------
    //  private int DictionarySize
    //  private int MaxMatch
    //  private int Threshold
    //------------------------------------------------------------------
    /**
     * LZSS �����T�C�Y
     */
    private int DictionarySize;

    /**
     * LZSS �Œ���v��
     */
    private int MaxMatch;

    /**
     * LZSS ���k/�񈳏k��臒l
     */
    private int Threshold;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  backup for mark/reset
    //------------------------------------------------------------------
    //  private boolean markPositionIsInCache
    //  private byte[]    markCache
    //  private int       markCacheLimit
    //  private int       markCachePosition
    //  private int       markBitBuffer
    //  private int       markBitCount
    //  private int       markBlockSize
    //  private int[]     markCodeLen
    //  private short[]   markCodeTable
    //  private short[][] markCodeTree
    //  private int[]     markOffLenLen
    //  private short[]   markOffLenTable
    //  private short[][] markOffLenTree
    //------------------------------------------------------------------
    /**
     * mark�ʒu���L���b�V���͈͓̔��ɂ��邩�������B
     * mark���ꂽ�Ƃ� true �ɐݒ肳��A
     * ���� in ���� �L���b�V���ւ̓ǂݍ��݂�
     * �s��ꂽ�Ƃ��� false �ɐݒ肳���B
     */
    private boolean markPositionIsInCache;

    /** cache �� �o�b�N�A�b�v�p */
    private byte[] markCache;
    /** cacheAvailable �̃o�b�N�A�b�v�p */
    private int markCacheLimit;
    /** cachePosition �̃o�b�N�A�b�v�p */
    private int markCachePosition;
    /** bitBuffer �̃o�b�N�A�b�v�p */
    private int markBitBuffer;
    /** bitCount �̃o�b�N�A�b�v�p */
    private int markBitCount;
    /** blockSize�̃o�b�N�A�b�v�p */
    private int markBlockSize;
    /** codeLen �̃o�b�N�A�b�v�p */
    private int[] markCodeLen;
    /** codeTable �̃o�b�N�A�b�v�p */
    private short[] markCodeTable;
    /** codeTree �̃o�b�N�A�b�v�p */
    private short[][] markCodeTree;
    /** offLenLen �̃o�b�N�A�b�v�p */
    private int[] markOffLenLen;
    /** offLenTable �̃o�b�N�A�b�v�p */
    private short[] markOffLenTable;
    /** offLenTree �̃o�b�N�A�b�v�p */
    private short[][] markOffLenTree;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private PreLh5Decoder()
    //  public PreLh5Decoder( InputStream in )
    //  public PreLh5Decoder( InputStream in, String CompressMethod )
    //  public PreLh5Decoder( InputStream in, String CompressMethod, 
    //                            int CodeTableBits, int OffLenTableBits )
    //------------------------------------------------------------------

    /**
     * �f�t�H���g�R���X�g���N�^�B
     * �g�p�s�B
     */
    private PreLh5Decoder() {
    }

    /**
     * -lh5- �𓀗p PreLzssDecoder ���\�z����B<br>
     * �e�[�u���T�C�Y�̓f�t�H���g�l���g�p����B
     *
     * @param in -lh5-�`���̈��k�f�[�^������������̓X�g���[��
     */
    public PreLh5Decoder(InputStream in) {
        this(in, CompressMethod.LH5, 12, 8);
    }

    /**
     * -lh4-,-lh5-,-lh6-,-lh7- �𓀗p PreLzssDecoder ���\�z����B<br>
     * �e�[�u���T�C�Y�ɂ� �f�t�H���g�l���g�p����B
     *
     * @param in      ���k�f�[�^������������̓X�g���[��
     * @param method  ���k�@���ʎq<br>
     *  &emsp;&emsp; CompressMethod.LH4 <br>
     *  &emsp;&emsp; CompressMethod.LH5 <br>
     *  &emsp;&emsp; CompressMethod.LH6 <br>
     *  &emsp;&emsp; CompressMethod.LH7 <br>
     *  &emsp;&emsp; �̉��ꂩ���w�肷��B
     *
     * @exception IllegalArgumentException
     *               method ����L�ȊO�̏ꍇ
     */
    public PreLh5Decoder(InputStream in,
                         String method) {

        this(in, method, 12, 8);
    }

    /**
     * -lh4-,-lh5-,-lh6-,-lh7- �𓀗p PreLzssDecoder ���\�z����B
     *
     * @param in              ���k�f�[�^������������̓X�g���[��
     * @param method          ���k�@���ʎq<br>
     *           &emsp;&emsp; CompressMethod.LH4 <br>
     *           &emsp;&emsp; CompressMethod.LH5 <br>
     *           &emsp;&emsp; CompressMethod.LH6 <br>
     *           &emsp;&emsp; CompressMethod.LH7 <br>
     *           &emsp;&emsp; �̉��ꂩ���w�肷��B
     * @param CodeTableBits   code ���𕜍����邽�߂Ɏg�p����
     *                        �e�[�u���̃T�C�Y���r�b�g���Ŏw�肷��B 
     *                        12 ���w�肷��� 4096 �̃��b�N�A�b�v�e�[�u���𐶐�����B 
     * @param OffLenTableBits offLen ���𕜍����邽�߂Ɏg�p����
     *                        �e�[�u���̃T�C�Y���r�b�g���Ŏw�肷��B
     *                        8 ���w�肷��� 256 �̃��b�N�A�b�v�e�[�u���𐶐�����B 
     *
     * @exception IllegalArgumentException <br>
     *           &emsp;&emsp; (1) method ����L�ȊO�̏ꍇ<br>
     *           &emsp;&emsp; (2) CodeTableBits �������� 
     *                            OffLenTableBits �� 0�ȉ��̏ꍇ<br>
     *           &emsp;&emsp; �̉��ꂩ
     */
    public PreLh5Decoder(InputStream in,
                         String method,
                         int CodeTableBits,
                         int OffLenTableBits) {
        if (CompressMethod.LH4.equals(method)
                || CompressMethod.LH5.equals(method)
                || CompressMethod.LH6.equals(method)
                || CompressMethod.LH7.equals(method)) {

            this.DictionarySize = CompressMethod.toDictionarySize(method);
            this.MaxMatch = CompressMethod.toMaxMatch(method);
            this.Threshold = CompressMethod.toThreshold(method);

            if (in != null
                    && 0 < CodeTableBits
                    && 0 < OffLenTableBits) {
                this.in = in;
                this.cache = new byte[1024];
                this.cacheLimit = 0;
                this.cachePosition = 0;
                this.bitBuffer = 0;
                this.bitCount = 0;
                this.blockSize = 0;
                this.codeTableBits = CodeTableBits;
                this.offLenTableBits = OffLenTableBits;

                this.markPositionIsInCache = false;
                this.markCache = null;
                this.markCacheLimit = 0;
                this.markCachePosition = 0;
                this.markBitBuffer = 0;
                this.markBitCount = 0;

            } else if (in == null) {
                throw new NullPointerException("in");
            } else if (CodeTableBits <= 0) {
                throw new IllegalArgumentException("CodeTableBits too small. CodeTableBits must be larger than 1.");
            } else {
                throw new IllegalArgumentException("OffHiTableBits too small. OffHiTableBits must be larger than 1.");
            }
        } else if (null == method) {
            throw new NullPointerException("method");
        } else {
            throw new IllegalArgumentException("Unknown compress method " + method);
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
     * -lh5- �n�̈��k�@�ň��k���ꂽ 
     * 1byte ��LZSS�����k�̃f�[�^�A
     * �������͈��k�R�[�h�̂�����v����ǂݍ��ށB<br>
     *
     * @return 1byte �� �����k�̃f�[�^�A
     *         �������͈��k���ꂽ���k�R�[�h�̂�����v��
     *
     * @exception IOException  ���o�̓G���[�����������ꍇ
     * @exception EOFException EndOfStream�ɒB�����ꍇ
     * @exception BadHuffmanTableException
     *                         �n�t�}���؂��\�����邽�߂�
     *                         �n�t�}���������̕\���s���ł���ꍇ
     */
    public int readCode() throws IOException {
        if (this.blockSize <= 0) {
            this.readBlockHead();
        }
        this.blockSize--;

        if (this.bitCount < 16) {
            if (2 <= this.cacheLimit - this.cachePosition) {
                this.bitBuffer |= ((this.cache[this.cachePosition++] & 0xFF) << (24 - this.bitCount))
                        | ((this.cache[this.cachePosition++] & 0xFF) << (16 - this.bitCount));
                this.bitCount += 16;
            } else {
                this.fillBitBuffer();

                int node = this.codeTable[this.bitBuffer >>> (32 - this.codeTableBits)];
                if (0 <= node) {
                    int bits = this.bitBuffer << this.codeTableBits;
                    do {
                        node = this.codeTree[bits >>> 31][node];
                        bits <<= 1;
                    } while (0 <= node);
                }
                int len = this.codeLen[~node];
                if (len <= this.bitCount) {
                    this.bitBuffer <<= len;
                    this.bitCount -= len;

                    return ~node;
                } else {
                    this.bitCount = 0;
                    this.bitBuffer = 0;
                    throw new EOFException();
                }
            }
        }

        int node = this.codeTable[this.bitBuffer >>> (32 - this.codeTableBits)];
        if (0 <= node) {
            int bits = this.bitBuffer << this.codeTableBits;
            do {
                node = this.codeTree[bits >>> 31][node];
                bits <<= 1;
            } while (0 <= node);
        }
        int len = this.codeLen[~node];
        this.bitBuffer <<= len;
        this.bitCount -= len;

        return ~node;
    }

    /**
     * -lh5- �n�̈��k�@�ň��k���ꂽ
     * LZSS���k�R�[�h�̂�����v�ʒu��ǂݍ��ށB<br>
     *
     * @return -lh5- �n�ň��k���ꂽ���k�R�[�h�̂�����v�ʒu
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public int readOffset() throws IOException {
        if (this.bitCount < 16) {
            if (2 <= this.cacheLimit - this.cachePosition) {
                this.bitBuffer |= ((this.cache[this.cachePosition++] & 0xFF) << (24 - this.bitCount))
                        | ((this.cache[this.cachePosition++] & 0xFF) << (16 - this.bitCount));
                this.bitCount += 16;
            } else {
                this.fillBitBuffer();
            }
        }

        int node = this.offLenTable[this.bitBuffer >>> (32 - this.offLenTableBits)];
        if (0 <= node) {
            int bits = this.bitBuffer << this.offLenTableBits;
            do {
                node = this.offLenTree[bits >>> 31][node];
                bits <<= 1;
            } while (0 <= node);
        }
        int offlen = ~node;
        int len = this.offLenLen[offlen];
        this.bitBuffer <<= len;
        this.bitCount -= len;

        offlen--;
        if (0 <= offlen) {
            return (1 << offlen) | this.readBits(offlen);
        } else {
            return 0;
        }
    }


    //------------------------------------------------------------------
    //  method of PreLzssDecoder
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

        //------------------------------------------------------------------
        //  �n�t�}���������ōň��̏ꍇ���l������ readLimit ���v�Z����
        if (this.blockSize < readLimit) {
            readLimit = readLimit * StaticHuffman.LimitLen / 8;
            readLimit += 272; //block head
        } else {
            readLimit = readLimit * StaticHuffman.LimitLen / 8;
        }

        //------------------------------------------------------------------
        //  BitInputStream �p�L���b�V���� readLimit ���v�Z����B
        readLimit -= this.cacheLimit - this.cachePosition;
        readLimit -= this.bitCount / 8;
        readLimit += 4;
        readLimit = (readLimit + this.cache.length - 1) / this.cache.length
                * this.cache.length;

        //------------------------------------------------------------------
        //  mark ����
        this.in.mark(readLimit);

        if (this.markCache == null) {
            this.markCache = (byte[]) this.cache.clone();
        } else {
            System.arraycopy(this.cache, 0,
                    this.markCache, 0,
                    this.cacheLimit);
        }
        this.markCacheLimit = this.cacheLimit;
        this.markCachePosition = this.cachePosition;
        this.markBitBuffer = this.bitBuffer;
        this.markBitCount = this.bitCount;
        this.markPositionIsInCache = true;

        this.markBlockSize = this.blockSize;
        this.markCodeLen = this.codeLen;
        this.markCodeTable = this.codeTable;
        this.markCodeTree = this.codeTree;
        this.markOffLenLen = this.offLenLen;
        this.markOffLenTable = this.offLenTable;
        this.markOffLenTree = this.offLenTree;
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
        if (this.markPositionIsInCache) {
            this.cachePosition = this.markCachePosition;
            this.bitBuffer = this.markBitBuffer;
            this.bitCount = this.markBitCount;

            this.blockSize = this.markBlockSize;
            this.codeLen = this.markCodeLen;
            this.codeTable = this.markCodeTable;
            this.codeTree = this.markCodeTree;
            this.offLenLen = this.markOffLenLen;
            this.offLenTable = this.markOffLenTable;
            this.offLenTree = this.markOffLenTree;
        } else if (!this.in.markSupported()) {
            throw new IOException("not support mark()/reset().");
        } else if (this.markCache == null) { //���̏������͖����Ƀ}�[�N����Ă��Ȃ����Ƃ������B�R���X�g���N�^�� markCache �� null �ɐݒ肳���̂𗘗p����B
            throw new IOException("not marked.");
        } else {
            //in �� reset() �ł��Ȃ��ꍇ��
            //�ŏ��̍s�� this.in.reset() ��
            //IOException �𓊂��邱�Ƃ����҂��Ă���B
            this.in.reset();                                                    //throws IOException
            System.arraycopy(this.markCache, 0,
                    this.cache, 0,
                    this.markCacheLimit);
            this.cacheLimit = this.markCacheLimit;
            this.cachePosition = this.markCachePosition;
            this.bitBuffer = this.markBitBuffer;
            this.bitCount = this.markBitCount;

            this.blockSize = this.markBlockSize;
            this.codeLen = this.markCodeLen;
            this.codeTable = this.markCodeTable;
            this.codeTree = this.markCodeTree;
            this.offLenLen = this.markOffLenLen;
            this.offLenTable = this.markOffLenTable;
            this.offLenTree = this.markOffLenTree;
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
     * ���̍Œ�o�C�g���͕K�������ۏႳ��Ă��Ȃ����ɒ��ӂ��邱�ƁB<br>
     *
     * @return �u���b�N���Ȃ��œǂݏo����Œ�o�C�g���B<br>
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     *
     * @see PreLzssDecoder#available()
     */
    public int available() throws IOException {
        int avail = ((this.cacheLimit - this.cachePosition)
                + this.in.available() / this.cache.length * this.cache.length);//throws IOException
        avail += this.bitCount - 32;
        avail = avail / StaticHuffman.LimitLen;
        if (this.blockSize < avail) {
            avail -= 272;
        }
        return Math.max(avail, 0);
    }

    /**
     * ���̃X�g���[������A�g�p���Ă����S�Ă̎������������B
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void close() throws IOException {
        this.in.close();                                                        //throws IOException
        this.in = null;

        this.cache = null;
        this.cacheLimit = 0;
        this.cachePosition = 0;
        this.bitBuffer = 0;
        this.bitCount = 0;

        this.markCache = null;
        this.markCacheLimit = 0;
        this.markCachePosition = 0;
        this.markBitBuffer = 0;
        this.markBitCount = 0;
        this.markPositionIsInCache = false;

        this.blockSize = 0;
        this.codeLen = null;
        this.codeTable = null;
        this.codeTree = null;
        this.offLenLen = null;
        this.offLenTable = null;
        this.offLenTree = null;

        this.markBlockSize = 0;
        this.markCodeLen = null;
        this.markCodeTable = null;
        this.markCodeTree = null;
        this.markOffLenLen = null;
        this.markOffLenTable = null;
        this.markOffLenTree = null;
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
     * ���� PreLh5Decoder ������LZSS�����̃T�C�Y�𓾂�B
     *
     * @return ���� PreLh5Decoder ������LZSS�����̃T�C�Y
     */
    public int getDictionarySize() {
        return this.DictionarySize;
    }

    /**
     * ���� PreLh5Decoder ������LZSS�̍ő��v���𓾂�B
     *
     * @return ���� PreLh5Decoder ������LZSS�̍ő��v��
     */
    public int getMaxMatch() {
        return this.MaxMatch;
    }

    /**
     * ���� PreLh5Decoder ���������k�A�񈳏k��臒l�𓾂�B
     *
     * @return ���� PreLh5Decoder ���������k�A�񈳏k��臒l
     */
    public int getThreshold() {
        return this.Threshold;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  read block head
    //------------------------------------------------------------------
    //  private void readBlockHead()
    //  private int[] readCodeLenLenList()
    //  private int[] readCodeLenList( HuffmanDecoder decoder )
    //  private int[] readOffLenLenList()
    //------------------------------------------------------------------

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
     * @exception NotEnoughBitsException
     *                         �\�����ʌ����Ńf�[�^�ǂ݂��݂�
     *                         ���f���ꂽ���ߗv�����ꂽ�r�b�g��
     *                         �̃f�[�^�������Ȃ������ꍇ
     */
    private void readBlockHead() throws IOException {
        //�u���b�N�T�C�Y�ǂݍ���
        //����ȃf�[�^�̏ꍇ�A���̕����� EndOfStream �ɓ��B����B
        try {
            this.blockSize = this.readBits(16);                               //throws BitDataBrokenException, EOFException, IOException
        } catch (BitDataBrokenException exception) {
            if (exception.getCause() instanceof EOFException) {
                throw (EOFException) exception.getCause();
            } else {
                throw exception;
            }
        }

        //codeLen ���̏���
        int[] codeLenLen = this.readCodeLenLen();                               //throws BitDataBrokenException, EOFException, IOException
        short[] codeLenTable;
        if (null != codeLenLen) {
            codeLenTable = StaticHuffman.createTable(codeLenLen);             //throws BadHuffmanTableException
        } else {
            codeLenTable = new short[]{(short) this.readBits(5)};            //throws BitDataBrokenException EOFException IOException
            codeLenLen = new int[codeLenTable[0] + 1];
        }

        //code ���̏���
        this.codeLen = this.readCodeLen(codeLenTable, codeLenLen);            //throws BitDataBrokenException NotEnoughBitsException EOFException IOException
        if (null != this.codeLen) {
            short[][] tableAndTree =
                    StaticHuffman.createTableAndTree(this.codeLen, this.codeTableBits);//throws BadHuffmanTableException
            this.codeTable = tableAndTree[0];
            this.codeTree = new short[][]{tableAndTree[1], tableAndTree[2]};
        } else {
            int code = this.readBits(9);                                      //throws BitDataBrokenException EOFException IOException
            this.codeLen = new int[256 + this.MaxMatch - this.Threshold + 1];
            this.codeTable = new short[1 << this.codeTableBits];
            for (int i = 0; i < this.codeTable.length; i++) {
                this.codeTable[i] = ((short) ~code);
            }
            this.codeTree = new short[][]{new short[0], new short[0]};
        }

        //offLen ���̏���
        this.offLenLen = this.readOffLenLen();                                  //throws BitDataBrokenException EOFException IOException
        if (null != this.offLenLen) {
            short[][] tableAndTree =
                    StaticHuffman.createTableAndTree(this.offLenLen, this.offLenTableBits);//throws BadHuffmanTableException
            this.offLenTable = tableAndTree[0];
            this.offLenTree = new short[][]{tableAndTree[1], tableAndTree[2]};
        } else {
            int offLen = this.readBits(Bits.len(Bits.len(this.DictionarySize)));//throws BitDataBrokenException EOFException IOException
            this.offLenLen = new int[Bits.len(this.DictionarySize)];
            this.offLenTable = new short[1 << this.offLenTableBits];
            for (int i = 0; i < this.offLenTable.length; i++) {
                this.offLenTable[i] = ((short) ~offLen);
            }
            this.offLenTree = new short[][]{new short[0], new short[0]};
        }
    }

    /**
     * Code�̃n�t�}���������̃��X�g��
     * �n�t�}�������𕜍����邽�߂�
     * �n�t�}���������̃��X�g��ǂ݂��ށB
     *
     * @return �n�t�}���������̃��X�g�B
     *         �������̃��X�g�������ꍇ�� null
     *
     * @exception IOException  ���o�̓G���[�����������ꍇ
     * @exception EOFException EndOfStream�ɒB�����ꍇ
     * @exception BitDataBrokenException
     *                         �\�����ʌ����Ńf�[�^�ǂ݂��݂�
     *                         ���f���ꂽ���ߗv�����ꂽ�r�b�g��
     *                         �̃f�[�^�������Ȃ������ꍇ
     */
    private int[] readCodeLenLen() throws IOException {
        int listlen = this.readBits(5);                                       //throws BitDataBrokenException, EOFException, IOException
        if (0 < listlen) {
            int[] codeLenLen = new int[listlen];
            int index = 0;

            while (index < listlen) {
                int codelenlen = this.readBits(3);                            //throws BitDataBrokenException, EOFException, IOException
                if (codelenlen == 0x07) {
                    while (this.readBoolean()) codelenlen++;                   //throws EOFException, IOException
                }
                codeLenLen[index++] = codelenlen;

                if (index == 3) {
                    index += this.readBits(2);                                //throws BitDataBrokenException, EOFException, IOException
                }
            }
            return codeLenLen;
        } else {
            return null;
        }
    }

    /**
     * Code�̃n�t�}���������̃��X�g�𕜍����Ȃ���ǂ݂���
     *
     * @return �n�t�}���������̃��X�g�B
     *         �������̃��X�g�������ꍇ�� null
     *
     * @exception IOException  ���o�̓G���[�����������ꍇ
     * @exception EOFException EndOfStream�ɒB�����ꍇ
     * @exception BitDataBrokenException
     *                         �\�����ʌ����Ńf�[�^�ǂ݂��݂�
     *                         ���f���ꂽ���ߗv�����ꂽ�r�b�g��
     *                         �̃f�[�^�������Ȃ������ꍇ
     * @exception NotEnouthBitsException
     *                         �\�����ʌ����Ńf�[�^�ǂ݂��݂�
     *                         ���f���ꂽ���ߗv�����ꂽ�r�b�g��
     *                         �̃f�[�^�������Ȃ������ꍇ
     */
    private int[] readCodeLen(short[] codeLenTable, int[] codeLenLen)
            throws IOException {

        final int codeLenTableBits = Bits.len(codeLenTable.length - 1);

        int listlen = this.readBits(9);                                       //throws BitDataBrokenException, EOFException, IOException
        if (0 < listlen) {
            int[] codeLen = new int[listlen];
            int index = 0;

            while (index < listlen) {
                this.fillBitBuffer();
                int bits = (0 < codeLenTableBits
                        ? this.bitBuffer >>> (32 - codeLenTableBits)
                        : 0);
                int codelen = codeLenTable[bits];
                int len = codeLenLen[codelen];
                this.bitBuffer <<= len;
                this.bitCount -= len;

                if (codelen == 0) index++;
                else if (codelen == 1)
                    index += this.readBits(4) + 3;        //throws BitDataBrokenException, EOFException, IOException
                else if (codelen == 2)
                    index += this.readBits(9) + 20;       //throws BitDataBrokenException, EOFException, IOException
                else codeLen[index++] = codelen - 2;
            }
            return codeLen;
        } else {
            return null;
        }
    }

    /**
     * offLen �̃n�t�}���������̃��X�g��ǂ݂���
     *
     * @return �n�t�}���������̃��X�g�B
     *         �������̃��X�g�������ꍇ�� null
     *
     * @exception IOException  ���o�̓G���[�����������ꍇ
     * @exception EOFException EndOfStream�ɒB�����ꍇ
     * @exception BitDataBrokenException
     *                         �\�����ʌ����Ńf�[�^�ǂ݂��݂�
     *                         ���f���ꂽ���ߗv�����ꂽ�r�b�g��
     *                         �̃f�[�^�������Ȃ������ꍇ
     */
    private int[] readOffLenLen() throws IOException {
        int listlen = this.readBits(Bits.len(Bits.len(this.DictionarySize)));//throws BitDataBrokenException, EOFException, IOException
        if (0 < listlen) {
            int[] offLenLen = new int[listlen];
            int index = 0;

            while (index < listlen) {
                int offlenlen = this.readBits(3);                             //throws BitDataBrokenException, EOFException, IOException
                if (offlenlen == 0x07) {
                    while (this.readBoolean()) offlenlen++;                    //throws EOFException, IOException
                }
                offLenLen[index++] = offlenlen;
            }
            return offLenLen;
        } else {
            return null;
        }
    }


    //------------------------------------------------------------------
    //  staff of BitInputStream
    //------------------------------------------------------------------
    //  bit read
    //------------------------------------------------------------------
    //  private boolean readBoolean()
    //  private int readBits( int count )
    //  private int cachedBits()
    //------------------------------------------------------------------

    /**
     * �ڑ����ꂽ���̓X�g���[������ 1�r�b�g�̃f�[�^��
     * �^�U�l�Ƃ��ēǂݍ��ށB<br>
     *
     * @return �ǂݍ��܂ꂽ1�r�b�g�̃f�[�^�� 
     *         1�ł���� true�A0�ł���� false ��Ԃ��B<br>
     *
     * @exception EOFException ����EndOfStream�ɒB���Ă����ꍇ
     * @exception IOException  �ڑ����ꂽ���̓X�g���[����
     *                         ���o�̓G���[�����������ꍇ
     */
    private boolean readBoolean() throws IOException {
        if (0 < this.bitCount) {
            boolean bool = (this.bitBuffer < 0);
            this.bitBuffer <<= 1;
            this.bitCount -= 1;
            return bool;
        } else {
            this.fillBitBuffer();
            boolean bool = (this.bitBuffer < 0);
            this.bitBuffer <<= 1;
            this.bitCount -= 1;
            return bool;
        }
    }

    /**
     * �ڑ����ꂽ���̓X�g���[������ count �r�b�g�̃f�[�^��
     * �ǂݍ��ށB �߂�l�� int�l�ł��鎖���������悤��
     * �ǂݍ��ނ��Ƃ̂ł��� �ő�L���r�b�g���� 32�r�b�g��
     * ���邪�Acount ��32�ȏ�̒l��ݒ肵�Ă��`�F�b�N��
     * �󂯂Ȃ����� ����ȏ�̒l��ݒ肵���ꍇ�� �r�b�g
     * �f�[�^���ǂݎ̂Ă���B<br>
     * ���Ƃ��� readBits( 33 ) �Ƃ����Ƃ��� �܂�1�r�b�g��
     * �f�[�^��ǂݎ̂āA���̌�� 32�r�b�g�̃f�[�^��Ԃ��B<br>
     * �܂� count �� 0�ȉ��̐�����ݒ肵�ČĂяo�����ꍇ�A
     * �f�[�^��ǂݍ��ޓ���𔺂�Ȃ����� �߂�l�� ���0�A
     * EndOfStream �ɒB���Ă��Ă� EOFException ��
     * �����Ȃ��_�ɒ��ӂ��邱�ƁB<br>
     *
     * @param count  �ǂݍ��ރf�[�^�̃r�b�g��
     *
     * @return �ǂݍ��܂ꂽ�r�b�g�f�[�^�B<br>
     *
     * @exception IOException
     *               �ڑ����ꂽ���̓X�g���[����
     *               ���o�̓G���[�����������ꍇ
     * @exception EOFException
     *               ����EndOfStream�ɒB���Ă����ꍇ
     * @exception BitDataBrokenException
     *               �ǂݍ��ݓr���� EndOfStream�ɒB��������
     *               �v�����ꂽ�r�b�g���̃f�[�^�̓ǂݍ���
     *               �Ɏ��s�����ꍇ�B<br>
     */
    private int readBits(int count) throws IOException {
        if (0 < count) {
            if (count <= this.bitCount) {
                int bits = this.bitBuffer >>> (32 - count);
                this.bitBuffer <<= count;
                this.bitCount -= count;
                return bits;
            } else {
                final int requested = count;
                int bits = 0;
                try {
                    this.fillBitBuffer();                                       //throws LocalEOFException IOException
                    while (this.bitCount < count) {
                        count -= this.bitCount;
                        if (count < 32) {
                            bits |= (this.bitBuffer >>> (32 - this.bitCount)) << count;
                        }
                        this.bitBuffer = 0;
                        this.bitCount = 0;
                        this.fillBitBuffer();                                   //throws LocalEOFException IOException
                    }
                    bits |= this.bitBuffer >>> (32 - count);
                    this.bitBuffer <<= count;
                    this.bitCount -= count;
                    return bits;
                } catch (LocalEOFException exception) {
                    if (exception.thrownBy(this) && count < requested) {
                        throw new BitDataBrokenException(exception, bits >>> count, requested - count);
                    } else {
                        throw exception;
                    }
                }
            }
        } else {
            return 0;
        }
    }

    /**
     * ���� BitInputStream ���ɒ~�����Ă���r�b�g���𓾂�B<br>
     *
     * @return ���� BitInputStream ���ɒ~�����Ă���r�b�g���B<br>
     */
    private int cachedBits() {
        return this.bitCount + ((this.cacheLimit - this.cachePosition) << 3);
    }


    //------------------------------------------------------------------
    //  staff of BitInputSteram
    //------------------------------------------------------------------
    //  fill
    //------------------------------------------------------------------
    //  private void fillBitBuffer()
    //  private void fillCache()
    //------------------------------------------------------------------

    /**
     * bitBuffer �Ƀf�[�^�𖞂����B
     * EndOfStream �t�߂������� bitBuffer �ɂ�
     * 25bit �̃f�[�^���m�ۂ���邱�Ƃ�ۏႷ��B
     *
     * @exception IOException       ���o�̓G���[�����������ꍇ
     * @exception LocalEOFException bitBuffer ����̏�Ԃ� EndOfStream �ɒB�����ꍇ
     */
    private void fillBitBuffer() throws IOException {
        if (32 <= this.cachedBits()) {
            if (this.bitCount <= 24) {
                if (this.bitCount <= 16) {
                    if (this.bitCount <= 8) {
                        if (this.bitCount <= 0) {
                            this.bitBuffer = this.cache[this.cachePosition++] << 24;
                            this.bitCount = 8;
                        }
                        this.bitBuffer |= (this.cache[this.cachePosition++] & 0xFF)
                                << (24 - this.bitCount);
                        this.bitCount += 8;
                    }
                    this.bitBuffer |= (this.cache[this.cachePosition++] & 0xFF)
                            << (24 - this.bitCount);
                    this.bitCount += 8;
                }
                this.bitBuffer |= (this.cache[this.cachePosition++] & 0xFF)
                        << (24 - this.bitCount);
                this.bitCount += 8;
            }
        } else if (this.bitCount < 25) {
            if (this.bitCount == 0) {
                this.bitBuffer = 0;
            }

            int count = Math.min((32 - this.bitCount) >> 3,
                    this.cacheLimit - this.cachePosition);
            while (0 < count--) {
                this.bitBuffer |= (this.cache[this.cachePosition++] & 0xFF)
                        << (24 - this.bitCount);
                this.bitCount += 8;
            }
            this.fillCache();                                                   //throws IOException
            if (this.cachePosition < this.cacheLimit) {
                count = Math.min((32 - this.bitCount) >> 3,
                        this.cacheLimit - this.cachePosition);
                while (0 < count--) {
                    this.bitBuffer |= (this.cache[this.cachePosition++] & 0xFF)
                            << (24 - this.bitCount);
                    this.bitCount += 8;
                }
            } else if (this.bitCount <= 0) {
                throw new LocalEOFException(this);
            }
        }
    }

    /**
     * cache ����ɂȂ������� cache �Ƀf�[�^��ǂݍ��ށB
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    private void fillCache() throws IOException {
        this.markPositionIsInCache = false;
        this.cacheLimit = 0;
        this.cachePosition = 0;

        //cache �Ƀf�[�^��ǂݍ���
        int read = 0;
        while (0 <= read && this.cacheLimit < this.cache.length) {
            read = this.in.read(this.cache,
                    this.cacheLimit,
                    this.cache.length - this.cacheLimit);         //throws IOException

            if (0 < read) this.cacheLimit += read;
        }
    }


    //------------------------------------------------------------------
    //  inner classes
    //------------------------------------------------------------------
    //  private static class LocalEOFException
    //------------------------------------------------------------------

    /**
     * BitInputStream ���� EndOfStream �̌��o��
     * EOFException ���g�p����̂͏��X��肪����̂�
     * ���[�J���� EOFException ���`����B
     */
    private static class LocalEOFException extends EOFException {

        //------------------------------------------------------------------
        //  instance field
        //------------------------------------------------------------------
        //  private Object owner
        //------------------------------------------------------------------
        /**
         * ���̗�O�𓊂����I�u�W�F�N�g
         */
        private Object owner;

        //------------------------------------------------------------------
        //  constructor
        //------------------------------------------------------------------
        //  public LocalEOFException()
        //  public LocalEOFException( String message )
        //------------------------------------------------------------------

        /**
         * �R���X�g���N�^�B
         *
         * @param object ���̗�O�𓊂����I�u�W�F�N�g
         */
        public LocalEOFException(Object object) {
            super();
            this.owner = object;
        }

        //------------------------------------------------------------------
        //  original method
        //------------------------------------------------------------------
        //  public boolean thrownBy( Object object )
        //------------------------------------------------------------------

        /**
         * ���̗�O�� object �ɂ���ē�����ꂽ���ǂ����𓾂�B<br>
         *
         * @param object �I�u�W�F�N�g
         *
         * @return ���̗�O�� object�ɂ����
         *         ������ꂽ��O�ł���� true<br>
         *         �Ⴆ�� false<br>
         */
        public boolean thrownBy(Object object) {
            return this.owner == object;
        }
    }
}
//end of PreLh5Decoder.java
