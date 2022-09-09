//start of LittleEndian.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * LittleEndian.java
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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

//import exceptions


/**
 * ���g���G���f�B�A����
 * �o�C�g�z��� InputStream, OutputStream
 * �ɃA�N�Z�X���郁�\�b�h��񋟂��郆�[�e�B���e�B�N���X�B
 *
 * <pre>
 * -- revision history --
 * $Log: LittleEndian.java,v $
 * Revision 1.0  2002/07/24 00:00:00  dangan
 * add to version control
 * [change]
 *     writeByte(), readByte() ��P���B
 * [maintenance]
 *     �\�[�X����
 *
 * </pre>
 *
 * @author $Author: dangan $
 * @version $Revision: 1.0 $
 */
public class LittleEndian {


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private LittleEndian()
    //------------------------------------------------------------------

    /**
     * �f�t�H���g�R���X�g���N�^�B�g�p�s�B
     */
    private LittleEndian() {
    }


    //------------------------------------------------------------------
    //  shared method
    //------------------------------------------------------------------
    //  read from byte array
    //------------------------------------------------------------------
    //  public static final int readShort( byte[] ByteArray, int index )
    //  public static final int readInt( byte[] ByteArray, int index )
    //  public static final long readLong( byte[] ByteArray, int index )
    //------------------------------------------------------------------

    /**
     * ByteArray �� index �̈ʒu���� ���g���G���f�B�A����
     * 2�o�C�g�l��ǂݏo���B�ǂݏo���ꂽ 2�o�C�g�l�� 
     * 0x0000�`0xFFFF�Ƀ}�b�v�����B
     *
     * @param ByteArray �o�C�g�z��
     * @param index     ByteArray���̃f�[�^�̊J�n�ʒu
     *
     * @return �ǂݏo���ꂽ2�o�C�g�l
     *
     * @exception ArrayIndexOutOfBoundsException
     *                  index����n�܂�f�[�^�� 
     *                  ByteArray�͈͓̔��ɖ����ꍇ�B
     */
    public static final int readShort(byte[] ByteArray, int index) {
        return (ByteArray[index] & 0xFF)
                | ((ByteArray[index + 1] & 0xFF) << 8);
    }

    /**
     * ByteArray �� index �̈ʒu���烊�g���G���f�B�A����
     * 4�o�C�g�l��ǂݏo���B
     *
     * @param ByteArray �o�C�g�z��
     * @param index     ByteArray���̃f�[�^�̊J�n�ʒu
     *
     * @return �ǂݏo���ꂽ4�o�C�g�l
     *
     * @exception ArrayIndexOutOfBoundsException
     *                  index����n�܂�f�[�^�� 
     *                  ByteArray�͈͓̔��ɖ����ꍇ�B
     */
    public static final int readInt(byte[] ByteArray, int index) {
        return (ByteArray[index] & 0xFF)
                | ((ByteArray[index + 1] & 0xFF) << 8)
                | ((ByteArray[index + 2] & 0xFF) << 16)
                | (ByteArray[index + 3] << 24);
    }

    /**
     * ByteArray �� index �̈ʒu���烊�g���G���f�B�A����
     * 8�o�C�g�l��ǂݏo���B
     *
     * @param ByteArray �o�C�g�z��
     * @param index     ByteArray���̃f�[�^�̊J�n�ʒu
     *
     * @return �ǂݏo���ꂽ8�o�C�g�l
     *
     * @exception ArrayIndexOutOfBoundsException
     *                  index����n�܂�f�[�^�� 
     *                  ByteArray�͈͓̔��ɖ����ꍇ�B
     */
    public static final long readLong(byte[] ByteArray, int index) {
        return ((long) LittleEndian.readInt(ByteArray, index) & 0xFFFFFFFFL)
                | ((long) LittleEndian.readInt(ByteArray, index + 4) << 32L);
    }


    //------------------------------------------------------------------
    //  shared method
    //------------------------------------------------------------------
    //  read from InputStream
    //------------------------------------------------------------------
    //  public static final int readShort( InputStream in )
    //  public static final int readInt( InputStream in )
    //  public static final long readLong( InputStream in )
    //------------------------------------------------------------------

