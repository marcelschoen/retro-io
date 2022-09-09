//start of LhaInputStream.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * LhaInputStream.java
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

package jp.gr.java_conf.dangan.util.lha;

//import classes and interfaces

import jp.gr.java_conf.dangan.io.DisconnectableInputStream;
import jp.gr.java_conf.dangan.io.LimitedInputStream;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

//import exceptions


/**
 * �ڑ����ꂽ�X�g���[������LHA���Ƀf�[�^��ǂ݂��݁A
 * �G���g�����𓀂��ǂݍ��ނ��߂̃��[�e�B���e�B�N���X�B<br>
 * java.util.zip.ZipInputStream �Ǝ����C���^�[�t�F�C�X�����悤�ɍ�����B<br>
 * ��ꂽ���ɂ̏����Ɋւ��Ă͉�ꂽ�G���g���ȍ~��
 * ���Ă��Ȃ��G���g��������ɓǂ݂��߂Ȃ��\��������B<br>
 *
 * <pre>
 * -- revision history --
 * $Log: LhaInputStream.java,v $
 * Revision 1.1.2.1  2003/07/20 13:22:31  dangan
 * [bug fix]
 *     getNextEntry() �� CompressMethod.connectDecoder �� 
 *     this.limit ��n���ׂ��Ƃ���� this.in ��n���Ă����B
 *
 * Revision 1.1  2002/12/08 00:00:00  dangan
 * [maintenance]
 *     LhaConstants ���� CompressMethod �ւ̃N���X���̕ύX�ɍ��킹�ďC���B
 *
 * Revision 1.0  2002/08/05 00:00:00  dangan
 * add to version control
 * [change]
 *     �R���X�g���N�^���� ������ String encode �������̂�p�~�A
 *     Properties �������Ɏ����̂�ǉ��B
 *     ���ɏI�[�ɒB�����ꍇ�͂���ȏ�ǂݍ��߂Ȃ��悤�ɏC���B
 *     available() �̐U�镑���� java.util.zip.ZipInputStream �Ɠ����悤��
 *     �G���g���̏I�[�ɒB���Ă��Ȃ��ꍇ�� 1 �G���g���̏I�[�ɒB�����ꍇ�� 0 ��Ԃ��悤�ɕύX�B
 * [maintenance]
 *     �\�[�X����
 *     �^�u�p�~
 *     ���C�Z���X���̏C��
 *
 * </pre>
 *
 * @author $Author: dangan $
 * @version $Revision: 1.1.2.1 $
 */
public class LhaInputStream extends InputStream {


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  source
    //------------------------------------------------------------------
    //  private InputStream source
    //  private boolean alreadyOpenedFirstEnrty
    //  private boolean reachedEndOfArchive
    //------------------------------------------------------------------
    /**
     * LHA���Ɍ`���̃f�[�^����������InputStream�B
     */
    private InputStream source;

    /**
     * ���ɍŏ��̃G���g����ǂݍ���ł��邩�������B
     */
    private boolean alreadyOpenedFirstEnrty;

    /**
     * ���ɏI�[�ɒB�������������B
     */
    private boolean reachedEndOfArchive;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  for taking out a file from the archive
    //------------------------------------------------------------------
    //  private InputStream in
    //  private LimitedInputStream limit
    //  private boolean reachedEndOfEntry
    //------------------------------------------------------------------
    /**
     * LHA���ɓ��̂P�G���g���̉𓀂��ꂽ�f�[�^
     * ���������� InputStream�B
     */
    private InputStream in;

    /**
     * LHA���ɓ��̂P�G���g���̈��k���ꂽ�f�[�^
     * ����������LimitedInputStream�B
     * closeEntry ���ɃX�L�b�v���邽�߁B
     */
    private LimitedInputStream limit;

    /**
     * ���ݏ������̃G���g���̏I�[�ɒB�������� true �ɃZ�b�g�����B
     */
    private boolean reachedEndOfEntry;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  backup for mark/reset
    //------------------------------------------------------------------
    //  private boolean markReachedEndOfEntry
    //------------------------------------------------------------------
    /** reachEndOfEntry �̃o�b�N�A�b�v�p */
    private boolean markReachedEndOfEntry;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  properties
    //------------------------------------------------------------------
    //  private Properties property
    //------------------------------------------------------------------
    /**
     * �e���k�`���ɑΉ�����������̐����������܂܂��v���p�e�B
     */
    private Properties property;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private LhaInputStream()
    //  public LhaInputStream( InputStream in )
    //  public LhaInputStream( InputStream in, Properties property )
    //  private void constructerHelper( InputStream in, Properties property )
    //------------------------------------------------------------------

