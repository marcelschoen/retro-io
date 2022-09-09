//start of PostLh5Encoder.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * PostLh5Encoder.java
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
import jp.gr.java_conf.dangan.io.Bits;

import java.io.IOException;
import java.io.OutputStream;

//import exceptions


/**
 * -lh4-, -lh5-, -lh6-, -lh7- ���k�p PostLzssEncoder�B<br>
 *
 * <pre>
 * -- revision history --
 * $Log: PostLh5Encoder.java,v $
 * Revision 1.4  2002/12/08 00:00:00  dangan
 * [change]
 *     �N���X�� �� PostLh5EncoderCombo ���� PostLh5Encoder �ɕύX�B
 *
 * Revision 1.3  2002/12/06 00:00:00  dangan
 * [maintenance]
 *     �\�[�X����
 *
 * Revision 1.2  2002/12/01 00:00:00  dangan
 * [change]
 *     flush() ����Ȃ������� 
 *     �ڑ����ꂽ OutputStream ��flush() ���Ȃ��悤�ɕύX�B
 *
 * Revision 1.1  2002/12/01 00:00:00  dangan
 * [bug fix] 
 *     writeOutGroup �Ń��[�J���ϐ� offLenFreq ���g�p���Ȃ����
 *     �Ȃ�Ȃ������� this.offLenFreq ���g�p���Ă����B
 * [maintenance]
 *     PostLh5Encoder ����󂯌p�����C���X�^���X�t�B�[���h
 *     buffer, codeFreq, offLenFreq �p�~
 *     �\�[�X����
 *
 * Revision 1.0  2002/07/31 00:00:00  dangan
 * add to version control
 * [improvement]
 *     DivideNum �𓱓����鎖�ɂ���ď�������p�^�[�����̌�����}��B
 * [maintenance]
 *     �\�[�X����
 *     �^�u�p�~
 *     ���C�Z���X���̏C��
 *
 * </pre>
 *
 * @author $Author: dangan $
 * @version $Revision: 1.4 $
 */
public class PostLh5Encoder implements PostLzssEncoder {


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  sink
    //------------------------------------------------------------------
    //  private BitOutputStream out
    //------------------------------------------------------------------
    /**
     * -lh4-, -lh5-, -lh6-, -lh7- �`���̈��k�f�[�^�̏o�͐�� �r�b�g�o�̓X�g���[��
     */
    private BitOutputStream out;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  LZSS parameter
    //------------------------------------------------------------------
    //  private int DictionarySize
    //  private int MaxMatch
    //  private int Threshold
    //  private int DictionarySizeByteLen
    //------------------------------------------------------------------
    /**
     * LZSS�̎����T�C�Y
     */
    private int DictionarySize;

    /**
     * LZSS�̍ő��v��
     */
    private int MaxMatch;

    /**
     * LZSS ���k/�񈳏k ��臒l
     */
    private int Threshold;

    /**
     * �����T�C�Y�������̂ɕK�v�ȃo�C�g��
     */
    private int DictionarySizeByteLen;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  current position
    //------------------------------------------------------------------
    //  private int position
    //  private int flagBit
    //  private int flagPos
    //------------------------------------------------------------------
    /**
     * this.block[ this.currentBlock ] ���̌��ݏ����ʒu
     */
    private int position;

    /**
     * flag �o�C�g���̈��k/�񈳏k�������t���O
     */
    private int flagBit;

    /**
     * this.block[ this.currentBlock ] ���� flag�o�C�g�̈ʒu
     */
    private int flagPos;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  huffman code blocks
    //------------------------------------------------------------------
    //  private int currentBlock
    //  private byte[][] block
    //  private int[] blockSize
    //  private int[][] blockCodeFreq
    //  private int[][] blockOffLenFreq
    //------------------------------------------------------------------
    /**
     * ���ݏ������̃n�t�}���u���b�N�������B
     */
    private int currentBlock;

    /**
     * �n�t�}���R�[�h�i�[�p�o�b�t�@�Q
     */
    private byte[][] block;

    /**
     * �e�u���b�N�� code �f�[�^�̐�
     */
    private int[] blockSize;

    /**
     * �Y������u���b�N�� code �����̕p�x�\�����p�x�\�Q
     */
    private int[][] blockCodeFreq;

    /**
     * �Y������u���b�N�� offLen �����̕p�x�\�����p�x�\�Q
     */
    private int[][] blockOffLenFreq;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  groups of huffman code blocks and patterns of groups
    //------------------------------------------------------------------
    //  private int[][] pattern
    //  private int[][] group
    //------------------------------------------------------------------
    /**
     * �S�u���b�N������̃O���[�v�ɕ�������p�^�[���̔z��B
     */
    private int[][] pattern;

    /**
     * �����u���b�N��g�ݍ��킹���O���[�v�̔z��B
     * this.group[0] �S�u���b�N�����O���[�v��
     * this.group[1] this.group[2] �ɂ� �S�u���b�N����e�X�Ō�ƍŏ��̃u���b�N���������O���[�v��
     * �c�Ƃ����悤�Ƀs���~�b�h��ɍ\�������B
     */
    private int[][] group;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private PostLh5Encoder()
    //  public PostLh5Encoder( OutputStream out )
    //  public PostLh5Encoder( OutputStream out, String method )
    //  public PostLh5Encoder( OutputStream out, String method, 
    //                              int BufferSize )
    //  public PostLh5Encoder( OutputStream out, String method,
    //                              int BlockNum, int BlockSize, int DivideNum )
    //------------------------------------------------------------------

