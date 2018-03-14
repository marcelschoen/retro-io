//start of DynamicHuffman.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * DynamicHuffman.java
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
//import exceptions


/**
 * ���I�n�t�}���������N���X�B
 * 
 * <pre>
 * -- revision history --
 * $Log: DynamicHuffman.java,v $
 * Revision 1.0  2002/07/24 00:00:00  dangan
 * add to version control
 * [bug fix]
 *     addLeaf() �ŗt�̐��� 1 ���� 2�ւƑ�������Ƃ���
 *     �ŏ����炠�����t�̏d���� 1 ���ƌ��ߕt���Ă����B
 * [change]
 *     �R���X�g���N�^ DynamicHuffman( int, int ) ��
 *     �J�n���̃n�t�}���؂̃T�C�Y�łȂ� �J�n���̗t�̐���n���悤�ɕύX�B
 * [maintenance]
 *     �\�[�X����
 *     �^�u�p�~
 *     ���C�Z���X���̕ύX
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.0 $
 */
public class DynamicHuffman implements Cloneable{


    //------------------------------------------------------------------
    //  class field
    //------------------------------------------------------------------
    //  public static final int ROOT
    //  private static final int MAX_WEIGHT
    //------------------------------------------------------------------
    /**
     * �n�t�}���؂̃��[�g�������B
     */
    public static final int ROOT = 0;

    /**
     * �n�t�}���؂��č\�z����d��
     */
    private static final int MAX_WEIGHT = 0x8000;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  huffman tree
    //------------------------------------------------------------------
    //  private int[] weight
    //  private int[] child
    //  private int[] parent
    //  private int[] leafs
    //  private int size
    //------------------------------------------------------------------
    /**
     * �Y�����̃m�[�h�̏d���������B
     */
    private int[] weight;

    /**
     * �Y�����̃m�[�h�̎q�m�[�h�̃m�[�h�ԍ���ێ�����
     * �Z������𗘗p���邽�߁A
     * child     �� �������m�[�h�̃m�[�h�ԍ�
     * child - 1 �� �傫���m�[�h�̃m�[�h�ԍ��ƂȂ�B
     * �t�̏ꍇ�̓f�[�^��bit���]�������̂������Ă���B
     */
    private int[] child;

    /**
     * �Y�����̃m�[�h�̐e�m�[�h�̃m�[�h�ԍ���ێ�����
     */
    private int[] parent;

    /**
     * �t�̃m�[�h�ԍ���ێ�����B
     */
    private int[] leafs;

    /**
     * ���݂̃n�t�}���؂̑傫��
     */
    private int size;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private DynamicHuffman()
    //  public DynamicHuffman( int count )
    //  public DynamicHuffman( int max, int first )
    //------------------------------------------------------------------
    /**
     * �f�t�H���g�R���X�g���N�^�B
     * �g�p�s�B
     */
    private DynamicHuffman(){   }

    /**
     * �R���X�g���N�^
     * 
     * @param count �t�̐�
     */
    public DynamicHuffman( int count ){
        this( count, count );
    }

    /**
     * �R���X�g���N�^
     * 
     * @param max   �t�̍ő吔
     * @param start �J�n���̗t�̐� 
     */
    public DynamicHuffman( int max, int first ){
        if( 1 <= first && first <= max ){

            this.weight = new int[ max * 2 - 1 ];
            this.child  = new int[ max * 2 - 1 ];
            this.parent = new int[ max * 2 - 1 ];
            this.leafs  = new int[ max ];
            this.size   = Math.max( 0, first * 2 - 1 );

            //�t�𐶐����Ă����B
            int node = this.size - 1;
            for( int code = 0 ; code < first ; code++, node-- ){
                this.weight[ node ] = 1;
                this.child[ node ]  = ~code;
                this.leafs[ code ]  = node;
            }

            //�}�𐶐����Ă����B
            int child = this.size - 1;
            while( 0 <= node && node != child ){
                this.weight[node]  = this.weight[child] + this.weight[child-1];

                this.child[node]   = child;
                this.parent[child] = this.parent[child-1] = node;

                child -= 2;
                node--;
            }
        }else if( max < first ){
            throw new IllegalArgumentException( "\"max\" must be larger than \"first\"." );
        }else{
            throw new IllegalArgumentException( "\"first\" must be one or more." );
        }
    }


    //------------------------------------------------------------------
    //  method of java.lang.Object
    //------------------------------------------------------------------
    //  public Object clone()
    //------------------------------------------------------------------
    /**
     * ���̃I�u�W�F�N�g�̌��݂̏�Ԃ����R�s�[���쐬���ĕԂ��B
     * 
     * @return ���̃I�u�W�F�N�g�̌��݂̏�Ԃ����R�s�[
     */
    public Object clone(){
        DynamicHuffman clone = new DynamicHuffman();
        clone.weight = (int[])this.weight.clone();
        clone.child  = (int[])this.child.clone();
        clone.parent = (int[])this.parent.clone();
        clone.leafs  = (int[])this.leafs.clone();
        clone.size   = this.size;
        return clone;
    }


    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  access to huffman tree
    //------------------------------------------------------------------
    //  public int codeToNode( int code )
    //  public int childNode( int node )
    //  public int parentNode( int node )
    //------------------------------------------------------------------
    /**
     * �f�[�^����m�[�h�ԍ��𓾂�B
     * 
     * @param code �f�[�^
     * 
     * @return code�̃m�[�h�ԍ�
     */
    public int codeToNode( int code ){
        return this.leafs[code];
    }

