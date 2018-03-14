//start of PreLzssDecoder.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * PreLzssDecoder.java
 * 
 * Copyright (C) 2001  Michel Ishizuka  All rights reserved.
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

import java.io.EOFException;
import java.io.IOException;

/**
 * LZSS���k�R�[�h����������C���^�[�t�F�C�X�B
 * 
 * <pre>
 * -- revision history --
 * $Log: PreLzssDecoder.java,v $
 * Revision 1.0  2002/07/25 00:00:00  dangan
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
public interface PreLzssDecoder{


    //------------------------------------------------------------------
    //  original method ( on the model of java.io.InputStream )
    //------------------------------------------------------------------
    //  mark/reset
    //------------------------------------------------------------------
    //  public abstract void mark( int readLimit )
    //  public abstract void reset()
    //  public abstract boolean markSupported()
    //------------------------------------------------------------------
    /**
     * �ڑ����ꂽ���̓X�g���[���̌��݈ʒu�Ƀ}�[�N��ݒ肵�A
     * reset() ���\�b�h�Ń}�[�N�������_�̓ǂݍ��݈ʒu�ɖ߂��悤�ɂ���B<br>
     * InputStream �� mark() �ƈႢ�A readLimit �Őݒ肵��
     * ���E�o�C�g�����O�Ƀ}�[�N�ʒu�������ɂȂ��Ă����܂�Ȃ��B
     * �������AreadLimit �𖳎����Ė����� reset() �\�� 
     * InputStream �Ɛڑ����Ă���ꍇ�� readLimit �ɂǂ̂悤�Ȓl��ݒ肳��Ă�
     * reset() �ŕK���}�[�N�ʒu�ɕ����ł��Ȃ���΂Ȃ�Ȃ��B<br>
     * 
     * @param readLimit �}�[�N�ʒu�ɖ߂����E�̃o�C�g���B
     *                  ���̃o�C�g���𒴂��ăf�[�^��ǂݍ��񂾏ꍇ 
     *                  reset()�ł��Ȃ��Ȃ�\��������B<br>
     */
    public abstract void mark( int readLimit );

    /**
     * �ڑ����ꂽ���̓X�g���[���̓ǂݍ��݈ʒu���Ō��
     * mark() ���\�b�h���Ăяo���ꂽ�Ƃ��̈ʒu�ɐݒ肷��B<br>
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public abstract void reset() throws IOException;

    /**
     * �ڑ����ꂽ���̓X�g���[���� mark() �� reset() ��
     * �T�|�[�g���邩�𓾂�B<br>
     * 
     * @return �X�g���[���� mark() �� reset() ��
     *         �T�|�[�g����ꍇ�� true�B<br>
     *         �T�|�[�g���Ȃ��ꍇ�� false�B<br>
     */
    public abstract boolean markSupported();


    //------------------------------------------------------------------
    //  original method ( on the model of java.io.InputStream )
    //------------------------------------------------------------------
    //  other method
    //------------------------------------------------------------------
    //  public abstract int available()
    //  public abstract void close()
    //------------------------------------------------------------------
    /**
     * �ڑ����ꂽ���̓X�g���[������u���b�N���Ȃ���
     * �ǂݍ��ނ��Ƃ̂ł���Œ�o�C�g���𓾂�B<br>
     * ���̐��l�͊��S�ł��鎖��ۏႵ�Ȃ��Ă悢�B
     * ����͌̈ӂɍ쐬���ꂽ�f�[�^���ł̓u���b�N������
     * �ǂݍ��ގ��̏o����Œ�o�C�g���𓾂�ɂ�
     * ���ۂɓǂݍ���ł݂�ȊO�ɕ��@���Ȃ����߂ł���B
     * 
     * @return �u���b�N���Ȃ��œǂݏo����Œ�o�C�g���B<br>
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public abstract int available() throws IOException;

    /**
     * ���̓��̓X�g���[������A�g�p���Ă���
     * �S�Ẵ��\�[�X���J������B<br>
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public abstract void close() throws IOException;


    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  read
    //------------------------------------------------------------------
    //  public abstract int readCode()
    //  public abstract int readOffset()
    //------------------------------------------------------------------
    /**
     * 1byte �� LZSS�����k�̃f�[�^�������́A
     * LZSS �ň��k���ꂽ���k�R�[�h�̂�����v����ǂݍ��ށB<br>
     * �����k�f�[�^�� 0�`255�A
     * LZSS���k�R�[�h(��v��)�� 256�`511 �̒l�����Ȃ���΂Ȃ�Ȃ��B<br>
     * 
     * @return 1byte �� LZSS�����k�̃f�[�^�������́A
     *         LZSS �ň��k���ꂽ���k�R�[�h�̂�����v��
     * 
     * @exception IOException  ���o�̓G���[�����������ꍇ
     * @exception EOFException EndOfStream�ɒB�����ꍇ<br>
     */
    public abstract int readCode() throws IOException;

    /**
     * LZSS �ň��k���ꂽ���k�R�[�h�̂�����v�ʒu��ǂݍ��ށB<br>
     * 
     * @return LZSS �ň��k���ꂽ���k�R�[�h�̂�����v�ʒu
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public abstract int readOffset() throws IOException;


    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  get LZSS parameter
    //------------------------------------------------------------------
    //  public abstract int getDictionarySize()
    //  public abstract int getMaxMatch()
    //  public abstract int getThreshold()
    //------------------------------------------------------------------
    /**
     * ����PreLzssDecoder����������LZSS�����̃T�C�Y�𓾂�B
     * 
     * @return LZSS�����̃T�C�Y
     */
    public abstract int getDictionarySize();

    /**
     * ����PreLzssDecoder����������Œ���v���𓾂�B
     * 
     * @return �Œ���v��
     */
    public abstract int getMaxMatch();

    /**
     * ����PreLzssDecoder���������鈳�k�A�񈳏k��臒l�𓾂�B
     * 
     * @return LZSS��臒l
     */
    public abstract int getThreshold();

}
//end of PreLzssDecoder.java

