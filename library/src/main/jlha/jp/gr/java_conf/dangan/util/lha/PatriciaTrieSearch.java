//start of PatriciaTrieSearch.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * PatriciaTrieSearch.java
 * 
 * Copyright (C) 2001-2002  Michel Ishizuka  All rights reserved.
 * 
 * �ȉ��̏����ɓ��ӂ���Ȃ�΃\�[�X�ƃo�C�i���`���̍Ĕz�z�Ǝg�p��
 * �ύX�̗L���ɂ�����炸������B
 * 
 * �P�D�\�[�X�R�[�h�̍Ĕz�z�ɂ����Ē��쌠�\���� ���̏����̃��X�g
 *     ����щ��L�̐�������ێ����Ȃ��Ă͂Ȃ�Ȃ��B
 * 
 * �Q�D�o�C�i���`���̍Ĕz�z�ɂ����Ē��쌠�\���� ���̏����̃��X�g
 *     ����щ��L�̐��������g�p�������������� ���̑��̔z�z������
 *     �܂ގ����ɋL�q���Ȃ���΂Ȃ�Ȃ��B
 * 
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

import jp.gr.java_conf.dangan.io.Bits;

import java.math.BigInteger;

//import exceptions

/**
 * PATRICIA Trie ���g�p���� LzssSearchMethod �̎����B
 * 
 * <pre>
 * -- revision history --
 * $Log: PatriciaTrieSearch.java,v $
 * Revision 1.2  2002/12/10 22:28:55  dangan
 * [bug fix]
 *     put( DictionarySize * 2 )
 *     searchAndPut( DictionarySize * 2 ) �ɑΉ����Ă��Ȃ������̂��C���B
 *
 * Revision 1.1  2002/12/04 00:00:00  dangan
 * [change]
 *     LzssSearchMethod �̃C���^�t�F�C�X�ύX�ɍ��킹�ăC���^�t�F�C�X�ύX�B
 * [maintenance]
 *     �\�[�X����
 *
 * Revision 1.0  2002/08/15 00:00:00  dangan
 * add to version control
 * [bug fix]
 *     contractNode �� hashtable ����̘A�����X�g�Ɍq���̂�Y��Ă����C���B
 *     �z�� �� PatriciaTrieSearch.ROOT_NODE(-1) �ŃA�N�Z�X���Ă����̂��C���B
 * [maintenance]
 *     �\�[�X����
 *     �^�u�p�~
 *     ���C�Z���X���̏C��
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.2 $
 */
public class PatriciaTrieSearch implements LzssSearchMethod{


    //------------------------------------------------------------------
    //  class field
    //------------------------------------------------------------------
    //  private static final int UNUSED
    //------------------------------------------------------------------
    /**
     * �g�p����Ă��Ȃ����������l�B
     * parent[node] �� UNUSED ������ꍇ�� node �͖��g�p�� node �ł���B
     * prev[node], next[node] �� UNUSED ������ꍇ�́A
     * �����瑤�ɌZ��m�[�h���������Ƃ������B
     */
    private static final int UNUSED = 0;


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
     * LZSS ���k�Ɏg�p�����l�B
     * �ő��v���������B
     */
    private int MaxMatch;

    /**
     * LZSS ���k/�񈳏k��臒l�B
     * ��v���� ���̒l�ȏ�ł���΁A���k�R�[�h���o�͂���B
     */
    private int Threshold;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  Text Buffer
    //------------------------------------------------------------------
    //  private byte[] TextBuffer
    //  private int DictionaryLimit
    //------------------------------------------------------------------
    /**
     * LZSS ���k���{�����߂̃o�b�t�@�B
     * LzssSearchMethod �̎������ł͓ǂݍ��݂̂݋������B
     */
    private byte[] TextBuffer;

    /**
     * �����̌��E�ʒu�B
     * TextBuffer�O���̎����̈�ɖ����Ƀf�[�^�������ꍇ��
     * �����̈�ɂ���s��̃f�[�^(Java �ł�0)���g�p����
     * ���k���s���Ȃ��悤�ɂ���B
     */
    private int DictionaryLimit;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  PATRICIA TRIE
    //------------------------------------------------------------------
    //  private int[] parent
    //  private int[] hashTable
    //  private int[] prev
    //  private int[] next
    //  private int[] position
    //  private int[] level
    //  private int[] childnum
    //  private int avail
    //  private int shift
    //------------------------------------------------------------------
    /**
     * �e�̃m�[�h�ԍ��������B
     * parent[node] �� node �̐e�m�[�h�̔ԍ��������B
     */
    private int[] parent;

    /**
     * �q�̃n�b�V���l�������B
     * hashTable[ hash( node, ch ) ] ��
     * node �̕��� ch �̎q�m�[�h�̃n�b�V���l�������B
     */
    private int[] hashTable;

    /**
     * hashTable ����A�Ȃ�o�����A�����X�g�̈ꕔ�B
     * �����n�b�V���l������ ��O�̃m�[�h�̃m�[�h�ԍ��������B
     * prev[ node ] �� node �Ɠ����n�b�V���l�������A
     * �A�����X�g���� node �̈�O�Ɉʒu����m�[�h�� node �ԍ��B
     * prev[ node ] �� ���l�̏ꍇ�͑S�r�b�g���]�����n�b�V���l�������B
     */
    private int[] prev;

    /**
     * hashTable ����A�Ȃ�o�����A�����X�g�̈ꕔ�B
     * �����n�b�V���l������ ���̃m�[�h�̃m�[�h�ԍ��������B
     * next[ node ] �� node �Ɠ����n�b�V���l�������A
     * �A�����X�g���� node �̈���Ɉʒu����m�[�h�� node �ԍ��B
     * 
     * �܂��A�t�łȂ��m�[�h�Ɋւ��Ă� next �� avail �� ���g�p�ȃm�[�h��
     * �X�^�b�N(������A�����X�g)���\������B
     * 
     * ����ɁA���S��v�����������ߍ폜���ꂽ�t�m�[�h�ŁA
     * PATRICIA Trie ���ɑ��݂��Ă���t�m�[�h�ւ̈�����A�����X�g���\������B
     */
    private int[] next;

    /**
     * �m�[�h�� TextBuffer ���̃f�[�^�p�^���̊J�n�ʒu�������B
     * position[ node ] �� node �̃f�[�^�p�^���̊J�n�ʒu�������B
     */
    private int[] position;

    /**
     * �m�[�h�� ����ʒu�������B
     * level[ node ] �� node �̎q�m�[�h�����򂷂�ʒu�������B
     */
    private int[] level;

    /**
     * �m�[�h�̎q�m�[�h�̐��������B
     * childnum[ node ] �� node �̎q�m�[�h�̐��������B
     */
    private int[] childnum;

    /**
     * next ���\�����関�g�p�m�[�h�̃X�^�b�N�̃X�^�b�N�|�C���^�B
     */
    private int avail;

    /**
     * �n�b�V�����Ɏg�p����V�t�g�l
     */
    private int shift;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  other
    //------------------------------------------------------------------
    //  private int lastMatchPos
    //  private int lastMatchLen
    //------------------------------------------------------------------
    /**
     * �Ō�� searchAndPut() �܂��� put() �œ���ꂽ
     * ����ꂽ PatriciaTrie���̍Œ���v�ʒu
     */
    private int lastMatchPos;

    /**
     * �Ō�� searchAndPut() �܂��� put() ��
     * ����ꂽ PatriciaTrie���̍Œ���v��
     */
    private int lastMatchLen;


    //------------------------------------------------------------------
    //  constructer
    //------------------------------------------------------------------
    //  private PatriciaTreeSearch()
    //  public PatriciaTreeSearch( int DictionarySize, int MaxMatch,
    //                             int Threshold, byte[] TextBuffer )
    //------------------------------------------------------------------
    /**
     * �f�t�H���g�R���X�g���N�^�B
     * �g�p�s�B
     */
    private PatriciaTrieSearch(){   }

    /**
     * �R���X�g���N�^�B
     * PATRICIA Trie ���g�p���������@�\���\�z����B
     * 
     * @param DictionarySize �����T�C�Y
     * @param MaxMatch       �Œ���v��
     * @param Threshold      ���k�A�񈳏k��臒l
     * @param TextBuffer     LZSS���k���{�����߂̃o�b�t�@
     */
    public PatriciaTrieSearch( int    DictionarySize,
                               int    MaxMatch,
                               int    Threshold,
                               byte[] TextBuffer ){

        this.DictionarySize  = DictionarySize;
        this.MaxMatch        = MaxMatch;
        this.Threshold       = Threshold;
        this.TextBuffer      = TextBuffer;
        this.DictionaryLimit = this.DictionarySize;

        this.parent          = new int[ this.DictionarySize * 2 ];
        this.prev            = new int[ this.DictionarySize * 2 ];
        this.next            = new int[ this.DictionarySize * 2 ];
        this.position        = new int[ this.DictionarySize ];
        this.level           = new int[ this.DictionarySize ];
        this.childnum        = new int[ this.DictionarySize ];
        this.hashTable       = new int[ 
                PatriciaTrieSearch.generateProbablePrime( 
                        this.DictionarySize + ( this.DictionarySize >> 2 ) ) ];

        for( int i = 2 ; i < this.DictionarySize ; i++ ){
            this.next[i] = i - 1;
        }
        this.avail = this.DictionarySize - 1;

        for( int i = 0 ; i < this.DictionarySize * 2 ; i++ ){
            this.parent[ i ]    = PatriciaTrieSearch.UNUSED;
        }

        for( int i = 0 ; i < this.hashTable.length ; i++ ){
            this.hashTable[ i ] = PatriciaTrieSearch.UNUSED;
        }

        this.shift = Bits.len( this.DictionarySize ) - 8;

        this.lastMatchLen = 0;
        this.lastMatchPos = 0;
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
     * PATRICIA Trie �ɓo�^����B<br>
     * 
     * @param position TextBuffer���̃f�[�^�p�^���̊J�n�ʒu
     */
    public void put( int position ){

        //------------------------------------------------------------------
        //  PATRICIA Trie ����ł��Â��f�[�^�p�^�����폜
        int posnode = ( position & ( this.DictionarySize - 1 ) ) + this.DictionarySize;
        this.deleteNode( posnode );

        //------------------------------------------------------------------
        //  PATRICIA Trie ����Œ���v������
        int matchnode = -1;
        int matchpos  = position;
        int scannode;
        int matchlen;
        if( 3 < this.lastMatchLen ){

            //�O��̈�v����臒l���傫����΁A
            //�t���� lastMatchLen - 1 �̈�v����������B
            scannode  = ( this.lastMatchPos + 1 ) | this.DictionarySize;

            //�Œ���v�����������߂� scannode �� 
            //PATRICIA Trie �����菜����Ă���ꍇ�̏���
            while( this.parent[ scannode ] == PatriciaTrieSearch.UNUSED ){
                scannode = this.next[ scannode ];
            }

            //�t���� ���Ԃɐe�ւƒH����
            //lastMatchLen - 1 �ȉ��� level �����m�[�h��T���B
            int node  = this.parent[ scannode ];
            this.lastMatchLen--;
            while( 0 < node 
                && this.lastMatchLen <= this.level[ node ] ){
                scannode = node;
                node = this.parent[ node ];
            }

            //����ɐe�ւƒH���� position ���X�V���Ă����B
            while( 0 < node  ){
                this.position[ node ] = position;
                node = this.parent[ node ];
            }

            matchlen  = this.lastMatchLen;
        }else{

            //PATRICIA Trie �� ������H��B
            scannode  = this.child( this.TextBuffer[ position ] - 128, 
                                    this.TextBuffer[ position + 1 ] & 0xFF );
            matchlen  = 2;

            if( scannode == PatriciaTrieSearch.UNUSED ){
                //���� position ��ǉ�����B
                this.attachNode( this.TextBuffer[ position ] - 128, posnode, 
                                 this.TextBuffer[ position + 1 ] & 0xFF );
                this.lastMatchLen = matchlen;
                return;
            }
        }

        while( true ){
            int max;
            if( scannode < this.DictionarySize ){
                max       = this.level[ scannode ];
                matchnode = scannode;
                matchpos  = this.position[ scannode ];
            }else{
                max       = this.MaxMatch;
                matchnode = scannode;
                matchpos  = ( position <= scannode
                            ? scannode - this.DictionarySize
                            : scannode );
            }

            while( matchlen < max
                && ( this.TextBuffer[ matchpos + matchlen ] 
                  == this.TextBuffer[ position + matchlen ] ) ){
                matchlen++;
            }

            if( matchlen == max && matchlen < this.MaxMatch ){
                this.position[ scannode ] = position;
                scannode = this.child( scannode, 
                                       this.TextBuffer[ position + matchlen ] & 0xFF );

                if( scannode == PatriciaTrieSearch.UNUSED ){
                    this.attachNode( matchnode, posnode, 
                                     this.TextBuffer[ position + matchlen ] & 0xFF );
                    break;
                }else{
                    matchlen++;
                }
            }else if( matchlen < max ){
                //matchnode �� position �𕪊򂳂���B
                this.splitNode( matchnode, matchpos, posnode, position, matchlen );
                break;
            }else{
                //���S��v�𔭌��A�m�[�h��u��������B
                this.replaceNode( matchnode, posnode );
                this.next[ matchnode ] = position;
                break;
            }
        }

        //�������ʂ�ۑ�
        this.lastMatchLen = matchlen;
        this.lastMatchPos = matchpos;
    }

    /**
     * PATRICIA Trie �ɓo�^���ꂽ�f�[�^�p�^������ 
     * position ����n�܂�f�[�^�p�^����
     * �Œ��̈�v�������̂��������A
     * ������ position ����n�܂�f�[�^�p�^���� 
     * PATRICIA Trie �ɓo�^����B<br>
     * 
     * @param position TextBuffer���̃f�[�^�p�^���̊J�n�ʒu�B
     * 
     * @return ��v�����������ꍇ��
     *         LzssOutputStream.createSearchReturn 
     *         �ɂ���Đ������ꂽ��v�ʒu�ƈ�v���̏������l�A
     *         ��v��������Ȃ������ꍇ��
     *         LzssOutputStream.NOMATCH�B
     * 
     * @see LzssOutputStream#createSearchReturn(int,int)
     * @see LzssOutputStream#NOMATCH
     */
    public int searchAndPut( int position ){

        //------------------------------------------------------------------
        //  PATRICIA Trie ����ł��Â��f�[�^�p�^�����폜
        int posnode = ( position & ( this.DictionarySize - 1 ) ) + this.DictionarySize;
        this.deleteNode( posnode );

        //------------------------------------------------------------------
        //  PATRICIA Trie ����Œ���v������
        int matchnode = -1;
        int matchpos  = position;
        int scannode  = 0;
        int matchlen  = 0;
        if( 3 < this.lastMatchLen ){

            //�O��̈�v����臒l���傫����΁A
            //�t���� lastMatchLen - 1 �̈�v����������B
            scannode  = ( this.lastMatchPos + 1 ) | this.DictionarySize;

            //�Œ���v�����������߂� scannode �� 
            //PATRICIA Trie �����菜����Ă���ꍇ�̏���
            while( this.parent[ scannode ] == PatriciaTrieSearch.UNUSED ){
                scannode = this.next[ scannode ];
            }

            //�t���� ���Ԃɐe�ւƒH����
            //lastMatchLen - 1 �ȉ��� level �����m�[�h��T���B
            int node  = this.parent[ scannode ];
            this.lastMatchLen--;
            while( 0 < node 
                && this.lastMatchLen <= this.level[ node ] ){
                scannode = node;
                node = this.parent[ node ];
            }

            //����ɐe�ւƒH���� position ���X�V���Ă����B
            while( 0 < node  ){
                this.position[ node ] = position;
                node = this.parent[ node ];
            }

            matchlen  = this.lastMatchLen;
        }else{
            //PATRICIA Trie �� ������H��B
            scannode  = this.child( this.TextBuffer[ position ] - 128, 
                                    this.TextBuffer[ position + 1 ] & 0xFF );
            matchlen  = 2;
        }

        // scannode == UNUSED �ƂȂ�̂� lastMatchLen ��臒l��菬�����Ƃ��̂݁B
        if( scannode != PatriciaTrieSearch.UNUSED ){
            while( true ){
                int max;
                if( scannode < this.DictionarySize ){
                    max       = this.level[ scannode ];
                    matchnode = scannode;
                    matchpos  = this.position[ scannode ];
                }else{
                    max       = this.MaxMatch;
                    matchnode = scannode;
                    matchpos  = ( position <= scannode
                                ? scannode - this.DictionarySize
                                : scannode );
                }

                while( matchlen < max
                    && ( this.TextBuffer[ matchpos + matchlen ] 
                      == this.TextBuffer[ position + matchlen ] ) ){
                    matchlen++;
                }

                if( matchlen == max && matchlen < this.MaxMatch ){
                    this.position[ scannode ] = position;
                    scannode = this.child( scannode, 
                                           this.TextBuffer[ position + matchlen ] & 0xFF );

                    if( scannode == PatriciaTrieSearch.UNUSED ){
                        //matchnode �� position ��ǉ�����B
                        this.attachNode( matchnode, posnode, 
                                         this.TextBuffer[ position + matchlen ] & 0xFF );
                        break;
                    }else{
                        matchlen++;
                    }
                }else if( matchlen < max ){
                    //matchnode �� position �𕪊򂳂���B
                    this.splitNode( matchnode, matchpos, posnode, position, matchlen );
                    break;
                }else{
                    //���S��v�𔭌��A�m�[�h��u��������B
                    this.replaceNode( matchnode, posnode );
                    this.next[ matchnode ] = position;
                    break;
                }
            }
        }else{ //if( scannode != PatriciaTrieSearch.UNUSED )
            //���� position ��ǉ�����B
            this.attachNode( this.TextBuffer[ position ] - 128, posnode, 
                             this.TextBuffer[ position + 1 ] & 0xFF );
            matchlen = 0;
        }

        //�������ʂ�ۑ�
        this.lastMatchLen = matchlen;
        this.lastMatchPos = matchpos;


        //------------------------------------------------------------------
        //  ���\�b�h�擪�� PATRICIA Trie ����폜�����f�[�^�p�^�����`�F�b�N����B
        scannode = position - this.DictionarySize;
        if( this.DictionaryLimit <= scannode ){
            int len = 0;
            while( this.TextBuffer[ scannode + len ]
                == this.TextBuffer[ position + len ] )
                if( this.MaxMatch <= ++len ) break;

            if( matchlen < len ){
                matchpos = scannode;
                matchlen = len;
            }
        }

        //------------------------------------------------------------------
        //  �Œ���v���Ăяo�����ɕԂ��B
        if( this.Threshold <= matchlen ){
            return LzssOutputStream.createSearchReturn( matchlen, matchpos );
        }else{
            return LzssOutputStream.NOMATCH;
        }
    }

    /**
     * PATRICIA Trie �ɓo�^���ꂽ�f�[�^�p�^����������
     * position ����n�܂�f�[�^�p�^����
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
     * @see LzssOutputStream#createSearchReturn(int,int)
     * @see LzssOutputStream#NOMATCH
     */
    public int search( int position, int lastPutPos ){

        //------------------------------------------------------------------
        //  PATRICIA Trie �ɓo�^����Ă��Ȃ��f�[�^�p�^����
        //  �P���Ȓ��������Ō�������B
        int scanlimit = Math.max( this.DictionaryLimit, lastPutPos );
        int scanpos   = position - 1;
        int matchlen  = 0;
        int matchpos  = 0;

        byte[] buf    = this.TextBuffer;
        int max       = Math.min( this.TextBuffer.length,
                                  position + this.MaxMatch );
        int s         = 0;
        int p         = 0;
        int len       = 0;
        while( scanlimit < scanpos ){
            s = scanpos;
            p = position;
            while( buf[ s ] == buf[ p ] ){
                s++;
                p++;
                if( max <= p ) break;
            }

            len = p - position;
            if( matchlen < len ){
                matchpos = scanpos;
                matchlen = len;
                if( max <= p ) break;
            }
            scanpos--;
        }


        //------------------------------------------------------------------
        //  PATRICIA Trie ��T��
        if( 2 < this.TextBuffer.length - position  ){
            int matchnode = this.child( this.TextBuffer[ position ] - 128, 
                                        this.TextBuffer[ position + 1 ] & 0xFF );
            scanlimit = Math.max( this.DictionaryLimit, 
                                  position - this.DictionarySize );
            len       = 2;
            while( matchnode != PatriciaTrieSearch.UNUSED ){
                int maxlen;
                if( matchnode < this.DictionarySize ){
                    maxlen  = this.level[ matchnode ];
                    scanpos = this.position[ matchnode ];
                }else{
                    maxlen  = this.MaxMatch;
                    scanpos = ( lastPutPos < matchnode
                              ? matchnode - this.DictionarySize
                              : matchnode );
                }

                if( scanlimit <= scanpos ){
                    max = Math.min( this.TextBuffer.length,
                                    position + maxlen );
                    s   = scanpos  + len;
                    p   = position + len;
                    if( p < max ){
                        while( buf[ s ] == buf[ p ] ){
                            s++;
                            p++;
                            if( max <= p ) break;
                        }
                    }

                    len = p - position;
                    if( matchlen < len ){
                        matchpos = scanpos;
                        matchlen = len;
                    }

                    if( len == maxlen && matchlen < this.MaxMatch ){
                        if( position + len < this.TextBuffer.length ){
                            matchnode = this.child( matchnode, 
                                                    this.TextBuffer[ position + len ] & 0xFF );

                            if( matchnode != PatriciaTrieSearch.UNUSED ){
                                len++;
                            }
                        }else{
                            break;
                        }
                    }else{  //maxlen �ɖ����Ȃ���v������������ ���S��v����������
                        break;
                    }
                }else{ //if( scanlimit <= scanpos ) ��v�����p�^���͌������E�𒴂��Ă����B
                    break;
                }
            }   //while( matchnode != PatriciaTrieSearch.UNUSED )
        }   //if( 2 <= this.TextBuffer.length - position  )


        //------------------------------------------------------------------
        //  �Œ���v���Ăяo�����ɕԂ��B
        if( this.Threshold <= matchlen ){
            return LzssOutputStream.createSearchReturn( matchlen, matchpos );
        }else{
            return LzssOutputStream.NOMATCH;
        }
    }

    /**
     * TextBuffer����position�܂ł̃f�[�^��
     * �O���ֈړ�����ہA����ɉ����� LzssSearchMethod
     * ���̃f�[�^�� TextBuffer���̃f�[�^�Ɩ������Ȃ���
     * ���ɑO���ֈړ����鏈�����s���B 
     */
    public void slide(){
        this.DictionaryLimit = Math.max( 0, this.DictionaryLimit - this.DictionarySize );
        this.lastMatchPos   -= this.DictionarySize;

        for( int i = 0 ; i < this.position.length ; i++ ){
            int pos = this.position[i] - this.DictionarySize;
            if( 0 < pos ){
                this.position[i] = pos;
            }else{
                this.position[i] = 0;
            }
        }
    }

    /**
     * put() �� LzssSearchMethod�Ƀf�[�^��
     * �o�^����Ƃ��Ɏg�p�����f�[�^�ʂ𓾂�B
     * PatriciaTrieSearch �ł́A��� MaxMatch ��Ԃ��B
     * 
     * @return ��� MaxMatch
     */
    public int putRequires(){
        return this.MaxMatch;
    } 

    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  manipulate node
    //------------------------------------------------------------------
    //  private void splitNode( int oldnode, int oldpos, int position, int splitLen )
    //  private void deleteNode( int node )
    //  private void attatchNode( int parentnode, int childnode, int pos )
    //  private void replaceNode( int oldnode, int newnode )
    //  private void contractNode( int node )
    //------------------------------------------------------------------
    /**
     * oldnode �� splitLen �ŕ��򂳂���B
     * oldnode �̂������ʒu�ɂ͐V�����m�[�h���V�݂���A
     * �V�����m�[�h�� oldnode �� position ���q�Ɏ��B
     * 
     * @param oldnode  ���򂳂���m�[�h 
     * @param oldpos   oldnode ���w���f�[�^�p�^���̊J�n�ʒu
     * @param posnode  position �p�m�[�h
     * @param position TextBuffer ���̃f�[�^�p�^���̊J�n�ʒu
     * @param splitLen �f�[�^�p�^�����̕���ʒu
     */
    private void splitNode( int oldnode, int oldpos, int posnode, int position, int splitLen ){
        //�X�^�b�N���� �V�����m�[�h���擾����B
        int newnode = this.avail;
        this.avail  = this.next[ newnode ];

        this.replaceNode( oldnode, newnode );
        this.level[ newnode ]     = splitLen;
        this.position[ newnode ]  = position;
        this.childnum[ newnode ]  = 0;

        this.attachNode( newnode, oldnode,  
                         this.TextBuffer[ oldpos   + splitLen ] & 0xFF );
        this.attachNode( newnode, posnode, 
                         this.TextBuffer[ position + splitLen ] & 0xFF );
    }


    /**
     * PATRICIA Trie ����t�ł��� node ���폜����B
     * �K�v�ł���� node �̐e�m�[�h�̌J�グ�������s���B
     * 
     * @param node �폜����t�m�[�h
     */
    private void deleteNode( int node ){
        if( this.parent[ node ] != PatriciaTrieSearch.UNUSED ){
            int parent = this.parent[ node ];
            int prev   = this.prev[ node ];
            int next   = this.next[ node ];

            this.parent[ node ] = PatriciaTrieSearch.UNUSED;
            this.prev[ node ]   = PatriciaTrieSearch.UNUSED;
            this.next[ node ]   = PatriciaTrieSearch.UNUSED;

            if( 0 <= prev ){
                this.next[ prev ]       = next;
            }else{
                this.hashTable[ ~prev ] = next;
            }
            this.prev[ next ] = prev;

            if( 0 < parent ){ //parent �� PATRICIA Trie �̍��Ŗ����ꍇ true �ƂȂ������
                this.childnum[ parent ]--;

                if( this.childnum[ parent ] <= 1 ){
                    this.contractNode( this.child( parent,
                                        this.TextBuffer[ this.position[ parent ]
                                                        + this.level[ parent ] ]
                                        & 0xFF ) );
                }
            }
        }
    }

    /**
     * parentnode �� childnode ��ǉ�����B
     * 
     * @param parentnode childnode ��ǉ�����Ώۂ̐e�m�[�h
     * @param childnode  parentnode �ɒǉ�����m�[�h
     * @param pos        TextBuffer�����ݏ����ʒu�B
     *                   �t�� position ���m�肷�邽�߂Ɏg�p�����B
     */
    private void attachNode( int parentnode, int childnode, int ch ){
        int hash                 = this.hash( parentnode, ch );
        int node                 = this.hashTable[ hash ];
        this.hashTable[ hash ]   = childnode;
        this.parent[ childnode ] = parentnode;
        this.prev[ childnode ]   = ~hash;
        this.next[ childnode ]   = node;
        this.prev[ node ]        = childnode;

        if( 0 < parentnode ){
            this.childnum[ parentnode ]++;
        }
    }

    /**
     * oldnode �� newnode �����ւ���B
     * newnode �͎q�m�[�h�Ƃ̊֌W��ێ�����B
     * oldnode �͒u���������� PATRICIA Trie �����菜�����B
     * 
     * @param oldnode ����ւ����� Trie ����폜�����m�[�h
     * @param newnode oldnode �̂������ʒu�֐ڑ������m�[�h
     */
    private void replaceNode( int oldnode, int newnode ){
        this.parent[ newnode ]   = this.parent[ oldnode ];
        this.prev[ newnode ]     = this.prev[ oldnode ];
        this.next[ newnode ]     = this.next[ oldnode ];

        this.prev[ this.next[ newnode ] ] = newnode;

        if( this.prev[ newnode ] < 0 ){
            this.hashTable[ ~this.prev[ newnode ] ] = newnode;
        }else{
            this.next[ this.prev[ newnode ] ]       = newnode;
        }

        this.parent[ oldnode ] = PatriciaTrieSearch.UNUSED;
        this.prev[ oldnode ]   = PatriciaTrieSearch.UNUSED;
        this.next[ oldnode ]   = PatriciaTrieSearch.UNUSED;
    }

    /**
     * �Z��̖����Ȃ��� node �������グ��B
     * node �̐e�m�[�h�� PATRICIA Trie ����폜����A
     * ����� node �����̈ʒu�ɐڑ������B
     * �Z�킪�������ǂ����� ����͌Ăяo�������s���B
     * 
     * @param node �����グ��m�[�h
     */
    private void contractNode( int node ){
        int parentnode    = this.parent[ node ];

        this.prev[ this.next[ node ] ] = this.prev[ node ];
        if( 0 <= this.prev[ node ] ){
            this.next[ this.prev[ node ] ]        = this.next[ node ];
        }else{
            this.hashTable[ ~ this.prev[ node ] ] = this.next[ node ];
        }
        this.replaceNode( parentnode, node );

        //�g�p����Ȃ��Ȃ��� parentnode ���X�^�b�N�ɕԊ҂���B
        this.next[ parentnode ] = this.avail;
        this.avail              = parentnode;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  other
    //------------------------------------------------------------------
    //  public void slideTree( int[] src, int[] dst, int width )
    //  private int child( int parent, int ch )
    //  private int hash( int node, int ch )
    //------------------------------------------------------------------
    /**
     * slide ���� Trie �̊e�v�f���ړ������鏈�����s���B
     * 
     * @param src   �ړ���
     * @param dst   �ړ���
     * @param width �ړ���
     */
    private void slideTree( int[] src, int[] dst, int width ){
        for( int i = 0 ; i < this.DictionarySize ; i++ )
            dst[i] = ( src[ i ] < this.DictionarySize
                     ? src[ i ]
                     : ( ( src[ i ] - width ) & ( this.DictionarySize - 1 ) ) 
                         + this.DictionarySize );

        for( int i = this.DictionarySize ; i < src.length ; i++  )
            dst[ ( ( i - width ) & ( this.DictionarySize - 1 ) ) 
                 + this.DictionarySize ] = ( src[ i ] < this.DictionarySize
                                           ? src[ i ]
                                           : ( ( src[ i ] - width ) 
                                               & ( this.DictionarySize - 1 ) )
                                             + this.DictionarySize );
    }

    /**
     * parent ���� ch �ŕ��򂵂��q�𓾂�B
     * �m�[�h�������ꍇ�� UNUSED ��Ԃ��B
     * 
     * @param parent �e�m�[�h
     * @param ch     ���򕶎�
     * 
     * @return �q�m�[�h
     */
    private int child( int parent, int ch ){
        int node = this.hashTable[ this.hash( parent, ch ) ];

        //this.parent[ PatriciaTrieSearch.UNUSED ] = parent;
        while( node != PatriciaTrieSearch.UNUSED
            && this.parent[ node ] != parent ){
            node = this.next[ node ];
        }

        return node;
    }

    /**
     * node �� ch ���� �n�b�V���l�𓾂�
     * 
     * @param node �m�[�h
     * @param ch   ���򕶎�
     * 
     * @return �n�b�V���l
     */
    private int hash( int node, int ch ){
        return ( node + ( ch << this.shift ) + 256 ) % this.hashTable.length;
    }

    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  generate prime
    //------------------------------------------------------------------
    //  private static int generateProbablePrime( int num )
    //------------------------------------------------------------------
    /**
     * num �ȏ�̍ł������� �f��(�������͋[���f��)�𐶐�����B 
     * �߂�l�� �f���łȂ��m���� 1/256 �ȉ��ł���B
     * 
     * @param num ���̒l�ȏ�̑f���𐶐�����B
     *
     * @return �������ꂽ�f��(�������͋[���f��)
     */
    private static int generateProbablePrime( int num ){
        num = num + ( ( num & 1 ) == 0 ? 1 : 0 );

        while( !(new BigInteger(Integer.toString(num))).isProbablePrime( 8 ) ){
            num += 2;
            num = num + ( ( num % 3 ) == 0 ? 2 : 0 );
            num = num + ( ( num % 5 ) == 0 ? 2 : 0 );
            num = num + ( ( num % 7 ) == 0 ? 2 : 0 );
        }
        return num;
    }

}

// 
//  
//  //------------------------------------------------------------------
//  //  local method
//  //------------------------------------------------------------------
//  //  check
//  //------------------------------------------------------------------
//  //  private void checkTrie( int pos )
//  //  private void checkNode( int node, int pos )
//  //  private void writeNode( int node )
//  //------------------------------------------------------------------
//  /**
//   * Trie�S�̂̃`�F�b�N���s���B
//   * 
//   * @param pos ���ݏ����ʒu�B
//   * 
//   * @exception RuntimeException Trie ������Ă����ꍇ�B
//   */
//  private void checkTrie( int pos ){
//      for( int i = -256 ; i < 0 ; i++ ){
//          this.checkNode( i, pos );
//      }
//
//      for( int i = 1 ; i < this.DictionarySize ; i++ ){
//          if( this.parent[ i ] != PatriciaTrieSearch.UNUSED ){
//              this.checkNode( i, pos );
//          }
//      }
//  }
//
//  /**
//   * �t�łȂ� Node �̃`�F�b�N���s���B
//   * 
//   * �`�F�b�N���ڂ�
//   * (1) �e�q�֌W
//   * (2) position �ɖ������������B
//   * (3) level �ɖ������������B
//   * (4) node �� this.childnum[node] �̎q���������Ă��鎖�B
//   * ��4���ځB
//   * 
//   * @param node �`�F�b�N����m�[�h
//   * @param pos  ���ݏ����ʒu
//   * 
//   * @exception RuntimeException ��L�̃`�F�b�N�̉��ꂩ�����s�����ꍇ�B
//   */
//  private void checkNode( int node, int pos ){
//
//      int nlevel;
//      int npos;
//      if( node < 0 ){
//          nlevel = 0;
//          npos   = this.TextBuffer.length;
//      }else{
//          nlevel = this.level[ node ];
//          npos   = this.position[ node ];
//      }
//
//      int childcount = 0;
//      for( int i = 0 ; i < 256 ; i++ ){
//          int child = this.child( node, i );
//
//          if( child != PatriciaTrieSearch.UNUSED ){
//              childcount++;
//
//              if( this.parent[ child ] != node ){
//                  System.out.println( "unlink::parent<->child" );
//                  this.writeNode( node );
//                  this.writeNode( child );
//                  throw new RuntimeException( "Trie Broken" );
//              }
//
//              if( child < this.DictionarySize ){
//                  if( this.level[ child ] <= nlevel ){
//                      System.out.println( "broken hierarchy::level" );
//                      this.writeNode( node );
//                      this.writeNode( child );
//                      throw new RuntimeException( "Trie Broken" );
//                  }
//
//                  if( npos < this.position[ child ] ){
//                      System.out.println( "broken hierarchy::position" );
//                      this.writeNode( node );
//                      this.writeNode( child );
//                      throw new RuntimeException( "Trie Broken" );
//                  }
//                  //this.checkTrie( child, pos );
//              }else{
//                  int childpos = ( pos <= child ? child - this.DictionarySize : child );
//                  if( npos < childpos ){
//                      System.out.println( "broken hierarchy::position" );
//                      this.writeNode( node );
//                      this.writeNode( child );
//                      throw new RuntimeException( "Trie Broken" );
//                  }
//              }
//          }
//      }
//
//      if( 0 < node && node < this.DictionarySize ){
//          if( this.childnum[ node ] != childcount ){
//              System.out.println( "broken hierarchy::childnum" );
//              this.writeNode( node );
//              throw new RuntimeException( "Trie Broken" );
//          }
//      }
//  }
//
//  /**
//   * �m�[�h�̏����o�͂���B
//   * 
//   * @param node �����o�͂���m�[�h
//   */
//  private void writeNode( int node ){
//      if( 0 < node ){
//        System.out.println( "this.parent[" + node + "]  ::" + this.parent[ node ] );
//        System.out.println( "this.prev[" + node + "]    ::" + this.prev[ node ] );
//        System.out.println( "this.next[" + node + "]    ::" + this.next[ node ] );
//        if( node < this.DictionarySize ){
//            System.out.println( "this.childnum[" + node + "]::" + this.childnum[ node ] );
//            System.out.println( "this.position[" + node + "]::" + this.position[ node ] );
//            System.out.println( "this.level[" + node + "]   ::" + this.level[ node ] );
//        }
//    }else if( node < 0 ){
//        System.out.println( "ROOT_NODE                  ::" + node );
//    }else{
//        System.out.println( "UNUSED                     ::" + node );
//    }
//      
//  }

//end of PatriciaTrieSearch.java
