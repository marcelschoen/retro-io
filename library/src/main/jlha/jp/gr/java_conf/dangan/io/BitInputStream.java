//start of BitInputStream.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * BitInputStream.java
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

//import exceptions

/**
 * �r�b�g���͂̂��߂̃��[�e�B���e�B�N���X�B<br>
 *
 * <pre>
 * -- revision history --
 * $Log: BitInputStream.java,v $
 * Revision 1.5  2002/12/07 00:00:00  dangan
 * [maintenance]
 *     �\�[�X����
 *
 * Revision 1.4  2002/11/15 00:00:00  dangan
 * [improvement]
 *     prefetchBits() ��  32bit �̓ǂݍ��݂�ۏ؂���悤�ɏC��
 * [change]
 *     ���\�b�h���̕ύX
 *     prefetchBit     -> peekBit
 *     prefetchBoolean -> peekBoolean
 *     prefetchBits    -> peekBits
 *
 * Revision 1.3  2002/11/02 00:00:00  dangan
 * [bug fix]
 *     available() availableBits() ��
 *     �u���b�N�����ɓǂݍ��߂�ʂ����傫���l��Ԃ��Ă����B
 *
 * Revision 1.2  2002/09/05 00:00:00  dangan
 * [change]
 *     EndOfStream �ɒB������� read( new byte[0] ) �� 
 *     read( byte[] buf, int off, 0 ) �̖߂�l��
 *     InputStream �Ɠ����� 0 �ɂȂ�悤�ɂ���
 *
 * Revision 1.1  2002/09/04 00:00:00  dangan
 * [bug fix]
 *     skip( len ) �� skipBits( len ) �� len �� 0 �����̂Ƃ�
 *     �����������ł��Ă��Ȃ������B
 *
 * Revision 1.0  2002/09/03 00:00:00  dangan
 * add to version control
 * [bug fix]
 *     mark() �� �ڑ����ꂽ in �ɓn�� readLimit �̌v�Z���Â��������߁A
 *     �v�����ꂽ readLimit �ɒB����O�Ƀ}�[�N�ʒu���j������鎖���������B
 *     EndOfStream �ɒB������� skip() ����� skip( 0 ) �� -1 ��Ԃ��Ă����B
 * [maintenance]
 *     �^�u�p�~
 *     ���C�Z���X���̏C��
 *
 *
 * </pre>
 *
 * @author $Author: dangan $
 * @version $Revision: 1.5 $
 */
public class BitInputStream extends InputStream {

    //------------------------------------------------------------------
    //  class field
    //------------------------------------------------------------------
    //  default
    //------------------------------------------------------------------
    //  private static final int DefaultCacheSize
    //------------------------------------------------------------------
    /**
     * �f�t�H���g�̃L���b�V���T�C�Y
     */
    private static final int DefaultCacheSize = 1024;


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
    //  instance field
    //------------------------------------------------------------------
    //  cache
    //------------------------------------------------------------------
    //  private byte[] cache
    //  private int    cacheLimit
    //  private int    cachePosition
    //------------------------------------------------------------------
    /**
     * ���x�ቺ�}�~�p�o�C�g�z��
     */
    private byte[] cache;

    /**
     * cache ���̗L���o�C�g��
     */
    private int cacheLimit;

    /**
     * cache ���̌��ݏ����ʒu
     */
    private int cachePosition;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  bit buffer
    //------------------------------------------------------------------
    //  private int    bitBuffer
    //  private int    bitCount
    //------------------------------------------------------------------
    /**
     * �r�b�g�o�b�t�@�B
     * �r�b�g�f�[�^�͍ŏ�ʃr�b�g���� bitCount �����i�[����Ă���B
     */
    private int bitBuffer;

    /**
     * bitBuffer �� �L���r�b�g��
     */
    private int bitCount;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  backup for mark/reset
    //------------------------------------------------------------------
    //  private boolean markPositionIsInCache
    //  private byte[] markCache
    //  private int    markCacheLimit
    //  private int    markCachePosition
    //  private int    markBitBuffer
    //  private int    markBitCount
    //------------------------------------------------------------------
    /**
     * mark�ʒu���L���b�V���͈͓̔��ɂ��邩�������B
     * mark���ꂽ�Ƃ� true �ɐݒ肳��A
     * ���� in ���� �L���b�V���ւ̓ǂݍ��݂�
     * �s��ꂽ�Ƃ��� false �ɐݒ肳���B
     */
    private boolean markPositionIsInCache;

