//start of HashShort.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * HashShort.java
 * 
 * Copyright (C) 2002  Michel Ishizuka  All rights reserved.
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
 * �f�[�^�p�^���̐擪2�o�C�g����
 * 0 �` 4095 �̃n�b�V���l�𐶐�����n�b�V���֐��B
 * 
 * <pre>
 * -- revision history --
 * $Log: HashShort.java,v $
 * Revision 1.0  2002/08/05 00:00:00  dangan
 * add to version control
 * [change]
 *     HashMethod �̃C���^�t�F�C�X�ύX�ɂ��킹�ăC���e�t�F�C�X�ύX�B
 * [maintanance]
 *     �\�[�X����
 *     �^�u�p�~
 *     ���C�Z���X���̏C��
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.0 $
 */
public class HashShort implements HashMethod{


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  private byte[] TextBuffer
    //------------------------------------------------------------------
    /**
     * LZSS���k���{�����߂̃o�b�t�@�B
     * �O���͎����̈�A
     * �㔼�͈��k���{�����߂̃f�[�^�̓������o�b�t�@�B
     * HashMethod�̎������ł� Hash�l�̐����̂��߂̓ǂݍ��݂ɂ̂ݎg�p����B
     */
    private byte[] TextBuffer;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private HashShort()
    //  public HashShort( byte[] TextBuffer )
    //------------------------------------------------------------------
    /**
     * �f�t�H���g�R���X�g���N�^�B
     * �g�p�s��
     */
    private HashShort(){    }

    /**
     * �f�[�^�p�^���̐擪2�o�C�g���� 0x000 �` 0xFFF �܂ł̒l�𐶐�����
     * �n�b�V���֐����\�z����B
     * 
     * @param TextBuffer LZSS���k�p�̃o�b�t�@�B
     *                   Hash�l�����̂��ߓǂݍ��ݗp�Ɏg�p����B
     */
    public HashShort( byte[] TextBuffer ){
        this.TextBuffer = TextBuffer;
    }


    //------------------------------------------------------------------
    //  method of HashMethod
    //------------------------------------------------------------------
    //  public int hash( int position )
    //  public int hashRequires()
    //  public int tableSize()
    //------------------------------------------------------------------
    /**
     * �n�b�V���֐��B
     * �R���X�g���N�^�œn���ꂽ TextBuffer �� position �����
     * �f�[�^�p�^���� hash�l�𐶐�����B
     *
     * @param position �f�[�^�p�^���̊J�n�ʒu
     * 
     * @return �n�b�V���l
     */
    public int hash( int position ){
        return ( ( ( ( this.TextBuffer[ position + 1 ] & 0x0F ) << 8 )
                 | ( ( this.TextBuffer[ position + 1 ] & 0xFF ) >> 4 ) )
               ^ ( ( this.TextBuffer[ position  ] & 0xFF ) << 2 ) );
    }

    /**
     * �n�b�V���֐����n�b�V���l�𐶐����邽�߂Ɏg�p����o�C�g���𓾂�B<br>
     * ���̃n�b�V���֐��̓f�[�^�p�^���̐擪 2 �o�C�g�̃f�[�^����
     * �n�b�V���l�𐶐����邽�߁A���̃��\�b�h�͏�� 2 ��Ԃ��B
     * 
     * @return ��� 2
     */
    public int hashRequires(){
        return 2;
    }

    /**
     * �n�b�V���e�[�u���̃T�C�Y�𓾂�B<br>
     * ���̃n�b�V���֐��� 0x000 �` 0xFFF �܂ł̃n�b�V���l�𐶐����邽��
     * ���̃��\�b�h�͏�� 0x1000(4096) ��Ԃ��B
     * 
     * @return ��� 0x1000(4096) 
     */
    public int tableSize(){
        return 0x1000;
    }


}
//end of HashShort.java