    /**
     * ���̓X�g���[�� in ���� ���g���G���f�B�A����
     * 2byte�l��ǂݏo���B
     *
     * @param in ���̓X�g���[��
     *
     * @return �ǂݏo���ꂽ2byte�l
     *
     * @exception EOFException
     *                  ���� End Of Stream�ɒB���Ă������A
     *                  �ǂݍ��݂̓r���� End Of Stream�ɒB�����B
     *                  �ǂݍ��ݓr���̃f�[�^�͏�������B
     * @exception IOException
     *                  ���o�̓G���[�����������ꍇ
     */
    public static final int readShort(InputStream in)
            throws IOException {
        int byte1 = in.read();
        int byte2 = in.read();

        if (0 <= byte1 && 0 <= byte2) {
            return (byte1 & 0xFF)
                    | ((byte2 & 0xFF) << 8);
        } else {
            throw new EOFException();
        }
    }

    /**
     * ���̓X�g���[�� in ���� ���g���G���f�B�A����
     * 4byte�l��ǂݏo���B
     *
     * @param in ���̓X�g���[��
     *
     * @return �ǂݏo���ꂽ4byte�l
     *
     * @exception EOFException
     *                  ���� End Of Stream�ɒB���Ă������A
     *                  �ǂݍ��݂̓r���� End Of Stream�ɒB�����B
     *                  �ǂݍ��ݓr���̃f�[�^�͏�������B
     * @exception IOException
     *                  ���o�̓G���[�����������ꍇ
     */
    public static final int readInt(InputStream in)
            throws IOException {
        int byte1 = in.read();
        int byte2 = in.read();
        int byte3 = in.read();
        int byte4 = in.read();

        if (0 <= byte1 && 0 <= byte2 && 0 <= byte3 && 0 <= byte4) {
            return (byte1 & 0xFF)
                    | ((byte2 & 0xFF) << 8)
                    | ((byte3 & 0xFF) << 16)
                    | (byte4 << 24);
        } else {
            throw new EOFException();
        }
    }

    /**
     * ���̓X�g���[�� in ���� ���g���G���f�B�A����
     * 8byte�l��ǂݏo���B
     *
     * @param in ���̓X�g���[��
     *
     * @return �ǂݏo���ꂽ8byte�l
     *
     * @exception EOFException
     *                  ���� End Of Stream�ɒB���Ă������A
     *                  �ǂݍ��݂̓r���� End Of Stream�ɒB�����B
     *                  �ǂݍ��ݓr���̃f�[�^�͏�������B
     * @exception IOException
     *                  ���o�̓G���[�����������ꍇ
     */
    public static final long readLong(InputStream in)
            throws IOException {

        return ((long) LittleEndian.readInt(in) & 0xFFFFFFFFL)
                | ((long) LittleEndian.readInt(in) << 32);

    }


    //------------------------------------------------------------------
    //  shared method
    //------------------------------------------------------------------
    //  write to byte array
    //------------------------------------------------------------------
    //  public static final void writeShort( byte[] ByteArray, int index, int value )
    //  public static final void writeInt( byte[] ByteArray, int index, int value )
    //  public static final void writeLong( byte[] ByteArray, int index, long value )
    //------------------------------------------------------------------

