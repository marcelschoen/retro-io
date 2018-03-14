//start of LzssOutputStream.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * LzssOutputStream.java
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

import jp.gr.java_conf.dangan.lang.reflect.Factory;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

//import exceptions


/**
 * �f�[�^�� LZSS���k���Ȃ���
 * �w�肳�ꂽ PostLzssEncoder �ɏo�͂��鈳�k�p�o�̓X�g���[���B<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: LzssOutputStream.java,v $
 * Revision 1.2  2002/12/06 00:00:00  dangan
 * [change]
 *     flush() �� write() ���ꂽ�S�Ẵf�[�^�� 
 *     �ڑ����ꂽ PostLzssEncoder �ɏo�͂���悤�ɏC���B
 * [maintenance]
 *     slide������� DictionarySize �o�C�g�ɂȂ�悤�ɏC���B
 *
 * Revision 1.1  2002/10/20 00:00:00  dangan
 * [bug fix]
 *     ������Ԃ� flush() ������ �A���� flush() �����
 *     ( lastsearchret �� NEEDSEARCH �̎��� encode() ���Ă΂��� )
 *     ����� 1�o�C�g�������Ă����B
 *     flush() ���� putLength() ���l�����Ă��Ȃ���������
 *     �����@�\��j�󂷂�悤�� searchAndPut ���s���Ă����̂��C���B
 *     flush() ���� TextBuffer �Ō����MaxMatch�o�C�g�̃f�[�^���o�͂��Ă��Ȃ������B
 *
 * Revision 1.0  2002/07/25 00:00:00  dangan
 * add to version control
 * [bug fix]
 *     getMatchLen() �� searchret >> 22 �Ƃ��ׂ��Ƃ��낪 
 *     searchret >>> 22 �ƂȂ��Ă����̂��C���B
 * [maintenance]
 *     LhaUtil.createInstance() �̎g�p�����
 *     ����� Factory.createInstance() ���g�p����B
 *     �\�[�X����
 *     �^�u�p�~
 *     ���C�Z���X���̏C��
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.2 $
 */
public class LzssOutputStream extends OutputStream{


    //------------------------------------------------------------------
    //  class field
    //------------------------------------------------------------------
    //  special value
    //------------------------------------------------------------------
    //  private static final int NEEDSEARCH
    //  private static final int NOMATCH
    //------------------------------------------------------------------
    /**
     * lastsearchret �ɓo�^����l�B
     * searchAndPut�̏������K�v�ł��鎖�������B
     */
    private static final int NEEDSEARCH = 0;

    /**
     * searchret �����̒l�������ꍇ�A
     * �����̌��ʁA臒l�ȏ�̈�v��������Ȃ��������������B
     */
    public static final int NOMATCH = -1;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  sink
    //------------------------------------------------------------------
    //  private PostLzssEncoder encoder
    //------------------------------------------------------------------
    /**
     * LZSS���k�R�[�h��r�o�����̏o�̓X�g���[��
     */
    private PostLzssEncoder encoder;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  LZSS parameter
    //------------------------------------------------------------------
    //  private int DictionarySize
    //  private int Threshold
    //  private int MaxMatch
    //------------------------------------------------------------------
    /**
     * LZSS�����T�C�Y�B
     */
    private int DictionarySize;

    /**
     * LZSS���k�Ɏg�p�����臒l�B
     * ��v���� ���̒l�ȏ�ł���΁A���k�R�[�h���o�͂���B
     */
    private int Threshold;

    /**
     * LZSS���k�Ɏg�p�����l�B
     * �ő��v���������B
     */
    private int MaxMatch;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  text buffer
    //------------------------------------------------------------------
    //  private byte[] TextBuffer
    //  private int DictionaryLimit
    //  private int writtenPos
    //  private int putPos
    //  private int searchedPos
    //------------------------------------------------------------------
    /**
     * LZSS���k���{�����߂̃o�b�t�@�B
     * �O���͎����̈�A
     * �㔼�͈��k���{�����߂̃f�[�^�̓������o�b�t�@�B
     */
    private byte[] TextBuffer;

    /**
     * �����̌��E�ʒu�B 
     * TextBuffer�O���̎����̈�Ƀf�[�^�������ꍇ��
     * �����̈�ɂ���s��̃f�[�^(Java�ł�0)���g�p
     * ���Ĉ��k���s����̂�}�~����B
     */
    private int DictionaryLimit;

