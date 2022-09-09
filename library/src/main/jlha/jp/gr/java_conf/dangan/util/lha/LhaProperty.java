//start of LhaProperty.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * LhaProperty.java
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

import jp.gr.java_conf.dangan.lang.reflect.Factory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

//import exceptions


/**
 * LHA Library for Java �̊e��ݒ�������B<br>
 * LhaProperty.getProperty() �� LhaProperty.getProperties() �œ�����l��
 * �V�X�e���v���p�e�B�A�ݒ�t�@�C���A�f�t�H���g�l�̉��ꂩ���p�����A
 * ���̗D�揇�ʂ͈ȉ��̂悤�ɂȂ�B
 *  <ol>
 *    <li>�V�X�e���v���p�e�B �ɐݒ肳��Ă���l�B
 *    <li>jp/gr/java_conf/dangan/util/lha/resources/lha.properties 
 *        �ɐݒ肳�ꂽ�l�B
 *    <li>�f�t�H���g�l�B
 *  </ol>
 * <br>
 * <br>
 * �L�[�̈ꗗ�͈ȉ��̂Ƃ���B
 * <br>
 *  <table border="0" cellspacing="4">
 *    <tr>
 *      <td nowrap>�L�[</td>
 *      <td nowrap>�Ή�����l�̐���</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.encoding</td>
 *      <td nowrap>String �ƃw�b�_���̕�����Ƃ̑��ݕϊ��ɗp����G���R�[�f�B���O</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.packages</td>
 *      <td nowrap>���������Ŏg����N���X�̃p�b�P�[�W���̗�(�J���}��؂�)</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lzs.encoder</td>
 *      <td nowrap>-lzs- �`���ւ̕��������s���I�u�W�F�N�g�̐�����</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lz4.encoder</td>
 *      <td nowrap>-lz4- �`���ւ̕��������s���I�u�W�F�N�g�̐�����</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lz5.encoder</td>
 *      <td nowrap>-lz5- �`���ւ̕��������s���I�u�W�F�N�g�̐�����</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lh0.encoder</td>
 *      <td nowrap>-lh0- �`���ւ̕��������s���I�u�W�F�N�g�̐�����</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lh1.encoder</td>
 *      <td nowrap>-lh1- �`���ւ̕��������s���I�u�W�F�N�g�̐�����</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lh2.encoder</td>
 *      <td nowrap>-lh2- �`���ւ̕��������s���I�u�W�F�N�g�̐�����</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lh3.encoder</td>
 *      <td nowrap>-lh3- �`���ւ̕��������s���I�u�W�F�N�g�̐�����</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lh4.encoder</td>
 *      <td nowrap>-lh4- �`���ւ̕��������s���I�u�W�F�N�g�̐�����</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lh5.encoder</td>
 *      <td nowrap>-lh5- �`���ւ̕��������s���I�u�W�F�N�g�̐�����</td>
 *   </tr>
 *    <tr>
 *      <td nowrap>lha.lh6.encoder</td>
 *      <td nowrap>-lh6- �`���ւ̕��������s���I�u�W�F�N�g�̐�����</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lh7.encoder</td>
 *      <td nowrap>-lh7- �`���ւ̕��������s���I�u�W�F�N�g�̐�����</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lhd.encoder</td>
 *      <td nowrap>-lhd- �`���ւ̕��������s���I�u�W�F�N�g�̐�����</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lzs.decoder</td>
 *      <td nowrap>-lzs- �`���̃f�[�^�𕜍�������I�u�W�F�N�g�̐�����</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lz4.decoder</td>
 *      <td nowrap>-lz4- �`���̃f�[�^�𕜍�������I�u�W�F�N�g�̐�����</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lz5.decoder</td>
 *      <td nowrap>-lz5- �`���̃f�[�^�𕜍�������I�u�W�F�N�g�̐�����</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lh0.decoder</td>
 *      <td nowrap>-lh0- �`���̃f�[�^�𕜍�������I�u�W�F�N�g�̐�����</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lh1.decoder</td>
 *      <td nowrap>-lh1- �`���̃f�[�^�𕜍�������I�u�W�F�N�g�̐�����</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lh2.decoder</td>
 *      <td nowrap>-lh2- �`���̃f�[�^�𕜍�������I�u�W�F�N�g�̐�����</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lh3.decoder</td>
 *      <td nowrap>-lh3- �`���̃f�[�^�𕜍�������I�u�W�F�N�g�̐�����</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lh4.decoder</td>
 *      <td nowrap>-lh4- �`���̃f�[�^�𕜍�������I�u�W�F�N�g�̐�����</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lh5.decoder</td>
 *      <td nowrap>-lh5- �`���̃f�[�^�𕜍�������I�u�W�F�N�g�̐�����</td>
 *   </tr>
 *    <tr>
 *      <td nowrap>lha.lh6.decoder</td>
 *      <td nowrap>-lh6- �`���̃f�[�^�𕜍�������I�u�W�F�N�g�̐�����</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lh7.decoder</td>
 *      <td nowrap>-lh7- �`���̃f�[�^�𕜍�������I�u�W�F�N�g�̐�����</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lhd.decoder</td>
 *      <td nowrap>-lhd- �`���̃f�[�^�𕜍�������I�u�W�F�N�g�̐�����</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.header</td>
 *      <td nowrap>LhaHeader �̃C���X�^���X�̐�����</td>
 *    </tr>
 *  </table>
 * <br>
 * �������͈ȉ��̂悤�ɒ�`�����B<br>
 *  <table border="0" cellspacing="4">
 *    <tr>
 *      <td nowrap>&lt;������&gt;</td>
 *      <td nowrap>::= &lt;�R���X�g���N�^&gt; | &lt;�z��&gt; | &lt;�u��������&gt; | &lt;�N���X��&gt; | &lt;������&gt;</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>&lt;�R���X�g���N�^&gt;</td>
 *      <td nowrap>::= &lt;�N���X��&gt; '(' ���� ')'</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>&lt;����&gt;</td>
 *      <td nowrap>::= [ &lt;������&gt; [ ',' &lt;����&gt; ] ]</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>&lt;�z��&gt;</td>
 *      <td nowrap>::= '[' &lt;�v�f&gt; ']'</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>&lt;�v�f&gt;</td>
 *      <td nowrap>::= [ &lt;������&gt; [ ',' &lt;�v�f&gt; ] ]</td>
 *    </tr>
 *  </table>
 * <br>
 * �N���X���� "lha.packages" �ɑΉ�����l���g�p���Ċ��S�C�����ւƕϊ������B<br>
 * �u�������� �̓��C�u���������ŃI�u�W�F�N�g�ɒu������镶�����
 * ���݈ȉ���4��ނ���`����Ă���B<br>
 *  <table border="0" cellspacing="4">
 *    <tr>
 *      <td nowrap>lha.???.encoder</td>
 *      <td nowrap>out</td>
 *      <td nowrap>���k��̃f�[�^���󂯎�� java.io.OutputStream</td>
 *    </tr>
 *    <tr>
 *      <td nowrap rowspan="2">lha.???.decoder</td>
 *      <td nowrap>in</td>
 *      <td nowrap>���k�f�[�^���������� java.io.InputStream</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>length</td>
 *      <td nowrap>���������ꂽ�f�[�^�̃o�C�g��</td>
 *    </tr>
 *    <tr>
 *      <td nowrap rowspan="2">lha.header</td>
 *      <td nowrap>data</td>
 *      <td nowrap>�w�b�_�f�[�^���i�[���� byte�z��</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>encoding</td>
 *      <td nowrap>�w�b�_���̕����f�[�^�� String �ɕϊ�����ۂɎg�p����G���R�[�f�B���O</td>
 *    </tr>
 *  </table>
 * <br>
 * <pre>
 * -- revision history --
 * $Log: LhaProperty.java,v $
 * Revision 1.0.2.2  2005/04/29 02:15:53  dangan
 * [bug fix]
 *     createDefaultProperty() �ň��k�@���ʎq -lhd- �p�̃G���R�[�_�ǃf�R�[�_���ݒ肳��Ă��Ȃ������B
 *
 * Revision 1.0.2.1  2004/06/27 12:09:49  dangan
 * [bugfix]
 *     �������ŃJ���}���g���ׂ������Ńs���I�h���g���Ă����̂��C���B
 *
 * Revision 1.0  2002/12/05 00:00:00  dangan
 * first edition
 * add to version control
 *
 * </pre>
 *
 * @author $Author: dangan $
 * @version $Revision: 1.0.2.2 $
 */
