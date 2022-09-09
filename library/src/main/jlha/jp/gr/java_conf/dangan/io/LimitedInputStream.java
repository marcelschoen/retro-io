//start of LimitedInputStream.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF


/**
 * LimitedInputStream.java
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

import java.io.IOException;
import java.io.InputStream;

//import exceptions

/**
 * �ǂݍ��݉\�ȃf�[�^�ʂ��������ꂽ���̓X�g���[���B<br>
 *
 * <pre>
 * -- revision history --
 * $Log: LimitedInputStream.java,v $
 * Revision 1.1.2.1  2003/07/20 17:03:37  dangan
 * [maintenance]
 *     �ŐV�� LimitedInputStream ����\�[�X����荞�ށB
 *
 * Revision 1.1  2002/12/05 00:00:00  dangan
 * [maintenance]
 *     �\�[�X����
 *
 * Revision 1.0  2002/07/24 00:00:00  dangan
 * add to version control
 * [change]
 *     EndOfStream �ɒB������� read( new byte[0] ) �� 
 *     read( byte[] buf, int off, 0 ) �̖߂�l��
 *     InputStream �Ɠ����� 0 �ɂȂ�悤�ɂ���
 * [maintenance]
 *     �^�u�p�~
 *     ���C�Z���X���̏C��
 *
 * </pre>
 *
 * @author $Author: dangan $
 * @version $Revision: 1.1.2.1 $
 */
public class LimitedInputStream extends InputStream {


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  source
    //------------------------------------------------------------------
    //  private InputStream in
    //------------------------------------------------------------------
    /**
     * �ǂݍ��݌��E
     */
    private final long limit;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  current position
    //------------------------------------------------------------------
    //  private long position
    //  private final long limit
    //  private long markPosition
    //------------------------------------------------------------------
    /**
     * �ڑ����ꂽ���̓X�g���[��
     */
    private InputStream in;
    /**
     * ���ݓǂݍ��݈ʒu
     */
    private long position;
    /**
     * �}�[�N�ʒu
     */
    private long markPosition;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  public LimitedInputStream( InputStream in, long limit )
    //------------------------------------------------------------------

    /**
     * in ����̓ǂݍ��݉\�ȃf�[�^�ʂ𐧌�����
     * ���̓X�g���[�����\�z����B<br>
     *
     * @param in    ���̓X�g���[��
     * @param limit �ǂݍ��݉\�o�C�g��
     *
     * @exception IllegalArgumentException
     *              limit �������ł���ꍇ
     */
    public LimitedInputStream(InputStream in, long limit) {
        if (in != null && 0 <= limit) {
            this.in = in;
            this.position = 0;
            this.limit = limit;
            this.markPosition = -1;
        } else if (in == null) {
            throw new NullPointerException("in");
        } else {
            throw new IllegalArgumentException("limit must be 0 or more.");
        }
    }


    //------------------------------------------------------------------
    //  method of java.io.InputStream
    //------------------------------------------------------------------
    //  read
    //------------------------------------------------------------------
    //  public int read()
    //  public int read( byte[] buffer )
    //  public int read( byte[] buffer, int index, int length )
    //  public long skip( long length )
    //------------------------------------------------------------------