    /**
     * TextBuffer���������݊����ʒu
     * LzssOutputStream.write() �ɂ���ď������܂ꂽ�ʒu
     * 
     * �ȉ���3�҂̊֌W�� putPos <= searchedPos <= writtenPos �ƂȂ�B
     */
    private int writtenPos;

    /**
     * TextBuffer�� put() �����ʒu
     * LzssSearchMethod �� put() �������� searchAndPut() ��
     * �����@�\�ւ̓o�^�����������ʒu
     */
    private int putPos;

    /**
     * TextBuffer�� ���݌����ʒu
     * ���� LzssSearchMethod �� search() �������� searchAndPut() ��
     * ���������ׂ��ʒu
     */
    private int searchPos;

    /**
     * �O���encode�̍Ō��searchret��ۑ����Ă���
     * �R���X�g���N�^�ł� lastsearchret �ɖ�����
     * �����ł��鎖������ LzssOutputStream.NEEDSEARCH��
     * ���͂��Ă����B
     */
    private int lastsearchret;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  search method
    //------------------------------------------------------------------
    //  private LzssSearchMethod method
    //------------------------------------------------------------------
    /**
     * �����������ǂ�N���X
     */
    private LzssSearchMethod method;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private LzssOutputStream()
    //  public LzssOutputStream( PostLzssEncoder encoder )
    //  public LzssOutputStream( PostLzssEncoder encoder, String SearchMethod )
    //------------------------------------------------------------------
    /**
     * �f�t�H���g�R���X�g���N�^�B
     * �g�p�s�B
     */
    private LzssOutputStream(){ }

    /**
     * write() �ɂ���ď������܂ꂽ�f�[�^��
     * LZSS�ň��k���A���k�����f�[�^�� encoder�ɏo�͂���
     * �o�̓X�g���[�����\�z����B
     * 
     * @param encoder LZSS���k�f�[�^�o�̓X�g���[��
     */
    public LzssOutputStream( PostLzssEncoder encoder ){
        this( encoder, 
              HashAndChainedListSearch.class.getName(),
              new Object[0] );
    }

    /**
     * write() �ɂ���ď������܂ꂽ�f�[�^��
     * LZSS�ň��k���A���k�����f�[�^�� encoder�ɏo�͂���
     * �o�̓X�g���[�����\�z����B
     * 
     * @param encoder LZSS���k�f�[�^�o�̓X�g���[��
     * @param LzssSearchMethodClassName 
     *                LzssSearchMethod �̎����������p�b�P�[�W�����܂߂��N���X��
     * 
     * @exception NoClassDefFoundError
     *              LzssSearchMethodClassName �ŗ^����ꂽ�N���X��
     *              ������Ȃ��ꍇ�B
     * @exception InstantiationError
     *              LzssSearchMethodClassName �ŗ^����ꂽ�N���X��
     *              abstract class �ł��邽�߃C���X�^���X�𐶐��ł��Ȃ��ꍇ�B
     * @exception NoSuchMethodError
     *              LzssSearchMethodClassName �ŗ^����ꂽ�N���X��
     *              �R���X�g���N�^ LzssSearchMethod( int, int, int, byte[], int )
     *              �������Ȃ��ꍇ
     */
    public LzssOutputStream( PostLzssEncoder encoder, 
                             String          LzssSearchMethodClassName ){
        this( encoder, 
              LzssSearchMethodClassName,
              new Object[0] );
    }