public class LhaProperty {


    //------------------------------------------------------------------
    //  class field
    //------------------------------------------------------------------
    //  private static final Properties property
    //  public static final String encoding
    //------------------------------------------------------------------
    /**
     * LHA Library for Java �̐ݒ��ێ�����v���p�e�B
     */
    private static final Properties property = LhaProperty.createLhaProperty();

    /**
     * LHA Library for Java �� ��
     * �f�t�H���g�Ŏg�p�����G���R�[�f�B���O
     */
    public static final String encoding = LhaProperty.property.getProperty("lha.encoding");


    //------------------------------------------------------------------
    //  constructpr
    //------------------------------------------------------------------
    //  private LhaProperty()
    //------------------------------------------------------------------

    /**
     * �f�t�H���g�R���X�g���N�^�g�p�s��
     */
    private LhaProperty() {
    }


    //------------------------------------------------------------------
    //  access method
    //------------------------------------------------------------------
    //  public static String getProperty( String key )
    //  public static Properties getProperties()
    //------------------------------------------------------------------

    /**
     * LHA Library for Java �̃v���p�e�B���� 
     * key �ɑΉ�������̂��擾����B<br>
     *
     * @param key �v���p�e�B�̃L�[
     *
     * @return �u���p�e�B�̕�����
     */
    public static String getProperty(String key) {
        String def = LhaProperty.property.getProperty(key);
        try {
            if (key.equals("lha.encoding")
                    && System.getProperty(key, def).equals("ShiftJISAuto")) {
                try {
                    String encoding = System.getProperty("file.encoding");
                    if (LhaProperty.isCategoryOfShiftJIS(encoding)) {
                        return encoding;
                    } else {
                        return "SJIS";
                    }
                } catch (SecurityException exception) {
                    return "SJIS";
                }
            } else {
                return System.getProperty(key, def);
            }
        } catch (SecurityException exception) {
        }

        return def;
    }

