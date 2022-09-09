//start of PostLh2Encoder.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * PostLh2Encoder.java
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
 * -lh2- ���k�p PostLzssEncoder�B <br>
 *
 * <pre>
 * -- revision history --
 * $Log: PostLh2Encoder.java,v $
 * Revision 1.1  2002/12/01 00:00:00  dangan
 * [maintenance]
 *     �\�[�X����
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
 * @version $Revision: 1.1 $
 */
public class PostLh2Encoder implements PostLzssEncoder {

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
    //  sink
    //------------------------------------------------------------------
    //  private BitOutputStream out
    //------------------------------------------------------------------
    /**
     * -lh2- �`���̈��k�f�[�^�̏o�͐�� �r�b�g�o�̓X�g���[��
     */
    private BitOutputStream out;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  dynamic huffman tree
    //------------------------------------------------------------------
    //  private DynamicHuffman codeHuffman
    //  private DynamicHuffman offHiHuffman
    //------------------------------------------------------------------
    /**
     * code�����k�p�K���I�n�t�}����
     */
    private DynamicHuffman codeHuffman;

    /**
     * offHi�����k�p�K���I�n�t�}����
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
     * �o�͂����I���W�i���̃T�C�Y���J�E���g����
     */
    private int position;

    /**
     * ���ɗt�������铮�������ʒu
     */
    private int nextPosition;

    /**
     * ���ݏ������̈�v��
     */
    private int matchLength;


    //------------------------------------------------------------------
    //  constructer
    //------------------------------------------------------------------
    //  private PostLh2Encoder()
    //  public PostLh2Encoder( OutputStream out )
    //------------------------------------------------------------------

    /**
     * �f�t�H���g�R���X�g���N�^�B
     * �g�p�s�B
     */
    private PostLh2Encoder() {
    }

    /**
     * -lh2- ���k�p PostLzssEncoder ���\�z����B
     *
     * @param out ���k�f�[�^���󂯎��o�̓X�g���[��
     */
    public PostLh2Encoder(OutputStream out) {
        if (out != null) {
            if (out instanceof BitOutputStream) {
                this.out = (BitOutputStream) out;
            } else {
                this.out = new BitOutputStream(out);
            }
            this.codeHuffman = new DynamicHuffman(PostLh2Encoder.CodeSize);
            this.offHiHuffman = new DynamicHuffman(
                    PostLh2Encoder.DictionarySize >> 6, 1);
            this.position = 0;
            this.nextPosition = 1 << 6;
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
        final int CodeMax = PostLh2Encoder.CodeSize - 1;

        int node = this.codeHuffman.codeToNode(Math.min(code, CodeMax));
        int hcode = 0;
        int hlen = 0;
        do {
            hcode >>>= 1;
            hlen++;
            if ((node & 1) != 0) hcode |= 0x80000000;

            node = this.codeHuffman.parentNode(node);
        } while (node != DynamicHuffman.ROOT);

        this.out.writeBits(hlen, hcode >>> (32 - hlen));                    //throws IOException


        if (code < 0x100) {
            this.position++;
        } else {
            this.matchLength = (code & 0xFF) + PostLh2Encoder.Threshold;

            if (CodeMax <= code) {
                this.out.writeBits(8, code - CodeMax);                        //throws IOException
                code = CodeMax;   //update����R�[�h��CodeMax�ɂ���B
            }
        }
        this.codeHuffman.update(code);
    }

    /**
     * LZSS �ň��k���ꂽ���k�R�[�h�̂�����v�ʒu���������ށB<br>
     *
     * @param offset LZSS �ň��k���ꂽ���k�R�[�h�̂�����v�ʒu
     */
    public void writeOffset(int offset) throws IOException {
        if (this.nextPosition < PostLh2Encoder.DictionarySize) {
            while (this.nextPosition < this.position) {
                this.offHiHuffman.addLeaf(this.nextPosition >> 6);
                this.nextPosition += 64;

                if (PostLh2Encoder.DictionarySize <= this.nextPosition)
                    break;
            }
        }

        int node = this.offHiHuffman.codeToNode(offset >> 6);
        int hcode = 0;
        int hlen = 0;
        while (node != DynamicHuffman.ROOT) {
            hcode >>>= 1;
            hlen++;
            if ((node & 1) != 0) hcode |= 0x80000000;

            node = this.offHiHuffman.parentNode(node);
        }

        this.out.writeBits(hlen, hcode >> (32 - hlen));                     //throws IOException
        this.out.writeBits(6, offset);                                        //throws IOException
        this.offHiHuffman.update(offset >> 6);

        this.position += this.matchLength;
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
     * ���� PostLzssEncoder �Ƀo�b�t�@�����O����Ă���
     * �S�Ă� 8�r�b�g�P�ʂ̃f�[�^���o�͐�� OutputStream �ɏo�͂��A
     * �o�͐�� OutputStream �� flush() ����B<br>
     * ���̃��\�b�h�͈��k����ω������Ȃ��B
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     *
     * @see PostLzssEncoder#flush()
     * @see BitOutputStream#flush()
     */
    public void flush() throws IOException {
        this.out.flush();                                                       //throws IOException
    }

    /**
     * ���̏o�̓X�g���[���ƁA�ڑ����ꂽ�o�̓X�g���[������A
     * �g�p���Ă������\�[�X���J������B<br>
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void close() throws IOException {
        this.out.close();                                                       //throws IOException

        this.out = null;
        this.codeHuffman = null;
        this.offHiHuffman = null;
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
        return PostLh2Encoder.DictionarySize;
    }

    /**
     * -lh2-�`���� LZSS�̍ő��v���𓾂�B
     *
     * @return -lh2-�`���� LZSS�̍ő��v��
     */
    public int getMaxMatch() {
        return PostLh2Encoder.MaxMatch;
    }

    /**
     * -lh2-�`���� LZSS�̈��k�A�񈳏k��臒l�𓾂�B
     *
     * @return -lh2-�`���� LZSS�̈��k�A�񈳏k��臒l
     */
    public int getThreshold() {
        return PostLh2Encoder.Threshold;
    }

}
//end of PostLh2Encoder.java