    /**
     * write() �ɂ���ď������܂ꂽ�f�[�^��
     * LZSS�ň��k���A���k�����f�[�^�� encoder�ɏo�͂���
     * �o�̓X�g���[�����\�z����B
     * 
     * @param encoder LZSS���k�f�[�^�o�̓X�g���[��
     * @param LzssSearchMethodClassName 
     *                LzssSearchMethod �̎����������p�b�P�[�W�����܂߂��N���X��
     * 
     * @exception NoClassDefFoundError
     *              LzssSearchMethodClassName �ŗ^����ꂽ�N���X��
     *              ������Ȃ��ꍇ�B
     * @exception InstantiationError
     *              LzssSearchMethodClassName �ŗ^����ꂽ�N���X��
     *              abstract class �ł��邽�߃C���X�^���X�𐶐��ł��Ȃ��ꍇ�B
     * @exception NoSuchMethodError
     *              LzssSearchMethodClassName �ŗ^����ꂽ�N���X��
     *              �R���X�g���N�^ LzssSearchMethod( int, int, int, byte[] )
     *              �������Ȃ��ꍇ
     */
    public LzssOutputStream( PostLzssEncoder encoder, 
                             String   LzssSearchMethodClassName,
                             Object[] LzssSearchMethodExtraArguments ){

        this.DictionarySize  = encoder.getDictionarySize();
        this.MaxMatch        = encoder.getMaxMatch();
        this.Threshold       = encoder.getThreshold();

        this.encoder         = encoder;
        this.TextBuffer      = new byte[ this.DictionarySize * 2 
                                       + this.MaxMatch ];
        this.writtenPos      = this.DictionarySize;
        this.putPos          = this.DictionarySize;
        this.searchPos       = this.DictionarySize;
        this.DictionaryLimit = this.DictionarySize;
        this.lastsearchret   = LzssOutputStream.NEEDSEARCH;

        Object[] arguments   = new Object[ LzssSearchMethodExtraArguments.length + 4 ];
        arguments[0] = new Integer( this.DictionarySize );
        arguments[1] = new Integer( this.MaxMatch );
        arguments[2] = new Integer( this.Threshold );
        arguments[3] = this.TextBuffer;
        for( int i = 0 ; i < LzssSearchMethodExtraArguments.length ; i++ ){
            arguments[4+i] = LzssSearchMethodExtraArguments[i];
        }

        try{
            this.method = (LzssSearchMethod)Factory.createInstance( 
                            LzssSearchMethodClassName, 
                            arguments );                                        //throw ClasNotfoundException, InvocationTargetException, NoSuchMethodException, InstantiationException
        }catch( ClassNotFoundException exception ){
            throw new NoClassDefFoundError( exception.getMessage() );
        }catch( InvocationTargetException exception ){
            throw new Error( exception.getTargetException().getMessage() );
        }catch( NoSuchMethodException exception ){
            throw new NoSuchMethodError( exception.getMessage() );
        }catch( InstantiationException exception ){
            throw new InstantiationError( exception.getMessage() );
        }
    }