    /**
     * LHA Library for Java �̃v���p�e�B�̃R�s�[�𓾂�B<br>
     *
     * @return �v���p�e�B�̃R�s�[
     */
    public static Properties getProperties() {
        Properties property = (Properties) LhaProperty.property.clone();
        Enumeration enumkey = property.propertyNames();

        while (enumkey.hasMoreElements()) {
            String key = (String) enumkey.nextElement();
            try {
                String val = System.getProperty(key);
                if (null != val) {
                    property.put(key, val);
                }
            } catch (SecurityException exception) {
            }
        }

        if (property.getProperty("lha.encoding").equals("ShiftJISAuto")) {
            try {
                String encoding = System.getProperty("file.encoding");
                if (LhaProperty.isCategoryOfShiftJIS(encoding)) {
                    property.put("lha.encoding", encoding);
                } else {
                    property.put("lha.encoding", "SJIS");
                }
            } catch (SecurityException exception) {
                property.put("lha.encoding", "SJIS");
            }
        }

        return property;
    }


    //------------------------------------------------------------------
    //  parse
    //------------------------------------------------------------------
    //  public static Object parse( String source, 
    //                         Hashtable substitute, String packages )
    //  public static Object parse( String source, 
    //                         Hashtable substitute, String[] packages )
    //  private static Object parseConstructor( String source, 
    //                         Hashtable substitute, String[] packages )
    //  private static Object[] parseArray( String source, 
    //                         Hashtable substitute, String[] packages )
    //  private static String applyPackages( String str, String[] packages )
    //------------------------------------------------------------------

