//start of LhaRetainedOutputStream.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * LhaRetainedOutputStream.java
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

import java.io.*;
import java.util.Properties;
import java.util.Vector;

//import exceptions

/**
 * �ڑ����ꂽRandomAccessFile�� ���k�f�[�^���o�͂��邽�߂̃��[�e�B���e�B�N���X�B<br>
 * java.util.zip.ZipOutputStream �Ǝ����C���^�[�t�F�C�X�����悤�ɍ�����B<br>
 * ���k���s��( ���k��T�C�Y�����k�O�T�C�Y���������ꍇ )�̏����������I�ɍs���B
 * �i���񍐂���������ꍇ�A���̂悤�ȏ������N���X���ɉB������Ɛi���񍐂͉��b�Ԃ�
 * ���ɂ���Ă͉��\�����������Ȃ��Ȃ�B(�Ⴆ�΃M�K�o�C�g���̃f�[�^���������ꍇ)
 * ���̂悤�Ȏ��Ԃ���������ꍇ�� LhaImmediateOutputStream���g�p���邱�ƁB<br>
 * �܂��AJDK 1.1 �ȑO�ł� RandomAccessFile �� setLength �������Ȃ����߁A
 * ���Ƀf�[�^�̌��ɑ��̃f�[�^������ꍇ�ł��t�@�C���T�C�Y��؂�l�߂邱�Ƃ��o���Ȃ��B
 * ���̖��_�͏�ɃT�C�Y0�̐V�����t�@�C�����J�����ɂ���ĉ�����鎖���ł���B<br>
 *
 * <pre>
 * -- revision history --
 * $Log: LhaRetainedOutputStream.java,v $
 * Revision 1.2  2002/12/11 02:25:14  dangan
 * [bug fix]
 *     jdk1.2 �ŃR���p�C���ł��Ȃ������ӏ����C���B
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
 * [maintenance]
 *     �\�[�X����
 *     �^�u�p�~
 *     ���C�Z���X���̏C��
 *
 * </pre>
 *
 * @author $Author: dangan $
 * @version $Revision: 1.2 $
 */
public class LhaRetainedOutputStream extends OutputStream {


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  sink
    //------------------------------------------------------------------
    //  private RandomAccessFile archive
    //------------------------------------------------------------------
    /**
     * ���Ƀt�@�C��
     */
    private RandomAccessFile archive;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  to compress a file
    //------------------------------------------------------------------
    //  private OutputStream out
    //  private RandomAccessFileOutputStream rafo
    //  private LhaHeader header
    //  private String encoding
    //  private long headerpos
    //  private CRC16 crc
    //------------------------------------------------------------------
    /**
     * ���k�p�o�̓X�g���[��
     */
    private OutputStream out;

    /**
     * ���k�p�o�̓X�g���[��
     */
    private RandomAccessFileOutputStream rafo;

    /**
     * ���݈��k���̃w�b�_
     */
    private LhaHeader header;

    /**
     * �w�b�_�̏o�͂Ɏg�p�����G���R�[�f�B���O
     */
    private String encoding;

    /**
     * �w�b�_�ʒu
     */
    private long headerpos;

    /**
     * CRC�l�Z�o�p
     */
    private CRC16 crc;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  property
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
    //  private LhaRetainedOutputStream()
    //  public LhaRetainedOutputStream( String filename )
    //  public LhaRetainedOutputStream( String filename, Properties property )
    //  public LhaRetainedOutputStream( File file )
    //  public LhaRetainedOutputStream( File file, Properties property )
    //  public LhaRetainedOutputStream( RandomAccessFile archive )
    //  public LhaRetainedOutputStream( RandomAccessFile archive, Properties property )
    //  private void constructerHelper( RandomAccesFile archive, Properties property )
    //------------------------------------------------------------------

    /**
     * �f�t�H���g�R���X�g���N�^
     * �g�p�s��
     */
    private LhaRetainedOutputStream() {
    }

    /**
     * filename �̃t�@�C���� ���k�f�[�^���o�͂���OutputStream���\�z����B<br>
     * �e���k�`���ɑΉ�����������̐������������v���p�e�B�ɂ�
     * LhaProperty.getProperties() �œ���ꂽ�v���p�e�B���g�p�����B<br>
     *
     * @param filename ���k�f�[�^���������ރt�@�C���̖��O
     *
     * @exception FileNotFoundException
     *               filename �ŗ^����ꂽ�t�@�C����������Ȃ��ꍇ�B
     * @exception SecurityException
     *               �Z�L�����e�B�}�l�[�W�����t�@�C���ւ̃A�N�Z�X�������Ȃ��ꍇ�B
     *
     * @see LhaProperty#getProperties()
     */
    public LhaRetainedOutputStream(String filename)
            throws FileNotFoundException {

        if (filename != null) {
            RandomAccessFile file = new RandomAccessFile(filename, "rw");     //throws FileNotFoundException, SecurityException
            Properties property = LhaProperty.getProperties();

            this.constructerHelper(file, property);
        } else {
            throw new NullPointerException("filename");
        }
    }