    /**
     * �ڑ����ꂽ���̓X�g���[������ 1�o�C�g�̃f�[�^��ǂݍ��ށB
     *
     * @return �ǂݍ��܂ꂽ 1�o�C�g�̃f�[�^<br>
     *         ����EndOfStream �ɒB���Ă������A
     *         �����ɒB�����ꍇ�� -1 ��Ԃ��B<br>
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public int read() throws IOException {
        if (this.position < this.limit) {
            int ret = this.in.read();                                           //throws IOException
            if (0 <= ret) {
                this.position++;
            }
            return ret;
        } else {
            return -1;
        }
    }

    /**
     * �ڑ����ꂽ���̓X�g���[������ buffer �𖞂����悤��
     * �f�[�^��ǂݍ��ށB<br>
     * �f�[�^�͕K������ buffer �𖞂����Ƃ͌���Ȃ����Ƃɒ��ӁB<br>
     *
     * @param buffer �ǂݍ��񂾃f�[�^���i�[���邽�߂̃o�C�g�z��<br>
     *
     * @return buffer �ɓǂݍ��񂾃f�[�^�ʂ��o�C�g���ŕԂ��B<br>
     *         ����EndOfStream �ɒB���Ă������A
     *         �����ɒB�����ꍇ�� -1 ��Ԃ��B<br>
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public int read(byte[] buffer) throws IOException {
        if (0 < buffer.length) {
            int ret;
            if (buffer.length < this.limit - this.position) {
                ret = this.in.read(buffer);                                   //throws IOException
            } else if (this.position < this.limit) {
                ret = this.in.read(buffer, 0, (int) (this.limit - this.position));//throws IOException
            } else {
                return -1;
            }
            if (0 < ret) {
                this.position += ret;
            }
            return ret;
        } else {
            return 0;
        }
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
     *         ����EndOfStream �ɒB���Ă������A
     *         �����ɒB�����ꍇ�� -1 ��Ԃ��B<br>
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public int read(byte[] buffer, int index, int length)
            throws IOException {

        if (0 < length) {
            if (this.limit <= this.position) {
                return -1;
            } else if (this.limit - this.position < length) {
                length = (int) (this.limit - this.position);
            }
            int ret = this.in.read(buffer, index, length);                    //throws IOException
            if (0 < ret) {
                this.position += ret;
            }
            return ret;
        } else {
            return 0;
        }
    }

    /**
     * �ڑ����ꂽ���̓X�g���[���̃f�[�^�� length �o�C�g�ǂݔ�΂��B<br>
     *
     * @param length �ǂݔ�΂��o�C�g���B<br>
     *
     * @return ���ۂɓǂݔ�΂��ꂽ�o�C�g���B<br>
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public long skip(long length) throws IOException {

        if (0 < length) {
            if (this.limit <= this.position) {
                return 0;
            } else if (this.limit - this.position < length) {
                length = this.limit - this.position;
            }
            length = this.in.skip(length);                                    //throws IOException
            if (0 < length) {
                this.position += length;
            }
            return length;
        } else {
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
    public void mark(int readLimit) {
        this.in.mark(readLimit);
        this.markPosition = this.position;
    }

    /**
     * �ڑ����ꂽ���̓X�g���[���̓ǂݍ��݈ʒu���Ō��
     * mark() ���\�b�h���Ăяo���ꂽ�Ƃ��̈ʒu�ɐݒ肷��B<br>
     *
     * @exception IOException <br>
     *              <ol>
     *                <li> LimitedInputStream �� mark ���Ȃ���Ă��Ȃ��ꍇ�B<br>
     *                <li> �ڑ����ꂽ���̓X�g���[���� markSupported()��
     *                     false ��Ԃ��ꍇ�B<br>
     *                <li> �ڑ����ꂽ���̓X�g���[����
     *                     ���o�̓G���[�����������ꍇ�B<br>
     *              </ol>
     *              �̉��ꂩ�B
     */
    public void reset() throws IOException {
        if (!this.in.markSupported()) {
            throw new IOException("not support mark()/reset().");
        } else if (this.markPosition < 0) { //�R���X�g���N�^�� MarkPosition �� -1 �ɐݒ肳���̂𗘗p����B
            throw new IOException("not marked.");
        } else {
            this.in.reset();                                                    //throws IOException
            this.position = this.markPosition;
        }
    }

    /**
     * �ڑ����ꂽ���̓X�g���[���� mark() �� reset() ��
     * �T�|�[�g���邩�𓾂�B<br>
     *
     * @return �X�g���[���� mark() �� reset() ��
     *         �T�|�[�g����ꍇ�� true�B<br>
     *         �T�|�[�g���Ȃ��ꍇ�� false�B<br>
     */
    public boolean markSupported() {
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
        return (int) Math.min((long) this.in.available(),                        //throws IOException
                this.limit - this.position);
    }

    /**
     * ���̓��̓X�g���[������A�g�p���Ă���
     * �S�Ẵ��\�[�X���J������B<br>
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void close() throws IOException {
        this.in.close();                                                        //throws IOException
        this.in = null;
    }

}
//end of LimitedInputStream.java
