//start of CachedInputStream.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * CachedInputStream.java
 * <p>
 * Copyright (C) 2002  Michel Ishizuka  All rights reserved.
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
 * �L���b�V�����g�p���č��������邽�߂̓��̓X�g���[���B<br> 
 * BufferedInputStream �Ƃ� read�n���\�b�h�� synchronized
 * ����Ă��Ȃ����߁A���������ɂ�郍�X���Ȃ��Amark/reset ��
 * �L���b�V�����̓ǂݍ��݈ʒu�̈ړ��ōs����Ƃ��̂݃T�|�[�g�ł���A
 * ����ȏ�͐ڑ����ꂽ���̓X�g���[���̐��\�ɂ��A���̈Ⴂ������B
 *
 * <pre>
 * -- revision history --
 * $Log: CachedInputStream.java,v $
 * Revision 1.3  2002/12/05 00:00:00  dangan
 * [maintenance]
 *     �\�[�X����
 *
 * Revision 1.2  2002/11/02 00:00:00  dangan
 * [bug fix]
 *     available() �Ńu���b�N�����ɓǂݍ��߂�ʂ����傫���l��Ԃ��Ă����B
 *
 * Revision 1.1  2002/09/05 00:00:00  dangan
 * [change]
 *     EndOfStream �ɒB������� read( new byte[0] ) �� 
 *     read( byte[] buf, int off, 0 ) �̖߂�l��
 *     InputStream �Ɠ����� 0 �ɂȂ�悤�ɂ����B
 *
 * Revision 1.0  2002/09/05 00:00:00  dangan
 * add to version control
 * [bug fix]
 *     mark() �� �ڑ����ꂽ in �ɓn�� readLimit �̌v�Z���Â��������߁A
 *     �v�����ꂽ readLimit �ɒB����O�Ƀ}�[�N�ʒu���j������鎖���������B
 *     read( buf, off, len ) ���� System.arraycopy �̌Ăяo���� 
 *     dst �� src ���t�ɂ��Ă����B
 * [change]
 *     EndOfStream �ɒB������� read( new byte[0] ) ��
 *     read( buf, off�C0 )  �� -1 ��Ԃ��悤�ɏC���B
 * [maintenance]
 *     �^�u�p�~
 *     ���C�Z���X���̏C��
 *
 * </pre>
 *
 * @author $Author: dangan $
 * @version $Revision: 1.3 $
 */
public class CachedInputStream extends InputStream {

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
     * �f�[�^������������̓X�g���[��
     */
    private InputStream in;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  cache
    //------------------------------------------------------------------
    //  private byte[] cache
    //  private int cachePosition
    //  private int cacheLimit
    //------------------------------------------------------------------
    /**
     * �f�[�^��~���邽�߂̃L���b�V��
     */
    private byte[] cache;

    /**
     * cache���̌��ݏ����ʒu
     */
    private int cachePosition;

    /**
     * cache�̓ǂݍ��݌��E�ʒu
     */
    private int cacheLimit;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  backup for mark/reset
    //------------------------------------------------------------------
    //  private boolean markPositionIsInCache
    //  private byte[] markCache
    //  private int markCachePosition
    //  private int markCacheLimit
    //------------------------------------------------------------------
    /**
     * mark�ʒu���L���b�V���͈͓̔��ɂ��邩�������B
     * mark���ꂽ�Ƃ� true �ɐݒ肳��A
     * ���� in ���� �L���b�V���ւ̓ǂݍ��݂�
     * �s��ꂽ�Ƃ��� false �ɐݒ肳���B
     */
    private boolean markPositionIsInCache;

    /** cache�̃o�b�N�A�b�v�p */
    private byte[] markCache;

    /** cachePosition�̃o�b�N�A�b�v�p */
    private int markCachePosition;

    /** cacheLimit�̃o�b�N�A�b�v�p */
    private int markCacheLimit;


    //------------------------------------------------------------------
    //  constructer
    //------------------------------------------------------------------
    //  private CachedInputStream()
    //  public CachedInputStream( InputStream in )
    //  public CachedInputStream( InputStream in, int cacheSize )
    //------------------------------------------------------------------

    /**
     * �f�t�H���g�R���X�g���N�^�B
     * �g�p�s�B
     */
    private CachedInputStream() {
    }

    /**
     * �f�t�H���g�̃T�C�Y�̃L���b�V��������
     * CachedInputStream���\�z����B
     *
     * @param in �L���b�V�����K�v�ȓ��̓X�g���[��
     *
     * @exception IllegalArgumentException
     *                     in �� null �������ꍇ
     */
    public CachedInputStream(InputStream in) {
        this(in, CachedInputStream.DefaultCacheSize);
    }

    /**
     * �w�肳�ꂽ�T�C�Y�̃L���b�V��������
     * CachedInputStream���\�z����B
     *
     * @param in        �L���b�V�����K�v�ȓ��̓X�g���[��
     * @param cacheSize �L���b�V���̃T�C�Y
     *
     * @exception IllegalArgumentException
     *                     cacheSize �� 0�ȉ��ł��邩�A
     *                     in �� null �������ꍇ
     */
    public CachedInputStream(InputStream in, int cacheSize) {
        if (in != null && 0 < cacheSize) {
            this.in = in;

            this.cache = new byte[cacheSize];
            this.cachePosition = 0;
            this.cacheLimit = 0;

            this.markPositionIsInCache = false;
            this.markCache = null;
            this.markCachePosition = 0;
            this.markCacheLimit = 0;

        } else if (in == null) {
            throw new IllegalArgumentException("in must not be null.");
        } else {
            throw new IllegalArgumentException("cacheSize must be one or more.");
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
     * �ڑ����ꂽ�X�g���[������ 1�o�C�g�̃f�[�^��
     * 0�`255 �Ƀ}�b�v���ēǂݍ��ށB
     *
     * @return �ǂݏo���ꂽ 1�o�C�g�̃f�[�^��Ԃ��B<br>
     *         ���� EndOfStream�ɒB���Ă����ꍇ�� -1��Ԃ��B<br>
     *
     * @exception IOException �ڑ����ꂽ���̓X�g���[����
     *                        ���o�̓G���[�����������ꍇ
     */
    public int read() throws IOException {
        if (this.cachePosition < this.cacheLimit) {
            return this.cache[this.cachePosition++] & 0xFF;
        } else {
            this.fillCache();                                                     //throws IOException

            if (this.cachePosition < this.cacheLimit) {
                return this.cache[this.cachePosition++] & 0xFF;
            } else {
                return -1;
            }
        }
    }

    /**
     * �ڑ����ꂽ�X�g���[������ buffer�𖞂����悤��
     * �f�[�^��ǂݍ��ށB<br>
     * ���̃��\�b�h�� buffer �𖞂����܂Ńf�[�^��ǂݍ��ނ��A
     * EndOfStream�ɓ��B����܂Ńu���b�N����B<br>
     *
     * @param buffer �ǂݍ��񂾃f�[�^���i�[���邽�߂̃o�C�g�z��
     *
     * @return buffer �ɓǂݍ��񂾃f�[�^�ʂ��o�C�g���ŕԂ��B<br>
     *         ���� EndOfStream�ɒB���Ă����ꍇ�� -1��Ԃ��B<br>
     *
     * @exception IOException �ڑ����ꂽ���̓X�g���[����
     *                        ���o�̓G���[�����������ꍇ
     */
    public int read(byte[] buffer) throws IOException {
        return this.read(buffer, 0, buffer.length);
    }

    /**
     * �ڑ����ꂽ�X�g���[������ buffer �� index �Ŏw�肳�ꂽ
     * �ʒu�� length �o�C�g�f�[�^��ǂݍ��ށB<br>
     * ���̃��\�b�h�� length �o�C�g�ǂݍ��ނ��A
     * EndOfStream�ɓ��B����܂Ńu���b�N����B<br>
     *
     * @param buffer �ǂݍ��񂾃f�[�^���i�[���邽�߂̃o�C�g�z��
     * @param index  buffer���̃f�[�^�ǂݍ��݊J�n�ʒu
     * @param length buffer�ɓǂݍ��ރf�[�^��
     *
     * @return buffer �ɓǂݍ��񂾃f�[�^�ʂ��o�C�g���ŕԂ��B<br>
     *         ���� EndOfStream�ɒB���Ă����ꍇ�� -1��Ԃ��B<br>
     *
     * @exception IOException �ڑ����ꂽ���̓X�g���[����
     *                        ���o�̓G���[�����������ꍇ
     */
    public int read(byte[] buffer, int index, int length)
            throws IOException {
        final int requested = length;

        while (0 < length) {
            if (this.cacheLimit <= this.cachePosition) {
                this.fillCache();                                             //throws IOException
                if (this.cacheLimit <= this.cachePosition) {
                    if (requested == length) {
                        return -1;
                    } else {
                        break;
                    }
                }
            }

            int copylen = Math.min(length,
                    this.cacheLimit - this.cachePosition);
            System.arraycopy(this.cache, this.cachePosition,
                    buffer, index, copylen);

            index += copylen;
            length -= copylen;
            this.cachePosition += copylen;
        }

        return requested - length;
    }

    /**
     * �ڑ����ꂽ���̓X�g���[���̃f�[�^�� length �o�C�g�ǂݔ�΂��B<br>
     * ���̃��\�b�h�� length �o�C�g�ǂݔ�΂���
     * EndOfStream �ɓ��B����܂Ńu���b�N����B<br>
     *
     * @param length �ǂݔ�΂��o�C�g���B<br>
     *
     * @return ���ۂɓǂݔ�΂��ꂽ�o�C�g���B<br>
     *
     * @exception IOException �ڑ����ꂽ���̓X�g���[����
     *                        ���o�̓G���[�����������ꍇ
     */
    public long skip(long length) throws IOException {
        final long requested = length;

        while (0 < length) {
            if (this.cacheLimit <= this.cachePosition) {
                this.fillCache();                                             //throws IOException

                if (this.cacheLimit <= this.cachePosition) {
                    break;
                }
            }

            long skiplen = Math.min(length, (long) (this.cacheLimit - this.cachePosition));

            length -= skiplen;
            this.cachePosition += (int) skiplen;
        }

        return requested - length;
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
        readLimit = (readLimit / this.cache.length) * this.cache.length
                + (readLimit % this.cache.length == 0 ? 0 : this.cache.length);


        this.in.mark(readLimit);

        if (this.markCache == null) {
            this.markCache = (byte[]) this.cache.clone();
        } else {
            System.arraycopy(this.cache, 0, this.markCache, 0, this.cacheLimit);
        }

        this.markCacheLimit = this.cacheLimit;
        this.markCachePosition = this.cachePosition;
        this.markPositionIsInCache = true;
    }

    /**
     * �ڑ����ꂽ���̓X�g���[���̓ǂݍ��݈ʒu���Ō��
     * mark() ���\�b�h���Ăяo���ꂽ�Ƃ��̈ʒu�ɐݒ肷��B<br>
     *
     * @exception IOException <br>
     *              (1) CachedInputStream �� mark ���Ȃ���Ă��Ȃ��ꍇ�B<br>
     *              (2) �ڑ����ꂽ���̓X�g���[���� markSupported()��
     *                  false ��Ԃ��ꍇ�B<br>
     *              (3) �ڑ����ꂽ���̓X�g���[����
     *                  ���o�̓G���[�����������ꍇ�B<br>
     *              �̉��ꂩ�B
     */
    public void reset() throws IOException {
        if (this.markPositionIsInCache) {
            this.cachePosition = this.markCachePosition;
        } else if (!this.in.markSupported()) {
            throw new IOException("not support mark()/reset().");
        } else if (this.markCache == null) { //���̏������͖����Ƀ}�[�N����Ă��Ȃ����Ƃ������B�R���X�g���N�^�� markCache �� null �ɐݒ肳���̂𗘗p����B
            throw new IOException("not marked.");
        } else {
            //in �� reset() �ł��Ȃ��ꍇ��
            //�ŏ��̍s�� this.in.reset() ��
            //IOException �𓊂��邱�Ƃ����҂��Ă���B
            this.in.reset();                                                    //throws IOException
            System.arraycopy(this.markCache, 0, this.cache, 0, this.markCacheLimit);
            this.cacheLimit = this.markCacheLimit;
            this.cachePosition = this.markCachePosition;
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
        return this.cacheLimit - this.cachePosition
                + (this.in.available() / this.cache.length) * this.cache.length;//throws IOException
    }

    /**
     * ���̓��̓X�g���[������A�g�p���Ă���
     * �S�Ẵ��\�[�X���J������B<br>
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

        this.markCache = null;
        this.markCacheLimit = 0;
        this.markCachePosition = 0;
        this.markPositionIsInCache = false;
    }


    //------------------------------------------------------------------
    //  local methods
    //------------------------------------------------------------------
    //  private void fillCache()
    //------------------------------------------------------------------

    /**
     * �K�v������ꍇ�ɁA�L���b�V���p�o�b�t�@�Ƀf�[�^��
     * ��U���L���b�V���p�o�b�t�@�ɕK���f�[�^�����݂���
     * ���Ƃ�ۏ؂��邽�߂ɌĂ΂��B<br>
     * ���� EndOfStream �܂œǂݍ��܂�Ă���ꍇ�� �f�[�^��
     * ��U����Ȃ����Ƃɂ���� ����������B
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    private void fillCache() throws IOException {
        this.markPositionIsInCache = false;
        this.cacheLimit = 0;
        this.cachePosition = 0;

        //�L���b�V���Ƀf�[�^��ǂݍ���
        int read = 0;
        while (0 <= read && this.cacheLimit < this.cache.length) {
            read = this.in.read(this.cache,
                    this.cacheLimit,
                    this.cache.length - this.cacheLimit);         //throws IOException

            if (0 < read) this.cacheLimit += read;
        }
    }


}
//end of CachedInputStream.java
