//start of WindowsDate.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * WindowsDate.java
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

package jp.gr.java_conf.dangan.util;

//import classes and interfaces

import java.util.Date;

//import exceptions

/**
 * Windows��FILETIME�`���̏�������Date�̔h���N���X�B<br>
 * FILETIME �� 1601�N 1�� 1�� 0��0��0�b����̌o�ߎ��Ԃ�
 * 100�i�m�b�P�ʂŎ���64�r�b�g�l�B<br>
 * ���̃N���X�ł� FILETIME �� long(64�r�b�g�l)�Ƃ��Ĉ����Ƃ���
 * ��{�I�ɕ��������Ƃ݂Ȃ��B<br>
 * 1601�N 1�� 1�� 0��0��0�b�ȑO�̎��Ԃ����������ꍇ��
 * WindowsDate( Date date ) ���AWindowsDate.setTime( long time )���g�p����B<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: WindowsDate.java,v $
 * Revision 1.1  2002/12/05 00:00:00  dangan
 * [maintenance]
 *     javadoc �R�����g�̃X�y���~�X���C���B
 *     �\�[�X����
 *
 * Revision 1.0  2002/08/05 00:00:00  dangan
 * add to version control
 * [bug fix] 
 *     set�n���\�b�h�� �͈͊O�̎��Ԃ��Z�b�g���悤�Ƃ���
 *     ��O�𓊂���P�[�X�Ŏ��Ԃ̏����߂����������s���Ă��Ȃ������B
 *     checkRange �̎��Ԃ͈̔͂��Ԉ���Ă����B
 * [maintenance]
 *     �^�u�̔p�~
 *     ���C�Z���X���̏C��
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class WindowsDate extends Date
                         implements Cloneable{


    //------------------------------------------------------------------
    //  class field
    //------------------------------------------------------------------
    //  public static final long TIME_DIFFERENCE
    //------------------------------------------------------------------
    /**
     * FILETIME�`���̃f�[�^�ƁAjava.util.Date.getTime() ��
     * �����鎞�Ԍ`���Ƃ̎��ԍ��� 100�i�m�Z�J���h�P�ʂŎ��������l�B
     * �Ȃ��A�[�b���͍l���ɓ���Ă��Ȃ��B
     */
    public static final long TIME_DIFFERENCE = 0x19DB1DED53E8000L;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  private int NanoSecounds
    //------------------------------------------------------------------
    /**
     * java.util.Date �ł͕ێ��ł��Ȃ� 
     * �i�m�b�P�ʂ̎��Ԃ�ێ����邽�߂ɗp����B
     */
    private int NanoSecounds;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  public WindowsDate()
    //  public WindowsDate( Date date )
    //  public WindowsDate( long time )
    //------------------------------------------------------------------
    /**
     * �f�t�H���g�R���X�g���N�^�B
     * ���݂̎��ԏ������� WindowsDate���\�z����B
     * �i�m�b�P�ʂ̎��Ԃ͎擾�ł��Ȃ����߁A0�ɐݒ肳���B
     * 
     * @exception IllegalArgumentException
     *                  ���݂̎��Ԃ� FILETIME �`���ŕ\���ł���
     *                  �͈͊O�������ꍇ�B
     */
    public WindowsDate(){
        super();
        this.NanoSecounds = 0;

        this.checkRange();
    }

    /**
     * date�Ŏ�����鎞�Ԃ�\�� WindowsDate���\�z����B<br>
     * date�� WindowsDate �̃C���X�^���X�Ȃ��
     * �i�m�b�P�ʂ̏����R�s�[����邪�A����ȊO�̏ꍇ��
     * �i�m�b�P�ʂ̏��ɂ� 0 ���ݒ肳���B
     * 
     * @param date �V�����\�z����� WindowsDate �̌��ƂȂ鎞�ԏ������� 
     *             Date �̃I�u�W�F�N�g
     * 
     * @exception IllegalArgumentException
     *                  ���݂̎��Ԃ� FILETIME �`���ŕ\���ł���
     *                  �͈͊O�������ꍇ�B
     */
    public WindowsDate( Date date ){
        super( date.getTime() );
        if( date instanceof WindowsDate ){
            this.NanoSecounds = ((WindowsDate)date).NanoSecounds;
        }else{
            this.NanoSecounds = 0;
            this.checkRange();
        }
    }

    /**
     * ��������64�r�b�g��FILETIME�`���̎��ԏ�񂩂�
     * �V����WindowsDate���\�z����B<br>
     * 
     * @param time FILETIME�`���̎��ԏ��
     */
    public WindowsDate( long time ){
        super( 0 <= time 
         ? ( time - WindowsDate.TIME_DIFFERENCE ) / 10000L
         : ( ( time >>> 1 ) - ( WindowsDate.TIME_DIFFERENCE >>> 1 ) ) / 5000L );

        this.NanoSecounds = 
                 (int)( ( time >>> 1 ) % 5000L * 2 + ( time & 1 ) ) * 100;
    }


    //------------------------------------------------------------------
    //  method of java.lang.Cloneable
    //------------------------------------------------------------------
    //  public Object clone()
    //------------------------------------------------------------------
    /**
     * ���̃I�u�W�F�N�g�̃R�s�[��Ԃ��B
     * 
     * @return ����WindowsDate�I�u�W�F�N�g�̕���
     */
    public Object clone(){
        return new WindowsDate( this );
    }


    //------------------------------------------------------------------
    //  method of java.util.Date
    //------------------------------------------------------------------
    //  set method with range check
    //------------------------------------------------------------------
    //  public void setYear( int year )
    //  public void setMonth( int month )
    //  public void setDate( int day )
    //  public void setHours( int hour )
    //  public void setMinutes( int minute )
    //  public void setSecounds( int secound )
    //  public void setTime( long time )
    //------------------------------------------------------------------
    /**
     * ���� WindowsDate �̎����N�� year ��
     * �w�肳�ꂽ�l��1900�𑫂������̂ɐݒ肷��B<br>
     * ���̃��\�b�h�͔͈̓`�F�b�N���s�������̂��߂ɑ��݂���B<br>
     *
     * @param year 1900�𑫂����ƂŐ����\���悤�� �N�̒l
     * 
     * @exception IllegalArgumentException
     *             year �ɕύX�����Ƃ��� FILETIME�`���ň����Ȃ�
     *             �͈͂̎��ԂɂȂ����ꍇ
     * @deprecated
     */
    public void setYear( int year ){
        long temp = this.getTime();

        try{
            super.setYear( year );
            this.checkRange();
        }catch( IllegalArgumentException exception ){
            this.setTime( temp );
            throw exception;
        }
    }

    /**
     * ���� WindowsDate �̎������� month �Ŏw�肳�ꂽ�l�ɐݒ肷��B<br>
     * ���̃��\�b�h�͔͈̓`�F�b�N���s�������̂��߂ɑ��݂���B<br>
     *
     * @param month 0��1���A1��2���������悤�Ȍ��̒l
     * 
     * @exception IllegalArgumentException
     *             month �ɕύX�����Ƃ��� FILETIME�`���ň����Ȃ�
     *             �͈͂̎��ԂɂȂ����ꍇ
     * @deprecated
     */
    public void setMonth( int month ){
        long temp = this.getTime();

        try{
            super.setMonth( month );
            this.checkRange();
        }catch( IllegalArgumentException exception ){
            this.setTime( temp );
            throw exception;
        }
    }

    /**
     * ���� WindowsDate �̎��� �ꃖ����
     * ���ł̉����ڂ��� date �Ŏw�肳�ꂽ�l�ɐݒ肷��B<br>
     * ���̃��\�b�h�͔͈̓`�F�b�N���s�������̂��߂ɑ��݂���B<br>
     *
     * @param date 1��1���A2��2���������悤�ȓ��̒l
     * 
     * @exception IllegalArgumentException
     *             date �ɕύX�����Ƃ��� FILETIME�`���ň����Ȃ�
     *             �͈͂̎��ԂɂȂ����ꍇ
     * @deprecated
     */
    public void setDate( int date ){
        long temp = this.getTime();

        try{
            super.setDate( date );
            this.checkRange();
        }catch( IllegalArgumentException exception ){
            this.setTime( temp );
            throw exception;
        }
    }

    /**
     * ���� WindowsDate �̎�������̒��ł̎��Ԃ�
     * hours �Ŏw�肳�ꂽ�l�ɐݒ肷��B<br>
     * ���̃��\�b�h�͔͈̓`�F�b�N���s�������̂��߂ɑ��݂���B<br>
     *
     * @param hours ���Ԃ̒l
     * 
     * @exception IllegalArgumentException
     *             hours �ɕύX�����Ƃ��� FILETIME�`���ň����Ȃ�
     *             �͈͂̎��ԂɂȂ����ꍇ
     * @deprecated
     */
    public void setHours( int hours ){
        long temp = this.getTime();

        try{
            super.setHours( hours );
            this.checkRange();
        }catch( IllegalArgumentException exception ){
            this.setTime( temp );
            throw exception;
        }
    }

    /**
     * ���� WindowsDate �̎����ꎞ�Ԃ̒��ł̕���
     * minutes �Ŏw�肳�ꂽ�l�ɐݒ肷��B<br>
     * ���̃��\�b�h�͔͈̓`�F�b�N���s�������̂��߂ɑ��݂���B<br>
     *
     * @param minutes ���̒l
     * 
     * @exception IllegalArgumentException
     *             minutes �ɕύX�����Ƃ��� FILETIME�`���ň����Ȃ�
     *             �͈͂̎��ԂɂȂ����ꍇ
     * @deprecated
     */
    public void setMinutes( int minutes ){
        long temp = this.getTime();

        try{
            super.setMinutes( minutes );
            this.checkRange();
        }catch( IllegalArgumentException exception ){
            this.setTime( temp );
            throw exception;
        }
    }

    /**
     * ���� WindowsDate �̎����ꕪ�̒��ł̕b����
     * secounds �Ŏw�肳�ꂽ�l�ɐݒ肷��B<br>
     * ���̃��\�b�h�͔͈̓`�F�b�N���s�������̂��߂ɑ��݂���B<br>
     *
     * @param secounds �b��
     * 
     * @exception IllegalArgumentException
     *             secounds �ɕύX�����Ƃ��� FILETIME�`���ň����Ȃ�
     *             �͈͂̎��ԂɂȂ����ꍇ
     * @deprecated
     */
    public void setSeconds( int seconds ){
        long temp = this.getTime();

        try{
            super.setSeconds( seconds );
            this.checkRange();
        }catch( IllegalArgumentException exception ){
            this.setTime( temp );
            throw exception;
        }
    }

    /**
     * ���� WindowsDate �̎������Ԃ� 
     * 1970�N1��1�� 00:00:00 GMT����
     * time �~���b�o�߂��������ɐݒ肷��B<br>
     * ���̃��\�b�h�͔͈̓`�F�b�N���s�������̂��߂ɑ��݂���B<br>
     * 
     * @param time 1970�N1��1�� 00:00:00GMT ����̌o�߃~���b
     * 
     * @exception IllegalArgumentException
     *             time ��FILETIME�`���ň����Ȃ�
     *             �͈͂̎��Ԃ������Ă����ꍇ
     */
    public void setTime( long time ){
        long temp = this.getTime();

        try{
            super.setTime( time );
            this.checkRange();
        }catch( IllegalArgumentException exception ){
            this.setTime( temp );
            throw exception;
        }
    }

    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  access method with FILETIME format
    //------------------------------------------------------------------
    //  public void setWindowsTime( long time )
    //  public long getWindowsTime()
    //------------------------------------------------------------------
    /**
     * ���� WindowsDate �� FILETIME�`���̎��ԏ���ݒ肷��B
     * 
     * @param time FILETIME�`���̎��ԏ��
     */
    public void setWindowsTime( long time ){
        super.setTime( 0 <= time 
         ? ( time - WindowsDate.TIME_DIFFERENCE ) / 10000L
         : ( ( time >>> 1 ) - ( WindowsDate.TIME_DIFFERENCE >>> 1 ) ) / 5000L );

        this.NanoSecounds = 
                 (int)( ( time >>> 1 ) % 5000L * 2 + ( time & 1 ) ) * 100;
    }

    /**
     * ���� WindowsDate���������ԏ��� FILETIME �`���œ���B
     * 
     * @return FILETIME�`���̒l
     */
    public long getWindowsTime() {
        return ( super.getTime() * 10000L + WindowsDate.TIME_DIFFERENCE
                 + (long)( this.NanoSecounds / 100 ) );
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  private void checkRange()
    //------------------------------------------------------------------
    /**
     * ���� WindowsDate �� FILETIME�`���ŕ\���鎞�Ԃ�
     * �͈͓��ł��邩�𔻒肷��B�܂��s���S
     * 
     * @exception IllegalArgumentException
     *             ���� WindowsDate �� FILETIME�`���ň����Ȃ�
     *             �͈͂̎��Ԃ������Ă����ꍇ
     */
    private void checkRange(){
        long time = super.getTime();
        if( !( 0xFFFCAE8C71E60F9BL <= time && time <= 0x000683218A10A8CBL ) )
            throw new IllegalArgumentException( "outside of range of Windows FILETIME format. " );
    }

}
