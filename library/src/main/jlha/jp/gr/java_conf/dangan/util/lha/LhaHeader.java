//start of LhaHeader.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * LhaHeader.java
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

package jp.gr.java_conf.dangan.util.lha;

//import classes and interfaces

import jp.gr.java_conf.dangan.io.LittleEndian;
import jp.gr.java_conf.dangan.util.MsdosDate;

import java.io.*;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

//import exceptions


/**
 * LHA�w�b�_�������B<br>
 * ���̃N���X�� java.util.zip �p�b�P�[�W�ł� ZipEntry �Ƌ߂����A
 * �w�b�_�̓��o�͂̂��߂̃��[�e�B���e�B�֐������_���Ⴄ�B<br>
 * ���̃N���X�� set�n���\�b�h�ňׂ��ꂽ�����ǂ��`�F�b�N��
 * getBytes() ���ɍs���悤�ɏ�����Ă���B���̓_�͒��ӂ��邱�ƁB<br>
 *
 * <pre>
 * -- revision history --
 * $Log: LhaHeader.java,v $
 * Revision 1.2.2.3  2005/05/03 07:50:30  dangan
 * [bug fix]
 *     exportLevel1Header() �� skip size �̃`�F�b�N������Ă��Ȃ������B
 *
 * Revision 1.2.2.2  2005/02/02 00:57:46  dangan
 * [bug fix]
 *     importLevelXHeader(byte[], String) �Ńt�@�C���T�C�Y�� int �œǂݍ���ł�������
 *     31�r�b�g�l�ȏ�̃T�C�Y�̃t�@�C���𐳂��������Ă��Ȃ������̂��C���B
 *
 * Revision 1.2.2.1  2003/07/20 13:19:21  dangan
 * [bug fix]
 *     exportDirNameExtHeader(String) �� System.arraycopy �� src �� dest �̔z�u���Ԉ���Ă����B
 *
 * Revision 1.2  2002/12/08 00:00:00  dangan
 * [maintenance]
 *     LhaConstants ���� CompressMethod �ւ̃N���X���̕ύX�ɍ��킹�ďC���B
 *
 * Revision 1.1  2002/12/05 00:00:00  dangan
 * [improvement]
 *     64�r�b�g�t�@�C���T�C�Y�w�b�_�ɑΉ��B
 * [change]
 *     LhaUtil.DefaultEncoding ���� LhaProperty.encoding ���g�p����悤�ɕύX�B
 *     getNextHeaderData() �� getFirstHeaderData() �ɖ��O�ύX�B
 *     �V���� getNextHeaderData() �͌Ăяo���ꂽ�ʒu��
 *     �w�b�_�𔭌��ł��Ȃ��ꍇ null ��Ԃ��B
 *     LhaHeader ���g�������T�u�N���X���g�p����l�̂��߂� createInstance() ��ǉ��B
 *
 * Revision 1.0  2002/08/05 00:00:00  dangan
 * add to version control
 * [bug fix]
 *     setDate( null ) �������Ă����B
 *     setCompressMethod( null ) �������Ă����B
 *     exportLevel2,3Header ��
 *     Date �� 32bit �� time_t �͈̔͊O�̒l(���̒l���܂�)�̏ꍇ�������Ă����B
 * [change]
 *     exportHeader �� �w�b�_���x���� 0,1,2,3 �̂�����ł��Ȃ��ꍇ
 *     IllegalStateException �𓊂���悤�ɕύX�B
 * [maintenance]
 *     �\�[�X����
 *     �^�u�p�~
 *     ���C�Z���X���̏C��
 *
 * </pre>
 *
 * @author $Author: dangan $
 * @version $Revision: 1.2.2.3 $
 */
public class LhaHeader implements Cloneable {

    //------------------------------------------------------------------
    //  class field
    //------------------------------------------------------------------
    //  public static final int UNKNOWN
    //  public static final int NO_CRC
    //------------------------------------------------------------------
    /**
     * �s�����Ӗ�����l�B
     * LhaHeader.getCRC(), LhaHeader.getCompressedSize(), 
     * LhaHeader.getOriginalSzie() �����̒l��Ԃ����ꍇ��
     * �����O�̂��߂ɁA���̒l���s���ł��鎖�������B
     */
    public static final int UNKNOWN = -1;

    /**
     * CRC�l�����������Ӗ�����l�B
     * ���x��0�w�b�_��CRC�l�����݂��Ȃ������Ӗ�����B
     */
    public static final int NO_CRC = -2;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  file information
    //------------------------------------------------------------------
    //  private long OriginalSize
    //  private Date LastModified
    //  private String Path
    //  private int CRC
    //------------------------------------------------------------------
    /**
     * ���k�O�T�C�Y�B
     * -1 �͏����O�̂��߃T�C�Y���s���ł��邱�Ƃ��Ӗ�����B
     */
    private long OriginalSize;

    /**
     * �ŏI�X�V�����B
     * ���k�����t�@�C���̍ŏI�X�V�����B
     */
    private Date LastModified;

    /**
     * �p�X���B
     * �p�X�f���~�^�ɂ� java.io.File.separator ���g�p����B
     */
    private String Path;

    /**
     * CRC16 �̒l�B
     * -1 �� �����O�̂��߂�CRC16�l���s���ł��鎖���Ӗ�����B
     * -2 �� ���x��0�w�b�_��CRC16�l�����������Ӗ�����B
     */
    private int CRC;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  information of compressed data
    //------------------------------------------------------------------
    //  private String Method
    //  private long CompressedSize
    //  private int HeaderLevel
    //  private byte OSID
    //------------------------------------------------------------------
    /**
     * ���k�@������B 
     */
    private String Method;

    /**
     * ���k��T�C�Y�B
     * -1 �͏����O�̂��߃T�C�Y���s���ł��邱�Ƃ��Ӗ�����B
     */
    private long CompressedSize;

    /**
     * �w�b�_���x���B
     * 0,1,2,3�̉��ꂩ
     */
    private int HeaderLevel;

    /**
     * �w�b�_���쐬���� OS�B
     */
    private byte OSID;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  other
    //------------------------------------------------------------------
    //  private byte[] ExtraData
    //  private byte Level0DosAttribute
    //  private Vector ExtraExtHeaders
    //------------------------------------------------------------------
    /**
     * ���x��0�w�b�_�������� ���x��1�w�b�_�̊�{�w�b�_����
     * �g����񂪂������ꍇ�A�����ۑ�����B
     */
    private byte[] ExtraData;

    /**
     * ���x��0�w�b�_�ɂ����� DOS�̃t�@�C��������ۑ�����B
     */
    private byte Level0DosAttribute;

    /**
     * LhaHeader�ł͓ǂݍ��܂Ȃ��������g���w�b�_��ۑ�����B
     */
    private Vector ExtraExtHeaders;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private LhaHeader()
    //  public LhaHeader( String path )
    //  public LhaHeader( String path, Date date )
    //  public LhaHeader( byte[] HeaderData )
    //  public LhaHeader( byte[] HeaderData, String encode )
    //------------------------------------------------------------------

    /**
     * LhaHeader�̊e�l������������B
     */
    private LhaHeader() {
        this.Method = CompressMethod.LH5;
        this.OriginalSize = LhaHeader.UNKNOWN;
        this.CompressedSize = LhaHeader.UNKNOWN;
        this.LastModified = null;
        this.HeaderLevel = 2;
        this.Path = "";
        this.CRC = LhaHeader.UNKNOWN;
        this.OSID = (byte) 'J';
        this.ExtraData = null;
        this.Level0DosAttribute = 0x20;
        this.ExtraExtHeaders = null;
    }

    /**
     * path �Ƃ������O������ LhaHeader �̃C���X�^���X�𐶐�����B<br>
     * �p�X�f���~�^�ɂ� File.separator ���g�p���邱�ƁB<br>
     * path �� �p�X�f���~�^�Ń^�[�~�l�[�g����Ă���ꍇ�� 
     * �f�B���N�g���ł���Ɖ��߂����B<br>
     *
     * @param path �p�X��
     *
     * @exception IllgelArgumentException
     *             path �� null �� �󕶎���̂����ꂩ�ł���ꍇ
     */
    public LhaHeader(String path) {
        this(path, new Date(System.currentTimeMillis()));
    }

    /**
     * path �Ƃ������O�������A�ŏI�X�V������ date ��
     *  LhaHeader �̃C���X�^���X�𐶐�����B<br>
     * �p�X�f���~�^�ɂ� File.separator ���g�p���邱�ƁB<br>
     * path �� �p�X�f���~�^�Ń^�[�~�l�[�g����Ă���ꍇ�� 
     * �f�B���N�g���ł���Ɖ��߂����B<br>
     *
     * @param path �p�X��
     * @param date �ŏI�X�V����
     *
     * @exception IllgelArgumentException
     *             path �� null �� �󕶎���̂����ꂩ�ł��邩�A
     *             date �� null�ł���ꍇ�B
     */
    public LhaHeader(String path, Date date) {
        this();
        if (path != null && !path.equals("") && date != null) {
            if (path.endsWith(File.separator)) {
                this.Method = CompressMethod.LHD;
            }

            this.Path = path;
            this.LastModified = date;
        } else if (path == null) {
            throw new NullPointerException("path");
        } else if (path.equals("")) {
            throw new IllegalArgumentException("path must not be empty.");
        } else {
            throw new NullPointerException("date");
        }
    }

    /**
     * �w�b�_�f�[�^���� �V���� LhaHeader ��
     * �C���X�^���X�𐶐�����B<br>
     * �G���R�[�h�� LhaUtil.DefaultEncode ���g�p�����B<br>
     *
     * @param HeaderData �w�b�_�f�[�^
     *
     * @exception IndexOutOfBoundsException
     *                   �w�b�_�f�[�^�����Ă��邽��
     *                   �f�[�^������Ɖ��肵���ʒu��
     *                   HeaderData �͈̔͊O�ɂȂ���
     * @exception IllegalArgumentException
     *                   �w�b�_���x���� 0,1,2,3 �̉���ł��Ȃ����A
     *                   HeaderData �� null �̏ꍇ
     */
    public LhaHeader(byte[] HeaderData) {
        this();
        if (HeaderData != null) {
            try {
                this.importHeader(HeaderData, LhaProperty.encoding);
            } catch (UnsupportedEncodingException exception) {
                throw new Error("Java Runtime Environment not support " + LhaProperty.encoding + " encoding");
            }
        } else {
            throw new NullPointerException("HeaderData");
        }
    }

    /**
     * �w�b�_�f�[�^���� �V���� LhaHeader ��
     * �C���X�^���X�𐶐�����B<br>
     *
     * @param HeaderData �w�b�_�f�[�^
     * @param encode     ������������߂���ۂɎg�p����
     *                   �G���R�[�h
     *
     * @exception IndexOutOfBoundsException
     *                   �w�b�_�f�[�^�����Ă��邽��
     *                   �f�[�^������Ɖ��肵���ʒu��
     *                   HeaderData �͈̔͊O�ɂȂ���
     * @exception UnsupportedEncodingException
     *                   encode �Ŏw�肳�ꂽ�G���R�[�h��
     *                   �T�|�[�g����Ȃ��ꍇ
     * @exception IllegalArgumentException
     *                   �w�b�_���x���� 0,1,2,3 �̉���ł��Ȃ����A
     *                   HeaderData �� null �̏ꍇ
     */
    public LhaHeader(byte[] HeaderData, String encode)
            throws UnsupportedEncodingException {
        this();
        if (HeaderData != null && encode != null) {
            this.importHeader(HeaderData, encode);                            //throw UnsupportedEncodingException
        } else if (HeaderData == null) {
            throw new NullPointerException("HeaderData");
        } else {
            throw new NullPointerException("encode");
        }
    }


    //------------------------------------------------------------------
    //  method of java.lang.Cloneable
    //------------------------------------------------------------------
    //  public Object clone()
    //------------------------------------------------------------------

    /**
     * �w�b�_�f�[�^�������ł��邩���`�F�b�N����B
     *
     * @param HeaderData �w�b�_�f�[�^���o�C�g�z��Ɋi�[��������
     *
     * @return �w�b�_�f�[�^�������ł���� true
     *         �Ⴆ�� false
     */
    public static boolean checkHeaderData(byte[] HeaderData) {
        final int HeaderLevelIndex = 20;
        try {
            switch (HeaderData[HeaderLevelIndex] & 0xFF) {
                case 0:
                    return LhaHeader.verifyHeaderChecksum(HeaderData);

                case 1:
                    return LhaHeader.verifyHeaderChecksum(HeaderData)
                            && (LhaHeader.getCRC16Position(HeaderData) == -1
                            || LhaHeader.verifyHeaderCRC16(HeaderData));

                case 2:
                    return LhaHeader.verifyHeaderCRC16(HeaderData);

                case 3:
                    return LhaHeader.verifyHeaderCRC16(HeaderData);

            }
        } catch (ArrayIndexOutOfBoundsException exception) { //Ignore
        }
        return false;
    }


    //------------------------------------------------------------------
    //  access method
    //------------------------------------------------------------------
    //  getter
    //------------------------------------------------------------------
    //  public String getCompressMethod()
    //  public long getOriginalSize()
    //  public long getCompressedSize()
    //  public Date getLastModified()
    //  public int getHeaderLevel()
    //  public String getPath()
    //  public int getCRC()
    //  public byte getOSID()
    //  protected byte[] getExtraData()
    //  protected byte getLevel0DosAttribute()
    //  private String getFileName()
    //  private String getDirName()
    //------------------------------------------------------------------

    /**
     * �w�b�_��CRC�l���i�[���Ă���ʒu�𓾂�B
     *
     * @param HeaderData �w�b�_�f�[�^���o�C�g�z��Ɋi�[��������
     *
     * @return �w�b�_��CRC�l�̈ʒu<br>
     *         �w�b�_��CRC�l�������Ȃ��ꍇ�� -1
     */
    private static int getCRC16Position(byte[] HeaderData) {
        final int HeaderLevelIndex = 20;
        int WordSize;
        int position;
        int length;

        switch (HeaderData[HeaderLevelIndex] & 0xFF) {
            case 1:
                WordSize = 2;
                position = length = (HeaderData[0] & 0xFF) + 2;
                break;
            case 2:
                WordSize = 2;
                position = length = 26;
                break;
            case 3:
                WordSize = 4;
                position = length = 32;
                break;
            default:
                return -1;
        }

        while (true) {
            if (0 < length && position < HeaderData.length) {
                length = 0;

                for (int i = 0; i < WordSize; i++) {
                    length = (length << 8 | (HeaderData[position - (1 + i)] & 0xFF));
                }

                if (HeaderData[position] == 0) {
                    return position + 1;
                }

                position += length;
            } else {
                return -1;
            }
        }
    }

    /**
     * ���x��0�w�b�_�A���x��1�w�b�_��
     * �w�b�_�f�[�^����`�F�b�N�T���l���v�Z����B
     *
     * @param HeaderData �w�b�_�f�[�^���o�C�g�z��Ɋi�[��������
     *
     * @return �v�Z���ꂽ�w�b�_�̃`�F�b�N�T���l
     */
    private static int calcHeaderChecksum(byte[] HeaderData) {
        int length = HeaderData[0] & 0xFF;

        LhaChecksum checksum = new LhaChecksum();
        checksum.update(HeaderData, 2, length);

        return (int) checksum.getValue();
    }

