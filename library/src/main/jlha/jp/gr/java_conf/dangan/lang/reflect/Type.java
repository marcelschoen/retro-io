//start of Type.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * Type.java
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

import java.math.BigInteger;

//import exceptions

/**
 * Reflection �̋@�\�������₷������悤��
 * �^�����������[�e�B���e�B�N���X�B
 * 
 * <pre>
 * -- revision history --
 * $Log: Type.java,v $
 * Revision 1.0  2002/10/01 00:00:00  dangan
 * first edition
 * add to version control
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.0 $
 */
public class Type{


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private Type()
    //------------------------------------------------------------------
    /**
     * �f�t�H���g�R���X�g���N�^�B
     * �g�p�s�B
     */
    private Type(){  }


    //------------------------------------------------------------------
    //  shared method
    //------------------------------------------------------------------
    //  utility methods for type matching
    //------------------------------------------------------------------
    //  public static boolean matchFullAll( Class[] types, Object[] args )
    //  public static boolean matchRestrictAll( Class[] types, Object[] args )
    //  public static boolean matchAll( Class[] types, Object[] args )
    //------------------------------------------------------------------
    /**
     * args ���ϊ������� types �ƈ�v���邩�𓾂�B
     * 
     * @param types �^���z��
     * @param args  ����Ώۂ̃I�u�W�F�N�g�z��
     * 
     * @return args �� types �Ɉ�v����� true�B<br>
     *         �Ⴆ�� flase�B
     */
    public static boolean matchFullAll( Class[] types, Object[] args ){
        boolean match = ( types.length == args.length );

        for( int i = 0 ; i < types.length ; i++ )
            match = match && Type.matchFull( types[i], args[i] );

        return match;
    }

    /**
     * args �� Type.parse �ɂ��ϊ��𔺂���
     * types �ƈ�v���邩�𓾂�B
     * matchAll() ��茵���ɔ��肷��B
     * 
     * @param types �^���z��
     * @param args  ����Ώۂ̃I�u�W�F�N�g�z��
     * 
     * @return args �� types �Ɉ�v����� true�B<br>
     *         �Ⴆ�� flase�B
     */
    public static boolean matchRestrictAll( Class[] types, Object[] args ){
        boolean match = ( types.length == args.length );

        for( int i = 0 ; i < types.length ; i++ )
            match = match && Type.matchRestrict( types[i], args[i] );

        return match;
    }

    /**
     * args �� Type.parse �ɂ��ϊ��𔺂���
     * types �ƈ�v���邩�𓾂�B
     * 
     * @param types �^���z��
     * @param args  ����Ώۂ̃I�u�W�F�N�g�z��
     * 
     * @return args �� types �Ɉ�v����� true�B<br>
     *         �Ⴆ�� flase�B
     */
    public static boolean matchAll( Class[] types, Object[] args ){
        boolean match = ( types.length == args.length );

        for( int i = 0 ; i < types.length ; i++ )
            match = match && Type.match( types[i], args[i] );

        return match;
    }


    //------------------------------------------------------------------
    //  shared method
    //------------------------------------------------------------------
    //  type matching
    //------------------------------------------------------------------
    //  public static boolean matchFull( Class type, Object obj )
    //  public static boolean matchRestrict( Class type, Object obj )
    //  public static boolean match( Class type, Object obj )
    //------------------------------------------------------------------
    /**
     * obj ���ϊ������� type �ƈ�v���邩�𓾂�B
     * 
     * @param type �^���
     * @param obj  ����Ώۂ̃I�u�W�F�N�g
     * 
     * @return obj �� type �̎��̂ł���� true�B<br>
     *         �Ⴆ�� false�B
     */
    public static boolean matchFull( Class type, Object obj ){
        if( type.isInstance( obj ) ){
            return true;
        }else if( !type.isPrimitive() && obj == null ){
            return true;
        }else if( type.equals( Boolean.TYPE ) && obj instanceof Boolean ){
            return true;
        }else if( type.equals( Byte.TYPE ) && obj instanceof Byte ){
            return true;
        }else if( type.equals( Short.TYPE ) && obj instanceof Short ){
            return true;
        }else if( type.equals( Character.TYPE ) && obj instanceof Character ){
            return true;
        }else if( type.equals( Integer.TYPE ) && obj instanceof Integer ){
            return true;
        }else if( type.equals( Long.TYPE ) && obj instanceof Long ){
            return true;
        }else if( type.equals( Float.TYPE ) && obj instanceof Float ){
            return true;
        }else if( type.equals( Double.TYPE ) && obj instanceof Double ){
            return true;
        }else{
            return false;
        }
    }

