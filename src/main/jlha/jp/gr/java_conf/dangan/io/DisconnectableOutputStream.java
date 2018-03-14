//start of DisconnectableOutputStream.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * DisconnectableOutputStream.java
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
import java.io.OutputStream;

//import exceptions

/**
 * �f�[�^���������ďo�͂���o�̓X�g���[����
 * �f�[�^���f�o�C�X�ɏo�͂���X�g���[���Ƃ�
 * �ڑ����������邽�߂̃��[�e�B���e�B�N���X�B<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: DisconnectableOutputStream.java,v $
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
public class DisconnectableOutputStream extends OutputStream
                                        implements Disconnectable {

    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  sink
    //------------------------------------------------------------------
    //  private OutputStream out
    //------------------------------------------------------------------
    /**
     * �ڑ����ꂽ�o�̓X�g���[��
     */
    private OutputStream out;


    //------------------------------------------------------------------
    //  constructer
    //------------------------------------------------------------------
    //  private DisconnectableOutputStream()
    //  public DisconnectableOutputStream( OutputStream out )
    //------------------------------------------------------------------
    /**
     * �f�t�H���g�R���X�g���N�^�B
     * �g�p�s�B
     */
    private DisconnectableOutputStream(){   }

    /**
     * out �Ƃ̐ڑ��������\�ȏo�̓X�g���[�����\�z����B
     * 
     * @param out �o�̓X�g���[��
     */
    public DisconnectableOutputStream( OutputStream out ){
        if( out != null ){
            this.out = out;
        }else{
            throw new NullPointerException( "out" );
        }
    }


    //------------------------------------------------------------------
    //  method of java.io.OutputStream method
    //------------------------------------------------------------------
    //  write
    //------------------------------------------------------------------
    //  public void write( int data )
    //  public void write( byte[] buffer )
    //  public void write( byte[] buffer, int index, int length )
    //------------------------------------------------------------------
    /**
     * �ڑ����ꂽ�o�̓X�g���[���� 1�o�C�g�̃f�[�^���o�͂���B<br>
     * 
     * @param data �������܂��ׂ� 1�o�C�g�̃f�[�^�B<br>
     *             ��ʓI�ɏ��3�o�C�g�͖��������B<br>
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void write( int data ) throws IOException {
        this.out.write( data );                                                 //throws IOException
    }

    /**
     * �ڑ����ꂽ�o�̓X�g���[���� buffer���̃f�[�^��
     * �S�ďo�͂���B<br>
     * 
     * @param buffer �������܂��ׂ��f�[�^���i�[����
     *               �o�C�g�z��B<br>
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void write( byte[] buffer ) throws IOException {
        this.out.write( buffer, 0, buffer.length );                             //throws IOException
    }

    /**
     * �ڑ����ꂽ�o�̓X�g���[���� buffer���̃f�[�^��
     * index�Ŏw�肳�ꂽ�ʒu���� length�o�C�g�o�͂���B<br>
     * 
     * @param buffer �������܂��ׂ��f�[�^���i�[����
     *               �o�C�g�z��B<br>
     * @param index  buffer���̏������ނׂ��f�[�^�̊J�n�ʒu�B<br>
     * @param length �������ނׂ��f�[�^�ʁB<br>
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void write( byte[] buffer, int index, int length )
                                                           throws IOException {
        this.out.write( buffer, index, length );                                //throws IOException
    }


    //------------------------------------------------------------------
    //  method of java.io.OutputStream
    //------------------------------------------------------------------
    //  other
    //------------------------------------------------------------------
    //  public void flush()
    //  public void close()
    //------------------------------------------------------------------
    /**
     * �ڑ����ꂽ�o�̓X�g���[���ɒ~����ꂽ�f�[�^��S�ďo�͂���
     * �悤�Ɏw������B<br>
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void flush() throws IOException {
        this.out.flush();
    }

    /**
     * �ڑ����ꂽ�o�̓X�g���[���Ƃ̐ڑ�����������B<br>
     * ���̃��\�b�h�� disconnect() ���Ăяo�������ł���B<br>
     */
    public void close(){
        this.disconnect();
    }


    //------------------------------------------------------------------
    //  method of Disconnectable
    //------------------------------------------------------------------
    //  public void disconnect()
    //------------------------------------------------------------------
    /**
     * �ڑ����ꂽ�o�̓X�g���[���Ƃ̐ڑ�����������B<br>
     */
    public void disconnect(){
        this.out = null;
    }

}
//end of DisconnectableOutputStream.java