    /**
     * ���x��1�w�b�_�A���x��2�w�b�_�A���x��3�w�b�_��
     * �w�b�_�f�[�^����CRC16�l���v�Z����B
     *
     * @param HeaderData �w�b�_�f�[�^���o�C�g�z��Ɋi�[��������
     *
     * @return �v�Z���ꂽ�w�b�_��CRC16�l
     */
    private static int calcHeaderCRC16(byte[] HeaderData) {
        int position = LhaHeader.getCRC16Position(HeaderData);
        int crcValue = 0;
        if (position != -1) {
            crcValue = LittleEndian.readShort(HeaderData, position);
            LittleEndian.writeShort(HeaderData, position, 0);
        }

        CRC16 crc16 = new CRC16();
        crc16.update(HeaderData);

        if (position != -1) {
            LittleEndian.writeShort(HeaderData, position, crcValue);
        }

        return (int) crc16.getValue();
    }

    /**
     * ���x��0�w�b�_�A���x��1�w�b�_��
     * �w�b�_�f�[�^����`�F�b�N�T���l��ǂݍ��ށB
     *
     * @param HeaderData �w�b�_�f�[�^���o�C�g�z��Ɋi�[��������
     *
     * @return �w�b�_�ɋL�^���ꂽ�`�F�b�N�T���l
     */
    private static int readHeaderChecksum(byte[] HeaderData) {
        return HeaderData[1] & 0xFF;
    }

    /**
     * ���x��1�w�b�_�A���x��2�w�b�_�A���x��3�w�b�_��
     * �w�b�_�f�[�^����CRC16�l��ǂݍ��ށB
     *
     * @param HeaderData �w�b�_�f�[�^���o�C�g�z��Ɋi�[��������
     *
     * @return �w�b�_�ɋL�^���ꂽCRC16�l
     */
    private static int readHeaderCRC16(byte[] HeaderData) {
        int position = LhaHeader.getCRC16Position(HeaderData);
        if (position != -1) {
            return LittleEndian.readShort(HeaderData, position);
        } else {
            return -1;
        }
    }

    /**
     * �`�F�b�N�T���l�ɂ���ăw�b�_�f�[�^�̐��������`�F�b�N����B
     *
     * @param HeaderData �w�b�_�f�[�^���o�C�g�z��Ɋi�[��������
     *
     * @return �`�F�b�N�T���l�ɂ���ăw�b�_�f�[�^�̐�������
     *         �ؖ������� true�A
     *         �ؖ�����Ȃ���� false
     */
    private static boolean verifyHeaderChecksum(byte[] HeaderData) {
        final int HeaderLevelIndex = 20;

        switch (HeaderData[HeaderLevelIndex] & 0xFF) {
            case 0:
            case 1:
                return LhaHeader.readHeaderChecksum(HeaderData)
                        == LhaHeader.calcHeaderChecksum(HeaderData);
            default:
                return false;
        }
    }

    /**
     * CRC16�l�ɂ���ăw�b�_�f�[�^�̐��������`�F�b�N����B
     *
     * @param HeaderData �w�b�_�f�[�^���o�C�g�z��Ɋi�[��������
     *
     * @return CRC16�l�ɂ���ăw�b�_�f�[�^�̐�������
     *         �ؖ������� true�A
     *         �ؖ�����Ȃ���� false
     */
    private static boolean verifyHeaderCRC16(byte[] HeaderData) {
        final int HeaderLevelIndex = 20;

        switch (HeaderData[HeaderLevelIndex] & 0xFF) {
            case 1:
            case 2:
            case 3:
                return LhaHeader.readHeaderCRC16(HeaderData)
                        == LhaHeader.calcHeaderCRC16(HeaderData);
            default:
                return false;
        }
    }

    /**
     * ���̓X�g���[������ �ŏ��̃w�b�_��ǂݍ��ށB<br>
     * ���̃��\�b�h�̓��x��1�w�b�_�A�������� ���x��3�w�b�_��
     * �����f�[�^�����݂���ƁA�w�b�_�S�Ă�ǂݍ������Ƃ���
     * in.mark( 65536 ) �� ���E�𒴂��� �ǂݍ��މ\��������A
     * ���̌��� reset() �ł����� ���̊Ԃ̃f�[�^��ǂݗ��Ƃ�
     * �\��������B<br>
     * �܂��AInputStream ��mark/reset �̎�������ł�
     * �X�g���[���I�[�t�߂� �w�b�_�Ɏ����f�[�^�����݂����
     * �w�b�_��S�ēǂݍ������Ƃ��� EndOfStream�ɒB���Ă��܂��A
     * reset()�ł����� ���̊Ԃ̃f�[�^��ǂݗ��Ƃ��\��������B<br>
     *
     * @param in �w�b�_�f�[�^��ǂݍ��ޓ��̓X�g���[��
     *           �X�g���[���� mark/reset�̃T�|�[�g��K�v�Ƃ���B
     *
     * @return �ǂݎ��ꂽ�w�b�_�f�[�^<br>
     *         �w�b�_�������炸�� EndOfStream �ɒB�����ꍇ�� null<br>
     *
     * @exception IOException  ���o�̓G���[�����������ꍇ
     * @exception IllegalArgumentException
     *                         in �� mark/reset���T�|�[�g���Ȃ��ꍇ
     */
    public static byte[] getFirstHeaderData(InputStream in)
            throws IOException {
        if (in.markSupported()) {
            try {
                int stock1 = -1;
                int stock2 = -1;
                int read;

                while (0 <= (read = in.read())) {                            //throw IOException
                    if (read == '-' && 0 < stock1) {
                        in.mark(65536);   //65536�ŕۏ؂ł���̂�level0,2�̂�
                        LhaHeader.ensureSkip(in, 3);                          //throw IOException
                        if (in.read() == '-') {                                 //throw IOException
                            LhaHeader.ensureSkip(in, 13);                     //throw IOException
                            int HeaderLevel = in.read();                        //throw IOException
                            in.reset();                                         //throw IOException
                            byte[] HeaderData;
                            switch (HeaderLevel) {
                                case 0:
                                    HeaderData = LhaHeader.readLevel0HeaderData(
                                            stock1, stock2, read, in); //throw IOException
                                    break;
                                case 1:
                                    HeaderData = LhaHeader.readLevel1HeaderData(
                                            stock1, stock2, read, in); //throw IOException
                                    break;
                                case 2:
                                    HeaderData = LhaHeader.readLevel2HeaderData(
                                            stock1, stock2, read, in); //throw IOException
                                    break;
                                case 3:
                                    HeaderData = LhaHeader.readLevel3HeaderData(
                                            stock1, stock2, read, in); //throw IOException
                                    break;
                                default:
                                    HeaderData = null;
                            }

                            if (HeaderData != null
                                    && LhaHeader.checkHeaderData(HeaderData))
                                return HeaderData;
                        }
                        in.reset();                                             //throw IOException
                    }
                    stock1 = stock2;
                    stock2 = read;
                }
            } catch (EOFException exception) { //Ignore
            }
            return null;
        } else {
            throw new IllegalArgumentException("InputStream needed mark()/reset() support.");
        }
    }

    /**
     * ���̓X�g���[������ ���̃w�b�_��ǂݍ��ށB<br>
     * ���̃��\�b�h�̓��x��1�w�b�_�A�������� ���x��3�w�b�_��
     * �����f�[�^�����݂���ƁA�w�b�_�S�Ă�ǂݍ������Ƃ���
     * in.mark( 65536 ) �� ���E�𒴂��� �ǂݍ��މ\��������A
     * ���̌��� reset() �ł����� ���̊Ԃ̃f�[�^��ǂݗ��Ƃ�
     * �\��������B<br>
     * �܂��A�X�g���[���I�[�t�߂� �w�b�_�Ɏ����f�[�^�����݂���
     * �� �w�b�_��S�ēǂݍ������Ƃ��� EndOfStream�ɒB���Ă��܂��A
     * reset()�ł����� ���̊Ԃ̃f�[�^��ǂݗ��Ƃ��\��������B<br>
     *
     * @param in �w�b�_�f�[�^��ǂݍ��ޓ��̓X�g���[��
     *           �X�g���[���� mark/reset�̃T�|�[�g��K�v�Ƃ���B
     *
     * @return �ǂݎ��ꂽ�w�b�_�f�[�^<br>
     *         �w�b�_�������炸�� EndOfStream �ɒB�����ꍇ�� null<br>
     *
     * @exception IOException  ���o�̓G���[�����������ꍇ
     * @exception IllegalArgumentException
     *                         in �� mark/reset���T�|�[�g���Ȃ��ꍇ
     */
    public static byte[] getNextHeaderData(InputStream in)
            throws IOException {
        if (in.markSupported()) {
            try {
                int first = in.read();                                          //throw IOException
                if (0 < first) { // ���̒l�� EndOfStream�ɓ��B�A 0�̏ꍇ�͏��ɏI�[�ɓ��B
                    int second = in.read();                                    //throw IOException
                    int third = in.read();                                    //throw IOException
                    in.mark(65536); //65536�ŕۏ؂ł���̂�level0,2�̂�
                    LhaHeader.ensureSkip(in, 3);                              //throw IOException
                    int seventh = in.read();                                    //throw IOException
                    if (third == '-' && seventh == '-') {
                        LhaHeader.ensureSkip(in, 13);                         //throw IOException
                        int HeaderLevel = in.read();                            //throw IOException
                        in.reset();
                        byte[] HeaderData;
                        switch (HeaderLevel) {
                            case 0:
                                HeaderData = LhaHeader.readLevel0HeaderData(
                                        first, second, third, in);//throw IOException
                                break;
                            case 1:
                                HeaderData = LhaHeader.readLevel1HeaderData(
                                        first, second, third, in);//throw IOException
                                break;
                            case 2:
                                HeaderData = LhaHeader.readLevel2HeaderData(
                                        first, second, third, in);//throw IOException
                                break;
                            case 3:
                                HeaderData = LhaHeader.readLevel3HeaderData(
                                        first, second, third, in);//throw IOException
                                break;
                            default:
                                HeaderData = null;
                        }

                        if (HeaderData != null && LhaHeader.checkHeaderData(HeaderData)) {
                            return HeaderData;
                        }
                    }
                }
            } catch (EOFException exception) { //Ignore
            }
            return null;
        } else {
            throw new IllegalArgumentException("InputStream needed mark()/reset() support.");
        }
    }

    /**
     * ���̓X�g���[�����烌�x��0�w�b�_��ǂݍ���
     *
     * @param HeaderLength    �w�b�_�̒���
     * @param HeaderChecksum  �w�b�_�̃`�F�b�N�T��
     * @param CompressMethod1 ���k�@������
     * @param in              �w�b�_�f�[�^��ǂݍ��ޓ��̓X�g���[��
     *
     * @return �ǂݎ��ꂽ�w�b�_�f�[�^
     *
     * @exception IOException  ���o�̓G���[�����������ꍇ
     * @exception EOFException �w�b�_�̓ǂݍ��ݓr���� EndOfStream�ɒB�����ꍇ
     */
    private static byte[] readLevel0HeaderData(int HeaderLength,
                                               int HeaderChecksum,
                                               int CompressMethod1,
                                               InputStream in)
            throws IOException {
        byte[] HeaderData = new byte[HeaderLength + 2];
        HeaderData[0] = (byte) HeaderLength;
        HeaderData[1] = (byte) HeaderChecksum;
        HeaderData[2] = (byte) CompressMethod1;
        int readed = 3;
        int length = 0;
        HeaderLength += 2;

        while (readed < HeaderLength && 0 <= length) {
            length = in.read(HeaderData, readed, HeaderLength - readed);      //throws IOException
            readed += length;
        }

        if (readed == HeaderLength) {
            return HeaderData;
        } else {
            throw new EOFException();
        }
    }

    /**
     * ���̓X�g���[�����烌�x��1�w�b�_��ǂݍ���
     *
     * @param BaseHeaderLength   ��{�w�b�_�̒���
     * @param BaseHeaderChecksum ��{�w�b�_�̃`�F�b�N�T��
     * @param CompressMethod1    ���k�@������
     * @param in                 �w�b�_�f�[�^��ǂݍ��ޓ��̓X�g���[��
     *
     * @return �ǂݎ��ꂽ�w�b�_�f�[�^�B
     *         ���x��1�w�b�_�łȂ����Ƃ����������ꍇ�� null��Ԃ��B
     *
     * @exception IOException  ���o�̓G���[�����������ꍇ
     * @exception EOFException �w�b�_�̓ǂݍ��ݓr���� EndOfStream�ɒB�����ꍇ
     */
    private static byte[] readLevel1HeaderData(int BaseHeaderLength,
                                               int BaseHeaderChecksum,
                                               int CompressMethod1,
                                               InputStream in)
            throws IOException {
        int HeaderLength = BaseHeaderLength + 2;
        Vector headers = new Vector();
        byte[] HeaderData = new byte[HeaderLength];
        HeaderData[0] = (byte) BaseHeaderLength;
        HeaderData[1] = (byte) BaseHeaderChecksum;
        HeaderData[2] = (byte) CompressMethod1;

        //�w�b�_�f�[�^�擾
        int readed = 0;
        int length = 0;
        do {
            if (0 == headers.size()) {
                readed = 3;
            } else {
                readed = 0;
            }

            while (readed < HeaderLength && 0 <= length) {
                length = in.read(HeaderData, readed, HeaderLength - readed);  //throws IOException
                readed += length;
            }

            if (readed == HeaderLength) {
                if (0 == headers.size() && !LhaHeader.verifyHeaderChecksum(HeaderData)) {
                    return null;
                } else {
                    headers.addElement(HeaderData);
                }
            } else {
                throw new EOFException();
            }

            length = HeaderLength;
            HeaderLength = LittleEndian.readShort(HeaderData, HeaderLength - 2);
            HeaderData = new byte[HeaderLength];
        } while (0 < HeaderLength && readed == length);

        //�擾�����w�b�_�f�[�^����̃o�C�g�z���
        HeaderLength = 0;
        for (int i = 0; i < headers.size(); i++) {
            HeaderLength += ((byte[]) headers.elementAt(i)).length;
        }

        HeaderData = new byte[HeaderLength];
        int position = 0;
        for (int i = 0; i < headers.size(); i++) {
            byte[] Data = (byte[]) headers.elementAt(i);
            System.arraycopy(Data, 0, HeaderData, position, Data.length);

            position += Data.length;
        }
        return HeaderData;
    }

