//start of StaticHuffman.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * StaticHuffman.java
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
 * �ÓI�n�t�}���p���[�e�B���e�B�֐��Q��ێ�����B<br>
 * �n�t�}�������͍ő�16�r�b�g�ɐ��������B<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: StaticHuffman.java,v $
 * Revision 1.0  2002/07/24 00:00:00  dangan
 * add to version control
 * [maintenance]
 *     �\�[�X����
 *     �^�u�p�~
 *     ���C�Z���X���̏C��
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.0 $
 */
public class StaticHuffman{


    //------------------------------------------------------------------
    //  class field
    //------------------------------------------------------------------
    //  public static final int LimitLen
    //------------------------------------------------------------------
    /**
     * LHA��DOS��16bit���[�h���g�p���č��ꂽ���Ƃɂ��
     * �n�t�}���������̐����B
     */
    public static final int LimitLen = 16;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private StaticHuffman()
    //------------------------------------------------------------------
    /**
     * �f�t�H���g�R���X�g���N�^�B
     * �g�p�s�B
     */
    private StaticHuffman(){  }


    //------------------------------------------------------------------
    //  shared method
    //------------------------------------------------------------------
    //  convert
    //------------------------------------------------------------------
    //  public static int[] FreqListToLenList( int[] FreqList )
    //  public static int[] FreqListToLenListOriginal( int[] FreqList )
    //  public static int[] LenListToCodeList( int[] LenList )
    //------------------------------------------------------------------
    /**
     * �p�x�\���� �n�t�}�������̃r�b�g���̕\���쐬����B
     * 
     * @param FreqList �p�x�\
     * 
     * @return �n�t�}�������̃r�b�g���̕\
     */
    public static int[] FreqListToLenList( int[] FreqList ){
        /**
         * �n�t�}���؂��\������z��Q
         * �n�t�}���؂� 0�`FreqList.length �܂ł͑S�Ă��t�ł���A
         * ���̃m�[�h�ԍ��͕����ł���B�؂��������񊮐��������
         * TreeCount-1�����[�g�m�[�h�ƂȂ�B
         * NodeWeight:: ���̃m�[�h�̏d�݂�����
         * SmallNode::  �����Ȏq�m�[�h�̃m�[�h�ԍ�������
         * LargeNode::  �傫�Ȏq�m�[�h�̃m�[�h�ԍ�������
         * TreeCount::  �L���ȃm�[�h�̌�������
         */
        int[] NodeWeight = new int[ FreqList.length * 2 - 1 ];
        int[] SmallNode  = new int[ FreqList.length * 2 - 1 ];
        int[] LargeNode  = new int[ FreqList.length * 2 - 1 ];
        int TreeCount    = FreqList.length;

        /**
         * �n�t�}���؂̗t�̃m�[�h�ԍ��������ȏ��Ɋi�[�������X�g�B
         * Leafs::     ���X�g�{��
         * LeafCount:: �t�̌�
         */
        int[] Leafs     = new int[ FreqList.length ];
        int LeafCount   = 0;

        /**
         * �n�t�}���؂̗t�łȂ��m�[�h�̃m�[�h�ԍ���
         * �����ȏ��Ɋi�[�������X�g���i�[����B
         * Nodes::     ���X�g�{��
         * NodeCount:: �t�łȂ��m�[�h�̌�
         */
        int[] Nodes     = new int[ FreqList.length - 1 ];
        int NodeCount   = 0;

        //�؂ɗt���Z�b�g���A
        //Leafs�ɕp�x1�ȏ�̗t�̂݃Z�b�g����B
        for( int i = 0 ; i < FreqList.length ; i++ ){
            NodeWeight[i] = FreqList[i];

            if( 0 < FreqList[i] )
                Leafs[ LeafCount++ ] = i;
        }

        if( 2 <= LeafCount ){
            //=================================
            //�n�t�}���؂��쐬����
            //=================================

            //�n�t�}���؂̗t�ƂȂ�ׂ��v�f�𐮗񂳂���B
            StaticHuffman.MergeSort( Leafs, 0, LeafCount - 1, 
                                 FreqList, new int[ ( LeafCount / 2 ) + 1 ] );

            //�t���A�m�[�h�̍ŏ��̂���2��V�����m�[�h��
            //���т��鎖���J��Ԃ��A���[�g�m�[�h�܂ō쐬����B
            //���̏����ɂ���ăn�t�}���؂���������B
            int LeafIndex = 0;
            int NodeIndex = 0;
            do{
                int small;
                if( NodeCount <= NodeIndex )
                    small = Leafs[ LeafIndex++ ];
                else if( LeafCount <= LeafIndex )
                    small = Nodes[ NodeIndex++ ];
                else if( NodeWeight[Leafs[LeafIndex]] <= NodeWeight[Nodes[NodeIndex]] )
                    small = Leafs[ LeafIndex++ ];
                else 
                    small = Nodes[ NodeIndex++ ];

                int large;
                if( NodeCount <= NodeIndex )
                    large = Leafs[ LeafIndex++ ];
                else if( LeafCount <= LeafIndex )
                    large = Nodes[ NodeIndex++ ];
                else if( NodeWeight[Leafs[LeafIndex]] <= NodeWeight[Nodes[NodeIndex]] )
                    large = Leafs[ LeafIndex++ ];
                else 
                    large = Nodes[ NodeIndex++ ];

                int newNode = TreeCount++;
                NodeWeight[newNode] = NodeWeight[small] + NodeWeight[large];
                SmallNode[newNode]  = small;
                LargeNode[newNode]  = large;
                Nodes[NodeCount++]  = newNode;
            }while( NodeIndex + LeafIndex < NodeCount + LeafCount - 1 );

            //============================================
            //�n�t�}���؂���n�t�}���������̕\���쐬����B
            //============================================
            //�n�t�}���؂���n�t�}���������̕p�x�\���쐬����B
            int[] LenFreq = StaticHuffman.HuffmanTreeToLenFreq( SmallNode, 
                                               LargeNode, TreeCount - 1 );

            //�n�t�}���������̕p�x�����畄�����̕\���쐬����B
            int[] LenList = new int[ FreqList.length ];
            LeafIndex = 0;
            for( int len = StaticHuffman.LimitLen ; 0 < len ; len-- )
                while( 0 < LenFreq[len]-- )
                    LenList[Leafs[LeafIndex++]] = len;

            return LenList;
        }else{
            return new int[ FreqList.length ];
        }
    }

    /**
     * �p�x�\���� �n�t�}�������̃r�b�g���̕\���쐬����B
     * �I���W�i����LHA�Ɠ����R�[�h���o�͂���B
     * 
     * @param FreqList �p�x�\
     * 
     * @return �n�t�}�������̃r�b�g���̕\
     */
    public static int[] FreqListToLenListOriginal( int[] FreqList ){
        /**
         * �n�t�}���؂��\������z��Q
         * �n�t�}���؂� 0�`FreqList.length �܂ł͑S�Ă��t�ł���A
         * ���̃m�[�h�ԍ��͕����ł���B�؂��������񊮐��������
         * TreeCount-1�����[�g�m�[�h�ƂȂ�B
         * NodeWeight:: ���̃m�[�h�̏d�݂�����
         * SmallNode::  �����Ȏq�m�[�h�̃m�[�h�ԍ�������
         * LargeNode::  �傫�Ȏq�m�[�h�̃m�[�h�ԍ�������
         * TreeCount::  �L���ȃm�[�h�̌�������
         */
        int[] NodeWeight = new int[ FreqList.length * 2 - 1 ];
        int[] SmallNode  = new int[ FreqList.length * 2 - 1 ];
        int[] LargeNode  = new int[ FreqList.length * 2 - 1 ];
        int TreeCount    = FreqList.length;

        /**
         * �n�t�}���؂̗t�̃m�[�h�ԍ��������ȏ��Ɋi�[�������X�g�B
         * Leafs::     ���X�g�{��
         * LeafCount:: �t�̌�
         */
        int[] Leafs     = new int[ FreqList.length ];
        int LeafCount   = 0;

        /**
         * �n�t�}���؂̑S�Ẵm�[�h�̃m�[�h�ԍ���
         * �����ȏ��Ɋi�[�������X�g���i�[����B
         * �q�[�v�\�[�g���g�p���邽�߁AHeap[0]�͎g�p���Ȃ�
         * Heap::     ���X�g�{��
         * HeapLast:: Heap�̍Ō�̗v�f
         */
        int[] Heap     = new int[ FreqList.length * 2 ];
        int HeapLast   = 0;

        //�؂ɗt���Z�b�g���A
        //Heap�ɕp�x1�ȏ�̗t�̂݃Z�b�g����B
        for( int i = 0 ; i < FreqList.length ; i++ ){
            NodeWeight[i] = FreqList[i];

            if( 0 < FreqList[i] )
                Heap[ ++HeapLast ] = i;
        }

        if( 2 <= HeapLast ){
            //=================================
            //�n�t�}���؂��쐬����
            //=================================

            //�n�t�}���؂̗t�ƂȂ�ׂ��v�f�𐮗񂳂���B
            for( int i = HeapLast / 2 ; 1 <= i ; i-- )
                StaticHuffman.DownHeap( Heap, HeapLast, NodeWeight, i );

            //�t���A�m�[�h�̍ŏ��̂���2��V�����m�[�h��
            //���т��鎖���J��Ԃ��A���[�g�m�[�h�܂ō쐬����B
            //���̏����ɂ���ăn�t�}���؂���������B
            do{
                int small = Heap[1];
                if( small < FreqList.length ) Leafs[LeafCount++] = small;

                Heap[1] = Heap[HeapLast--];
                StaticHuffman.DownHeap( Heap, HeapLast, NodeWeight, 1 );
                int large = Heap[1];
                if( large < FreqList.length ) Leafs[LeafCount++] = large;

                int newNode = TreeCount++;
                NodeWeight[newNode] = NodeWeight[small] + NodeWeight[large];
                SmallNode[newNode]  = small;
                LargeNode[newNode]  = large;

                Heap[1]             = newNode;
                StaticHuffman.DownHeap( Heap, HeapLast, NodeWeight, 1 );
            }while( 1 < HeapLast );

            //============================================
            //�n�t�}���؂���n�t�}���������̕\���쐬����B
            //============================================

            //�n�t�}���؂���n�t�}���������̕p�x�\���쐬����B
            int[] LenFreq = StaticHuffman.HuffmanTreeToLenFreq( SmallNode, 
                                               LargeNode, TreeCount - 1 );
            //�n�t�}���������̕p�x�����畄�����̕\���쐬����B
            int[] LenList = new int[ FreqList.length ];
            int LeafIndex = 0;
            for( int len = StaticHuffman.LimitLen ; 0 < len ; len-- )
                while( 0 < LenFreq[len]-- )
                    LenList[Leafs[LeafIndex++]] = len;

            return LenList;
        }else{
            return new int[ FreqList.length ];
        }
    }

    /**
     * �n�t�}���������̃��X�g���� �n�t�}�������\���쐬����B
     * 
     * @param LenList �n�t�}���������̃��X�g
     * 
     * @return �n�t�}�������\
     *
     * @exception BadHuffmanTableException
     *                LenList���s���Ȃ��߁A
     *                �n�t�}�������\�������o���Ȃ��ꍇ
     */
    public static int[] LenListToCodeList( int[] LenList )
                                        throws BadHuffmanTableException {
        //�n�t�}���������̕p�x�\
        int[] LenFreq   = new int[ StaticHuffman.LimitLen + 1 ];
        //�n�t�}���������ɑΉ���������
        int[] CodeStart = new int[ StaticHuffman.LimitLen + 2 ];

        //�n�t�}���������̕p�x�\�쐬
        for( int i = 0 ; i < LenList.length ; i++ )
            LenFreq[LenList[i]]++;

        if( LenFreq[0] < LenList.length ){

            //CodeStart[1] = 0; //Java�ł͕K�v�����̂ŃR�����g�A�E�g���Ă���B
            for( int i = 1 ; i <= StaticHuffman.LimitLen ; i++ )
                CodeStart[i + 1] = CodeStart[i] + LenFreq[i] << 1;

            if( CodeStart[ StaticHuffman.LimitLen + 1 ] != 0x20000 )
                throw new BadHuffmanTableException();

            int[] CodeList = new int[ LenList.length ];
            for( int i = 0 ; i < CodeList.length ; i++ )
                if( 0 < LenList[i] )
                    CodeList[i] = CodeStart[ LenList[i] ]++;

            return CodeList;
        }else{
            return new int[ LenList.length ];
        }
    }


    //------------------------------------------------------------------
    //  shared method
    //------------------------------------------------------------------
    //  utility for decoder
    //------------------------------------------------------------------
    //  public static short[] createTable( int[] LenList )
    //  public static short[][] createTableAndTree( int[] LenList, int TableBits )
    //------------------------------------------------------------------
    /**
     * LenList ����A�n�t�}�������p�̃e�[�u���𐶐�����B<br>
     * 
     * @param LenList �n�t�}���������̕\
     * 
     * @return �n�t�}�������p�e�[�u���B
     * 
     * @exception BadHuffmanTableException
     *                  LenList���s���Ȃ��߁A
     *                  �n�t�}�������\�������o���Ȃ��ꍇ
     */
    public static short[] createTable( int[] LenList ) 
                                            throws BadHuffmanTableException {
        int[] CodeList = StaticHuffman.LenListToCodeList( LenList );            //throws BadHuffmanTableException
        int TableBits  = 0;
        int LastCode   = 0;

        for( int i = 0 ; i < LenList.length ; i++ ){
            if( TableBits <= LenList[i] ){
                TableBits = LenList[i];
                LastCode  = i;
            }
        }

        short[] Table = new short[ 1 << TableBits ];
        for( int i = 0 ; i < LenList.length ; i++ ){
            if( 0 < LenList[i] ){
                int start = CodeList[i] << ( TableBits - LenList[i] );
                int end   = ( i != LastCode 
                              ? start + ( 1 << ( TableBits - LenList[i] ) )
                              : Table.length );

                for( int j = start ; j < end ; j++ )
                    Table[j] = (short)i;
            }
        }
        return Table;
    }


    /**
     * LenList ����A�n�t�}�������p�̃e�[�u���Ɩ؂𐶐�����B
     * �e�[�u���� TableBits �̑傫���������A����ȏ�̕����͖؂Ɋi�[�����B<br>
     * �߂�l�� new short[][]{ Table, Tree[0], Tree[1] } �ƂȂ�B<br>
     * �e�[�u�������������ʂ������͖؂𑖍������ہA���̒l�𓾂��ꍇ�A
     * ����͕��������ꂽ�R�[�h��S�r�b�g���]�������̂ł���B
     * ���̒l�ł���΂���� �؂𑖍����邽�߂� index �ł���A
     * Tree[bit][index] �̂悤�Ɏg�p����B 
     * 
     * @param LenList   �n�t�}���������̕\
     * @param TableBits �n�t�}�������p�e�[�u���̑傫���B
     * 
     * @return �n�t�}�������p�e�[�u���Ɩ؁B
     * 
     * @exception BadHuffmanTableException
     *                  LenList���s���Ȃ��߁A
     *                  �n�t�}�������\�������o���Ȃ��ꍇ
     */
    public static short[][] createTableAndTree( int[] LenList, int TableBits ) 
                                               throws BadHuffmanTableException {

        //------------------------------------------------------------------
        //�n�t�}�����������X�g���� �n�t�}�������̃��X�g�𓾂�B
        int[] CodeList = StaticHuffman.LenListToCodeList( LenList );            //throws BadHuffmanTableException

        //------------------------------------------------------------------
        //�n�t�}���������̃��X�g�𑖍����A
        //LastCode �𓾂�B
        //�܂� �؂��\������̂ɕK�v�Ȕz��T�C�Y�𓾂邽�߂̏������s���B
        short[] Table  = new short[ 1 << TableBits ];
        int LastCode   = 0;
        for( int i = 0 ; i < LenList.length ; i++ ){
            if( LenList[LastCode] <= LenList[i] ) LastCode = i;

            if( TableBits < LenList[i] ){
                Table[ CodeList[i] >> ( LenList[i] - TableBits ) ]++;
            }
        }

        //------------------------------------------------------------------
        //�؂��\������̂ɕK�v�Ȕz��T�C�Y�𓾁A�e�[�u��������������B
        final short INIT = -1;
        int count = 0;
        for( int i = 0 ; i < Table.length ; i++ ){
            if( 0 < Table[i] ) count += Table[i] - 1;
            Table[i] = INIT;
        }
        short[] Small = new short[ count ];
        short[] Large = new short[ count ];


        //------------------------------------------------------------------
        //�e�[�u���Ɩ؂��\������B
        int avail = 0;
        for( int i = 0 ; i < LenList.length ; i++ ){
            if( 0 < LenList[i] ){
                int TreeBits  = LenList[i] - TableBits;
                if( TreeBits <= 0 ){
                    int start = CodeList[i] << ( TableBits - LenList[i] );
                    int end   = ( i != LastCode 
                                  ? start + ( 1 << ( TableBits - LenList[i] ) )
                                  : Table.length );
                    for( int j = start ; j < end ; j++ ){
                        Table[ j ] = (short)~i;
                    }
                }else{
                    int TableCode = CodeList[i] >> TreeBits;
                    int node;
                    if( Table[ TableCode ] == INIT ){
                        node = Table[ TableCode ] = (short)(avail++);
                    }else{
                        node = Table[ TableCode ];
                    }
                    for( int j = TableBits + 1 ; j < LenList[i] ; j++ ){
                        if( 0 == ( CodeList[i] & ( 1 << ( LenList[i] - j ) ) ) ){
                            if( Small[node] == 0 ) node = Small[node] = (short)(avail++);
                            else                   node = Small[node];
                        }else{
                            if( Large[node] == 0 ) node = Large[node] = (short)(avail++);
                            else                   node = Large[node];
                        }
                    }
                    if( 0 == ( CodeList[i] & 0x01 ) ) Small[node] = (short)~i;
                    else                              Large[node] = (short)~i;
                }
            }
        }
        return new short[][]{ Table, Small, Large };
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  stuff of converter
    //------------------------------------------------------------------
    //  private static void MergeSort( int[] array, int first, int last,
    //                                 int[] weight, int[] work )
    //  private static int[] HuffmanTreeToLenFreq( int[] SmallNode, 
    //                                     int[] LargeNode, int root )
    //  private static void internalCountLenFreq( int[] SmallNode,
    //               int[] LargeNode, int node, int len, int[] LenFreq )
    //------------------------------------------------------------------
    /**
     * �}�[�W�\�[�g�A�ċA�֐�<br>
     * array�� weight�̓Y���Aarray��first����last�̋�ԓ���
     * weight�����������ɕ��Ԃ悤�Ƀ\�[�g����B
     * work�͂��̂��߂̍�Ɨ̈�B
     * 
     * @param array  �\�[�g�Ώۂ̔z��
     * @param first  �\�[�g��Ԃ̍ŏ�
     * @param last   �\�[�g��Ԃ̍Ō�
     * @param weight �\�[�g�̍ۂɎQ�Ƃ����d�݂̃��X�g
     * @param work   �}�[�W�\�[�g�p��Ɨ̈�
     */
    private static void MergeSort( int[] array, 
                                   int   first, 
                                   int   last, 
                                   int[] weight, 
                                   int[] work ){
        if( first < last ){
            int middle = ( first + last ) / 2 + ( first + last ) % 2;
            //�O�����\�[�g
            StaticHuffman.MergeSort( array, first, middle - 1, weight, work );
            //�㔼���\�[�g
            StaticHuffman.MergeSort( array, middle,      last, weight, work );

            //�O���� work��
            System.arraycopy( array, first, work, 0, middle - first );

            //�\�[�g���ꂽ�O���� �\�[�g���ꂽ�㔼��
            //���񂵂}�[�W����B
            int srcIndex  = middle;
            int workIndex = 0;
            int dstIndex  = first;
            while( srcIndex <= last && workIndex < middle - first )
                array[ dstIndex++ ] = 
                  ( weight[work[workIndex]] < weight[array[srcIndex]] 
                      ? work[ workIndex++ ] : array[ srcIndex++ ] );

            //work�Ɏc�����v�f�� array�ɖ߂�
            if( workIndex < middle - first )
                System.arraycopy( work, workIndex, array, dstIndex,
                                  middle - first - workIndex );
        }
    }

    /**
     * heap��weight�̓Y����
     * num*2, num*2+1�̒n�_�Ńq�[�v���o���Ă��邱�Ƃ�
     * �O��Ƃ��� heap �� num�𒸓_�Ƃ���q�[�v�����B<br>
     * �q�[�v�\�[�g�̈ꕔ���B
     * 
     * @param heap   �q�[�v�𐶐�����z��
     * @param size   �q�[�v�̃T�C�Y
     * @param weight ����̊�ƂȂ�d�݂̃��X�g
     * @param num    ����쐬����q�[�v�̒��_
     */
    private static void DownHeap( int[] heap, int size, int[] weight, int num ){

        int top = heap[num];
        int i;
        while( ( i = 2 * num ) <= size ){
            if( i < size && weight[heap[i]] > weight[heap[i + 1]] ) i++;
            if( weight[top] <= weight[heap[i]] ) break;

            heap[num] = heap[i];
            num = i;
        }
        heap[num] = top;
    }

    /**
     * �n�t�}���؂��� �n�t�}���������̕p�x�\���쐬����B<br>
     * �n�t�}���؂�H���� �n�t�}���������̕p�x�\���쐬����B
     * �܂��A�������� 16�r�b�g�ɐ������邽�߂̏����������ōs���B
     * 
     * @param SmallNode �������q�m�[�h�̃m�[�h�ԍ��̕\
     * @param LargeNode �傫���q�m�[�h�̃m�[�h�ԍ��̕\
     * @param root      �n�t�}���؂̃��[�g�m�[�h
     * 
     * @return �n�t�}�����������ő�16�r�b�g�ɐ�������
     *         �n�t�}���������\
     */
    private static int[] HuffmanTreeToLenFreq( int[] SmallNode, 
                                               int[] LargeNode,
                                               int   root ){
        int[] LenFreq = new int[ StaticHuffman.LimitLen + 1 ];

        //�n�t�}���؂���p�x�\�쐬
        StaticHuffman.internalHuffmanTreeToLenFreq( SmallNode, LargeNode, 
                                                   root, 0, LenFreq );

//      System.out.println( "���B::StaticHuffman.HuffmanTreeToLenFreq--�n�t�}���؂���n�t�}���������̃��X�g�擾--" );

        //�ő�16�r�b�g�̐����ɂ��A�C�����󂯂Ă���ꍇ��
        //�������̕\����A��ʂ̃m�[�h�����ʂւƈ������肨�낷
        //���Ƃɂ���ĕ������̕\���C������B
        int weight = 0;
        for( int i = StaticHuffman.LimitLen ; 0 < i ; i-- )
            weight += LenFreq[i] << ( StaticHuffman.LimitLen - i );

//      System.out.println( "weight::" + weight );

        while( ( 1 << StaticHuffman.LimitLen ) < weight ){
            LenFreq[ StaticHuffman.LimitLen ]--;
            for( int i = StaticHuffman.LimitLen - 1 ; 0 < i ; i-- )
                if( 0 < LenFreq[i] ){
                    LenFreq[i]--;
                    LenFreq[i + 1] += 2;
                    break;
                }

            weight--;
        }

        return LenFreq;
    }

    /**
     * �n�t�}���ؒT�����\�b�h�A�ċA�֐��B<br>
     * �n�t�}���؂�T�����Ă����Anode���t�ł����
     * �n���ꂽ�������̕p�x�\���X�V���A
     * �m�[�h�ł���΁A���������Ƒ傫�����̗�����
     * �q�m�[�h���ċA�I�ɒT������B<br>
     * 
     * @param SmallNode �������q�m�[�h�̃m�[�h�ԍ��̕\
     * @param LargeNode �傫���q�m�[�h�̃m�[�h�ԍ��̕\
     * @param node      ��������m�[�h�ԍ�
     * @param len       �n�t�}���؂�root����̒���
     * @param LenFreq   �������̕p�x�\
     */
    private static void internalHuffmanTreeToLenFreq( int[] SmallNode,
                                                      int[] LargeNode,
                                                      int   node,
                                                      int   len,
                                                      int[] LenFreq ){
        if( node < ( SmallNode.length + 1 ) / 2 ){
            //node���t�Ȃ�p�x�\�X�V
            LenFreq[ ( len < StaticHuffman.LimitLen
                     ? len : StaticHuffman.LimitLen ) ]++;
        }else{
            //node���m�[�h�Ȃ痼���̃m�[�h���ċA�I�ɒT��
            StaticHuffman.internalHuffmanTreeToLenFreq( SmallNode, LargeNode, 
                                               SmallNode[node], len + 1, LenFreq );
            StaticHuffman.internalHuffmanTreeToLenFreq( SmallNode, LargeNode, 
                                               LargeNode[node], len + 1, LenFreq );
        }
    }

}
//end of StaticHuffman.java
