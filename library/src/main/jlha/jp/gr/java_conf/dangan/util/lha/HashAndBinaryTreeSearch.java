//start of HashAndBinaryTreeSearch.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * HashAndBinaryTreeSearch.java
 * <p>
 * Copyright (C) 2002  Michel Ishizuka  All rights reserved.
 * <p>
 * �ȉ��̏����ɓ��ӂ���Ȃ�΃\�[�X�ƃo�C�i���`���̍Ĕz�z�Ǝg�p��
 * �ύX�̗L���ɂ�����炸������B
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
 * �n�b�V���Ɠ񕪖؂��g���� LzssSearchMethod �̎����B<br>
 * <pre>
 * �f�[�^���k�n���h�u�b�N[�������]
 *        M.�l���\��/J.-L.�Q�B���[ ��
 *                �������u�E�R���p ��
 *                  ISBN4-8101-8605-9
 *                             5728�~(�Ŕ���,�����̍w�������̉��i)
 * </pre>
 * ���Q�l�ɂ����B<br>
 * �񕪖؂ł́A�Œ���v�������邱�Ƃ͂ł��邪�A
 * �ł��߂���v����������Ƃ͌���Ȃ����߁A
 * LZSS�� ��v�ʒu���߂��ꏊ�ɕ΂鎖��
 * ���p����悤�� -lh5- �̂悤�Ȉ��k�@�ł́A
 * ���k���͂����炩�ቺ����B
 *
 * <pre>
 * -- revision history --
 * $Log: HashAndBinaryTreeSearch.java,v $
 * Revision 1.0  2002/08/05 00:00:00  dangan
 * add to version control
 * [change]
 *     LzssSearchMethod �̃C���^�t�F�C�X�ύX�ɂ��킹�ăC���^�t�F�C�X�ύX
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
public class HashAndBinaryTreeSearch implements LzssSearchMethod {


    //------------------------------------------------------------------
    //  class field
    //------------------------------------------------------------------
    //  private static final int UNUSED
    //  private static final int ROOT_NODE
    //------------------------------------------------------------------
    /**
     * �g�p����Ă��Ȃ����������l�B
     * parent[node] �� UNUSED ������ꍇ�� node �͖��g�p��node�ł���B
     * small[node], large[node] �� UNUSED ������ꍇ��
     * node �������瑤�̎q�m�[�h�������Ȃ��������������B
     */
    private static final int UNUSED = -1;

    /**
     * �񕪖؂̍��������l�B
     * parent[node] �� ROOT_NODE ������ꍇ�� node �͓񕪖؂̍��ł���B
     */
    private static final int ROOT_NODE = -2;


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
    //------------------------------------------------------------------
    /**
     * �n�b�V���֐�
     */
    private HashMethod hashMethod;

    /**
     * �n�b�V���e�[�u��
     * �Y���̓n�b�V���l�A���e�͌X�̃n�b�V���l������
     * �񕪖؂̍��̃f�[�^�p�^���̊J�n�ʒu�B
     */
    private int[] hashTable;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  binary tree
    //------------------------------------------------------------------
    //  private int[] parent
    //  private int[] small
    //  private int[] large
    //  private int[] dummy
    //------------------------------------------------------------------
    /**
     * �e�̃f�[�^�p�^���̊J�n�ʒu�������B
     * �Y�����̓m�[�h�ԍ��A���e�͐e�m�[�h�̃f�[�^�p�^���̊J�n�ʒu
     */
    private int[] parent;

    /**
     * �������q�̃f�[�^�p�^���̊J�n�ʒu�������B
     * �Y�����̓m�[�h�ԍ��A���e�͏������q�m�[�h�f�[�^�p�^���̊J�n�ʒu
     */
    private int[] small;

    /**
     * �傫���q�̃f�[�^�p�^���̊J�n�ʒu�������B
     * �Y�����̓m�[�h�ԍ��A���e�͑傫���q�m�[�h�f�[�^�p�^���̊J�n�ʒu
     */
    private int[] large;

    /**
     * slide �p�̃o�b�t�@
     */
    private int[] dummy;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private HashAndBinaryTreeSearch()
    //  public HashAndBinaryTreeSearch( int DictionarySize, int MaxMatch, 
    //                                  int Threshold, byte[] TextBuffer )
    //  public HashAndBinaryTreeSearch( int DictionarySize, int MaxMatch, 
    //                                  int Threshold, byte[] TextBuffer, 
    //                                  String HashMethodClassName )
    //------------------------------------------------------------------

    /**
     * �f�t�H���g�R���X�g���N�^�B
     * �g�p�s��
     */
    private HashAndBinaryTreeSearch() {
    }

    /**
     * �n�b�V���Ɠ񕪖؂��g�p���������@�\���\�z����B<br>
     * �n�b�V���֐��̓f�t�H���g�̂��̂��g�p����B
     *
     * @param DictionarySize      �����T�C�Y
     * @param MaxMatch            �Œ���v��
     * @param Threshold           ���k�A�񈳏k��臒l
     * @param TextBuffer          LZSS���k���{�����߂̃o�b�t�@
     */
    public HashAndBinaryTreeSearch(int DictionarySize,
                                   int MaxMatch,
                                   int Threshold,
                                   byte[] TextBuffer) {

        this(DictionarySize,
                MaxMatch,
                Threshold,
                TextBuffer,
                HashShort.class.getName());
    }

    /**
     * �n�b�V���Ɠ񕪖؂��g�p���� LzssSearchMethod ���\�z����B
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
     *              �R���X�g���N�^ HashMethod( byte[] )
     *              �������Ȃ��ꍇ
     */
    public HashAndBinaryTreeSearch(int DictionarySize,
                                   int MaxMatch,
                                   int Threshold,
                                   byte[] TextBuffer,
                                   String HashMethodClassName) {


        this.DictionarySize = DictionarySize;
        this.MaxMatch = MaxMatch;
        this.Threshold = Threshold;
        this.TextBuffer = TextBuffer;
        this.DictionaryLimit = this.DictionarySize;

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

        //  �n�b�V���e�[�u���̏�����
        this.hashTable = new int[this.hashMethod.tableSize()];
        for (int i = 0; i < this.hashTable.length; i++) {
            this.hashTable[i] = HashAndBinaryTreeSearch.UNUSED;
        }

        //  �񕪖؂̏�����
        this.parent = new int[DictionarySize];
        this.large = new int[DictionarySize];
        this.small = new int[DictionarySize];
        for (int i = 0; i < this.parent.length; i++) {
            this.parent[i] = HashAndBinaryTreeSearch.UNUSED;
        }
    }


    //------------------------------------------------------------------
    //  method of LzssSearchMethod
    //------------------------------------------------------------------
    //  public void put( int position )
    //  public int searchAndPut( int position )
    //  public int search( int position, int lastPutPos )
    //  public void slide( int slideWidth, int slideEnd )
    //  public int putRequires()
    //------------------------------------------------------------------

    /**
     * position ����n�܂�f�[�^�p�^����
     * �n�b�V���Ɠ񕪖؂��g�p���������@�\�ɓo�^����B<br>
     *
     * @param position TextBuffer���̃f�[�^�p�^���̊J�n�ʒu
     */
    public void put(int position) {

        //------------------------------------------------------------------
        //  �񕪖؂���ł��Â��f�[�^�p�^�����폜
        this.deleteNode(position - this.DictionarySize);

        //------------------------------------------------------------------
        //  �񕪖؂��� position ��}������ʒu������
        int hash = this.hashMethod.hash(position);
        int parentpos = this.hashTable[hash];
        int scanpos = this.hashTable[hash];

        byte[] buf = this.TextBuffer;
        int max = position + this.MaxMatch;
        int p = 0;
        int s = 0;
        while (scanpos != HashAndBinaryTreeSearch.UNUSED) {

            s = scanpos;
            p = position;
            while (buf[s] == buf[p]) {
                s++;
                p++;
                if (max <= p) {
                    //���S��v�𔭌�
                    this.replaceNode(scanpos, position);
                    return;
                }
            }

            parentpos = scanpos;
            scanpos = (buf[s] < buf[p]
                    ? this.large[scanpos & (this.DictionarySize - 1)]
                    : this.small[scanpos & (this.DictionarySize - 1)]);
        }

        //------------------------------------------------------------------
        //  position ����n�܂�f�[�^�p�^���� �񕪖؂ɓo�^
        if (this.hashTable[hash] != HashAndBinaryTreeSearch.UNUSED) {
            this.addNode(parentpos, position, p - position);
        } else {
            this.hashTable[hash] = position;
            int node = position & (this.DictionarySize - 1);
            this.parent[node] = HashAndBinaryTreeSearch.ROOT_NODE;
            this.small[node] = HashAndBinaryTreeSearch.UNUSED;
            this.large[node] = HashAndBinaryTreeSearch.UNUSED;
        }
    }

    /**
     * �n�b�V���Ɠ񕪖؂��g�p���������@�\�ɓo�^���ꂽ
     * �f�[�^�p�^������ position ����n�܂�f�[�^�p�^����
     * �Œ��̈�v�������̂��������A
     * ������ position ����n�܂�f�[�^�p�^���� 
     * �n�b�V���Ɠ񕪖؂��g�p���������@�\�ɓo�^����B<br>
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

        //------------------------------------------------------------------
        //  �񕪖؂���ł��Â��f�[�^�p�^�����폜
        this.deleteNode(position - this.DictionarySize);

        //------------------------------------------------------------------
        //  �񕪖؂���Œ���v������
        int hash = this.hashMethod.hash(position);
        int matchlen = -1;
        int matchpos = this.hashTable[hash];
        int parentpos = this.hashTable[hash];
        int scanpos = this.hashTable[hash];

        byte[] buf = this.TextBuffer;
        int max = position + this.MaxMatch;
        int p = 0;
        int s = 0;
        int len = 0;
        while (scanpos != HashAndBinaryTreeSearch.UNUSED) {
            s = scanpos;
            p = position;
            while (buf[s] == buf[p]) {
                s++;
                p++;
                if (max <= p) {
                    //���S��v�𔭌�
                    this.replaceNode(matchpos, position);
                    return LzssOutputStream.createSearchReturn(matchlen, matchpos);
                }
            }

            len = p - position;
            if (matchlen < len) {
                matchpos = scanpos;
                matchlen = len;
            } else if (matchlen == len && matchpos < scanpos) {
                matchpos = scanpos;
            }

            parentpos = scanpos;
            scanpos = (buf[s] < buf[p]
                    ? this.large[scanpos & (this.DictionarySize - 1)]
                    : this.small[scanpos & (this.DictionarySize - 1)]);
        }

        //------------------------------------------------------------------
        //  position ����n�܂�f�[�^�p�^���� �񕪖؂ɓo�^
        if (this.hashTable[hash] != HashAndBinaryTreeSearch.UNUSED) {
            this.addNode(parentpos, position, len);
        } else {
            this.hashTable[hash] = position;
            int node = position & (this.DictionarySize - 1);
            this.parent[node] = HashAndBinaryTreeSearch.ROOT_NODE;
            this.small[node] = HashAndBinaryTreeSearch.UNUSED;
            this.large[node] = HashAndBinaryTreeSearch.UNUSED;
        }

        //------------------------------------------------------------------
        //  ���\�b�h�̐擪�ō폜���ꂽ
        //  �ł������f�[�^�p�^���Ɣ�r
        scanpos = position - this.DictionarySize;
        if (this.DictionaryLimit <= scanpos) {
            len = 0;
            while (this.TextBuffer[scanpos + len]
                    == this.TextBuffer[position + len]) {
                if (this.MaxMatch <= ++len) break;
            }

            if (matchlen < len) {
                matchpos = scanpos;
                matchlen = len;
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
     * �n�b�V���Ɠ񕪖؂��g�p���������@�\�ɓo�^���ꂽ�f�[�^�p�^����������
     * position ����n�܂�f�[�^�p�^���ƍŒ��̈�v�������̂𓾂�B<br>
     * TextBuffer.length &lt position + MaxMatch �ƂȂ�悤�� position �ł́A
     * �񕪖؂����S�ɑ������Ȃ����ߍŒ���v�𓾂���Ƃ͌���Ȃ��B<br>
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
        //  �񕪖؂ɓo�^����Ă��Ȃ��f�[�^�p�^����
        //  �P���Ȓ��������Ō�������B
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
        //  �񕪖؂�T��
        if (this.hashMethod.hashRequires() <= this.TextBuffer.length - position) {
            int hash = this.hashMethod.hash(position);
            scanpos = this.hashTable[hash];
            scanlimit = Math.max(this.DictionaryLimit,
                    position - this.DictionarySize);
            while (scanpos != HashAndBinaryTreeSearch.UNUSED) {
                s = scanpos;
                p = position;
                while (buf[s] == buf[p]) {
                    s++;
                    p++;
                    if (max <= p) break;
                }

                if (p < max) {
                    len = p - position;
                    if (scanlimit <= scanpos) {
                        if (matchlen < len) {
                            matchpos = scanpos;
                            matchlen = len;
                        } else if (matchlen == len && matchpos < scanpos) {
                            matchpos = scanpos;
                        }
                    }
                    scanpos = (buf[s] < buf[p]
                            ? this.large[scanpos & (this.DictionarySize - 1)]
                            : this.small[scanpos & (this.DictionarySize - 1)]);
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
     * TextBuffer���� position �܂ł̃f�[�^��O���ֈړ�����ہA
     * ����ɉ����� �n�b�V���Ɠ񕪖؂��g�p���������@�\���\������f�[�^��
     * TextBuffer���̃f�[�^�Ɩ������Ȃ��悤�ɑO���ֈړ����鏈�����s���B 
     */
    public void slide() {
        this.DictionaryLimit = Math.max(0, this.DictionaryLimit - this.DictionarySize);

        this.slideTree(this.hashTable);
        this.slideTree(this.parent);
        this.slideTree(this.small);
        this.slideTree(this.large);
    }

    /**
     * put() �܂��� searchAndPut() ���g�p����
     * �f�[�^�p�^����񕪖؂ɓo�^����ۂ�
     * �K�v�Ƃ���f�[�^�ʂ𓾂�B<br>
     * �񕪖؂͓o�^�̍ۂɃf�[�^�p�^�����\������ 
     * �S��(MaxMatch�o�C�g)�̃f�[�^��K�v�Ƃ���B
     *
     * @return �R���X�g���N�^�ŗ^���� MaxMatch
     */
    public int putRequires() {
        return this.MaxMatch;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  node operation
    //------------------------------------------------------------------
    //  private void addNode( int addpos, int position, int len )
    //  private void deleteNode( int position )
    //  private void contractNode( int oldpos, int newpos )
    //  private void replaceNode( int oldpos, int newpos )
    //------------------------------------------------------------------

    /**
     * parentpos �̃f�[�^�p�^���̎q�Ƃ��� 
     * position ����n�܂�f�[�^�p�^����񕪖؂ɓo�^����B<br>
     * parentpos �� position �̃f�[�^�p�^���� len �o�C�g��v����B
     * position �̈ʒu�̃m�[�h�͂��炩���� deleteNode ����
     * UNUSED �̏�Ԃɂ��Ă������ƁB
     *
     * @param parentpos �e�̃f�[�^�p�^����TextBuffer���̊J�n�ʒu
     * @param position  �V�K�ǉ�����f�[�^�p�^����TextBuffer���̊J�n�ʒu
     * @param len       �e�̃f�[�^�p�^���ƐV�K�ǉ�����f�[�^�p�^���̈�v��
     */
    private void addNode(int parentpos, int position, int len) {
        int parentnode = parentpos & (this.DictionarySize - 1);
        int node = position & (this.DictionarySize - 1);

        if (this.TextBuffer[parentpos + len] < this.TextBuffer[position + len]) {
            this.large[parentnode] = position;
        } else {
            this.small[parentnode] = position;
        }
        this.parent[node] = parentpos;
        this.small[node] = HashAndBinaryTreeSearch.UNUSED;
        this.large[node] = HashAndBinaryTreeSearch.UNUSED;
    }

    /**
     * position ����n�܂�f�[�^�p�^����񕪖؂���폜����B<br>
     *
     * @param position �폜����f�[�^�p�^���̊J�n�ʒu
     */
    private void deleteNode(int position) {
        int node = position & (this.DictionarySize - 1);

        if (this.parent[node] != HashAndBinaryTreeSearch.UNUSED) {
            if (this.small[node] == HashAndBinaryTreeSearch.UNUSED
                    && this.large[node] == HashAndBinaryTreeSearch.UNUSED) {
                this.contractNode(position, HashAndBinaryTreeSearch.UNUSED);
            } else if (this.small[node] == HashAndBinaryTreeSearch.UNUSED) {
                this.contractNode(position, this.large[node]);
            } else if (this.large[node] == HashAndBinaryTreeSearch.UNUSED) {
                this.contractNode(position, this.small[node]);
            } else {
                int replace = this.findNext(position);
                this.deleteNode(replace);
                this.replaceNode(position, replace);
            }
        }
    }

    /**
     * �q�� newpos ���������Ȃ� oldpos ��, newpos �Œu��������B
     * oldpos �͓񕪖؂���폜�����B
     *
     * @param oldpos �폜����f�[�^�p�^���̊J�n�ʒu
     * @param newpos oldpos�ɒu�������f�[�^�p�^���̊J�n�ʒu
     */
    private void contractNode(int oldpos, int newpos) {
        int oldnode = oldpos & (this.DictionarySize - 1);
        int newnode = newpos & (this.DictionarySize - 1);
        int parentpos = this.parent[oldnode];
        int parentnode = parentpos & (this.DictionarySize - 1);

        if (parentpos != HashAndBinaryTreeSearch.ROOT_NODE) {
            if (oldpos == this.small[parentnode]) {
                this.small[parentnode] = newpos;
            } else {
                this.large[parentnode] = newpos;
            }
        } else {
            this.hashTable[this.hashMethod.hash(oldpos)] = newpos;
        }

        if (newpos != HashAndBinaryTreeSearch.UNUSED) {
            this.parent[newnode] = parentpos;
        }

        this.parent[oldnode] = HashAndBinaryTreeSearch.UNUSED;
    }

    /**
     * oldpos ��񕪖؂Ɋ܂܂�Ȃ��V�����f�[�^�p�^�� newpos �Œu��������B
     * newpos ���񕪖؂Ɋ܂܂�Ă���悤�ȏꍇ�ɂ́A
     * ��������deleteNode(newpos) ����Ȃǂ��āA
     * �񕪖؂���O���K�v������B
     * oldpos �͓񕪖؂���폜�����B
     *
     * @param oldpos �폜����f�[�^�p�^���̊J�n�ʒu
     * @param newpos oldpos�ɒu�������f�[�^�p�^���̊J�n�ʒu
     */
    private void replaceNode(int oldpos, int newpos) {
        int oldnode = oldpos & (this.DictionarySize - 1);
        int newnode = newpos & (this.DictionarySize - 1);
        int parentpos = this.parent[oldnode];
        int parentnode = parentpos & (this.DictionarySize - 1);

        if (parentpos != HashAndBinaryTreeSearch.ROOT_NODE) {
            if (oldpos == this.small[parentnode]) {
                this.small[parentnode] = newpos;
            } else {
                this.large[parentnode] = newpos;
            }
        } else {
            this.hashTable[this.hashMethod.hash(oldpos)] = newpos;
        }

        this.parent[newnode] = parentpos;
        this.small[newnode] = this.small[oldnode];
        this.large[newnode] = this.large[oldnode];
        if (this.small[newnode] != HashAndBinaryTreeSearch.UNUSED) {
            this.parent[this.small[newnode] & (this.DictionarySize - 1)] = newpos;
        }
        if (this.large[newnode] != HashAndBinaryTreeSearch.UNUSED) {
            this.parent[this.large[newnode] & (this.DictionarySize - 1)] = newpos;
        }

        this.parent[oldnode] = HashAndBinaryTreeSearch.UNUSED;
        this.large[oldnode] = HashAndBinaryTreeSearch.UNUSED;
        this.small[oldnode] = HashAndBinaryTreeSearch.UNUSED;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  other
    //------------------------------------------------------------------
    //  private int findNext( int position )
    //  private void slideTree( int[] src, int[] dst, int start, int end, int width )
    //------------------------------------------------------------------

    /**
     * deleteNode( position ) �����Ƃ��ɁA
     * small �� large �̗����̗t�����������ꍇ�A
     * position �̂���n�܂�f�[�^�p�^����
     * �u��������ׂ� �f�[�^�p�^���̊J�n�ʒu��T���o���B
     *
     * @param position �u����������f�[�^�p�^���̊J�n�ʒu
     *
     * @return position �̂���n�܂�f�[�^�p�^����
     *         �u��������ׂ� �f�[�^�p�^���̊J�n�ʒu
     */
    private int findNext(int position) {
        int node = position & (this.DictionarySize - 1);

        position = this.small[node];
        node = position & (this.DictionarySize - 1);
        while (HashAndBinaryTreeSearch.UNUSED != this.large[node]) {
            position = this.large[node];
            node = position & (this.DictionarySize - 1);
        }

        return position;
    }


    /**
     * slide() ���ɁA�񕪖؂̊e�v�f���ړ������邽�߂Ɏg�p����B
     *
     * @param array ��������z��
     */
    private void slideTree(int[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = (0 <= array[i]
                    ? array[i] - this.DictionarySize
                    : array[i]);
        }
    }

}
//end of HashAndBinaryTreeSearch.java
