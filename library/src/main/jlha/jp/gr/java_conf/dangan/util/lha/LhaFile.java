//start of LhaFile.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * LhaFile.java
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
import java.util.*;

//import exceptions


/**
 * LHA���Ƀt�@�C������G���g���f�[�^��ǂݏo��
 * InputStream�𓾂邽�߂̃��[�e�B���e�B�N���X�B<br>
 * java.util.zip.ZipFile �Ǝ���
 * �C���^�[�t�F�C�X�����悤�ɍ�����B
 * CRC16���ɂ��`�F�b�N�͍s��Ȃ��B
 *
 * <pre>
 * -- revision history --
 * $Log: LhaFile.java,v $
 * Revision 1.1  2002/12/08 00:00:00  dangan
 * [maintenance]
 *     LhaConstants ���� CompressMethod �ւ̃N���X���̕ύX�ɍ��킹�ďC���B
 *
 * Revision 1.0  2002/08/05 00:00:00  dangan
 * add to version control
 * [improvement]
 *     �G���g���̊Ǘ��� Hashtable ���g�p���鎖�ɂ����
 *     ��ʂ̃G���g���������ɂŃG���g���J�n�ʒu��
 *     ��葬����������悤�ɉ��ǁB
 * [change]
 *     �R���X�g���N�^���� ������ String encode �������̂�p�~�A
 *     Properties �������Ɏ����̂�ǉ��B
 * [maintanance]
 *     �\�[�X����
 *     �^�u�p�~
 *     ���C�Z���X���̏C��
 *
 * </pre>
 *
 * @author $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class LhaFile {


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  archive file of LHA 
    //------------------------------------------------------------------
    //  private RandomAccessFile archive
    //  private Object LastAccessObject
    //  private Vector headers
    //  private Vector entryStart
    //  private Hashtable hash
    //  private Vector duplicate
    //------------------------------------------------------------------
    /**
     * LHA���Ɍ`���̃f�[�^������
     * RandomAccessFile�̃C���X�^���X
     */
    private RandomAccessFile archive;

    /**
     * �Ō�� archive �ɃA�N�Z�X�����I�u�W�F�N�g
     */
    private Object LastAccessObject;

    /**
     * �e�G���g���̃w�b�_������ LhaHeader �� Vector
     * headers.elementAt( index ) �̃w�b�_�����G���g���� 
     * entryPoint.elementAt( index ) �̈ʒu����n�܂�B
     */
    private Vector headers;

    /**
     * �e�G���g���̊J�n�ʒu������ Long �� Vector
     * headers.elementAt( index ) �̃w�b�_�����G���g���� 
     * entryPoint.elementAt( index ) �̈ʒu����n�܂�B
     */
    private Vector entryPoint;

    /**
     * �G���g���̖��O(�i�[�t�@�C����)���L�[�ɁA
     * �L�[�̖��O�̃G���g���� index �����n�b�V���e�[�u���B
     * �v�f�� Integer
     */
    private Hashtable hash;

    /**
     * �����t�@�C���̋~�o�p�B
     * �d���������O�����G���g���� index ������ Vector
     * �v�f�� Integer
     */
    private Vector duplicate;


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
    //  private LhaFile()
    //  public LhaFile( String filename )
    //  public LhaFile( String filename, Properties property )
    //  public LhaFile( File file )
    //  public LhaFile( File file, Properties property )
    //  public LhaFile( RandomAccessFile archive )
    //  public LhaFile( RandomAccessFile archive, boolean rescueMode )
    //  public LhaFile( RandomAccessFile archive, Properties property )
    //  public LhaFile( RandomAccessFile archive, 
    //                  Properties property, boolean rescueMode )
    //  private void constructerHelper( RandomAccessFile archive, 
    //                                  Properties property,
    //                                  boolean rescueMode )
    //------------------------------------------------------------------

    /**
     * �f�t�H���g�R���X�g���N�^�B
     * �d�l�s��
     */
    private LhaFile() {
    }

    /**
     * filename �Ŏw�肳�ꂽ�t�@�C�����珑�Ƀf�[�^��ǂ݂���LhaFile���\�z����B<br>
     * �e���k�`���ɑΉ�����������̐������������v���p�e�B�ɂ�
     * LhaProperty.getProperties() �œ���ꂽ�v���p�e�B���g�p�����B<br>
     *
     * @param filename LHA���Ƀt�@�C���̖��O
     *
     * @exception IOException
     *                 ���o�̓G���[�����������ꍇ
     * @exception FileNotFoundException
     *                 �t�@�C����������Ȃ��ꍇ
     * @exception SecurityException
     *                 �Z�L�����e�B�}�l�[�W�����t�@�C���̓ǂݍ��݂������Ȃ��ꍇ
     *
     * @see LhaProperty#getProperties()
     */
    public LhaFile(String filename) throws IOException {
        Properties property = LhaProperty.getProperties();
        RandomAccessFile file = new RandomAccessFile(filename, "r");          //throws FileNotFoundException SecurityException

        this.constructerHelper(file, property, false);                        //After Java 1.1 throws UnsupportedEncodingException
    }

    /**
     * filename �Ŏw�肳�ꂽ�t�@�C�����珑�Ƀf�[�^��ǂ݂���LhaFile���\�z����B<br>
     *
     * @param filename LHA���Ƀt�@�C���̖��O
     * @param property �e���k�`���ɑΉ�����������̐����������܂܂��v���p�e�B
     *
     * @exception IOException
     *                 ���o�̓G���[�����������ꍇ
     * @exception FileNotFoundException
     *                 �t�@�C����������Ȃ��ꍇ
     * @exception UnsupportedEncodingException
     *                 property.getProperty( "lha.encoding" ) �œ���ꂽ
     *                 �G���R�[�f�B���O�����T�|�[�g����Ȃ��ꍇ
     * @exception SecurityException
     *                 �Z�L�����e�B�}�l�[�W�����t�@�C���̓ǂݍ��݂������Ȃ��ꍇ
     *
     * @see LhaProperty
     */
    public LhaFile(String filename, Properties property) throws IOException {
        RandomAccessFile file = new RandomAccessFile(filename, "r");          //throws FileNotFoundException SecurityException

        this.constructerHelper(file, property, false);                        //After Java 1.1 throws UnsupportedEncodingException
    }

    /**
     * filename �Ŏw�肳�ꂽ�t�@�C�����珑�Ƀf�[�^��ǂ݂���LhaFile���\�z����B<br>
     * �e���k�`���ɑΉ�����������̐������������v���p�e�B�ɂ�
     * LhaProperty.getProperties() �œ���ꂽ�v���p�e�B���g�p�����B<br>
     *
     * @param filename LHA���Ƀt�@�C��
     *
     * @exception IOException
     *                 ���o�̓G���[�����������ꍇ
     * @exception FileNotFoundException
     *                 �t�@�C����������Ȃ��ꍇ
     * @exception SecurityException
     *                 �Z�L�����e�B�}�l�[�W�����t�@�C���̓ǂݍ��݂������Ȃ��ꍇ
     *
     * @see LhaProperty#getProperties()
     */
    public LhaFile(File filename) throws IOException {
        Properties property = LhaProperty.getProperties();
        RandomAccessFile file = new RandomAccessFile(filename, "r");          //throws FileNotFoundException SecurityException

        this.constructerHelper(file, property, false);                        //After Java 1.1 throws UnsupportedEncodingException
    }

    /**
     * filename �Ŏw�肳�ꂽ�t�@�C�����珑�Ƀf�[�^��ǂ݂��� LhaFile ���\�z����B<br>
     *
     * @param filename LHA���Ƀt�@�C��
     * @param property �e���k�`���ɑΉ�����������̐����������܂܂��v���p�e�B
     *
     * @exception IOException
     *                 ���o�̓G���[�����������ꍇ
     * @exception FileNotFoundException
     *                 �t�@�C����������Ȃ��ꍇ
     * @exception UnsupportedEncodingException
     *                 property.getProperty( "lha.encoding" ) �œ���ꂽ
     *                 �G���R�[�f�B���O�����T�|�[�g����Ȃ��ꍇ
     * @exception SecurityException
     *                 �Z�L�����e�B�}�l�[�W�����t�@�C���̓ǂݍ��݂������Ȃ��ꍇ
     *
     * @see LhaProperty
     */
    public LhaFile(File filename, Properties property) throws IOException {
        RandomAccessFile file = new RandomAccessFile(filename, "r");          //throws FileNotFoundException SecurityException

        this.constructerHelper(file, property, false);                        //After Java 1.1 throws UnsupportedEncodingException
    }

    /**
     * file �Ŏw�肳�ꂽ�t�@�C�����珑�Ƀf�[�^��ǂ݂��� LhaFile ���\�z����B<br>
     * �e���k�`���ɑΉ�����������̐������������v���p�e�B�ɂ�
     * LhaProperty.getProperties() �œ���ꂽ�v���p�e�B���g�p�����B<br>
     *
     * @param file LHA���Ƀt�@�C��
     *
     * @exception IOException
     *                 ���o�̓G���[�����������ꍇ
     * @exception FileNotFoundException
     *                 �t�@�C����������Ȃ��ꍇ
     * @exception SecurityException
     *                 �Z�L�����e�B�}�l�[�W�����t�@�C���̓ǂݍ��݂������Ȃ��ꍇ
     *
     * @see LhaProperty#getProperties()
     */
    public LhaFile(RandomAccessFile file) throws IOException {
        Properties property = LhaProperty.getProperties();

        this.constructerHelper(file, property, false);
    }

    /**
     * file �Ŏw�肳�ꂽ�t�@�C�����珑�Ƀf�[�^��ǂ݂��� LhaFile ���\�z����B<br>
     * �e���k�`���ɑΉ�����������̐������������v���p�e�B�ɂ�
     * LhaProperty.getProperties() �œ���ꂽ�v���p�e�B���g�p�����B<br>
     *
     * @param file       LHA���Ƀt�@�C��
     * @param rescueMode true �ɂ���Ɖ�ꂽ���ɂ̃f�[�^��
     *                   �������邽�߂̕������[�h�ŃG���g������������B
     *
     * @exception IOException
     *                 ���o�̓G���[�����������ꍇ
     * @exception FileNotFoundException
     *                 �t�@�C����������Ȃ��ꍇ
     * @exception SecurityException
     *                 �Z�L�����e�B�}�l�[�W�����t�@�C���̓ǂݍ��݂������Ȃ��ꍇ
     *
     * @see LhaProperty#getProperties()
     */
    public LhaFile(RandomAccessFile file, boolean rescueMode)
            throws IOException {
        Properties property = LhaProperty.getProperties();

        this.constructerHelper(file, property, rescueMode);
    }

    /**
     * file �Ŏw�肳�ꂽ�t�@�C�����珑�Ƀf�[�^��ǂ݂��� LhaFile ���\�z����B<br>
     *
     * @param file     LHA���Ƀt�@�C��
     * @param property �e���k�`���ɑΉ�����������̐����������܂܂��v���p�e�B
     *
     * @exception IOException
     *                 ���o�̓G���[�����������ꍇ
     * @exception FileNotFoundException
     *                 �t�@�C����������Ȃ��ꍇ
     * @exception SecurityException
     *                 �Z�L�����e�B�}�l�[�W�����t�@�C���̓ǂݍ��݂������Ȃ��ꍇ
     *
     * @see LhaProperty
     */
    public LhaFile(RandomAccessFile file, Properties property)
            throws IOException {

        this.constructerHelper(file, property, false);
    }

    /**
     * file �Ŏw�肳�ꂽ�t�@�C�����珑�Ƀf�[�^��ǂ݂��� LhaFile ���\�z����B<br>
     *
     * @param file       LHA���Ƀt�@�C��
     * @param property   �e���k�`���ɑΉ�����������̐����������܂܂��v���p�e�B
     * @param rescueMode true �ɂ���Ɖ�ꂽ���ɂ̃f�[�^��
     *                   �������邽�߂̕������[�h�ŃG���g������������B
     *
     * @exception IOException
     *                 ���o�̓G���[�����������ꍇ
     * @exception FileNotFoundException
     *                 �t�@�C����������Ȃ��ꍇ
     * @exception SecurityException
     *                 �Z�L�����e�B�}�l�[�W�����t�@�C���̓ǂݍ��݂������Ȃ��ꍇ
     *
     * @see LhaProperty
     */
    public LhaFile(RandomAccessFile file, Properties property, boolean rescueMode)
            throws IOException {

        this.constructerHelper(file, property, rescueMode);
    }

    /**
     * 2�� LhaHeader�Aheader1 �� header2 �����������ׂ�B
     *
     * @param header1 �����Ώۂ̃w�b�_ ����1
     * @param header2 �����Ώۂ̃w�b�_ ����2
     *
     * @return header1 �� header2 �������ł���� true �Ⴆ�� false
     */
    private static boolean equal(LhaHeader header1, LhaHeader header2) {
        return header1.getPath().equals(header2.getPath())
                && header1.getCompressMethod().equals(header2.getCompressMethod())
                && header1.getLastModified().equals(header2.getLastModified())
                && header1.getCompressedSize() == header2.getCompressedSize()
                && header1.getOriginalSize() == header2.getOriginalSize()
                && header1.getCRC() == header2.getCRC()
                && header1.getOSID() == header2.getOSID()
                && header1.getHeaderLevel() == header2.getHeaderLevel();
    }


    //------------------------------------------------------------------
    //  original method ( on the model of java.util.zip.ZipFile )
    //------------------------------------------------------------------
    //  get InputStream
    //------------------------------------------------------------------
    //  public InputStream getInputStream( LhaHeader header )
    //  public InputStream getInputStream( String name )
    //  public InputStream getInputStreamWithoutExtract( LhaHeader header )
    //  public InputStream getInputStreamWithoutExtract( String name )
    //------------------------------------------------------------------

    /**
     * file �𑖍����ăG���g�������\�z����B<br>
     *
     * @param file       LHA���Ƀt�@�C��
     * @param propety    �e���k�`���ɑΉ�����������̐����������܂܂��v���p�e�B
     * @param rescueMode true �ɂ���Ɖ�ꂽ���ɂ̃f�[�^��
     *                   �������邽�߂̕������[�h�ŃG���g������������B
     *
     * @exception IOException
     *                 ���o�̓G���[�����������ꍇ
     * @exception UnsupportedEncodingException
     *                 encode���T�|�[�g����Ȃ��ꍇ
     */
    private void constructerHelper(RandomAccessFile file,
                                   Properties property,
                                   boolean rescueMode)
            throws IOException {

        this.headers = new Vector();
        this.entryPoint = new Vector();

        file.seek(0);
        CachedRandomAccessFileInputStream archive = new CachedRandomAccessFileInputStream(file);

        byte[] HeaderData = LhaHeader.getFirstHeaderData(archive);
        while (null != HeaderData) {
            LhaHeader header = LhaHeader.createInstance(HeaderData, property);
            headers.addElement(header);
            entryPoint.addElement(new Long(archive.position()));

            if (!rescueMode) {
                archive.skip(header.getCompressedSize());
                HeaderData = LhaHeader.getNextHeaderData(archive);
            } else {
                HeaderData = LhaHeader.getFirstHeaderData(archive);
            }
        }
        archive.close();

        this.hash = new Hashtable();
        this.duplicate = new Vector();
        for (int i = 0; i < this.headers.size(); i++) {
            LhaHeader header = (LhaHeader) headers.elementAt(i);

            if (!this.hash.containsKey(header.getPath())) {
                this.hash.put(header.getPath(), new Integer(i));
            } else {
                this.duplicate.addElement(new Integer(i));
            }
        }

        this.archive = file;
        this.property = (Properties) property.clone();
    }

    /**
     * header �Ŏw�肳�ꂽ�G���g����
     * ���e���𓀂��Ȃ���ǂ݂��ޓ��̓X�g���[���𓾂�B<br>
     *
     * @param header �w�b�_
     *
     * @return header�Ŏw�肳�ꂽ�w�b�_�����G���g����
     *         ���e��ǂ݂��ޓ��̓X�g���[���B<br>
     *         �G���g����������Ȃ��ꍇ�� null�B
     */
    public InputStream getInputStream(LhaHeader header) {
        int index = this.getIndex(header);
        if (0 <= index) {
            long start = ((Long) this.entryPoint.elementAt(index)).longValue();
            long len = header.getCompressedSize();
            InputStream in = new RandomAccessFileInputStream(start, len);

            return CompressMethod.connectDecoder(in,
                    header.getCompressMethod(),
                    this.property,
                    header.getOriginalSize());
        } else {
            return null;
        }
    }

    /**
     * name�Ŏw�肳�ꂽ���O�����G���g����
     * ���e���𓀂��Ȃ���ǂ݂��ޓ��̓X�g���[���𓾂�B<br>
     *
     * @param name �G���g���̖��O
     *
     * @return name�Ŏw�肳�ꂽ���O�����G���g����
     *         ���e���𓀂��Ȃ���ǂ݂��ޓ��̓X�g���[���B<br>
     *         �G���g����������Ȃ��ꍇ�� null�B
     */
    public InputStream getInputStream(String name) {
        if (this.hash.containsKey(name)) {
            int index = ((Integer) this.hash.get(name)).intValue();
            LhaHeader header = (LhaHeader) this.headers.elementAt(index);
            long start = ((Long) this.entryPoint.elementAt(index)).longValue();
            long len = header.getCompressedSize();
            InputStream in = new RandomAccessFileInputStream(start, len);

            return CompressMethod.connectDecoder(in,
                    header.getCompressMethod(),
                    this.property,
                    header.getOriginalSize());
        } else {
            return null;
        }
    }

    /**
     * header�Ŏw�肳�ꂽ�G���g���̓��e��
     * �𓀂����ɓǂ݂��ޓ��̓X�g���[����Ԃ��B<br>
     *
     * @param header �w�b�_
     *
     * @return header�Ŏw�肳�ꂽ�G���g���̓��e��
     *         �𓀂����ɓǂ݂��ޓ��̓X�g���[���B<br>
     *         �G���g����������Ȃ��ꍇ�� null�B
     */
    public InputStream getInputStreamWithoutExtract(LhaHeader header) {
        int index = this.getIndex(header);
        if (0 <= index) {
            long start = ((Long) this.entryPoint.elementAt(index)).longValue();
            long len = header.getCompressedSize();

            return new RandomAccessFileInputStream(start, len);
        } else {
            return null;
        }
    }


    //------------------------------------------------------------------
    //  original method ( on the model of java.util.zip.ZipFile  )
    //------------------------------------------------------------------
    //  other
    //------------------------------------------------------------------
    //  public int size()
    //  public Enumeration entries()
    //  public LhaHeader[] getEntries()
    //  public void close()
    //------------------------------------------------------------------

    /**
     * name�Ŏw�肳�ꂽ���O�����G���g����
     * ���e���𓀂����ɓǂ݂��ޓ��̓X�g���[����Ԃ��B<br>
     *
     * @param name �G���g���̖��O
     *
     * @return name�Ŏw�肳�ꂽ���O�����G���g����
     *         ���e���𓀂����ɓǂ݂��ޓ��̓X�g���[���B<br>
     *         �G���g����������Ȃ��ꍇ�� null�B
     */
    public InputStream getInputStreamWithoutExtract(String name) {
        if (this.hash.containsKey(name)) {
            int index = ((Integer) this.hash.get(name)).intValue();
            LhaHeader header = (LhaHeader) this.headers.elementAt(index);
            long start = ((Long) this.entryPoint.elementAt(index)).longValue();
            long len = header.getCompressedSize();

            return new RandomAccessFileInputStream(start, len);
        } else {
            return null;
        }
    }

    /**
     * ���� LhaFile ���̃G���g���̐��𓾂�B
     *
     * @return �t�@�C�����̃G���g���̐�
     */
    public int size() {
        return this.headers.size();
    }

    /**
     * ���� LhaFile ���̃G���g���� LhaHeader �̗񋓎q�𓾂�B
     *
     * @return LhaHeader �̗񋓎q
     *
     * @exception IllegalStateException
     *                   LhaFile �� close() �ŕ����Ă���ꍇ�B
     */
    public Enumeration entries() {
        if (this.archive != null) {
            return new HeaderEnumeration();
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * �t�@�C�����̃G���g����񋓂����z��𓾂�B
     *
     * @return �t�@�C�����̃G���g����񋓂����z��
     */
    public LhaHeader[] getEntries() {
        LhaHeader[] headers = new LhaHeader[this.headers.size()];

        for (int i = 0; i < this.headers.size(); i++) {
            headers[i] = (LhaHeader) ((LhaHeader) this.headers.elementAt(i)).clone();
        }

        return headers;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  private int getIndex( LhaHeader target )
    //  private static boolean equal( LhaHeader header1, LhaHeader header2 )
    //------------------------------------------------------------------

    /**
     * ���� LHA���Ƀt�@�C�������B
     * ���̍ہA����LhaFile�����s�����S�Ă�
     * InputStream�͋����I�ɕ�����B
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void close() throws IOException {
        this.archive.close();
        this.archive = null;
        this.LastAccessObject = null;
        this.headers = null;
        this.entryPoint = null;
        this.hash = null;
        this.property = null;
        this.duplicate = null;
    }

    /**
     * headers �ɂ����� target �� index �𓾂�B
     *
     * @param target �w�b�_
     *
     * @return headers ���ł� target �� index�B
     *         headers ���� target ���Ȃ��ꍇ�� -1
     */
    private int getIndex(LhaHeader target) {
        int index = ((Integer) this.hash.get(target.getPath())).intValue();

        LhaHeader header = (LhaHeader) this.headers.elementAt(index);
        if (!LhaFile.equal(header, target)) {
            boolean match = false;
            for (int i = 0; i < this.duplicate.size() && !match; i++) {
                index = ((Integer) this.duplicate.elementAt(i)).intValue();
                header = (LhaHeader) this.headers.elementAt(index);

                if (LhaFile.equal(header, target)) {
                    match = true;
                }
            }

            if (match) {
                return index;
            } else {
                return -1;
            }
        } else {
            return index;
        }
    }


    //------------------------------------------------------------------
    //  inner classes
    //------------------------------------------------------------------
    //  private class RandomAccessFileInputStream
    //  private static class CachedRandomAccessFileInputStream
    //  private class EntryEnumeration
    //------------------------------------------------------------------

    /**
     * �w�b�_�����p �� RandomAccessFileInputStream�B<br>
     * �o�b�t�@�����O�Ɠ����������s��Ȃ����ɂ���č��������Ă���B
     */
    private static class CachedRandomAccessFileInputStream extends InputStream {

        //------------------------------------------------------------------
        //  instance field
        //------------------------------------------------------------------
        //  source
        //------------------------------------------------------------------
        //  private RandomAccessFile archive
        //------------------------------------------------------------------
        /**
         * �f�[�^���������� RandomAccessFile
         */
        private RandomAccessFile archive;


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
        //  private long markPosition
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

        /** position �̃o�b�N�A�b�v�p */
        private long markPosition;


        //------------------------------------------------------------------
        //  constructer
        //------------------------------------------------------------------
        //  public CachedRandomAccessFileInputStream()
        //------------------------------------------------------------------

        /**
         * �L���b�V�����g�p���� ���������� RandomAccessFileInputStream ���\�z����B
         *
         * @param file �f�[�^���������� RandomAccessFile
         */
        public CachedRandomAccessFileInputStream(RandomAccessFile file) {
            this.archive = file;

            this.cache = new byte[1024];
            this.cachePosition = 0;
            this.cacheLimit = 0;
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
         * archive�̌��ݏ����ʒu���� 1byte�̃f�[�^��ǂݍ��ށB
         *
         * @return �ǂ݂��܂ꂽ1byte�̃f�[�^<br>
         *         ���ɓǂ݂��݌��E�ɒB�����ꍇ�� -1
         *
         * @exception IOException ���o�̓G���[�����������ꍇ
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
         * archive�̌��ݏ����ʒu���� buffer�𖞂����悤�Ƀf�[�^��ǂݍ��ށB
         *
         * @param buffer �ǂ݂��܂ꂽ�f�[�^���i�[����o�b�t�@
         *
         * @return �ǂ݂��܂ꂽ�o�C�g��<br>
         *         ���ɓǂ݂��݌��E�ɒB���Ă����ꍇ��-1
         *
         * @exception IOException ���o�̓G���[�����������ꍇ
         */
        public int read(byte[] buffer) throws IOException {
            return this.read(buffer, 0, buffer.length);
        }

        /**
         * archive�̌��ݏ����ʒu���� buffer��index����n�܂�̈��
         * length�o�C�g�̃f�[�^��ǂݍ��ށB
         *
         * @param buffer �ǂ݂��܂ꂽ�f�[�^���i�[����o�b�t�@
         * @param index  buffer���̓ǂ݂��݊J�n�ʒu
         * @param length �ǂ݂��ރo�C�g���B
         *
         * @return �ǂ݂��܂ꂽ�o�C�g��<br>
         *         ���ɓǂ݂��݌��E�ɒB���Ă����ꍇ��-1
         *
         * @exception IOException ���o�̓G���[�����������ꍇ
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
         * length�o�C�g�̃f�[�^��ǂݔ�΂��B
         *
         * @param length �ǂݔ�΂������o�C�g��
         *
         * @return ���ۂɓǂݔ�΂��ꂽ�o�C�g��
         */
        public long skip(long length) throws IOException {
            final long requested = length;

            if (this.cachePosition < this.cacheLimit) {
                long avail = (long) this.cacheLimit - this.cachePosition;
                long skiplen = Math.min(length, avail);

                length -= skiplen;
                this.cachePosition += (int) skiplen;
            }

            if (0 < length) {
                long avail = this.archive.length() - this.archive.getFilePointer();
                long skiplen = Math.min(avail, length);

                length -= skiplen;
                archive.seek(archive.getFilePointer() + skiplen);
            }

            return requested - length;
        }


        //------------------------------------------------------------------
        //  method of java.io.InputStream
        //------------------------------------------------------------------
        //  mark/reset
        //------------------------------------------------------------------
        //  public boolean markSupported()
        //  public void mark( int readLimit )
        //  public void reset()
        //------------------------------------------------------------------

        /**
         * ���̃I�u�W�F�N�g��mark/reset���T�|�[�g���邩��Ԃ��B
         *
         * @return ���̃I�u�W�F�N�g��mark/reset���T�|�[�g����B<br>
         *         ���true�B
         */
        public boolean markSupported() {
            return true;
        }

        /**
         * ���ݏ����ʒu�Ƀ}�[�N���{������reset��
         * ���݂̏����ʒu�ɖ߂��悤�ɂ���B
         *
         * @param readLimit �}�[�N�̗L�����E�B
         *                  ���̃I�u�W�F�N�g�ł͈Ӗ��������Ȃ��B
         */
        public void mark(int readLimit) {
            try {
                this.markPosition = this.archive.getFilePointer();
            } catch (IOException exception) {
                throw new Error("caught IOException( " + exception.getMessage() + " ) in mark()");
            }

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
         * �Ō�Ƀ}�[�N���ꂽ�����ʒu�ɖ߂��B
         *
         * @exception IOException mark()����Ă��Ȃ��ꍇ
         */
        public void reset() throws IOException {
            if (this.markPositionIsInCache) {
                this.cachePosition = this.markCachePosition;
            } else if (this.markCache == null) { //���̏������͖����Ƀ}�[�N����Ă��Ȃ����Ƃ������B�R���X�g���N�^�� markCache �� null �ɐݒ肳���̂𗘗p����B
                throw new IOException("not marked.");
            } else {
                //in �� reset() �ł��Ȃ��ꍇ��
                //�ŏ��̍s�� this.in.reset() ��
                //IOException �𓊂��邱�Ƃ����҂��Ă���B
                this.archive.seek(this.markPosition);                 //throws IOException

                System.arraycopy(this.markCache, 0, this.cache, 0, this.markCacheLimit);
                this.cacheLimit = this.markCacheLimit;
                this.cachePosition = this.markCachePosition;
            }
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
         */
        public int available() {
            return this.cacheLimit - this.cachePosition;
        }

        /**
         * ���̓��̓X�g���[������A�g�p���Ă���
         * �S�Ẵ��\�[�X���J������B<br>
         */
        public void close() {
            this.archive = null;

            this.cache = null;
            this.cachePosition = 0;
            this.cacheLimit = 0;

            this.markPositionIsInCache = false;
            this.markCache = null;
            this.markCachePosition = 0;
            this.markCacheLimit = 0;
            this.markPosition = 0;
        }


        //------------------------------------------------------------------
        //  original method
        //------------------------------------------------------------------
        //  public long position()
        //------------------------------------------------------------------

        /**
         * �t�@�C���擪���n�_�Ƃ��錻�݂̓ǂݍ��݈ʒu�𓾂�B
         *
         * @return ���݂̓ǂݍ��݈ʒu�B
         */
        public long position() throws IOException {
            long position = this.archive.getFilePointer();

            position -= this.cacheLimit - this.cachePosition;

            return position;
        }

        //------------------------------------------------------------------
        //  local method
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
                read = this.archive.read(this.cache,
                        this.cacheLimit,
                        this.cache.length - this.cacheLimit);//throws IOException

                if (0 < read) this.cacheLimit += read;
            }
        }

    }

    /**
     * LhaFile��archive�� �����ԓ��̃f�[�^�𓾂� InputStream�B
     * �����G���g���𓯎��ɏ������邽�߂� �����������܂ށB
     */
    private class RandomAccessFileInputStream extends InputStream {

        //------------------------------------------------------------------
        //  member values
        //------------------------------------------------------------------
        //  private long position
        //  private long end
        //  private long markPosition
        //------------------------------------------------------------------
        /**
         * archive���̌��ݏ����ʒu
         */
        private long position;

        /**
         * archive���̂���InputStream�̓ǂݎ����E
         */
        private long end;

        /**
         * archive���̃}�[�N�ʒu
         */
        private long markPosition;


        //------------------------------------------------------------------
        //  constructor
        //------------------------------------------------------------------
        //  public RandomAccessFileInputStream( long start, long size )
        //------------------------------------------------------------------

        /**
         * �R���X�g���N�^�B
         *
         * @param start �ǂ݂��݊J�n�ʒu
         * @param size  �f�[�^�̃T�C�Y
         */
        public RandomAccessFileInputStream(long start, long size) {
            this.position = start;
            this.end = start + size;
            this.markPosition = -1;
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
         * archive�̌��ݏ����ʒu���� 1byte�̃f�[�^��ǂݍ��ށB
         *
         * @return �ǂ݂��܂ꂽ1byte�̃f�[�^<br>
         *         ���ɓǂ݂��݌��E�ɒB�����ꍇ�� -1
         *
         * @exception IOException ���o�̓G���[�����������ꍇ
         */
        public int read() throws IOException {
            synchronized (LhaFile.this.archive) {
                if (this.position < this.end) {
                    if (LhaFile.this.LastAccessObject != this)
                        LhaFile.this.archive.seek(this.position);

                    int data = LhaFile.this.archive.read();
                    if (0 <= data) this.position++;
                    return data;
                } else {
                    return -1;
                }
            }
        }

        /**
         * archive�̌��ݏ����ʒu���� buffer�𖞂����悤�Ƀf�[�^��ǂݍ��ށB
         *
         * @param buffer �ǂ݂��܂ꂽ�f�[�^���i�[����o�b�t�@
         *
         * @return �ǂ݂��܂ꂽ�o�C�g��<br>
         *         ���ɓǂ݂��݌��E�ɒB���Ă����ꍇ��-1
         *
         * @exception IOException ���o�̓G���[�����������ꍇ
         */
        public int read(byte[] buffer) throws IOException {
            return this.read(buffer, 0, buffer.length);
        }

        /**
         * archive�̌��ݏ����ʒu���� buffer��index����n�܂�̈��
         * length�o�C�g�̃f�[�^��ǂݍ��ށB
         *
         * @param buffer �ǂ݂��܂ꂽ�f�[�^���i�[����o�b�t�@
         * @param index  buffer���̓ǂ݂��݊J�n�ʒu
         * @param length �ǂ݂��ރo�C�g���B
         *
         * @return �ǂ݂��܂ꂽ�o�C�g��<br>
         *         ���ɓǂ݂��݌��E�ɒB���Ă����ꍇ��-1
         *
         * @exception IOException ���o�̓G���[�����������ꍇ
         */
        public int read(byte[] buffer, int index, int length)
                throws IOException {
            synchronized (LhaFile.this.archive) {
                if (this.position < this.end) {
                    if (LhaFile.this.LastAccessObject != this) {
                        LhaFile.this.archive.seek(this.position);
                        LhaFile.this.LastAccessObject = this;
                    }

                    length = (int) Math.min(this.end - this.position, length);
                    length = LhaFile.this.archive.read(buffer, index, length);
                    if (0 <= length) this.position += length;
                    return length;
                } else {
                    return -1;
                }
            }
        }

        /**
         * length�o�C�g�̃f�[�^��ǂݔ�΂��B
         *
         * @param length �ǂݔ�΂������o�C�g��
         *
         * @return ���ۂɓǂݔ�΂��ꂽ�o�C�g��
         */
        public long skip(long length) {
            synchronized (LhaFile.this.archive) {
                long skiplen = Math.min(this.end - this.position, length);
                this.position += skiplen;

                if (LhaFile.this.LastAccessObject == this)
                    LhaFile.this.LastAccessObject = null;

                return skiplen;
            }
        }

        //------------------------------------------------------------------
        //  method of java.io.InputStream
        //------------------------------------------------------------------
        //  mark/reset
        //------------------------------------------------------------------
        //  public boolean markSupported()
        //  public void mark( int readLimit )
        //  public void reset()
        //------------------------------------------------------------------

        /**
         * ���̃I�u�W�F�N�g��mark/reset���T�|�[�g���邩��Ԃ��B
         *
         * @return ���̃I�u�W�F�N�g��mark/reset���T�|�[�g����B<br>
         *         ���true�B
         */
        public boolean markSupported() {
            return true;
        }

        /**
         * ���ݏ����ʒu�Ƀ}�[�N���{������reset��
         * ���݂̏����ʒu�ɖ߂��悤�ɂ���B
         *
         * @param readLimit �}�[�N�̗L�����E�B
         *                  ���̃I�u�W�F�N�g�ł͈Ӗ��������Ȃ��B
         */
        public void mark(int readLimit) {
            this.markPosition = this.position;
        }

        /**
         * �Ō�Ƀ}�[�N���ꂽ�����ʒu�ɖ߂��B
         *
         * @exception IOException mark()����Ă��Ȃ��ꍇ
         */
        public void reset() throws IOException {
            synchronized (LhaFile.this.archive) {
                if (0 <= this.markPosition) {
                    this.position = this.markPosition;
                } else {
                    throw new IOException("not marked");
                }

                if (LhaFile.this.LastAccessObject == this)
                    LhaFile.this.LastAccessObject = null;
            }
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
         * RandomAccessFileInputStream �ł�
         * �ǂݍ��݂͏�� RandomAccessFile �ɑ΂���
         * �A�N�Z�X�𔺂����߁A���̃��\�b�h�͏�� 0 ��Ԃ��B
         *
         * @return ��� 0<br>
         */
        public int available() {
            return 0;
        }

        /**
         * ���̓��̓X�g���[������A�g�p���Ă����S�Ẵ��\�[�X���J������B<br>
         * ���̃��\�b�h�͉����s��Ȃ��B
         */
        public void close() {
        }

    }

    /**
     * LhaFile �ɂ���S�Ă� LhaHeader ��Ԃ��񋓎q
     */
    private class HeaderEnumeration implements Enumeration {

        //------------------------------------------------------------------
        //  instance field
        //------------------------------------------------------------------
        //  private int index
        //------------------------------------------------------------------
        /**
         * ���ݏ����ʒu
         */
        private int index;

        //------------------------------------------------------------------
        //  constructor
        //------------------------------------------------------------------
        //  public EntryEnumeration()
        //------------------------------------------------------------------

        /**
         * LhaFile �ɂ���S�Ă� LhaHeader ��Ԃ��񋓎q���\�z����B
         */
        public HeaderEnumeration() {
            this.index = 0;
        }

        //------------------------------------------------------------------
        //  method of java.util.Enumeration
        //------------------------------------------------------------------
        //  public boolean hasMoreElements()
        //  public Object nextElement()
        //------------------------------------------------------------------

        /**
         * �񋓎q�ɂ܂��v�f���c���Ă��邩�𓾂�B
         *
         * @return �񋓎q�ɂ܂��v�f���c���Ă���Ȃ� true
         *         �c���Ă��Ȃ���� false
         *
         * @exception IllegalStateException
         *                 �e�� LhaFile ������ꂽ�ꍇ
         */
        public boolean hasMoreElements() {
            if (LhaFile.this.archive != null) {
                return this.index < LhaFile.this.headers.size();
            } else {
                throw new IllegalStateException();
            }
        }

        /**
         * �񋓎q�̎��̗v�f�𓾂�B
         *
         * @return �񋓎q�̎��̗v�f
         *
         * @exception IllegalStateException
         *                 �e�� LhaFile ������ꂽ�ꍇ�B
         * @exception NoSuchElementException
         *                 �񋓎q�ɗv�f�������ꍇ�B
         *
         */
        public Object nextElement() {
            if (LhaFile.this.archive != null) {
                if (this.index < LhaFile.this.headers.size()) {
                    return ((LhaHeader) LhaFile.this.headers.elementAt(this.index++)).clone();
                } else {
                    throw new NoSuchElementException();
                }
            } else {
                throw new IllegalStateException();
            }
        }
    }

}
//end of LhaFile.java