    /**
     * LHA Library for Java �̃v���p�e�B�p��
     * ������ source ����͂��� �V���� Object �𐶐�����B
     *
     * @param souce      ��͂��ׂ�������
     * @param substitute �u���Ώە������key�ɂ����A�u������Object��l�Ɏ��� Hashtable
     * @param packages   �J���}�ŋ�؂�ꂽ�p�b�P�[�W���̗�
     *
     * @return �������ꂽ Object
     */
    public static Object parse(String source,
                               Hashtable substitute,
                               String packages) {

        StringTokenizer tokenizer = new StringTokenizer(packages, ",");
        String[] packageArray = new String[tokenizer.countTokens()];
        int i = 0;
        while (tokenizer.hasMoreTokens()) {
            packageArray[i++] = tokenizer.nextToken().trim();
        }

        return LhaProperty.parse(source, substitute, packageArray);
    }

    /**
     * LHA Library for Java �̃v���p�e�B�p��
     * ������ source ����͂��� �V���� Object �𐶐�����B
     *
     * @param souce      ��͂��ׂ�������
     * @param substitute �u���Ώە������key�ɂ����A�u������Object��l�Ɏ��� Hashtable
     * @param packages   �p�b�P�[�W���̔z��
     *
     * @return �������ꂽ Object
     */
    public static Object parse(String source,
                               Hashtable substitute,
                               String[] packages) {

        source = source.trim();
        int casearcpos = source.indexOf("(");
        int bracepos = source.indexOf("[");

        if (0 <= casearcpos && (bracepos < 0 || casearcpos < bracepos)) {
            return LhaProperty.parseConstructor(source, substitute, packages);
        } else if (0 <= bracepos && (casearcpos < 0 || bracepos < casearcpos)) {
            return LhaProperty.parseArray(source, substitute, packages);
        } else if (substitute.containsKey(source)) {
            return substitute.get(source);
        } else {
            return LhaProperty.applyPackages(source, packages);
        }
    }

    /**
     * LHA Library for Java �̃v���p�e�B�p��
     * �R���X�g���N�^������������ source ����͂��āA
     * �V���� �C���X�^���X�𐶐�����B
     *
     * @param souce      ��͂��ׂ��R���X�g���N�^������������
     * @param substitute �u���Ώە������key�ɂ����A�u������Object��l�Ɏ��� Hashtable
     * @param packages   �p�b�P�[�W���̔z��
     *
     * @return �������ꂽ�C���X�^���X
     */
    private static Object parseConstructor(String source,
                                           Hashtable substitute,
                                           String[] packages) {

        String classname = source.substring(0, source.indexOf('(')).trim();
        String arguments = source.substring(source.indexOf('(') + 1,
                source.lastIndexOf(')')).trim();

        classname = LhaProperty.applyPackages(classname, packages);
        Object[] args;
        if (!arguments.equals("")) {
            StringTokenizer tokenizer = new StringTokenizer(arguments, ",()[]", true);
            Stack stack = new Stack();
            int pos = 0;
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (token.equals("(")) {
                    stack.push("(");
                } else if (token.equals(")")) {
                    if (!stack.empty() && stack.peek().equals("(")) {
                        stack.pop();
                    }
                } else if (token.equals("[")) {
                    stack.push("[");
                } else if (token.equals("]")) {
                    if (!stack.empty() && stack.peek().equals("[")) {
                        stack.pop();
                    }
                } else if (token.equals(",")) {
                    if (stack.empty()
                            || (!stack.peek().equals("(")
                            && !stack.peek().equals("["))) {
                        stack.push(new Integer(pos));
                    }
                }
                pos += token.length();
            }

            pos = 0;
            args = new Object[stack.size() + 1];
            for (int i = 0; i < stack.size() + 1; i++) {
                String arg;
                if (i < stack.size()) {
                    arg = arguments.substring(pos, ((Integer) stack.elementAt(i)).intValue());
                } else {
                    arg = arguments.substring(pos);
                }
                pos += arg.length() + 1;
                args[i] = LhaProperty.parse(arg, substitute, packages);
            }

        } else {
            args = new Object[0];
        }

