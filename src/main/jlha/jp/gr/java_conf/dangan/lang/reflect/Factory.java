//start of Factory.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * Factory.java
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

package jp.gr.java_conf.dangan.lang.reflect;

//import classes and interfaces

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

//import exceptions

/**
 * �N���X���� �����ɂȂ�Object �̔z�񂩂�A
 * createInstance() �ɂ���ĐV�����C���X�^���X�����o��
 * ���[�e�B���e�B�N���X�B
 * 
 * <pre>
 * -- revision history --
 * $Log: Factory.java,v $
 * Revision 1.0  2002/10/01 00:00:00  dangan
 * first edition
 * add to version control
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.0 $
 */
public class Factory{


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private Factory()
    //------------------------------------------------------------------
    /**
     * �f�t�H���g�R���X�g���N�^�B
     * �g�p�s�B
     */
    private Factory(){  }


    //------------------------------------------------------------------
	//	shared method
    //------------------------------------------------------------------
	//	create instance
    //------------------------------------------------------------------
    //  public static Object createInstance( String classname, Object[] args )
    //  public static Object createInstance( Class clas, Object[] args )
    //------------------------------------------------------------------
    /**
     * classname �Ŏ������N���X�̃C���X�^���X�𐶐�����B
     * �R���X�g���N�^�ɂ� args �̌^�ƈ�v������̂��g�p����B
     * 
     * @param classname �N���X��
     * @param args      �����̔z��
     * 
     * @return �������ꂽ�C���X�^���X
     *         args �ƌ^��񂪃}�b�`����
     *         �R���X�g���N�^�����݂��Ȃ������ꍇ�� null
     * 
     * @exception InvocationTargetException
     *                 �R���X�g���N�^�ŗ�O�����������ꍇ
     * 
     * @exception InstantiationException
     *                 abstract�N���X�̃C���X�^���X�𓾂悤�Ƃ����ꍇ
     * 
     * @exception ClassNotFoundException
     *                 classname �Ŏ������N���X�����݂��Ȃ��ꍇ
     */
    public static Object createInstance( String classname, Object[] args )
                                              throws InvocationTargetException,
                                                     InstantiationException,
                                                     ClassNotFoundException,
                                                     NoSuchMethodException {
        return Factory.createInstance( Class.forName( classname ), args );
    }

    /**
     * type �Ŏ������N���X�̃C���X�^���X�𐶐�����B
     * �R���X�g���N�^�ɂ� args �̌^�ƈ�v������̂��g�p����B
     * 
     * @param type �N���X��
     * @param args �����̔z��
     * 
     * @return �������ꂽ�C���X�^���X
     *         args �ƌ^��񂪃}�b�`����
     *         �R���X�g���N�^�����݂��Ȃ������ꍇ�� null
     * 
     * @exception InvocationTargetException
     *                 �R���X�g���N�^�ŗ�O�����������ꍇ
     * 
     * @exception InstantiationException
     *                 abstract�N���X�̃C���X�^���X�𓾂悤�Ƃ����ꍇ
     */
    public static Object createInstance( Class type, Object[] args ) 
                                              throws InvocationTargetException,
                                                     InstantiationException,
                                                     NoSuchMethodException {
        Constructor constructor = Factory.getMatchFullConstructor( type, args );

        if( constructor == null ){
            constructor = Factory.getConstructor( type, args );

            if( constructor != null )
                args        = Type.parseAll( constructor.getParameterTypes(), args );
        }

        if( constructor != null ){
            try{
                return constructor.newInstance( args );
            }catch( IllegalAccessException exception ){
                throw new IllegalAccessError( exception.toString() );
            }
        }else{
            throw new NoSuchMethodException();
        }
    }


    //------------------------------------------------------------------
	//	shared method
    //------------------------------------------------------------------
    //  get constructor
    //------------------------------------------------------------------
    //  public static Constructor getConstructor( String   classname,
    //                                            Object[] args )
    //  public static Constructor getConstructor( Class type, Object[] args )
    //  public static Constructor getConstructor( String   classname,
    //                                            Object[] args, boolean all )
    //  public static Constructor getConstructor( Class type,
    //                                            Object[] args, boolean  all )
    //------------------------------------------------------------------
    /**
     * classname �Ŏ������N���X�� public �ȃR���X�g���N�^�̂����A
     * args �� Type.parse �����ꍇ�A�����Ƃ��Ď󂯓���邱�Ƃ�
     * �ł�����̂𓾂�B
     * 
     * @param classname �N���X���B
     *                  ���̌^�̃C���X�^���X�𐶐����邽�߂�
     *                  �R���X�g���N�^�𓾂�B
     * @param args      �����z��B
     *                  null ���܂߂Ă��ǂ����Anull ���g�p�����ꍇ��
     *                  Object �̃T�u�N���X�ł���ΑS�ă}�b�`���Ă��܂����߁A
     *                  �ړI�̃R���X�g���N�^�ȊO�̂��̂�������\��������B
     * 
     * @return args �������Ɏ�邱�Ƃ��ł���R���X�g���N�^�B
     *         ������Ȃ���� null�B
     * 
     * @exception ClassNotFoundException
     *                 classname �Ŏ������N���X�����݂��Ȃ��ꍇ
     */
    public static Constructor getConstructor( String   classname,
                                              Object[] args ) 
                                                throws  ClassNotFoundException {
        return Factory.getConstructor( Class.forName( classname ),
                                       args );
    }
    
