//start of LhaImmediateOutputStream.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * LhaImmediateOutputStream.java
 * 
 * Copyright (C) 2002  Michel Ishizuka  All rights reserved.
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

package jp.gr.java_conf.dangan.util.lha;

//import classes and interfaces

import java.io.*;
import java.util.Properties;

//import exceptions


/**
 * �ڑ����ꂽRandomAccessFile�� ���k�f�[�^���o�͂��邽�߂̃��[�e�B���e�B�N���X�B<br>
 * java.util.zip.ZipOutputStream �Ǝ����C���^�[�t�F�C�X�����悤�ɍ�����B<br>
 * ���k���s��( ���k��T�C�Y�����k�O�T�C�Y���������ꍇ )�̏�����
 * �蓮�ōs��Ȃ���΂Ȃ�Ȃ��B
 * �ȉ��� ���̂悤�ȃR�[�h�������B
 * <pre>
 * LhaCompressFiles( String arcfile, File[] files ){
 *   LhaImmediateOutputStream lio = new LhaImmediateOutputStream( arcfile );
 * 
 *   for( int i = 0 ; i &lt files.length ; i++ ){
 *     RandomAccessFile raf = new RandomAccessFile( files[i] );
 *     LhaHeader header = new LhaHeader( files[i].getName() );
 *     header.setLastModified( new Date( files.lastModified() ) );
 *     header.setOriginalSize( files.length() );
 *     byte[] buffer  = new byte[8192];
 *     int    length;
 * 
 *     while( 0 &lt= ( length = raf.read( buffer ) ) ){
 *         lio.write( buffer, 0, length );
 *     }
 * <strong>
 *     if( !lio.closeEntry() ){
 *       header.setCompressMethod( CompressMethod.LH0 );
 *       lio.putNextEntry( lhaheader );
 *       raf.seek( 0 );
 *       while( 0 &lt= ( length = raf.read( buffer ) ) ){
 *           lio.write( buffer, 0, length );
 *       }
 *       lio.closeEntry();
 *     }
 * </strong>
 *   lio.close();
 * }
 * </pre>
 * �i���񍐂���������ꍇ�A���̂悤�ȏ������N���X���ɉB������Ɛi���񍐂͉��b�Ԃ�
 * ���ɂ���Ă͉��\�����������Ȃ��Ȃ�B(�Ⴆ�΃M�K�o�C�g���̃f�[�^���������ꍇ)
 * LhaRetainedOutputStream �Ŕ�������A���̂悤�Ȏ��Ԃ�����邽�߂ɐ݌v����Ă���B<br>
 * �܂��AJDK 1.1 �ȑO�ł� RandomAccessFile �� setLength �������Ȃ����߁A
 * ���Ƀf�[�^�̌��ɑ��̃f�[�^������ꍇ�ł��t�@�C���T�C�Y��؂�l�߂邱�Ƃ��o���Ȃ��B<br>
 * ���̖��_�͏�ɃT�C�Y0�̐V�����t�@�C�����J�����ɂ���ĉ�����鎖���ł���B<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: LhaImmediateOutputStream.java,v $
 * Revision 1.2  2002/12/11 02:25:06  dangan
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
 * @author  $Author: dangan $
 * @version $Revision: 1.2 $
 */
