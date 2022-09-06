//start of CompressMethod.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * CompressMethod.java
 * 
 * Copyright (C) 2001-2002  Michel Ishizuka  All rights reserved.
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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Properties;

//import exceptions


/**
 * LHA�̊e��萔���`����B
 * 
 * <pre>
 * -- revision history --
 * $Log: CompressMethod.java,v $
 * Revision 1.1  2002/12/08 00:00:00  dangan
 * [change]
 *     �N���X���� LhaConstants ���� CompressMethod �ւƕύX�B
 *
 * Revision 1.0  2002/07/24 00:00:00  dangan
 * add to version control
 * [change]
 *     LhaUtil �� connectExtractInputStream �� connectDecoder �Ƃ���
 *     connectCompressOutputStream �� connectEncoder �Ƃ��Ĉ����p���B
 *     LhaUtil �� CompressMethodTo????????? �������p���B
 * [maintanance]
 *     �\�[�X����
 *     �^�u�p�~
 *     ���C�Z���X���̏C��
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class CompressMethod{


    //------------------------------------------------------------------
    //  class field
    //------------------------------------------------------------------
    //  compress method identifier
    //------------------------------------------------------------------
    //  public static final String LH0
    //  public static final String LH1
    //  public static final String LH2
    //  public static final String LH3
    //  public static final String LH4
    //  public static final String LH5
    //  public static final String LH6
    //  public static final String LH7
    //  public static final String LHD
    //  public static final String LZS
    //  public static final String LZ4
    //  public static final String LZ5
    //------------------------------------------------------------------
    /**
     * ���k�`��������������B
     * LH0 �� �����k������ "-lh0-" �ł���B
     */
    public static final String LH0 = "-lh0-";

    /**
     * ���k�`��������������B
     * LH1 �͑O�i�� 4�L���o�C�g�̎����A�ő��v��60�o�C�g��
     * LZSS�@�A��i�� �K���I�n�t�}���@���g�p���邱�Ƃ��Ӗ�����
     * "-lh1-" �ł���B
     */
    public static final String LH1 = "-lh1-";

    /**
     * ���k�`��������������B
     * LH2 �͑O�i�� 8�L���o�C�g�̎����A�ő��v��256�o�C�g��
     * LZSS�@�A��i�� �K���I�n�t�}���@���g�p���邱�Ƃ��Ӗ�����
     * "-lh2-" �ł���B
     * ���̈��k�@�� LH1 ���� LH5 �ւ̉��Ǔr���Ŏ����I��
     * �g��ꂽ���A���݂͎g�p����Ă��Ȃ��B
     */
    public static final String LH2 = "-lh2-";

    /**
     * ���k�`��������������B
     * LH3 �͑O�i�� 8�L���o�C�g�̎����A�ő��v��256�o�C�g��
     * LZSS�@�A��i�� �ÓI�n�t�}���@���g�p���邱�Ƃ��Ӗ�����
     * "-lh3-" �ł���B
     * ���̈��k�@�� LH1 ���� LH5 �ւ̉��Ǔr���Ŏ����I��
     * �g��ꂽ���A���݂͎g�p����Ă��Ȃ��B
     */
    public static final String LH3 = "-lh3-";

    /**
     * ���k�`��������������B
     * LH4 �͑O�i�� 4�L���o�C�g�̎����A�ő��v��256�o�C�g��
     * LZSS�@�A��i�� �ÓI�n�t�}���@���g�p���邱�Ƃ��Ӗ�����
     * "-lh4-" �ł���B
     * ���̈��k�@�� 1990�N��O���̔�͂ȃ}�V����ň��k���s���ہA
     * LH5���k���s�������̃V�X�e�������𓾂��Ȃ��������Ɏg��
     * �ꂽ���A���݂͖w�ǎg�p����Ă��Ȃ��B
     */
    public static final String LH4 = "-lh4-";

    /**
     * ���k�`��������������B
     * LH5 �͑O�i�� 8�L���o�C�g�̎����A�ő��v��256�o�C�g��
     * LZSS�@�A��i�� �ÓI�n�t�}���@���g�p���邱�Ƃ��Ӗ�����
     * "-lh5-" �ł���B
     * ���݁ALHA�ŕW���Ŏg�p����鈳�k�@�ł���B
     */
    public static final String LH5 = "-lh5-";

    /**
     * ���k�`��������������B
     * LH6 �͑O�i�� 32�L���o�C�g�̎����A�ő��v��256�o�C�g��
     * LZSS�@�A��i�� �ÓI�n�t�}���@���g�p���邱�Ƃ��Ӗ�����
     * "-lh6-" �ł���B
     * "-lh6-" �Ƃ���������� LH7 �̈��k�@�̎����Ɏg�p�����
     * �����B���̂��߁ALHA�̎����ł��쐬�������ɂɂ� "-lh6-"
     * �̕�������g�p���Ȃ��� LH7 �`���ň��k����Ă�����̂�
     * ���݂���炵���B
     * �܂� ���̈��k�@�͊J������Ă��� 10�N�߂��o��������
     * ���̏ꏊ�� ���̈��k�@�ň��k���ꂽ���ɂ͓o�^���Ȃ�����
     * ���]�܂����Ƃ���Ă���B
     */
    public static final String LH6 = "-lh6-";

    /**
     * ���k�`��������������B
     * LH7 �͑O�i�� 64�L���o�C�g�̎����A�ő��v��256�o�C�g��
     * LZSS�@�A��i�� �ÓI�n�t�}���@���g�p���邱�Ƃ��Ӗ�����
     * "-lh7-" �ł���B
     * �܂� ���̈��k�@�͊J������Ă��� 10�N�߂��o��������
     * ���̏ꏊ�� ���̈��k�@�ň��k���ꂽ���ɂ͓o�^���Ȃ�����
     * ���]�܂����Ƃ���Ă���B
     */
    public static final String LH7 = "-lh7-";

    /**
     * ���k�`��������������B
     * LHD �͖����k�ŁA�f�B���N�g�����i�[���Ă��邱�Ƃ�����
     * "-lhd-" �ł���B
     */
    public static final String LHD = "-lhd-";

    /**
     * ���k�`��������������B
     * LZS �� 2�L���o�C�g�̎����A�ő��v��17�o�C�g�� 
     * LZSS�@���g�p���邱�Ƃ����� "-lzs-" �ł���B
     * "-lzs-" �� LHA���쐬�����O�Ƀ��W���[�ł�����
     * Larc �̌`���ł���A�����̌݊����ɔz�����Ē�`��
     * �ꂽ�B���݂͖w�ǎg�p����Ă��Ȃ��B
     */
    public static final String LZS = "-lzs-";

    /**
     * ���k�`��������������B
     * LZ4 �� �����k������ "-lz4-" �ł���B
     * "-lz4-" �� LHA���쐬�����O�Ƀ��W���[�ł�����
     * Larc �̌`���ł���A�����̌݊����ɔz�����Ē�`��
     * �ꂽ�B���݂͖w�ǎg�p����Ă��Ȃ��B
     */
    public static final String LZ4 = "-lz4-";

    /**
     * ���k�`��������������B
     * LZ5 �� 4�L���o�C�g�̎����A�ő��v��17�o�C�g�� 
     * LZSS�@���g�p���邱�Ƃ����� "-lz5-" �ł���B
     * "-lz5-" �� LHA���쐬�����O�Ƀ��W���[�ł�����
     * Larc �̌`���ł���A�����̌݊����ɔz�����Ē�`��
     * �ꂽ�B���݂͖w�ǎg�p����Ă��Ȃ��B
     */
    public static final String LZ5 = "-lz5-";


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private CompressMethod()
    //------------------------------------------------------------------
    /**
     * �f�t�H���g�R���X�g���N�^�g�p�s��
     */
    private CompressMethod(){ }


    //------------------------------------------------------------------
    //  convert to LZSS parameter
    //------------------------------------------------------------------
    //  public static int toDictionarySize( String method )
    //  public static int toThreshold( String method )
    //  public static int toMaxMatch( String method )
    //------------------------------------------------------------------
    /**
     * ���k�@���ʎq���� �����T�C�Y�𓾂�B
     * 
     * @param method ���k�@���ʎq
     * 
     * @return �����T�C�Y
     */
    public static int toDictionarySize( String method ){
        if( CompressMethod.LZS.equalsIgnoreCase( method ) ){
            return  2048;
        }else if( CompressMethod.LZ5.equalsIgnoreCase( method ) ){
            return  4096;
        }else if( CompressMethod.LH1.equalsIgnoreCase( method ) ){
            return  4096;
        }else if( CompressMethod.LH2.equalsIgnoreCase( method ) ){
            return  8192;
        }else if( CompressMethod.LH3.equalsIgnoreCase( method ) ){
            return  8192;
        }else if( CompressMethod.LH4.equalsIgnoreCase( method ) ){
            return  4096;
        }else if( CompressMethod.LH5.equalsIgnoreCase( method ) ){
            return  8192;
        }else if( CompressMethod.LH6.equalsIgnoreCase( method ) ){
            return 32768;
        }else if( CompressMethod.LH7.equalsIgnoreCase( method ) ){
            return 65536;
        }else if( CompressMethod.LZ4.equalsIgnoreCase( method ) ){
            throw new IllegalArgumentException( method + " means no compress." );
        }else if( CompressMethod.LH0.equalsIgnoreCase( method ) ){
            throw new IllegalArgumentException( method + " means no compress." );
        }else if( CompressMethod.LHD.equalsIgnoreCase( method ) ){
            throw new IllegalArgumentException( method + " means no compress." );
        }else if( method == null ){
            throw new NullPointerException( "method" );
        }else{
            throw new IllegalArgumentException( "Unknown compress method. " + method );
        }
    }

    /**
     * ���k�@���ʎq���� ���k/�񈳏k��臒l�𓾂�B
     * 
     * @param method ���k�@���ʎq
     * 
     * @return ���k/�񈳏k
     */
    public static int toThreshold( String method ){
        if( CompressMethod.LZS.equalsIgnoreCase( method ) ){
            return 2;
        }else if( CompressMethod.LZ5.equalsIgnoreCase( method ) ){
            return 3;
        }else if( CompressMethod.LH1.equalsIgnoreCase( method ) ){
            return 3;
        }else if( CompressMethod.LH2.equalsIgnoreCase( method ) ){
            return 3;
        }else if( CompressMethod.LH3.equalsIgnoreCase( method ) ){
            return 3;
        }else if( CompressMethod.LH4.equalsIgnoreCase( method ) ){
            return 3;
        }else if( CompressMethod.LH5.equalsIgnoreCase( method ) ){
            return 3;
        }else if( CompressMethod.LH6.equalsIgnoreCase( method ) ){
            return 3;
        }else if( CompressMethod.LH7.equalsIgnoreCase( method ) ){
            return 3;
        }else if( CompressMethod.LZ4.equalsIgnoreCase( method ) ){
            throw new IllegalArgumentException( method + " means no compress." );
        }else if( CompressMethod.LH0.equalsIgnoreCase( method ) ){
            throw new IllegalArgumentException( method + " means no compress." );
        }else if( CompressMethod.LHD.equalsIgnoreCase( method ) ){
            throw new IllegalArgumentException( method + " means no compress." );
        }else if( method == null ){
            throw new NullPointerException( "method" );
        }else{
            throw new IllegalArgumentException( "Unknown compress method. " + method );
        }
    }

    /**
     * ���k�@���ʎq���� �ő��v���𓾂�B
     * 
     * @param method ���k�@���ʎq
     * 
     * @return �ő��v��
     */
    public static int toMaxMatch( String method ){
        if( CompressMethod.LZS.equalsIgnoreCase( method ) ){
            return  17;
        }else if( CompressMethod.LZ5.equalsIgnoreCase( method ) ){
            return  18;
        }else if( CompressMethod.LH1.equalsIgnoreCase( method ) ){
            return  60;
        }else if( CompressMethod.LH2.equalsIgnoreCase( method ) ){
            return 256;
        }else if( CompressMethod.LH3.equalsIgnoreCase( method ) ){
            return 256;
        }else if( CompressMethod.LH4.equalsIgnoreCase( method ) ){
            return 256;
        }else if( CompressMethod.LH5.equalsIgnoreCase( method ) ){
            return 256;
        }else if( CompressMethod.LH6.equalsIgnoreCase( method ) ){
            return 256;
        }else if( CompressMethod.LH7.equalsIgnoreCase( method ) ){
            return 256;
        }else if( CompressMethod.LZ4.equalsIgnoreCase( method ) ){
            throw new IllegalArgumentException( method + " means no compress." );
        }else if( CompressMethod.LH0.equalsIgnoreCase( method ) ){
            throw new IllegalArgumentException( method + " means no compress." );
        }else if( CompressMethod.LHD.equalsIgnoreCase( method ) ){
            throw new IllegalArgumentException( method + " means no compress." );
        }else if( method == null ){
            throw new NullPointerException( "method" );
        }else{
            throw new IllegalArgumentException( "Unknown compress method. " + method );
        }
    }


    //------------------------------------------------------------------
    //  shared method
    //------------------------------------------------------------------
    //  connect encoder/decoder
    //------------------------------------------------------------------
    //  public static OutputStream connectEncoder( OutputStream out,
    //                 String method, Properties property )
    //  public static InputStream connectDecoder( InputStream in,
    //                 String method, Properties property, long length )
    //------------------------------------------------------------------
    /**
     * property �ɐݒ肳�ꂽ�������𗘗p����
     * method �̈��k�@�Ńf�[�^�����k���Aout�ɏo�͂���X�g���[�����\�z����B
     * 
     * @param out      ���k�f�[�^�o�͐�̃X�g���[��
     * @param method   ���k�@���ʎq
     * @param property �e���k�`���ɑΉ�����������̐����������܂܂��v���p�e�B
     * 
     * @return method �̈��k�@�Ńf�[�^�����k���Aout�ɏo�͂���X�g���[��
     */
    public static OutputStream connectEncoder( OutputStream out,
                                               String       method,
                                               Properties   property ){

        String key = "lha." + CompressMethod.getCore( method ) + ".encoder";

        String generator = property.getProperty( key );
        if( generator == null ){
            generator = LhaProperty.getProperty( key );
        }

        String packages = property.getProperty( "lha.packages" );
        if( packages == null ){
            packages = LhaProperty.getProperty( "lha.packages" );
        }

        Hashtable substitute = new Hashtable();
        substitute.put( "out", out );

        return (OutputStream)LhaProperty.parse( generator,
                                                substitute,
                                                packages );
    }

    /**
     * property �ɐݒ肳�ꂽ�������𗘗p����
     * in ���� method �̈��k�@�ň��k���ꂽ�f�[�^���𓀂�
     * ����������̓X�g���[�����\�z����B
     * 
     * @param in       ���k�f�[�^����������X�g���[��
     * @param method   ���k�@���ʎq
     * @param property �e���k�`���ɑΉ�����������̐����������܂܂��v���p�e�B
     * 
     * @return in ���� method �̈��k�@�ň��k���ꂽ�f�[�^���𓀂�
     *         ����������̓X�g���[�����\�z����B
     */
    public static InputStream connectDecoder( InputStream in,
                                              String      method,
                                              Properties  property,
                                              long        length ){

        String key = "lha." + CompressMethod.getCore( method ) + ".decoder";

        String generator = property.getProperty( key );
        if( generator == null ){
            generator = LhaProperty.getProperty( key );
        }

        String packages = property.getProperty( "lha.packages" );
        if( packages == null ){
            packages = LhaProperty.getProperty( "lha.packages" );
        }

        Hashtable substitute = new Hashtable();
        substitute.put( "in",     in );
        substitute.put( "length", new Long( length ) );

        return (InputStream)LhaProperty.parse( generator,
                                               substitute,
                                               packages );
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  private static String getCore( String method )
    //------------------------------------------------------------------
    /**
     * ���k�@���ʎq �̑O��� '-' ����苎����
     * LhaProperty �̃L�[ lha.???.encoder / lha.???.decoder 
     * �� ??? �ɓ��镶����𐶐�����B
     * 
     * @param method ���k�@���ʎq
     * 
     * @return �L�[�̒��S�Ɏg���镶����
     */
    private static String getCore( String method ){
        if( method.startsWith( "-" ) && method.endsWith( "-" ) ){
            return method.substring( 1, method.lastIndexOf( '-' ) ).toLowerCase();
        }else{
            throw new IllegalArgumentException( "" );
        }
    }

}
//end of CompressMethod.java
