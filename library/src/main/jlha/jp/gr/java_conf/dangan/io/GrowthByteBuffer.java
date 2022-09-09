//start of GrowthByteBuffer.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * GrowthByteBuffer.java
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

/**
 * �����I�ɐL������o�b�t�@�B<br>
 * RandomAccessFile �� �������łƂ��Ďg�p����B
 * �������A���܂苐��ȃf�[�^����舵���̂ɂ͌����Ȃ��B
 * �X���b�h�Z�[�t�ł͂Ȃ��B
 * jdk1.4 �ȍ~�� ByteBuffer�Ƃ͌݊����������B
 *
 * <pre>
 * -- revision history --
 * $Log: GrowthByteBuffer.java,v $
 * Revision 1.1  2002/12/05 00:00:00  dangan
 * [maintenance]
 *     �\�[�X����
 *
 * Revision 1.0  2002/07/24 00:00:00  dangan
 * add to version control
 * [bug fix]
 *     grow() �Ńo�b�t�@�̑����ʂ̌v�Z���Ԉ���Ă����̂��C���B
 * [change]
 *     �ǂݍ��݌��E�ɒB������� read( new byte[0] ) �� 
 *     read( byte[] buf, int off, 0 ) �̖߂�l��
 *     InputStream �Ɠ����� 0 �ɂȂ�悤�ɂ���
 * [maintenance]
 *     �\�[�X����
 *
 * </pre>
 *
 * @author $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class GrowthByteBuffer {


    //------------------------------------------------------------------
    //  class field
    //------------------------------------------------------------------
    //  default
    //------------------------------------------------------------------
    //  private static final int DefaultBufferSize
    //------------------------------------------------------------------
    /**
     * �f�t�H���g�̈�̃o�b�t�@�̃T�C�Y
     */
    private static final int DefaultBufferSize = 16384;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  byte buffer
    //------------------------------------------------------------------
    //  private byte[][] buffer
    //  private int position
    //  private int limit
    //------------------------------------------------------------------
    /**
     * �o�b�t�@
     * �S�� buffer[0].length �Ɠ����T�C�Y��byte�z��̔z��B
     */
    private byte[][] buffer;

    /**
     * ���ݏ����ʒu�B
     * position �� limit�ȍ~�ɂȂ�\��������B
     */
    private int position;

    /**
     * ���ݓǂ݂��݌��E�B
     * ����ȍ~�̃f�[�^�͕s��B
     * ���̈ʒu�̃f�[�^�͓ǂ߂邱�Ƃɒ��ӂ��邱�ƁB
     */
    private int limit;


    //------------------------------------------------------------------
    //  constructer
    //------------------------------------------------------------------
    //  public GrowthByteBuffer()
    //  public GrouthByteBuffer( int BufferSize )
    //------------------------------------------------------------------

    /**
     * �T�C�Y�������ŐL������o�b�t�@���\�z����B<br>
     * �o�b�t�@�T�C�Y�ɂ̓f�t�H���g�l���g�p�����B
     */
    public GrowthByteBuffer() {
        this(GrowthByteBuffer.DefaultBufferSize);
    }

    /**
     * �T�C�Y�������ŐL������o�b�t�@���\�z����B<br>
     *
     * @param BufferSize �o�b�t�@�̃T�C�Y
     */
    public GrowthByteBuffer(int BufferSize) {
        if (0 < BufferSize) {
            this.buffer = new byte[16][];
            this.buffer[0] = new byte[BufferSize];
            this.position = 0;
            this.limit = -1;
        } else {
            throw new IllegalArgumentException("BufferSize most be 1 or more.");
        }
    }


    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  write
    //------------------------------------------------------------------
    //  public void write( int data )
    //  public void write( byte[] buffer )
    //  public void write( byte[] buffer, int index, int length )
    //------------------------------------------------------------------

    /**
     * ���݈ʒu�� 1�o�C�g�̃f�[�^���������ށB
     *
     * @param data 1�o�C�g�̃f�[�^
     */
    public void write(int data) {
        this.grow(this.position);

        this.buffer[this.position / this.buffer[0].length]
                [this.position % this.buffer[0].length]
                = (byte) data;

        this.position++;
    }

    /**
     * ���݈ʒu�� buffer �̓��e���������ށB
     *
     * @param buffer �������ރf�[�^�يi�[���ꂽ�o�b�t�@
     */
    public void write(byte[] buffer) {
        this.write(buffer, 0, buffer.length);
    }

    /**
     * ���݈ʒu�� buffer �� index����length�o�C�g�̓��e���������ށB
     *
     * @param buffer �������ރf�[�^�يi�[���ꂽ�o�b�t�@
     * @param index  buffer���̏������ރf�[�^�̊J�n�ʒu
     * @param length �������ރf�[�^��
     */
    public void write(byte[] buffer, int index, int length) {
        this.grow(this.position + length - 1);

        while (0 < length) {
            int copylen = Math.min((this.position / this.buffer[0].length + 1)
                            * this.buffer[0].length,
                    this.position + length) - this.position;

            System.arraycopy(buffer, index,
                    this.buffer[this.position / this.buffer[0].length],
                    this.position % this.buffer[0].length,
                    copylen);

            this.position += copylen;
            index += copylen;
            length -= copylen;
        }
    }


    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  read
    //------------------------------------------------------------------
    //  public int read()
    //  public int read( byte[] buffer )
    //  public int read( byte[] buffer, int index, int length )
    //------------------------------------------------------------------

    /**
     * ���݈ʒu���� 1byte�̃f�[�^��ǂ݂��ށB
     *
     * @return �ǂ݂��܂ꂽ1byte�̃f�[�^�B<br>
     *         �ǂ݂��݌��E�𒴂��ēǂ����Ƃ����ꍇ�� -1
     */
    public int read() {
        if (this.position <= this.limit) {
            return this.buffer[this.position / this.buffer[0].length]
                    [this.position++ % this.buffer[0].length] & 0xFF;
        } else {
            return -1;
        }
    }

    /**
     * ���݈ʒu���� buffer�𖞂����悤�Ƀf�[�^��ǂݍ��ށB
     *
     * @param buffer �f�[�^��ǂݍ��ރo�b�t�@
     *
     * @return ���ۂɓǂ݂��܂ꂽ�f�[�^��<br>
     *         �ǂ݂��݌��E�𒴂��ēǂ����Ƃ����ꍇ�� -1
     */
    public int read(byte[] buffer) {
        return this.read(buffer, 0, buffer.length);
    }

    /**
     * ���݈ʒu���� buffer ��index�� length�̃f�[�^��ǂݍ��ށB
     *
     * @param buffer �f�[�^��ǂݍ��ރo�b�t�@
     * @param index  buffer���f�[�^�ǂ݂��݈ʒu
     * @param length �ǂݍ��ރf�[�^�̗�
     *
     * @return ���ۂɓǂ݂��܂ꂽ�f�[�^��<br>
     *         �ǂ݂��݌��E�𒴂��ēǂ����Ƃ����ꍇ�� -1
     */
    public int read(byte[] buffer, int index, int length) {
        if (this.position <= this.limit) {
            int len = 0;
            while (0 < length) {
                int copylen = Math.min(Math.min((this.position / this.buffer[0].length + 1)
                                        * this.buffer[0].length,
                                this.position + length),
                        this.limit + 1) - this.position;
                if (0 < copylen) {
                    System.arraycopy(this.buffer[this.position / this.buffer[0].length],
                            this.position % this.buffer[0].length,
                            buffer, index,
                            copylen);

                    this.position += copylen;
                    index += copylen;
                    len += copylen;
                    length -= copylen;
                } else {
                    break;
                }
            }
            return len;
        } else if (0 < length) {
            return -1;
        } else {
            return 0;
        }
    }


    //------------------------------------------------------------------
    //  original methods
    //------------------------------------------------------------------
    //  access methods
    //------------------------------------------------------------------
    //  public int length()
    //  public void setLength( int length )
    //  public int position()
    //  public void setPosition( int position )
    //  public void seek( int position )
    //------------------------------------------------------------------

    /**
     * ���݂̓ǂ݂��݌��E�𓾂�B
     *
     * @return ���݂̓ǂ݂��݌��E
     */
    public int length() {
        return this.limit + 1;
    }

    /**
     * �ǂ݂��݌��E�ʒu��ݒ肷��B
     *
     * @param �V�����ǂ݂��݌��E�ʒu
     */
    public void setLength(int length) {
        length--;
        if (this.limit < length) {
            this.grow(length);
        } else {
            this.limit = length;
        }
    }

    /**
     * ���݈ʒu�𓾂�B
     *
     * @return ���݈ʒu
     */
    public int position() {
        return this.position;
    }

    /**
     * ���݈ʒu��ݒ肷��B
     * java.io.RandomAccessFile�Ɠ����� 
     * setPosition �œǂ݂��݌��E�𒴂����l��
     * �ݒ肵������ɂ̓o�b�t�@�͑������Ă��Ȃ��B
     * ���̌� write �ɂ���ď������񂾎��ɂ͂�
     * �߂ăo�b�t�@�͑�������B
     *
     * @param position �V�������݈ʒu
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * ���݈ʒu��ݒ肷��B
     * java.io.RandomAccessFile�Ɠ����� 
     * seek �œǂ݂��݌��E�𒴂����l��
     * �ݒ肵������ɂ̓o�b�t�@�͑������Ă��Ȃ��B
     * ���̌� write �ɂ���ď������񂾎��ɂ͂�
     * �߂ăo�b�t�@�͑�������B
     *
     * @param position �V�������݈ʒu
     */
    public void seek(int position) {
        this.setPosition(position);
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  private void grow( int limit )
    //------------------------------------------------------------------

    /**
     * �V�����ǂ݂��݌��E limit ��ݒ肵�A
     * limit �܂� �o�b�t�@�𑝉�������B
     *
     * @param �V�����ǂ݂��݌��E
     */
    private void grow(int limit) {
        if (this.limit < limit) {
            int last = 0;
            while (last < this.buffer.length
                    && this.buffer[last] != null)
                last++;

            limit++;
            if (last * this.buffer[0].length < limit) {
                int need = (limit / this.buffer[0].length)
                        + (limit % this.buffer[0].length == 0 ? 0 : 1);

                if (this.buffer.length < need) {
                    byte[][] old = this.buffer;
                    this.buffer = new byte[Math.max(old.length * 2, need)][];

                    for (int i = 0; i < last; i++)
                        this.buffer[i] = old[i];
                }
                for (int i = last; i < need; i++)
                    this.buffer[i] = new byte[this.buffer[0].length];
            }

            this.limit = limit - 1;
        }
    }

}
//end of GrowthByteBuffer.java