    /**
     * filename �̃t�@�C���� ���k�f�[�^���o�͂���OutputStream���\�z����B<br>
     *
     * @param filename ���k�f�[�^���������ރt�@�C���̖��O
     * @param property �e���k�`���ɑΉ�����������̐����������܂܂��v���p�e�B
     *
     * @exception FileNotFoundException
     *               filename �ŗ^����ꂽ�t�@�C����������Ȃ��ꍇ�B
     * @exception SecurityException
     *               �Z�L�����e�B�}�l�[�W�����t�@�C���ւ̃A�N�Z�X�������Ȃ��ꍇ�B
     *
     * @see LhaProperty
     */
    public LhaRetainedOutputStream(String filename, Properties property)
            throws FileNotFoundException {

        if (filename != null) {
            RandomAccessFile file = new RandomAccessFile(filename, "rw");     //throws FileNotFoundException, SecurityException
            this.constructerHelper(file, property);
        } else {
            throw new NullPointerException("filename");
        }
    }

    /**
     * filename �̃t�@�C���� ���k�f�[�^���o�͂���OutputStream���\�z����B<br>
     * �e���k�`���ɑΉ�����������̐������������v���p�e�B�ɂ�
     * LhaProperty.getProperties() �œ���ꂽ�v���p�e�B���g�p�����B<br>
     *
     * @param filename ���k�f�[�^���������ރt�@�C���̖��O
     *
     * @exception FileNotFoundException
     *               filename �ŗ^����ꂽ�t�@�C����������Ȃ��ꍇ�B
     * @exception SecurityException
     *               �Z�L�����e�B�}�l�[�W�����t�@�C���ւ̃A�N�Z�X�������Ȃ��ꍇ�B
     * @exception IOException
     *               JDK1.2 �ŃR���p�C�����邽�߂����ɑ��݂���B
     *
     * @see LhaProperty#getProperties()
     */
    public LhaRetainedOutputStream(File filename) throws IOException {

        if (filename != null) {
            RandomAccessFile file = new RandomAccessFile(filename, "rw");     //throws FileNotFoundException, SecurityException
            Properties property = LhaProperty.getProperties();

            this.constructerHelper(file, property);
        } else {
            throw new NullPointerException("filename");
        }
    }

    /**
     * filename �̃t�@�C���� ���k�f�[�^���o�͂���OutputStream���\�z����B<br>
     *
     * @param filename ���k�f�[�^���������ރt�@�C���̖��O
     * @param property �e���k�`���ɑΉ�����������̐����������܂܂��v���p�e�B
     *
     * @exception FileNotFoundException
     *               filename �ŗ^����ꂽ�t�@�C����������Ȃ��ꍇ�B
     * @exception SecurityException
     *               �Z�L�����e�B�}�l�[�W�����t�@�C���ւ̃A�N�Z�X�������Ȃ��ꍇ�B
     * @exception IOException
     *               JDK1.2 �ŃR���p�C�����邽�߂����ɑ��݂���B
     *
     * @see LhaProperty
     */
    public LhaRetainedOutputStream(File filename, Properties property)
            throws IOException {

        if (filename != null) {
            RandomAccessFile file = new RandomAccessFile(filename, "rw");     //throws FileNotFoundException, SecurityException
            this.constructerHelper(file, property);
        } else {
            throw new NullPointerException("filename");
        }
    }

    /**
     * file�� ���k�f�[�^���o�͂���OutputStream���\�z����B<br>
     * �e���k�`���ɑΉ�����������̐������������v���p�e�B�ɂ�
     * LhaProperty.getProperties() �œ���ꂽ�v���p�e�B���g�p�����B<br>
     *
     * @param file RandomAccessFile �̃C���X�^���X�B<br>
     *             <ul>
     *                <li>���� close() ����Ă��Ȃ����B
     *                <li>�R���X�g���N�^�� mode �ɂ� "rw" �I�v�V�������g�p���āA
     *                    �ǂ݂��݂Ə������݂��o����悤�ɐ������ꂽ�C���X�^���X�ł��邱�ƁB
     *              </ul>
     *              �̏����𖞂������́B
     *
     * @see LhaProperty#getProperties()
     */
    public LhaRetainedOutputStream(RandomAccessFile file) {

        if (file != null) {
            Properties property = LhaProperty.getProperties();
            this.constructerHelper(file, property);
        } else {
            throw new NullPointerException("out");
        }
    }