        try {
            return Factory.createInstance(classname, args);
        } catch (InstantiationException exception) {
            throw new InstantiationError(exception.getMessage());
        } catch (InvocationTargetException exception) {
            if (exception.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) exception.getTargetException();
            } else if (exception.getTargetException() instanceof Error) {
                throw (Error) exception.getTargetException();
            } else {
                throw new Error(exception.getTargetException().getMessage());
            }
        } catch (ClassNotFoundException exception) {
            throw new NoClassDefFoundError(exception.getMessage());
        } catch (NoSuchMethodException exception) {
            throw new NoSuchMethodError(exception.getMessage());
        }
    }

    /**
     * LHA Library for Java �̃v���p�e�B�p��
     * �z������������� source ����͂��āA
     * �V���� Object �̔z��𐶐�����B
     *
     * @param souce      ��͂��ׂ��R���X�g���N�^������������
     * @param substitute �u���Ώە������key�ɂ����A�u������Object��l�Ɏ��� Hashtable
     * @param packages   �p�b�P�[�W���̔z��
     *
     * @return �������ꂽ Object �̔z��
     */
    private static Object[] parseArray(String source,
                                       Hashtable substitute,
                                       String[] packages) {

        String arguments = source.substring(source.indexOf('[') + 1,
                source.lastIndexOf(']')).trim();

        if (!arguments.equals("")) {
            StringTokenizer tokenizer = new StringTokenizer(arguments, ",()[]", true);
            Stack stack = new Stack();
            int pos = 0;
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (token.equals("(")) {
                    stack.push("(");
                } else if (token.equals(")")) {
                    if (!stack.empty() && stack.peek().equals("("))
                        stack.pop();
                } else if (token.equals("[")) {
                    stack.push("[");
                } else if (token.equals("]")) {
                    if (!stack.empty() && stack.peek().equals("["))
                        stack.pop();
                } else if (token.equals(",")) {
                    if (stack.empty()
                            || (!stack.peek().equals("(")
                            && !stack.peek().equals("[")))
                        stack.push(new Integer(pos));
                }
                pos += token.length();
            }

            pos = 0;
            Object[] array = new Object[stack.size() + 1];
            for (int i = 0; i < stack.size() + 1; i++) {
                String arg;
                if (i < stack.size()) {
                    arg = arguments.substring(pos, ((Integer) stack.elementAt(i)).intValue());
                } else {
                    arg = arguments.substring(pos);
                }
                pos += arg.length() + 1;
                array[i] = LhaProperty.parse(arg, substitute, packages);
            }
            return array;
        } else {
            return new Object[0];
        }
    }


    /**
     * str ���N���X�����Ɖ��肵�� packages �Ɋ܂܂��p�b�P�[�W����
     * �A�����Ċ��S�C�������쐬���鎖�����݂�B
     *
     * @param str      �N���X����������Ȃ�������
     * @param packages �p�b�P�[�W���̗�
     *
     * @return ���S�C�����A�������� str
     */
    private static String applyPackages(String str, String[] packages) {
        for (int i = 0; i < packages.length; i++) {
            String classname;
            if (packages[i].equals("")) {
                classname = str;
            } else {
                classname = packages[i] + "." + str;
            }
            try {
                Class.forName(classname);
                return classname;
            } catch (ClassNotFoundException exception) {
            } catch (LinkageError error) {
            }
        }
        return str;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  create property
    //------------------------------------------------------------------
    //  private static final Properties createLhaProperty()
    //  private static final Properties createDefaultProperty()
    //------------------------------------------------------------------

    /**
     * LHA Library for Java �̃v���p�e�B�𐶐�����B
     *
     * @return �������ꂽ�v���p�e�B
     */
    private static final Properties createLhaProperty() {
        String path = "jp.gr.java_conf.dangan.util.lha.resources.lha";
        Properties property = LhaProperty.createDefaultProperty();

        try {
            ResourceBundle bundle = ResourceBundle.getBundle(path);
            Enumeration enumkey = bundle.getKeys();
            while (enumkey.hasMoreElements()) {
                String key = (String) enumkey.nextElement();
                property.put(key, bundle.getString(key));
            }
        } catch (MissingResourceException exception) {
        }

        if (property.getProperty("lha.encoding").equals("ShiftJISAuto")) {
            try {
                String encoding = System.getProperty("file.encoding");
                if (LhaProperty.isCategoryOfShiftJIS(encoding)) {
                    property.put("lha.encoding", encoding);
                } else {
                    property.put("lha.encoding", "SJIS");
                }
            } catch (SecurityException exception) {
                property.put("lha.encoding", "SJIS");
            }
        }

        return property;
    }

    /**
     * LHA Library for Java �̃f�t�H���g�̃v���p�e�B�𐶐�����B
     * jp/gr/java_conf/dangan/util/lha/resources/ �ȉ���
     * �ݒ�t�@�C�������������ꍇ�p�B
     *
     * @return �f�t�H���g�̃v���p�e�B
     */
    private static final Properties createDefaultProperty() {
        Properties property = new Properties();

        //------------------------------------------------------------------
        //  encoding of String
        property.put("lha.encoding", LhaProperty.getSystemEncoding());

        //------------------------------------------------------------------
        //  package names
        property.put("lha.packages", "jp.gr.java_conf.dangan.util.lha");

        //------------------------------------------------------------------
        //  encoders
        property.put("lha.lzs.encoder", "LzssOutputStream( PostLzsEncoder( out ), HashAndChainedListSearch, [ HashShort ] )");
        property.put("lha.lz4.encoder", "out");
        property.put("lha.lz5.encoder", "LzssOutputStream( PostLz5Encoder( out ), HashAndChainedListSearch )");
        property.put("lha.lhd.encoder", "out");
        property.put("lha.lh0.encoder", "out");
        property.put("lha.lh1.encoder", "LzssOutputStream( PostLh1Encoder( out ), HashAndChainedListSearch )");
        property.put("lha.lh2.encoder", "LzssOutputStream( PostLh2Encoder( out ), HashAndChainedListSearch )");
        property.put("lha.lh3.encoder", "LzssOutputStream( PostLh3Encoder( out ), HashAndChainedListSearch )");
        property.put("lha.lh4.encoder", "LzssOutputStream( PostLh5Encoder( out, -lh4- ), HashAndChainedListSearch )");
        property.put("lha.lh5.encoder", "LzssOutputStream( PostLh5Encoder( out, -lh5- ), HashAndChainedListSearch )");
        property.put("lha.lh6.encoder", "LzssOutputStream( PostLh5Encoder( out, -lh6- ), HashAndChainedListSearch )");
        property.put("lha.lh7.encoder", "LzssOutputStream( PostLh5Encoder( out, -lh7- ), HashAndChainedListSearch )");

        //------------------------------------------------------------------
        //  decoders
        property.put("lha.lzs.decoder", "LzssInputStream( PreLzsDecoder( in ), length )");
        property.put("lha.lz4.decoder", "in");
        property.put("lha.lz5.decoder", "LzssInputStream( PreLz5Decoder( in ), length )");
        property.put("lha.lhd.decoder", "in");
        property.put("lha.lh0.decoder", "in");
        property.put("lha.lh1.decoder", "LzssInputStream( PreLh1Decoder( in ), length )");
        property.put("lha.lh2.decoder", "LzssInputStream( PreLh2Decoder( in ), length )");
        property.put("lha.lh3.decoder", "LzssInputStream( PreLh3Decoder( in ), length )");
        property.put("lha.lh4.decoder", "LzssInputStream( PreLh5Decoder( in, -lh4- ), length )");
        property.put("lha.lh5.decoder", "LzssInputStream( PreLh5Decoder( in, -lh5- ), length )");
        property.put("lha.lh6.decoder", "LzssInputStream( PreLh5Decoder( in, -lh6- ), length )");
        property.put("lha.lh7.decoder", "LzssInputStream( PreLh5Decoder( in, -lh7- ), length )");

        //------------------------------------------------------------------
        //  header
        property.put("lha.header", "LhaHeader( data, encoding )");

        return property;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  encoding
    //------------------------------------------------------------------
    //  private static final String getSystemEncoding()
    //  private static final boolean isJapanese( String encoding )
    //  private static final boolean isCategoryOfShiftJIS( String encoding )
    //------------------------------------------------------------------

    /**
     * System.getProperty( "file.encoding" ) �œ����� �G���R�[�f�B���O��Ԃ��B
     * ����ꂽ�G���R�[�f�B���O�� ���{��̃G���R�[�f�B���O�ŁA
     * �Ȃ�����ShiftJIS�n��Ŗ����ꍇ�͋����I�� "SJIS" ���g�p����B
     * �Z�L�����e�B�}�l�[�W���� �V�X�e���v���p�e�B�ւ̃A�N�Z�X�������Ȃ��ꍇ�� 
     * "ISO8859_1" ���g�p����B
     *
     * @return System.getProperty(" file.encoding ") �œ����� �G���R�[�f�B���O
     */
    private static final String getSystemEncoding() {
        String encoding;
        try {
            encoding = System.getProperty("file.encoding");
            if (LhaProperty.isJapanese(encoding)
                    && !LhaProperty.isCategoryOfShiftJIS(encoding)) {
                return "SJIS";
            } else {
                return encoding;
            }
        } catch (SecurityException exception) {
            encoding = "ISO8859_1";
        }
        return encoding;
    }

    /**
     * encoding �����{��̃G���R�[�f�B���O�ł��邩��Ԃ��B
     *
     * @param encoding �G���R�[�f�B���O
     *
     * @return encoding �����{��̃G���R�[�f�B���O�Ȃ� true �Ⴆ�� false
     */
    private static final boolean isJapanese(String encoding) {

        String[] Coverters = {"Cp930",     //Japanese EBCDIC
                "Cp939",     //Japanese EBCDIC
                "Cp942",     //SJIS OS/2 ���{��, Cp932 �̃X�[�p�[�Z�b�g, 0x5C -> '�_' (���p�o�b�N�X���b�V��)
                "Cp942C",    //SJIS OS/2 ���{��, Cp932 �̃X�[�p�[�Z�b�g, 0x5C -> '��' (���p�~�L��)
                "Cp943",     //SJIS OS/2 ���{��, Cp942 �̃X�[�p�[�Z�b�g �VJIS�Ή�, 0x5C -> '�_' (���p�o�b�N�X���b�V��)
                "Cp943C",    //SJIS OS/2 ���{��, Cp942 �̃X�[�p�[�Z�b�g �VJIS�Ή�, 0x5C -> '��' (���p�~�L��)
                "Cp33722",   //EUC IBM ���{��,
                "MS932",     //Windows ���{��
                "SJIS",      //Shift-JIS�A���{��
                "EUC_JP",    //EUC, ���{�� JIS X 0201, 0208, 0212
                "ISO2022JP", //JIS X 0201, ISO 2022 �`���� 0208�A���{��
                "JIS0201",   //JIS X 0201, ���{��
                "JIS0208",   //JIS X 0208, ���{��
                "JIS0212",   //JIS X 0212, ���{��
                "JISAutoDetect"}; //Shift-JIS EUC-JP ISO 2022 JP �̌��o����ѕϊ��B�ǂݍ��ݐ�p�B
        for (int i = 0; i < Coverters.length; i++) {
            if (encoding.equals(Coverters[i])) {
                return true;
            }
        }

        String[] Aliases = {"eucjis", "euc-jp", "eucjp", "x-euc-jp", "x-eucjp", //Aliases of "EUC_JP"
                "csEUCPkdFmtJapanese",                              //Alias of "EUCJIS"(?)
                "extended_unix_code_packed_format_for_japanese ",   //Alias of "EUCJIS"(?)
                "shift_jis", "ms_kanji", "csShiftJIS",             //JDK1.1.1 - JDK1.1.7B Alias of "SJIS", JDK1.2 - JDK1.3 Alias of "MS932", JDK1.4 Alias of "SJIS"
                "csWindows31J", "windows-31j",                      //Alias of "MS932"
                "x-sjis",                                           //JDK1.2 Alias of "MS932", JDK1.3 Alias of "SJIS", JDK1.4 Alias of "MS932"
                "jis",                                              //Alias of "ISO2022JP"
                "iso-2022-jp",                                      //JDK1.1.1-JDK1.1.5 Alias of "JIS", JDK1.1.6- Alias of "ISO2022JP"
                "csISO2022JP",                                      //JDK1.1.1-JDK1.1.5 Alias of "JIS", JDK1.1.6- Alias of "ISO2022JP"
                "jis_encoding",                                     //JDK1.1.1-JDK1.1.5 Alias of "JIS", JDK1.1.6- Alias of "ISO2022JP"
                "csJISEncoding",                                    //JDK1.1.1-JDK1.1.5 Alias of "JIS", JDK1.1.6- Alias of "ISO2022JP"
                "jis auto detect",                                  //Alias of "JISAutoDetect"
                "cp930", "ibm-930", "ibm930", "930",                //Aliases of "Cp930"
                "cp939", "ibm-939", "ibm939", "939",                //Aliases of "Cp939"
                "cp942", "ibm-942", "ibm942", "942",                //Aliases of "Cp942"
                "cp942c",                                           //Alias of "Cp942C"
                "cp943", "ibm-943", "ibm943", "943",                //Aliases of "Cp943"
                "cp943c",                                           //Alias of "Cp943C"
                "cp33722", "ibm-33722", "ibm33722", "33722"};     //Aliases of "Cp33722"
        for (int i = 0; i < Aliases.length; i++) {
            if (encoding.equalsIgnoreCase(Aliases[i])) {
                return true;
            }
        }

        return false;
    }


    /**
     * encoding �� ShiftJIS �n��̃G���R�[�f�B���O�ł��邩��Ԃ��B
     *
     * @param encoding �G���R�[�f�B���O
     *
     * @return encoding �����{��̃G���R�[�f�B���O�Ȃ� true �Ⴆ�� false
     */
    private static final boolean isCategoryOfShiftJIS(String encoding) {

        String[] Coverters = {"Cp942",     //SJIS OS/2 ���{��, Cp932 �̃X�[�p�[�Z�b�g, 0x5C -> '�_' (���p�o�b�N�X���b�V��)
                "Cp942C",    //SJIS OS/2 ���{��, Cp932 �̃X�[�p�[�Z�b�g, 0x5C -> '��' (���p�~�L��)
                "Cp943",     //SJIS OS/2 ���{��, Cp942 �̃X�[�p�[�Z�b�g �VJIS�Ή�, 0x5C -> '�_' (���p�o�b�N�X���b�V��)
                "Cp943C",    //SJIS OS/2 ���{��, Cp942 �̃X�[�p�[�Z�b�g �VJIS�Ή�, 0x5C -> '��' (���p�~�L��)
                "MS932",     //Windows ���{��
                "SJIS"};   //Shift-JIS�A���{��
        for (int i = 0; i < Coverters.length; i++) {
            if (encoding.equals(Coverters[i])) {
                return true;
            }
        }

        String[] Aliases = {"shift_jis", "ms_kanji", "csShiftJIS", //JDK1.1.1 - JDK1.1.7B Alias of "SJIS", JDK1.2 - JDK1.3 Alias of "MS932", JDK1.4 Alias of "SJIS"
                "csWindows31J", "windows-31j",          //Alias of "MS932"
                "x-sjis",                               //JDK1.2 Alias of "MS932", JDK1.3 Alias of "SJIS", JDK1.4 Alias of "MS932"
                "cp942", "ibm-942", "ibm942", "942",    //Aliases of "Cp942"
                "cp942c",                               //Alias of "Cp942C"
                "cp943", "ibm-943", "ibm943", "943",    //Aliases of "Cp943"
                "cp943c"};                             //Alias of "Cp943C"
        for (int i = 0; i < Aliases.length; i++) {
            if (encoding.equalsIgnoreCase(Aliases[i])) {
                return true;
            }
        }

        return false;
    }

}
//end of LhaProperty.java