    /**
     * obj �� type �̎��̂ł��邩�𓾂�B
     * type �����l�������v���~�e�B�u�^
     * ( byte, short, int, long, float, double �̂����ꂩ )��
     * �ł���A���� obj �������̃v���~�e�B�u�̃��b�p�^�A
     * ( Byte, Short, Integer, Long, Float, Double �̂����ꂩ )
     * �̃C���X�^���X�ł���ꍇ �ϊ��\�Ɣ��f���� true ��Ԃ��B
     * 
     * @param type �^���
     * @param obj  ����Ώۂ̃I�u�W�F�N�g
     * 
     * @return obj �� type �̎��̂ł���� true�B<br>
     *         �Ⴆ�� false�B
     */
    public static boolean matchRestrict( Class type, Object obj ){

        if( Type.matchFull( type, obj ) ){
            return true;
        }else if( ( type.equals( Byte.TYPE ) || type.equals( Short.TYPE ) 
                 || type.equals( Integer.TYPE ) || type.equals( Long.TYPE ) 
                 || type.equals( Float.TYPE )  || type.equals( Double.TYPE ) )
               && ( obj instanceof Byte || obj instanceof Short
                 || obj instanceof Integer || obj instanceof Long
                 || obj instanceof Float || obj instanceof Double ) ){
            return true;
        }else{
            return false;
        }
    }

    /**
     * obj �� type �̎��̂ł��邩�𓾂�B
     * obj �� Type.parse( type, obj ) �ŕϊ��\�ȏꍇ
     * true��Ԃ��B
     * 
     * @param type �^���
     * @param obj  ����Ώۂ̃I�u�W�F�N�g
     * 
     * @return obj �� type �̎��̂ł���� true�B<br>
     *         �Ⴆ�� false�B
     */
    public static boolean match( Class type, Object obj ){
        final String str = ( obj == null ? null : obj.toString() );

        if( Type.matchRestrict( type, obj ) ){
            return true;
        }else if( type.equals( String.class ) ){
            return true;
        }else if( !type.isPrimitive() && "NULL".equalsIgnoreCase( str ) ){
            return true;
        }else if( ( type.equals( Byte.class ) || type.equals( Byte.TYPE )
                 || type.equals( Short.class ) || type.equals( Short.TYPE )
                 || type.equals( Integer.class ) || type.equals( Integer.TYPE )
                 || type.equals( Long.class ) || type.equals( Long.TYPE )
                 || type.equals( Float.class ) || type.equals( Float.TYPE )
                 || type.equals( Double.class ) || type.equals( Double.TYPE ) )
               && ( obj instanceof Number
                 || ( obj != null && Type.isLongString( obj.toString() ) )
                 || ( obj != null && Type.isDoubleString( obj.toString() ) ) ) ){
            return true;
        }else if( ( type.equals( Boolean.TYPE ) || type.equals( Boolean.class ) )
               && ( "TRUE".equalsIgnoreCase( str ) || "FALSE".equalsIgnoreCase( str ) ) ){
            return true;
        }else if( ( type.equals( Character.class ) || type.equals( Character.TYPE ) )
               && obj instanceof String 
               && ( str.length() == 1 || Type.isUnicodeEscape( str ) ) ){
            return true;
        }else{
            return false;
        }
    }