    /**
     * file�� ���k�f�[�^���o�͂���OutputStream���\�z����B<br>
     * �e���k�`���ɑΉ�����������̐������������v���p�e�B�ɂ�
     * LhaProperty.getProperties() �œ���ꂽ�v���p�e�B���g�p�����B<br>
     *
     * @param file     RandomAccessFile �̃C���X�^���X�B<br>
     *                 <ul>
     *                   <li>���� close() ����Ă��Ȃ����B
     *                   <li>�R���X�g���N�^�� mode �ɂ� "rw" �I�v�V�������g�p���āA
     *                       �ǂ݂��݂Ə������݂��o����悤�ɐ������ꂽ�C���X�^���X�ł��邱�ƁB
     *                 </ul>
     *                 �̏����𖞂������́B
     * @param property �e���k�`���ɑΉ�����������̐����������܂܂��v���p�e�B
     *
     * @see LhaProperty
     */
    public LhaRetainedOutputStream(RandomAccessFile file,
                                   Properties property) {

        if (file != null
                && property != null) {

            this.constructerHelper(file, property);                           //throws UnsupportedEncodingException

        } else if (file == null) {
            throw new NullPointerException("null");
        } else {
            throw new NullPointerException("property");
        }

    }

    /**
     * �R���X�g���N�^�̏�����������S�����郁�\�b�h�B
     *
     * @param file     RandomAccessFile �̃C���X�^���X�B<br>
     *                 <ul>
     *                   <li>���� close() ����Ă��Ȃ����B
     *                   <li>�R���X�g���N�^�� mode �ɂ� "rw" �I�v�V�������g�p���āA
     *                       �ǂ݂��݂Ə������݂��o����悤�ɐ������ꂽ�C���X�^���X�ł��邱�ƁB
     *                 </ul>
     *                 �̏����𖞂������́B
     * @param property �e���k�`���ɑΉ�����������̐����������܂܂��v���p�e�B
     */
    private void constructerHelper(RandomAccessFile file,
                                   Properties property) {

        this.archive = file;

        this.out = null;
        this.header = null;
        this.headerpos = -1;
        this.crc = new CRC16();
        this.property = property;
    }

    //------------------------------------------------------------------
    //  method of java.io.OutputStream
    //------------------------------------------------------------------
    //  write
    //------------------------------------------------------------------
    //  public void write( int data )
    //  public void write( byte[] buffer )
    //  public void write( byte[] buffer, int index, int length )
    //------------------------------------------------------------------

    /**
     * ���݂̃G���g����1�o�C�g�̃f�[�^���������ށB
     *
     * @param data �������ރf�[�^
     *
     * @exception IOException ���o�̓G���[�����������ꍇ�B
     */
    public void write(int data) throws IOException {
        if (this.out != null) {
            if (this.header != null) {
                crc.update(data);
            }

            this.out.write(data);
        } else {
            throw new IOException("no entry");
        }
    }

    /**
     * ���݂̃G���g���� buffer�̓��e��S�ď����o���B
     *
     * @param buffer �����o���f�[�^�̓������o�C�g�z��
     *
     * @exception IOException ���o�̓G���[�����������ꍇ�B
     */
    public void write(byte[] buffer) throws IOException {
        this.write(buffer, 0, buffer.length);
    }