    /**
     * ���̓X�g���[�����烌�x��2�w�b�_��ǂݍ���
     *
     * @param HeaderLengthLow �w�b�_�̒������ʃo�C�g
     * @param HeaderLengthHi  �w�b�_�̒�����ʃo�C�g
     * @param CompressMethod1 ���k�@������
     * @param in              �w�b�_�f�[�^��ǂݍ��ޓ��̓X�g���[��
     *
     * @return �ǂݎ��ꂽ�w�b�_�f�[�^
     *
     * @exception IOException  ���o�̓G���[�����������ꍇ
     * @exception EOFException �w�b�_�̓ǂݍ��ݓr���� EndOfStream�ɒB�����ꍇ
     */
    private static byte[] readLevel2HeaderData(int HeaderLengthLow,
                                               int HeaderLengthHi,
                                               int CompressMethod1,
                                               InputStream in)
            throws IOException {
        int HeaderLength = (HeaderLengthHi << 8) | HeaderLengthLow;
        byte[] HeaderData = new byte[HeaderLength];
        HeaderData[0] = (byte) HeaderLengthLow;
        HeaderData[1] = (byte) HeaderLengthHi;
        HeaderData[2] = (byte) CompressMethod1;

        int readed = 3;
        int length = 0;
        while (readed < HeaderLength && 0 <= length) {
            length = in.read(HeaderData, readed, HeaderLength - readed);      //throws IOException
            readed += length;
        }

        if (readed == HeaderLength) {
            return HeaderData;
        } else {
            throw new EOFException();
        }
    }

    /**
     * ���̓X�g���[�����烌�x��3�w�b�_��ǂݍ��ށB<br>
     * ���̃��\�b�h�� ���̓ǂݍ��݃��\�b�h�ƈႢ�A
     * getNextHeaderData() �ɂ����� mark() �����
     * ���鎖��O��Ƃ��Ă���B
     *
     * @param WordSizeLow     �w�b�_�Ɏg�p����郏�[�h�T�C�Y ���ʃo�C�g
     * @param WordSizeHi      �w�b�_�Ɏg�p����郏�[�h�T�C�Y ��ʃo�C�g
     * @param CompressMethod1 ���k�@������
     * @param in              �w�b�_�f�[�^��ǂݍ��ޓ��̓X�g���[��
     *
     * @return �ǂݎ��ꂽ�w�b�_�f�[�^�B<br>
     *         ���x��3�w�b�_�łȂ����Ƃ����������ꍇ�� null��Ԃ��B
     *
     * @exception IOException  ���o�̓G���[�����������ꍇ
     * @exception EOFException �w�b�_�̓ǂݍ��ݓr���� EndOfStream�ɒB�����ꍇ
     */
    private static byte[] readLevel3HeaderData(int WordSizeLow,
                                               int WordSizeHi,
                                               int CompressMethod1,
                                               InputStream in)
            throws IOException {
        if (WordSizeLow == 0x04 && WordSizeHi == 0x00) {
            in.skip(21);
            int HeaderLength = LittleEndian.readInt(in);
            in.reset();

            byte[] HeaderData = new byte[HeaderLength];
            HeaderData[0] = (byte) WordSizeLow;
            HeaderData[1] = (byte) WordSizeHi;
            HeaderData[2] = (byte) CompressMethod1;

            int readed = 3;
            int length = 0;
            while (readed < HeaderLength && 0 <= length) {
                length = in.read(HeaderData, readed, HeaderLength - readed);  //throws IOException
                readed += length;
            }

            if (readed == HeaderLength) {
                return HeaderData;
            } else {
                throw new EOFException();
            }
        } else {
            return null;
        }
    }

    /**
     * property �� �L�["lha.header" �Ɍ��ѕt����ꂽ���������g�p����
     * HeaderData ���� LhaHeader �̃C���X�^���X�𐶐�����B<br>
     *
     * @param HeaderData �w�b�_�̃f�[�^�����o�C�g�z��
     * @param property   LhaProperty.parse() �� LhaHeader �̃C���X�^���X�������ł���悤��
     *                   �������� �L�["lha.header" �̒l�Ƃ��Ď��v���p�e�B
     *
     * @return LhaHeader �̃C���X�^���X
     */
    public static LhaHeader createInstance(byte[] HeaderData,
                                           Properties property) {

        String encoding = property.getProperty("lha.encoding");
        if (encoding == null) {
            encoding = LhaProperty.getProperty("lha.encoding");
        }

        String packages = property.getProperty("lha.packages");
        if (packages == null) {
            packages = LhaProperty.getProperty("lha.packages");
        }

        String generator = property.getProperty("lha.header");
        if (generator == null) {
            generator = LhaProperty.getProperty("lha.header");
        }


        Hashtable substitute = new Hashtable();
        substitute.put("data", HeaderData);
        substitute.put("encoding", encoding);

        return (LhaHeader) LhaProperty.parse(generator, substitute, packages);
    }

    //------------------------------------------------------------------
    //  access method
    //------------------------------------------------------------------
    //  setter
    //------------------------------------------------------------------
    //  public void setCompressMethod( String method )
    //  public void setOriginalSize( long size )
    //  public void setCompressedSize( long size )
    //  public void setLastModified( Date date )
    //  public void setHeaderLevel( int level )
    //  public void setPath( String path )
    //  public void setCRC( int crc )
    //  public void setOSID( byte id )
    //  protected void setExtraData( byte[] data )
    //  protected void setLevel0DosAttribute( byte attribute )
    //  private void setFileName( String filename )
    //  private void setDirName( String dirname )
    //------------------------------------------------------------------

    /**
     * InputStream �� len �o�C�g�X�L�b�v����B
     *
     * @param in  ���̓X�g���[��
     * @param len �X�L�b�v���钷��
     *
     * @exception IOException  ���o�̓G���[�����������ꍇ
     * @exception EOFException EndOfStream �ɒB�����ꍇ�B
     */
    private static void ensureSkip(InputStream in, long len) throws IOException {
        while (0 < len) {
            long skiplen = in.skip(len);
            if (skiplen <= 0) {
                if (0 <= in.read()) {
                    len--;
                } else {
                    throw new EOFException();
                }
            } else {
                len -= skiplen;
            }
        }
    }

    /**
     * ���̃I�u�W�F�N�g�̃R�s�[���쐬���ĕԂ��B<br>
     *
     * @return ���̃I�u�W�F�N�g�̃R�s�[
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException exception) { //Ignore
            throw new Error("java.lang.Object is not support clone().");
        }
    }

    /**
     * �f�[�^�����k�������@�����ʂ��镶����𓾂�B<br>
     *
     * @return ���k�@������
     */
    public String getCompressMethod() {
        return this.Method;
    }

    /**
     * ���k�@�������ݒ肷��B
     *
     * @param method ���k�@������
     *
     * @exception IllegalArgumentException
     *               ���k�@������ '-' �Ŏn�܂��Ă��Ȃ����A
     *               '-' �ŏI����Ă��Ȃ��ꍇ�B
     */
    public void setCompressMethod(String method) {
        if (method == null) {
            throw new NullPointerException("method");
        } else if (!method.startsWith("-") || !method.endsWith("-")) {
            throw new IllegalArgumentException("method must starts with \'-\' and ends with \'-\'");
        } else {
            this.Method = method;
        }
    }

    /**
     * �f�[�^�̈��k�O�̃T�C�Y�𓾂�B<br>
     *
     * @return ���k�O�̃T�C�Y<br>
     *         LhaHeader( String path ) �܂���
     *         LhaHeader( String path, Date date )�Ő������ꂽ
     *         �C���X�^���X�͏�����Ԃł̓T�C�Y���s���̂���
     *         LhaHeader.UNKNOWN( -1 ) ��Ԃ��B<br>
     *
     * @see LhaHeader#UNKNOWN
     */
    public long getOriginalSize() {
        return this.OriginalSize;
    }

    /**
     * ���k�O�f�[�^�T�C�Y��ݒ肷��B<br>
     * LhaHeader.UNKNOWN( -1 ) �� �T�C�Y�s��������
     * ���ʂȐ����ł��邽�ߐݒ�ł��Ȃ��B<br>
     * �܂� ���x��0,1,3 �ł͏����ł���̂� 4�o�C�g�l�݂̂ł��邽��
     * 4�o�C�g�ŕ\���ł��Ȃ��l��ݒ肵���ꍇ getByte() ���ɗ�O�𓊂���B<br>
     *
     * @param size ���k�O�f�[�^�T�C�Y
     *
     * @exception IllegalArgumentException
     *             size �� LhaHeader.UNKNOWN( -1 )��ݒ肵�悤�Ƃ����ꍇ
     *
     * @see LhaHeader#UNKNOWN
     */
    public void setOriginalSize(long size) {
        if (size != LhaHeader.UNKNOWN) {
            this.OriginalSize = size;
        } else {
            throw new IllegalArgumentException("size must not LhaHeader.UNKNOWN( " + LhaHeader.UNKNOWN + " )");
        }
    }

    /**
     * �f�[�^�̈��k��̃T�C�Y�𓾂�B<br>
     *
     * @return ���k��̃T�C�Y<br>
     *         LhaHeader( String path ) �܂���
     *         LhaHeader( String path, Date date )�Ő������ꂽ
     *         �C���X�^���X�͏�����Ԃł̓T�C�Y���s���̂���
     *         LhaHeader.UNKNOWN( -1 ) ��Ԃ��B<br>
     *
     * @see LhaHeader#UNKNOWN
     */
    public long getCompressedSize() {
        return this.CompressedSize;
    }

    /**
     * ���k��f�[�^�T�C�Y��ݒ肷��B<br>
     * LhaHeader.UNKNOWN( -1 ) �� �T�C�Y�s��������
     * ���ʂȐ����ł��邽�ߐݒ�ł��Ȃ��B<br>
     * �܂� ���x��0,1,3 �ł͏����ł���̂� 4�o�C�g�l�݂̂ł��邽��
     * 4�o�C�g�ŕ\���ł��Ȃ��l��ݒ肵���ꍇ getByte() ���ɗ�O�𓊂���B<br>
     *
     * @param size ���k��f�[�^�T�C�Y
     *
     * @exception IllegalArgumentException
     *             size �� LhaHeader.UNKNOWN ��ݒ肵�悤�Ƃ���
     *
     * @see LhaHeader#UNKNOWN
     */
    public void setCompressedSize(long size) {
        if (size != LhaHeader.UNKNOWN) {
            this.CompressedSize = size;
        } else {
            throw new IllegalArgumentException("size must not LhaHeader.UNKNOWN( " + LhaHeader.UNKNOWN + " )");
        }
    }

    /**
     * �f�[�^�̍ŏI�X�V�����𓾂�B<br>
     *
     * @return �f�[�^�̍ŏI�X�V����
     */
    public Date getLastModified() {
        return new Date(this.LastModified.getTime());
    }

    /**
     * ���k�f�[�^�̍ŏI�X�V������ݒ肷��B<br>
     * �w�b�_���x���� 0,1 �̏ꍇ�� MsdosDate�ŕ\����͈͓��A
     * �w�b�_���x���� 2,3 �̏ꍇ�� 4byte �� time_t�ŕ\����͈͓�
     * �̓��t�Ŗ�����΂Ȃ�Ȃ��B<br>
     * �͈͓��łȂ��Ă� ���̃��\�b�h�͗�O�𓊂��Ȃ����Ƃɒ��ӂ�
     * �邱�ƁB�͈͓��ɖ����ꍇ�� ���̃��\�b�h�͗�O�𓊂��Ȃ����A
     * getBytes() ���ɗ�O�𓊂���B<br>
     *
     * @param date �ŏI�X�V����
     *
     * @exception IllegalArgumentException
     *               date �� null ��ݒ肵�悤�Ƃ����ꍇ
     */
    public void setLastModified(Date date) {
        if (date != null) {
            this.LastModified = date;
        } else {
            throw new NullPointerException("date");
        }
    }

    /**
     * ���̃w�b�_�̃w�b�_���x���𓾂�B<br>
     *
     * @return �w�b�_���x��
     */
    public int getHeaderLevel() {
        return this.HeaderLevel;
    }

    /**
     * �w�b�_���x����ݒ肷��B<br>
     * ���ݐݒ�ł���̂� 0,1,2,3 �݂̂ƂȂ��Ă���B<br>
     * �w�b�_���x���̕ύX�̓p�X�̍ő咷��ALastModified �̐����͈�
     * �Ȃǂ�ω������邽�ߒ��ӂ��K�v�ł���B<br>
     *
     * @param level �w�b�_���x��
     */
    public void setHeaderLevel(int level) {
        this.HeaderLevel = level;
    }

    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  import base header
    //------------------------------------------------------------------
    //  private void importLevel0Header( byte[] HeaderData, String encode )
    //  private void importLevel1Header( byte[] HeaderData, String encode )
    //  private void importLevel2Header( byte[] HeaderData, String encode )
    //  private void importLevel3Header( byte[] HeaderData, String encode )
    //  private void importHeader( byte[] HeaderData, String encode )
    //------------------------------------------------------------------

    /**
     * �f�[�^�̖��O�A
     * �������̓f�[�^���t�@�C���ł������ꍇ�̃p�X���𓾂�B<br>
     * �p�X���Ƃ͂����Ă��AWindows �n�� A: �̂悤��
     * �h���C�u�����܂�ł͂Ȃ�Ȃ��B<br>
     * �p�X�f���~�^�ɂ� File.separator ���g�p����B
     *
     * @return �f�[�^�̖��O�A�������� �p�X���B
     *
     * @see File#separator
     */
    public String getPath() {
        return this.Path;
    }

    /**
     * �f�[�^�̖��O�A�������̓f�[�^���t�@�C���ł���ꍇ�A
     * �f�[�^�̃p�X��ݒ肷��B<br>
     * �p�X�f���~�^�ɂ� File.separator ���g�p����B<br>
     * �w�b�_���x���ɂ���� path �ɂ̓o�C�g���̐��������݂��邪�A
     * ���̃��\�b�h�͐������z�����ꍇ�ł� ��O�𓊂��Ȃ����Ƃ�
     * ���ӁB�������z�����ꍇ�� ���̃��\�b�h�͗�O�𓊂��Ȃ����A
     * getBytes()���ɗ�O�𓊂���<br>
     *
     * @param path �f�[�^�̖��O�A�������̓t�@�C����
     *
     * @exception IllegalArgumentException
     *              path ���󕶎���ł���ꍇ
     *
     * @see File#separator
     */
    public void setPath(String path) {
        if (path == null) {
            throw new NullPointerException("path");
        } else if (path.equals("")) {
            throw new IllegalArgumentException("path must not empty.");
        } else {
            this.Path = path;
        }
    }

    /**
     * �f�[�^��CRC16�l�𓾂�B<br>
     *
     * @return �f�[�^��CRC16�l<br>
     *         LhaHeader( String path ) �܂���
     *         LhaHeader( String path, Date date )�Ő������ꂽ
     *         �C���X�^���X�͏�����Ԃł�CRC���s���̂���
     *         LhaHeader.UNKNOWN( -1 ) ��Ԃ��B<br>
     *         ���x��0�w�b�_��CRC16�l��
     *         �t�B�[���h�������ꍇ��
     *         LhaHeader.NO_CRC( -2 )��Ԃ�<br>
     *
     * @see LhaHeader#UNKNOWN
     * @see LhaHeader#NO_CRC
     */
    public int getCRC() {
        return this.CRC;
    }

