package com.dc.f01.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;

/**
 * Copyright: Copyright (c) 2014
 * Company: Fixed Star (Xiamen)
 * Version: 1.0.0
 * Author: ...
 * Date: 2015/8/18
 * Description:
 */
public final class SysCommTools {
    private SysCommTools(){}

    /**
     * 获取逗号分隔的字符串
     * @param strs
     * @return
     */
    public static String getCommaSeqStrByStrArray(String[] strs){
        if(strs == null || strs.length == 0){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for(String str:strs){
            sb.append(",'").append(str).append("'");
        }
        return sb.toString().replaceFirst(",","");

    }
    //base64 加密
    public static String getBase64Encode(String strs){
        try {
            return new String(Base64.encodeBase64(strs.getBytes("utf-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    //base64 解密
    public static String getBase64Decode(String strs){
        try {
            return new String(Base64.decodeBase64(strs),"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getNewVipLevel(int userLv){
        int rt = 0;
        if(userLv <= 1){
            rt = 0;
        }else if(userLv >= 2 && userLv <=5){
            rt = 1;
        }else if(userLv >= 6 && userLv <=15){
            rt = 2;
        }else if(userLv >= 16 && userLv <=25){
            rt = 3;
        }else if(userLv >= 26 && userLv <=35){
            rt = 4;
        }else if(userLv >= 36 && userLv <=45){
            rt = 5;
        }else if(userLv >= 46 && userLv <=50){
            rt = 6;
        }else if(userLv >= 51 && userLv <=55){
            rt = 7;
        }else if(userLv >= 56 && userLv <=60){
            rt = 8;
        }else if(userLv >= 61 && userLv <=65){
            rt = 9;
        }else if(userLv >= 66 && userLv <=70){
            rt = 10;
        }else if(userLv == 71){
            rt = 11;
        }else if(userLv == 72){
            rt = 12;
        }else if(userLv == 73){
            rt = 13;
        }else if(userLv == 74){
            rt = 14;
        }else if(userLv == 75){
            rt = 15;
        }
        return rt;
    }
    public static void main (String[] sas) throws UnsupportedEncodingException {
    }
}
