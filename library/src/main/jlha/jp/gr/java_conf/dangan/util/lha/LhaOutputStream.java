//start of LhaOutputStream.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * LhaOutputStream.java
 * <p>
 * Copyright (C) 2001-2002 Michel Ishizuka  All rights reserved.
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

import jp.gr.java_conf.dangan.io.GrowthByteBuffer;

import java.io.*;
import java.util.Properties;

//import exceptions


/**
 * �ڑ����ꂽ�X�g���[���� ���k�f�[�^���o�͂��邽�߂̃��[�e�B���e�B�N���X�B<br>
 * java.util.zip.ZipOutputStream �Ǝ����C���^�[�t�F�C�X�����悤�ɍ�����B
 * Zip�ƈႢ�ALHA�̏o�͖͂{�� 2�p�X�ł��邽�߁A1�̃G���g�������k����܂ŁA
 * �G���g���S�̂̃f�[�^�����ꎞ�L���̈悪�K�v�ƂȂ�B
 * ���̂悤�ȋL���̈���g�p�������Ȃ��ꍇ�� LhaRetainedOutputStream ��
 * LhaImmediateOutputStream ���g�p���鎖�B<br>
 *
 * <pre>
 * -- revision history --
 * $Log: LhaOutputStream.java,v $
 * Revision 1.1.2.2  2005/05/03 07:48:40  dangan
 * [bug fix]
 *     ���k�@���ʎq -lhd- ���w�肵�����A���k��T�C�Y���I���W�i���T�C�Y�������Ȃ����߁A
 *     �K�� -lh0- �ɍĐݒ肳��Ă����B���̂��߃f�B���N�g�������i�[�ł��Ȃ������B
 *
 * Revision 1.1.2.1  2005/04/29 02:14:28  dangan
 * [bug fix]
 *     ���k�@���ʎq -lhd- ���w�肵�����A���k��T�C�Y���I���W�i���T�C�Y�������Ȃ����߁A
 *     �K�� -lh0- �ɍĐݒ肳��Ă����B���̂��߃f�B���N�g�������i�[�ł��Ȃ������B
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
 * @version $Revision: 1.1.2.2 $
 */
public class LhaOutputStream extends OutputStream {


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  sink
    //------------------------------------------------------------------
    //  private OutputStream out
    //------------------------------------------------------------------
    /**
     * ���k�f�[�^���o�͂���X�g���[��
     */
    private OutputStream out;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  to compress a file
    //------------------------------------------------------------------
    //  private CRC16 crc
    //  private Temporary temp
    //  private LhaHeader header
    //  private OutputStream tempOut
    //  private long length
    //------------------------------------------------------------------
    /**
     * CRC16�l�Z�o�p�N���X
     */
    private CRC16 crc;

    /**
     * �ꎞ�L���p�I�u�W�F�N�g
     */
    private Temporary temp;

    /**
     * ���݈��k���̃G���g���̃w�b�_
     */
    private LhaHeader header;

    /**
     * ���݈��k���̃G���g���̈��k�p�o�̓X�g���[��
     */
    private OutputStream tempOut;

    /**
     * ���݈��k���G���g���̈��k�O�̃f�[�^�̒���
     */
    private long length;


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
    //  private LhaOutputStream()
    //  public LhaOutputStream( OutputStream out )
    //  public LhaOutputStream( OutputStream out, Properties property )
    //  public LhaOutputStream( OutputStream out, RandomAccessFile file )
    //  public LhaOutputStream( OutputStream out, RandomAccessFile file,
    //                          Properties property )
    //  private void constructerHelper( OutputStream out, Temporary temp,
    //                          Properties property )
    //------------------------------------------------------------------

    /**
     * �f�t�H���g�R���X�g���N�^
     * �g�p�s��
     */
    private LhaOutputStream() {
    }

    /**
     * out �� ���k�f�[�^���o�͂���OutputStream���\�z����B<br>
     * �ꎞ�ޔ��@�\�̓��������g�p����B���̂��߁A
     * ���k���f�[�^�ʂ��������ʂ𒴂���悤�ȃt�@�C���͈��k�ł��Ȃ��B<br>
     * �e���k�`���ɑΉ�����������̐������������v���p�e�B�ɂ�
     * LhaProperty.getProperties() �œ���ꂽ�v���p�e�B���g�p�����B<br>
     *
     * @param out ���k�f�[�^���o�͂���X�g���[��
     *
     * @see LhaProperty#getProperties()
     */
    public LhaOutputStream(OutputStream out) {

        if (out != null) {

            Properties property = LhaProperty.getProperties();
            this.constructerHelper(out, new TemporaryBuffer(), property);         //throws UnsupportedEncodingException

        } else {
            throw new NullPointerException("out");
        }
    }

