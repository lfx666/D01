package com.dc.f01.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang.StringUtils;

/**
 * Copyright: Copyright (c) 2014
 * Company: Fixed Star (XiaMen)
 * Version: 1.0.0
 * Author: Gary
 * Date: 2014-8-19
 * Description: Number 帮助类
 */
public class NumbericTools {

	private static java.text.DecimalFormat  dff =new  java.text.DecimalFormat("#0.0000");
	
	public static String toBigNum(Double dd){
		Number dd1 = new BigDecimal(dd);
		String str=dff.format(dd1);
		return str.substring(0, str.lastIndexOf('.')+3);
	}
	
	public static int toInt(String str) {
		if(!StringUtils.isNumeric(str)){
			return 0;
		}
		return Integer.parseInt(str);
	}
	
//	public static Number toNumber(String str) {
//		if(!StringUtils.isNumeric(str)){
//			return 0;
//		}
//		return (str);
//	}
	
	public static int obj2Int(Object obj) {
		if(obj == null){return 0;}
		
		if(!NumbericTools.isNum(obj.toString())){
			return 0;
		}
		return Integer.parseInt(obj.toString());
	}

	private static java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.0000");
	   
	public static String getCurrencyVal(Double dd){
		if(dd == null){
			return null;
		}
		return df.format(dd);
	}
	
	public static boolean isNum(String str){
		final String reg ="^[-+]?(\\d+\\.{0,1}\\d*)$";
		return str.matches(reg);
	}

	public static Double toDouble(Object obj) {
		if(obj == null){return 0.00;}
		
		if(!isNum(obj.toString())){
			return 0.00;
		}
		return Double.valueOf(obj.toString());
	}

	public static Long obj2Long(Object obj) {
		if(obj == null){return 0l;}
		
		if(!isNum(obj.toString())){
			return 0l;
		}
		return Long.valueOf(obj.toString());
	}
	//千分符分割
	private static java.text.DecimalFormat df_num = new java.text.DecimalFormat("#,##0.00");

	public static String getFormCoinVal(String dd){
		if(dd == null){
			return null;
		}
		Double num=DataTools.getAsDouble(dd);
		df_num.setRoundingMode(RoundingMode.FLOOR);
		return df_num.format(num);
	}
}
