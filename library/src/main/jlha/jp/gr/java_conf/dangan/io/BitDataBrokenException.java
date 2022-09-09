//start of BitDataBrokenException.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * BitDataBrokenException.java
 * <p>
 * Copyright (C) 2001-2002  Michel Ishizuka  All rights reserved.
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

package jp.gr.java_conf.dangan.io;

//import classes and interfaces

//import exceptions

import java.io.IOException;


/**
 * EndOfStream �ɒB���Ă��܂������ߗv�����ꂽ�r�b�g����
 * �f�[�^�𓾂��Ȃ������ꍇ�ɓ��������O�B<br>
 * BitInputStream �p�ł��邽�߁A
 * �ێ����Ă����� �f�[�^�� 32�r�b�g�܂łƂȂ��Ă���_��
 * ���ӂ��邱�ƁB<br>
 * NotEnoughBitsException �ƈႢ�A������̗�O�𓊂���
 * �ꍇ�ɂ� ���ۂɓǂݍ��ݓ�����s���Ă��܂��Ă��邽��
 * �ǂݍ��݈ʒu�͗�O�𓊂���O�̎��_����ω����Ă��܂�
 * �Ă���_�ɒ��ӂ��邱�ƁB<br>
 *
 * <pre>
 * -- revision history --
 * $Log: BitDataBrokenException.java,v $
 * Revision 1.1  2002/12/07 00:00:00  dangan
 * [maintenance]
 *     �\�[�X����
 *
 * Revision 1.0  2002/07/24 00:00:00  dangan
 * add to version control
 * [maintenance]
 *     �^�u�p�~
 *     ���C�Z���X���̏C��
 *
 * </pre>
 *
 * @author $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class BitDataBrokenException extends IOException {


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  private Throwable cause
    //  private int bitData
    //  private int bitCount
    //------------------------------------------------------------------
    /**
     * �r�b�g�f�[�^���r���܂ł���
     * �擾�ł��Ȃ������ƂȂ�����O
     */
    private Throwable cause;

    /**
     * �r���܂ł̃r�b�g�f�[�^
     */
    private int bitData;

    /**
     * bitData �̗L���r�b�g��
     */
    private int bitCount;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private BitDataBrokenException()
    //  public BitDataBrokenException( Throwable cause,
    //                                 int bitData, int bitCount )
    //------------------------------------------------------------------

    /**
     * �f�t�H���g�R���X�g���N�^�B
     * �g�p�s��
     */
    private BitDataBrokenException() {
    }

    /**
     * �V���� BitDataBrokenException ���\�z����B<br>
     *
     * @param cause    �r�b�g�f�[�^���r���܂ł����擾�ł��Ȃ�
     *                 �����ƂȂ�����O
     * @param bitData  �v�����ꂽ�r�b�g���ɖ����Ȃ��r�b�g�f�[�^
     * @param bitCount bitData �̃r�b�g��
     *
     */
    public BitDataBrokenException(Throwable cause,
                                  int bitData,
                                  int bitCount) {
        this.cause = cause;
        this.bitData = bitData;
        this.bitCount = bitCount;
    }


    //------------------------------------------------------------------
    //  access method
    //------------------------------------------------------------------
    //  public Throwable getCause()
    //  public int getBitData()
    //  public int getBitCount()
    //------------------------------------------------------------------

    /**
     * �r�b�g�f�[�^���r���܂ł���
     * �擾�ł��Ȃ������ƂȂ�����O�𓾂�B<br>
     *
     * @return �����ƂȂ�����O
     */
    public Throwable getCause() {
        return this.cause;
    }

    /**
     * �v�����ꂽ�r�b�g���ɖ����Ȃ�
     * "��ꂽ" �r�b�g�f�[�^�𓾂�B<br>
     *
     * @return �r�b�g�f�[�^
     */
    public int getBitData() {
        return this.bitData;
    }

    /**
     * getBitData() �œ�����
     * �r�b�g�f�[�^�̗L���r�b�g���𓾂�B
     *
     * @return �r�b�g�f�[�^�̗L���r�b�g��
     */
    public int getBitCount() {
        return this.bitCount;
    }

}
// end of BitDataBrokenException.java
