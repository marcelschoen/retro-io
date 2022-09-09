//start of SimpleSearch.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * SimpleSearch.java
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

//import exceptions

/**
 * ���ʂȌ����@�\��p���Ȃ� 
 * LzssSearchMethod �̍ł��V���v���Ȏ����B<br>
 * �����@�\��p���Ȃ����߁A
 * ���̌����@�\��p��������Ɣ�ׂ�ƒx�����A
 * ����������ʂ����ɏ��Ȃ��B<br>
 *
 * <pre>
 * -- revision history --
 * $Log: SimpleSearch.java,v $
 * Revision 1.0  2002/08/05 00:00:00  dangan
 * add to version control
 * [change]
 *     LzssSearchMethod �̃C���^�t�F�C�X�ύX�ɂ��킹�ăC���^�t�F�C�X�ύX�B
 * [maintenance]
 *     �\�[�X����
 *     �^�u�p�~
 *     ���C�Z���X���̏C��
 *
 * </pre>
 *
 * @author $Author: dangan $
 * @version $Revision: 1.0 $
 */
public class SimpleSearch implements LzssSearchMethod {


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
     * LZSS�����T�C�Y�B
     */
    private int DictionarySize;

    /**
     * LZSS���k�Ɏg�p�����l�B
     * �ő��v���������B
     */
    private int MaxMatch;

    /**
     * LZSS���k�Ɏg�p�����臒l�B
     * ��v���� ���̒l�ȏ�ł���΁A���k�R�[�h���o�͂���B
     */
    private int Threshold;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  text buffer
    //------------------------------------------------------------------
    //  private byte[] TextBuffer
    //  private int DictionaryLimit
    //------------------------------------------------------------------
    /**
     * LZSS���k���{�����߂̃o�b�t�@�B
     * position ������ �O���͎����̈�A
     * �㔼�͈��k���{�����߂̃f�[�^�̓������o�b�t�@�B
     * LzssSearchMethod�̎������ł͓ǂݍ��݂̂݋������B
     */
    private byte[] TextBuffer;

    /**
     * �����̌��E�ʒu�B 
     * TextBuffer�O���̎����̈�Ƀf�[�^�������ꍇ��
     * �����̈�ɂ���s��̃f�[�^(Java�ł�0)���g�p
     * ���Ĉ��k���s����̂�}�~����B
     */
    private int DictionaryLimit;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private SimpleSearch()
    //  public SimpleSearch( int DictionarySize, int MaxMatch, 
    //                       int Threshold, byte[] TextBuffer )
    //------------------------------------------------------------------

    /**
     * �f�t�H���g�R���X�g���N�^�B
     *�g�p�s��
     */
    private SimpleSearch() {
    }

    /**
     * ���ʂȌ����@�\��p���Ȃ��V���v����
     * LzssSearchMethod ���\�z����B<br>
     *
     * @param DictionarySize  �����T�C�Y
     * @param MaxMatch        �ő��v��
     * @param Threshold       ���k�A�񈳏k��臒l
     * @param TextBuffer      LZSS���k���{�����߂̃o�b�t�@
     */
    public SimpleSearch(int DictionarySize,
                        int MaxMatch,
                        int Threshold,
                        byte[] TextBuffer) {

        this.DictionarySize = DictionarySize;
        this.MaxMatch = MaxMatch;
        this.Threshold = Threshold;
        this.TextBuffer = TextBuffer;
        this.DictionaryLimit = this.DictionarySize;
    }


    //------------------------------------------------------------------
    // method of LzssSearchMethod
    //------------------------------------------------------------------
    //  public void put( int position )
    //  public int searchAndPut( int position )
    //  public int search( int position, int lastPutPos, int maxMatch )
    //  public void slide()
    //  public int putRequires()
    //------------------------------------------------------------------

    /**
     * SimpleSearch �͌����@�\���g�p���Ȃ�����
     * ���̃��\�b�h�͉������Ȃ��B
     *
     * @param position TextBuffer���̃f�[�^�p�^���̊J�n�ʒu
     */
    public void put(int position) {
    }

