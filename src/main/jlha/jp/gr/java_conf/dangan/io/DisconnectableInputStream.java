//start of DisconnectableInputStream.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * DisconnectableInputStream.java
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

package jp.gr.java_conf.dangan.io;

//import classes and interfaces

import java.io.IOException;
import java.io.InputStream;

//import exceptions

/**
 * �f�[�^������������̓X�g���[���� �f�[�^����������
 * ���̓X�g���[���Ƃ̐ڑ����������邽�߂̃��[�e�B���e�B�N���X�B<br>
 * java.io.BufferedInputStream ���̃o�b�t�@�����O����X�g���[��
 * �Ƃ̐ڑ�����������ꍇ��
 * LimitedInputStream �����g�p����
 * �ڑ������ʒu���߂����o�b�t�@�����O��}�~����K�v������B<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: DisconnectableInputStream.java,v $
 * Revision 1.0  2002/07/24 00:00:00  dangan
 * add to version control
 * [maintenance]
 *     �^�u�p�~
 *     ���C�Z���X���̏C��
 *     �\�[�X����
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.0 $
 */
public class DisconnectableInputStream extends InputStream
                                       implements Disconnectable {

    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  source
    //------------------------------------------------------------------
    //  private InputStream in
    //------------------------------------------------------------------
    /**
     * �ڑ����ꂽ���̓X�g���[��
     */
    private InputStream in;

    //------------------------------------------------------------------
    //  constructer
    //------------------------------------------------------------------
    //  private DisconnectableInputStream()
    //  public DisconnectableInputStream( InputStream in )
    //------------------------------------------------------------------
    /**
     * �f�t�H���g�R���X�g���N�^�B
     * �g�p�s�B
     */
    private DisconnectableInputStream(){    }

    /**
     * in �Ƃ̐ڑ��������\�ȓ��̓X�g���[�����\�z����B
     * 
     * @param in ���̓X�g���[��
     */
    public DisconnectableInputStream( InputStream in ){
        if( in != null ){
            this.in = in;
        }else{
            throw new NullPointerException( "in" );
        }
    }


    //------------------------------------------------------------------
    //  java.io.InputStream methods
    //------------------------------------------------------------------
    //  read method
    //------------------------------------------------------------------
    //  public int read()
    //  public int read( byte[] buffer )
    //  public int read( byte[] buffer, int index, int length )
    //  public long skip( long length )
    //------------------------------------------------------------------
    /**
     * �ڑ����ꂽ���̓X�g���[������ ����1�o�C�g�̃f�[�^�𓾂�B<br>
     * 
     * @return �ǂݍ��܂ꂽ1�o�C�g�̃f�[�^�B<br>
     *         EndOfStream�ɒB�����ꍇ�� -1 ��Ԃ��B<br>
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public int read() throws IOException {
        return this.in.read();                                                  //throws IOException
    }

    /**
     * �ڑ����ꂽ���̓X�g���[������ �o�C�g�z�� buffer ��
     * �������悤�Ƀf�[�^��ǂݍ��ށB<br>
     * �f�[�^�͕K������ buffer �𖞂����Ƃ͌���Ȃ����Ƃɒ��ӁB<br>
     * 
     * @param buffer �ǂݍ��܂ꂽ�f�[�^���i�[���邽�߂̃o�C�g�z��
     * 
     * @return buffer �ɓǂݍ��񂾃f�[�^�ʂ��o�C�g���ŕԂ��B<br>
     *         ���� EndOfStream �ɒB���Ă����ꍇ�� -1 ��Ԃ��B<br>
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public int read( byte[] buffer ) throws IOException {
        return this.in.read( buffer, 0, buffer.length );                        //throws IOException
    }

    /**
     * �ڑ����ꂽ���̓X�g���[������ �o�C�g�z�� buffer ��
     * index �Ŏw�肳�ꂽ�ʒu���� length �o�C�g�̃f�[�^��
     * �ǂݍ��ށB<br>
     * �f�[�^�͕K������ length �o�C�g�ǂݍ��܂��Ƃ͌���
     * �Ȃ����Ƃɒ��ӁB<br>
     * 
     * @param buffer �ǂݍ��܂ꂽ�f�[�^���i�[���邽�߂̃o�C�g�z��
     * @param index  buffer���̃f�[�^�ǂݍ��݊J�n�ʒu
     * @param length buffer�ɓǂݍ��ރf�[�^��
     * 
     * @return buffer �ɓǂݍ��񂾃f�[�^�ʂ��o�C�g���ŕԂ��B<br>
     *         ���� EndOfStream �ɒB���Ă����ꍇ�� -1 ��Ԃ��B<br>
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public int read( byte[] buffer, int index, int length ) throws IOException {
        if( 0 < length ){
            return this.in.read( buffer, index, length );                       //throws IOException
        }else{
            return 0;
        }
    }

    /**
     * �ڑ����ꂽ���̓X�g���[���̃f�[�^�� length �o�C�g
     * �ǂݔ�΂��B<br>
     * 
     * @param length �ǂݔ�΂��o�C�g���B<br>
     * 
     * @return ���ۂɓǂݔ�΂��ꂽ�o�C�g���B<br>
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public long skip( long length ) throws IOException {
        if( 0 < length ){
            return this.in.skip( length );                                      //throws IOException
        }else{
            return 0;
        }
    }


    //------------------------------------------------------------------
    //  method of java.io.InputStream
    //------------------------------------------------------------------
    //  mark/reset
    //------------------------------------------------------------------
    //  public void mark( int readLimit )
    //  public void reset()
    //  public boolean markSupprted()
    //------------------------------------------------------------------
    /**
     * �ڑ����ꂽ���̓X�g���[���̌��݈ʒu�Ƀ}�[�N��ݒ肵�A
     * reset() ���\�b�h�Ń}�[�N�������_�� �ǂݍ��݈ʒu��
     * �߂��悤�ɂ���B<br>
     * 
     * @param readLimit �}�[�N�ʒu�ɖ߂����E�̃o�C�g���B
     *                  ���̃o�C�g���𒴂��ăf�[�^��ǂ�
     *                  ���񂾏ꍇ reset()�ł��Ȃ��Ȃ��
     *                  �\��������B<br>
     */
    public void mark( int readLimit ){
        this.in.mark( readLimit );
    }

    /**
     * �ڑ����ꂽ���̓X�g���[���̓ǂݍ��݈ʒu���Ō��
     * mark() ���\�b�h���Ăяo���ꂽ�Ƃ��̈ʒu�ɐݒ肷��B<br>
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void reset() throws IOException {
        this.in.reset();                                                        //throws IOException
    }

    /**
     * �ڑ����ꂽ���̓X�g���[���� mark() �� reset() ��
     * �T�|�[�g���邩�𓾂�B<br>
     * 
     * @return �X�g���[���� mark() �� reset() ��
     *         �T�|�[�g����ꍇ�� true�B<br>
     *         �T�|�[�g���Ȃ��ꍇ�� false�B<br>
     */
    public boolean  markSupprted(){
        return this.in.markSupported();
    }


    //------------------------------------------------------------------
    //  method of java.io.InputStream
    //------------------------------------------------------------------
    //  other
    //------------------------------------------------------------------
    //  public int available()
    //  public void close()
    //------------------------------------------------------------------
    /**
     * �ڑ����ꂽ���̓X�g���[������u���b�N���Ȃ���
     * �ǂݍ��ނ��Ƃ̂ł���o�C�g���𓾂�B<br>
     * 
     * @return �u���b�N���Ȃ��œǂݏo����o�C�g���B<br>
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public int available() throws IOException {
        return this.in.available();                                             //throws IOException
    }

    /**
     * �ڑ����ꂽ���̓X�g���[���Ƃ̐ڑ�����������B<br>
     * ���̃��\�b�h�� disconnect() ���ĂԂ����ł���B<br>
     */
    public void close(){
        this.disconnect();
    }


    //------------------------------------------------------------------
    //  method of Disconnectable
    //------------------------------------------------------------------
    //  public void disconnect
    //------------------------------------------------------------------
    /**
     * �ڑ����ꂽ���̓X�g���[���Ƃ̐ڑ�����������B<br>
     */
    public void disconnect(){
        this.in = null;
    }

}
//end of DisconnectableInputStream.java