    //------------------------------------------------------------------
    //  method of java.io.OutputStream method
    //------------------------------------------------------------------
    //  write
    //------------------------------------------------------------------
    //  public void write( int data )
    //  public void write( byte[] buffer )
    //  public void write( byte[] buffer, int index, int length )
    //------------------------------------------------------------------
    /**
     * ���k�@�\��1�o�C�g�̃f�[�^���o�͂���B<br>
     * ���ۂ�PostLzssEncoder �Ƀf�[�^���n�����̂� 
     * TextBuffer ���������ꂽ�Ƃ����A
     * flush �Ŗ����I�ɏo�͂��w���������̂݁B<br>
     * 
     * @param data 1�o�C�g�̃f�[�^
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void write( int data ) throws IOException {
        this.TextBuffer[ this.writtenPos++ ] = (byte)data;

        if( this.TextBuffer.length <= this.writtenPos ){
            this.encode( false );                                               //throws IOException
            this.slide();                                                       
        }
    }

    /**
     * ���k�@�\�� buffer ���̃f�[�^��S�ďo�͂���B<br>
     * ���ۂ�PostLzssEncoder �Ƀf�[�^���n�����̂� 
     * TextBuffer ���������ꂽ�Ƃ����A
     * flush �Ŗ����I�ɏo�͂��w���������̂݁B<br>
     * 
     * @param buffer �f�[�^�̊i�[���ꂽ�o�b�t�@
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void write( byte[] buffer ) throws IOException {
        this.write( buffer, 0, buffer.length );                                 //throws IOException
    }

    /**
     * ���k�@�\�� buffer ���� index ���� length�o�C�g�̃f�[�^���o�͂���B<br>
     * ���ۂ�PostLzssEncoder �Ƀf�[�^���n�����̂� 
     * TextBuffer ���������ꂽ�Ƃ����A
     * flush �Ŗ����I�ɏo�͂��w���������̂݁B<br>
     * 
     * @param buffer �f�[�^�̊i�[���ꂽ�o�b�t�@
     * @param index  buffer���f�[�^�J�n�ʒu
     * @param length buffer���f�[�^�̒���
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void write( byte[] buffer, int index, int length ) throws IOException {
        int pos = index;
        int end = index + length;

        while( pos < end ){
            int space = TextBuffer.length - writtenPos;
            if( end - pos < space ){
                System.arraycopy( buffer, pos, 
                                  this.TextBuffer, this.writtenPos, 
                                  end - pos );
                this.writtenPos += end - pos;
                pos = end;
            }else{
                System.arraycopy( buffer, pos, 
                                  this.TextBuffer, this.writtenPos,
                                  space );
                this.writtenPos += space;
                pos += space;
                this.encode( false );                                           //throws IOException
                this.slide();
            }
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
     * ���k�@�\�ɏ������܂ꂽ�S�Ẵf�[�^��
     * �ڑ����ꂽ PostLzssEncoder �ɏo�͂��A
     * �ڑ����ꂽ PostLzssEncoder �� flush() ����B<br>
     * ���̂Ƃ��A�o�͂���f�[�^�̏I�[�t�߂ł�
     * ������ search() ���g�p���邽�߈��k���x���ቺ����B
     * �܂� flush() ���Ȃ��ꍇ�Ɣ�ׂĈ��k�����ω�����B
     * ����� flush() �����ʒu�t�߂ł̓f�[�^�p�^���̌�����
     * MaxMatch �ɖ����Ȃ��f�[�^�p�^�����g�p���邽�߁A
     * �������ʂ��s���S�ɂȂ邽�߁B
     * ���̈��k���̕ω��́A�����̏ꍇ���k�������X�ቺ���邾���ł��邪�A
     * �Ⴆ�Ύ��̂悤�ȃR�[�h�� LZ ���k��S���s��Ȃ��B
     * <pre>
     *  public void wrongCompress( InputStream in, LzssOutputSteam out ){
     *      int r;
     *      while( 0 <= r = in.read() ){
     *          out.write( r );
     *          out.flush();
     *      }
     *  }
     * </pre>
     * �܂��A���̃��\�b�h�� PostLzssEncoder.flush() ���Ăяo������
     * flush() ���Ȃ��ꍇ�Ɣ�ׂāA�o�̓f�[�^���ω�����\��������B<br>
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     * 
     * @see PostLzssEncoder#flush()
     */
    public void flush() throws IOException {
        this.encode( false );                                                   //throw IOException
        if( this.DictionarySize * 2 <= this.putPos ){
            this.slide();
            if( this.searchPos < this.writtenPos ){
                this.encode( false );                                           //throw IOException
            }
        }
        this.encoder.flush();                                                   //throw IOException
    }

