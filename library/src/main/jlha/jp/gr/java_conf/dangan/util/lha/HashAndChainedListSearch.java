//start of HashAndChainedListSearch.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * HashAndChainedListSearch.java
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

import jp.gr.java_conf.dangan.lang.reflect.Factory;

import java.lang.reflect.InvocationTargetException;

//import exceptions


/**
 * �n�b�V���ƒP�����A�����X�g���g���č��������ꂽ LzssSearchMethod�B<br>
 * ������ł��؂邱�Ƃɂ�鍂�������s���Ă��邽�߁A
 * �K���Œ���v�������邱�Ƃ��o����Ƃ͌���Ȃ��B<br>
 *
 * <pre>
 * -- revision history --
 * $Log: HashAndChainedListSearch.java,v $
 * Revision 1.0  2002/08/05 00:00:00  dangan
 * add to version control
 * [change]
 *     LzssSearchMethod �̃C���^�t�F�C�X�ύX�ɂ��킹�ăC���^�t�F�C�X�ύX
 * [improvement]
 *     ar940528 �� TEST5���� �̎����ɕύX�B
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
public class HashAndChainedListSearch implements LzssSearchMethod {


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
     * �O���͎����̈�A
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
    //  instance field
    //------------------------------------------------------------------
    //  hash
    //------------------------------------------------------------------
    //  private HashMethod hashMethod
    //  private int[] hashTable
    //  private char[] tooBigFlag
    //------------------------------------------------------------------
    /**
     * �n�b�V���֐�
     */
    private HashMethod hashMethod;

    /**
     * �n�b�V���e�[�u��
     * �Y���̓n�b�V���l�A���e��TextBuffer���̈ʒu
     */
    private int[] hashTable;

    /**
     * �����n�b�V���l�����f�[�^�p�^���̘A�����X�g�̒�����
     * ���ȏ�ɂȂ����ꍇ�ɃZ�b�g����t���O�B
     *
     * boolean[] �ɂ���Ɖ��̂��x���Ȃ�̂�
     * char[] �Ƃ��� 16�Z�߂Ĉ����B
     * �����ꍇ�� ���[�e�B���e�B���\�b�h
     * isTooBig(), setTooBigFlag(), clearTooBigFlag() ����Ĉ����B
     */
    private char[] tooBigFlag;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  cahined list
    //------------------------------------------------------------------
    //  private int[] prev
    //  private int SearchLimitCount
    //------------------------------------------------------------------
    /**
     * �����n�b�V���l�����f�[�^�p�^���J�n�ʒu������
     * �P�����A�����X�g�B
     */
    private int[] prev;

    /**
     * �T�����s�񐔂̏���l�����B
     * ���̉񐔈ȏ�̒T���͍s��Ȃ��B
     */
    private int SearchLimitCount;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private HashAndChainedListSearch()
    //  public HashAndChainedListSearch( int DictionarySize, int MaxMatch, 
    //                                   int Threshold, byte[] TextBuffer )
    //  public HashAndChainedListSearch( int DictionarySize, int MaxMatch, 
    //                                   int Threshold, byte[] TextBuffer,
    //                                   int SearchLimitCount )
    //  public HashAndChainedListSearch( int DictionarySize, int MaxMatch, 
    //                                   int Threshold, byte[] TextBuffer,
    //                                   String HashMethodClassName )
    //  public HashAndChainedListSearch( int DictionarySize, int MaxMatch, 
    //                                   int Threshold, byte[] TextBuffer,
    //                                   String HashMethodClassName,
    //                                   int SearchLimitCount )
    //------------------------------------------------------------------

    /**
     * �f�t�H���g�R���X�g���N�^�B
     * �g�p�s�B
     */
    private HashAndChainedListSearch() {
    }

    /**
     * �n�b�V���ƘA�����X�g���g�p���� LzssSearchMethod ���\�z����B<br>
     * �n�b�V���֐��ƒT�����s�񐔂̏���l�ɂ̓f�t�H���g�̂��̂��g�p�����B<br>
     *
     * @param DictionarySize      �����T�C�Y
     * @param MaxMatch            �Œ���v��
     * @param Threshold           ���k�A�񈳏k��臒l
     * @param TextBuffer          LZSS���k���{�����߂̃o�b�t�@
     */
    public HashAndChainedListSearch(int DictionarySize,
                                    int MaxMatch,
                                    int Threshold,
                                    byte[] TextBuffer) {
        this(DictionarySize,
                MaxMatch,
                Threshold,
                TextBuffer,
                HashDefault.class.getName(),
                256);
    }

    /**
     * �n�b�V���ƘA�����X�g���g�p���� LzssSearchMethod ���\�z����B<br>
     * �n�b�V���֐��ɂ̓f�t�H���g�̂��̂��g�p�����B<br>
     *
     * @param DictionarySize      �����T�C�Y
     * @param MaxMatch            �Œ���v��
     * @param Threshold           ���k�A�񈳏k��臒l
     * @param TextBuffer          LZSS���k���{�����߂̃o�b�t�@
     * @param SearchLimitCount    �T�����s�񐔂̏��
     *
     * @exception IllegalArgumentException
     *              SearchLimitCount ��0�ȉ��̏ꍇ
     */
    public HashAndChainedListSearch(int DictionarySize,
                                    int MaxMatch,
                                    int Threshold,
                                    byte[] TextBuffer,
                                    int SearchLimitCount) {
        this(DictionarySize,
                MaxMatch,
                Threshold,
                TextBuffer,
                HashDefault.class.getName(),
                SearchLimitCount);
    }

    /**
     * �n�b�V���ƘA�����X�g���g�p���� LzssSearchMethod ���\�z����B<br>
     * �T�����s�񐔂̏���l�ɂ̓f�t�H���g�̂��̂��g�p�����B<br>
     *
     * @param DictionarySize      �����T�C�Y
     * @param MaxMatch            �Œ���v��
     * @param Threshold           ���k�A�񈳏k��臒l
     * @param TextBuffer          LZSS���k���{�����߂̃o�b�t�@
     * @param HashMethodClassName Hash�֐���񋟂���N���X��
     *
     * @exception NoClassDefFoundError
     *              HashMethodClassName �ŗ^����ꂽ�N���X��
     *              ������Ȃ��ꍇ�B
     * @exception InstantiationError
     *              HashMethodClassName �ŗ^����ꂽ�N���X��
     *              abstract class �ł��邽�߃C���X�^���X�𐶐��ł��Ȃ��ꍇ�B
     * @exception NoSuchMethodError
     *              HashMethodClassName �ŗ^����ꂽ�N���X��
     *              �R���X�g���N�^ HashMethod( byte[] )�������Ȃ��ꍇ
     */
    public HashAndChainedListSearch(int DictionarySize,
                                    int MaxMatch,
                                    int Threshold,
                                    byte[] TextBuffer,
                                    String HashMethodClassName) {
        this(DictionarySize,
                MaxMatch,
                Threshold,
                TextBuffer,
                HashMethodClassName,
                256);
    }


    /**
     * �n�b�V���ƘA�����X�g���g�p���� LzssSearchMethod ���\�z����B<br>
     *
     * @param DictionarySize      �����T�C�Y
     * @param MaxMatch            �Œ���v��
     * @param Threshold           ���k�A�񈳏k��臒l
     * @param TextBuffer          LZSS���k���{�����߂̃o�b�t�@
     * @param HashMethodClassName Hash�֐���񋟂���N���X��
     * @param SearchLimitCount    �T�����s�񐔂̏��
     *
     * @exception IllegalArgumentException
     *              SearchLimitCount ��0�ȉ��̏ꍇ
     * @exception NoClassDefFoundError
     *              HashMethodClassName �ŗ^����ꂽ�N���X��
     *              ������Ȃ��ꍇ�B
     * @exception InstantiationError
     *              HashMethodClassName �ŗ^����ꂽ�N���X��
     *              abstract class �ł��邽�߃C���X�^���X�𐶐��ł��Ȃ��ꍇ�B
     * @exception NoSuchMethodError
     *              HashMethodClassName �ŗ^����ꂽ�N���X��
     *              �R���X�g���N�^ HashMethod( byte[] )�������Ȃ��ꍇ
     */
    public HashAndChainedListSearch(int DictionarySize,
                                    int MaxMatch,
                                    int Threshold,
                                    byte[] TextBuffer,
                                    String HashMethodClassName,
                                    int SearchLimitCount) {

        if (0 < SearchLimitCount) {

            this.DictionarySize = DictionarySize;
            this.MaxMatch = MaxMatch;
            this.Threshold = Threshold;
            this.TextBuffer = TextBuffer;
            this.DictionaryLimit = this.DictionarySize;
            this.SearchLimitCount = SearchLimitCount;

            try {
                this.hashMethod = (HashMethod) Factory.createInstance(
                        HashMethodClassName,
                        new Object[]{TextBuffer});
            } catch (ClassNotFoundException exception) {
                throw new NoClassDefFoundError(exception.getMessage());
            } catch (InvocationTargetException exception) {
                throw new Error(exception.getTargetException().getMessage());
            } catch (NoSuchMethodException exception) {
                throw new NoSuchMethodError(exception.getMessage());
            } catch (InstantiationException exception) {
                throw new InstantiationError(exception.getMessage());
            }

            // �n�b�V���e�[�u��������
            this.hashTable = new int[this.hashMethod.tableSize()];
            for (int i = 0; i < this.hashTable.length; i++) {
                this.hashTable[i] = -1;
            }

            // �A�����X�g������
            this.prev = new int[this.DictionarySize];
            for (int i = 0; i < this.prev.length; i++) {
                this.prev[i] = -1;
            }

            this.tooBigFlag = new char[this.hashMethod.tableSize() >> 4];

        } else {
            throw new IllegalArgumentException("SearchLimitCount must be 1 or more.");
        }
    }


    //------------------------------------------------------------------
    //  method LzssSearchMethod
    //------------------------------------------------------------------
    //  public void put( int position )
    //  public int searchAndPut( int position )
    //  public int search( int position, int lastPutPos )
    //  public void slide( int slideWidth, int slideEnd )
    //  public int putRequires()
    //------------------------------------------------------------------

    /**
     * position ����n�܂�f�[�^�p�^����
     * �n�b�V���ƘA�����X�g���琬�錟���@�\�ɓo�^����B<br>
     *
     * @param position TextBuffer���̃f�[�^�p�^���̊J�n�ʒu
     */
    public void put(int position) {
        int hash = this.hashMethod.hash(position);
        this.prev[position & (this.DictionarySize - 1)] = this.hashTable[hash];
        this.hashTable[hash] = position;
    }

    /**
     * �n�b�V���ƘA�����X�g���琬�錟���@�\�ɓo�^���ꂽ
     * �f�[�^�p�^������ position ����n�܂�f�[�^�p�^����
     * �Œ��̈�v�������̂��������A
     * ������ position ����n�܂�f�[�^�p�^���� 
     * �n�b�V���ƘA�����X�g���琬�錟���@�\�ɓo�^����B<br>
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
        int maxmatch = this.MaxMatch;
        int scanlimit = Math.max(this.DictionaryLimit,
                position - this.DictionarySize);

        //------------------------------------------------------------------
        //  �A�����X�g�̒�������������ꍇ offset ���g�p����
        //  �A�����X�g�̒Z���n�b�V���l���g���B
        int poshash = this.hashMethod.hash(position);
        int offhash = poshash;
        int offset = 0;
        while (this.isTooBig(offhash)
                && offset < this.MaxMatch - this.hashMethod.hashRequires()) {
            offset++;
            offhash = this.hashMethod.hash(position + offset);
        }

        //------------------------------------------------------------------
        //  ���C�����[�v
        //  �ő� offhash �� poshash ����n�܂� 2�̘A�����X�g�𑖍�����B
        byte[] buf = this.TextBuffer;
        int max = position + this.MaxMatch;
        int s = 0;
        int p = 0;
        int len = 0;
        while (true) {
            int scanpos = this.hashTable[offhash];
            int searchCount = this.SearchLimitCount;

            while (scanlimit <= scanpos - offset && 0 < --searchCount) {
                if (buf[scanpos + matchlen - offset]
                        == buf[position + matchlen]) {
                    s = scanpos - offset;
                    p = position;
                    while (buf[s] == buf[p]) {
                        s++;
                        p++;
                        if (max <= p) break;
                    }

                    len = p - position;
                    if (matchlen < len) {
                        matchpos = scanpos - offset;
                        matchlen = len;
                        if (max <= p) break;
                    }
                }
                scanpos = this.prev[scanpos & (this.DictionarySize - 1)];
            }

            if (searchCount <= 0) {
                this.setTooBigFlag(offhash);
            } else if (scanpos < scanlimit) {
                this.clearTooBigFlag(offhash);
            }

            if (0 < offset
                    && matchlen < this.hashMethod.hashRequires() + offset) {
                offset = 0;
                maxmatch = this.hashMethod.hashRequires() + offset - 1;
                max = position + maxmatch;
                offhash = poshash;
            } else {
                break;
            }
        }

        //------------------------------------------------------------------
        //  �n�b�V���ƘA�����X�g���g�p���������@�\��
        //  position ����n�܂�f�[�^�p�^����o�^����B
        this.prev[position & (this.DictionarySize - 1)] = this.hashTable[poshash];
        this.hashTable[poshash] = position;

        //------------------------------------------------------------------
        //  �Œ���v���Ăяo�����ɕԂ��B
        if (this.Threshold <= matchlen) {
            return LzssOutputStream.createSearchReturn(matchlen, matchpos);
        } else {
            return LzssOutputStream.NOMATCH;
        }
    }

    /**
     * �n�b�V���ƘA�����X�g���g�p���������@�\�ɓo�^���ꂽ
     * �f�[�^�p�^���������� position ����n�܂�f�[�^�p�^����
     * �Œ��̈�v�������̂𓾂�B<br>
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

        //------------------------------------------------------------------
        //  �n�b�V���ƘA�����X�g�ɂ�錟���@�\�ɓo�^����Ă��Ȃ�
        //  �f�[�^�p�^����P���Ȓ��������Ō�������B
        int matchlen = this.Threshold - 1;
        int matchpos = position;
        int scanpos = position - 1;
        int scanlimit = Math.max(this.DictionaryLimit, lastPutPos);

        byte[] buf = this.TextBuffer;
        int max = Math.min(this.TextBuffer.length,
                position + this.MaxMatch);
        int s = 0;
        int p = 0;
        int len = 0;
        while (scanlimit < scanpos) {
            s = scanpos;
            p = position;
            while (buf[s] == buf[p]) {
                s++;
                p++;
                if (max <= p) break;
            }

            if (matchlen < len) {
                matchpos = scanpos;
                matchlen = len;
            }
            scanpos--;
        }

        //------------------------------------------------------------------
        //  �n�b�V���ƘA�����X�g���g�p���������@�\ ���猟������B
        if (this.hashMethod.hashRequires() < this.TextBuffer.length - position) {
            int maxmatch = this.MaxMatch;
            scanlimit = Math.max(this.DictionaryLimit,
                    position - this.DictionarySize);

            //  �A�����X�g�̒�������������ꍇ offset ���g�p����
            //  �A�����X�g�̒Z���n�b�V���l���g���B
            int poshash = this.hashMethod.hash(position);
            int offhash = poshash;
            int offset = 0;
            while (this.isTooBig(offhash)
                    && offset < this.MaxMatch - this.hashMethod.hashRequires()) {
                offset++;
                offhash = this.hashMethod.hash(position + offset);
            }

            //  ���C�����[�v
            //  �ő� offhash �� poshash ����n�܂� 2�̘A�����X�g�𑖍�����B
            while (true) {
                int searchCount = this.SearchLimitCount;
                scanpos = this.hashTable[offhash];

                while (scanlimit <= scanpos - offset && 0 < --searchCount) {
                    if (buf[scanpos + matchlen - offset]
                            == buf[position + matchlen]) {
                        s = scanpos - offset;
                        p = position;
                        while (buf[s] == buf[p]) {
                            s++;
                            p++;
                            if (max <= p) break;
                        }

                        len = p - position;
                        if (matchlen < len) {
                            matchpos = scanpos - offset;
                            matchlen = len;
                            if (max <= p) break;
                        }
                    }
                    scanpos = this.prev[scanpos & (this.DictionarySize - 1)];
                }

                if (searchCount <= 0) {
                    this.setTooBigFlag(offhash);
                } else if (scanpos < scanlimit) {
                    this.clearTooBigFlag(offhash);
                }

                if (0 < offset
                        && matchlen < this.hashMethod.hashRequires() + offset) {
                    offset = 0;
                    maxmatch = this.hashMethod.hashRequires() + offset - 1;
                    max = Math.min(this.TextBuffer.length,
                            position + maxmatch);
                    offhash = poshash;
                } else {
                    break;
                }
            }
        }

        //------------------------------------------------------------------
        //  �Œ���v���Ăяo�����ɕԂ��B
        if (this.Threshold <= matchlen) {
            return LzssOutputStream.createSearchReturn(matchlen, matchpos);
        } else {
            return LzssOutputStream.NOMATCH;
        }
    }

    /**
     * TextBuffer����position�܂ł̃f�[�^��
     * �O���ֈړ�����ہA����ɉ����� SearchMethod����
     * �f�[�^�� TextBuffer���̃f�[�^�Ɩ������Ȃ��悤��
     * �O���ֈړ����鏈�����s���B
     */
    public void slide() {
        this.DictionaryLimit = Math.max(0, this.DictionaryLimit - this.DictionarySize);

        for (int i = 0; i < this.hashTable.length; i++) {
            int pos = this.hashTable[i] - this.DictionarySize;
            this.hashTable[i] = (0 <= pos ? pos : -1);
        }

        for (int i = 0; i < this.prev.length; i++) {
            int pos = this.prev[i] - this.DictionarySize;
            this.prev[i] = (0 <= pos ? pos : -1);
            ;
        }
    }

    /**
     * put() �� LzssSearchMethod�Ƀf�[�^��
     * �o�^����Ƃ��Ɏg�p�����f�[�^�ʂ𓾂�B
     * HashAndChainedListSearch �ł́A
     * �����Ŏg�p���Ă��� HashMethod �̎����� 
     * hash() �̂��߂ɕK�v�Ƃ���f�[�^��
     * ( HashMethod.hashRequires() �̖߂�l ) ��Ԃ��B
     *
     * @return �����Ŏg�p���Ă��� HashMethod �̎����� 
     *         hash() �̂��߂ɕK�v�Ƃ���f�[�^��
     */
    public int putRequires() {
        return this.hashMethod.hashRequires();
    }

    //------------------------------------------------------------------
    //  method of ImprovedLzssSearchMethod
    //------------------------------------------------------------------
    //  public int searchAndPut( int position, int[] matchposs )
    //------------------------------------------------------------------

    /**
     * ���ǂ� LZSS ���k�̂��߂̑I������񋟂��� searchAndPut()�B 
     * �Ⴆ�Έ�v�� 3, ��v�ʒu 4 �� ��v�� 4, ��v�ʒu 1024 �ł�
     * ��v�� 3, ��v�ʒu 4 + �񈳏k1���� �̕����o�̓r�b�g����
     * ���Ȃ��Ȃ鎖������B���̂悤�ȏꍇ�ɑΏ����邽�߈�v������
     * position�Ɉ�ԋ߂���v�ʒu��񋓂���B
     *
     * @param position  �����Ώۂ̃f�[�^�p�^���̊J�n�ʒu
     * @param matchposs ��v�ʒu�̗񋓂��i�[���ĕԂ����߂̔z��<br>
     *                  matchpos[0] �ɂ� ��v���� Threshold �̈�v�ʒu���A<br>
     *                  matchpos[1] �ɂ� ��v���� Threshold + 1 �̈�v�ʒu���i�[�����B<br>
     *                  ��v��������Ȃ������ꍇ�ɂ� LzssOutputStream.NOMATCH ���i�[�����B
     *
     * @return ��v�����������ꍇ��
     *         LzssOutputStream.createSearchReturn �Ő������ꂽ SearchReturn ���Ԃ����B<br>
     *         ��v��������Ȃ��ꍇ�� LzssOutputStream.NOMATCH ���Ԃ����B<br>
     */
    public int searchAndPut(int position, int[] matchposs) {
        int matchlen = this.Threshold - 1;
        int matchpos = position;
        int maxmatch = this.MaxMatch;
        int scanlimit = Math.max(this.DictionaryLimit,
                position - this.DictionarySize);
        int searchCount = this.SearchLimitCount;

        for (int i = 0; i < matchposs.length; i++)
            matchposs[i] = LzssOutputStream.NOMATCH;

        int scanpos = this.hashTable[this.hashMethod.hash(position)];

        while (scanlimit < scanpos && 0 < searchCount--) {
            if (this.TextBuffer[scanpos + matchlen]
                    == this.TextBuffer[position + matchlen]) {
                int len = 0;
                while (this.TextBuffer[scanpos + len]
                        == this.TextBuffer[position + len]) {
                    if (maxmatch <= ++len) break;
                }

                if (matchlen < len) {
                    int i = matchlen + 1 - this.Threshold;
                    int end = Math.min(len + 1 - this.Threshold, matchposs.length);
                    while (i < end) matchposs[i++] = scanpos;

                    matchpos = scanpos;
                    matchlen = len;
                    if (maxmatch <= len)
                        break;
                }
            }
            scanpos = this.prev[scanpos & (this.DictionarySize - 1)];
        }

        this.put(position);

        if (matchpos < position)
            return LzssOutputStream.createSearchReturn(matchlen, matchpos);
        else
            return LzssOutputStream.NOMATCH;
    }


    //------------------------------------------------------------------
    //  local methods
    //------------------------------------------------------------------
    //  too big flag
    //------------------------------------------------------------------
    //  private int isTooBig( int hash )
    //  private void setTooBigFlag( int hash )
    //  private void clearTooBigFlag( int hash )
    //------------------------------------------------------------------

    /**
     * hash �̘A�����X�g��臒l�𒴂��Ă��邩�𓾂�B
     *
     * @param hash �n�b�V���l
     *
     * @return �A�����X�g�̒�����臒l�𒴂��Ă���Ȃ� true
     *         �����Ă��Ȃ���� false
     */
    private boolean isTooBig(int hash) {
        return 0 != (this.tooBigFlag[hash >> 4] & (1 << (hash & 0x0F)));
    }

    /**
     * hash �̘A�����X�g��臒l�𒴂�����������
     * �t���O���Z�b�g����B
     *
     * @param hash too big �t���O���Z�b�g����n�b�V���l
     */
    private void setTooBigFlag(int hash) {
        this.tooBigFlag[hash >> 4] |= 1 << (hash & 0x0F);
    }

    /**
     * hash �̘A�����X�g��臒l�𒴂��Ă��鎖������
     * �t���O���N���A����B
     *
     * @param hash too big �t���O���N���A����n�b�V���l
     */
    private void clearTooBigFlag(int hash) {
        this.tooBigFlag[hash >> 4] &= ~(1 << (hash & 0x0F));
    }

}
//end of HashAndChainedListSearch.java