    /**
     * �g�p�s�B
     */
    private PostLh5Encoder() {
    }

    /**
     * -lh5- ���k�p PostLzssEncoder ���\�z����B<br>
     * �o�b�t�@�T�C�Y�ɂ̓f�t�H���g�l���g�p�����B
     *
     * @param out ���k�f�[�^���󂯎�� OutputStream
     */
    public PostLh5Encoder(OutputStream out) {
        this(out, CompressMethod.LH5);
    }

    /**
     * -lh4-, -lh5-, -lh6-, -lh7- ���k�p PostLzssEncoder ���\�z����B<br>
     * �o�b�t�@�T�C�Y�ɂ̓f�t�H���g�l���g�p�����B
     *
     * @param out    ���k�f�[�^���󂯎�� OutputStream
     * @param method ���k�@������������<br>
     *  &emsp;&emsp; CompressMethod.LH4 <br>
     *  &emsp;&emsp; CompressMethod.LH5 <br>
     *  &emsp;&emsp; CompressMethod.LH6 <br>
     *  &emsp;&emsp; CompressMethod.LH7 <br>
     *  &emsp;&emsp; �̉��ꂩ���w�肷��B
     *
     * @exception IllegalArgumentException
     *               method ����L�ȊO�̏ꍇ
     */
    public PostLh5Encoder(OutputStream out,
                          String method) {
        this(out, method, 16384);
    }

    /**
     * -lh4-, -lh5-, -lh6-, -lh7- ���k�p PostLzssEncoder ���\�z����B<br>
     *
     * @param out        ���k�f�[�^���󂯎�� OutputStream
     * @param method     ���k�@������������<br>
     *      &emsp;&emsp; CompressMethod.LH4 <br>
     *      &emsp;&emsp; CompressMethod.LH5 <br>
     *      &emsp;&emsp; CompressMethod.LH6 <br>
     *      &emsp;&emsp; CompressMethod.LH7 <br>
     *      &emsp;&emsp; �̉��ꂩ���w�肷��B
     * @param BufferSize LZSS���k�f�[�^��ޔ����Ă���
     *                   �o�b�t�@�̃T�C�Y
     *
     * @exception IllegalArgumentException <br>
     *      &emsp;&emsp; (1) method ����L�ȊO�̏ꍇ<br>
     *      &emsp;&emsp; (2) BufferSize ������������ꍇ<br>
     *      &emsp;&emsp; �̉��ꂩ
     */
    public PostLh5Encoder(OutputStream out,
                          String method,
                          int BufferSize) {
        this(out, method, 1, BufferSize, 0);
    }