    /**
     * out �� ���k�f�[�^���o�͂���OutputStream���\�z����B<br>
     * �ꎞ�ޔ��@�\�̓��������g�p����B���̂��߁A
     * ���k���f�[�^�ʂ��������ʂ𒴂���悤�ȃt�@�C���͈��k�ł��Ȃ��B<br>
     *
     * @param out      ���k�f�[�^���o�͂���X�g���[��
     * @param property �e���k�`���ɑΉ�����������̐����������܂܂��v���p�e�B
     *
     * @see LhaProperty
     */
    public LhaOutputStream(OutputStream out, Properties property) {

        if (out != null
                && property != null) {

            this.constructerHelper(out, new TemporaryBuffer(), property);         //throws UnsupportedEncodingException

        } else if (out == null) {
            throw new NullPointerException("out");
        } else {
            throw new NullPointerException("property");
        }
    }

    /**
     * out �� ���k�f�[�^���o�͂���OutputStream���\�z����B<br>
     * �e���k�`���ɑΉ�����������̐������������v���p�e�B�ɂ�
     * LhaProperty.getProperties() �œ���ꂽ�v���p�e�B���g�p�����B<br>
     *
     * @param out   ���k�f�[�^���o�͂���X�g���[��
     * @param file  RandomAccessFile �̃C���X�^���X�B<br>
     *          <ul>
     *            <li>���� close() ����Ă��Ȃ����B
     *            <li>�R���X�g���N�^�� mode �ɂ� "rw" �I�v�V�������g�p���āA
     *                �ǂ݂��݂Ə������݂��o����悤�ɐ������ꂽ�C���X�^���X�ł��邱�ƁB
     *          </ul>
     *          �̏����𖞂������́B
     *
     * @see LhaProperty#getProperties()
     */
    public LhaOutputStream(OutputStream out, RandomAccessFile file) {

        if (out != null
                && file != null) {

            Properties property = LhaProperty.getProperties();
            this.constructerHelper(out, new TemporaryFile(file), property); //throws UnsupportedEncodingException

        } else if (out == null) {
            throw new NullPointerException("out");
        } else {
            throw new NullPointerException("file");
        }
    }

    /**
     * out �� ���k�f�[�^���o�͂���OutputStream���\�z����B<br>
     *
     * @param out      ���k�f�[�^���o�͂���X�g���[��
     * @param file     RandomAccessFile �̃C���X�^���X�B<br>
     *            <ul>
     *              <li>���� close() ����Ă��Ȃ����B
     *              <li>�R���X�g���N�^�� mode �ɂ� "rw" �I�v�V�������g�p���āA
     *                  �ǂ݂��݂Ə������݂��o����悤�ɐ������ꂽ�C���X�^���X�ł��邱�ƁB
     *            </ul>
     *            �̏����𖞂������́B
     * @param property �e���k�`���ɑΉ�����������̐����������܂܂��v���p�e�B
     *
     * @exception UnsupportedEncodingException
     *               encode ���T�|�[�g����Ȃ��ꍇ
     *
     * @see LhaProperty
     */
    public LhaOutputStream(OutputStream out,
                           RandomAccessFile file,
                           Properties property) {

        if (out != null
                && file != null
                && property != null) {

            this.constructerHelper(out, new TemporaryFile(file), property);     //throws UnsupportedEncodingException

        } else if (out == null) {
            throw new NullPointerException("out");
        } else if (file == null) {
            throw new NullPointerException("file");
        } else {
            throw new NullPointerException("property");
        }
    }