    /**
     * ���̃N���X�ɒ�����ꂽ�S�Ẵf�[�^��ڑ����ꂽ 
     * PostLzssEncoder �ɏo�͂� ���̏o�̓X�g���[���ƁA
     * �ڑ����ꂽ�X�g���[������A
     * �g�p���Ă������\�[�X���J������B
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public void close() throws IOException {
        while( this.DictionarySize <= this.writtenPos ){
            this.encode( true );                                      //throw IOException
            if( this.writtenPos <= this.searchPos ){
                break;
            }else{
                this.slide();
            }
        }

        this.encoder.close();                                                   //throw IOException
        this.encoder = null;

        this.TextBuffer = null;
        this.method     = null;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  private int encode()
    //  private void slide( int position )
    //------------------------------------------------------------------
    /**
     * TextBuffer �ɒ�����ꂽ�f�[�^�����k���Ȃ���
     * private�ϐ� this.encoder �ɏo�͂���B
     * 
     * @return TextBuffer ���̏o�͊��������f�[�^�̏I�[�ʒu + 1
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    private void encode( boolean last ) throws IOException {

        int end = Math.min( this.TextBuffer.length  - this.MaxMatch,
                            this.writtenPos - ( last ? 0 : this.method.putRequires() ) );
        if( this.searchPos < end ){

            //------------------------------------------------------------------
            //  �O����
            if( this.lastsearchret == LzssOutputStream.NEEDSEARCH ){

                //------------------------------------------------------------------
                //  �����@�\�ɖ��o�^�̃f�[�^�p�^����o�^
                while( this.putPos < this.searchPos - 1 ){
                    this.method.put( ++this.putPos );

                    //���O�� flush() �� put() �ł��Ȃ�����
                    //�f�[�^�p�^���� put() ���������̏ꍇ�� return
                    if( this.DictionarySize * 2 <= this.putPos ){
                        return;
                    }
                }

                //  lastsearchret �� NEEDSEARCH �Ȃ̂� searchAndPut �Ō�������B
                this.lastsearchret = this.method.searchAndPut( this.searchPos );
            }

            int searchret = this.lastsearchret;
            int matchlen  = LzssOutputStream.getMatchLen( searchret );
            int matchpos  = LzssOutputStream.getMatchPos( searchret );
            if( this.writtenPos - this.searchPos < matchlen ){
                matchlen = this.writtenPos - this.searchPos;
            }

            //------------------------------------------------------------------
            //  ���C�����[�v
            while( true ){
                int lastmatchlen = matchlen;
                int lastmatchoff = this.searchPos - matchpos - 1;

                searchret = this.method.searchAndPut( ++this.searchPos );
                matchlen  = LzssOutputStream.getMatchLen( searchret );
                matchpos  = LzssOutputStream.getMatchPos( searchret );
                if( this.writtenPos - this.searchPos < matchlen ){
                    matchlen = this.writtenPos - this.searchPos;
                }

                if( lastmatchlen < matchlen || lastmatchlen < this.Threshold ){
                    this.encoder.writeCode( 0xFF & this.TextBuffer[ this.searchPos - 1 ] ); //throws IOException
                    if( end <= this.searchPos ){
                        this.putPos        = this.searchPos;
                        this.lastsearchret = searchret;
                        break;
                    }
                }else{
                    this.encoder.writeCode( 256 + lastmatchlen - this.Threshold );//throws IOException
                    this.encoder.writeOffset( lastmatchoff );                   //throws IOException

                    lastmatchlen--;
                    if( this.searchPos + lastmatchlen < end ){
                        while( 0 < --lastmatchlen ){
                            this.method.put( ++this.searchPos );
                        }

                        searchret = this.method.searchAndPut( ++this.searchPos );
                        matchlen  = LzssOutputStream.getMatchLen( searchret );
                        matchpos  = LzssOutputStream.getMatchPos( searchret );
                        if( this.writtenPos - this.searchPos < matchlen ){
                            matchlen = this.writtenPos - this.searchPos;
                        }
                    }else if( end < this.searchPos + lastmatchlen ){
                        this.putPos = this.searchPos;
                        while( this.putPos < end ){
                            this.method.put( ++this.putPos );
                        }
                        this.searchPos    += lastmatchlen;
                        this.lastsearchret = LzssOutputStream.NEEDSEARCH;
                        break;
                    }else{
                        this.putPos = this.searchPos;
                        while( this.putPos < end - 1 ){
                            this.method.put( ++this.putPos );
                        }
                        this.putPos++;
                        this.searchPos    += lastmatchlen;
                        this.lastsearchret = this.method.searchAndPut( this.searchPos );
                        break;
                    }
                }// if( lastmatchlen < matchlen || lastmatchlen < this.Threshold )
            }// while( true )
        }// if( this.searchPos < end )

        //------------------------------------------------------------------
        //  flush() ��p
        //  putPos �͂��̂܂܂� searchPos �̂ݐi�߂�B
        end = Math.min( this.TextBuffer.length  - this.MaxMatch,
                        this.writtenPos );
        if( !last && this.searchPos < end ){
            if( this.lastsearchret == LzssOutputStream.NEEDSEARCH ){
                this.lastsearchret = this.method.search( this.searchPos, this.putPos );
            }
            int searchret = this.lastsearchret;
            int matchlen  = LzssOutputStream.getMatchLen( searchret );
            int matchpos  = LzssOutputStream.getMatchPos( searchret );
            if( this.writtenPos - this.searchPos < matchlen ){
                matchlen = this.writtenPos - this.searchPos;
            }

            while( this.searchPos < end ){
                int lastmatchlen = matchlen;
                int lastmatchoff = this.searchPos - matchpos - 1;

                searchret = this.method.search( ++this.searchPos, this.putPos );
                matchlen  = LzssOutputStream.getMatchLen( searchret );
                matchpos  = LzssOutputStream.getMatchPos( searchret );
                if( this.writtenPos - this.searchPos < matchlen ){
                    matchlen = this.writtenPos - this.searchPos;
                }

                if( lastmatchlen < matchlen || lastmatchlen < this.Threshold ){
                    this.encoder.writeCode( 0xFF & this.TextBuffer[this.searchPos - 1] ); //throws IOException
                }else{
                    this.encoder.writeCode( 256 + lastmatchlen - this.Threshold );  //throws IOException
                    this.encoder.writeOffset( lastmatchoff );                       //throws IOException

                    this.searchPos += lastmatchlen - 1;
                    searchret = this.method.search( this.searchPos, this.putPos );
                    matchlen  = LzssOutputStream.getMatchLen( searchret );
                    matchpos  = LzssOutputStream.getMatchPos( searchret );
                    if( this.writtenPos - this.searchPos < matchlen ){
                        matchlen = this.writtenPos - this.searchPos;
                    }
                }
            }
            this.lastsearchret = LzssOutputStream.NEEDSEARCH;
        }
    }


    /**
     * TextBuffer����position�܂ł̃f�[�^��
     * �O���ֈړ�����
     * 
     * @param position ���� TextBuffer����
     *                 DictionarySize �̈ʒu�ɗ���ׂ�
     *                 �v�f�����݂���index
     */
    private void slide(){
        this.DictionaryLimit = Math.max( 0, this.DictionaryLimit - this.DictionarySize );

        this.method.slide();

        if( this.lastsearchret != LzssOutputStream.NEEDSEARCH ){
            int matchlen = LzssOutputStream.getMatchLen( this.lastsearchret );
            int matchpos = LzssOutputStream.getMatchPos( this.lastsearchret );
            this.lastsearchret = LzssOutputStream.createSearchReturn( 
                                    matchlen, matchpos - this.DictionarySize );
        }

        this.writtenPos -= this.DictionarySize;
        this.searchPos  -= this.DictionarySize;
        this.putPos     -= this.DictionarySize;
        for( int i = this.DictionaryLimit ; i < this.writtenPos ; i++ )
            this.TextBuffer[ i ] = this.TextBuffer[ i + this.DictionarySize ];
    }


