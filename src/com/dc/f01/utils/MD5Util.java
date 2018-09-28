package com.dc.f01.utils;

import org.apache.commons.codec.digest.DigestUtils;


/**
*md5加密算法，有16位、32位加密，分别生成32位、64位密文
*/
public final class MD5Util {
    private MD5Util(){}
	public static String Md5(String plainText) {
		return md5Hex(plainText);
	}
    public static String md5Hex(String d){
        return DigestUtils.md5Hex(d);
    }
    public static String Md5_16(String plainText) {
        return md5Hex(plainText).substring(8, 24);
    }
}