public class LhaImmediateOutputStream extends OutputStream{


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
    //  private LhaImmediateOutputStream()
    //  public LhaImmediateOutputStream( String filename )
    //  public LhaImmediateOutputStream( String filename, Properties property )
    //  public LhaImmediateOutputStream( File file )
    //  public LhaImmediateOutputStream( File file, Properties property )
    //  public LhaImmediateOutputStream( RandomAccessFile archive )
    //  public LhaImmediateOutputStream( RandomAccessFile archive, Properties property )
    //  private void constructerHelper( RandomAccesFile archive, Properties property )
    //------------------------------------------------------------------
    /**
     * �f�t�H���g�R���X�g���N�^
     * �g�p�s��
     */
    private LhaImmediateOutputStream(){ }

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
    public LhaImmediateOutputStream( String filename ) 
                                                throws FileNotFoundException {

        if( filename != null ){
            RandomAccessFile file = new RandomAccessFile( filename, "rw" );     //throws FileNotFoundException, SecurityException
            Properties property   = LhaProperty.getProperties();
        
            this.constructerHelper( file, property );
        }else{
            throw new NullPointerException( "filename" );
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
    public LhaImmediateOutputStream( String filename, Properties property )
                                                  throws FileNotFoundException {

        if( filename != null ){
            RandomAccessFile file = new RandomAccessFile( filename, "rw" );     //throws FileNotFoundException, SecurityException
            this.constructerHelper( file, property );
        }else{
            throw new NullPointerException( "filename" );
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
    public LhaImmediateOutputStream( File filename ) throws IOException {

        if( filename != null ){
            RandomAccessFile file = new RandomAccessFile( filename, "rw" );     //throws FileNotFoundException, SecurityException, IOException(jdk1.2)
            Properties property   = LhaProperty.getProperties();
        
            this.constructerHelper( file, property );
        }else{
            throw new NullPointerException( "filename" );
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
    public LhaImmediateOutputStream( File filename, Properties property )
                                                  throws IOException {

        if( filename != null ){
            RandomAccessFile file = new RandomAccessFile( filename, "rw" );     //throws FileNotFoundException, SecurityException, IOException(jdk1.2)
            this.constructerHelper( file, property );
        }else{
            throw new NullPointerException( "filename" );
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
    public LhaImmediateOutputStream( RandomAccessFile file ){

        if( file != null ){
            Properties property   = LhaProperty.getProperties();
            this.constructerHelper( file, property );
        }else{
            throw new NullPointerException( "out" );
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
    public LhaImmediateOutputStream( RandomAccessFile file, 
                                     Properties       property ){

        if( file != null
         && property != null ){

            this.constructerHelper( file, property );                           //throws UnsupportedEncodingException

        }else if( file == null ){
            throw new NullPointerException( "null" );
        }else{
            throw new NullPointerException( "property" );
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
    private void constructerHelper( RandomAccessFile file, 
                                    Properties       property ){

        this.archive   = file;

        this.out       = null;
        this.header    = null;
        this.headerpos = -1;
        this.crc       = new CRC16();
        this.property  = property;
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
    public void write( int data ) throws IOException {
        if( this.out != null ){
            if( this.header != null ){
                crc.update( data );
            }

            this.out.write( data );
        }else{
            throw new IOException( "no entry" );
        }
    }

    /**
     * ���݂̃G���g���� buffer�̓��e��S�ď����o���B
     * 
     * @param buffer �����o���f�[�^�̓������o�C�g�z��
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ�B
     */
    public void write( byte[] buffer ) throws IOException {
        this.write( buffer, 0, buffer.length );
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
    public void write( byte[] buffer, int index, int length ) throws IOException {
        if( this.out != null ){
            if( this.header != null ){
                crc.update( buffer, index, length );
            }

            this.out.write( buffer, index, length );
        }else{
            throw new IOException( "no entry" );
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
        if( this.out != null ){
            this.out.flush();                                                   //throws IOException
        }else{
            throw new IOException( "no entry" );
        }
    }

    /**
     * �o�͐�ɑS�Ẵf�[�^���o�͂��A�X�g���[�������B<br>
     * �܂��A�g�p���Ă����S�Ẵ��\�[�X���������B
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void close() throws IOException {
        if( this.out != null ){
            this.closeEntry();                                                  //throws IOException
        }

        //�^�[�~�l�[�^���o��
        this.archive.write( 0 );                                                //throws IOException
        try{
            this.archive.setLength( this.archive.getFilePointer() );            //After Java1.2 throws IOException
        }catch( NoSuchMethodError error ){
        }
        this.archive.close();                                                   //throws IOException
        this.archive = null;

        this.crc      = null;
        this.property = null;
        this.encoding = null;
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
    public void putNextEntry( LhaHeader header ) throws IOException {
        if( header.getCompressedSize() == LhaHeader.UNKNOWN
         || header.getCRC()            == LhaHeader.UNKNOWN ){
            this.putNextEntryNotYetCompressed( header );                        //throws IOException
        }else{
            this.putNextEntryAlreadyCompressed( header );                       //throws IOException
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
     * @exception IllegalStateException
     *               �ȑO�̃G���g���������� closeEntry() ����Ă��Ȃ��ꍇ
     */
    public void putNextEntryAlreadyCompressed( LhaHeader header )
                                                            throws IOException {

        if( this.out == null ){

            if( header.getOriginalSize()   != LhaHeader.UNKNOWN
             && header.getCompressedSize() != LhaHeader.UNKNOWN
             && header.getCRC()            != LhaHeader.UNKNOWN ){

                this.headerpos = this.archive.getFilePointer();

                this.encoding = this.property.getProperty( "lha.encoding" );
                if( this.encoding == null ){
                    this.encoding = LhaProperty.getProperty( "lha.encoding" );
                }

                this.archive.write( header.getBytes( encoding ) );                  //throws IOException
                this.out = new RandomAccessFileOutputStream( this.archive, header.getCompressedSize() );

            }else if( header.getOriginalSize() == LhaHeader.UNKNOWN ){
                throw new IllegalArgumentException( "OriginalSize must not \"LhaHeader.UNKNOWN\"." );
            }else if( header.getCompressedSize() == LhaHeader.UNKNOWN ){
                throw new IllegalArgumentException( "CompressedSize must not \"LhaHeader.UNKNOWN\"." );
            }else{
                throw new IllegalArgumentException( "CRC must not \"LhaHeader.UNKNOWN\"." );
            }

        }else{
            throw new IllegalStateException( "entry is not closed." );
        }
    }

    /**
     * �����Ɉ��k����Ă��Ȃ��G���g�����������ނ悤�ɃX�g���[����
     * �ݒ肷��Bheader �� CompressedSize,CRC���w�肳��Ă��Ă���
     * �������B���̃��\�b�h�ɓn����� header �ɂ�
     * LhaHeader.setOriginalSize() ��p���� ���m�ȃI���W�i���T�C�Y
     * ���w�肳��Ă���K�v������B
     * 
     * @param header �������ރG���g���ɂ��Ă̏�������
     *               LhaHeader�̃C���X�^���X�B
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     * @exception IllegalArgumentException
     *                        header.getOriginalSize() ��
     *                        LhaHeader.UNKNOWN ��Ԃ����ꍇ
     * @exception IllegalStateException
     *                        �ȑO�̃G���g����������
     *                        closeEntry() ����Ă��Ȃ��ꍇ
     */
    public void putNextEntryNotYetCompressed( LhaHeader header ) 
                                                        throws IOException {

        if( out == null ){

            if( header.getOriginalSize() != LhaHeader.UNKNOWN ){

                this.crc.reset();
                this.headerpos = this.archive.getFilePointer();
                this.header    = (LhaHeader)header.clone();
                this.header.setCompressedSize( 0 );
                this.header.setCRC( 0 );

                this.encoding = this.property.getProperty( "lha.encoding" );
                if( this.encoding == null ){
                    this.encoding = LhaProperty.getProperty( "lha.encoding" );
                }

                this.archive.write( this.header.getBytes( encoding ) );
                this.out = new RandomAccessFileOutputStream( this.archive, header.getOriginalSize() );
                this.out = CompressMethod.connectEncoder( this.out, 
                                                          header.getCompressMethod(), 
                                                          this.property  );

            }else{
                throw new IllegalArgumentException( "OriginalSize must not \"LhaHeader.UNKNOWN\"." );
            }

        }else{
            throw new IllegalStateException( "entry is not closed." );
        }
    }

    /**
     * ���ݏo�͒��̃G���g������A���̃G���g�����o�͉\�ȏ�Ԃɂ���B<br>
     * putNextEntryNotYetCompressed() �ŊJ�����G���g�������ꍇ
     * ���̃��\�b�h�͈��k�Ɏ��s����(���k��T�C�Y�����k�O�T�C�Y��������)�ꍇ�A
     * �G���g���S�̂��������ݐ� �� RandomAccessFile ����폜����B<br>
     * ���̍폜�����͒P�� �t�@�C���|�C���^�� �G���g���J�n�ʒu�܂Ŋ����߂������Ȃ̂�
     * RandomAccessFile �� setLength() ������ jdk1.1 �ȑO�ł� 
     * �G���g���𖳈��k(�������͑��̈��k�@)�ōďo�͂��Ȃ��ꍇ�A
     * ���Ƀf�[�^�̏I�[�ȍ~�Ɉ��k�Ɏ��s�����s���S�ȃf�[�^���c�����܂܂ɂȂ�B<br>
     * 
     * @return �G���g�����o�͂��ꂽ�ꍇ�� true�A
     *         ���k�O�������k��̕����T�C�Y���傫���Ȃ������߁A
     *         �G���g�����폜���ꂽ�ꍇ�� false�B
     *         �܂��A���ݏ������̃G���g�������������ꍇ�� true ��Ԃ��B
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public boolean closeEntry() throws IOException {
        if( this.out != null ){

            this.out.close();
            if( this.header != null ){

                long pos = this.archive.getFilePointer();
                long size = ( pos - this.headerpos
                                  - this.header.getBytes( this.encoding ).length );

                this.header.setCompressedSize( size );
                if( this.header.getCRC() != LhaHeader.NO_CRC ){
                    this.header.setCRC( (int)this.crc.getValue() );
                }

                this.archive.seek( this.headerpos );
                if( this.header.getCompressMethod().equals( CompressMethod.LH0 )
                 || this.header.getCompressMethod().equals( CompressMethod.LHD )
                 || this.header.getCompressMethod().equals( CompressMethod.LZ4 )
                 || this.header.getCompressedSize() < this.header.getOriginalSize() ){

                    this.archive.write( this.header.getBytes( this.encoding ) );
                    this.archive.seek( pos );
                    this.header = null;
                    this.out    = null;
                    return true;
                }else{
                    this.header = null;
                    this.out    = null;
                    return false;
                }
            }else{
                this.out    = null;
                return true;
            }
        }else{
            return true;
        }
    }


    //------------------------------------------------------------------
    //  inner classes
    //------------------------------------------------------------------
    //  private static class RandomAccessFileOutputStream
    //------------------------------------------------------------------
    /**
     * RandomAccessFile��OutputStream�̃C���^�t�F�C�X�ɍ��킹�邽�߂̃��b�p�N���X
     */
    private static class RandomAccessFileOutputStream extends OutputStream {

        //------------------------------------------------------------------
        //  instance field
        //------------------------------------------------------------------
        //  sink
        //------------------------------------------------------------------
        //  private RandomAccessFile archive
        //------------------------------------------------------------------
        /**
         * �o�͐�RandomAccessFile
         */
        private RandomAccessFile archive;

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
        public RandomAccessFileOutputStream( RandomAccessFile archive,
                                             long length ) throws IOException {
            this.archive = archive;
            this.pos     = this.archive.getFilePointer();                       //throws IOException
            this.limit   = this.pos + length;
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
         * �ڑ����ꂽRandomAccessFile��1�o�C�g�������ށB<br>
         * �R���X�g���N�^�ɓn���ꂽ���E�𒴂��ď����������Ƃ����ꍇ��
         * �����s��Ȃ��B
         * 
         * @param data ��������1byte�̃f�[�^
         * 
         * @exception IOException  ���o�̓G���[�����������ꍇ
         */
        public void write( int data ) throws IOException {
            if( this.pos < this.limit ){
                this.pos++;
                this.archive.write( data );                                     //throws IOException
            }
        }

        /**
         * �ڑ����ꂽRandomAccessFile��buffer�̓��e��S�ď������ށB
         * �R���X�g���N�^�ɓn���ꂽ���E�𒴂��ď����������Ƃ����ꍇ��
         * �����s��Ȃ��B
         * 
         * @param buffer �������ރf�[�^�̓������o�C�g�z��
         * 
         * @exception IOException  ���o�̓G���[�����������ꍇ
         */
        public void write( byte[] buffer ) throws IOException {
            this.write( buffer, 0, buffer.length );                             //throws IOException
        }

        /**
         * �ڑ����ꂽRandomAccessFile��buffer�̓��e��index���� length�o�C�g�������ށB
         * �R���X�g���N�^�ɓn���ꂽ���E�𒴂��ď����������Ƃ����ꍇ��
         * �����s��Ȃ��B
         * 
         * @param buffer �������ރf�[�^�̓������o�C�g�z��
         * @param index  buffer���̏������ރf�[�^�̊J�n�ʒu
         * @param length �������ރf�[�^��
         * 
         * @exception IOException  ���o�̓G���[�����������ꍇ
         */
        public void write( byte[] buffer, int index, int length )
                                                        throws IOException {

            if( this.limit < this.pos + length ){
                length = (int)Math.max( this.limit - this.pos, 0 );
            }
            this.archive.write( buffer, index, length );                        //throws IOException
            this.pos += length;
        }

        //------------------------------------------------------------------
        //  method of java.io.OutputStream
        //------------------------------------------------------------------
        //  public void close()
        //------------------------------------------------------------------
        /**
         * ���̃X�g���[������� �g�p���Ă������\�[�X���J������B<br>
         */
        public void close(){
            this.archive = null;
        }

    }

}
//end of LhaImmediateOutputStream.java