    /**
     * type ��public �ȃR���X�g���N�^�̂����Aargs �� 
     * Type.parse �����ꍇ �����Ƃ��Ď󂯓���邱�Ƃ��ł�����̂𓾂�B
     * 
     * @param type �^���B 
     *             ���̌^�̃C���X�^���X�𐶐����邽�߂̃R���X�g���N�^�𓾂�B
     * @param args �����z��B
     *             null ���܂߂Ă��ǂ����Anull ���g�p�����ꍇ��
     *             Object �̃T�u�N���X�ł���ΑS�ă}�b�`���Ă��܂����߁A
     *             �ړI�̃R���X�g���N�^�ȊO�̂��̂�������\��������B
     * 
     * @return args �������Ɏ�邱�Ƃ��ł���R���X�g���N�^�B
     *         ������Ȃ���� null�B
     */
    public static Constructor getConstructor( Class    type,
                                              Object[] args ){
        return Factory.getConstructor( type, args, false );
    }

    /**
     * classname�Ŏ������N���X�� �R���X�g���N�^�̂����Aargs �� 
     * Type.parse ���� �����Ƃ��Ď󂯓���邱�Ƃ��ł�����̂𓾂�B
     * 
     * @param classname �N���X���B
     *                  ���̌^�̃C���X�^���X�𐶐����邽�߂̃R���X�g���N�^�𓾂�B
     * @param args      �����z��B
     *                  null ���܂߂Ă��ǂ����Anull ���g�p�����ꍇ��
     *                  Object �̃T�u�N���X�ł���ΑS�ă}�b�`���Ă��܂����߁A
     *                  �ړI�̃R���X�g���N�^�ȊO�̂��̂�������\��������B
     * @param all  public �̃R���X�g���N�^�݂̂���������Ȃ� false�B
     *             public, protected, private, �p�b�P�[�W�v���C�x�[�g��
     *             �S�ẴR���X�g���N�^�����񂳂�����Ȃ� true�B
     * 
     * @return args �������Ɏ�邱�Ƃ��ł���R���X�g���N�^�B
     *         ������Ȃ���� null�B
     * 
     * @exception ClassNotFoundException
     *                 classname �Ŏ������N���X�����݂��Ȃ��ꍇ
     */
    public static Constructor getConstructor( String   classname,
                                              Object[] args,
                                              boolean  all ) 
                                                throws  ClassNotFoundException {
        return Factory.getConstructor( Class.forName( classname ),
                                       args,
                                       all );
    }

    /**
     * type �̃R���X�g���N�^�̂����Aargs �� Type.parse �����ꍇ
     * �����Ƃ��Ď󂯓���邱�Ƃ��ł�����̂𓾂�B
     * 
     * @param type �^���B 
     *             ���̌^�̃C���X�^���X�𐶐����邽�߂̃R���X�g���N�^�𓾂�B
     * @param args �����z��B
     *             null ���܂߂Ă��ǂ����Anull ���g�p�����ꍇ��
     *             Object �̃T�u�N���X�ł���ΑS�ă}�b�`���Ă��܂����߁A
     *             �ړI�̃R���X�g���N�^�ȊO�̂��̂�������\��������B
     * @param all  public �̃R���X�g���N�^�݂̂���������Ȃ� false�B
     *             public, protected, private, �p�b�P�[�W�v���C�x�[�g��
     *             �S�ẴR���X�g���N�^�����񂳂�����Ȃ� true�B
     * 
     * @return args �������Ɏ�邱�Ƃ��ł���R���X�g���N�^�B
     *         ������Ȃ���� null�B
     */
    public static Constructor getConstructor( Class    type,
                                              Object[] args,
                                              boolean  all ){
        Constructor[] constructors = all 
                                   ? type.getDeclaredConstructors()
                                   : type.getConstructors();

        for( int i = 0 ; i < constructors.length ; i++ )
            if( Type.matchFullAll( constructors[i].getParameterTypes(), args ) )
                return constructors[i];

        for( int i = 0 ; i < constructors.length ; i++ )
            if( Type.matchRestrictAll( constructors[i].getParameterTypes(), args ) )
                return constructors[i];

        for( int i = 0 ; i < constructors.length ; i++ )
            if( Type.matchAll( constructors[i].getParameterTypes(), args ) )
                return constructors[i];

        
        return null;        
    }