    /**
     * �m�[�h���t�łȂ��m�[�h�Ȃ�q�m�[�h�̃m�[�h�ԍ��A
     * �m�[�h���t�Ȃ�m�[�h�̎��f�[�^��S�r�b�g���]�������̂𓾂�B
     * �q�m�[�h�̃m�[�h�ԍ��͌Z������Ɨ��p���邽�߁A<br>
     * node �� 0 �̎q�m�[�h�̏ꍇ childNode( node )<br>
     * node �� 1 �̎q�m�[�h�̏ꍇ childNode( node ) - 1<br>
     * �ƂȂ�B
     * 
     * @param node �m�[�h
     * 
     * @return node �̎q�m�[�h�̃m�[�h�ԍ�
     */
    public int childNode( int node ){
        return this.child[node];
    }

    /**
     * node �̐e�m�[�h�̃m�[�h�ԍ��𓾂�B
     * 
     * @param node �m�[�h
     * 
     * @return node �̐e�m�[�h�̃m�[�h�ԍ��B
     */
    public int parentNode( int node ){
        return this.parent[node];
    }


    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  update huffman tree
    //------------------------------------------------------------------
    //  public void update( int code )
    //  public void addLeaf( int code )
    //------------------------------------------------------------------
    /**
     * code �̏d�݂������悤�Ƀn�t�}���؂��X�V����B
     * 
     * @param code �d�݂𑝂₷�t
     */
    public void update( int code ){
        if( this.weight[ DynamicHuffman.ROOT ] == DynamicHuffman.MAX_WEIGHT ){
            this.rebuildTree();
        }

        int node = this.leafs[code];
        while( DynamicHuffman.ROOT != node ){
            int swapNode = node;
            while( this.weight[swapNode - 1] == this.weight[node]
                && DynamicHuffman.ROOT < swapNode - 1 ){
                swapNode--;
            }

            if( node != swapNode ) this.swap( node, swapNode );

            this.weight[swapNode]++;
            node = this.parent[swapNode];
        }
        this.weight[ DynamicHuffman.ROOT ]++;
    }

    /**
     * �n�t�}���؂� code �������t��ǉ�����B
     * 
     * @param code �t�̎�������
     * 
     * @exception IllegalStateException
     *              �n�t�}���؂��\���ɑ傫������
     *              �t���ǉ��ł��Ȃ��ꍇ
     */
    public void addLeaf( int code ){
        if( this.size < this.weight.length - 1 ){
            int last  = this.size - 1;
            int large = this.size;
            int small = this.size + 1;
            this.child[ large ] = this.child[ last ];
            this.child[ small ] = ~code;
            this.child[ last ]  = small;
            this.weight[ large ] = this.weight[ last ];
            this.weight[ small ] = 0;
            this.leafs[ ~this.child[ large ] ] = large;
            this.leafs[ ~this.child[ small ] ] = small;
            this.parent[ large ] = this.parent[ small ] = last;
            this.size = small + 1;

            if( last == DynamicHuffman.ROOT ){
                this.weight[ last  ] -= 1;
            }

            this.update( code );
        }else{
            throw new IllegalStateException();
        }
    }

    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  private void rebuildTree()
    //  private void swap( int i, int j )
    //------------------------------------------------------------------
    /**
     * �n�t�}���؂��č\�z����B
     * �d�݂� private�Ȓ萔 MAX_WEIGHT �𒴂�������
     * update(int)����Ăяo�����B
     * �S�Ẵm�[�h�̏d�݂� ���悻�����ɂ���B
     */
    private void rebuildTree(){
        int leafCount = 0;
        for( int i = 0 ; i < this.size ; i++ )
            if( this.child[i] < 0 ){
                this.weight[leafCount] = ( this.weight[i] + 1 ) / 2;
                this.child[leafCount]  = this.child[i];
                leafCount++;
            }

        leafCount--;
        int position     = this.size - 1;
        int leafPosition = this.size - 2;
        while( 0 <= position ){
            while( leafPosition <= position ){
                this.leafs[~this.child[leafCount]] = position;
                this.weight[ position ]  = this.weight[ leafCount ];
                this.child[ position-- ] = this.child[ leafCount-- ];
            }

            int weight = this.weight[leafPosition]
                       + this.weight[leafPosition + 1];

            while( 0 <= leafCount && this.weight[leafCount] <= weight ){
                this.leafs[~this.child[leafCount]] = position;
                this.weight[ position ]  = this.weight[ leafCount ];
                this.child[ position-- ] = this.child[ leafCount-- ];
            }

            this.weight[position] = weight;
            this.child[position]  = leafPosition + 1;
            this.parent[leafPosition]
                = this.parent[leafPosition + 1]
                = position;

            position--;
            leafPosition -= 2;
        }
    }

    /**
     * �m�[�h�ԍ�i�̃m�[�h��
     * �m�[�h�ԍ�j�̃m�[�h����ꊷ���鏈�����s���B
     * 
     * @param i ���ꊷ���Ώۂ̃m�[�h
     * @param j ���ꊷ���Ώۂ̃m�[�h
     */
    private void swap( int i, int j ){
        if( this.child[i] < 0 ){
            this.leafs[ ~this.child[i] ] = j;
        }else{
            this.parent[ this.child[i] ]
                = this.parent[ this.child[i] - 1 ]
                = j;
        }

        if( this.child[j] < 0 ){
            this.leafs[ ~this.child[j] ] = i;
        }else{
            this.parent[ this.child[j] ]
                = this.parent[ this.child[j] - 1 ]
                = i;
        }

        int temp      = this.child[i];
        this.child[i] = this.child[j];
        this.child[j] = temp;

        temp           = this.weight[i];
        this.weight[i] = this.weight[j];
        this.weight[j] = temp;
    }

}
//end of DynamicHuffman.java