    /**
     * �f�t�H���g�R���X�g���N�^�B
     * �g�p�s�B
     */
    private LhaInputStream() {
    }

    /**
     * in ���� LHA���ɂ̃f�[�^��ǂݎ�� InputStream ���\�z����B<br>
     * �e���k�`���ɑΉ�����������̐������������v���p�e�B�ɂ�
     * LhaProperty.getProperties() �œ���ꂽ�v���p�e�B���g�p�����B<br>
     *
     * @param in LHA���Ɍ`���̃f�[�^������������̓X�g���[��
     *
     * @see LhaProperty#getProperties()
     */
    public LhaInputStream(InputStream in) {
        Properties property = LhaProperty.getProperties();

        try {
            this.constructerHelper(in, property);                             //After Java 1.1 throws UnsupportedEncodingException
        } catch (UnsupportedEncodingException exception) {
            throw new Error("Unsupported encoding \"" + property.getProperty("lha.encoding") + "\".");
        }
    }

    /**
     * in ���� LHA���ɂ̃f�[�^��ǂݎ�� InputStream���\�z����B<br>
     *
     * @param in       LHA���Ɍ`���̃f�[�^������������̓X�g���[��
     * @param property �e���k�`���ɑΉ�����������̐����������܂܂��v���p�e�B
     *
     * @exception UnsupportedEncodingException
     *                 property.getProperty( "lha.encoding" ) �œ���ꂽ
     *                 �G���R�[�f�B���O�����T�|�[�g����Ȃ��ꍇ
     */
    public LhaInputStream(InputStream in, Properties property)
            throws UnsupportedEncodingException {

        this.constructerHelper(in, property);                                 //After Java 1.1 throws UnsupportedEncodingException
    }