    //------------------------------------------------------------------
    //  shared methods
    //------------------------------------------------------------------
    //  private static final int createSearchReturn( int matchlen, int matchpos )
    //  private static final int getMatchLen( int searchret )
    //  private static final int getMatchPos( int searchret )
    //------------------------------------------------------------------
    /**
     * search �̖߂�l�𐶐�����B
     * search �͈�v�ʒu��Ԃ����A��v���������ɕԂ����ق���
     * ���ɕ֗��ł��邽�߁A��v�ʒu����v�����K�v�ȃr�b�g����
     * ���Ȃ����Ƃ𗘗p���� int�^�ł���肷��B
     * ���̂��߂̓��ꂵ��������񑩂���֐��B
     * ���̊֐��Ő������ꂽ�l���� ��v�ʒu���v�������o���ۂɂ�
     * getMatchLen�A getMatchPos ���g�p����B
     * 
     * @param matchlen ��v��
     * @param matchpos ��v�ʒu
     * 
     * @return ��v���ƈ�v�ʒu�̏����܂�search�̖߂�l
     */
    public static final int createSearchReturn( int matchlen, int matchpos ){
        return matchlen << 22 | matchpos;
    }

    /**
     * createSearchReturn �Ő������ꂽ search�̖߂�l����
     * ��v�������o���B
     * 
     * @param searchret search �̖߂�l
     * 
     * @return ��v��
     */
    public static final int getMatchLen( int searchret ){
        return searchret >> 22;
    }

    /**
     * createSearchReturn �Ő������ꂽ search�̖߂�l����
     * ��v�ʒu�����o���B
     * 
     * @param searchret search �̖߂�l
     * 
     * @return ��v�ʒu
     */
    public static final int getMatchPos( int searchret ){
        if( 0 <= searchret ) return searchret & 0x3FFFFF;
        else                 return -1;
    }

}
//end of LzssOutputStream.java