    /**
     * �R���X�g���N�^�̏�����������S�����郁�\�b�h�B
     *
     * @param out    LHA���Ɍ`���̃f�[�^���o�͂���o�̓X�g���[��
     * @param temp   ���k�f�[�^�̈ꎞ�ޔ��@�\
     * @param encode �w�b�_���̕������ϊ�����̂Ɏg�p����
     *               �G���R�[�h���{�ł� �V�t�gJIS(SJIS,MS932,
     *               CP932��)���g�p���鎖
     *
     * @exception UnsupportedEncodingException
     *               encode ���T�|�[�g����Ȃ��ꍇ
     */
    private void constructerHelper(OutputStream out,
                                   Temporary temp,
                                   Properties property) {
        this.out = out;
        this.temp = temp;
        this.property = property;

        this.crc = new CRC16();
        this.header = null;
        this.tempOut = null;
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
     * ���݂̃G���g����1�o�C�g�̃f�[�^���������ށB<br>
     *
     * @param data �������ރf�[�^
     *
     * @exception IOException ���o�̓G���[�����������ꍇ�B
     */
    public void write(int data) throws IOException {
        if (this.tempOut != null) {
            if (this.header != null) {
                crc.update(data);
            }

            this.tempOut.write(data);
            this.length++;
        } else {
            throw new IOException("no entry");
        }
    }

    /**
     * ���݂̃G���g���� buffer�̓��e��S�ď����o���B<br>
     *
     * @param buffer �����o���f�[�^�̓������o�C�g�z��
     *
     * @exception IOException ���o�̓G���[�����������ꍇ�B
     */
    public void write(byte[] buffer) throws IOException {
        this.write(buffer, 0, buffer.length);
    }

    /**
     * ���݂̃G���g���� buffer�� index���� length�o�C�g�̃f�[�^�������o���B<br>
     *
     * @param buffer �����o���f�[�^�̓������o�C�g�z��
     * @param index  buffer���̏����o���ׂ��f�[�^�̊J�n�ʒu
     * @param length �f�[�^�̃o�C�g��
     *
     * @exception IOException ���o�̓G���[�����������ꍇ�B
     */
    public void write(byte[] buffer, int index, int length) throws IOException {
        if (this.tempOut != null) {
            if (this.header != null) {
                crc.update(buffer, index, length);
            }

            this.tempOut.write(buffer, index, length);
            this.length += length;
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
     * flush �͓�̓�����s���B
     * ��͌��ݏ������ݒ��̃G���g���̃f�[�^�� 
     * �ꎞ�ޔ��@�\�ɑ��肱�ނ悤�Ɏw������B
     * ����� PostLzssDecoder�ALzssOutputStream 
     * �̋K��ǂ��� flush() ���Ȃ������ꍇ��
     * �����f�[�^���o�͂���鎖��ۏ؂��Ȃ��B
     * ������� ���ۂ̏o�͐�� flush() ����B
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     *
     * @see PostLzssEncoder#flush()
     * @see LzssOutputStream#flush()
     */
    public void flush() throws IOException {
        if (this.tempOut != null) {
            this.tempOut.flush();                                               //throws IOException
        }

        if (this.tempOut != this.out) {
            this.out.flush();                                                   //throws IOException
        }
    }

    /**
     * �o�͐�ɑS�Ẵf�[�^���o�͂��A
     * �X�g���[�������B
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void close() throws IOException {
        if (this.tempOut != null) {
            this.closeEntry();                                                  //throws IOException
        }

        //�^�[�~�l�[�^���o��
        this.out.write(0);                                                    //throws IOException
        this.out.close();                                                       //throws IOException
        this.out = null;

        this.temp.close();
        this.temp = null;

        this.property = null;
        this.crc = null;
        this.header = null;
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
     *   <li>header.getOriginalSize()<br>
     *   <li>header.getCRC()<br>
     * </ul>
     * �̂ǂꂩ��ł� LhaHeader.UNKNOWN �ł���Ζ����Ɉ��k����Ă��Ȃ��Ƃ���B
     *
     * @param header �������ރG���g���ɂ��Ă̏�������
     *               LhaHeader�̃C���X�^���X�B
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void putNextEntry(LhaHeader header) throws IOException {
        if (header.getCompressedSize() == LhaHeader.UNKNOWN
                || header.getOriginalSize() == LhaHeader.UNKNOWN
                || header.getCRC() == LhaHeader.UNKNOWN) {
            this.putNextEntryNotYetCompressed(header);                        //throws IOException
        } else {
            this.putNextEntryAlreadyCompressed(header);                       //throws IOException
        }
    }

    /**
     * ���Ɉ��k�ς݂̃G���g�����������ނ悤�ɃX�g���[����ݒ肷��B<br>
     * ���k�ς݂Ȃ̂ŁA�ꎞ�ޔ��@�\���o���ɒ��ڏo�͐�ɏo�͂����B
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
    public void putNextEntryAlreadyCompressed(LhaHeader header) throws IOException {
        if (header.getOriginalSize() != LhaHeader.UNKNOWN
                && header.getCompressedSize() != LhaHeader.UNKNOWN
                && header.getCRC() != LhaHeader.UNKNOWN) {

            if (this.tempOut != null) {
                this.closeEntry();                                              //throws IOException
            }

            String encoding = this.property.getProperty("lha.encoding");
            if (encoding == null) {
                encoding = LhaProperty.getProperty("lha.encoding");
            }
            this.out.write(header.getBytes(encoding));                      //throws IOException
            this.tempOut = out;


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
     * header �� OriginalSize, CompressedSize, CRC���w�肳��Ă��Ă����������B
     *
     * @param header �������ރG���g���ɂ��Ă̏�������
     *               LhaHeader�̃C���X�^���X�B
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void putNextEntryNotYetCompressed(LhaHeader header) throws IOException {
        if (this.tempOut != null) {
            this.closeEntry();                                                  //throws IOException
        }

        this.crc.reset();
        this.length = 0;
        this.header = (LhaHeader) header.clone();
        this.tempOut = CompressMethod.connectEncoder(this.temp.getOutputStream(),
                header.getCompressMethod(),
                this.property);
    }

    /**
     * ���ݏo�͒��̃G���g������A���̃G���g�����o�͉\�ȏ�Ԃɂ���B
     * ���k�Ɏ��s����(���k��T�C�Y�����k�O�T�C�Y��������)�ꍇ�A
     * �𓀂������k�Ŋi�[����B�G���g���̃T�C�Y���傫���ꍇ�A
     * ���̏����ɂ͂��Ȃ�̎��Ԃ�������B
     *
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void closeEntry() throws IOException {
        if (this.header != null) {
            this.tempOut.close();
            InputStream in;

            if (this.temp.length() < this.length) {
                this.header.setOriginalSize(this.length);
                this.header.setCompressedSize(this.temp.length());
                this.header.setCRC((int) crc.getValue());

                in = this.temp.getInputStream();                                //throws IOException
            } else {
                String method = this.header.getCompressMethod();

                this.header.setOriginalSize(this.length);
                this.header.setCompressedSize(this.length);
                this.header.setCRC((int) crc.getValue());
                if (!this.header.getCompressMethod().equalsIgnoreCase(CompressMethod.LHD)) {
                    this.header.setCompressMethod(CompressMethod.LH0);
                }

                in = this.temp.getInputStream();                                //throws IOException
                in = CompressMethod.connectDecoder(in,
                        method,
                        this.property,
                        this.temp.length());
            }

            String encoding = this.property.getProperty("lha.encoding");
            if (encoding == null) {
                encoding = LhaProperty.getProperty("lha.encoding");
            }
            this.out.write(this.header.getBytes(encoding));                 //throws UnsupportedEncodingException, IOException

            byte[] buffer = new byte[8192];
            int length;
            while (0 <= (length = in.read(buffer))) {                       //throws IOException
                this.out.write(buffer, 0, length);                            //throws IOException
            }
        }
        this.header = null;
        this.tempOut = null;
    }


    //------------------------------------------------------------------
    //  inner class
    //------------------------------------------------------------------
    //  private static interface Temporary
    //  private static class TemporaryFile
    //  private static class TemporaryBuffer
    //------------------------------------------------------------------

    /**
     * �f�[�^�̈ꎞ�ޔ��@�\��񋟂���B
     */
    private static interface Temporary {

        //------------------------------------------------------------------
        //  original method
        //------------------------------------------------------------------
        //  public abstract InputStream getInputStream()
        //  public abstract OutputStream getOutputStream()
        //  public abstract long length()
        //  public abstract void close()
        //------------------------------------------------------------------

        /**
         * �ꎞ�ޔ��@�\�ɒ�����ꂽ�f�[�^�����o��InputStream �𓾂�B<br>
         * ���̃f�[�^�͒��O�� getOutputStream() �ŗ^������ 
         * OutputStream �ɏo�͂��ꂽ�f�[�^�Ɠ����ł���B<br>
         * getInputStream() �œ���ꂽ InputStream �� close() �����܂ŁA
         * getOutputStream() ���Ă�ł͂Ȃ�Ȃ��B<br>
         * �܂��AgetInputStream() �œ���ꂽ InputStream �� close() �����܂ŁA
         * �Ă� getInputStream() ���Ă�ł͂Ȃ�Ȃ��B<br>
         *
         * @return �ꎞ�ޔ��@�\����f�[�^�����o�� InputStream
         *
         * @exception IOException ���o�̓G���[�����������ꍇ
         */
        public abstract InputStream getInputStream() throws IOException;

        /**
         * �f�[�^���ꎞ�ޔ��@�\�ɒ�����OutputStream �𓾂�B<br>
         * �������f�[�^�͒���� getInputStream() �œ�����
         * InputStream ���瓾�鎖���o����B<br>
         * getOutputStream �œ���ꂽ OutputStream �� close() �����܂ŁA
         * getInputStream() ���Ă�ł͂Ȃ�Ȃ��B
         * �܂��AgetOutputStream() �œ���ꂽ OutputStream �� close() �����܂ŁA
         * �Ă� getOutputStream() ���Ă�ł͂Ȃ�Ȃ��B<br>
         *
         * @return �f�[�^���ꎞ�ޔ��@�\�ɒ����� OutputStream
         *
         * @exception IOException ���o�̓G���[�����������ꍇ
         */
        public abstract OutputStream getOutputStream() throws IOException;

        /**
         * �ꎞ�ޔ��@�\�Ɋi�[����Ă���f�[�^�ʂ𓾂�B
         * ����� ���O�� getOutputStream() �ŗ^����ꂽ
         * OutputStream �ɏo�͂��ꂽ�f�[�^�ʂƓ����ł���B
         *
         * @return �ꎞ�ޔ��@�\�Ɋi�[����Ă���f�[�^��
         */
        public abstract long length() throws IOException;

        /**
         * �ꎞ�ޔ��@�\�Ŏg�p����Ă����A�S�ẴV�X�e�����\�[�X���J������B
         *
         * @exception IOException ���o�̓G���[�����������ꍇ
         */
        public abstract void close() throws IOException;

    }

    /**
     * �ꎞ�ޔ��@�\�� RandomAccessFile ���g�p����N���X�B
     */
    private static class TemporaryFile implements Temporary {

        //------------------------------------------------------------------
        //  instance field
        //------------------------------------------------------------------
        //  private RandomAccessFile tempfile
        //  private long length
        //------------------------------------------------------------------
        /**
         * �ꎞ�ޔ��@�\�Ɏg�p���� RandomAccessFile
         */
        private RandomAccessFile tempfile;

        /**
         * getOutputStream �ŗ^���� OutputStream �ɏo�͂��ꂽ�f�[�^�ʂ�ێ�����B
         */
        private long length;

        //------------------------------------------------------------------
        //  constructor
        //------------------------------------------------------------------
        //  public TemporaryFile( RandomAccessFile file )
        //------------------------------------------------------------------

        /**
         * �R���X�g���N�^ file���g�p���� TemporaryFile ���\�z����B
         *
         * @param file RandomAccessFile �̃C���X�^���X
         */
        public TemporaryFile(RandomAccessFile file) {
            if (file != null) {
                this.tempfile = file;
            } else {
                throw new NullPointerException("file");
            }
        }

        //------------------------------------------------------------------
        //  method of Temporary
        //------------------------------------------------------------------
        //  public InputStream getInputStream()
        //  public OutputStream getOutputStream()
        //  public long length()
        //  public void close()
        //------------------------------------------------------------------

        /**
         * �ꎞ�ޔ��@�\�ɒ�����ꂽ�f�[�^�����o�� InputStream �𓾂�B<br>
         * ���̃f�[�^�͒��O�� getOutputStream() �ŗ^������ 
         * OutputStream �ɏo�͂��ꂽ�f�[�^�Ɠ����B<br>
         *
         * @return �ꎞ�ޔ��@�\����f�[�^�����o�� InputStream
         *
         * @exception IOException ���o�̓G���[�����������ꍇ
         */
        public InputStream getInputStream() throws IOException {
            return new TemporaryFileInputStream();
        }

        /**
         * �f�[�^���ꎞ�ޔ��@�\�ɒ�����OutputStream�𓾂�B<br>
         * �������f�[�^�͒���� getInputStream() ��
         * ������ InputStream ���瓾�鎖���o����B<br>
         *
         * @return �f�[�^���ꎞ�ޔ��@�\�ɒ����� OutputStream
         *
         * @exception IOException ���o�̓G���[�����������ꍇ
         */
        public OutputStream getOutputStream() throws IOException {
            return new TemporaryFileOutputStream();
        }

        /**
         * �ꎞ�ޔ��@�\�Ɋi�[����Ă���f�[�^�ʂ𓾂�B<br>
         * ����� ���O�� getOutputStream() �ŗ^����ꂽ
         * OutputStream �ɏo�͂��ꂽ�f�[�^�ʂƓ����ł���B<br>
         *
         * @return �ꎞ�ޔ��@�\�Ɋi�[����Ă���f�[�^��
         */
        public long length() {
            return this.length;
        }

        /**
         * �ꎞ�ޔ��@�\�Ŏg�p����Ă����A�S�ẴV�X�e�����\�[�X���J������B
         * �R���X�g���N�^�ŗ^����ꂽ RandomAccessFile �͕�����B
         *
         * @exception IOException ���o�̓G���[�����������ꍇ
         */
        public void close() throws IOException {
            this.tempfile.close(); //throws IOException
            this.tempfile = null;
        }

        //------------------------------------------------------------------
        //  inner classes
        //------------------------------------------------------------------
        //  private class TemporaryFileInputStream
        //  private class TemporaryFileOutputStream
        //------------------------------------------------------------------

        /**
         * TemporaryFile �̓��̓X�g���[��
         */
        private class TemporaryFileInputStream extends InputStream {

            //------------------------------------------------------------------
            //  constructor
            //------------------------------------------------------------------
            //  public TemporaryFileInputStream()
            //------------------------------------------------------------------

            /**
             * TemporaryFile ����f�[�^��ǂݍ��� InputStream ���\�z����B<br>
             *
             * @exception IOException ���o�̓G���[�����������ꍇ
             */
            public TemporaryFileInputStream() throws IOException {
                TemporaryFile.this.tempfile.seek(0);                          //throws IOException
            }

            //------------------------------------------------------------------
            //  method of java.io.InputStream
            //------------------------------------------------------------------
            //  public int read()
            //  public int read( byte[] buffer )
            //  public int read( byte[] buffer, int index, int length )
            //------------------------------------------------------------------

            /**
             * TemporaryFile���� 1�o�C�g�̃f�[�^��ǂݍ��ށB
             *
             * @return �ǂ݂��܂ꂽ1�o�C�g�̃f�[�^
             *         ����EndOfStream�ɒB���Ă���ꍇ��-1
             *
             * @exception IOException ���o�̓G���[�����������ꍇ
             */
            public int read() throws IOException {
                long pos = TemporaryFile.this.tempfile.getFilePointer();      //throws IOException
                long limit = TemporaryFile.this.length;

                if (pos < limit) {
                    return TemporaryFile.this.tempfile.read();                  //throws IOException
                } else {
                    return -1;
                }
            }

            /**
             * TemporaryFile���� buffer�𖞂����悤�Ƀf�[�^��ǂݍ��ށB
             *
             * @param buffer �f�[�^��ǂݍ��ރo�b�t�@
             *
             * @return �ǂ݂��܂ꂽ�f�[�^��
             *         ����EndOfStream�ɒB���Ă���ꍇ��-1
             *
             * @exception IOException ���o�̓G���[�����������ꍇ
             */
            public int read(byte[] buffer) throws IOException {
                return this.read(buffer, 0, buffer.length);                   //throws IOException
            }

            /**
             * TemporaryFile���� buffer�� index��length�o�C�g�̃f�[�^��ǂݍ���
             *
             * @param buffer �f�[�^��ǂݍ��ރo�b�t�@
             * @param index  buffer���̃f�[�^�ǂ݂��݊J�n�ʒu
             * @param length �ǂݍ��ރf�[�^��
             *
             * @return �ǂ݂��܂ꂽ�f�[�^��
             *         ����EndOfStream�ɒB���Ă���ꍇ��-1
             *
             * @exception IOException ���o�̓G���[�����������ꍇ
             */
            public int read(byte[] buffer, int index, int length)
                    throws IOException {
                long pos = TemporaryFile.this.tempfile.getFilePointer();      //throws IOException
                long limit = TemporaryFile.this.length;
                length = (int) (Math.min(pos + length, limit) - pos);

                if (pos < limit) {
                    return TemporaryFile.this.tempfile.read(buffer, index, length);//throws IOException
                } else {
                    return -1;
                }
            }

        }

        /**
         * TemporaryFile �̏o�̓X�g���[��
         */
        private class TemporaryFileOutputStream extends OutputStream {

            //------------------------------------------------------------------
            //  constructor
            //------------------------------------------------------------------
            //  public TemporaryFileOutputStream()
            //------------------------------------------------------------------

            /**
             * TemporaryFile �Ƀf�[�^���o�͂��� OutputStream ���\�z����B<br>
             *
             * @exception IOException ���o�̓G���[�����������ꍇ
             */
            public TemporaryFileOutputStream() throws IOException {
                TemporaryFile.this.tempfile.seek(0);                          //throws IOException
                TemporaryFile.this.length = 0;
            }

            //------------------------------------------------------------------
            //  method of java.io.OutputStream
            //------------------------------------------------------------------
            //  public void write( int data )
            //  public void write( byte[] buffer )
            //  public void write( byte[] buffer, int index, int length )
            //------------------------------------------------------------------

            /**
             * TemporaryFile �� 1byte�̃f�[�^�������o���B
             *
             * @param data �����o��1byte�̃f�[�^
             *
             * @exception IOException ���o�̓G���[�����������ꍇ
             */
            public void write(int data) throws IOException {
                TemporaryFile.this.tempfile.write(data);                      //throws IOException
                TemporaryFile.this.length++;
            }

            /**
             * TemporaryFile �� buffer�̓��e��S�ď����o���B
             *
             * @param buffer �����o���f�[�^�̓������o�C�g�z��
             *
             * @exception IOException ���o�̓G���[�����������ꍇ
             */
            public void write(byte[] buffer) throws IOException {
                TemporaryFile.this.tempfile.write(buffer);                    //throws IOException
                TemporaryFile.this.length += buffer.length;
            }

            /**
             * TemporaryFile �� buffer��index ����length�o�C�g�̓��e�������o���B
             *
             * @param buffer �����o���f�[�^�̓������o�C�g�z��
             * @param index  buffer���̏����o���f�[�^�̊J�n�ʒu
             * @param length �����o���f�[�^��
             *
             * @exception IOException ���o�̓G���[�����������ꍇ
             */
            public void write(byte[] buffer, int index, int length)
                    throws IOException {
                TemporaryFile.this.tempfile.write(buffer, index, length);     //throws IOException
                TemporaryFile.this.length += length;
            }

        }

    }

    /**
     * �ꎞ�ޔ��@�\�� GrowthByteBuffer���g�p����N���X
     */
    private static class TemporaryBuffer implements Temporary {

        //------------------------------------------------------------------
        //  instance field
        //------------------------------------------------------------------
        //  private GrowthByteBuffer tempbuffer
        //------------------------------------------------------------------
        /**
         * �ꎞ�ޔ��@�\�Ɏg�p�����o�b�t�@
         */
        private GrowthByteBuffer tempbuffer;


        //------------------------------------------------------------------
        //  constructor
        //------------------------------------------------------------------
        //  public TemporaryBuffer()
        //------------------------------------------------------------------

        /**
         * GrowthByteBuffer ���g�p���������@�\���\�z����B
         */
        public TemporaryBuffer() {
            this.tempbuffer = new GrowthByteBuffer();
        }

        //------------------------------------------------------------------
        //  method of Temporary
        //------------------------------------------------------------------
        //  public InputStream getInputStream()
        //  public OutputStream getOutputStream()
        //  public long length()
        //  public void close()
        //------------------------------------------------------------------

        /**
         * �ꎞ�ޔ��@�\�ɒ�����ꂽ�f�[�^�����o�� InputStream �𓾂�B<br>
         * ���̃f�[�^�͒��O�� getOutputStream() �ŗ^������ 
         * OutputStream �ɏo�͂��ꂽ�f�[�^�Ɠ����B<br>
         *
         * @return �ꎞ�ޔ��@�\����f�[�^�����o�� InputStream
         */
        public InputStream getInputStream() {
            return new TemporaryBufferInputStream();
        }

        /**
         * �f�[�^���ꎞ�ޔ��@�\�ɒ����� OutputStream �𓾂�B<br>
         * �������f�[�^�͒���� getInputStream() �œ����� 
         * InputStream ���瓾�鎖���o����B<br>
         *
         * @return �f�[�^���ꎞ�ޔ��@�\�ɒ����� OutputStream
         */
        public OutputStream getOutputStream() {
            return new TemporaryBufferOutputStream();
        }

        /**
         * �ꎞ�ޔ��@�\�Ɋi�[����Ă���f�[�^�ʂ𓾂�B<br>
         * ����� ���O�� getOutputStream() �ŗ^����
         * OutputStream �ɏo�͂��ꂽ�f�[�^�ʂƓ����ł���B
         *
         * @return �ꎞ�ޔ��@�\�Ɋi�[����Ă���f�[�^��
         */
        public long length() {
            return this.tempbuffer.length();
        }

        /**
         * �ꎞ�ޔ��@�\�Ŏg�p����Ă����A�S�ẴV�X�e�����\�[�X���J������B
         */
        public void close() {
            this.tempbuffer = null;
        }

        //------------------------------------------------------------------
        //  inner classes
        //------------------------------------------------------------------
        //  private class TemporaryBufferInputStream
        //  private class TemporaryBufferOutputStream
        //------------------------------------------------------------------

        /**
         * TemporaryBuffer �̓��̓X�g���[��
         */
        private class TemporaryBufferInputStream extends InputStream {

            //------------------------------------------------------------------
            //  constructor
            //------------------------------------------------------------------
            //  public TemporaryBufferInputStream()
            //------------------------------------------------------------------

            /**
             * TemporaryBuffer ����f�[�^��ǂݍ��� InputStream ���\�z����B<br>
             */
            public TemporaryBufferInputStream() {
                TemporaryBuffer.this.tempbuffer.seek(0);
            }

            //------------------------------------------------------------------
            //  method of java.io.InputStream
            //------------------------------------------------------------------
            //  public int read()
            //  public int read( byte[] buffer )
            //  public int read( byte[] buffer, int index, int length )
            //------------------------------------------------------------------

            /**
             * TemporaryBuffer ���� 1�o�C�g�̃f�[�^��ǂݍ��ށB
             *
             * @return �ǂ݂��܂ꂽ1�o�C�g�̃f�[�^
             *         ����EndOfStream�ɒB���Ă���ꍇ��-1
             */
            public int read() {
                return TemporaryBuffer.this.tempbuffer.read();
            }

            /**
             * TemporaryBuffer ���� buffer�𖞂����悤�Ƀf�[�^��ǂݍ��ށB
             *
             * @param buffer �f�[�^��ǂݍ��ރo�b�t�@
             *
             * @return �ǂ݂��܂ꂽ�f�[�^��
             *         ����EndOfStream�ɒB���Ă���ꍇ��-1
             */
            public int read(byte[] buffer) {
                return TemporaryBuffer.this.tempbuffer.read(buffer);
            }

            /**
             * TemporaryBuffer ���� buffer�� index�� length�o�C�g�̃f�[�^��ǂݍ���
             *
             * @param buffer �f�[�^��ǂݍ��ރo�b�t�@
             * @param index  buffer���̃f�[�^�ǂ݂��݊J�n�ʒu
             * @param length �ǂݍ��ރf�[�^��
             *
             * @return �ǂ݂��܂ꂽ�f�[�^��
             *         ����EndOfStream�ɒB���Ă���ꍇ��-1
             */
            public int read(byte[] buffer, int index, int length) {
                return TemporaryBuffer.this.tempbuffer.read(buffer, index, length);
            }

        }

        /**
         * TemporaryBuffer �̏o�̓X�g���[��
         */
        private class TemporaryBufferOutputStream extends OutputStream {

            //------------------------------------------------------------------
            //  constructor
            //------------------------------------------------------------------
            //  public TemporaryBufferOutputStream()
            //------------------------------------------------------------------

            /**
             * TemporaryBuffer �Ƀf�[�^���o�͂��� OutputStream ���\�z����B<br>
             */
            public TemporaryBufferOutputStream() {
                TemporaryBuffer.this.tempbuffer.seek(0);
                TemporaryBuffer.this.tempbuffer.setLength(0);
            }

            //------------------------------------------------------------------
            //  method of java.io.OutputStream
            //------------------------------------------------------------------
            //  public void write( int data )
            //  public void write( byte[] buffer )
            //  public void write( byte[] buffer, int index, int length )
            //------------------------------------------------------------------

            /**
             * TemporaryBuffer �� 1byte�̃f�[�^�������o���B
             *
             * @param data �����o��1byte�̃f�[�^
             */
            public void write(int data) {
                TemporaryBuffer.this.tempbuffer.write(data);
            }

            /**
             * TemporaryBuffer �� buffer�̓��e��S�ď����o���B
             *
             * @param buffer �����o���f�[�^�̓������o�C�g�z��
             */
            public void write(byte[] buffer) {
                TemporaryBuffer.this.tempbuffer.write(buffer);
            }

            /**
             * TemporaryBuffer �� buffer��index ���� length�o�C�g�̓��e�������o���B
             *
             * @param buffer �����o���f�[�^�̓������o�C�g�z��
             * @param index  buffer���̏����o���f�[�^�̊J�n�ʒu
             * @param length �����o���f�[�^��
             */
            public void write(byte[] buffer, int index, int length) {
                TemporaryBuffer.this.tempbuffer.write(buffer, index, length);
            }

        }

    }

}
//end of LhaOutputStream.java