    /**
     * ���k�O�̃f�[�^�� CRC16�l��ݒ肷��B<br>
     * LhaHeader.UNKNOWN( -1 ) �� �T�C�Y�s��������
     * ���ʂȐ����ł��邽�ߐݒ�ł��Ȃ��B<br>
     * LhaHeader.NO_CRC( -2 ) �� ���x��0�w�b�_�̏�
     * ���� CRC�l���o�͂��Ȃ����Ƃ��Ӗ�������ʂȒl
     * �ł���B<br>
     * ���̃w�b�_���x���̎��� LhaHeader.NO_CRC( -2 )
     * ��ݒ肵�Ă���O�𓊂��Ȃ��� getBytes() ����
     * ��O�𓊂���̂Œ��ӂ��邱�ƁB<br>
     * �L���Ȃ͉̂���2�o�C�g�ŁA���2�o�C�g�͖��������B<br>
     *
     * @param crc �f�[�^�̈��k�O��CRC16�l
     *
     * @exception IllegalArgumentException
     *             crc �� LhaHeader.UNKNOWN ��ݒ肵�悤�Ƃ���
     *
     * @see LhaHeader#UNKNOWN
     * @see LhaHeader#NO_CRC
     */
    public void setCRC(int crc) {
        if (crc != LhaHeader.UNKNOWN) {
            this.CRC = crc;
        } else {
            throw new IllegalArgumentException("crc must not LhaHeader.UNKNOWN( " + LhaHeader.UNKNOWN + " )");
        }
    }