    //------------------------------------------------------------------
    //  shared method
    //------------------------------------------------------------------
    //  parse
    //------------------------------------------------------------------
    //  public static Object[] parseAll( Class[] types, Object[] args )
    //  public static Object parse( Class type, Object obj )
    //------------------------------------------------------------------
    /**
     * Factory.matchAll( types, args ) �Ń}�b�`���� args ��
     * �ꊇ���� types �Ŏ������^�ɕϊ�����B
     * 
     * @param types �ϊ�����^���z��
     * @param args  �ϊ��Ώۂ̃I�u�W�F�N�g�z��
     * 
     * @return �ϊ���̃I�u�W�F�N�g�z��
     * 
     * @exception IllegalAccessError
     *             args �� types �ɕϊ��s�\�ȏꍇ�B
     */
    public static Object[] parseAll( Class[] types, Object[] args ){
        if( types.length == args.length ){
            Object[] objs = new Object[ args.length ];

            for( int i = 0 ; i < args.length ; i++ )
                objs[i] = Type.parse( types[i], args[i] );

            return objs;
        }else{
            throw new IllegalArgumentException();
        }
    }

    /**
     * Factory.match( type, obj ) �Ń}�b�`���� obj ��
     * type �Ŏ������^�ɕϊ�����B
     * 
     * @param type �ϊ�����^���
     * @param obj  �ϊ��Ώۂ̃I�u�W�F�N�g
     * 
     * @return �ϊ���̃I�u�W�F�N�g
     * 
     * @exception IllegalArgumentException
     *             Factory.match( type, obj ) �Ń}�b�`���Ă��Ȃ� obj ��
     *             �ϊ����悤�Ƃ����ꍇ�B
     */
    public static Object parse( Class type, Object obj ){
        final String str = ( obj == null ? null : obj.toString() );

        if( type.isInstance( obj ) ){
            return obj;
        }else if( !type.isPrimitive() 
               && !type.equals( String.class ) 
               && ( obj == null || "NULL".equalsIgnoreCase( str ) ) ){
            return null;
        }else if( type.equals( String.class ) ){
            return str;
        }else if( ( type.equals( Byte.class ) || type.equals( Byte.TYPE )
                 || type.equals( Short.class ) || type.equals( Short.TYPE )
                 || type.equals( Integer.class ) || type.equals( Integer.TYPE )
                 || type.equals( Long.class ) || type.equals( Long.TYPE )
                 || type.equals( Float.class ) || type.equals( Float.TYPE )
                 || type.equals( Double.class ) || type.equals( Double.TYPE ) )
               && ( obj instanceof Number
                 || ( obj != null && Type.isLongString( str ) )
                 || ( obj != null && Type.isDoubleString( str ) ) ) ){
            Number num = null;
            if( obj instanceof Number ){
                num = (Number)obj;
            }else{
                try{
                    if( Type.isLongString( str ) )
                        num = new Long( Long.parseLong( str ) );
                    else
                        num = new Double( str );
                }catch( NumberFormatException exception ){
                    num = Type.parseHexadecimal( str.substring( 2 ) );
                }
            }
            
            if( type.equals( Byte.class ) || type.equals( Byte.TYPE ) ){
                return new Byte( num.byteValue() );
            }else if( type.equals( Short.class ) || type.equals( Short.TYPE ) ){
                return new Short( num.shortValue() );
            }else if( type.equals( Integer.class ) || type.equals( Integer.TYPE ) ){
                return new Integer( num.intValue() );
            }else if( type.equals( Long.class ) || type.equals( Long.TYPE ) ){
                return new Long( num.longValue() );
            }else if( type.equals( Float.class ) || type.equals( Float.TYPE ) ){
                return new Float( num.floatValue() );
            }else{
                return new Double( num.doubleValue() );
            }    
        }else if( type.equals( Boolean.class )
               || type.equals( Boolean.TYPE ) ){
            if( "TRUE".equalsIgnoreCase( str ) ){
                return new Boolean( true );
            }else if( "FALSE".equalsIgnoreCase( str ) ){
                return new Boolean( false );
            }
        }else if( ( type.equals( Character.class )
                 || type.equals( Character.TYPE ) )
               && obj != null ){
            if( str.length() == 1 ){
                return new Character( str.charAt( 0 ) );
            }else if( Type.isUnicodeEscape( str ) ){
                return new Character( (char)Type.parseHexadecimal( str.substring( 2 ) ).intValue() );
            }
        }
        throw new IllegalArgumentException();
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  chack that string match the pattern of type.
    //------------------------------------------------------------------
    //  private static boolean isUnicodeEscape( String str )
    //  private static boolean isLongString( String str )
    //  private static boolean isDoubleString( String str )
    //------------------------------------------------------------------
    /**
     * str �� ���j�R�[�h�G�X�P�[�v���ꂽ1�����ł��邩�𓾂�B
     * 
     * @param str ������
     * 
     * @return str �����j�R�[�h�G�X�P�[�v���ꂽ1�����ł���ꍇ
     */
    private static boolean isUnicodeEscape( String str ){
        if( str.length() == 6
         && str.startsWith( "\\u" )
         && Type.isHexadecimal( str.substring( 2 ) ) ){
            return true;
         }else{
            return false;
         }
    }

    /**
     * str ���m���� Integer ������������ł��邩�𓾂�B
     * 
     * @param str ������
     * 
     * @return str ���m���� Integer ������������Ȃ� true�B
     *         �Ⴆ�� false�B
     */
    private static boolean isLongString( String str ){
        try{
            Long.parseLong( str );
            return true;
        }catch( NumberFormatException exception ){
        }

        if( str.startsWith( "0x" ) && Type.isHexadecimal( str.substring( 2 ) ) ){
            BigInteger val = Type.parseHexadecimal( str.substring( 2 ) );
            final BigInteger zero  = new BigInteger( "0" );
            final BigInteger limit = new BigInteger( "FFFFFFFFFFFFFFFF", 16 );

            if( zero.compareTo( val ) <= 0 && val.compareTo( limit ) <= 0  ) 
                return true;
            else
                return false;
        }else{
            return false;
        }
    }

    /**
     * str ���m���� Double ������������ł��邩�𓾂�B
     * 
     * @param str ������
     * 
     * @return str ���m���� Integer ������������Ȃ� true�B
     *         �Ⴆ�� false�B
     */
    private static boolean isDoubleString( String str ){
        try{
            Double  num = Double.valueOf( str );

            if( !num.isInfinite()
             || str.equals( "Infinity" )
             || str.equals( "-Infinity" ) )
                return true;
            else
                return false;
        }catch( NumberFormatException exception ){
        }

        return false;
    }

    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  processing hexadecimal
    //------------------------------------------------------------------
    //  private static boolean isHexadecimal( String str )
    //  private static long perseHexadecimal( String str )
    //------------------------------------------------------------------
    /**
     * ������ 16�i�̕����񂩂𔻒肷��B
     * 
     * @param str ����Ώۂ̕�����
     * 
     * @return str ��16�i�̕�����ł���� true�B
     *         �Ⴆ�� false�B
     */
    private static boolean isHexadecimal( String str ){
        final String hexadecimal  = "0123456789ABCDEF";
        str = str.toUpperCase();

        if( 0 < str.length() ){
            for( int i = 0 ; i < str.length() ; i++ )
                if( hexadecimal.indexOf( str.charAt( i ) ) < 0 )
                    return false;

            return true;
        }else{
            return false;
        }
    }

    /**
     * ������� 16�i�̕�����Ƃ��ĉ��߂��A�l�𓾂�B
     * 
     * @param str ������
     * 
     * @return str ��16�i���Ƃ��ĉ��߂����l�B
     *         str ��16�i���łȂ��ꍇ�̌��ʂ͕s��B
     */
    private static BigInteger parseHexadecimal( String str ){
        return new BigInteger( str, 16 );
    }

}
//end of Type.java
