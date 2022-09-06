//start of MethodUtil.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * MethodUtil.java
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

//import exceptions

/**
 * ���\�b�h�Ɋւ��郆�[�e�B���e�B�N���X�B
 * 
 * <pre>
 * -- revision history --
 * $Log: MethodUtil.java,v $
 * Revision 1.0  2002/10/01 00:00:00  dangan
 * first edition
 * add to version control
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.0 $
 */
public class MethodUtil{


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private MethodUtil()
    //------------------------------------------------------------------
    /**
     * �f�t�H���g�R���X�g���N�^�B
     * �g�p�s�B
     */
    private MethodUtil(){  }


    //------------------------------------------------------------------
	//	shared method
    //------------------------------------------------------------------
    //  invoke static method
    //------------------------------------------------------------------
    //  public static Object invoke( Object obj, String name, Object[] args )
    //------------------------------------------------------------------
    /**
     * �C���X�^���Xobj�� name�Ƃ������O��
     * ���\�b�h��args�������Ƃ��Ď��s����B
     * 
     * @param obj  �C���X�^���X
     * @param name ���\�b�h��
     * @param args �����̔z��
     * 
     * @return �߂�l
     * 
     * @exception InvocationTargetException
     *                 �R���X�g���N�^�ŗ�O�����������ꍇ
     * 
     * @exception NoSuchMethodException
     *                 args �������Ɏ��� name �Ƃ������O��
     *                 �C���X�^���X���\�b�h��������Ȃ������ꍇ�B
     */
    public static Object invoke( Object obj, String name, Object[] args ) 
                                              throws InvocationTargetException,
                                                     NoSuchMethodException {
        Class  type   = obj.getClass();
        Method method = MethodUtil.getMatchFullInstanceMethod( type, name, args );

        if( method == null ){
            method    = MethodUtil.getInstanceMethod( type, name, args );

            if( method != null )
                args      = Type.parseAll( method.getParameterTypes(), args );
        }

        if( method != null ){
            try{
                return method.invoke( obj, args );
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
    //  invoke static method
    //------------------------------------------------------------------
    //  public static Object invokeStatic( String classname, String name, Object[] args )
    //  public static Object invokeStatic( Class type, String name, Object[] args )
    //------------------------------------------------------------------
    /**
     * classname �Ŏ������N���X�� name�Ƃ������O��
     * static ���\�b�h��args�������Ƃ��Ď��s����B
     * 
     * @param classname �N���X��
     * @param name      ���\�b�h��
     * @param args      �����̔z��
     * 
     * @return �߂�l
     * 
     * @exception ClassNotFoundException
     *                 classname �̃N���X��������Ȃ������ꍇ
     * 
     * @exception InvocationTargetException
     *                 �R���X�g���N�^�ŗ�O�����������ꍇ
     * 
     * @exception NoSuchMethodException
     *                 args �������Ɏ��� name �Ƃ������O��
     *                 �C���X�^���X���\�b�h��������Ȃ������ꍇ�B
     */
    public static Object invokeStatic( String   classname, 
                                       String   name, 
                                       Object[] args ) 
                                              throws ClassNotFoundException,
                                                     InvocationTargetException,
                                                     NoSuchMethodException {
        return MethodUtil.invokeStatic( Class.forName( classname ),             //throw ClassNotFoundException
                                        name, 
                                        args );                                 //throw InvocationTargetException, NoSuchMethodException
    }

    /**
     * type �Ŏ������N���X�� name�Ƃ������O��
     * static ���\�b�h��args�������Ƃ��Ď��s����B
     * 
     * @param type �^���
     * @param name ���\�b�h��
     * @param args �����̔z��
     * 
     * @return �߂�l
     * 
     * @exception InvocationTargetException
     *                 �R���X�g���N�^�ŗ�O�����������ꍇ
     * 
     * @exception NoSuchMethodException
     *                 args �������Ɏ��� name �Ƃ������O��
     *                 �C���X�^���X���\�b�h��������Ȃ������ꍇ�B
     */
    public static Object invokeStatic( Class type, String name, Object[] args ) 
                                              throws InvocationTargetException,
                                                     NoSuchMethodException {
        Method method = MethodUtil.getMatchFullStaticMethod( type, name, args );

        if( method == null ){
            method    = MethodUtil.getStaticMethod( type, name, args );

            if( method != null )
                args      = Type.parseAll( method.getParameterTypes(), args );
        }

        if( method != null ){
            try{
                return method.invoke( null, args );
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
    //  get instance method
    //------------------------------------------------------------------
    //  public static Method getInstanceMethod( String classname, 
    //                                          String name, Object[] args )
    //  public static Method getInstanceMethod( Class  type,
    //                                          String name, Object[] args )
    //  public static Method getInstanceMethod( String classname, String  name, 
    //                                          Object[] args,    boolean all )
    //  public static Method getInstanceMethod( Class  type,      String name, 
    //                                          Object[] args,    boolean all )
    //------------------------------------------------------------------
    /**
     * classname �Ŏ������N���X�� public �ȃC���X�^���X���\�b�h�̂����A
     * name �Ƃ������O�� args �� Type.parse ������
     * �󂯓���邱�Ƃ��ł�����̂𓾂�B
     * 
     * @param classname �N���X���B
     * @param name      �������郁�\�b�h���B
     * @param args      �����z��B
     *                  null ���܂߂Ă��ǂ����Anull ���g�p�����ꍇ��
     *                  Object �̃T�u�N���X�ł���ΑS�ă}�b�`���Ă��܂����߁A
     *                  �ړI�̃��\�b�h�ȊO�̂��̂�������\��������B
     * 
     * @return args �������Ɏ�邱�Ƃ��ł��� name�Ƃ������O�� ���\�b�h�B
     *         ������Ȃ���� null�B
     * 
     * @exception ClassNotFoundException
     *                 classname �Ŏ������N���X�����݂��Ȃ��ꍇ
     */
    public static Method getInstanceMethod( String   classname,
                                            String   name,
                                            Object[] args ) 
                                                throws  ClassNotFoundException {
        return MethodUtil.getInstanceMethod( Class.forName( classname ),
                                             name,
                                             args,
                                             false );
    }

    /**
     * type �� public �ȃC���X�^���X���\�b�h�̂����A
     * name �Ƃ������O�� args �� Type.parse ������
     * �󂯓���邱�Ƃ��ł�����̂𓾂�B
     * 
     * @param type �^���B
     * @param name �������郁�\�b�h���B
     * @param args �����z��B
     *             null ���܂߂Ă��ǂ����Anull ���g�p�����ꍇ��
     *             Object �̃T�u�N���X�ł���ΑS�ă}�b�`���Ă��܂����߁A
     *             �ړI�̃��\�b�h�ȊO�̂��̂�������\��������B
     * 
     * @return args �������Ɏ�邱�Ƃ��ł��� name�Ƃ������O�� ���\�b�h�B
     *         ������Ȃ���� null�B
     */
    public static Method getInstanceMethod( Class    type,
                                            String   name,
                                            Object[] args ){
        return MethodUtil.getInstanceMethod( type, name, args, false );
    }

    /**
     * classname �Ŏ������N���X�� �C���X�^���X���\�b�h�̂����A
     * name �Ƃ������O�� args �� Type.parse ������
     * �󂯓���邱�Ƃ��ł�����̂𓾂�B
     * 
     * @param classname �N���X���B
     * @param name      �������郁�\�b�h���B
     * @param args      �����z��B
     *                  null ���܂߂Ă��ǂ����Anull ���g�p�����ꍇ��
     *                  Object �̃T�u�N���X�ł���ΑS�ă}�b�`���Ă��܂����߁A
     *                  �ړI�̃��\�b�h�ȊO�̂��̂�������\��������B
     * @param all       public �̃��\�b�h�݂̂���������Ȃ� false�B
     *                  public, protected, private, �p�b�P�[�W�v���C�x�[�g��
     *                  �S�Ẵ��\�b�h����������Ȃ� true�B
     * 
     * @return args �������Ɏ�邱�Ƃ��ł��� name�Ƃ������O�� ���\�b�h�B
     *         ������Ȃ���� null�B
     * 
     * @exception ClassNotFoundException
     *                 classname �Ŏ������N���X�����݂��Ȃ��ꍇ
     */
    public static Method getInstanceMethod( String   classname,
                                            String   name,
                                            Object[] args,
                                            boolean  all ) 
                                                throws  ClassNotFoundException {
        return MethodUtil.getInstanceMethod( Class.forName( classname ),
                                             name,
                                             args,
                                             all );
    }

    /**
     * type �� �C���X�^���X���\�b�h�̂����Aname �Ƃ������O�� args ��
     * Type.parse ������ �󂯓���邱�Ƃ��ł�����̂𓾂�B
     * 
     * @param type �^���B 
     * @param name �������郁�\�b�h���B
     * @param args �����z��B
     *             null ���܂߂Ă��ǂ����Anull ���g�p�����ꍇ��
     *             Object �̃T�u�N���X�ł���ΑS�ă}�b�`���Ă��܂����߁A
     *             �ړI�̃��\�b�h�ȊO�̂��̂�������\��������B
     * @param all  public �̃��\�b�h�݂̂���������Ȃ� false�B
     *             public, protected, private, �p�b�P�[�W�v���C�x�[�g��
     *             �S�Ẵ��\�b�h����������Ȃ� true�B
     * 
     * @return args �������Ɏ�邱�Ƃ��ł��� name�Ƃ������O�� ���\�b�h�B
     *         ������Ȃ���� null�B
     */
    public static Method getInstanceMethod( Class    type,
                                            String   name,
                                            Object[] args,
                                            boolean  all ){
        Method[] methods = all 
                         ? type.getDeclaredMethods()
                         : type.getMethods();

        for( int i = 0 ; i < methods.length ; i++ )
            if( methods[i].getName().equals( name )
             && !Modifier.isStatic( methods[i].getModifiers() )
             && Type.matchFullAll( methods[i].getParameterTypes(), args ) )
                return methods[i];

        for( int i = 0 ; i < methods.length ; i++ )
            if( methods[i].getName().equals( name )
             && !Modifier.isStatic( methods[i].getModifiers() )
             && Type.matchRestrictAll( methods[i].getParameterTypes(), args ) )
                return methods[i];

        for( int i = 0 ; i < methods.length ; i++ )
            if( methods[i].getName().equals( name )
             && !Modifier.isStatic( methods[i].getModifiers() )
             && Type.matchAll( methods[i].getParameterTypes(), args ) )
                return methods[i];
        
        return null;
    }


    //------------------------------------------------------------------
	//	shared method
    //------------------------------------------------------------------
    //  get match full instance method
    //------------------------------------------------------------------
    //  public static Method getMatchFullInstanceMethod( String classname, 
    //                                    String name, Object[] args )
    //  public static Method getMatchFullInstanceMethod( Class  type,
    //                                    String name, Object[] args )
    //  public static Method getMatchFullInstanceMethod( String classname, 
    //                String  name,  Object[] args,    boolean all )
    //  public static Method getMatchFullInstanceMethod( Class  type,
    //                String name,   Object[] args,    boolean all )
    //------------------------------------------------------------------
    /**
     * classname �Ŏ������N���X�� public �ȃC���X�^���X���\�b�h�̂����A
     * name �Ƃ������O�� args �� ���ڎ󂯓���邱�Ƃ��ł�����̂𓾂�B
     * 
     * @param classname �N���X���B
     * @param name      �������郁�\�b�h���B
     * @param args      �����z��B
     *                  null ���܂߂Ă��ǂ����Anull ���g�p�����ꍇ��
     *                  Object �̃T�u�N���X�ł���ΑS�ă}�b�`���Ă��܂����߁A
     *                  �ړI�̃��\�b�h�ȊO�̂��̂�������\��������B
     * 
     * @return args �������Ɏ�邱�Ƃ��ł��� name�Ƃ������O�� ���\�b�h�B
     *         ������Ȃ���� null�B
     * 
     * @exception ClassNotFoundException
     *                 classname �Ŏ������N���X�����݂��Ȃ��ꍇ
     */
    public static Method getMatchFullInstanceMethod( String   classname,
                                                     String   name,
                                                     Object[] args ) 
                                                throws  ClassNotFoundException {
        return MethodUtil.getMatchFullInstanceMethod( 
                                  Class.forName( classname ),
                                  name,
                                  args,
                                  false );
    }

    /**
     * type �� public �ȃC���X�^���X���\�b�h�̂����A
     * name �Ƃ������O�� args �𒼐ڎ󂯓���邱�Ƃ��ł�����̂𓾂�B
     * 
     * @param type �^���B
     * @param name �������郁�\�b�h���B
     * @param args �����z��B
     *             null ���܂߂Ă��ǂ����Anull ���g�p�����ꍇ��
     *             Object �̃T�u�N���X�ł���ΑS�ă}�b�`���Ă��܂����߁A
     *             �ړI�̃��\�b�h�ȊO�̂��̂�������\��������B
     * 
     * @return args �������Ɏ�邱�Ƃ��ł��� name�Ƃ������O�� ���\�b�h�B
     *         ������Ȃ���� null�B
     */
    public static Method getMatchFullInstanceMethod( Class    type,
                                                     String   name,
                                                     Object[] args ){
        return MethodUtil.getMatchFullInstanceMethod( type, name, args, false );
    }

    /**
     * classname �Ŏ������N���X�� �C���X�^���X���\�b�h�̂����A
     * name �Ƃ������O�� args �𒼐ڎ󂯓���邱�Ƃ��ł�����̂𓾂�B
     * 
     * @param classname �N���X���B
     * @param name      �������郁�\�b�h���B
     * @param args      �����z��B
     *                  null ���܂߂Ă��ǂ����Anull ���g�p�����ꍇ��
     *                  Object �̃T�u�N���X�ł���ΑS�ă}�b�`���Ă��܂����߁A
     *                  �ړI�̃��\�b�h�ȊO�̂��̂�������\��������B
     * @param all       public �̃��\�b�h�݂̂���������Ȃ� false�B
     *                  public, protected, private, �p�b�P�[�W�v���C�x�[�g��
     *                  �S�Ẵ��\�b�h����������Ȃ� true�B
     * 
     * @return args �������Ɏ�邱�Ƃ��ł��� name�Ƃ������O�� ���\�b�h�B
     *         ������Ȃ���� null�B
     * 
     * @exception ClassNotFoundException
     *                 classname �Ŏ������N���X�����݂��Ȃ��ꍇ
     */
    public static Method getMatchFullInstanceMethod( String   classname,
                                                     String   name,
                                                     Object[] args,
                                                     boolean  all ) 
                                                throws  ClassNotFoundException {
        return MethodUtil.getMatchFullInstanceMethod( 
                                  Class.forName( classname ),
                                  name,
                                  args,
                                  all );
    }


    /**
     * type �� �C���X�^���X���\�b�h�̂����Aname �Ƃ������O�� 
     * args �𒼐ڎ󂯓���邱�Ƃ��ł�����̂𓾂�B
     * 
     * @param type �^���B 
     * @param name �������郁�\�b�h���B
     * @param args �����z��B
     *             null ���܂߂Ă��ǂ����Anull ���g�p�����ꍇ��
     *             Object �̃T�u�N���X�ł���ΑS�ă}�b�`���Ă��܂����߁A
     *             �ړI�̃��\�b�h�ȊO�̂��̂�������\��������B
     * @param all  public �̃��\�b�h�݂̂���������Ȃ� false�B
     *             public, protected, private, �p�b�P�[�W�v���C�x�[�g��
     *             �S�Ẵ��\�b�h����������Ȃ� true�B
     * 
     * @return args �������Ɏ�邱�Ƃ��ł��� name�Ƃ������O�� ���\�b�h�B
     *         ������Ȃ���� null�B
     */
    public static Method getMatchFullInstanceMethod( Class    type,
                                                     String   name,
                                                     Object[] args,
                                                     boolean  all ){
        Method[] methods = all 
                         ? type.getDeclaredMethods()
                         : type.getMethods();

        for( int i = 0 ; i < methods.length ; i++ )
            if( methods[i].getName().equals( name )
             && !Modifier.isStatic( methods[i].getModifiers() )
             && Type.matchFullAll( methods[i].getParameterTypes(), args ) )
                return methods[i];
        
        return null;        
    }


    //------------------------------------------------------------------
	//	shared method
    //------------------------------------------------------------------
    //  get static method
    //------------------------------------------------------------------
    //  public static Method getStaticMethod( String classname, 
    //                                        String name, Object[] args )
    //  public static Method getStaticMethod( Class  type,
    //                                        String name, Object[] args )
    //  public static Method getStaticMethod( String classname, String  name, 
    //                                        Object[] args,    boolean all )
    //  public static Method getStaticMethod( Class  type,      String name, 
    //                                        Object[] args,    boolean all )
    //------------------------------------------------------------------
    /**
     * classname �Ŏ������N���X�� public static ���\�b�h�̂����A
     * name �Ƃ������O�� args �� Type.parse ������
     * �󂯓���邱�Ƃ��ł�����̂𓾂�B
     * 
     * @param classname �N���X���B
     * @param name      �������郁�\�b�h���B
     * @param args      �����z��B
     *                  null ���܂߂Ă��ǂ����Anull ���g�p�����ꍇ��
     *                  Object �̃T�u�N���X�ł���ΑS�ă}�b�`���Ă��܂����߁A
     *                  �ړI�̃��\�b�h�ȊO�̂��̂�������\��������B
     * 
     * @return args �������Ɏ�邱�Ƃ��ł��� name�Ƃ������O�� ���\�b�h�B
     *         ������Ȃ���� null�B
     * 
     * @exception ClassNotFoundException
     *                 classname �Ŏ������N���X�����݂��Ȃ��ꍇ
     */
    public static Method getStaticMethod( String   classname,
                                          String   name,
                                          Object[] args ) 
                                                throws  ClassNotFoundException {
        return MethodUtil.getStaticMethod( Class.forName( classname ),
                                           name,
                                           args,
                                           false );
    }

    /**
     * type �� public static�ȃ��\�b�h�̂����A
     * name �Ƃ������O�� args �� Type.parse ������
     * �󂯓���邱�Ƃ��ł�����̂𓾂�B
     * 
     * @param type �^���B
     * @param name �������郁�\�b�h���B
     * @param args �����z��B
     *             null ���܂߂Ă��ǂ����Anull ���g�p�����ꍇ��
     *             Object �̃T�u�N���X�ł���ΑS�ă}�b�`���Ă��܂����߁A
     *             �ړI�̃��\�b�h�ȊO�̂��̂�������\��������B
     * 
     * @return args �������Ɏ�邱�Ƃ��ł��� name�Ƃ������O�� ���\�b�h�B
     *         ������Ȃ���� null�B
     */
    public static Method getStaticMethod( Class    type,
                                          String   name,
                                          Object[] args ){
        return MethodUtil.getStaticMethod( type, name, args, false );
    }

    /**
     * classname �Ŏ������N���X�� static ���\�b�h�̂����A
     * name �Ƃ������O�� args �� Type.parse ������
     * �󂯓���邱�Ƃ��ł�����̂𓾂�B
     * 
     * @param classname �N���X���B
     * @param name      �������郁�\�b�h���B
     * @param args      �����z��B
     *                  null ���܂߂Ă��ǂ����Anull ���g�p�����ꍇ��
     *                  Object �̃T�u�N���X�ł���ΑS�ă}�b�`���Ă��܂����߁A
     *                  �ړI�̃��\�b�h�ȊO�̂��̂�������\��������B
     * @param all       public �̃��\�b�h�݂̂���������Ȃ� false�B
     *                  public, protected, private, �p�b�P�[�W�v���C�x�[�g��
     *                  �S�Ẵ��\�b�h����������Ȃ� true�B
     * 
     * @return args �������Ɏ�邱�Ƃ��ł��� name�Ƃ������O�� ���\�b�h�B
     *         ������Ȃ���� null�B
     * 
     * @exception ClassNotFoundException
     *                 classname �Ŏ������N���X�����݂��Ȃ��ꍇ
     */
    public static Method getStaticMethod( String   classname,
                                          String   name,
                                          Object[] args,
                                          boolean  all ) 
                                                throws  ClassNotFoundException {
        return MethodUtil.getStaticMethod( Class.forName( classname ),
                                           name,
                                           args,
                                           all );
    }

    /**
     * type �� static ���\�b�h�̂����Aname �Ƃ������O�� args ��
     * Type.parse ������ �󂯓���邱�Ƃ��ł�����̂𓾂�B
     * 
     * @param type �^���B 
     * @param name �������郁�\�b�h���B
     * @param args �����z��B
     *             null ���܂߂Ă��ǂ����Anull ���g�p�����ꍇ��
     *             Object �̃T�u�N���X�ł���ΑS�ă}�b�`���Ă��܂����߁A
     *             �ړI�̃��\�b�h�ȊO�̂��̂�������\��������B
     * @param all  public �̃��\�b�h�݂̂���������Ȃ� false�B
     *             public, protected, private, �p�b�P�[�W�v���C�x�[�g��
     *             �S�Ẵ��\�b�h����������Ȃ� true�B
     * 
     * @return args �������Ɏ�邱�Ƃ��ł��� name�Ƃ������O�� ���\�b�h�B
     *         ������Ȃ���� null�B
     */
    public static Method getStaticMethod( Class    type,
                                          String   name,
                                          Object[] args,
                                          boolean  all ){
        Method[] methods = all 
                         ? type.getDeclaredMethods()
                         : type.getMethods();

        for( int i = 0 ; i < methods.length ; i++ )
            if( methods[i].getName().equals( name )
             && Modifier.isStatic( methods[i].getModifiers() )
             && Type.matchFullAll( methods[i].getParameterTypes(), args ) )
                return methods[i];

        for( int i = 0 ; i < methods.length ; i++ )
            if( methods[i].getName().equals( name )
             && Modifier.isStatic( methods[i].getModifiers() )
             && Type.matchRestrictAll( methods[i].getParameterTypes(), args ) )
                return methods[i];

        for( int i = 0 ; i < methods.length ; i++ )
            if( methods[i].getName().equals( name )
             && Modifier.isStatic( methods[i].getModifiers() )
             && Type.matchAll( methods[i].getParameterTypes(), args ) )
                return methods[i];
        
        return null;
    }


    //------------------------------------------------------------------
	//	shared method
    //------------------------------------------------------------------
    //  get match full static method
    //------------------------------------------------------------------
    //  public static Method getMatchFullStaticMethod( String classname, 
    //                                    String name, Object[] args )
    //  public static Method getMatchFullStaticMethod( Class  type,
    //                                    String name, Object[] args )
    //  public static Method getMatchFullStaticMethod( String classname, 
    //                String  name,  Object[] args,    boolean all )
    //  public static Method getMatchFullStaticMethod( Class  type,
    //                String name,   Object[] args,    boolean all )
    //------------------------------------------------------------------
    /**
     * classname �Ŏ������N���X�� public static ���\�b�h�̂����A
     * name �Ƃ������O�� args �� ���ڎ󂯓���邱�Ƃ��ł�����̂𓾂�B
     * 
     * @param classname �N���X���B
     * @param name      �������郁�\�b�h���B
     * @param args      �����z��B
     *                  null ���܂߂Ă��ǂ����Anull ���g�p�����ꍇ��
     *                  Object �̃T�u�N���X�ł���ΑS�ă}�b�`���Ă��܂����߁A
     *                  �ړI�̃��\�b�h�ȊO�̂��̂�������\��������B
     * 
     * @return args �������Ɏ�邱�Ƃ��ł��� name�Ƃ������O�� ���\�b�h�B
     *         ������Ȃ���� null�B
     * 
     * @exception ClassNotFoundException
     *                 classname �Ŏ������N���X�����݂��Ȃ��ꍇ
     */
    public static Method getMatchFullStaticMethod( String   classname,
                                                   String   name,
                                                   Object[] args ) 
                                                throws  ClassNotFoundException {
        return MethodUtil.getMatchFullStaticMethod( Class.forName( classname ),
                                                    name,
                                                    args,
                                                    false );
    }

    /**
     * type �� public static�ȃ��\�b�h�̂����A
     * name �Ƃ������O�� args �𒼐ڎ󂯓���邱�Ƃ��ł�����̂𓾂�B
     * 
     * @param type �^���B
     * @param name �������郁�\�b�h���B
     * @param args �����z��B
     *             null ���܂߂Ă��ǂ����Anull ���g�p�����ꍇ��
     *             Object �̃T�u�N���X�ł���ΑS�ă}�b�`���Ă��܂����߁A
     *             �ړI�̃��\�b�h�ȊO�̂��̂�������\��������B
     * 
     * @return args �������Ɏ�邱�Ƃ��ł��� name�Ƃ������O�� ���\�b�h�B
     *         ������Ȃ���� null�B
     */
    public static Method getMatchFullStaticMethod( Class    type,
                                                   String   name,
                                                   Object[] args ){
        return MethodUtil.getMatchFullStaticMethod( type, name, args, false );
    }

    /**
     * classname �Ŏ������N���X�� static ���\�b�h�̂����A
     * name �Ƃ������O�� args �𒼐ڎ󂯓���邱�Ƃ��ł�����̂𓾂�B
     * 
     * @param classname �N���X���B
     * @param name      �������郁�\�b�h���B
     * @param args      �����z��B
     *                  null ���܂߂Ă��ǂ����Anull ���g�p�����ꍇ��
     *                  Object �̃T�u�N���X�ł���ΑS�ă}�b�`���Ă��܂����߁A
     *                  �ړI�̃��\�b�h�ȊO�̂��̂�������\��������B
     * @param all       public �̃��\�b�h�݂̂���������Ȃ� false�B
     *                  public, protected, private, �p�b�P�[�W�v���C�x�[�g��
     *                  �S�Ẵ��\�b�h����������Ȃ� true�B
     * 
     * @return args �������Ɏ�邱�Ƃ��ł��� name�Ƃ������O�� ���\�b�h�B
     *         ������Ȃ���� null�B
     * 
     * @exception ClassNotFoundException
     *                 classname �Ŏ������N���X�����݂��Ȃ��ꍇ
     */
    public static Method getMatchFullStaticMethod( String   classname,
                                                   String   name,
                                                   Object[] args,
                                                   boolean  all ) 
                                                throws  ClassNotFoundException {
        return MethodUtil.getMatchFullStaticMethod( Class.forName( classname ),
                                                    name,
                                                    args,
                                                    all );
    }


    /**
     * type �� static ���\�b�h�̂����Aname �Ƃ������O�� 
     * args �𒼐ڎ󂯓���邱�Ƃ��ł�����̂𓾂�B
     * 
     * @param type �^���B 
     * @param name �������郁�\�b�h���B
     * @param args �����z��B
     *             null ���܂߂Ă��ǂ����Anull ���g�p�����ꍇ��
     *             Object �̃T�u�N���X�ł���ΑS�ă}�b�`���Ă��܂����߁A
     *             �ړI�̃��\�b�h�ȊO�̂��̂�������\��������B
     * @param all  public �̃��\�b�h�݂̂���������Ȃ� false�B
     *             public, protected, private, �p�b�P�[�W�v���C�x�[�g��
     *             �S�Ẵ��\�b�h����������Ȃ� true�B
     * 
     * @return args �������Ɏ�邱�Ƃ��ł��� name�Ƃ������O�� ���\�b�h�B
     *         ������Ȃ���� null�B
     */
    public static Method getMatchFullStaticMethod( Class    type,
                                                   String   name,
                                                   Object[] args,
                                                   boolean  all ){
        Method[] methods = all 
                         ? type.getDeclaredMethods()
                         : type.getMethods();

        for( int i = 0 ; i < methods.length ; i++ )
            if( methods[i].getName().equals( name )
             && Modifier.isStatic( methods[i].getModifiers() )
             && Type.matchFullAll( methods[i].getParameterTypes(), args ) )
                return methods[i];
        
        return null;        
    }

}
//end of Method.java