    /**
     * ���̃w�b�_���쐬���� OS �̎��ʎq�𓾂�B
     *
     * @return OS�̎��ʎq
     */
    public byte getOSID() {
        return this.OSID;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  import extend header
    //------------------------------------------------------------------
    //  private void importCommonExtHeader( byte[] HeaderData, int index, int length )
    //  private void importFileNameExtHeader( byte[] HeaderData, int index, 
    //                                        int length, String encode )
    //  private void importDirNameExtHeader( byte[] HeaderData, int index, 
    //                                       int length, String encode )
    //  protected void importExtendHeader( byte[] HeaderData, int index, 
    //                                     int length, String encode )
    //  private void importExtHeader( byte[] HeaderData, int index,
    //                                int length, String encode )
    //------------------------------------------------------------------

    /**
     * ���̃w�b�_��OS�ŗL�̏�񂪊܂܂��ꍇ�A
     * ���̃f�[�^�����߂���肪����Ƃ��� OS�̎��ʎq��ݒ肷��B<br>
     *
     * @param id OS���ʎq
     */
    public void setOSID(byte id) {
        this.OSID = id;
    }

    /**
     * ���x�� 0 �w�b�_�A ���x�� 1 �w�b�_�̎���
     * �t�������\���������{�w�b�_���̊g���f�[�^�𓾂�B
     *
     * @return �g���f�[�^
     */
    protected byte[] getExtraData() {
        return (byte[]) this.ExtraData.clone();
    }

    /**
     * ���x�� 0,1�w�b�_���Ɏg�p����� ��{�w�b�_��
     * �g������ݒ肷��B<br>
     * �g�����̃o�C�g���ɂ͐��������݂��邪�A���̃��\�b�h��
     * �������z���Ă���O�𓊂��Ȃ����Ƃɒ��ӁB�������z�����ꍇ
     * getBytes()���ɗ�O�𓊂���B<br>
     *
     * @param data �g�����
     *             �g�������o�͂��Ȃ��ꍇ�� null��ݒ肷��B
     */
    protected void setExtraData(byte[] data) {
        this.ExtraData = data;
    }

    /**
     * ���x�� 0 �w�b�_�ɋL�����
     * DOS �̃t�@�C�������𓾂�B
     *
     * @return DOS �� �t�@�C������
     */
    protected byte getLevel0DosAttribute() {
        return this.Level0DosAttribute;
    }

    /**
     * ���x�� 0�w�b�_�̏ꍇ�ɏo�͂����A
     * MS-DOS �̃t�@�C��������ݒ肷��B
     *
     * @param attribute MS-DOS�̃t�@�C������
     */
    protected void setLevel0DosAttribute(byte attribute) {
        this.Level0DosAttribute = attribute;
    }

    /**
     * �p�X������؂蕪����ꂽ�t�@�C�����𓾂�B
     *
     * @return �t�@�C����
     */
    private String getFileName() {
        return this.Path.substring(
                this.Path.lastIndexOf(File.separatorChar) + 1);
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  export base header
    //------------------------------------------------------------------
    //  private byte[] exportLevel0Header( String encode )
    //  private byte[] exportLevel1Header( String encode )
    //  private byte[] exportLevel2Header( String encode )
    //  private byte[] exportLevel3Header( String encode )
    //  private byte[] exportHeader( String encode )
    //------------------------------------------------------------------

    /**
     * filename �Ŏw�肳���t�@�C�������p�X���ɐݒ肷��B
     *
     * @param filename �t�@�C����
     */
    private void setFileName(String filename) {
        this.Path = this.getDirName() + filename;
    }

    /**
     * �p�X������؂蕪����ꂽ�f�B���N�g�����𓾂�B
     *
     * @return �f�B���N�g����
     */
    private String getDirName() {
        return this.Path.substring(0,
                this.Path.lastIndexOf(File.separatorChar) + 1);
    }

    /**
     * dirname �Ŏw�肳��� �f�B���N�g�������p�X���ɐݒ肷��B
     *
     * @param dirname �f�B���N�g����
     */
    private void setDirName(String dirname) {
        this.Path = dirname + this.getFileName();
    }

    /**
     * ����LhaHeader�̃f�[�^���g�p���� �w�b�_�f�[�^�𐶐����A
     * ������o�C�g�z��̌`�œ���B<br>
     * �G���R�[�h�̓f�t�H���g�̂��̂��g�p�����B
     *
     * @return �o�C�g�z��Ɋi�[�����w�b�_�f�[�^
     *
     * @exception IllegalStateException <br>
     *                <ol>
     *                   <li>���k�@�������encode�Ńo�C�g�z���
     *                       �������̂� 5byte�Ŗ����ꍇ
     *                   <li>���x��0,1,2�� �t�@�C�������������邽��
     *                       �w�b�_�Ɏ��܂肫��Ȃ��B
     *                   <li>���x��1,2�ŋ��ʊg���w�b�_���傫�����ďo�͂ł��Ȃ��B
     *                       ���̂��߃w�b�_��CRC�i�[�ꏊ�������B
     *                   <li>���x��0�ȊO�� CRC �� ���x��0�w�b�_��
     *                       CRC��񂪖��������������ʂȒl�ł���
     *                       LhaHeader.NO_CRC( -2 ) ���ݒ肳��Ă����B
     *                   <li>���x��0,1�̎���LastModified��MS-DOS�`��
     *                       �ŕ\���ł��Ȃ��͈͂̎��Ԃł������ꍇ
     *                   <li>���x��2,3�̎���LastModified��4�o�C�g��
     *                       time_t�ŕ\���ł��Ȃ��͈͂̎��Ԃł������ꍇ
     *                   <li>OriginalSize �ɃT�C�Y���s���ł��鎖������
     *                       ���ʂȒl�ł��� LhaHeader.UNKNOWN( -1 )���ݒ肳��Ă����B
     *                   <li>OriginalSize �����l�ł���ꍇ
     *                   <li>���x��0,1,3 �̎��� OriginalSize ��
     *                       4byte�l�ŕ\���ł��Ȃ��l�ł���ꍇ
     *                   <li>CompressedSize �ɃT�C�Y���s���ł��鎖������
     *                       ���ʂȒl�ł��� LhaHeader.UNKNOWN( -1 )���ݒ肳��Ă����B
     *                   <li>CompressedSize �����l�ł���ꍇ
     *                   <li>���x��0,1,3 �̎��� CompressedSize ��
     *                       4byte�l�ŕ\���ł��Ȃ��l�ł���ꍇ
     *                   <li>���x��2�̎���OriginalSize �܂��� CompressedSize��
     *                       4�o�C�g�l�𒴂��邽�߃t�@�C���T�C�Y�w�b�_���K�v�ȍۂ�
     *                       ���̊g���w�b�_���傫�����ăt�@�C���T�C�Y�w�b�_���o�͏o���Ȃ��ꍇ�B
     *                   <li>CRC ��CRC16�l���s���ł��鎖������
     *                       ���ʂȒl�ł��� LhaHeader.UNKNOWN( -1 )���ݒ肳��Ă����B
     *                   <li>�w�b�_���x���� 0,1,2,3 �ȊO�ł���ꍇ
     *                 </ol>
     *                 �̉��ꂩ�B
     */
    public byte[] getBytes() {
        try {
            return this.exportHeader(LhaProperty.encoding);
        } catch (UnsupportedEncodingException exception) {
            throw new Error("Java Runtime Environment not support " + LhaProperty.encoding + " encoding");
        }
    }

    /**
     * ����LhaHeader�̃f�[�^���g�p���� �w�b�_�f�[�^�𐶐����A
     * ������o�C�g�z��̌`�œ���B<br>
     *
     * @param encode ����������o�͂���ۂɎg�p����
     *               �G���R�[�h
     *
     * @return �o�C�g�z��Ɋi�[�����w�b�_�f�[�^
     *
     * @exception IllegalStateException
     *                <ol>
     *                   <li>���k�@�������encode�Ńo�C�g�z���
     *                       �������̂� 5byte�Ŗ����ꍇ
     *                   <li>���x��0,1,2�� �t�@�C�������������邽��
     *                       �w�b�_�Ɏ��܂肫��Ȃ��B
     *                   <li>���x��1,2�ŋ��ʊg���w�b�_���傫�����ďo�͂ł��Ȃ��B
     *                       ���̂��߃w�b�_��CRC�i�[�ꏊ�������B
     *                   <li>���x��0�ȊO�� CRC �� ���x��0�w�b�_��
     *                       CRC��񂪖��������������ʂȒl�ł���
     *                       LhaHeader.NO_CRC( -2 ) ���ݒ肳��Ă����B
     *                   <li>���x��0,1�̎���LastModified��MS-DOS�`��
     *                       �ŕ\���ł��Ȃ��͈͂̎��Ԃł������ꍇ
     *                   <li>���x��2,3�̎���LastModified��4�o�C�g��
     *                       time_t�ŕ\���ł��Ȃ��͈͂̎��Ԃł������ꍇ
     *                   <li>OriginalSize �ɃT�C�Y���s���ł��鎖������
     *                       ���ʂȒl�ł��� LhaHeader.UNKNOWN( -1 )���ݒ肳��Ă����B
     *                   <li>OriginalSize �����l�ł���ꍇ
     *                   <li>���x��0,1,3 �̎��� OriginalSize ��
     *                       4byte�l�ŕ\���ł��Ȃ��l�ł���ꍇ
     *                   <li>CompressedSize �ɃT�C�Y���s���ł��鎖������
     *                       ���ʂȒl�ł��� LhaHeader.UNKNOWN( -1 )���ݒ肳��Ă����B
     *                   <li>CompressedSize �����l�ł���ꍇ
     *                   <li>���x��0,1,3 �̎��� CompressedSize ��
     *                       4byte�l�ŕ\���ł��Ȃ��l�ł���ꍇ
     *                   <li>���x��2�̎���OriginalSize �܂��� CompressedSize��
     *                       4�o�C�g�l�𒴂��邽�߃t�@�C���T�C�Y�w�b�_���K�v�ȍۂ�
     *                       ���̊g���w�b�_���傫�����ăt�@�C���T�C�Y�w�b�_���o�͏o���Ȃ��ꍇ�B
     *                   <li>CRC ��CRC16�l���s���ł��鎖������
     *                       ���ʂȒl�ł��� LhaHeader.UNKNOWN( -1 )���ݒ肳��Ă����B
     *                   <li>�w�b�_���x���� 0,1,2,3 �ȊO�ł���ꍇ
     *                 </ol>
     *                 �̉��ꂩ�B
     * @exception UnsupportedEncodingException
     *                   encode �Ŏw�肳�ꂽ�G���R�[�h��
     *                   �T�|�[�g����Ȃ��ꍇ
     */
    public byte[] getBytes(String encode)
            throws UnsupportedEncodingException {
        return this.exportHeader(encode);                                     //throw UnsupportedEncodingException
    }

    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  export extend header
    //------------------------------------------------------------------
    //  private byte[] exportCommonExtHeader()
    //  private byte[] exportFileNameExtHeader( String encode )
    //  private byte[] exportDirNameExtHeader( String encode )
    //  private byte[] exportFileSizeHeader()
    //  protected byte[][] exportExtendHeaders( String encode )
    //  private byte[][] exportExtHeader( String encode )
    //------------------------------------------------------------------

    /**
     * HeaderData�����x��0�w�b�_�̃f�[�^�Ƃ��ĉ��߂��A
     * ����LhaHeader�ɒl��ݒ肷��B
     *
     * @param HeaderData �w�b�_�f�[�^
     * @param encode     ������������߂���ۂɎg�p����
     *                   �G���R�[�h
     *
     * @exception UnsupportedEncodingException
     *                   encode �Ŏw�肳�ꂽ�G���R�[�h��
     *                   �T�|�[�g����Ȃ��ꍇ
     */
    private void importLevel0Header(byte[] HeaderData, String encode)
            throws UnsupportedEncodingException {

        //------------------------------------------------------------------
        //  �w�b�_�f�[�^�ʒu�̒�`
        final int HeaderSizeIndex = 0;
        final int HeaderSize = (HeaderData[HeaderSizeIndex] & 0xFF) + 2;
        final int CompressMethodIndex = 2;
        final int CompressedSizeIndex = 7;
        final int OriginalSizeIndex = 11;
        final int LastModifiedIndex = 15;
        final int DosAttributeIndex = 19;
        final int HeaderLevelIndex = 20;
        final int PathLengthIndex = 21;
        final int PathLength = HeaderData[PathLengthIndex] & 0xFF;
        final int PathIndex = 22;
        final int CRCIndex = 22 + PathLength;
        final int ExtraDataIndex = 24 + PathLength;
        final int ExtraDataLength = HeaderSize - ExtraDataIndex;

        //------------------------------------------------------------------
        //  �w�b�_�f�[�^�ǂݍ���
        this.Method = new String(HeaderData, CompressMethodIndex, 5, encode);//After Java 1.1 throw UnsupportedEncodingException
        this.CompressedSize = ((long) LittleEndian.readInt(HeaderData, CompressedSizeIndex)) & 0xFFFFFFFFL;
        this.OriginalSize = ((long) LittleEndian.readInt(HeaderData, OriginalSizeIndex)) & 0xFFFFFFFFL;
        this.LastModified = new MsdosDate(LittleEndian.readInt(HeaderData, LastModifiedIndex));
        this.Level0DosAttribute = HeaderData[DosAttributeIndex];
        this.HeaderLevel = HeaderData[HeaderLevelIndex] & 0xFF;
        this.Path = new String(HeaderData, PathIndex, PathLength, encode);    //After Java 1.1 throw IndexOutOfBoundsException
        this.Path = this.Path.replace('\\', File.separatorChar);

        if (CRCIndex + 2 <= HeaderSize) {
            this.CRC = LittleEndian.readShort(HeaderData, CRCIndex);          //throw ArrayIndexOutOfBoundsException
            if (0 < ExtraDataLength) {
                this.ExtraData = new byte[ExtraDataLength];
                System.arraycopy(HeaderData, ExtraDataIndex,
                        this.ExtraData, 0, ExtraDataLength);         //throw IndexOutOfBoundsException
            }
        } else {
            this.CRC = LhaHeader.NO_CRC;
        }
    }

    /**
     * HeaderData�����x��1�w�b�_�̃f�[�^�Ƃ��ĉ��߂��A
     * ����LhaHeader�ɒl��ݒ肷��B
     *
     * @param HeaderData �w�b�_�f�[�^
     * @param encode     ������������߂���ۂɎg�p����
     *                   �G���R�[�h
     *
     * @exception UnsupportedEncodingException
     *                   encode �Ŏw�肳�ꂽ�G���R�[�h��
     *                   �T�|�[�g����Ȃ��ꍇ
     */
    private void importLevel1Header(byte[] HeaderData, String encode)
            throws UnsupportedEncodingException {

        //------------------------------------------------------------------
        //  ��{�w�b�_���f�[�^�ʒu�̒�`
        final int BaseHeaderSizeIndex = 0;
        final int BaseHeaderSize = (int) (HeaderData[BaseHeaderSizeIndex] & 0xFF) + 2;
        final int CompressMethodIndex = 2;
        final int SkipSizeIndex = 7;
        final int OriginalSizeIndex = 11;
        final int LastModifiedIndex = 15;
        final int HeaderLevelIndex = 20;
        final int FileNameLengthIndex = 21;
        final int FileNameLength = (int) (HeaderData[FileNameLengthIndex] & 0xFF);
        final int FileNameIndex = 22;
        final int CRCIndex = 22 + FileNameLength;
        final int OSIDIndex = 24 + FileNameLength;
        final int ExtraDataIndex = 25 + FileNameLength;
        final int ExtraDataLength = BaseHeaderSize - ExtraDataIndex - 2;

        //------------------------------------------------------------------
        //  ��{�w�b�_�f�[�^�ǂݍ���
        this.Method = new String(HeaderData, CompressMethodIndex, 5, encode);//After Java 1.1 throws UnsupportedEncodingException
        this.CompressedSize = ((long) LittleEndian.readInt(HeaderData, SkipSizeIndex)) & 0xFFFFFFFFL;
        this.OriginalSize = ((long) LittleEndian.readInt(HeaderData, OriginalSizeIndex)) & 0xFFFFFFFFL;
        this.LastModified = new MsdosDate(LittleEndian.readInt(HeaderData, LastModifiedIndex));
        this.HeaderLevel = HeaderData[HeaderLevelIndex] & 0xFF;
        this.Path = new String(HeaderData, FileNameIndex, FileNameLength, encode);//After Java 1.1 throw IndexOutOfBoundsException
        this.CRC = LittleEndian.readShort(HeaderData, CRCIndex);   //throw ArrayIndexOutOfBoundsException
        this.OSID = HeaderData[OSIDIndex];                          //throw ArrayIndexOutOfBoundsException
        if (0 < ExtraDataLength) {
            this.ExtraData = new byte[ExtraDataLength];
            System.arraycopy(HeaderData, ExtraDataIndex,
                    this.ExtraData, 0, ExtraDataLength);             //throw IndexOutOfBoundsException
        }

        //------------------------------------------------------------------
        //  �g���w�b�_�f�[�^�̓ǂݍ���
        boolean hasFileSize = false;
        int index = BaseHeaderSize;
        int length = LittleEndian.readShort(HeaderData, index - 2);           //throw ArrayIndexOutOfBoundsException
        while (length != 0) {
            if (!hasFileSize) {
                this.CompressedSize -= length;
            }

            this.importExtHeader(HeaderData, index, length - 2, encode);      //throw IndexOutOfBoundsException
            if (HeaderData[index] == (byte) 0x42) {
                hasFileSize = true;
            }

            index += length;
            length = LittleEndian.readShort(HeaderData, index - 2);           //throw ArrayIndexOutOfBoundsException
        }
    }

    /**
     * HeaderData�����x��2�w�b�_�̃f�[�^�Ƃ��ĉ��߂��A
     * ����LhaHeader�ɒl��ݒ肷��B
     *
     * @param HeaderData �w�b�_�f�[�^
     * @param encode     ������������߂���ۂɎg�p����
     *                   �G���R�[�h
     *
     * @exception UnsupportedEncodingException
     *                   encode �Ŏw�肳�ꂽ�G���R�[�h��
     *                   �T�|�[�g����Ȃ��ꍇ
     */
    private void importLevel2Header(byte[] HeaderData, String encode)
            throws UnsupportedEncodingException {

        //------------------------------------------------------------------
        //  ��{�w�b�_���f�[�^�ʒu�̒�`
        final int HeaderSizeIndex = 0;
        final int HeaderSize = LittleEndian.readShort(HeaderData, HeaderSizeIndex);
        final int CompressMethodIndex = 2;
        final int CompressedSizeIndex = 7;
        final int OriginalSizeIndex = 11;
        final int LastModifiedIndex = 15;
        final int HeaderLevelIndex = 20;
        final int CRCIndex = 21;
        final int OSIDIndex = 23;

        //------------------------------------------------------------------
        //  ��{�w�b�_�f�[�^�ǂݍ���
        this.Method = new String(HeaderData, CompressMethodIndex, 5, encode);//After Java 1.1  throw UnsupportedEncodingException
        this.CompressedSize = ((long) LittleEndian.readInt(HeaderData, CompressedSizeIndex)) & 0xFFFFFFFFL;
        this.OriginalSize = ((long) LittleEndian.readInt(HeaderData, OriginalSizeIndex)) & 0xFFFFFFFFL;
        this.LastModified = new Date((long) LittleEndian.readInt(HeaderData, LastModifiedIndex) * 1000L);
        this.HeaderLevel = HeaderData[HeaderLevelIndex] & 0xFF;
        this.CRC = LittleEndian.readShort(HeaderData, CRCIndex);   //throw ArrayIndexOutOfBoundsException
        this.OSID = HeaderData[OSIDIndex];                          //throw ArrayIndexOutOfBoundsException

        //------------------------------------------------------------------
        //  �g���w�b�_�f�[�^�̓ǂݍ���
        final int BaseHeaderSize = 26;
        int index = BaseHeaderSize;
        int length = LittleEndian.readShort(HeaderData, index - 2);           //throw ArrayIndexOutOfBoundsException
        while (length != 0) {
            this.importExtHeader(HeaderData, index, length - 2, encode);      //throw IndexOutOfBoundsException
            index += length;
            length = LittleEndian.readShort(HeaderData, index - 2);           //throw ArrayIndexOutOfBoundsException
        }
    }

    /**
     * HeaderData�����x��3�w�b�_�̃f�[�^�Ƃ��ĉ��߂��A
     * ����LhaHeader�ɒl��ݒ肷��B
     *
     * @param HeaderData �w�b�_�f�[�^
     * @param encode     ������������߂���ۂɎg�p����
     *                   �G���R�[�h
     *
     * @exception UnsupportedEncodingException
     *                   encode �Ŏw�肳�ꂽ�G���R�[�h��
     *                   �T�|�[�g����Ȃ��ꍇ
     */
    private void importLevel3Header(byte[] HeaderData, String encode)
            throws UnsupportedEncodingException {

        //------------------------------------------------------------------
        //  ��{�w�b�_���f�[�^�ʒu�̒�`
        final int WordSizeIndex = 0;
        final int WordSize = LittleEndian.readShort(HeaderData, WordSizeIndex);
        final int CompressMethodIndex = 2;
        final int CompressedSizeIndex = 7;
        final int OriginalSizeIndex = 11;
        final int LastModifiedIndex = 15;
        final int HeaderLevelIndex = 20;
        final int CRCIndex = 21;
        final int OSIDIndex = 23;

        //------------------------------------------------------------------
        //  ��{�w�b�_�f�[�^�ǂݍ���
        this.Method = new String(HeaderData, CompressMethodIndex, 5, encode);//After Java 1.1 throw UnsupportedEncodingException
        this.CompressedSize = ((long) LittleEndian.readInt(HeaderData, CompressedSizeIndex)) & 0xFFFFFFFFL;
        this.OriginalSize = ((long) LittleEndian.readInt(HeaderData, OriginalSizeIndex)) & 0xFFFFFFFFL;
        this.LastModified = new Date((long) LittleEndian.readInt(HeaderData, LastModifiedIndex) * 1000L);
        this.HeaderLevel = HeaderData[HeaderLevelIndex] & 0xFF;
        this.CRC = LittleEndian.readShort(HeaderData, CRCIndex);   //throw ArrayIndexOutOfBoundsException
        this.OSID = HeaderData[OSIDIndex];                          //throw ArrayIndexOutOfBoundsException

        //------------------------------------------------------------------
        //  �g���w�b�_�f�[�^�̓ǂݍ���
        final int BaseHeaderSize = 32;
        int index = BaseHeaderSize;
        int length = LittleEndian.readInt(HeaderData, index - 4);             //throw ArrayIndexOutOfBoundsException
        while (length != 0) {
            this.importExtHeader(HeaderData, index, length - 4, encode);      //throw IndexOutOfBoundsException
            index += length;
            length = LittleEndian.readInt(HeaderData, index - 4);             //throw ArrayIndexOutOfBoundsException
        }
    }

    /**
     * HeaderData �� LHA�w�b�_�f�[�^�Ƃ��ĉ��߂�
     * LhaHeader �ɒl��ݒ肷��B
     *
     * @param HeaderData �w�b�_�f�[�^
     * @param encode     ������������߂���ۂɎg�p����
     *                   �G���R�[�h
     *
     * @exception IndexOutOfBoundsException
     *                   �w�b�_�f�[�^�����Ă��邽��
     *                   �f�[�^������Ɖ��肵���ʒu��
     *                   HeaderData �͈̔͊O�ɂȂ���
     * @exception UnsupportedEncodingException
     *                   encode �Ŏw�肳�ꂽ�G���R�[�h��
     *                   �T�|�[�g����Ȃ��ꍇ
     * @exception IllegalArgumentException
     *                   �w�b�_���x���� 0,1,2,3 �̉���ł��Ȃ��B
     */
    private void importHeader(byte[] HeaderData, String encode)
            throws UnsupportedEncodingException {
        final int HeaderLevelIndex = 20;

        switch (HeaderData[HeaderLevelIndex]) {                                 //throws ArrayIndexOutOfBoundsException
            case 0:
                this.importLevel0Header(HeaderData, encode);                      //After Java1.1 throws UnsupporetdEncodingException, InexOutOfBoundsException
                break;
            case 1:
                this.importLevel1Header(HeaderData, encode);                      //After Java1.1 throws UnsupporetdEncodingException, InexOutOfBoundsException
                break;
            case 2:
                this.importLevel2Header(HeaderData, encode);                      //After Java1.1 throws UnsupporetdEncodingException, InexOutOfBoundsException
                break;
            case 3:
                this.importLevel3Header(HeaderData, encode);                      //After Java1.1 throws UnsupporetdEncodingException, InexOutOfBoundsException
                break;

            default:
                throw new IllegalArgumentException("unknown header level \"" + HeaderData[HeaderLevelIndex] + "\".");
        }
    }

    /**
     * HeaderData ���� ���ʊg���w�b�_��ǂݍ��ށB
     * ���̃��\�b�h�͋��ʊg���w�b�_�� �w�b�_�����p��CRC16�l�ȊO
     * �̃f�[�^�����݂����ꍇ ���ʊg���w�b�_�� ExtraExtHeaders ��
     * �o�^���邾���ł���B
     *
     * @param HeaderData �w�b�_�f�[�^
     * @param index      HeaderData���̊g���w�b�_�̊J�n�ʒu
     * @param length     �g���w�b�_�̒���
     */
    private void importCommonExtHeader(byte[] HeaderData,
                                       int index,
                                       int length) {
        //( 3 < length )�̔�r�� �g���w�b�_ID(1byte)��
        //�w�b�_��CRC16�l(2byte)�ȊO�Ƀf�[�^���܂ނ��̔���B
        //CRC16�l�ȊO�̏������Ȃ� ���̏���ۑ����邽��
        //ExtraExtHeaders�ɓo�^����B
        if (3 < length) {
            if (this.ExtraExtHeaders == null) {
                this.ExtraExtHeaders = new Vector();
            }
            byte[] ExtHeaderData = new byte[length];
            System.arraycopy(HeaderData, index, ExtHeaderData, 0, length);    //throws IndexOutOfBoundsException
            this.ExtraExtHeaders.addElement(ExtHeaderData);
        }
    }


    //------------------------------------------------------------------
    //  shared method
    //------------------------------------------------------------------
    //  public static boolean checkHeaderData( byte[] HeaderData )
    //------------------------------------------------------------------

    /**
     * HeaderData ���� �t�@�C�����g���w�b�_��ǂݍ��ށB
     *
     * @param HeaderData �w�b�_�f�[�^
     * @param index      HeaderData���̊g���w�b�_�̊J�n�ʒu
     * @param length     �g���w�b�_�̒���
     * @param encode     ������������߂���ۂɎg�p����
     *                   �G���R�[�h
     *
     * @exception UnsupportedEncodingException
     *                   ���̗�O���������邱�Ƃ͖����B
     */
    private void importFileNameExtHeader(byte[] HeaderData,
                                         int index,
                                         int length,
                                         String encode)
            throws UnsupportedEncodingException {

        this.setFileName(new String(HeaderData, index + 1, length - 1, encode));//throws IndexOutOfBoundsException
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  check header
    //------------------------------------------------------------------
    //  private static int getCRC16Position( byte[] HeaderData )
    //  private static int calcHeaderChecksum( byte[] HeaderData )
    //  private static int calcHeaderCRC16( byte[] HeaderData )
    //  private static int readHeaderChecksum( byte[] HeaderData )
    //  private static int readHeaderCRC16( byte[] HeaderData )
    //  private static boolean verifyHeaderCRC16( byte[] HeaderData )
    //  public static boolean checkHeaderData( byte[] HeaderData )
    //------------------------------------------------------------------

    /**
     * HeaderData ���� �f�B���N�g�����g���w�b�_��ǂݍ��ށB
     *
     * @param HeaderData �w�b�_�f�[�^
     * @param index      HeaderData���̊g���w�b�_�̊J�n�ʒu
     * @param length     �g���w�b�_�̒���
     * @param encode     ������������߂���ۂɎg�p����
     *                   �G���R�[�h
     *
     * @exception UnsupportedEncodingException
     *                   ���̗�O���������邱�Ƃ͖����B
     */
    private void importDirNameExtHeader(byte[] HeaderData,
                                        int index,
                                        int length,
                                        String encode)
            throws UnsupportedEncodingException {

        final byte LhaFileSeparator = (byte) 0xFF;

        int off = 1;
        String dir = "";
        while (off < length) {
            int len = 0;
            while (off + len < length) {
                if (HeaderData[index + off + len] != LhaFileSeparator) {
                    len++;
                } else {
                    break;
                }
            }

            if (off + len < length) {
                dir += new String(HeaderData, index + off, len, encode) + File.separator;
            } else {
                dir += new String(HeaderData, index + off, len, encode);
            }
            off += len + 1;
        }

        this.setDirName(dir);
    }

    /**
     * HeaderData ���� �t�@�C���T�C�Y�g���w�b�_��ǂݍ��ށB
     *
     * @param HeaderData �w�b�_�f�[�^
     * @param index      HeaderData���̊g���w�b�_�̊J�n�ʒu
     * @param length     �g���w�b�_�̒���
     */
    private void importFileSizeHeader(byte[] HeaderData,
                                      int index,
                                      int length) {
        if (length == 17) {
            this.CompressedSize = LittleEndian.readLong(HeaderData, index + 1);
            this.OriginalSize = LittleEndian.readLong(HeaderData, index + 9);
        } else {
        }
    }

    /**
     * �g���w�b�_��ǂݍ��ށB
     * ���̃��\�b�h���I�[�o�[���C�h���鎖�ɂ����
     * �l�X�Ȋg���w�b�_�ɑΉ����邱�Ƃ��\�ƂȂ�B
     * LhaHeader �ł� �g���w�b�_�� private �����o�ł���
     * ExtraExtHeaders �ɓo�^���邾���ł���B
     *
     * @param HeaderData �w�b�_�f�[�^
     * @param index      HeaderData���̊g���w�b�_�̊J�n�ʒu
     * @param length     �g���w�b�_�̒���
     * @param encode     ������������߂���ۂɎg�p����
     *                   �G���R�[�h
     *
     * @exception UnsupportedEncodingException
     *                   encode �Ŏw�肳�ꂽ�G���R�[�h��
     *                   �T�|�[�g����Ȃ��ꍇ
     */
    protected void importExtendHeader(byte[] HeaderData,
                                      int index,
                                      int length,
                                      String encode)
            throws UnsupportedEncodingException {
        if (this.ExtraExtHeaders == null) {
            this.ExtraExtHeaders = new Vector();
        }
        byte[] ExtHeaderData = new byte[length];
        System.arraycopy(HeaderData, index, ExtHeaderData, 0, length);        //throws IndexOutOfBoundsException
        this.ExtraExtHeaders.addElement(ExtHeaderData);
    }

    /**
     * HeaderData �� index ����͂��܂� length �o�C�g��
     * �g���w�b�_��ǂݍ��ށB
     *
     * @param HeaderData �w�b�_�f�[�^
     * @param index      HeaderData���̊g���w�b�_�̊J�n�ʒu
     * @param length     �g���w�b�_�̒���
     * @param encode     ������������߂���ۂɎg�p����
     *                   �G���R�[�h
     *
     * @exception UnsupportedEncodingException
     *                   encode �Ŏw�肳�ꂽ�G���R�[�h��
     *                   �T�|�[�g����Ȃ��ꍇ
     */
    private void importExtHeader(byte[] HeaderData,
                                 int index,
                                 int length,
                                 String encode)
            throws UnsupportedEncodingException {
        final int ExtendHeaderIDIndex = 0;

        switch (HeaderData[index + ExtendHeaderIDIndex]) {                    //throws ArrayIndexOutOfBoundsException
            case 0x00:
                this.importCommonExtHeader(HeaderData, index, length);            //throws IndexOutOfBoundsException
                break;
            case 0x01:
                this.importFileNameExtHeader(HeaderData, index, length, encode);  //throws IndexOutOfBoundsException
                break;
            case 0x02:
                this.importDirNameExtHeader(HeaderData, index, length, encode);   //throws IndexOutOfBoundsException
                break;
            case 0x42:
                this.importFileSizeHeader(HeaderData, index, length);             //throws IndexOutOfBoundsException
                break;
            default:
                this.importExtendHeader(HeaderData, index, length, encode);       //throws UnsupportedEncodingException IndexOutOfBoundsException
        }
    }

    /**
     * ���� LhaHeader �̏����g����
     * ���x��0�w�b�_�̃f�[�^�𐶐�����B<br>
     * ���̍ہAExtraData ���܂߂�ƃw�b�_�T�C�Y��
     * �K��l�Ɏ��܂�Ȃ��ꍇ�� ExtraData �͊܂܂�Ȃ����Ƃ�����B
     *
     * @param encode ����������o�͂���ۂɎg�p����
     *               �G���R�[�h
     *
     * @exception IllegalStateException <br>
     *                <ol>
     *                   <li>���k�@�������encode�Ńo�C�g�z���
     *                       �������̂� 5byte�Ŗ����ꍇ
     *                   <li>Path ���傫�����邽�� ���x��0�w�b�_��
     *                       �ő�T�C�Y�Ɏ��܂肫��Ȃ��B
     *                   <li>LastModified��MS-DOS�`����
     *                       �\���ł��Ȃ��͈͂̎��Ԃł������ꍇ
     *                   <li>OriginalSize �ɃT�C�Y���s���ł��鎖������
     *                       ���ʂȒl�ł��� LhaHeader.UNKNOWN( -1 )���ݒ�
     *                       ����Ă����B
     *                   <li>OriginalSize �����l�ł��邩�A
     *                       4byte�l�ŕ\���ł��Ȃ��l�ł���ꍇ
     *                   <li>CompressedSize �ɃT�C�Y���s���ł��鎖������
     *                       ���ʂȒl�ł��� LhaHeader.UNKNOWN( -1 )��
     *                       �ݒ肳��Ă����B
     *                   <li>CompressedSize �����l�ł��邩�A
     *                       4byte�l�ŕ\���ł��Ȃ��l�ł���ꍇ
     *                 </ol>
     *                 �̉��ꂩ
     * @exception UnsupportedEncodingException
     *                   encode �Ŏw�肳�ꂽ�G���R�[�h��
     *                   �T�|�[�g����Ȃ��ꍇ
     */
    private byte[] exportLevel0Header(String encode)
            throws UnsupportedEncodingException {

        //------------------------------------------------------------------
        //  �w�b�_�o�͏���
        final int LHarcHeaderSize = 100;
        final int CRCLength = (this.CRC == LhaHeader.NO_CRC
                || this.CRC == LhaHeader.UNKNOWN ? 0 : 2);
        byte[] CompressMethod = this.Method.getBytes(encode);     //After Java 1.1 throw UnsupportedEncodingException
        MsdosDate dosDate = null;
        try {
            dosDate = this.LastModified instanceof MsdosDate
                    ? (MsdosDate) this.LastModified
                    : new MsdosDate(this.LastModified);   //throw IllegalArgumentException
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException(exception.toString());
        }
        byte[] PathData = this.Path.replace(File.separatorChar,
                '\\').getBytes(encode);//After Java 1.1
        int HeaderLength = 22 + CRCLength + PathData.length;
        byte[] ExtraData;
        if (CRCLength != 0 && this.ExtraData != null
                && (HeaderLength + this.ExtraData.length <= LHarcHeaderSize)) {
            ExtraData = this.ExtraData;
        } else {
            ExtraData = new byte[0];
        }

        HeaderLength += ExtraData.length;

        //------------------------------------------------------------------
        //  �w�b�_�������`�F�b�N
        if (CompressMethod.length != 5) {
            throw new IllegalStateException("CompressMethod doesn't follow Format.");
        }

        if (LHarcHeaderSize < HeaderLength) {
            throw new IllegalStateException("Header size too large.");
        }

        if (this.CompressedSize == LhaHeader.UNKNOWN) {
            throw new IllegalStateException("CompressedSize must not be UNKNOWN.");
        }

        if (0x0000000100000000L <= this.CompressedSize) {
            throw new IllegalStateException("CompressedSize must be 0xFFFFFFFF or less.");
        }

        if (this.CompressedSize < 0) {
            throw new IllegalStateException("CompressedSize must be 0 or more.");
        }

        if (this.OriginalSize == LhaHeader.UNKNOWN) {
            throw new IllegalStateException("OriginalSize must not be UNKNOWN.");
        }

        if (0x0000000100000000L <= this.OriginalSize) {
            throw new IllegalStateException("OriginalSize must be 0xFFFFFFFF or less.");
        }

        if (this.OriginalSize < 0) {
            throw new IllegalStateException("OriginalSize must be 0 or more.");
        }

        //------------------------------------------------------------------
        //  �w�b�_�o��
        byte[] HeaderData;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            //�o�͂���w�b�_���ɂ̓w�b�_�擪�� �w�b�_��(1byte)�A
            //�`�F�b�N�T��(1byte)��2byte���܂܂Ȃ����� -2 ���Ă���B
            out.write(HeaderLength - 2);
            out.write(0);
            out.write(CompressMethod);
            LittleEndian.writeInt(out, (int) this.CompressedSize);
            LittleEndian.writeInt(out, (int) this.OriginalSize);
            LittleEndian.writeInt(out, dosDate.getMsdosTime());
            out.write(this.Level0DosAttribute);
            out.write(this.HeaderLevel);
            out.write(PathData.length);
            out.write(PathData);
            if (this.CRC != -1) {
                LittleEndian.writeShort(out, this.CRC);
                out.write(ExtraData);
            }
            out.close();
            HeaderData = out.toByteArray();
        } catch (IOException exception) {
            throw new Error("caught the IOException ( " + exception.getMessage() + " ) which should be never thrown by ByteArrayOutputStream.");
        }

        final int ChecksumIndex = 1;
        HeaderData[ChecksumIndex] = (byte) LhaHeader.calcHeaderChecksum(HeaderData);

        return HeaderData;
    }

    /**
     * ���� LhaHeader �̏����g����
     * ���x��1�w�b�_�̃f�[�^�𐶐�����B<br>
     * ���̍ہAExtraData ���܂߂�ƃw�b�_�T�C�Y��
     * �K��l�Ɏ��܂�Ȃ��ꍇ�� ExtraData �͊܂܂�Ȃ����Ƃ�����B
     * �܂��A�g���w�b�_�� 65534�o�C�g�ȏ�̃T�C�Y��
     * �����͖̂��������B
     *
     * @param encode ����������o�͂���ۂɎg�p����
     *               �G���R�[�h
     *
     * @exception IllegalStateException <br>
     *                <ol>
     *                   <li>���k�@�������encode�Ńo�C�g�z���
     *                       �������̂� 5byte�Ŗ����ꍇ
     *                   <li>�t�@�C�������傫�����邽��
     *                       ��{�w�b�_�ɂ��g���w�b�_�ɂ����܂肫��Ȃ��B
     *                   <li>���ʊg���w�b�_���傫�����ďo�͂ł��Ȃ��B
     *                       ���̂��߃w�b�_��CRC�i�[�ꏊ�������B
     *                   <li>CRC �� ���x��0�w�b�_�� CRC��񂪖�����������
     *                       ���ʂȒl�ł��� LhaHeader.NO_CRC( -2 ) ���ݒ肳��Ă����B
     *                   <li>LastModified��MS-DOS�`����
     *                       �\���ł��Ȃ��͈͂̎��Ԃł������ꍇ
     *                   <li>OriginalSize �ɃT�C�Y���s���ł��鎖������
     *                       ���ʂȒl�ł��� LhaHeader.UNKNOWN( -1 )���ݒ肳��Ă����B
     *                   <li>OriginalSize �����l�ł��邩�A
     *                       4byte�l�ŕ\���ł��Ȃ��l�ł���ꍇ
     *                   <li>CompressedSize �ɃT�C�Y���s���ł��鎖������
     *                       ���ʂȒl�ł��� LhaHeader.UNKNOWN( -1 )���ݒ肳��Ă����B
     *                   <li>CompressedSize �����l�ł��邩�A
     *                       4byte�l�ŕ\���ł��Ȃ��l�ł���ꍇ
     *                   <li>CRC ��CRC16�l���s���ł��鎖������ ���ʂȒl
     *                       �ł��� LhaHeader.UNKNOWN( -1 )���ݒ肳��Ă����B
     *                 </ol>
     *                 �̉��ꂩ
     * @exception UnsupportedEncodingException<br>
     *                   encode �Ŏw�肳�ꂽ�G���R�[�h��
     *                   �T�|�[�g����Ȃ��ꍇ
     */
    private byte[] exportLevel1Header(String encode)
            throws UnsupportedEncodingException {

        //------------------------------------------------------------------
        //  �w�b�_�o�͏���
        final int LHarcHeaderSize = 100;
        boolean hasFileName = false; //�t�@�C������������������
        boolean hasCRC = false; //�w�b�_��CRC��������������
        byte[] CompressMethod = this.Method.getBytes(encode);     //After Java 1.1 throw UnsupportedEncodingException
        MsdosDate dosDate;
        try {
            if (this.LastModified instanceof MsdosDate) {
                dosDate = (MsdosDate) this.LastModified;
            } else {
                dosDate = new MsdosDate(this.LastModified);                   //throw IllegalArgumentException
            }
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException(exception.toString());
        }

        int HeaderLength = 27;
        byte[] ExtraData;
        if (this.ExtraData != null && (HeaderLength + this.ExtraData.length <= LHarcHeaderSize)) {
            ExtraData = this.ExtraData;
        } else {
            ExtraData = new byte[0];
        }
        HeaderLength += ExtraData.length;

        byte[] FileName = this.getFileName().getBytes(encode);                //After Java 1.1
        if (LHarcHeaderSize < HeaderLength + FileName.length) {
            FileName = new byte[0];
        } else {
            hasFileName = true;
        }
        HeaderLength += FileName.length;


        byte[][] ExtendHeaders = this.exportExtHeaders(encode);
        long SkipSize = this.CompressedSize;
        for (int i = 0; i < ExtendHeaders.length; i++) {
            if (ExtendHeaders[i].length == 0
                    || 65534 <= ExtendHeaders[i].length
                    || (ExtendHeaders[i][0] == 1 && hasFileName)) {
                ExtendHeaders[i] = null;
            } else {
                if (ExtendHeaders[i][0] == 0x00) {
                    hasCRC = true;
                }
                if (ExtendHeaders[i][0] == 0x01) {
                    hasFileName = true;
                }

                SkipSize += ExtendHeaders[i].length + 2;
            }
        }

        //------------------------------------------------------------------
        //  �w�b�_�������`�F�b�N
        if (CompressMethod.length != 5) {
            throw new IllegalStateException("CompressMethod doesn't follow Format.");
        }

        if (SkipSize != this.CompressedSize && !hasCRC) {
            throw new IllegalStateException("no Header CRC field.");
        }

        if (!hasFileName) {
            throw new IllegalStateException("no Filename infomation.");
        }

        if (this.CRC == LhaHeader.NO_CRC) {
            throw new IllegalStateException("no CRC value.");
        }

        if (this.CRC == LhaHeader.UNKNOWN) {
            throw new IllegalStateException("CRC is UNKNOWN.");
        }

        if (this.CompressedSize == LhaHeader.UNKNOWN) {
            throw new IllegalStateException("CompressedSize must not be UNKNOWN.");
        }

        if (0x0000000100000000L <= this.CompressedSize) {
            throw new IllegalStateException("CompressedSize must be 0xFFFFFFFF or less.");
        }

        if (this.CompressedSize < 0) {
            throw new IllegalStateException("CompressedSize must be 0 or more.");
        }

        if (this.OriginalSize == LhaHeader.UNKNOWN) {
            throw new IllegalStateException("OriginalSize must not be UNKNOWN.");
        }

        if (0x0000000100000000L <= this.OriginalSize) {
            throw new IllegalStateException("OriginalSize must be 0xFFFFFFFF or less.");
        }

        if (this.OriginalSize < 0) {
            throw new IllegalStateException("OriginalSize must be 0 or more.");
        }

        if (0x0000000100000000L <= SkipSize) {
            throw new IllegalStateException("SkipSize must be 0xFFFFFFFF or less.");
        }

        //------------------------------------------------------------------
        //  �w�b�_�o��
        byte[] HeaderData;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            //�o�͂���w�b�_���ɂ̓w�b�_�擪�� �w�b�_��(1byte)�A
            //�`�F�b�N�T��(1byte)��2byte���܂܂Ȃ����� -2 ���Ă���B
            out.write(HeaderLength - 2);
            out.write(0);
            out.write(CompressMethod);
            LittleEndian.writeInt(out, (int) SkipSize);
            LittleEndian.writeInt(out, (int) this.OriginalSize);
            LittleEndian.writeInt(out, dosDate.getMsdosTime());
            out.write(0x20);
            out.write(this.HeaderLevel);
            out.write(FileName.length);
            out.write(FileName);
            LittleEndian.writeShort(out, this.CRC);
            out.write(this.OSID);
            out.write(ExtraData);

            for (int i = 0; i < ExtendHeaders.length; i++) {
                if (ExtendHeaders[i] != null) {
                    LittleEndian.writeShort(out, ExtendHeaders[i].length + 2);
                    out.write(ExtendHeaders[i]);
                }
            }
            LittleEndian.writeShort(out, 0);
            out.close();
            HeaderData = out.toByteArray();
        } catch (IOException exception) {
            throw new Error("caught the IOException ( " + exception.getMessage() + " ) which should be never thrown by ByteArrayOutputStream.");
        }

        final int ChecksumIndex = 1;
        final int CRCIndex = LhaHeader.getCRC16Position(HeaderData);
        HeaderData[ChecksumIndex] = (byte) LhaHeader.calcHeaderChecksum(HeaderData);
        if (hasCRC) {
            LittleEndian.writeShort(HeaderData, CRCIndex,
                    LhaHeader.calcHeaderCRC16(HeaderData));
        }

        return HeaderData;
    }

    /**
     * ���� LhaHeader �̏����g����
     * ���x��2�w�b�_�̃f�[�^�𐶐�����B<br>
     * �܂��A�S�g���w�b�_��65536�o�C�g�ȏ�̃T�C�Y�ɂȂ�ꍇ��
     * ���ʊg���w�b�_�A�t�@�C�����g���w�b�_���ŗD��Ŋi�[�����B
     * ��L�� 2�̊g���w�b�_�݂̂� 65536�o�C�g�ȏ�ɂȂ�ꍇ��
     * ��O�𓊂���B���̌�� �f�B���N�g�����g���w�b�_���D�悳��A
     * ���̌�� exportExtendHeaders(String) ���o�͂�������
     * �D�悵�ēo�^����A�S�w�b�_�� 65536�o�C�g�ȏ��
     * �Ȃ�Ȃ��悤�Ɋi�[�����B
     *
     * @param encode ����������o�͂���ۂɎg�p����
     *               �G���R�[�h
     *
     * @exception IllegalStateException
     *                 <ol>
     *                   <li>���k�@�������encode�Ńo�C�g�z���
     *                       �������̂� 5byte�Ŗ����ꍇ
     *                   <li>�t�@�C�������傫�����邽��
     *                       �g���w�b�_�Ɏ��܂肫��Ȃ��B
     *                   <li>���ʊg���w�b�_���傫�����ďo�͂ł��Ȃ��B
     *                       ���̂��߃w�b�_��CRC�i�[�ꏊ�������B
     *                   <li>CRC �� ���x��0�w�b�_�� CRC��񂪖�����������
     *                       ���ʂȒl�ł��� LhaHeader.NO_CRC( -2 ) ���ݒ肳��Ă����B
     *                   <li>LastModified��4�o�C�g��time_t��
     *                       �\���ł��Ȃ��͈͂̎��Ԃł������ꍇ
     *                   <li>OriginalSize �ɃT�C�Y���s���ł��鎖������
     *                       ���ʂȒl�ł��� LhaHeader.UNKNOWN( -1 )���ݒ肳��Ă����B
     *                   <li>OriginalSize �����l�ł���ꍇ
     *                   <li>CompressedSize �ɃT�C�Y���s���ł��鎖������
     *                       ���ʂȒl�ł��� LhaHeader.UNKNOWN( -1 )���ݒ肳��Ă����B
     *                   <li>CompressedSize �����l�ł���ꍇ
     *                   <li>CRC ��CRC16�l���s���ł��鎖������ ���ʂȒl�ł���
     *                       LhaHeader.UNKNOWN( -1 )���ݒ肳��Ă����B
     *                   <li>OriginalSize �܂��� CompressedSize��4�o�C�g�l��
     *                       �����邽�߃t�@�C���T�C�Y�w�b�_���K�v�ȍۂ�
     *                       ���̊g���w�b�_���傫������
     *                       �t�@�C���T�C�Y�w�b�_���o�͏o���Ȃ��ꍇ�B
     *                 </ol>
     *                 �̉��ꂩ�B
     * @exception UnsupportedEncodingException
     *                   encode �Ŏw�肳�ꂽ�G���R�[�h��
     *                   �T�|�[�g����Ȃ��ꍇ
     */
    private byte[] exportLevel2Header(String encode)
            throws UnsupportedEncodingException {

        //------------------------------------------------------------------
        //  �w�b�_�o�͏���
        final int MaxHeaderLength = 65535;
        boolean hasFileName = false; //�t�@�C������������������
        boolean hasCRC = false; //�w�b�_��CRC��������������
        boolean needExtraByte = false; //�w�b�_�̐擪��0x00�ɂ��Ȃ����߂ɗ]����1�o�C�g��t�����邩�������B
        boolean hasFileSize = false; //�t�@�C���T�C�Y�w�b�_�����������B
        byte[] CompressMethod = this.Method.getBytes(encode);     //After Java 1.1 throw UnsupportedEncodingException
        int HeaderLength = 26;

        boolean needFileSize = (0x0000000100000000L <= this.CompressedSize
                || 0x0000000100000000L <= this.OriginalSize);


        byte[][] ExtendHeaders = this.exportExtHeaders(encode);
        for (int i = 0; i < ExtendHeaders.length; i++) {
            if (ExtendHeaders[i].length == 0
                    || MaxHeaderLength <= HeaderLength + ExtendHeaders[i].length + 2) {
                ExtendHeaders[i] = null;
            } else {
                if (ExtendHeaders[i][0] == 0x00) {
                    hasCRC = true;
                }
                if (ExtendHeaders[i][0] == 0x01) {
                    hasFileName = true;
                }
                if (ExtendHeaders[i][0] == 0x42) {
                    hasFileSize = true;
                }

                HeaderLength += ExtendHeaders[i].length + 2;
            }
        }

        if ((HeaderLength & 0xFF) == 0) {
            HeaderLength++;
            needExtraByte = true;
        }

        //------------------------------------------------------------------
        //  �w�b�_�������`�F�b�N
        if (CompressMethod.length != 5) {
            throw new IllegalStateException("CompressMethod doesn't follow Format.");
        }

        if (this.LastModified.getTime() < 0
                || ((this.LastModified.getTime() / 1000L) & 0xFFFFFFFF00000000L)
                != 0) {
            throw new IllegalStateException("LastModified can not change to 4byte time_t format.");
        }

        if (!hasCRC) {
            throw new IllegalStateException("HeaderSize too large. can not contain CRC of the Header.");
        }

        if (!hasFileName) {
            throw new IllegalStateException("HeaderSize too large. can not contain Filename.");
        }

        if (needFileSize && !hasFileSize) {
            throw new IllegalStateException("HeaderSize too large. can not contain Filesize.");
        }

        if (this.CRC == LhaHeader.NO_CRC) {
            throw new IllegalStateException("no CRC.");
        }

        if (this.CRC == LhaHeader.UNKNOWN) {
            throw new IllegalStateException("CRC must not be UNKNOWN.");
        }

        if (this.CompressedSize == LhaHeader.UNKNOWN) {
            throw new IllegalStateException("CompressedSize must not be UNKNOWN.");
        }

        if (this.CompressedSize < 0) {
            throw new IllegalStateException("CompressedSize must be 0 or more.");
        }

        if (this.OriginalSize == LhaHeader.UNKNOWN) {
            throw new IllegalStateException("OriginalSize must not be UNKNOWN.");
        }

        if (this.OriginalSize < 0) {
            throw new IllegalStateException("OriginalSize must be 0 or more.");
        }


        //------------------------------------------------------------------
        //  �w�b�_�o��
        byte[] HeaderData;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            LittleEndian.writeShort(out, HeaderLength);
            out.write(CompressMethod);
            LittleEndian.writeInt(out, (int) this.CompressedSize);
            LittleEndian.writeInt(out, (int) this.OriginalSize);
            LittleEndian.writeInt(out,
                    (int) (this.LastModified.getTime() / 1000L));
            out.write(0x20);
            out.write(this.HeaderLevel);
            LittleEndian.writeShort(out, this.CRC);
            out.write(this.OSID);

            for (int i = 0; i < ExtendHeaders.length; i++) {
                if (ExtendHeaders[i] != null) {
                    LittleEndian.writeShort(out, ExtendHeaders[i].length + 2);
                    out.write(ExtendHeaders[i]);
                }
            }
            LittleEndian.writeShort(out, 0);

            if (needExtraByte) out.write(0x00);

            out.close();
            HeaderData = out.toByteArray();
        } catch (IOException exception) {
            throw new Error("caught the IOException ( " + exception.getMessage() + " ) which should be never thrown by ByteArrayOutputStream.");
        }

        final int CRCIndex = LhaHeader.getCRC16Position(HeaderData);
        LittleEndian.writeShort(HeaderData, CRCIndex,
                LhaHeader.calcHeaderCRC16(HeaderData));

        return HeaderData;
    }