    /**
     * -lh4-, -lh5-, -lh6-, -lh7- ���k�p PostLzssEncoder ���\�z����B<br>
     * 1�� BlockSize�o�C�g �� BlockNum �̃u���b�N��g�ݍ��킹��
     * �ł��o�̓r�b�g���̏��Ȃ��\���ŏo�͂���B
     * �g�ݍ��킹�� �S�u���b�N�� DivideNum + 1 �ɕ������ē�����
     * �S�p�^�[�����������B
     *
     * @param out       ���k�f�[�^���󂯎�� OutputStream
     * @param method    ���k�@������������<br>
     *     &emsp;&emsp; CompressMethod.LH4 <br>
     *     &emsp;&emsp; CompressMethod.LH5 <br>
     *     &emsp;&emsp; CompressMethod.LH6 <br>
     *     &emsp;&emsp; CompressMethod.LH7 <br>
     *     &emsp;&emsp; �̉��ꂩ���w�肷��B
     * @param BlockNum  �u���b�N��
     * @param BlockSize 1�u���b�N�̃o�C�g��
     * @param DivideNum �ő啪����
     *
     * @exception IllegalArgumentException <br>
     *     &emsp;&emsp; (1) CompressMethod ����L�ȊO�̏ꍇ<br>
     *     &emsp;&emsp; (2) BlockNum �� 0�ȉ��̏ꍇ<br>
     *     &emsp;&emsp; (3) BlockSize ������������ꍇ<br>
     *     &emsp;&emsp; (4) DivideNum �� 0�����ł��邩�ABlockNum�ȏ�̏ꍇ<br>
     *     &emsp;&emsp; �̂����ꂩ�B
     */
    public PostLh5Encoder(OutputStream out,
                          String method,
                          int BlockNum,
                          int BlockSize,
                          int DivideNum) {

        if (CompressMethod.LH4.equals(method)
                || CompressMethod.LH5.equals(method)
                || CompressMethod.LH6.equals(method)
                || CompressMethod.LH7.equals(method)) {

            this.DictionarySize = CompressMethod.toDictionarySize(method);
            this.MaxMatch = CompressMethod.toMaxMatch(method);
            this.Threshold = CompressMethod.toThreshold(method);
            this.DictionarySizeByteLen = (Bits.len(this.DictionarySize - 1) + 7) / 8;

            final int MinCapacity = (DictionarySizeByteLen + 1) * 8 + 1;

            if (out != null
                    && 0 < BlockNum
                    && 0 <= DivideNum && DivideNum < BlockNum
                    && MinCapacity <= BlockSize) {

                if (out instanceof BitOutputStream) {
                    this.out = (BitOutputStream) out;
                } else {
                    this.out = new BitOutputStream(out);
                }

                this.currentBlock = 0;
                this.block = new byte[BlockNum][];
                this.blockSize = new int[BlockNum];
                this.blockCodeFreq = new int[BlockNum][];
                this.blockOffLenFreq = new int[BlockNum][];

                int codeFreqSize = 256 + this.MaxMatch - this.Threshold + 1;
                int offLenFreqSize = Bits.len(this.DictionarySize);
                for (int i = 0; i < BlockNum; i++) {
                    this.block[i] = new byte[BlockSize];
                    this.blockCodeFreq[i] = new int[codeFreqSize];
                    this.blockOffLenFreq[i] = new int[offLenFreqSize];
                }

                this.group = PostLh5Encoder.createGroup(BlockNum, DivideNum);
                this.pattern = PostLh5Encoder.createPattern(BlockNum, DivideNum);

                this.position = 0;
                this.flagBit = 0;
                this.flagPos = 0;

            } else if (out == null) {
                throw new NullPointerException("out");
            } else if (BlockNum <= 0) {
                throw new IllegalArgumentException("BlockNum too small. BlockNum must be 1 or more.");
            } else if (DivideNum < 0 || BlockNum <= DivideNum) {
                throw new IllegalArgumentException("DivideNum out of bounds( 0 to BlockNum - 1(" + (BlockNum - 1) + ") ).");
            } else {
                throw new IllegalArgumentException("BlockSize too small. BlockSize must be larger than " + MinCapacity);
            }


        } else if (method == null) {
            throw new NullPointerException("method");
        } else {
            throw new IllegalArgumentException("Unknown compress method. " + method);
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
     * arrays �̒�����Aindexes �Ŏw�肳�ꂽ�z���A������B
     *
     * @param indexes arrays���̑����Ώۂ̔z��������Y�����̕\
     * @param arrays  �����Ώۂ̔z����܂񂾃��X�g
     */
    private static int[] margeArrays(int[] indexes, int[][] arrays) {
        if (1 < indexes.length) {
            int[] array = new int[arrays[0].length];

            for (int i = 0; i < indexes.length; i++) {
                int[] src = arrays[indexes[i]];

                for (int j = 0; j < src.length; j++) {
                    array[j] += src[j];
                }
            }
            return array;
        } else {
            return arrays[indexes[0]];
        }
    }

    /**
     * codeLen �����������O�X�ƃn�t�}���ŕ��������邽�߂̕p�x�\���쐬����B
     * �쐬����p�x�\��
     * codeLenFreq[0]�ɂ͗v�f��0�̗v�f��1�����ēǂݔ�΂������w������
     * codeLenFreq[1]�ɂ͗v�f��0�̗v�f��3�`18�����āA����5bit�̃f�[�^���݂�
     * ���̒����̃f�[�^��ǂݔ�΂������w������
     * codeLenFreq[2]�ɂ͗v�f��0�̗v�f��20�ȏ゠���āA����9bit�̃f�[�^���݂�
     * ���̒����̃f�[�^��ǂݔ�΂������w������
     * �Ƃ�������ȈӖ������v�f���܂܂��B
     * �]���̕p�x�� +2���ꂽ�ʒu�ɂ��ꂼ��z�u�����B
     *
     * @param codeLen codeFreq �̃n�t�}���������̃��X�g
     *
     * @return codeLen �̕p�x�\
     */
    private static int[] createCodeLenFreq(int[] codeLen) {
        int[] codeLenFreq = new int[StaticHuffman.LimitLen + 3];

        int end = codeLen.length;
        while (0 < end && codeLen[end - 1] == 0) {
            end--;
        }

        int index = 0;
        while (index < end) {
            int len = codeLen[index++];

            if (0 < len) {
                codeLenFreq[len + 2]++;
            } else {
                int count = 1;
                while (codeLen[index] == 0 && index < end) {
                    count++;
                    index++;
                }

                if (count <= 2) {
                    codeLenFreq[0] += count;
                } else if (count <= 18) {
                    codeLenFreq[1]++;
                } else if (count == 19) {
                    codeLenFreq[0]++;
                    codeLenFreq[1]++;
                } else {
                    codeLenFreq[2]++;
                }
            }
        }
        return codeLenFreq;
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
     * �w�肳�ꂽ�p�x���Ńn�t�}��������
     * �o�͂����ꍇ�̃r�b�g���𓾂�B
     *
     * @param DictionarySize LZSS�����T�C�Y
     * @param codeFreq       �R�[�h���̕p�x���
     * @param offLenFreq     �I�t�Z�b�g���̒����̕p�x���
     *
     * @return ���̕p�x���Ńn�t�}���������o�͂����ꍇ�̃r�b�g���𓾂�B
     */
    private static int calcHuffmanCodeLength(int DictionarySize,
                                             int[] codeFreq,
                                             int[] offLenFreq) {

        //------------------------------------------------------------------
        //  ������
        int length = 0;
        int[] codeLen, codeCode, offLenLen;
        try {
            codeLen = StaticHuffman.FreqListToLenList(codeFreq);
            codeCode = StaticHuffman.LenListToCodeList(codeLen);
            offLenLen = StaticHuffman.FreqListToLenList(offLenFreq);
        } catch (BadHuffmanTableException exception) { //�������Ȃ�
            throw new Error("caught the BadHuffmanTableException which should be never thrown.");
        }

        //------------------------------------------------------------------
        //  code ���̃n�t�}���p�x�\�̒������Z�o����B
        length += 16;
        if (2 <= PostLh5Encoder.countNoZeroElement(codeFreq)) {
            int[] codeLenFreq = PostLh5Encoder.createCodeLenFreq(codeLen);
            int[] codeLenLen = StaticHuffman.FreqListToLenList(codeLenFreq);
            if (2 <= PostLh5Encoder.countNoZeroElement(codeLenFreq)) {
                length += PostLh5Encoder.calcCodeLenLen(codeLenLen);
            } else {
                length += 5;
                length += 5;
            }
            length += PostLh5Encoder.calcCodeLen(codeLen, codeLenLen);
        } else {
            length += 10;
            length += 18;
        }

        //------------------------------------------------------------------
        //  offLen ���̃n�t�}���p�x�\�̒������Z�o����B
        if (2 <= PostLh5Encoder.countNoZeroElement(offLenFreq)) {
            length += PostLh5Encoder.calcOffLenLen(DictionarySize, offLenLen);
        } else {
            int len = Bits.len(Bits.len(DictionarySize));
            length += len;
            length += len;
        }

        //------------------------------------------------------------------
        //  LZSS���k��̃f�[�^������Ƀn�t�}�������������������Z�o����B
        for (int i = 0; i < codeFreq.length; i++) {
            length += codeFreq[i] * codeLen[i];
        }
        for (int i = 0; i < offLenFreq.length; i++) {
            length += offLenFreq[i] * (offLenLen[i] + i - 1);
        }
        return length;
    }

    /**
     * �w�肵���n�t�}���������̕\���o�͂����ꍇ�̃r�b�g���𓾂�B
     *
     * @param codeLenLen �R�[�h���̃n�t�}����������
     *                   ����Ƀn�t�}���������������̂̕\
     *
     * @return �w�肵���n�t�}���������̕\���o�͂����ꍇ�̃r�b�g��
     */
    private static int calcCodeLenLen(int[] codeLenLen) {
        int length = 0;
        int end = codeLenLen.length;
        while (0 < end && codeLenLen[end - 1] == 0) {
            end--;
        }

        length += 5;

        int index = 0;
        while (index < end) {
            int len = codeLenLen[index++];
            if (len <= 6) length += len;
            else length += len - 3;

            if (index == 3) {
                while (codeLenLen[index] == 0 && index < 6) {
                    index++;
                }
                length += 2;
            }
        }
        return length;
    }

    /**
     * �w�肵���n�t�}���������̕\���o�͂����ꍇ�̃r�b�g���𓾂�B
     *
     * @param codeLen    �R�[�h���̃n�t�}���������̕\
     * @param codeLenLen �R�[�h���̃n�t�}����������
     *                   ����Ƀn�t�}���������������̂̕\
     *
     * @return �w�肵���n�t�}���������̕\���o�͂����ꍇ�̃r�b�g��
     */
    private static int calcCodeLen(int[] codeLen,
                                   int[] codeLenLen) {
        int length = 0;
        int end = codeLen.length;
        while (0 < end && codeLen[end - 1] == 0) {
            end--;
        }

        length += 9;

        int index = 0;
        while (index < end) {
            int len = codeLen[index++];

            if (0 < len) {
                length += codeLenLen[len + 2];
            } else {
                int count = 1;
                while (codeLen[index] == 0 && index < end) {
                    count++;
                    index++;
                }

                if (count <= 2) {
                    for (int i = 0; i < count; i++)
                        length += codeLenLen[0];
                } else if (count <= 18) {
                    length += codeLenLen[1];
                    length += 4;
                } else if (count == 19) {
                    length += codeLenLen[0];
                    length += codeLenLen[1];
                    length += 4;
                } else {
                    length += codeLenLen[2];
                    length += 9;
                }
            }
        }
        return length;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  write huffman code
    //------------------------------------------------------------------
    //  private void writeOut()
    //  private void writeOutBestPattern()
    //  private void writeOutGroup( int[] group )
    //------------------------------------------------------------------

    /**
     * �w�肵���n�t�}���������̕\���o�͂����ꍇ�̃r�b�g���𓾂�B
     *
     * @param DictionarySize LZSS�����T�C�Y
     * @param offLenLen      �I�t�Z�b�g���̒����̃n�t�}���������̕\
     *
     * @return �w�肵���n�t�}���������̕\���o�͂����ꍇ�̃r�b�g��
     */
    private static int calcOffLenLen(int DictionarySize,
                                     int[] offLenLen) {
        int length = 0;
        int end = offLenLen.length;
        while (0 < end && offLenLen[end - 1] == 0) {
            end--;
        }

        length += Bits.len(Bits.len(DictionarySize));

        int index = 0;
        while (index < end) {
            int len = offLenLen[index++];
            if (len <= 6) length += 3;
            else length += len - 3;
        }
        return length;
    }

    /**
     * BlockNum�̃u���b�N��A�������u���b�N��
     * �O���[�v���������̂̃��X�g��Ԃ��B
     * <pre>
     * group = new int[]{ 0,1,2 }
     * </pre>
     * �̂悤�ȏꍇ
     * block[0] �� block[1] �� block[2]
     * ���琬��O���[�v�ł��邱�Ƃ������B
     * �܂��O���[�v��
     * group[0] �͑S�u���b�N���琬��O���[�v�A
     * group[1] �� group[2] �͂��ꂼ��S�u���b�N����
     * �Ō�̃u���b�N�ƍŏ��̃u���b�N�����������́A
     * �Ƃ����悤�� �s���~�b�h��ɋK���������Đ�������A
     * createPattern �͂��̋K�����𗘗p���邽��
     * ���̃��\�b�h�����ς���ꍇ�͒��ӂ��邱�ƁB
     * �܂��A�g�p���Ȃ� group �ɂ� null �������Ă���̂Œ��ӂ��邱�ƁB
     *
     * @param BlockNum  �u���b�N�̌�
     * @param DivideNum �ő啪����
     *
     * @reutrn �������ꂽ�O���[�v�̃��X�g
     */
    private static int[][] createGroup(int BlockNum, int DivideNum) {
        int[][] group = new int[(BlockNum + 1) * BlockNum / 2][];

        if (DivideNum == 0) {
            //------------------------------------------------------------------
            //  �S�u���b�N�����O���[�v�̂ݐ���
            group[0] = new int[BlockNum];
            for (int i = 0; i < BlockNum; i++) {
                group[0][i] = i;
            }
        } else if (2 < BlockNum && DivideNum == 1) {
            //------------------------------------------------------------------
            //  ���T�C�Y�̃O���[�v�̂����ŏ��̂��̂ƍŌ�̂��̂��������B
            int index = 0;
            for (int size = BlockNum; 0 < size; size--) {
                group[index] = new int[size];
                for (int i = 0; i < size; i++) {
                    group[index][i] = i;
                }
                if (size < BlockNum) {
                    index += BlockNum - size;
                    group[index] = new int[size];
                    for (int i = 0; i < size; i++) {
                        group[index][i] = i + BlockNum - size;
                    }
                }
                index++;
            }
        } else {
            //------------------------------------------------------------------
            //  �S�O���[�v�𐶐��B
            int index = 0;
            for (int size = BlockNum; 0 < size; size--) {
                for (int start = 0; size + start <= BlockNum; start++) {
                    group[index] = new int[size];

                    for (int i = 0; i < size; i++) {
                        group[index][i] = start + i;
                    }
                    index++;
                }
            }
        }
        return group;
    }

    /**
     * BlockNum�̃u���b�N���ő� DivideNum + 1�̗̈��
     * ���������Ƃ��� �p�^�[���̕\�𐶐�����B
     * 1�̃p�^�[���� createGroup �Ő��������
     * �O���[�v�z��ւ̓Y���̗񋓂Ŏ������B
     * <pre>
     * pattern = new int[]{ 1,3 };
     * </pre>
     * �̂悤�� �p�^�[���� group[1] �� group[3] �̊Ԃ�
     * �������ꂽ���Ƃ������B
     *
     * @param BlockNum  �u���b�N�̌�
     * @param DivideNum �ő啪����
     *
     * @return �������ꂽ�p�^�[���̃��X�g
     */
    private static int[][] createPattern(int BlockNum, int DivideNum) {
        int index = 0;
        int patternNum = PostLh5Encoder.calcPatternNum(BlockNum, DivideNum);
        int[][] pattern = new int[patternNum][];

        for (int div = 0; div < Math.min(BlockNum, DivideNum + 1); div++) {
            //�����ʒu��ێ�����z��B
            //�z����̒l�́A�Ⴆ�� 0�̏ꍇ�� Block[0] �� Block[1] �̊Ԃŕ������邱�Ƃ��Ӗ�����B
            int[] divPos = new int[div];
            for (int i = 0; i < divPos.length; i++) {
                divPos[i] = i;
            }

            //���� �������̃p�^�[���𐶐����郋�[�v
            //more �� ���̕������ŁA�܂��p�^�[���������ł��鎖�������B
            boolean more;
            do {
                pattern[index] = new int[div + 1];

                int start = 0;
                for (int i = 0; i < divPos.length; i++) {
                    int len = (divPos[i] - start) + 1;
                    int num = BlockNum - len;
                    pattern[index][i] = (num + 1) * num / 2 + start;
                    start += len;
                }
                int num = BlockNum - (BlockNum - start);
                pattern[index][divPos.length] = (num + 1) * num / 2 + start;
                index++;

                //�����ʒu���ړ�����B�����ʒu���ړ��ł���΁A
                //���̕������ł܂��o�͂ł���p�^�[��������Ɣ��f�ł���B
                more = false;
                int move = divPos.length - 1;
                int range = BlockNum - 2;
                while (0 <= move && !more) {
                    if (divPos[move] < range) {
                        divPos[move]++;
                        if (move < divPos.length - 1) {
                            for (int i = move; i < divPos.length - 1; i++)
                                divPos[i + 1] = divPos[i] + 1;
                        }
                        more = true;
                    }
                    range = divPos[move] - 1;
                    move--;
                }
            } while (more);
        }
        return pattern;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  write out huffman list
    //------------------------------------------------------------------
    //  private void writeCodeLenLen( int[] codeLenLen )
    //  private void writeCodeLen( int[] codeLen,
    //           int[] codeLenLen, int[] codeLenCode )
    //  private void writeOffLenLen( int[] offLenLen )
    //------------------------------------------------------------------

    /**
     * BlockNum �̃u���b�N��
     * �ő� DivideNum + 1 �ɘA�������̈�ɕ��������ꍇ
     * ���p�^�[���ł��邩�𓾂�B
     *
     * @param BlockNum  �u���b�N�̌�
     * @param DivideNum ������
     *
     * @return �p�^�[�����B
     */
    private static int calcPatternNum(int BlockNum, int DivideNum) {
        int patternNum = 0;
        for (int div = 0; div <= DivideNum; div++) {
            int count = (div <= (BlockNum / 2) ? div : BlockNum - 1 - div);

            int numerator = 1;
            for (int i = 1; i <= count; i++) {
                numerator *= (BlockNum - i);
            }

            int denominator = 1;
            for (int i = 1; i <= count; i++) {
                denominator *= i;
            }

            patternNum += numerator / denominator;
        }
        return patternNum;
    }

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
        int need = ((0x100 <= code ? this.DictionarySizeByteLen + 1 : 1)
                + (this.flagBit == 0 ? 1 : 0));

        if (this.block[this.currentBlock].length - this.position < need
                || 65535 <= this.blockSize[this.currentBlock]) {

            this.currentBlock++;
            if (this.block.length <= this.currentBlock) {
                this.writeOut();
            } else {
                this.position = 0;
            }

            this.flagBit = 0x80;
            this.flagPos = this.position++;
            this.block[this.currentBlock][this.flagPos] = 0;
        } else if (this.flagBit == 0) {
            this.flagBit = 0x80;
            this.flagPos = this.position++;
            this.block[this.currentBlock][this.flagPos] = 0;
        }

        //�f�[�^�i�[
        this.block[this.currentBlock][this.position++] = (byte) code;

        //���1�r�b�g���t���O�Ƃ��Ċi�[
        if (0x100 <= code) {
            this.block[this.currentBlock][this.flagPos] |= this.flagBit;
        }
        this.flagBit >>= 1;

        //�p�x�\�X�V
        this.blockCodeFreq[this.currentBlock][code]++;

        //�u���b�N�T�C�Y�X�V
        this.blockSize[this.currentBlock]++;
    }

    /**
     * LZSS �ň��k���ꂽ���k�R�[�h�̂�����v�ʒu���������ށB<br>
     *
     * @param offset LZSS �ň��k���ꂽ���k�R�[�h�̂�����v�ʒu
     */
    public void writeOffset(int offset) {
        //�f�[�^�i�[
        int shift = (this.DictionarySizeByteLen - 1) << 3;
        while (0 <= shift) {
            this.block[this.currentBlock][this.position++] = (byte) (offset >> shift);
            shift -= 8;
        }

        //�p�x�\�X�V
        this.blockOffLenFreq[this.currentBlock][Bits.len(offset)]++;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  staff of huffman encoder
    //------------------------------------------------------------------
    //  private static int countNoZeroElement( int[] array )
    //  private static int getNoZeroElementIndex( int[] array )
    //  private static int[] margeArrays( int[] indexes, int[][] arrays )
    //  private static int[] createCodeLenFreq( int[] CodeLenList )
    //------------------------------------------------------------------

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

    /**
     * ���̏o�̓X�g���[���ƁA�ڑ����ꂽ�o�̓X�g���[������A
     * �g�p���Ă������\�[�X���J������B<br>
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void close() throws IOException {
        this.writeOut();                                                        //throws IOException
        this.out.close();                                                       //throws IOException

        this.out = null;
        this.block = null;
        this.blockCodeFreq = null;
        this.blockOffLenFreq = null;
        this.group = null;
        this.pattern = null;
    }

    /**
     * ���� PostLh5Encoder ������LZSS�����̃T�C�Y�𓾂�B
     *
     * @return ���� PostLh5Encoder ������LZSS�����̃T�C�Y
     */
    public int getDictionarySize() {
        return this.DictionarySize;
    }

    /**
     * ���� PostLh5Encoder ������LZSS�̍Œ���v���𓾂�B
     *
     * @return ���� PostLh5Encoder ������LZSS�̍ő��v��
     */
    public int getMaxMatch() {
        return this.MaxMatch;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  calc the langth of encoded data
    //------------------------------------------------------------------
    //  private static int calcHuffmanCodeLength( int DictionarySize, 
    //                            int[] CodeFreq, int[] OffLenFreq )
    //  private static int calcCodeLenLen( int[] codeLenLen )
    //  private static int calcCodeLen( int[] codeLen, int[] codeLenLen )
    //  private static int calcOffLenLen( int DictionarySize, int[] offLenLen )
    //------------------------------------------------------------------

    /**
     * ���� PostLh5Encoder ������LZSS�̈��k�A�񈳏k��臒l�𓾂�B
     *
     * @return ���� PostLh5Encoder ������LZSS�̈��k�A�񈳏k��臒l
     */
    public int getThreshold() {
        return this.Threshold;
    }

    /**
     * �o�b�t�@�����O���ꂽ�S�Ẵf�[�^�� this.out �ɏo�͂���B<br>
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    private void writeOut() throws IOException {
        if (1 < this.block.length) {
            this.writeOutBestPattern();
        } else {
            this.writeOutGroup(new int[]{0});
            this.currentBlock = 0;
        }

        this.position = 0;
        this.flagBit = 0;
    }

    /**
     * �o�b�t�@�����O���ꂽ�S�Ẵf�[�^���ŗǂ̍\���� this.out �ɏo�͂���B<br>
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    private void writeOutBestPattern() throws IOException {
        int[] bestPattern = null;
        int[] groupHuffLen = new int[this.group.length];

        //------------------------------------------------------------------
        //  group ���o�͂����Ƃ��� bit �������߂�B
        for (int i = 0; i < this.group.length; i++) {
            if (this.group != null) {
                int blockSize = 0;
                for (int j = 0; j < this.group[i].length; j++) {
                    blockSize += this.blockSize[this.group[i][j]];
                }
                if (0 < blockSize && blockSize < 65536) {
                    groupHuffLen[i] =
                            PostLh5Encoder.calcHuffmanCodeLength(
                                    this.DictionarySize,
                                    PostLh5Encoder.margeArrays(this.group[i], this.blockCodeFreq),
                                    PostLh5Encoder.margeArrays(this.group[i], this.blockOffLenFreq));
                } else if (0 == blockSize) {
                    groupHuffLen[i] = 0;
                } else {
                    groupHuffLen[i] = -1;
                }
            } else {
                groupHuffLen[i] = -1;
            }
        }

        //------------------------------------------------------------------
        //  �o�� bit �����ŏ��ƂȂ� pattern �𑍓���ŋ��߂�B
        int smallest = Integer.MAX_VALUE;
        for (int i = 0; i < this.pattern.length; i++) {
            int length = 0;

            for (int j = 0; j < this.pattern[i].length; j++) {
                if (0 <= groupHuffLen[this.pattern[i][j]]) {
                    length += groupHuffLen[this.pattern[i][j]];
                } else {
                    length = Integer.MAX_VALUE;
                    break;
                }
            }
            if (length < smallest) {
                bestPattern = this.pattern[i];
                smallest = length;
            }
        }

        //------------------------------------------------------------------
        //  �ł��o�� bit ���̏��Ȃ��p�^�[���ŏo��
        //  �ǂ� �p�^�[�� ���u���b�N�T�C�Y�� 65536 �ȏ��
        //  �O���[�v�����ꍇ�̓u���b�N�P�ʂŏo�́B
        if (bestPattern != null) {
            for (int i = 0; i < bestPattern.length; i++) {
                this.writeOutGroup(this.group[bestPattern[i]]);             //throws IOException
            }
        } else {
            for (int i = 0; i < this.block.length; i++) {
                this.writeOutGroup(new int[]{i});
            }
        }

        this.currentBlock = 0;
    }

    /**
     * group �Ŏw�肳�ꂽ �u���b�N�Q���n�t�}������������ this.out �ɏo�͂���B<br>
     *
     * @param group �o�͂���u���b�N�ԍ������z��
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    private void writeOutGroup(int[] group) throws IOException {
        int[] codeFreq = PostLh5Encoder.margeArrays(group, this.blockCodeFreq);
        int[] offLenFreq = PostLh5Encoder.margeArrays(group, this.blockOffLenFreq);

        int blockSize = 0;
        for (int i = 0; i < group.length; i++) {
            blockSize += this.blockSize[group[i]];
        }

        if (0 < blockSize) {
            //------------------------------------------------------------------
            //  �u���b�N�T�C�Y�o��
            this.out.writeBits(16, blockSize);

            //------------------------------------------------------------------
            //  �n�t�}�������\����
            int[] codeLen = StaticHuffman.FreqListToLenList(codeFreq);
            int[] codeCode = StaticHuffman.LenListToCodeList(codeLen);
            int[] offLenLen = StaticHuffman.FreqListToLenList(offLenFreq);
            int[] offLenCode = StaticHuffman.LenListToCodeList(offLenLen);


            //------------------------------------------------------------------
            //  code ���̃n�t�}�������\�o��
            if (2 <= PostLh5Encoder.countNoZeroElement(codeFreq)) {
                int[] codeLenFreq = PostLh5Encoder.createCodeLenFreq(codeLen);
                int[] codeLenLen = StaticHuffman.FreqListToLenList(codeLenFreq);
                int[] codeLenCode = StaticHuffman.LenListToCodeList(codeLenLen);

                if (2 <= PostLh5Encoder.countNoZeroElement(codeLenFreq)) {
                    this.writeCodeLenLen(codeLenLen);                         //throws IOException
                } else {
                    this.out.writeBits(5, 0);                                 //throws IOException
                    this.out.writeBits(5,
                            PostLh5Encoder.getNoZeroElementIndex(codeLenFreq));//throws IOException
                }
                this.writeCodeLen(codeLen, codeLenLen, codeLenCode);          //throws IOException
            } else {
                this.out.writeBits(10, 0);                                    //throws IOException
                this.out.writeBits(18,
                        PostLh5Encoder.getNoZeroElementIndex(codeFreq));//throws IOException
            }

            //------------------------------------------------------------------
            //  offLen ���̃n�t�}�������\�o��
            if (2 <= PostLh5Encoder.countNoZeroElement(offLenFreq)) {
                this.writeOffLenLen(offLenLen);                               //throws IOException
            } else {
                int len = Bits.len(Bits.len(this.DictionarySize));
                this.out.writeBits(len, 0);                                   //throws IOException
                this.out.writeBits(len,
                        PostLh5Encoder.getNoZeroElementIndex(offLenFreq));//throws IOException
            }


            //------------------------------------------------------------------
            //  �n�t�}�������o��
            for (int i = 0; i < group.length; i++) {
                this.position = 0;
                this.flagBit = 0;
                byte[] buffer = this.block[group[i]];

                for (int j = 0; j < this.blockSize[group[i]]; j++) {
                    if (this.flagBit == 0) {
                        this.flagBit = 0x80;
                        this.flagPos = this.position++;
                    }
                    if (0 == (buffer[this.flagPos] & this.flagBit)) {
                        int code = buffer[this.position++] & 0xFF;
                        this.out.writeBits(codeLen[code], codeCode[code]);    //throws IOException
                    } else {
                        int code = (buffer[this.position++] & 0xFF) | 0x100;
                        int offset = 0;
                        for (int k = 0; k < this.DictionarySizeByteLen; k++) {
                            offset = (offset << 8) | (buffer[this.position++] & 0xFF);
                        }
                        int offlen = Bits.len(offset);
                        this.out.writeBits(codeLen[code], codeCode[code]);   //throws IOException
                        this.out.writeBits(offLenLen[offlen], offLenCode[offlen]); //throws IOException
                        if (1 < offlen) this.out.writeBits(offlen - 1, offset);  //throws IOException
                    }
                    this.flagBit >>= 1;
                }
            }

            //------------------------------------------------------------------
            //  ���̃u���b�N�̂��߂̏���
            for (int i = 0; i < group.length; i++) {
                this.blockSize[group[i]] = 0;

                codeFreq = this.blockCodeFreq[group[i]];
                for (int j = 0; j < codeFreq.length; j++) {
                    codeFreq[j] = 0;
                }

                offLenFreq = this.blockOffLenFreq[group[i]];
                for (int j = 0; j < offLenFreq.length; j++) {
                    offLenFreq[j] = 0;
                }
            }
        }//if( 0 < blockSize )
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  create group and pattern
    //------------------------------------------------------------------
    //  private static int[][] createGroup( int BlockNum, int DivideNum )
    //  private static int[][] createPattern( int BlockNum, int DivideNum )
    //  private static int calcPatternNum( int BlockNum, int DivideNum )
    //------------------------------------------------------------------

    /**
     * codeLen �� �n�t�}���������̃��X�g�������o���B
     *
     * @param codeLenLen codeLenFreq �̃n�t�}���������̃��X�g
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    private void writeCodeLenLen(int[] codeLenLen) throws IOException {
        int end = codeLenLen.length;
        while (0 < end && codeLenLen[end - 1] == 0) {
            end--;
        }

        this.out.writeBits(5, end);                                           //throws IOException
        int index = 0;
        while (index < end) {
            int len = codeLenLen[index++];
            if (len <= 6) this.out.writeBits(3, len);                        //throws IOException
            else this.out.writeBits(len - 3, (1 << (len - 3)) - 2);//throws IOException

            if (index == 3) {
                while (codeLenLen[index] == 0 && index < 6) {
                    index++;
                }
                this.out.writeBits(2, (index - 3) & 0x03);                  //throws IOException
            }
        }
    }

    /**
     * code ���̃n�t�}���������̃��X�g��
     * �n�t�}���ƃ��������O�X�ŕ��������Ȃ��珑���o���B
     *
     * @param codeLen     codeFreq �̃n�t�}���������̃��X�g
     * @param codeLenLen  codeLenFreq �̃n�t�}���������̃��X�g
     * @param codeLenCode codeLenFreq �̃n�t�}�������̃��X�g
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    private void writeCodeLen(int[] codeLen,
                              int[] codeLenLen,
                              int[] codeLenCode) throws IOException {
        int end = codeLen.length;
        while (0 < end && codeLen[end - 1] == 0) {
            end--;
        }

        this.out.writeBits(9, end);                                           //throws IOException
        int index = 0;
        while (index < end) {
            int len = codeLen[index++];

            if (0 < len) {
                this.out.writeBits(codeLenLen[len + 2], codeLenCode[len + 2]);//throws IOException
            } else {
                int count = 1;
                while (codeLen[index] == 0 && index < end) {
                    count++;
                    index++;
                }

                if (count <= 2) {
                    for (int i = 0; i < count; i++)
                        this.out.writeBits(codeLenLen[0], codeLenCode[0]);      //throws IOException
                } else if (count <= 18) {
                    this.out.writeBits(codeLenLen[1], codeLenCode[1]);        //throws IOException
                    this.out.writeBits(4, count - 3);                         //throws IOException
                } else if (count == 19) {
                    this.out.writeBits(codeLenLen[0], codeLenCode[0]);        //throws IOException
                    this.out.writeBits(codeLenLen[1], codeLenCode[1]);        //throws IOException
                    this.out.writeBits(4, 0x0F);                              //throws IOException
                } else {
                    this.out.writeBits(codeLenLen[2], codeLenCode[2]);        //throws IOException
                    this.out.writeBits(9, count - 20);                        //throws IOException
                }
            }
        }
    }

    /**
     * offLen �̃n�t�}���������̃��X�g�������o��
     *
     * @param offLenLen offLenFreq �̃n�t�}���������̃��X�g
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    private void writeOffLenLen(int[] offLenLen) throws IOException {
        int end = offLenLen.length;
        while (0 < end && offLenLen[end - 1] == 0) {
            end--;
        }

        int len = Bits.len(Bits.len(this.DictionarySize));
        this.out.writeBits(len, end);                                         //throws IOException
        int index = 0;
        while (index < end) {
            len = offLenLen[index++];
            if (len <= 6) this.out.writeBits(3, len);                         //throws IOException
            else this.out.writeBits(len - 3, (1 << (len - 3)) - 2);//throws IOException
        }
    }

}
//end of PostLh5Encoder.java