    /**
     * ���݂̃G���g���� buffer�� index����
     * length�o�C�g�̃f�[�^�������o���B
     *
     * @param buffer �����o���f�[�^�̓������o�C�g�z��
     * @param index  buffer���̏����o���ׂ��f�[�^�̊J�n�ʒu
     * @param length �f�[�^�̃o�C�g��
     *
     * @exception IOException ���o�̓G���[�����������ꍇ�B
     */
    public void write(byte[] buffer, int index, int length) throws IOException {
        if (this.out != null) {
            if (this.header != null) {
                crc.update(buffer, index, length);
            }

            this.out.write(buffer, index, length);
        } else {
            throw new IOException("no entry");
        }
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
     * ���ݏ������ݒ��̃G���g���̃f�[�^�������I�ɏo�͐�ɏ����o���B
     * ����� PostLzssEncoder, LzssOutputStream �̋K��ǂ���
     * flush() ���Ȃ������ꍇ�Ƃ͕ʂ̃f�[�^���o�͂���B
     * (���̏ꍇ�� �P�Ɉ��k�����ቺ���邾���ł���B)
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     *
     * @see PostLzssEncoder#flush()
     * @see LzssOutputStream#flush()
     */
    public void flush() throws IOException {
        if (this.out != null) {
            this.out.flush();                                                   //throws IOException
        } else {
            throw new IOException("no entry");
        }
    }

    /**
     * �o�͐�ɑS�Ẵf�[�^���o�͂��A�X�g���[�������B<br>
     * �܂��A�g�p���Ă����S�Ẵ��\�[�X���������B
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void close() throws IOException {
        if (this.out != null) {
            this.closeEntry();                                                  //throws IOException
        }

        //�^�[�~�l�[�^���o��
        this.archive.write(0);                                                //throws IOException
        try {
            this.archive.setLength(this.archive.getFilePointer());            //After Java1.2 throws IOException
        } catch (NoSuchMethodError error) {
        }

        this.archive.close();                                                   //throws IOException
        this.archive = null;
        this.header = null;
        this.crc = null;
        this.property = null;
        this.rafo = null;
    }


    //------------------------------------------------------------------
    //  original method ( on the model of java.util.zip.ZipOutputStream  )
    //------------------------------------------------------------------
    //  manipulate entry
    //------------------------------------------------------------------
    //  public void putNextEntry( LhaHeader header )
    //  public void putNextEntryAlreadyCompressed( LhaHeader header )
    //  public void putNextEntryNotYetCompressed( LhaHeader header )
    //  public void closeEntry()
    //------------------------------------------------------------------

    /**
     * �V�����G���g�����������ނ悤�ɃX�g���[����ݒ肷��B<br>
     * ���̃��\�b�h�� ���Ɉ��k�ς݂̃G���g���̏ꍇ��
     * putNextEntryAlreadyCompressed(),
     * �����Ɉ��k����Ă��Ȃ��ꍇ��
     * putNextEntryNotYetCompressed() ���Ăяo���B<br>
     * ���k����Ă��邩�̔���́A
     * <ul>
     *   <li>header.getCompressedSize()<br>
     *   <li>header.getCRC()<br>
     * </ul>
     * �̂ǂꂩ��ł� LhaHeader.UNKNOWN �ł���Ζ����Ɉ��k����Ă��Ȃ��Ƃ���B<br>
     * header �ɂ͐��m�� OriginalSize ���w�肳��Ă���K�v������B<br>
     *
     * @param header �������ރG���g���ɂ��Ă̏�������
     *               LhaHeader�̃C���X�^���X�B
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     * @exception IllegalArgumentException
     *                        header.getOriginalSize() �� LhaHeader.UNKNOWN ��Ԃ��ꍇ
     */
    public void putNextEntry(LhaHeader header) throws IOException {
        if (header.getCompressedSize() == LhaHeader.UNKNOWN
                || header.getCRC() == LhaHeader.UNKNOWN) {
            this.putNextEntryNotYetCompressed(header);                        //throws IOException
        } else {
            this.putNextEntryAlreadyCompressed(header);                       //throws IOException
        }
    }

    /**
     * ���Ɉ��k�ς݂̃G���g�����������ނ悤�ɃX�g���[����ݒ肷��B<br>
     * ���k�ς݃f�[�^�����������́A�Ăяo�������ۏ؂��鎖�B
     *
     * @param header �������ރG���g���ɂ��Ă̏�������
     *               LhaHeader�̃C���X�^���X�B
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     * @exception IllegalArgumentException
     *               <ol>
     *                  <li>header.getOriginalSize() �� LhaHeader.UNKNOWN ��Ԃ��ꍇ
     *                  <li>header.getComressedSize() �� LhaHeader.UNKNOWN ��Ԃ��ꍇ
     *                  <li>header.getCRC() �� LhaHeader.UNKNOWN ��Ԃ��ꍇ
     *               </ol>
     *               �̉��ꂩ�B
     */
    public void putNextEntryAlreadyCompressed(LhaHeader header)
            throws IOException {
        if (header.getOriginalSize() != LhaHeader.UNKNOWN
                && header.getCompressedSize() != LhaHeader.UNKNOWN
                && header.getCRC() != LhaHeader.UNKNOWN) {

            if (this.out != null) {
                this.closeEntry();
            }

            this.headerpos = this.archive.getFilePointer();

            this.encoding = this.property.getProperty("lha.encoding");
            if (this.encoding == null) {
                this.encoding = LhaProperty.getProperty("lha.encoding");
            }

            this.archive.write(header.getBytes(encoding));                  //throws IOException
            this.out = new RandomAccessFileOutputStream(this.archive, header.getCompressedSize());

        } else if (header.getOriginalSize() == LhaHeader.UNKNOWN) {
            throw new IllegalArgumentException("OriginalSize must not \"LhaHeader.UNKNOWN\".");
        } else if (header.getCompressedSize() == LhaHeader.UNKNOWN) {
            throw new IllegalArgumentException("CompressedSize must not \"LhaHeader.UNKNOWN\".");
        } else {
            throw new IllegalArgumentException("CRC must not \"LhaHeader.UNKNOWN\".");
        }
    }

    /**
     * �����Ɉ��k����Ă��Ȃ��G���g�����������ނ悤�ɃX�g���[����ݒ肷��B<br>
     * header �ɂ͐��m�� OriginalSize ���w�肳��Ă���K�v������B<br>
     * header �� CompressedSize, CRC���w�肳��Ă��Ă����������B<br>
     *
     * @param header �������ރG���g���ɂ��Ă̏�������
     *               LhaHeader�̃C���X�^���X�B
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     * @exception IllegalArgumentException
     *                        header.getOriginalSize() �� LhaHeader.UNKNOWN ��Ԃ��ꍇ
     */
    public void putNextEntryNotYetCompressed(LhaHeader header)
            throws IOException {
        if (header.getOriginalSize() != LhaHeader.UNKNOWN) {
            if (this.out != null) {
                this.closeEntry();
            }

            this.crc.reset();
            this.headerpos = this.archive.getFilePointer();
            this.header = (LhaHeader) header.clone();
            this.header.setCompressedSize(0);
            this.header.setCRC(0);

            this.encoding = this.property.getProperty("lha.encoding");
            if (this.encoding == null) {
                this.encoding = LhaProperty.getProperty("lha.encoding");
            }

            this.archive.write(this.header.getBytes(encoding));
            this.rafo = new RandomAccessFileOutputStream(this.archive, header.getOriginalSize());
            this.out = CompressMethod.connectEncoder(this.rafo,
                    header.getCompressMethod(),
                    this.property);

        } else {
            throw new IllegalArgumentException("OriginalSize must not \"LhaHeader.UNKNOWN\".");
        }
    }

    /**
     * ���ݏo�͒��̃G���g������A���̃G���g�����o�͉\�ȏ�Ԃɂ���B<br>
     * ���k�Ɏ��s����(���k��T�C�Y�����k�O�T�C�Y��������)�ꍇ�A
     * �𓀂������k�Ŋi�[����B�G���g���̃T�C�Y���傫���ꍇ�A
     * ���̏����ɂ͂��Ȃ�̎��Ԃ�������B
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void closeEntry() throws IOException {
        if (this.header != null) {
            this.out.close();

            if (!this.rafo.cache.isEmpty()) {
                RandomAccessFileInputStream rafi;
                InputStream in;
                long pos = this.rafo.start;
                rafi = new RandomAccessFileInputStream(this.archive, this.rafo);
                in = CompressMethod.connectDecoder(rafi,
                        header.getCompressMethod(),
                        this.property,
                        this.header.getOriginalSize());

                byte[] buffer = new byte[8192];
                int length;
                while (0 <= (length = in.read(buffer))) {
                    rafi.cache(pos + length);
                    this.archive.seek(pos);
                    this.archive.write(buffer, 0, length);
                    pos += length;
                }
                in.close();

                this.header.setCompressMethod(CompressMethod.LH0);
            }

            long pos = this.archive.getFilePointer();
            long size = (pos - this.headerpos
                    - this.header.getBytes(this.encoding).length);
            this.header.setCompressedSize(size);
            if (this.header.getCRC() != LhaHeader.NO_CRC) {
                this.header.setCRC((int) this.crc.getValue());
            }

            this.archive.seek(this.headerpos);
            this.archive.write(this.header.getBytes(this.encoding));
            this.archive.seek(pos);
        }
        this.header = null;
        this.out = null;
    }


    //------------------------------------------------------------------
    //  inner classes
    //------------------------------------------------------------------
    //  private static class RandomAccessFileOutputStream
    //  private static class RandomAccessFileInputStream
    //  private static class Cache
    //------------------------------------------------------------------

    /**
     * RandomAccessFile �� OutputStream�� �C���^�t�F�C�X�ɍ��킹�邽�߂̃��b�p�N���X
     */
    private static class RandomAccessFileOutputStream extends OutputStream {

        //------------------------------------------------------------------
        //  instance field
        //------------------------------------------------------------------
        //  sink
        //------------------------------------------------------------------
        //  private RandomAccessFile archive
        //  private GrowthByteBuffer cache
        //------------------------------------------------------------------
        /**
         * �o�͐�RandomAccessFile
         */
        private RandomAccessFile archive;

        /**
         * �i�[���E�𒴂��ď����������Ƃ���
         * �ꍇ�̃L���b�V��
         */
        private Cache cache;


        //------------------------------------------------------------------
        //  instance field
        //------------------------------------------------------------------
        //  position
        //------------------------------------------------------------------
        //  private long start
        //  private long pos
        //  private long limit
        //------------------------------------------------------------------
        /**
         * �i�[�J�n�ʒu
         */
        private long start;

        /**
         * ���ݏ����ʒu
         */
        private long pos;

        /**
         * �i�[���E
         */
        private long limit;


        //------------------------------------------------------------------
        //  consutructor
        //------------------------------------------------------------------
        //  public RandomAccessFileOutputStream( RandomAccessFile archive,
        //                                       long length )
        //------------------------------------------------------------------

        /**
         * RandomAccessFile �����b�v���� OutputStream ���\�z����B
         *
         * @param archive �o�͐��RandomAccessFile
         * @param length  �o�͌��E��
         *
         * @exception IOException ���o�̓G���[�G���[�����������ꍇ
         */
        public RandomAccessFileOutputStream(RandomAccessFile archive,
                                            long length) throws IOException {
            this.archive = archive;
            this.start = this.archive.getFilePointer();                       //throws IOException
            this.pos = this.start;
            this.limit = this.start + length;
            this.cache = new Cache();
        }


        //------------------------------------------------------------------
        //  method of java.io.OutputStream
        //------------------------------------------------------------------
        //  write
        //------------------------------------------------------------------
        //  public void write( int data )
        //  public void write( byte[] buffer )
        //  public void write( byte[] buffer, int index, int length )
        //------------------------------------------------------------------

        /**
         * �ڑ����ꂽ RandomAccessFile ��1�o�C�g�������ށB
         *
         * @param data ��������1byte�̃f�[�^
         *
         * @exception IOException  ���o�̓G���[�����������ꍇ
         */
        public void write(int data) throws IOException {
            if (this.pos < this.limit && this.cache.isEmpty()) {
                this.pos++;
                this.archive.write(data);                                     //throws IOException
            } else {
                this.cache.add(new byte[]{(byte) data});
            }
        }

        /**
         * �ڑ����ꂽ RandomAccessFile �� buffer �̓��e��S�ď������ށB
         *
         * @param buffer �������ރf�[�^�̓������o�C�g�z��
         *
         * @exception IOException  ���o�̓G���[�����������ꍇ
         * @exception EOFException �R���X�g���N�^�ɓn���ꂽ�����𒴂���
         *                         �����������Ƃ����ꍇ
         */
        public void write(byte[] buffer) throws IOException {
            this.write(buffer, 0, buffer.length);                             //throws IOException
        }

        /**
         * �ڑ����ꂽRandomAccessFile��buffer�̓��e��index����length�o�C�g�������ށB
         *
         * @param buffer �������ރf�[�^�̓������o�C�g�z��
         * @param index  buffer���̏������ރf�[�^�̊J�n�ʒu
         * @param length �������ރf�[�^��
         *
         * @exception IOException  ���o�̓G���[�����������ꍇ
         */
        public void write(byte[] buffer, int index, int length)
                throws IOException {

            if (this.pos + length < this.limit && this.cache.isEmpty()) {
                this.pos += length;
                this.archive.write(buffer, index, length);                    //throws IOException
            } else {
                this.cache.add(buffer, index, length);
            }
        }


        //------------------------------------------------------------------
        //  method of java.io.OutputStream
        //------------------------------------------------------------------
        //  other
        //------------------------------------------------------------------
        //  public void close()
        //------------------------------------------------------------------

        /**
         * ���̃X�g���[������Ďg�p���Ă������\�[�X���J������B
         */
        public void close() {
            this.archive = null;
        }

    }

    /**
     * RandomAccessFile �� InputStream�̃C���^�[�t�F�C�X�����Ԃ��郉�b�p�N���X�B
     * ���k��̃T�C�Y�����k�O�̃T�C�Y���������Ƃ��ɉ𓀂��� 
     * �����k�Ŋi�[���Ȃ��������̂��߂ɕK�v�B
     */
    private static class RandomAccessFileInputStream extends InputStream {

        //------------------------------------------------------------------
        //  instance field
        //------------------------------------------------------------------
        //  source
        //------------------------------------------------------------------
        //  private RandomAccessFile archive
        //  private Cache front
        //  private Cache rear
        //------------------------------------------------------------------
        /**
         * �ǂݍ��݌�RandomAccessFile
         */
        private RandomAccessFile archive;

        /**
         * �O���L���b�V��
         * �������݂��ǂݍ��݂�ǂ��z�������̃L���b�V��
         */
        private Cache front;

        /**
         * �㕔�L���b�V��
         * �������݌��E�𒴂������̃f�[�^�̃L���b�V��
         */
        private Cache rear;


        //------------------------------------------------------------------
        //  instance field
        //------------------------------------------------------------------
        //  position
        //------------------------------------------------------------------
        //  private long pos
        //  private long limit
        //------------------------------------------------------------------
        /**
         * ���ݏ����ʒu
         */
        private long pos;

        /**
         * �ǂݍ��݌��E
         */
        private long limit;


        //------------------------------------------------------------------
        //  consutructor
        //------------------------------------------------------------------
        //  public RandomAccessFileInputStream( RandomAccessFile archive,
        //                                      RandomAccessFileOutputStream out )
        //------------------------------------------------------------------

        /**
         * RandomAccessFile �����b�v���� InputStream ���\�z����B
         *
         * @param archive �f�[�^���������� RandomAccessFile
         * @param out     ���O�Ɉ��k�f�[�^���󂯎���Ă��� RandomAccessFileOutputStream
         */
        public RandomAccessFileInputStream(RandomAccessFile archive,
                                           RandomAccessFileOutputStream out) {
            this.archive = archive;
            this.pos = out.start;
            this.limit = out.pos;
            this.front = new Cache();
            this.rear = out.cache;
        }


        //------------------------------------------------------------------
        //  method of java.io.InputStream
        //------------------------------------------------------------------
        //  read
        //------------------------------------------------------------------
        //  public int read()
        //  public int read( byte[] buffer )
        //  public int read( byte[] buffer, int index, int length )
        //------------------------------------------------------------------

        /**
         * �L���b�V����RandomAccessFile���� 1�o�C�g�̃f�[�^��ǂݍ��ށB
         *
         * @return �ǂݍ��܂ꂽ1�o�C�g�̃f�[�^<br>
         *         �ǂݍ��ރf�[�^��������� -1
         *
         * @exception IOException ���o�̓G���[�����������ꍇ
         */
        public int read() throws IOException {
            int return_value = this.front.read();
            if (return_value < 0) {
                if (this.pos < this.limit) {
                    this.archive.seek(this.pos++);
                    return_value = this.archive.read();
                } else {
                    return_value = this.rear.read();
                }
            }

            return return_value;
        }

        /**
         * �L���b�V���� RandomAccessFile���� buffer�𖞂����悤�Ƀf�[�^��ǂݍ��ށB
         *
         * @param buffer �ǂݍ��܂ꂽ�f�[�^���i�[����o�b�t�@
         *
         * @return ���ۂɓǂݍ��܂ꂽ�f�[�^��
         *
         * @exception IOException ���o�̓G���[�����������ꍇ
         */
        public int read(byte[] buffer) throws IOException {
            return this.read(buffer, 0, buffer.length);
        }

        /**
         * �L���b�V���� RandomAccessFile���� buffer��index��length�o�C�g�ǂݍ��ށB
         *
         * @param buffer �ǂݍ��܂ꂽ�f�[�^���i�[����o�b�t�@
         * @param index  buffer���̓ǂݍ��݊J�n�ʒu
         * @param length �ǂݍ��ރf�[�^��
         *
         * @return ���ۂɓǂݍ��܂ꂽ�f�[�^��
         *
         * @exception IOException ���o�̓G���[�����������ꍇ
         */
        public int read(byte[] buffer, int index, int length) throws IOException {

            int count = 0;
            int ret = this.front.read(buffer, index, length);
            if (0 <= ret) {
                count += ret;
            }

            this.archive.seek(this.pos);                                      //throws IOException
            ret = Math.min(length - count,
                    Math.max((int) (this.limit - this.pos), 0));
            this.archive.readFully(buffer, index + count, ret);               //throws IOException
            if (0 <= ret) {
                this.pos += ret;
                count += ret;
            }

            ret = this.rear.read(buffer, index + count, length - count);
            if (0 <= ret) {
                count += ret;
            }

            if (0 < count) {
                return count;
            } else {
                return -1;
            }
        }


        //------------------------------------------------------------------
        //  method of java.io.InputStream 
        //------------------------------------------------------------------
        //  other
        //------------------------------------------------------------------
        //  public void close()
        //------------------------------------------------------------------

        /**
         * ���̃X�g���[�����
         * �g�p���Ă������\�[�X���J������B
         */
        public void close() {
            this.front = null;
            this.rear = null;
            this.archive = null;
        }


        //------------------------------------------------------------------
        //  original method
        //------------------------------------------------------------------
        //  public void cache( long pos )
        //------------------------------------------------------------------

        /**
         * pos�܂œǂݍ���ł��Ȃ���΁A
         * ���ݓǂݍ��݈ʒu����pos�܂ł̃f�[�^��
         * �O���L���b�V���Ƀf�[�^��ǉ�����B
         *
         * @param pos archive���̏����o���ʒu
         */
        public void cache(long pos) throws IOException {
            int length = (int) Math.min(this.limit - this.pos,
                    pos - this.pos);

            byte[] buffer = new byte[length];
            if (0 < length) {
                this.archive.seek(this.pos);                                  //throws IOException
                this.archive.readFully(buffer);                               //throws IOException
                this.front.add(buffer);

                this.pos += length;
            }
        }
    }

    /**
     * �������݌��E�𒴂����������݂�
     * �ǂݍ��݈ʒu�𒴂����������݂������ꍇ��
     * �f�[�^���L���b�V�����邽�߂Ɏg�p����B
     */
    private static class Cache {

        //------------------------------------------------------------------
        //  instance field
        //------------------------------------------------------------------
        //  private Vector cache
        //  private byte[] current
        //  private int position
        //------------------------------------------------------------------
        /**
         * byte[] �� Vector
         * �e�v�f�� �S�ēǂݍ��܂ꂽ��
         * �����Ɏ̂Ă���B
         */
        private Vector cache;

        /**
         * ���ݓǂݍ��ݒ��̗v�f
         */
        private byte[] current;

        /**
         * current�̌��ݏ����ʒu
         */
        private int position;


        //------------------------------------------------------------------
        //  constructor
        //------------------------------------------------------------------
        //  public Cache()
        //------------------------------------------------------------------

        /**
         * �f�[�^�̈ꎞ�ޔ��@�\���\�z����B
         */
        public Cache() {
            this.current = null;
            this.position = 0;
            this.cache = new Vector();
        }


        //------------------------------------------------------------------
        //  read
        //------------------------------------------------------------------
        //  public int read()
        //  public int read( byte[] buffer, int index, int length )
        //------------------------------------------------------------------

        /**
         * �L���b�V������ 1�o�C�g�̃f�[�^��
         * 0�`255�Ƀ}�b�v���ēǂݍ��ށB
         *
         * @return �ǂݍ��܂ꂽ1byte�̃f�[�^<br>
         *         �L���b�V������Ńf�[�^�������ꍇ�� -1
         */
        public int read() {
            if (null != this.current) {
                int ret = this.current[this.position++] & 0xFF;

                if (this.current.length <= this.position) {
                    if (0 < this.cache.size()) {
                        this.current = (byte[]) this.cache.firstElement();
                        this.cache.removeElementAt(0);
                    } else {
                        this.current = null;
                    }
                    this.position = 0;
                }

                return ret;
            } else {
                return -1;
            }
        }

        /**
         * �L���b�V������ buffer��index�Ŏn�܂�ꏊ��length�o�C�g�ǂݍ��ށB
         *
         * @param buffer �ǂݍ��񂾃f�[�^��ێ�����o�b�t�@
         * @param index  buffer���̓ǂݍ��݊J�n�ʒu
         * @param length �ǂݍ��ރf�[�^��
         *
         * @return ���ۂɓǂݍ��܂ꂽ�f�[�^��<br>
         *         �L���b�V������Ńf�[�^�������ꍇ�� -1
         */
        public int read(byte[] buffer, int index, int length) {
            int count = 0;

            while (null != this.current && count < length) {
                int copylen = Math.min(this.current.length - this.position,
                        length - count);
                System.arraycopy(this.current, this.position,
                        buffer, index + count, copylen);

                this.position += copylen;
                count += copylen;

                if (this.current.length <= this.position) {
                    if (0 < this.cache.size()) {
                        this.current = (byte[]) this.cache.firstElement();
                        this.cache.removeElementAt(0);
                    } else {
                        this.current = null;
                    }
                    this.position = 0;
                }
            }

            if (count == 0) {
                return -1;
            } else {
                return count;
            }
        }


        //------------------------------------------------------------------
        //  write
        //------------------------------------------------------------------
        //  public void add( byte[] buffer )
        //  public void add( byte[] buffer, int index, int length )
        //------------------------------------------------------------------

        /**
         * �L���b�V���Ƀf�[�^��ǉ�����B
         *
         * @param buffer �f�[�^�̊i�[���ꂽ�o�b�t�@
         */
        public void add(byte[] buffer) {
            if (this.current == null) {
                this.current = buffer;
            } else {
                this.cache.addElement(buffer);
            }
        }

        /**
         * �L���b�V���Ƀf�[�^��ǉ�����B
         *
         * @parma buffer �f�[�^�̊i�[���ꂽ�o�b�t�@
         * @param index  buffer���̃f�[�^�J�n�ʒu
         * @param length �i�[����Ă���f�[�^�̗�
         */
        public void add(byte[] buffer, int index, int length) {
            byte[] buf = new byte[length];
            System.arraycopy(buffer, index, buf, 0, length);

            if (this.current == null) {
                this.current = buf;
            } else {
                this.cache.addElement(buf);
            }
        }


        //------------------------------------------------------------------
        //  other
        //------------------------------------------------------------------
        //  public boolean isEmpty()
        //------------------------------------------------------------------

        /**
         * ���̃L���b�V�����󂩂𓾂�B
         *
         * @return ���̃L���b�V������Ȃ� true
         *         ��łȂ���� false
         */
        public boolean isEmpty() {
            return this.current == null;
        }

    }

}
//end of LhaRetainedOutputStream.java
