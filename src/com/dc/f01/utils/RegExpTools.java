package com.dc.f01.utils;

import java.util.regex.Pattern;

/**
 * Copyright: Copyright (c) 2014
 * Company: Fixed Star (Xiamen)
 * Version: 1.0.0
 * Author: ...
 * Date: 2014/9/3
 * Description:java正则表达式
 */
public final class RegExpTools {
    private RegExpTools(){}
    public static boolean judgeByRule(String regexp,String str){
        return  Pattern.matches(regexp, str);
    }

    /**
     * 判断字符串是否是数字且限定最大长度
     * @param str
     * @param maxLength
     * @return
     */
    public static boolean judgeNum(String str,int maxLength){
        return  Pattern.matches("^[0-9]{1,"+maxLength+"}+$", str);
    }
    public static boolean judgeNum(String str){
        return  Pattern.matches("^[0-9]+$", str);
    }

    public static boolean judgeLetter(String str,int maxLength){
        return  Pattern.matches("^[a-zA-Z]{1,"+maxLength+"}+$", str);
    }
    public static boolean judgeLetter(String str){
        return  Pattern.matches("^[a-zA-Z]+$", str);
    }

    /**
     * 英文字母开头或混合数字且限定字母最大出现次数,
     * @param str
     * @param maxLength
     * @return
     */
    public static boolean judgeEnAndNum(String str,int maxLength){
        return  Pattern.matches("^[a-zA-Z]{1,"+maxLength+"}+[0-9]*$", str);
    }

}
