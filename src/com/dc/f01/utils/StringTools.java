package com.dc.f01.utils;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Copyright: Copyright (c) 2014
 * Company: Fixed Star (XiaMen)
 * Version: 1.0.0
 * Author: Gary
 * Date: 2014-8-19
 * Description: String 帮助类
 */
public final class StringTools{
    private StringTools(){};
    public static void main(String[] sd){
        System.out.println(StringTools.strToHtml("St&art ti\\me is <not greater than end time"));
    }
    /*
	 * Object 转String
	 */
	public static String obj2String(Object obj){
		if(obj == null){
			return null;
		}
		return String.valueOf(obj);
	}
	
	public static Integer obj2Int(Object obj){
		if(obj == null){
			return 0;
		}
		return Integer.parseInt((String) obj);
	}
	/*
	 * 首写字母大写
	 */
	public static String capitalize(String str){
		if(!isBlank(str)){
			str = org.apache.commons.lang.StringUtils.capitalize(str);
		}
		return str;
		
	}
    public static String uncapitalize(String str){
        if(!isBlank(str)){
            str = org.apache.commons.lang.StringUtils.uncapitalize(str);
        }
        return str;

    }

    public static boolean isBlankNull(String str){
        return (org.apache.commons.lang.StringUtils.isBlank(str) || str.equalsIgnoreCase("NULL"));
    }
    public static boolean isNotBlankNull(String str){
        return !isBlankNull(str);
    }
    public static boolean isBlank(String str){
        return (org.apache.commons.lang.StringUtils.isBlank(str));
    }
    public static boolean isNotBlank(String str){
        return !isBlank(str);
    }
    public static String[] split(String str,String separatorChars) {
        return org.apache.commons.lang.StringUtils.split(str,separatorChars);
    }
    public static String replace(String text, String searchString, String replacement) {
        return org.apache.commons.lang.StringUtils.replace(text,searchString,replacement);
    }
    
    public static String lowerCase(String str){
    	return org.apache.commons.lang.StringUtils.lowerCase(str);
    }
    
    public static String upperCase(String str){
    	return org.apache.commons.lang.StringUtils.upperCase(str);
    }

    public static boolean isNumeric(String str) {
        if(isBlankNull(str)){
            return false;
        }
        return org.apache.commons.lang.StringUtils.isNumeric(str);
    }
    //是否数字(包含小数)
    public static boolean isDigit(String str) {
        String _s = "";
        if(isBlankNull(str)){
            return false;
        }else{
            _s = str.replaceAll(",","").replaceAll(".","");
        }
        return org.apache.commons.lang.StringUtils.isNumeric(str);
    }
    public static boolean contains(String str, char searchChar) {
        return org.apache.commons.lang.StringUtils.contains(str,searchChar);
    }
    public static boolean contains(String str, String searchChar) {
        return org.apache.commons.lang.StringUtils.contains(str,searchChar);
    }
    /**
     * 去除下划线,下划线后字母大写
     * @param str
     * @return
     * @see
     */
    public static String lowerRMUnderScode(String str){
    	if(isBlank(str)){return null;}
    	StringBuffer sb =new StringBuffer();
    	String value = lowerCase(str);
    	sb.append(value);
    	int count = sb.indexOf("_");  
        while(count!=0){  
            int num = sb.indexOf("_",count);  
            count = num+1;  
            if(num!=-1){  
                char ss = value.charAt(count);  
                char ia = (char) (ss - 32);  
                sb.replace(count,count+1,ia+"");  
            }  
        }  
    	return sb.toString().replaceAll("_", "");
    }
    /**
     * 更换为大写字母,原大写字母前加入下划线
     * @param str
     * @return
     * @see
     */
    public static String upperUnderScode(String str){
    	if(isBlank(str)){return null;}
    	StringBuffer sb =new StringBuffer();
    	char[] array = str.toCharArray();
    	for(char ch:array){
    		
    		if(Character.isUpperCase(ch)){
    			sb.append("_");
    		}
    		sb.append(ch);
    	}
    	return upperCase(sb.toString());
    }
    
    /**
     * String str = "abc = :bb and deg = :aa and dd = :bb";
     * @param aSourceString
     * @param aStartStr
     * @param aEndStr
     * @return
     */
    public static String[] getParamFromString(String aSourceString, String aStartStr, String aEndStr) {
        aSourceString = aSourceString + aEndStr;
        String strSource = aSourceString;
        List strKey = new ArrayList();
        int iStartIndex = strSource.indexOf(aStartStr);
        int iStartLength = aStartStr.length();
        int iEndLength = aEndStr.length();
        String strTemp = "";
        strTemp = strSource.substring(iStartIndex + iStartLength, strSource.length());
        int iEndIndex = strTemp.indexOf(aEndStr) + strSource.substring(0, iStartIndex + iStartLength).length();
        if (iEndIndex == iStartIndex) {
            strKey.add(strTemp);
        }
        while ((iStartIndex != -1) && (iEndIndex != -1) && (iStartIndex < iEndIndex)) {
            strTemp = strSource.substring(iStartIndex + iStartLength, iEndIndex);
            strKey.add(strTemp);
            strSource = strSource.substring(iEndIndex + iEndLength, strSource.length());
            iStartIndex = strSource.indexOf(aStartStr);
            strTemp = strSource.substring(iStartIndex + iStartLength, strSource.length());
            iEndIndex = strTemp.indexOf(aEndStr) + strSource.substring(0, iStartIndex + iStartLength).length();
        }
        return ((String[])(String[])strKey.toArray(new String[0])); }