    //------------------------------------------------------------------
	//	shared method
    //------------------------------------------------------------------
    //  get match full constructor
    //------------------------------------------------------------------
    //  public static Constructor getMatchFullConstructor( String   classname,
    //                                                     Object[] args )
    //  public static Constructor getMatchFullConstructor( Class type, 
    //                                                     Object[] args )
    //  public static Constructor getMatchFullConstructor( String   classname,
    //                                            Object[] args, boolean all )
    //  public static Constructor getMatchFullConstructor( Class type, 
    //                                            Object[] args, boolean all )
    //------------------------------------------------------------------
    /**
     * classname �Ŏ������N���X�� public �ȃR���X�g���N�^�̂����A
     * args �� ���̂܂܈����Ƃ��Ď󂯓���邱�Ƃ��ł�����̂𓾂�B
     * 
     * @param classname �N���X���B
     *                  ���̌^�̃C���X�^���X�𐶐����邽�߂�
     *                  �R���X�g���N�^�𓾂�B
     * @param args      �����z��B
     *                  null ���܂߂Ă��ǂ����Anull ���g�p�����ꍇ��
     *                  Object �̃T�u�N���X�ł���ΑS�ă}�b�`���Ă��܂����߁A
     *                  �ړI�̃R���X�g���N�^�ȊO�̂��̂�������\��������B
     * 
     * @return args �������Ɏ�邱�Ƃ��ł���R���X�g���N�^�B
     *         ������Ȃ���� null�B
     * 
     * @exception ClassNotFoundException
     *                 classname �Ŏ������N���X�����݂��Ȃ��ꍇ
     */
    public static Constructor getMatchFullConstructor( String   classname,
                                                       Object[] args ) 
                                                throws  ClassNotFoundException {
        return Factory.getMatchFullConstructor( Class.forName( classname ),
                                                args );
    }

    /**
     * type �� public �ȃR���X�g���N�^�̂����Aargs ��
     * ���̂܂܈����Ƃ��Ď󂯓���邱�Ƃ��ł�����̂𓾂�B
     * 
     * @param type �^���B 
     *             ���̌^�̃C���X�^���X�𐶐����邽�߂̃R���X�g���N�^�𓾂�B
     * @param args �����z��B
     *             null ���܂߂Ă��ǂ����Anull ���g�p�����ꍇ��
     *             Object �̃T�u�N���X�ł���ΑS�ă}�b�`���Ă��܂����߁A
     *             �ړI�̃R���X�g���N�^�ȊO�̂��̂�������\��������B
     * 
     * @return args �������Ɏ�邱�Ƃ��ł���R���X�g���N�^�B
     *         ������Ȃ���� null�B
     */
    public static Constructor getMatchFullConstructor( Class    type, 
                                                       Object[] args ){
        return Factory.getMatchFullConstructor( type, args, false );
    }

    /**
     * classname �Ŏ������N���X�� �R���X�g���N�^�̂����A
     * args �� ���̂܂܈����Ƃ��Ď󂯓���邱�Ƃ��ł�����̂𓾂�B
     * 
     * @param classname �N���X���B
     *                  ���̌^�̃C���X�^���X�𐶐����邽�߂�
     *                  �R���X�g���N�^�𓾂�B
     * @param args      �����z��B
     *                  null ���܂߂Ă��ǂ����Anull ���g�p�����ꍇ��
     *                  Object �̃T�u�N���X�ł���ΑS�ă}�b�`���Ă��܂����߁A
     *                  �ړI�̃R���X�g���N�^�ȊO�̂��̂�������\��������B
     * 
     * @return args �������Ɏ�邱�Ƃ��ł���R���X�g���N�^�B
     *         ������Ȃ���� null�B
     * 
     * @exception ClassNotFoundException
     *                 classname �Ŏ������N���X�����݂��Ȃ��ꍇ
     */
    public static Constructor getMatchFullConstructor( String   classname,
                                                       Object[] args,
                                                       boolean  all ) 
                                                throws  ClassNotFoundException {
        return Factory.getMatchFullConstructor( Class.forName( classname ),
                                                args, 
                                                all );
    }

    /**
     * type �̃R���X�g���N�^�̂����Aargs ��
     * ���̂܂܈����Ƃ��Ď󂯓���邱�Ƃ��ł�����̂𓾂�B
     * 
     * @param type �^���B 
     *             ���̌^�̃C���X�^���X�𐶐����邽�߂̃R���X�g���N�^�𓾂�B
     * @param args �����z��B
     *             null ���܂߂Ă��ǂ����Anull ���g�p�����ꍇ��
     *             Object �̃T�u�N���X�ł���ΑS�ă}�b�`���Ă��܂����߁A
     *             �ړI�̃R���X�g���N�^�ȊO�̂��̂�������\��������B
     * @param all  public �̃R���X�g���N�^�݂̂���������Ȃ� false�B
     *             public, protected, private, �p�b�P�[�W�v���C�x�[�g��
     *             �S�ẴR���X�g���N�^�����񂳂�����Ȃ� true�B
     * 
     * @return args �������Ɏ�邱�Ƃ��ł���R���X�g���N�^�B
     *         ������Ȃ���� null�B
     */
    public static Constructor getMatchFullConstructor( Class    type, 
                                                       Object[] args,
                                                       boolean  all ){
        Constructor[] constructors = all 
                                   ? type.getDeclaredConstructors()
                                   : type.getConstructors();

        for( int i = 0 ; i < constructors.length ; i++ )
            if( Type.matchFullAll( constructors[i].getParameterTypes(), args ) )
                return constructors[i];
        
        return null;
    }

}
//end of Factory.java
