//start of MsdosDate.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * MsdosDate.java
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

package jp.gr.java_conf.dangan.util;

//import classes and interfaces

import java.util.Date;

//import exceptions

/**
 * MS-DOS�`���̎��ԏ�������Date�̔h���N���X�B<br>
 * �f�[�^�� 4byte�l�ł���AMS-DOS�� ���Intel �� x86�nCPU���
 * ���삵�����Ƃ��� LittleEndian�Ŋi�[�����B<br>
 * �t�H�[�}�b�g�͈ȉ��̂Ƃ���B<br>
 * <pre>
 * +---------------+---------------++---------------+---------------+
 * | ���t-���byte | ���t-����byte || ����-���byte | ����-����byte |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-++-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |7|6|5|4|3|2|1|0|7|6|5|4|3|2|1|0||7|6|5|4|3|2|1|0|7|6|5|4|3|2|1|0|
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-++-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |   �N-7bit   |��-4bit| ��-5bit ||����-5bit|  ��-6bit  | �b-5bit |
 * +-------------+-------+---------++---------+-----------+---------+
 * </pre>
 * �E�N�� 1980�`2107�N �� 0�`127 �ŕ\���BWindows�̃V�X�e��API��
 *   �ꕔ�� 2099�N�܂ł����T�|�[�g���Ȃ��Ƃ�����񂪂���B<br>
 * �E���� 1�`12�� �� 1�`12�ŕ\���B0�`11�łȂ����Ƃɒ��ӁB<br>
 * �E���� 1�`31�� �� 1�`31�ŕ\���B0�`30�łȂ����Ƃɒ��ӁB<br>
 * �E���Ԃ� 0�`23�� �� 0�`23�ŕ\���B<br>
 * �E���� 0�`59�� �� 0�`59�ŕ\���B<br>
 * �E�b�� 0�`58�b �� 0�`29�ŕ\���B�b�̏��̓r�b�g��������Ȃ�
 *   ���� �ŏ��P�ʂ� 1�b�łȂ� 2�b�ł���B<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: MsdosDate.java,v $
 * Revision 1.1  2002/12/05 00:00:00  dangan
 * [maintenance]
 *     javadoc �R�����g�̃X�y���~�X���C���B
 *     �\�[�X����
 *
 * Revision 1.0  2002/07/24 00:00:00  dangan
 * add to version control
 * [bug fix]
 *     setTime() �� ftime�̌��E���x�ł���2�b�ȏ�̐��x�ŋL�^���Ă����B
 * [maintenance]
 *     �^�u�̔p�~
 *     ���C�Z���X���̏C��
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class MsdosDate extends Date
                       implements Cloneable {


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  public MsdosDate( Date date )
    //  public MsdosDate( int time )
    //------------------------------------------------------------------
    /**
     * date �Ŏ�����鎞�Ԃ�\�� MsdosDate ���\�z����B <br>
     * MS-DOS �`���̎��ԏ��ŕ\���Ȃ��ׂ������x�̏���
     * ��������A�ŏ����ԒP�ʂ� java.util.Date �� 1�~���b�łȂ�
     * MS-DOS �`���̎��ԏ�� �̍ŏ��P�ʂł��� 2�b�ƂȂ�B
     * 
     * @param date �V�����\�z����� MsdosDate �̊�ɂȂ鎞�ԏ��
     *             ������ Date�I�u�W�F�N�g
     * 
     * @exception IllegalArgumentException
     *             date �� MS-DOS���Ԍ`���ň����Ȃ��͈͂̎��Ԃ�
     *             �����Ă����ꍇ
     */
    public MsdosDate( Date date ){
        super( ( date.getTime() / 2000L ) * 2000L  );
        this.checkRange();
    }

    /**
     * MS-DOS �`���̎��ԏ�񂩂� �V���� MsdosDate ���\�z
     * ����B
     * 
     * @param time MS-DOS �`���̎��ԏ��
     */
    public MsdosDate( int time ){
        super( ( ( time >> 25 ) & 0x7F ) + 80,
               ( ( time >> 21 ) & 0x0F ) - 1,
               ( time >> 16 ) & 0x1F,
               ( time >> 11 ) & 0x1F,
               ( time >> 5 )  & 0x3F,
               ( time << 1 )  & 0x3F );                                         //deprecated

        this.checkRange();
    }


    //------------------------------------------------------------------
    //  method of java.lang.Cloneable
    //------------------------------------------------------------------
    //  public Object clone()
    //------------------------------------------------------------------
    /**
     * ���̃I�u�W�F�N�g�̃R�s�[��Ԃ��B
     * 
     * @return ����MsdosDate�I�u�W�F�N�g�̕���
     */
    public Object clone(){
        return new MsdosDate( this );
    }


    //------------------------------------------------------------------
    //  method of java.util.Date
    //------------------------------------------------------------------
    //  access method
    //------------------------------------------------------------------
    //  set method with range check
    //------------------------------------------------------------------
    //  public void setYear( int year )
    //  public void setTime( long time )
    //------------------------------------------------------------------
    /**
     * ���� MsdosDate �̎����N�� year �Ŏw�肳�ꂽ�l��1900�𑫂�
     * �����̂ɐݒ肷��B<br>
     * ���̃��\�b�h�͔͈̓`�F�b�N���s�������̂��߂ɑ��݂���B<br>
     *
     * @deprecated
     * @param year 1900�𑫂����ƂŐ����\���悤�� �N�̒l
     * 
     * @exception IllegalArgumentException
     *             year �� MS-DOS���Ԍ`���ň����Ȃ��͈͂̎��Ԃ�
     *             �����Ă����ꍇ
     */
    public void setYear( int year ){
        if( year < 80 || 207 < year ){
            throw new IllegalArgumentException( "out of MS-DOS time format range." );
        }else{
            super.setYear( year );                                              //deprecated
        }
    }

    /**
     * ���� MsdosDate �̎������Ԃ� 1970�N1��1�� 00:00:00 GMT����
     * time �~���b�o�߂��������ɐݒ肷��B<br>
     * MS-DOS �`���̎��ԏ��ŕ\���Ȃ��ׂ������x�̏���
     * ��������A�ŏ����ԒP�ʂ� java.util.Date �� 1�~���b�łȂ�
     * MS-DOS �`���̎��ԏ�� �̍ŏ��P�ʂł��� 2�b�ƂȂ�B
     * 
     * @param time 1970�N1��1�� 00:00:00GMT ����̌o�߃~���b
     * 
     * @exception IllegalArgumentException
     *             time �� MS-DOS���Ԍ`���ň����Ȃ��͈͂̎��Ԃ�
     *             �����Ă����ꍇ
     */
    public void setTime( long time ){
        int year = ( new Date( time ) ).getYear();
        if( year < 80 || 207 < year ){
            throw new IllegalArgumentException( "out of MS-DOS time format range." );
        }else{
            super.setTime( ( time / 2000L ) * 2000L );
        }
    }


    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  access method of MS-DOS time format
    //------------------------------------------------------------------
    //  public void setMsdosTime( int time )
    //  public int getMsdosTime()
    //------------------------------------------------------------------
    /**
     * ���� MsdosDate �� MS-DOS ���Ԍ`���̎��ԏ���ݒ肷��B
     * 
     * @param time MS-DOS ���Ԍ`���̎��ԏ��
     */
    public void setMsdosTime( int time ){
        Date date = new Date( ( ( time >> 25 ) & 0x7F ) + 80,
                              ( ( time >> 21 ) & 0x0F ) - 1,
                              ( time >> 16 ) & 0x1F,
                              ( time >> 11 ) & 0x1F,
                              ( time >> 5 )  & 0x3F,
                              ( time << 1 )  & 0x3F );                          //deprecated

        this.setTime( date.getTime() );
    }

    /**
     * ���� MsdosDate���������ԏ��� MS-DOS ���Ԍ`���œ���B
     * 
     * @return MS-DOS���Ԍ`���̒l
     */
    public int getMsdosTime(){
        return ( ( super.getYear() - 80 ) << 25 )                               //deprecated
               | ( ( super.getMonth() + 1 ) << 21 )                             //deprecated
               | ( super.getDate()    << 16 )                                   //deprecated
               | ( super.getHours()   << 11 )                                   //deprecated
               | ( super.getMinutes() <<  5 )                                   //deprecated
               | ( super.getSeconds() >>  1 );                                  //deprecated
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  private void checkRange()
    //------------------------------------------------------------------
    /**
     * ���� MsdosDate �� MS-DOS���Ԍ`���ŕ\���鎞�Ԃ͈͓̔���
     * ���邩�𔻒肷��B
     * 
     * @exception IllegalArgumentException
     *             ���� MsdosDate �� MS-DOS���Ԍ`���ň����Ȃ�
     *             �͈͂̎��Ԃ������Ă����ꍇ
     */
    private void checkRange(){
        int year = this.getYear();
        if( year < 80 || 207 < year )
            throw new IllegalArgumentException( "out of MS-DOS time format range." );
    }

}
//end of MsdosDate.java