    public static String replaceParamString(String source, String s1, String s2) {
        int index = source.indexOf(s1);
        if (index == 0)
            return s2 + source.substring(s1.length());
        if (index > 0) {
            return source.substring(0, index) + s2 + source.substring(index + s1.length());
        }
        return source;
    }

    public static String replaceParamString(String source, String[] l, String aCode, String aStartStr, String aEndStr) {
        for (int i = 0; i < l.length; ++i) {
            source = replaceParamString(source + aEndStr, aStartStr + l[i] + aEndStr, aCode);
        }
        return source;
    }

    /**
     * 只用于国际化资源的占位符替换,格式:123{0}abcd{1}
     * @param i18nRes
     * @param params
     * @return
     */
    public static String replaceI18nSpace(String i18nRes, Object[] params){
        String rtVal = i18nRes;
        if ((params != null) && (params.length > 0)) {
            for (int i = 0; i < params.length; ++i) {
                if (params[i] == null) {
                    rtVal = org.apache.commons.lang.StringUtils.replaceOnce(i18nRes, "{" + i + "}", "{null}");
                }else {
                    rtVal = org.apache.commons.lang.StringUtils.replaceOnce(i18nRes, "{" + i + "}", params[i].toString());
                }
            }
        }
        return rtVal;
    }
    
    public static String trim(String str){
    	return StringUtils.trim(str);
    }
    public static String numberStr_DecimalFormat(Object numStr,int f){
        String _f = ".####";
        if(f > 0){
            _f = ".";
            for (int i=0;i<f;i++){
                _f+="#";
            }
        }
        DecimalFormat df = new DecimalFormat(_f );
        return df.format(numStr);
    }
    public static String numberStr_DecimalFormat(String pattern,Object numStr){
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(numStr);
    }
    public static String numberStr_NumberFormat(Object numStr,int minFcd,int maxFcd,int minIgd,int maxIgd){
        NumberFormat ddf = NumberFormat.getNumberInstance();
        int xsMi = 4;
        int xsMx = 4;
        if(minFcd > 0 ){
            xsMi =  minFcd;
        }
        if(maxFcd > 0 ){
            xsMx =  maxFcd;
        }
        ddf.setMaximumFractionDigits(xsMx) ;//3 设置数值的小数部分允许的最大位数。
        ddf.setMinimumFractionDigits(xsMi);//0 设置数值的小数部分允许的最小位数。

        if(maxIgd > 0 ){
            ddf.setMaximumIntegerDigits(maxIgd) ;//40 设置数值的整数部分允许的最大位数。
        }
        if(minIgd > 0 ){
            ddf.setMinimumIntegerDigits(minIgd);//1  设置数值的整数部分允许的最小位数.
        }
        return ddf.format(numStr);
    }
    /**
     * 含有中文的字符串转成Unicode
     * @param str
     * @return
     * @throws Exception
     */
    public static String strToUnicode(String str){
        if(isBlankNull(str)){
            return str;
        }
        return StringEscapeUtils.escapeJava(str);
    }
    public static String strToHtml(String str){
        if(isBlankNull(str)){
            return str;
        }
        return StringEscapeUtils.escapeHtml(str).replaceAll("'","&apos;");
    }
    /**
     * 判断单个字符串是否为中文
     * @param oneStr
     * @return
     */
    public static boolean isChinese(String oneStr){
        String re = "[\u4e00-\u9fa5]";
        return RegExpTools.judgeByRule(re,oneStr);
    }
    
    public static String substring(String str,int start){
        return org.apache.commons.lang.StringUtils.substring(str,start);
    }
    public static String substring(String str,int start,int end){
        return org.apache.commons.lang.StringUtils.substring(str,start,end);
    }
    public static String substringAfterLast(String str, String separator){
        return org.apache.commons.lang.StringUtils.substringAfterLast(str, separator);
    }
    public static String substringBeforeLast(String str, String separator){
        return org.apache.commons.lang.StringUtils.substringBeforeLast(str,separator);
    }
    public static String right(String str, int len){
        return org.apache.commons.lang.StringUtils.right(str, len);
    }
    public static String rightPad(String str, int size, String padStr){
        return org.apache.commons.lang.StringUtils.rightPad(str, size, padStr);
    }
    public static String left(String str, int len){
        return org.apache.commons.lang.StringUtils.left(str, len);
    }
    public static boolean equalsStr(String str,String deStr){
        return org.apache.commons.lang.StringUtils.equals(str,deStr);
    }
    public static boolean equalsIgnoreCaseStr(String str,String deStr){
        return org.apache.commons.lang.StringUtils.equalsIgnoreCase(str,deStr);
    }
    public static String leftPad(String str, int size, String padStr){
        return org.apache.commons.lang.StringUtils.leftPad(str, size,padStr);
    }
    public static int countMatches(String str, String sub){
        return org.apache.commons.lang.StringUtils.countMatches(str, sub);
    }
    public static JSONObject stringtoJsonObj(String jasonStr) {
        JSONArray array = JSONArray.fromObject(jasonStr);
        return array.size()>0?JSONObject.fromObject(array.get(0)):null;
    }

}