    //------------------------------------------------------------------
    //  shared method
    //------------------------------------------------------------------
    //  read header data from InputStream
    //------------------------------------------------------------------
    //  public static byte[] getFirstHeaderData( InputStream in )
    //  public static byte[] getNextHeaderData( InputStream in )
    //------------------------------------------------------------------

    /**
     * ���� LhaHeader �̏����g����
     * ���x��3�w�b�_�̃f�[�^�𐶐�����B<br>
     *
     * @param encode ����������o�͂���ۂɎg�p����
     *               �G���R�[�h
     *
     * @return �o�C�g�z��Ɋi�[�����w�b�_�f�[�^
     *
     * @exception IllegalStateException <br>
     *                 <ol>
     *                   <li>���k�@�������encode�Ńo�C�g�z���
     *                       �������̂� 5byte�Ŗ����ꍇ
     *                   <li>���ʊg���w�b�_���傫�����ďo�͂ł��Ȃ��B
     *                       ���̂��߃w�b�_��CRC�i�[�ꏊ�������B
     *                   <li>CRC �� ���x��0�w�b�_�� CRC��񂪖�����������
     *                       ���ʂȒl�ł��� LhaHeader.NO_CRC( -2 ) ���ݒ肳��Ă����B
     *                   <li>LastModified��4�o�C�g��time_t��
     *                       �\���ł��Ȃ��͈͂̎��Ԃł������ꍇ<br>
     *                   <li>OriginalSize �ɃT�C�Y���s���ł��鎖������
     *                       ���ʂȒl�ł��� LhaHeader.UNKNOWN( -1 )���ݒ肳��Ă����B<br>
     *                   <li>OriginalSize �����l�ł��邩�A
     *                       4byte�l�ŕ\���ł��Ȃ��l�ł���ꍇ<br>
     *                   <li>CompressedSize �ɃT�C�Y���s���ł��鎖������
     *                       ���ʂȒl�ł��� LhaHeader.UNKNOWN( -1 )���ݒ肳��Ă����B<br>
     *                   <li>CompressedSize �����l�ł��邩�A
     *                       4byte�l�ŕ\���ł��Ȃ��l�ł���ꍇ<br>
     *                   <li>CRC ��CRC16�l���s���ł��鎖������ ���ʂȒl�ł���
     *                       LhaHeader.UNKNOWN( -1 )���ݒ肳��Ă����B<br>
     *                 </ol>
     *                 �̉��ꂩ�B
     * @exception UnsupportedEncodingException
     *                   encode �Ŏw�肳�ꂽ�G���R�[�h��
     *                   �T�|�[�g����Ȃ��ꍇ
     */
    private byte[] exportLevel3Header(String encode)
            throws UnsupportedEncodingException {

        //�w�b�_�o�͏���
        final int WordSize = 4;
        byte[] CompressMethod = this.Method.getBytes(encode);      //After Java 1.1 throw UnsupportedEncodingException
        int HeaderLength = 32;

        byte[][] ExtendHeaders = this.exportExtHeaders(encode);
        for (int i = 0; i < ExtendHeaders.length; i++) {
            if (ExtendHeaders[i].length == 0) {
                ExtendHeaders[i] = null;
            } else {
                HeaderLength += ExtendHeaders[i].length + 4;
            }
        }

        //�w�b�_�������`�F�b�N
        if (CompressMethod.length != 5) {
            throw new IllegalStateException("CompressMethod doesn't follow Format.");
        }

        if (this.LastModified.getTime() < 0
                || ((this.LastModified.getTime() / 1000L) & 0xFFFFFFFF00000000L)
                != 0) {
            throw new IllegalStateException("LastModified can not change to 4byte time_t format.");
        }

        if (this.CRC == LhaHeader.NO_CRC) {
            throw new IllegalStateException("no CRC value.");
        }

        if (this.CRC == LhaHeader.UNKNOWN) {
            throw new IllegalStateException("CRC is UNKNOWN.");
        }

        if (this.CompressedSize == LhaHeader.UNKNOWN) {
            throw new IllegalStateException("CompressedSize must not be UNKNOWN.");
        }

        if (0x0000000100000000L <= this.CompressedSize) {
            throw new IllegalStateException("CompressedSize must be 0xFFFFFFFF or less.");
        }

        if (this.CompressedSize < 0) {
            throw new IllegalStateException("CompressedSize must be 0 or more.");
        }

        if (this.OriginalSize == LhaHeader.UNKNOWN) {
            throw new IllegalStateException("OriginalSize must not be UNKNOWN.");
        }

        if (0x0000000100000000L <= this.OriginalSize) {
            throw new IllegalStateException("OriginalSize must be 0xFFFFFFFF or less.");
        }

        if (this.OriginalSize < 0) {
            throw new IllegalStateException("OriginalSize must be 0 or more.");
        }

        //�w�b�_�o��
        byte[] HeaderData;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            LittleEndian.writeShort(out, WordSize);
            out.write(CompressMethod);
            LittleEndian.writeInt(out, (int) this.CompressedSize);
            LittleEndian.writeInt(out, (int) this.OriginalSize);
            LittleEndian.writeInt(out,
                    (int) (this.LastModified.getTime() / 1000L));
            out.write(0x20);
            out.write(this.HeaderLevel);
            LittleEndian.writeShort(out, this.CRC);
            out.write(this.OSID);
            LittleEndian.writeInt(out, HeaderLength);

            for (int i = 0; i < ExtendHeaders.length; i++) {
                if (ExtendHeaders[i] != null) {
                    LittleEndian.writeInt(out, ExtendHeaders[i].length + 4);
                    out.write(ExtendHeaders[i]);
                }
            }
            LittleEndian.writeInt(out, 0);

            out.close();
            HeaderData = out.toByteArray();
        } catch (IOException exception) {
            throw new Error("caught the IOException ( " + exception.getMessage() + " ) which should be never thrown by ByteArrayOutputStream.");
        }