    /**
     * TextBuffer ���̎����̈�ɂ���f�[�^�p�^������
     * position ����n�܂�f�[�^�p�^����
     * �Œ��̈�v�������̂���������B<br>
     *
     * @param position TextBuffer���̃f�[�^�p�^���̊J�n�ʒu�B
     *
     * @return ��v�����������ꍇ��
     *         LzssOutputStream.createSearchReturn 
     *         �ɂ���Đ������ꂽ��v�ʒu�ƈ�v���̏������l�A
     *         ��v��������Ȃ������ꍇ��
     *         LzssOutputStream.NOMATCH�B
     *
     * @see LzssOutputStream#createSearchReturn(int, int)
     * @see LzssOutputStream#NOMATCH
     */
    public int searchAndPut(int position) {
        int matchlen = this.Threshold - 1;
        int matchpos = position;
        int scanlimit = Math.max(this.DictionaryLimit,
                position - this.DictionarySize);
        int scanpos = position - 1;

        byte[] buf = this.TextBuffer;
        int max = position + this.MaxMatch;
        int p = 0;
        int s = 0;
        int len = 0;
        while (scanlimit < scanpos) {
            s = scanpos;
            p = position;
            while (buf[s] == buf[p]) {
                s++;
                p++;
                if (max <= p) break;
            }

            len = p - position;
            if (matchlen < len) {
                matchpos = scanpos;
                matchlen = len;
                if (this.MaxMatch == len) break;
            }
            scanpos--;
        }

        if (this.Threshold <= matchlen) {
            return LzssOutputStream.createSearchReturn(matchlen, matchpos);
        } else {
            return LzssOutputStream.NOMATCH;
        }
    }

    /**
     * TextBuffer ���̎����̈�ɂ���f�[�^�p�^������
     * position ����n�܂�f�[�^�p�^����
     * �Œ��̈�v�������̂���������B<br>
     *
     * @param position   TextBuffer���̃f�[�^�p�^���̊J�n�ʒu�B
     * @param lastPutPos �Ō�ɓo�^�����f�[�^�p�^���̊J�n�ʒu�B
     *
     * @return ��v�����������ꍇ��
     *         LzssOutputStream.createSearchReturn 
     *         �ɂ���Đ������ꂽ��v�ʒu�ƈ�v���̏������l�A
     *         ��v��������Ȃ������ꍇ��
     *         LzssOutputStream.NOMATCH�B
     *
     * @see LzssOutputStream#createSearchReturn(int, int)
     * @see LzssOutputStream#NOMATCH
     */
    public int search(int position, int lastPutPos) {
        int matchlen = this.Threshold - 1;
        int matchpos = position;
        int scanlimit = Math.max(this.DictionaryLimit,
                position - this.DictionarySize);
        int scanpos = position - 1;

        byte[] buf = this.TextBuffer;
        int max = Math.min(position + this.MaxMatch,
                this.TextBuffer.length);
        int p = 0;
        int s = 0;
        int len = 0;
        while (scanlimit < scanpos) {
            s = scanpos;
            p = position;
            while (buf[s] == buf[p]) {
                s++;
                p++;
                if (max <= p) break;
            }

            len = p - position;
            if (matchlen < len) {
                matchpos = scanpos;
                matchlen = len;
                if (this.MaxMatch == len) break;
            }
            scanpos--;
        }

        if (this.Threshold <= matchlen) {
            return LzssOutputStream.createSearchReturn(matchlen, matchpos);
        } else {
            return LzssOutputStream.NOMATCH;
        }
    }

    /**
     * LzssOutputStream �� slide() ��TextBuffer���̃f�[�^��
     * DictionarySize �����ړ�������ۂɌ����@�\���̃f�[�^��
     * �����Ɩ��������ړ������鏈�����s���B
     */
    public void slide() {
        this.DictionaryLimit = Math.max(0, this.DictionaryLimit - this.DictionarySize);
    }

    /**
     * SimpleSearch �͌����@�\���g�p���Ȃ����ߏ�� 0 ��Ԃ��B
     *
     * @return ��� 0
     */
    public int putRequires() {
        return 0;
    }

}
//end of SimpleSearch.java
