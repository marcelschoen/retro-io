//start of PreLh2Decoder.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * PreLh2Decoder.java
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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

//import exceptions


/**
 * -lh2- �𓀗p PreLzssDecoder�B<br>
 *
 * <pre>
 * -- revision history --
 * $Log: PreLh2Decoder.java,v $
 * Revision 1.1  2002/12/06 00:00:00  dangan
 * [maintenance]
 *     �\�[�X����
 *
 * Revision 1.0  2002/08/05 00:00:00  dangan
 * [bug fix]
 *     available() �̌v�Z���Â������̂��C���B
 * [maintenance]
 *     �\�[�X����
 *
 * </pre>
 *
 * @author $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class PreLh2Decoder implements PreLzssDecoder {


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
    //  private BitInputStream
    //------------------------------------------------------------------
    /**
     * -lh2- �̈��k�f�[�^���������� BitInputStream
     */
    private BitInputStream in;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  huffman tree
    //------------------------------------------------------------------
    //  private DynamicHuffman codeHuffman
    //  private DynamicHuffman offHiHuffman
    //------------------------------------------------------------------
    /**
     * Lzss�񈳏k�f�[�^ 1byte �� Lzss���k�R�[�h�̂�����v����
     * ���邽�߂� ���I�n�t�}����
     */
    private DynamicHuffman codeHuffman;

    /**
     * Lzss���k�R�[�h�̏��7bit�̒l�𓾂邽�߂̓��I�n�t�}����
     */
    private DynamicHuffman offHiHuffman;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  current position
    //------------------------------------------------------------------
    //  private int position
    //  private int nextPosition
    //  private int matchLength
    //------------------------------------------------------------------
    /**
     * (�𓀌�̃f�[�^��)���ݏ����ʒu
     */
    private int position;

    /**
     * ���� addLeaf() ���ׂ� position
     */
    private int nextPosition;

    /**
     * ��v��
     */
    private int matchLength;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  backup for mark method
    //------------------------------------------------------------------
    //  private DynamicHuffman markCodeHuffman
    //  private DynamicHuffman markOffHiHuffman
    //  private int markPosition
    //  private int markNextPosition
    //  private int markMatchLength
    //------------------------------------------------------------------
    /** codeHuffman �̃o�b�N�A�b�v�p */
    private DynamicHuffman markCodeHuffman;

    /** offHiHuffman �̃o�b�N�A�b�v�p */
    private DynamicHuffman markOffHiHuffman;

    /** position �̃o�b�N�A�b�v�p */
    private int markPosition;

    /** nextPosition �̃o�b�N�A�b�v�p */
    private int markNextPosition;

    /** matchLength �̃o�b�N�A�b�v�p */
    private int markMatchLength;


    //------------------------------------------------------------------
    //  constructer
    //------------------------------------------------------------------
    //  private PreLh2Decoder()
    //  public PreLh2Decoder( InputStream in )
    //------------------------------------------------------------------

    /**
     * �f�t�H���g�R���X�g���N�^�B
     * �g�p�s�B
     */
    private PreLh2Decoder() {
    }


    /**
     * -lh2- �𓀗p PreLzssDecoder ���\�z����B<br>
     *
     * @param in ���k�f�[�^������������̓X�g���[��
     */
    public PreLh2Decoder(InputStream in) {
        if (in != null) {
            if (in instanceof BitInputStream) {
                this.in = (BitInputStream) in;
            } else {
                this.in = new BitInputStream(in);
            }
            this.codeHuffman = new DynamicHuffman(PreLh2Decoder.CodeSize);
            this.offHiHuffman = new DynamicHuffman(
                    PreLh2Decoder.DictionarySize >> 6, 1);

            this.position = 0;
            this.nextPosition = 1 << 6;
            this.matchLength = 0;
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
     * -lh2- �ň��k���ꂽ 
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
        final int CodeMax = PreLh2Decoder.CodeSize - 1;

        int node = this.codeHuffman.childNode(DynamicHuffman.ROOT);
        while (0 <= node) {
            node = this.codeHuffman.childNode(node - (in.readBoolean() ? 1 : 0));//throws EOFException,IOException
        }
        int code = ~node;
        this.codeHuffman.update(code);

        if (code < 0x100) {
            this.position++;
        } else {
            if (code == CodeMax) {
                try {
                    code += this.in.readBits(8);
                } catch (BitDataBrokenException exception) {
                    if (exception.getCause() instanceof EOFException)
                        throw (EOFException) exception.getCause();
                }
            }
            this.matchLength = code - 0x100 + PreLh2Decoder.Threshold;
        }
        return code;
    }

    /**
     * -lh2- �ň��k���ꂽ
     * LZSS���k�R�[�h�̂�����v�ʒu��ǂݍ��ށB<br>
     *
     * @return -lh2- �ň��k���ꂽ���k�R�[�h�̂�����v�ʒu
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     * @exception EOFException EndOfStream�ɒB�����ꍇ
     */
    public int readOffset() throws IOException {
        if (this.nextPosition < PreLh2Decoder.DictionarySize) {
            while (this.nextPosition < this.position) {
                this.offHiHuffman.addLeaf(this.nextPosition >> 6);
                this.nextPosition += 64;

                if (PreLh2Decoder.DictionarySize <= this.nextPosition)
                    break;
            }
        }
        this.position += this.matchLength;

        int node = this.offHiHuffman.childNode(DynamicHuffman.ROOT);
        while (0 <= node) {
            node = this.offHiHuffman.childNode(node - (in.readBoolean() ? 1 : 0));//throws EOFException,IOException
        }
        int offHi = ~node;
        this.offHiHuffman.update(offHi);

        return (offHi << 6) | this.in.readBits(6);
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
     * @see PreLzssDecoder#available()
     */
    public void mark(int readLimit) {
        this.in.mark(readLimit * 18 / 8 + 4);
        this.markCodeHuffman = (DynamicHuffman) this.codeHuffman.clone();
        this.markOffHiHuffman = (DynamicHuffman) this.offHiHuffman.clone();
        this.markPosition = this.position;
        this.markNextPosition = this.nextPosition;
        this.markMatchLength = this.matchLength;
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

        this.codeHuffman = (DynamicHuffman) this.markCodeHuffman.clone();
        this.offHiHuffman = (DynamicHuffman) this.markOffHiHuffman.clone();
        this.position = this.markPosition;
        this.nextPosition = this.markNextPosition;
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
        return Math.max(this.in.availableBits() / 18 - 4, 0);                 //throws IOException
    }

    /**
     * ���̃X�g���[������A�g�p���Ă����S�Ă̎������������B
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void close() throws IOException {
        this.in.close();                                                        //throws IOException

        this.in = null;
        this.codeHuffman = null;
        this.offHiHuffman = null;
        this.markCodeHuffman = null;
        this.markOffHiHuffman = null;
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
     * -lh2-�`���� LZSS�����̃T�C�Y�𓾂�B
     *
     * @return -lh2-�`���� LZSS�����̃T�C�Y
     */
    public int getDictionarySize() {
        return PreLh2Decoder.DictionarySize;
    }

    /**
     * -lh2-�`���� LZSS�̍ő��v���𓾂�B
     *
     * @return -lh2-�`���� LZSS�̍ő��v��
     */
    public int getMaxMatch() {
        return PreLh2Decoder.MaxMatch;
    }

    /**
     * -lh2-�`���� LZSS�̈��k�A�񈳏k��臒l�𓾂�B
     *
     * @return -lh2-�`���� LZSS�̈��k�A�񈳏k��臒l
     */
    public int getThreshold() {
        return PreLh2Decoder.Threshold;
    }

}
//end of PreLh2Decoder.java