    /** cache �� �o�b�N�A�b�v�p */
    private byte[] markCache;

    /** cacheLimit �̃o�b�N�A�b�v�p */
    private int markCacheLimit;

    /** cachePosition �̃o�b�N�A�b�v�p */
    private int markCachePosition;

    /** bitBuffer �̃o�b�N�A�b�v�p */
    private int markBitBuffer;

    /** bitCount �̃o�b�N�A�b�v�p */
    private int markBitCount;


    //------------------------------------------------------------------
    //  constructer
    //------------------------------------------------------------------
    //  private BitInputStream()
    //  public BitInputStream( InputStream in )
    //  public BitInputStream( InputStream in, int CacheSize )
    //------------------------------------------------------------------

    /**
     * �f�t�H���g�R���X�g���N�^�B
     * �g�p�s�B
     */
    private BitInputStream() {
    }

    /**
     * ���̓X�g���[�� in ����̃f�[�^���r�b�g�P�ʂ�
     * �ǂݍ��߂�悤�ȃX�g���[�����\�z����B<br>
     *
     * @param in ���̓X�g���[��
     */
    public BitInputStream(InputStream in) {
        this(in, BitInputStream.DefaultCacheSize);
    }

    /**
     * ���̓X�g���[�� in ����̃f�[�^���r�b�g�P�ʂ�
     * �ǂݍ��߂�悤�ȃX�g���[�����\�z����B<br>
     *
     * @param in        ���̓X�g���[��
     * @param CacheSize �o�b�t�@�T�C�Y
     */
    public BitInputStream(InputStream in, int CacheSize) {
        if (in != null && 4 <= CacheSize) {
            this.in = in;
            this.cache = new byte[CacheSize];
            this.cacheLimit = 0;
            this.cachePosition = 0;
            this.bitBuffer = 0;
            this.bitCount = 0;

            this.markPositionIsInCache = false;
            this.markCache = null;
            this.markCacheLimit = 0;
            this.markCachePosition = 0;
            this.markBitBuffer = 0;
            this.markBitCount = 0;
        } else if (in == null) {
            throw new NullPointerException("in");
        } else {
            throw new IllegalArgumentException("CacheSize must be 4 or more.");
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
     * �ڑ����ꂽ�X�g���[������ 8�r�b�g�̃f�[�^��ǂݍ��ށB<br>
     *
     * @return �ǂݏo���ꂽ 8�r�b�g�̃f�[�^�B<br>
     *         ���� EndOfStream �ɒB���Ă���ꍇ�� -1
     *
     * @exception IOException
     *               �ڑ����ꂽ���̓X�g���[����
     *               ���o�̓G���[�����������ꍇ
     * @exception BitDataBrokenException
     *               EndOfStream�ɒB��������
     *               �v�����ꂽ�r�b�g���̃f�[�^��
     *               �ǂݍ��݂Ɏ��s�����ꍇ�B<br>
     */
    public int read() throws IOException {
        try {
            return this.readBits(8);                                          //throws LocalEOFException BitDataBrokenException IOException
        } catch (LocalEOFException exception) {
            if (exception.thrownBy(this)) return -1;
            else throw exception;
        }
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
     * @exception IOException
     *               �ڑ����ꂽ���̓X�g���[����
     *               ���o�̓G���[�����������ꍇ
     * @exception BitDataBrokenException
     *               EndOfStream�ɒB��������
     *               �v�����ꂽ�r�b�g���̃f�[�^��
     *               �ǂݍ��݂Ɏ��s�����ꍇ�B<br>
     */
    public int read(byte[] buffer) throws IOException {
        return this.read(buffer, 0, buffer.length);                           //throws BitDataBrokenException IOException
    }

    /**
     * �ڑ����ꂽ���̓X�g���[������ �o�C�g�z�� buffer ��
     * index �Ŏw�肳�ꂽ�ʒu���� length �o�C�g�̃f�[�^��
     * �ǂݍ��ށB<br>
     * ���̃��\�b�h�� length�o�C�g�ǂݍ��ނ��A
     * EndOfStream �ɓ��B����܂Ńu���b�N����B<br>
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
     * @exception IOException
     *               �ڑ����ꂽ���̓X�g���[����
     *               ���o�̓G���[�����������ꍇ
     * @exception BitDataBrokenException
     *               EndOfStream�ɒB��������
     *               �v�����ꂽ�r�b�g���̃f�[�^��
     *               �ǂݍ��݂Ɏ��s�����ꍇ�B<br>
     */
    public int read(byte[] buffer, int index, int length) throws IOException {
        final int requested = length;
        try {
            while (0 < length) {
                buffer[index++] = (byte) this.readBits(8);                     //throws LocalEOFException BitDataBrokenException IOException
                length--;
            }
            return requested;
        } catch (LocalEOFException exception) {
            if (exception.thrownBy(this)) {
                if (requested != length) return requested - length;
                else return -1;
            } else {
                throw exception;
            }
        } catch (BitDataBrokenException exception) {
            if (exception.getCause() instanceof LocalEOFException
                    && ((LocalEOFException) exception.getCause()).thrownBy(this)) {
                this.bitBuffer >>>= exception.getBitCount();
                this.bitCount += exception.getBitCount();
                this.bitBuffer |= exception.getBitData() <<
                        (32 - exception.getBitCount());

                return requested - length;
            } else {
                throw exception;
            }
        }
    }

    /**
     * �ڑ����ꂽ���̓X�g���[���̃f�[�^�� length �o�C�g
     * �ǂݔ�΂��B<br>
     * ���̃��\�b�h�� length�o�C�g�ǂݔ�΂����A
     * EndOfStream �ɓ��B����܂Ńu���b�N����B<br>
     * �f�[�^�͕K������ length �o�C�g�ǂݔ�΂����Ƃ͌���
     * �Ȃ����Ƃɒ��ӁB<br>
     *
     * @param length �ǂݔ�΂��o�C�g���B<br>
     *
     * @return ���ۂɓǂݔ�΂��ꂽ�o�C�g���B<br>
     *
     * @exception IOException �ڑ����ꂽ���̓X�g���[����
     *                        ���o�̓G���[�����������ꍇ
     */
    public long skip(long length) throws IOException {
        length = (0 < length ? length : 0);
        final long requested = length;
        try {
            while (0 < length) {
                this.readBits(8);
                length--;
            }
            return requested;
        } catch (LocalEOFException exception) {
            return requested - length;
        } catch (BitDataBrokenException exception) {
            if (exception.getCause() instanceof LocalEOFException
                    && ((LocalEOFException) exception.getCause()).thrownBy(this)) {
                this.bitBuffer >>>= exception.getBitCount();
                this.bitCount += exception.getBitCount();
                this.bitBuffer |= exception.getBitData() <<
                        (32 - exception.getBitCount());
                return requested - length;
            } else {
                throw exception;
            }
        }
    }


    //------------------------------------------------------------------
    //  method of java.io.InputStream
    //------------------------------------------------------------------
    //  mark/reset
    //------------------------------------------------------------------
    //  public void mark( int readLimit )
    //  public void reset()
    //  public boolean markSupported()
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
        readLimit -= this.cacheLimit - this.cachePosition;
        readLimit -= this.bitCount / 8;
        readLimit += 4;
        readLimit = ((readLimit / this.cache.length) * this.cache.length
                + (readLimit % this.cache.length == 0 ? 0 : this.cache.length));

        this.in.mark(readLimit);

        if (this.markCache == null) {
            this.markCache = (byte[]) this.cache.clone();
        } else {
            System.arraycopy(this.cache, 0,
                    this.markCache, 0,
                    this.cacheLimit);
        }
        this.markCacheLimit = this.cacheLimit;
        this.markCachePosition = this.cachePosition;
        this.markBitBuffer = this.bitBuffer;
        this.markBitCount = this.bitCount;
        this.markPositionIsInCache = true;
    }

    /**
     * �ڑ����ꂽ���̓X�g���[���̓ǂݍ��݈ʒu���Ō��
     * mark() ���\�b�h���Ăяo���ꂽ�Ƃ��̈ʒu�ɐݒ肷��B<br>
     *
     * @exception IOException <br>
     *              (1) BitInputStream �� mark ���Ȃ���Ă��Ȃ��ꍇ�B<br>
     *              (2) �ڑ����ꂽ���̓X�g���[���� markSupported()��
     *                  false ��Ԃ��ꍇ�B<br>
     *              (3) �ڑ����ꂽ���̓X�g���[����
     *                  ���o�̓G���[�����������ꍇ�B<br>
     *              �̉��ꂩ�B
     */
    public void reset() throws IOException {
        if (this.markPositionIsInCache) {
            this.cachePosition = this.markCachePosition;
            this.bitBuffer = this.markBitBuffer;
            this.bitCount = this.markBitCount;
        } else if (!this.in.markSupported()) {
            throw new IOException("not support mark()/reset().");
        } else if (this.markCache == null) { //���̏������͖����Ƀ}�[�N����Ă��Ȃ����Ƃ������B�R���X�g���N�^�� markCache �� null �ɐݒ肳���̂𗘗p����B
            throw new IOException("not marked.");
        } else {
            //in �� reset() �ł��Ȃ��ꍇ��
            //�ŏ��̍s�� this.in.reset() ��
            //IOException �𓊂��邱�Ƃ����҂��Ă���B
            this.in.reset();                                                    //throws IOException
            System.arraycopy(this.markCache, 0,
                    this.cache, 0,
                    this.markCacheLimit);
            this.cacheLimit = this.markCacheLimit;
            this.cachePosition = this.markCachePosition;
            this.bitBuffer = this.markBitBuffer;
            this.bitCount = this.markBitCount;
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
     * @exception IOException �ڑ����ꂽ���̓X�g���[����
     *                        ���o�̓G���[�����������ꍇ
     */
    public int available() throws IOException {
        return this.availableBits() / 8;                                        //throws IOException
    }

    /**
     * ���̓��̓X�g���[������A
     * �g�p���Ă������\�[�X���J������B<br>
     *
     * @exception IOException �ڑ����ꂽ���̓X�g���[����
     *                        ���o�̓G���[�����������ꍇ
     */
    public void close() throws IOException {
        this.in.close();                                                        //throws IOException
        this.in = null;

        this.cache = null;
        this.cacheLimit = 0;
        this.cachePosition = 0;
        this.bitBuffer = 0;
        this.bitCount = 0;

        this.markCache = null;
        this.markCacheLimit = 0;
        this.markCachePosition = 0;
        this.markBitBuffer = 0;
        this.markBitCount = 0;
        this.markPositionIsInCache = false;
    }


    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  read
    //------------------------------------------------------------------
    //  public int readBit()
    //  public boolean readBoolean()
    //  public int readBits( int count )
    //  public int skipBits( int count )
    //------------------------------------------------------------------

    /**
     * �ڑ����ꂽ���̓X�g���[������ 1�r�b�g�̃f�[�^��
     * �ǂݍ��ށB<br>
     *
     * @return �ǂݍ��܂ꂽ1�r�b�g�̃f�[�^�B<br>
     *         ����EndOfStream�ɒB���Ă���ꍇ�� -1�B<br>
     *
     * @exception IOException �ڑ����ꂽ���̓X�g���[����
     *                        ���o�̓G���[�����������ꍇ
     */
    public int readBit() throws IOException {
        if (0 < this.bitCount) {
            int bit = this.bitBuffer >>> 31;
            this.bitBuffer <<= 1;
            this.bitCount -= 1;
            return bit;
        } else {
            try {
                this.fillBitBuffer();
                int bit = this.bitBuffer >>> 31;
                this.bitBuffer <<= 1;
                this.bitCount -= 1;
                return bit;
            } catch (LocalEOFException exception) {
                if (exception.thrownBy(this)) {
                    return -1;
                } else {
                    throw exception;
                }
            }
        }
    }

    /**
     * �ڑ����ꂽ���̓X�g���[������ 1�r�b�g�̃f�[�^��
     * �^�U�l�Ƃ��ēǂݍ��ށB<br>
     *
     * @return �ǂݍ��܂ꂽ1�r�b�g�̃f�[�^�� 
     *         1�ł���� true�A0�ł���� false ��Ԃ��B<br>
     *
     * @exception EOFException ����EndOfStream�ɒB���Ă����ꍇ
     * @exception IOException  �ڑ����ꂽ���̓X�g���[����
     *                         ���o�̓G���[�����������ꍇ
     */
    public boolean readBoolean() throws IOException {
        if (0 < this.bitCount) {
            boolean bool = (this.bitBuffer < 0);
            this.bitBuffer <<= 1;
            this.bitCount -= 1;
            return bool;
        } else {
            this.fillBitBuffer();
            boolean bool = (this.bitBuffer < 0);
            this.bitBuffer <<= 1;
            this.bitCount -= 1;
            return bool;
        }
    }

    /**
     * �ڑ����ꂽ���̓X�g���[������ count �r�b�g�̃f�[�^��
     * �ǂݍ��ށB �߂�l�� int�l�ł��鎖���������悤��
     * �ǂݍ��ނ��Ƃ̂ł��� �ő�L���r�b�g���� 32�r�b�g��
     * ���邪�Acount ��32�ȏ�̒l��ݒ肵�Ă��`�F�b�N��
     * �󂯂Ȃ����� ����ȏ�̒l��ݒ肵���ꍇ�� �r�b�g
     * �f�[�^���ǂݎ̂Ă���B<br>
     * ���Ƃ��� readBits( 33 ) �Ƃ����Ƃ��� �܂�1�r�b�g��
     * �f�[�^��ǂݎ̂āA���̌�� 32�r�b�g�̃f�[�^��Ԃ��B<br>
     * �܂� count �� 0�ȉ��̐�����ݒ肵�ČĂяo�����ꍇ�A
     * �f�[�^��ǂݍ��ޓ���𔺂�Ȃ����� �߂�l�� ���0�A
     * EndOfStream �ɒB���Ă��Ă� EOFException ��
     * �����Ȃ��_�ɒ��ӂ��邱�ƁB<br>
     *
     * @param count  �ǂݍ��ރf�[�^�̃r�b�g��
     *
     * @return �ǂݍ��܂ꂽ�r�b�g�f�[�^�B<br>
     *
     * @exception IOException
     *               �ڑ����ꂽ���̓X�g���[����
     *               ���o�̓G���[�����������ꍇ
     * @exception EOFException
     *               ����EndOfStream�ɒB���Ă����ꍇ
     * @exception BitDataBrokenException
     *               �ǂݍ��ݓr���� EndOfStream�ɒB��������
     *               �v�����ꂽ�r�b�g���̃f�[�^�̓ǂݍ���
     *               �Ɏ��s�����ꍇ�B<br>
     */
    public int readBits(int count) throws IOException {
        if (0 < count) {
            if (count <= this.bitCount) {
                int bits = this.bitBuffer >>> (32 - count);
                this.bitBuffer <<= count;
                this.bitCount -= count;
                return bits;
            } else {
                final int requested = count;
                int bits = 0;
                try {
                    this.fillBitBuffer();                                       //throws LocalEOFException IOException
                    while (this.bitCount < count) {
                        count -= this.bitCount;
                        if (count < 32) {
                            bits |= (this.bitBuffer >>> (32 - this.bitCount)) << count;
                        }
                        this.bitBuffer = 0;
                        this.bitCount = 0;
                        this.fillBitBuffer();                                   //throws LocalEOFException IOException
                    }
                    bits |= this.bitBuffer >>> (32 - count);
                    this.bitBuffer <<= count;
                    this.bitCount -= count;
                    return bits;
                } catch (LocalEOFException exception) {
                    if (exception.thrownBy(this) && count < requested) {
                        throw new BitDataBrokenException(exception, bits >>> count, requested - count);
                    } else {
                        throw exception;
                    }
                }
            }
        } else {
            return 0;
        }
    }

    /**
     * �ڑ����ꂽ�X�g���[������ count �r�b�g�̃f�[�^��
     * �ǂݔ�΂��B<br>
     *
     * @param count �ǂݔ�΂��Ăق����r�b�g��
     *
     * @return ���ۂɓǂݔ�т����r�b�g��
     *
     * @exception IOException �ڑ����ꂽ���̓X�g���[����
     *                        ���o�̓G���[�����������ꍇ
     */
    public int skipBits(int count) throws IOException {
        count = Math.max(count, 0);

        if (count < this.bitCount) {
            this.bitBuffer <<= count;
            this.bitCount -= count;
            return count;
        } else {
            final int requested = count;
            count -= this.bitCount;
            this.bitCount = 0;
            this.bitBuffer = 0;
            try {
                while ((this.cacheLimit - this.cachePosition) * 8 <= count) {
                    count -= (this.cacheLimit - this.cachePosition) * 8;
                    this.cachePosition = this.cacheLimit;
                    this.fillCache();
                    if (this.cacheLimit == this.cachePosition) {
                        throw new LocalEOFException(this);
                    }
                }
                this.cachePosition += (count >> 3);
                count = count & 0x07;
                if (0 < count) {
                    this.bitCount = 8 - count;
                    this.bitBuffer = this.cache[this.cachePosition++] << (24 + count);
                    count = 0;
                }
            } catch (LocalEOFException exception) {
            }
            return requested - count;
        }
    }


    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  prefetch
    //------------------------------------------------------------------
    //  public int peekBit()
    //  public boolean peekBoolean()
    //  public int peekBits( int count )
    //------------------------------------------------------------------

    /**
     * �ǂݍ��݈ʒu��ς����� 1�r�b�g�̃f�[�^���ǂ݂���B<br>
     *
     * @return �ǂݍ��܂ꂽ1�r�b�g�̃f�[�^�B<br>
     *         ����EndOfStream�ɒB���Ă���ꍇ�� -1�B<br>
     *
     * @exception IOException �ڑ����ꂽ���̓X�g���[����
     *                        ���o�̓G���[�����������ꍇ
     */
    public int peekBit() throws IOException {
        if (0 < this.bitCount) {
            return this.bitBuffer >>> 31;
        } else {
            try {
                this.fillBitBuffer();                                           //throws LocalEOFException IOException
                return this.bitBuffer >>> 31;
            } catch (LocalEOFException exception) {
                if (exception.thrownBy(this)) {
                    return -1;
                } else {
                    throw exception;
                }
            }

        }
    }

    /**
     * �ǂݍ��݈ʒu��ς����� 1�r�b�g�̃f�[�^��
     * �^�U�l�Ƃ��Đ�ǂ݂���B<br>
     *
     * @return �ǂݍ��܂ꂽ1�r�b�g�̃f�[�^�� 
     *         1�ł���� true�A0�ł���� false ��Ԃ��B<br>
     *
     * @exception EOFException ����EndOfStream�ɒB���Ă����ꍇ
     * @exception IOException  �ڑ����ꂽ���̓X�g���[����
     *                         ���o�̓G���[�����������ꍇ
     */
    public boolean peekBoolean() throws IOException {
        if (0 < this.bitCount) {
            return (this.bitBuffer < 0);
        } else {
            this.fillBitBuffer();                                               //throws LocalEOFException IOException
            return (this.bitBuffer < 0);
        }
    }

    /**
     * �ǂݍ��݈ʒu��ς����� count �r�b�g�̃f�[�^���ǂ݂���B<br>
     * �߂�l�� int�^�ł��邱�Ƃ�����킩��悤��
     * �ő�L���r�b�g���� 32�r�b�g�ł���B<br>
     * EndOfStream �t�߂������āA��ǂݏo���邱�Ƃ��ۏႳ���̂�
     * 32�r�b�g�ł���B(�r�b�g�o�b�t�@�̑傫���� 32�r�b�g�ł��邽��)<br>
     * ���� 32�r�b�g�ȏ�̐�ǂ݋@�\���K�{�ƂȂ�ꍇ��
     * ���̓s�x mark()�AreadBits()�Areset() ���J��Ԃ����A
     * ���̃N���X���g�p���邱�Ƃ���߂邱�ƁB<br>
     *
     * @param count �ǂݍ��ރr�b�g��
     *
     * @return ��ǂ݂��� count �r�b�g�̃r�b�g�f�[�^
     *
     * @exception EOFException
     *                    ����EndOfStream�ɒB���Ă����ꍇ
     * @exception IOException
     *                    �ڑ����ꂽ���̓X�g���[����
     *                    ���o�̓G���[�����������ꍇ
     * @exception NotEnoughBitsException
     *                    count ����ǂ݉\�Ȕ͈͊O�̏ꍇ
     */
    public int peekBits(int count) throws IOException {
        if (0 < count) {
            if (count <= this.bitCount) {
                return this.bitBuffer >>> (32 - count);
            } else {
                this.fillBitBuffer();                                           //throws LocalEOFException, IOException
                if (count <= this.bitCount) {
                    return this.bitBuffer >>> (32 - count);
                } else if (count <= this.cachedBits()) {
                    if (count <= 32) {
                        int bits = this.bitBuffer;
                        bits |= (this.cache[this.cachePosition] & 0xFF)
                                >> (this.bitCount - 24);
                        return bits >>> (32 - count);
                    } else if (count - 32 < this.bitCount) {
                        int bits = this.bitBuffer << (count - 32);
                        ;
                        int bcnt = this.bitCount - (count - 32);
                        int pos = this.cachePosition;
                        while (bcnt < 25) {
                            bits |= (this.cache[pos++] & 0xFF) << (24 - bcnt);
                            bcnt += 8;
                        }
                        if (bcnt < 32) {
                            bits |= (this.cache[pos] & 0xFF) >> (bcnt - 24);
                        }
                        return bits;
                    } else {
                        count -= this.bitCount;
                        count -= 32;
                        int pos = this.cachePosition + (count >> 3);
                        count &= 0x07;
                        if (0 < count) {
                            return (this.cache[pos] << (24 + count))
                                    | ((this.cache[pos + 1] & 0xFF) << (16 + count))
                                    | ((this.cache[pos + 2] & 0xFF) << (8 + count))
                                    | ((this.cache[pos + 3] & 0xFF) << count)
                                    | ((this.cache[pos + 4] & 0xFF) >> (8 - count));
                        } else {
                            return (this.cache[pos] << 24)
                                    | ((this.cache[pos + 1] & 0xFF) << 16)
                                    | ((this.cache[pos + 2] & 0xFF) << 8)
                                    | (this.cache[pos + 3] & 0xFF);
                        }
                    }
                } else {
                    throw new NotEnoughBitsException(this.cachedBits());
                }
            }
        } else {
            return 0;
        }
    }


    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  other
    //------------------------------------------------------------------
    //  public int availableBits()
    //  private int cachedBits()
    //------------------------------------------------------------------

    /**
     * �ڑ����ꂽ���̓X�g���[������u���b�N���Ȃ���
     * �ǂݍ��ނ��Ƃ̂ł���r�b�g���𓾂�B<br>
     *
     * @return �u���b�N���Ȃ��œǂݏo����r�b�g���B<br>
     *
     * @exception IOException �ڑ����ꂽ���̓X�g���[����
     *                        ���o�̓G���[�����������ꍇ
     */
    public int availableBits() throws IOException {
        int avail = (this.cacheLimit - this.cachePosition)
                + this.in.available() / this.cache.length * this.cache.length;//throws IOException
        avail += this.bitCount - 32;

        return Math.max(avail, 0);
    }

    /**
     * ���� BitInputStream ���ɒ~�����Ă���r�b�g���𓾂�B<br>
     *
     * @return ���� BitInputStream ���ɒ~�����Ă���r�b�g���B<br>
     */
    private int cachedBits() {
        return this.bitCount + ((this.cacheLimit - this.cachePosition) << 3);
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  fill
    //------------------------------------------------------------------
    //  private void fillBitBuffer()
    //  private void fillCache()
    //------------------------------------------------------------------

    /**
     * bitBuffer �Ƀf�[�^�𖞂����B
     * EndOfStream �t�߂������� bitBuffer �ɂ�
     * 25bit �̃f�[�^���m�ۂ���邱�Ƃ�ۏႷ��B
     *
     * @exception IOException       ���o�̓G���[�����������ꍇ
     * @exception LocalEOFException bitBuffer ����̏�Ԃ� EndOfStream �ɒB�����ꍇ
     */
    private void fillBitBuffer() throws IOException {
        if (32 <= this.cachedBits()) {
            if (this.bitCount <= 24) {
                if (this.bitCount <= 16) {
                    if (this.bitCount <= 8) {
                        if (this.bitCount <= 0) {
                            this.bitBuffer = this.cache[this.cachePosition++] << 24;
                            this.bitCount = 8;
                        }
                        this.bitBuffer |= (this.cache[this.cachePosition++] & 0xFF)
                                << (24 - this.bitCount);
                        this.bitCount += 8;
                    }
                    this.bitBuffer |= (this.cache[this.cachePosition++] & 0xFF)
                            << (24 - this.bitCount);
                    this.bitCount += 8;
                }
                this.bitBuffer |= (this.cache[this.cachePosition++] & 0xFF)
                        << (24 - this.bitCount);
                this.bitCount += 8;
            }
        } else if (this.bitCount < 25) {
            if (this.bitCount == 0) {
                this.bitBuffer = 0;
            }

            int count = Math.min((32 - this.bitCount) >> 3,
                    this.cacheLimit - this.cachePosition);
            while (0 < count--) {
                this.bitBuffer |= (this.cache[this.cachePosition++] & 0xFF)
                        << (24 - this.bitCount);
                this.bitCount += 8;
            }
            this.fillCache();                                                   //throws IOException
            if (this.cachePosition < this.cacheLimit) {
                count = Math.min((32 - this.bitCount) >> 3,
                        this.cacheLimit - this.cachePosition);
                while (0 < count--) {
                    this.bitBuffer |= (this.cache[this.cachePosition++] & 0xFF)
                            << (24 - this.bitCount);
                    this.bitCount += 8;
                }
            } else if (this.bitCount <= 0) {
                throw new LocalEOFException(this);
            }
        }
    }

    /**
     * cache ����ɂȂ������� cache �Ƀf�[�^��ǂݍ��ށB
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    private void fillCache() throws IOException {
        this.markPositionIsInCache = false;
        this.cacheLimit = 0;
        this.cachePosition = 0;

        //cache �Ƀf�[�^��ǂݍ���
        int read = 0;
        while (0 <= read && this.cacheLimit < this.cache.length) {
            read = this.in.read(this.cache,
                    this.cacheLimit,
                    this.cache.length - this.cacheLimit);         //throws IOException

            if (0 < read) this.cacheLimit += read;
        }
    }


    //------------------------------------------------------------------
    //  inner classes
    //------------------------------------------------------------------
    //  private static class LocalEOFException
    //------------------------------------------------------------------

    /**
     * BitInputStream ���� EndOfStream �̌��o��
     * EOFException ���g�p����̂͏��X��肪����̂�
     * ���[�J���� EOFException ���`����B
     */
    private static class LocalEOFException extends EOFException {


        //------------------------------------------------------------------
        //  instance field
        //------------------------------------------------------------------
        //  private Object owner
        //------------------------------------------------------------------
        /**
         * ���̗�O�𓊂����I�u�W�F�N�g
         */
        private Object owner;

        //------------------------------------------------------------------
        //  constructer
        //------------------------------------------------------------------
        //  public LocalEOFException( Object object )
        //------------------------------------------------------------------

        /**
         * �R���X�g���N�^�B
         *
         * @param object ���̗�O�𓊂����I�u�W�F�N�g
         */
        public LocalEOFException(Object object) {
            super();
            this.owner = object;
        }

        //------------------------------------------------------------------
        //  access method
        //------------------------------------------------------------------
        //  public boolean thrownBy( Object object )
        //------------------------------------------------------------------

        /**
         * ���̗�O�� object �ɂ���ē�����ꂽ���ǂ����𓾂�B<br>
         *
         * @param object �I�u�W�F�N�g
         *
         * @return ���̗�O�� object�ɂ����
         *         ������ꂽ��O�ł���� true<br>
         *         �Ⴆ�� false<br>
         */
        public boolean thrownBy(Object object) {
            return this.owner == object;
        }
    }

}
//end of BitInputStream.java