        final int CRCIndex = LhaHeader.getCRC16Position(HeaderData);
        LittleEndian.writeShort(HeaderData, CRCIndex,
                LhaHeader.calcHeaderCRC16(HeaderData));

        return HeaderData;
    }

    /**
     * ����LhaHeader�̃f�[�^���g�p���� �w�b�_�f�[�^�𐶐����A
     * ������o�C�g�z��̌`�œ���B<br>
     *
     * @param encode ����������o�͂���ۂɎg�p����
     *               �G���R�[�h
     *
     * @exception IllegalStateException
     *                <ol>
     *                   <li>���k�@�������encode�Ńo�C�g�z���
     *                       �������̂� 5byte�Ŗ����ꍇ
     *                   <li>���x��0,1,2�� �t�@�C�������������邽��
     *                       �w�b�_�Ɏ��܂肫��Ȃ��B
     *                   <li>���x��1,2�ŋ��ʊg���w�b�_���傫�����ďo�͂ł��Ȃ��B
     *                       ���̂��߃w�b�_��CRC�i�[�ꏊ�������B
     *                   <li>���x��0�ȊO�� CRC �� ���x��0�w�b�_��
     *                       CRC��񂪖��������������ʂȒl�ł���
     *                       LhaHeader.NO_CRC( -2 ) ���ݒ肳��Ă����B
     *                   <li>���x��0,1�̎���LastModified��MS-DOS�`��
     *                       �ŕ\���ł��Ȃ��͈͂̎��Ԃł������ꍇ
     *                   <li>���x��2,3�̎���LastModified��4�o�C�g��
     *                       time_t�ŕ\���ł��Ȃ��͈͂̎��Ԃł������ꍇ
     *                   <li>OriginalSize �ɃT�C�Y���s���ł��鎖������
     *                       ���ʂȒl�ł��� LhaHeader.UNKNOWN( -1 )���ݒ肳��Ă����B
     *                   <li>OriginalSize �����l�ł���ꍇ
     *                   <li>���x��0,1,3 �̎��� OriginalSize ��
     *                       4byte�l�ŕ\���ł��Ȃ��l�ł���ꍇ
     *                   <li>CompressedSize �ɃT�C�Y���s���ł��鎖������
     *                       ���ʂȒl�ł��� LhaHeader.UNKNOWN( -1 )���ݒ肳��Ă����B
     *                   <li>CompressedSize �����l�ł���ꍇ
     *                   <li>���x��0,1,3 �̎��� CompressedSize ��
     *                       4byte�l�ŕ\���ł��Ȃ��l�ł���ꍇ
     *                   <li>���x��2�̎���OriginalSize �܂��� CompressedSize��
     *                       4�o�C�g�l�𒴂��邽�߃t�@�C���T�C�Y�w�b�_���K�v�ȍۂ�
     *                       ���̊g���w�b�_���傫�����ăt�@�C���T�C�Y�w�b�_���o�͏o���Ȃ��ꍇ�B
     *                   <li>CRC ��CRC16�l���s���ł��鎖������
     *                       ���ʂȒl�ł��� LhaHeader.UNKNOWN( -1 )���ݒ肳��Ă����B
     *                   <li>�w�b�_���x���� 0,1,2,3 �ȊO�ł���ꍇ
     *                 </ol>
     *                 �̉��ꂩ�B
     * @exception UnsupportedEncodingException
     *                   encode �Ŏw�肳�ꂽ�G���R�[�h��
     *                   �T�|�[�g����Ȃ��ꍇ
     */
    private byte[] exportHeader(String encode)
            throws UnsupportedEncodingException {
        switch (this.HeaderLevel) {
            case 0:
                return this.exportLevel0Header(encode);                           //throw UnsupportedEncodingException IllegalStateException
            case 1:
                return this.exportLevel1Header(encode);                           //throw UnsupportedEncodingException IllegalStateException
            case 2:
                return this.exportLevel2Header(encode);                           //throw UnsupportedEncodingException IllegalStateException
            case 3:
                return this.exportLevel3Header(encode);                           //throw UnsupportedEncodingException IllegalStateException
            default:
                throw new IllegalStateException("unknown header level \"" + this.HeaderLevel + "\".");
        }
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  read header data
    //------------------------------------------------------------------
    //  private static byte[] readLevel0HeaderData( int HeaderLength,
    //                 int HeaderChecksum, int CompressMethod1, InputStream in )
    //  private static byte[] readLevel1HeaderData( int BaseHeaderLength,
    //             int BaseHeaderChecksum, int CompressMethod1, InputStream in )
    //  private static byte[] readLevel2HeaderData( int HeaderLengthLow,
    //             int HeaderLengthHi, int CompressMethod1, InputStream in )
    //  private static byte[] readLevel2HeaderData( int WordSizeLow,
    //             int WordSizeHi, int CompressMethod1, InputStream in )
    //------------------------------------------------------------------

    /**
     * ���ʊg���w�b�_���o�C�g�z��̌`�ɂ��ďo�͂���B
     * ���̃��\�b�h�� ExtraExtHeaders �� ���ʊg���w�b�_�̏��
     * �o�^����Ă���΂��̏����A�łȂ�� 0�ŏ��������ꂽ
     * 3 �o�C�g�̃o�C�g�z���Ԃ������ł���B
     *
     * @return ���ʊg���w�b�_���o�C�g�z��Ɋi�[��������
     */
    private byte[] exportCommonExtHeader() {
        if (this.ExtraExtHeaders != null) {
            for (int i = 0; i < this.ExtraExtHeaders.size(); i++) {
                byte[] ExtendHeaderData = (byte[]) this.ExtraExtHeaders.elementAt(i);

                if (ExtendHeaderData[0] == 0x00) {
                    return ExtendHeaderData;
                }
            }
        }

        return new byte[3];
    }

    /**
     * �t�@�C�����g���w�b�_���o�C�g�z��̌`�ɂ��ďo�͂���B
     * �t�@�C�����g���w�b�_�͋�ł��o�͂����B
     *
     * @param encode ����������o�͂���ۂɎg�p����
     *               �G���R�[�h
     *
     * @return �t�@�C�����g���w�b�_���o�C�g�z��Ɋi�[��������
     */
    private byte[] exportFileNameExtHeader(String encode)
            throws UnsupportedEncodingException {
        byte[] FileName = this.getFileName().getBytes(encode);        //After Java 1.1

        byte[] ExtendHeaderData = new byte[FileName.length + 1];
        ExtendHeaderData[0] = 0x01; //�g���w�b�_ID��ݒ�
        System.arraycopy(FileName, 0, ExtendHeaderData, 1, FileName.length);
        return ExtendHeaderData;
    }

    /**
     * �f�B���N�g�����g���w�b�_���o�C�g�z��̌`�ɂ��ďo�͂���B
     * ���̃��\�b�h�ł� �f�B���N�g�����g���w�b�_��
     * ��ł��o�͂���邪�A�f�B���N�g�����g���w�b�_����ł���
     * �ꍇ�� exportExtHeaders() �̒i�K�Ŏ�菜�����B
     *
     * @param encode ����������o�͂���ۂɎg�p����
     *               �G���R�[�h
     *
     * @return �f�B���N�g�����g���w�b�_���o�C�g�z��Ɋi�[��������
     */
    private byte[] exportDirNameExtHeader(String encode)
            throws UnsupportedEncodingException {

        final byte LhaFileSeparator = (byte) 0xFF;
        String dir = this.getDirName();

        Vector vec = new Vector();
        int index = 0;
        int len = 0;
        int length = 0;
        while (index + len < dir.length()) {
            if (dir.charAt(index + len) == File.separatorChar) {
                byte[] src = dir.substring(index, index + len).getBytes(encode);
                byte[] array = new byte[src.length + 1];
                System.arraycopy(src, 0, array, 0, src.length);
                array[src.length] = LhaFileSeparator;
                length += array.length;
                vec.addElement(array);

                index += len + 1;
                len = 0;
            } else if (index + len + 1 < dir.length()) {
                byte[] array = dir.substring(index, index + len + 1).getBytes(encode);
                length += array.length;
                vec.addElement(array);

                index += len + 1;
                len = 0;
            } else {
                len++;
            }
        }

        byte[] ExtendHeaderData = new byte[length + 1];
        ExtendHeaderData[0] = 0x02; //�g���w�b�_ID��ݒ�
        index = 1;
        for (int i = 0; i < vec.size(); i++) {
            byte[] array = (byte[]) vec.elementAt(i);

            System.arraycopy(array, 0, ExtendHeaderData, index, array.length);
            index += array.length;
        }

        return ExtendHeaderData;
    }

    /**
     * 64bit�t�@�C���T�C�Y�w�b�_���o�C�g�z��ɂ��ďo�͂���B
     * ���̃��\�b�h�̓I���W�i���T�C�Y�A�܂��͈��k��T�C�Y��
     * 32bit�l�ŕ\���ł���ꍇ�ł��o�C�g�z����o�͂���B
     * �K�v�̖����ꍇ�ɂ� exportExtHeaders() ���o�͂�}�~����B
     *
     * @return 64bit�t�@�C���T�C�Y�w�b�_
     */
    private byte[] exportFileSizeHeader() {
        byte[] ExtendHeaderData = new byte[17];

        ExtendHeaderData[0] = (byte) 0x42;
        LittleEndian.writeLong(ExtendHeaderData, 1, this.CompressedSize);
        LittleEndian.writeLong(ExtendHeaderData, 9, this.OriginalSize);

        return ExtendHeaderData;
    }


    //------------------------------------------------------------------
    //  shared method
    //------------------------------------------------------------------
    //  public static LhaHeader createInstance( byte[] HeaderData, 
    //                         String encoding, Properties property )
    //------------------------------------------------------------------

    /**
     * �g���w�b�_���o�C�g�z��̌`�ɂ��ďo�͂���B
     * ���̃��\�b�h���I�[�o�[���C�h���鎖�ɂ����
     * �l�X�Ȋg���w�b�_�ɑΉ����邱�Ƃ��\�ƂȂ�B
     * LhaHeader �ł� private �����o�ł���
     * ExtraExtHeaders �ɓo�^���ꂽ�g���w�b�_�̏���
     * �Ԃ������ł���B
     * �o�͂̌`���� ���o�C�g�ڂɊg���w�b�_���ʎq
     * �����āA�g���w�b�_�f�[�^���i�[����A
     * ���̊g���w�b�_�̑傫���͓Y�t����Ȃ��B
     *
     * @param encode ����������o�͂���ۂɎg�p����
     *               �G���R�[�h
     *
     * @return 1�̊g���w�b�_��1�̃o�C�g�z��Ɋi�[���A
     *         �����z��̌`�ɂ�������
     *
     * @exception UnsupportedEncodingException
     *                   encode �Ŏw�肳�ꂽ�G���R�[�h��
     *                   �T�|�[�g����Ȃ��ꍇ
     */
    protected byte[][] exportExtendHeaders(String encode)
            throws UnsupportedEncodingException {
        if (this.ExtraExtHeaders != null) {
            byte[][] ExtendHeaders = new byte[this.ExtraExtHeaders.size()][];

            for (int i = 0; i < this.ExtraExtHeaders.size(); i++) {
                ExtendHeaders[i] = (byte[]) this.ExtraExtHeaders.elementAt(i);
            }

            return ExtendHeaders;
        } else {
            return new byte[0][];
        }
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  helper of InputStream
    //------------------------------------------------------------------
    //  private static void ensureSkip( InputStream in, long len )
    //------------------------------------------------------------------

    /**
     * �g���w�b�_���o�C�g�z��̌`�ɂ��ďo�͂���B
     *
     * @param encode ����������o�͂���ۂɎg�p����
     *               �G���R�[�h
     *
     * @return 1�̊g���w�b�_��1�̃o�C�g�z��Ɋi�[���A
     *         �����S�Ă̊g���w�b�_�̔z��̌`�ɂ�������
     *
     * @exception UnsupportedEncodingException
     *                   encode �Ŏw�肳�ꂽ�G���R�[�h��
     *                   �T�|�[�g����Ȃ��ꍇ
     */
    private byte[][] exportExtHeaders(String encode)
            throws UnsupportedEncodingException {
        byte[] CommonExtHeader = this.exportCommonExtHeader();
        byte[] FileNameExtHeader = this.exportFileNameExtHeader(encode);
        byte[] DirNameExtHeader = this.exportDirNameExtHeader(encode);

        byte[][] ExtraExtHeaders = this.exportExtendHeaders(encode);
        Vector Headers = new Vector();

        Headers.addElement(CommonExtHeader);
        Headers.addElement(FileNameExtHeader);
        if (1 < DirNameExtHeader.length) {
            Headers.addElement(DirNameExtHeader);
        }

        if (this.HeaderLevel == 2
                && (0x0000000100000000L <= this.CompressedSize
                || 0x0000000100000000L <= this.OriginalSize)) {
            Headers.addElement(this.exportFileSizeHeader());
        }

        for (int i = 0; i < ExtraExtHeaders.length; i++) {
            byte[] ExtendHeaderData = ExtraExtHeaders[i];
            if (0 < ExtendHeaderData.length
                    && ExtendHeaderData[0] != 0x00
                    && ExtendHeaderData[0] != 0x01
                    && ExtendHeaderData[0] != 0x02) {
                Headers.addElement(ExtendHeaderData);
            }
        }

        byte[][] ExtendHeaders = new byte[Headers.size()][];
        for (int i = 0; i < ExtendHeaders.length; i++) {
            ExtendHeaders[i] = (byte[]) Headers.elementAt(i);
        }

        return ExtendHeaders;
    }

}
//end of LhaHeader.java