    /**
     * �R���X�g���N�^�̏�����������S�����郁�\�b�h�B
     *
     * @param in       LHA���Ɍ`���̃f�[�^������������̓X�g���[��
     * @param property �e���k�`���ɑΉ�����������̐����������܂܂��v���p�e�B
     *
     * @exception UnsupportedEncodingException
     *               encode ���T�|�[�g����Ȃ��ꍇ
     */
    private void constructerHelper(InputStream in, Properties property)
            throws UnsupportedEncodingException {

        if (in != null && property != null) {
            String encoding = property.getProperty("lha.encoding");
            if (encoding == null) {
                encoding = LhaProperty.getProperty("lha.encoding");
            }

            //encoding���`�F�b�N
            encoding.getBytes(encoding);                                      //After Java 1.1 throws UnsupportedEncodingException

            if (in.markSupported()) {
                this.source = in;
            } else {
                this.source = new BufferedInputStream(in);
            }

            this.in = null;
            this.limit = null;
            this.property = (Properties) property.clone();
            this.reachedEndOfEntry = false;
            this.reachedEndOfArchive = false;

        } else if (in == null) {
            throw new NullPointerException("in");
        } else {
            throw new NullPointerException("property");
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
     * ���݂̃G���g������ 1�o�C�g�̃f�[�^��ǂݍ��ށB
     *
     * @return �ǂ݂��܂ꂽ 1�o�C�g�̃f�[�^�B<br>
     *         ���ɃG���g���̏I�[�ɒB�����ꍇ�� -1
     *
     * @exception IOException ���ݓǂݍ��ݒ��̃G���g����������
     *                        ���o�̓G���[�����������ꍇ
     */
    public int read() throws IOException {
        if (this.in != null) {
            int ret = this.in.read();                                           //throws IOException
            if (ret < 0) {
                this.reachedEndOfEntry = true;
            }
            return ret;
        } else {
            throw new IOException("no entry");
        }
    }

    /**
     * ���݂̃G���g������ buffer �𖞂����悤�Ƀf�[�^��ǂݍ��ށB
     *
     * @param buffer �f�[�^��ǂݍ��ރo�b�t�@
     *
     * @return �ǂ݂��܂ꂽ�f�[�^�̗ʁB<br>
     *         ���ɃG���g���̏I�[�ɒB�����ꍇ�� -1�B
     *
     * @exception IOException ���ݓǂݍ��ݒ��̃G���g����������
     *                        ���o�̓G���[�����������ꍇ
     */
    public int read(byte[] buffer) throws IOException {
        return this.read(buffer, 0, buffer.length);                           //throws IOException
    }

    /**
     * ���݂̃G���g������ buffer ��index�� length�o�C�g��
     * �f�[�^����ǂݍ��ށB
     *
     * @param buffer �f�[�^��ǂݍ��ރo�b�t�@
     * @param index  buffer���̃f�[�^�ǂݍ��݊J�n�ʒu
     * @param length �ǂݍ��ރf�[�^��
     *
     * @return �ǂ݂��܂ꂽ�f�[�^�̗ʁB<br>
     *         ���ɃG���g���̏I�[�ɒB�����ꍇ�� -1�B
     *
     * @exception IOException ���ݓǂݍ��ݒ��̃G���g����������
     *                        ���o�̓G���[�����������ꍇ
     */
    public int read(byte[] buffer, int index, int length) throws IOException {
        if (this.in != null) {
            int ret = this.in.read(buffer, index, length);                    //throws IOException
            if (ret < 0) {
                this.reachedEndOfEntry = true;
            }
            return ret;
        } else {
            throw new IOException("no entry");
        }
    }

    /**
     * ���݂̃G���g���̃f�[�^�� length �o�C�g�ǂ݂Ƃ΂��B
     *
     * @param length �ǂ݂Ƃ΂��f�[�^��
     *
     * @return ���ۂɓǂ݂Ƃ΂����f�[�^��
     *
     * @exception IOException ���ݓǂݍ��ݒ��̃G���g����������
     *                        ���o�̓G���[�����������ꍇ
     */
    public long skip(long length) throws IOException {
        if (this.in != null) {
            if (0 < length) {
                long len = this.in.skip(length - 1);                          //throws IOException
                int ret = this.in.read();                                      //throws IOException
                if (ret < 0) {
                    this.reachedEndOfEntry = true;
                    return len;
                } else {
                    return len + 1;
                }
            } else {
                return 0;
            }
        } else {
            throw new IOException("no entry");
        }
    }


    //------------------------------------------------------------------
    //  method of java.io.InputStream
    //------------------------------------------------------------------
    //  mark/reset
    //------------------------------------------------------------------
    //  public void mark()
    //  public void reset()
    //  public boolean markSupported()
    //------------------------------------------------------------------

    /**
     * ���ݓǂݎ�蒆�̃G���g���̌��݈ʒu�Ƀ}�[�N��ݒ肵�A
     * reset() �Ń}�[�N�����ǂݍ��݈ʒu�ɖ߂��悤�ɂ���B<br>
     *
     * @param readLimit �}�[�N�ʒu�ɖ߂����E�ǂݍ��ݗʁB
     *                  ���̃o�C�g���𒴂��ăf�[�^��ǂݍ��񂾏ꍇ 
     *                  reset() �ł���ۏ؂͂Ȃ��B
     *
     * @exception IllegalStateException
     *                  ���ݓǂݍ��ݒ��̃G���g���������ꍇ
     */
    public void mark(int readLimit) {
        if (this.in != null) {
            this.in.mark(readLimit);
            this.markReachedEndOfEntry = this.reachedEndOfEntry;
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * ���ݓǂݎ�蒆�̃G���g���̓ǂݍ��݈ʒu���Ō��
     * mark() ���\�b�h���Ăяo���ꂽ�Ƃ��̈ʒu�ɐݒ肷��B
     *
     * @exception IOException ���ݓǂݍ��ݒ��̃G���g����������
     *                        ���o�̓G���[�����������ꍇ
     */
    public void reset() throws IOException {
        if (this.in != null) {
            this.in.reset();                                                    //throws IOException
            this.reachedEndOfEntry = this.markReachedEndOfEntry;
        } else {
            throw new IOException("no entry");
        }
    }

    /**
     * �ڑ����ꂽ���̓X�g���[���� mark()��
     * reset()���T�|�[�g���邩�𓾂�B<br>
     * �w�b�_�ǂݍ��ݎ��� mark/reset ���K�{�̂���
     * �R���X�g���N�^�œn���ꂽ in �� markSupported() �� 
     * false ��Ԃ��ꍇ�A���̃N���X�� in �� mark/reset ���T�|�[�g����
     * BufferedInputStream �Ń��b�v����B
     * ���̂��߁A���̃��\�b�h�͏�� true ��Ԃ��B
     *
     * @return ��� true
     */
    public boolean markSupported() {
        return this.source.markSupported();
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
     * ���ݓǂݎ�蒆�̃G���g���̏I�[�ɒB�������𓾂�B<br>
     * �u���b�N���Ȃ��œǂݍ��߂�f�[�^�ʂ�Ԃ��Ȃ����ɒ��ӂ��邱�ƁB
     *
     * @return ���ݓǂݎ�蒆�̃G���g���̏I�[�ɒB�����ꍇ 0 �B���Ă��Ȃ��ꍇ 1
     *
     * @exception IOException ���ݓǂݍ��ݒ��̃G���g����������
     *                        ���o�̓G���[�����������ꍇ
     *
     * @see java.util.zip.ZipInputStream#available()
     */
    public int available() throws IOException {
        if (this.in != null) {
            return (this.reachedEndOfEntry ? 0 : 1);
        } else {
            throw new IOException("no entry");
        }
    }

    /**
     * ���̓��̓X�g���[������A�g�p���Ă���
     * �S�Ẵ��\�[�X���J������B
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void close() throws IOException {
        if (this.in != null) {
            this.in.close();
            this.limit = null;
            this.in = null;
        }

        this.source.close();
        this.source = null;
    }


    //------------------------------------------------------------------
    //  original method  ( on the model of java.util.zip.ZipInputStream )
    //------------------------------------------------------------------
    //  manipulate entry
    //------------------------------------------------------------------
    //  public LhaHeader getNextEntry()
    //  public LhaHeader getNextEntryWithoutExtract()
    //  public void closeEntry()
    //------------------------------------------------------------------

    /**
     * ���̃G���g�����𓀂��Ȃ���ǂ݂��ނ悤�ɃX�g���[����ݒ肷��B<br>
     *
     * @return �G���g���̏������� LhaHeader
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public LhaHeader getNextEntry() throws IOException {
        if (!this.reachedEndOfArchive) {
            if (this.in != null) {
                this.closeEntry();                                                  //throws IOException
            }

            byte[] HeaderData;
            if (this.alreadyOpenedFirstEnrty) {
                HeaderData = LhaHeader.getNextHeaderData(this.source);
            } else {
                HeaderData = LhaHeader.getFirstHeaderData(this.source);
                this.alreadyOpenedFirstEnrty = true;
            }
            if (null != HeaderData) {
                LhaHeader header = LhaHeader.createInstance(HeaderData, this.property);
                this.in = new DisconnectableInputStream(this.source);
                this.limit = new LimitedInputStream(this.in, header.getCompressedSize());
                this.in = CompressMethod.connectDecoder(this.limit,
                        header.getCompressMethod(),
                        this.property,
                        header.getOriginalSize());

                this.reachedEndOfEntry = false;
                this.markReachedEndOfEntry = false;
                return header;
            } else {
                this.reachedEndOfArchive = true;
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * ���̃G���g�����𓀂��Ȃ��œǂ݂��ނ悤�ɃX�g���[����ݒ肷��B<br>
     *
     * @return �G���g���̏������� LhaHeader
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public LhaHeader getNextEntryWithoutExtract() throws IOException {

        if (!this.reachedEndOfArchive) {

            if (this.in != null) {
                this.closeEntry();                                                  //throws IOException
            }

            byte[] HeaderData;
            if (this.alreadyOpenedFirstEnrty) {
                HeaderData = LhaHeader.getNextHeaderData(this.source);
            } else {
                HeaderData = LhaHeader.getFirstHeaderData(this.source);
                this.alreadyOpenedFirstEnrty = true;
            }
            if (HeaderData != null) {

                LhaHeader header = LhaHeader.createInstance(HeaderData, this.property);
                this.in = new DisconnectableInputStream(this.source);
                this.limit = new LimitedInputStream(this.in, header.getCompressedSize());
                this.in = this.limit;

                this.reachedEndOfEntry = false;
                this.markReachedEndOfEntry = false;
                return header;
            } else {
                this.reachedEndOfArchive = true;
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * ���ݓǂݎ�蒆�̃G���g������A
     * ���̃G���g����ǂ݂��߂�悤�ɃX�g���[����ݒ肷��B
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void closeEntry() throws IOException {
        if (this.in != null) {
            while (0 <= this.limit.read()) {
                this.limit.skip(Long.MAX_VALUE);
            }

            this.in.close();
            this.in = null;
            this.limit = null;
        }
    }

}
//end of LhaInputStream.java