    /**
     * ByteArray �� index �̈ʒu�Ƀ��g���G���f�B�A����
     * 2byte�l�������o���B
     *
     * @param ByteArray �o�C�g�z��
     * @param index     ByteArray���̃f�[�^���������ވʒu
     * @param value     �������� 2byte�l
     *
     * @exception ArrayIndexOutOfBoundsException
     *                  index����n�܂�f�[�^�� 
     *                  ByteArray�͈͓̔��ɖ����ꍇ�B
     */
    public static final void writeShort(byte[] ByteArray,
                                        int index,
                                        int value) {

        if (0 <= index && index + 1 < ByteArray.length) {
            ByteArray[index] = (byte) value;
            ByteArray[index + 1] = (byte) (value >> 8);
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    /**
     * ByteArray �� index �̈ʒu�Ƀ��g���G���f�B�A����
     * 4byte�l�������o���B
     *
     * @param ByteArray �o�C�g�z��
     * @param index     ByteArray���̃f�[�^���������ވʒu
     * @param value     �������� 4byte�l
     *
     * @exception ArrayIndexOutOfBoundsException
     *                  index����n�܂�f�[�^�� 
     *                  ByteArray�͈͓̔��ɖ����ꍇ�B
     */
    public static final void writeInt(byte[] ByteArray,
                                      int index,
                                      int value) {

        if (0 <= index && index + 3 < ByteArray.length) {
            ByteArray[index] = (byte) value;
            ByteArray[index + 1] = (byte) (value >> 8);
            ByteArray[index + 2] = (byte) (value >> 16);
            ByteArray[index + 3] = (byte) (value >> 24);
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    /**
     * ByteArray �� index �̈ʒu�Ƀ��g���G���f�B�A����
     * 8byte�l�������o���B
     *
     * @param ByteArray �o�C�g�z��
     * @param index     ByteArray���̃f�[�^���������ވʒu
     * @param value     �������� 8byte�l
     *
     * @exception ArrayIndexOutOfBoundsException
     *                  index����n�܂�f�[�^�� 
     *                  ByteArray�͈͓̔��ɖ����ꍇ�B
     */
    public static final void writeLong(byte[] ByteArray,
                                       int index,
                                       long value) {
        if (0 <= index && index + 7 < ByteArray.length) {
            LittleEndian.writeInt(ByteArray, index, (int) value);
            LittleEndian.writeInt(ByteArray, index + 4, (int) (value >> 32));
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }


    //------------------------------------------------------------------
    //  shared method
    //------------------------------------------------------------------
    //  write to OutputStream
    //------------------------------------------------------------------
    //  public static final void writeShort( OutputStream out, int value )
    //  public static final void writeInt( OutputStream out, int value )
    //  public static final void writeLong( OutputStream out, long value )
    //------------------------------------------------------------------

    /**
     * �o�̓X�g���[�� out �� ���g���G���f�B�A����
     * 2�o�C�g�����o���B
     *
     * @param out   �o�̓X�g���[��
     * @param value �����o��2�o�C�g�l
     *
     * @exception IOException
     *                  ���o�̓G���[�����������ꍇ
     */
    public static final void writeShort(OutputStream out, int value)
            throws IOException {
        out.write(value & 0xFF);
        out.write((value >> 8) & 0xFF);
    }

    /**
     * �o�̓X�g���[�� out �� ���g���G���f�B�A����
     * 4�o�C�g�l�������o���B
     *
     * @param out   �o�̓X�g���[��
     * @param value �����o��1�o�C�g�l
     *
     * @exception IOException
     *                  ���o�̓G���[�����������ꍇ
     */
    public static final void writeInt(OutputStream out, int value)
            throws IOException {
        out.write(value & 0xFF);
        out.write((value >> 8) & 0xFF);
        out.write((value >> 16) & 0xFF);
        out.write(value >>> 24);
    }

    /**
     * �o�̓X�g���[�� out �� ���g���G���f�B�A����
     * 8�o�C�g�l�������o���B
     *
     * @param out   �o�̓X�g���[��
     * @param value �����o��1�o�C�g�l
     *
     * @exception IOException
     *                  ���o�̓G���[�����������ꍇ
     */
    public static final void writeLong(OutputStream out, long value)
            throws IOException {
        int low = (int) value;
        int hi = (int) (value >> 32);

        out.write(low & 0xFF);
        out.write((low >> 8) & 0xFF);
        out.write((low >> 16) & 0xFF);
        out.write(low >>> 24);
        out.write(hi & 0xFF);
        out.write((hi >> 8) & 0xFF);
        out.write((hi >> 16) & 0xFF);
        out.write(hi >>> 24);
    }

}
//end of LittleEndian.java
