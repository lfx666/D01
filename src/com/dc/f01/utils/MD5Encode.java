package com.dc.f01.utils;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Copyright: Copyright (c) 2014
 * Company: Fixed Star (Xiamen)
 * Version: 1.0.0
 * Author: ...
 * Date: 2015/9/25
 * Description:
 */
public class MD5Encode {
    public static final String encode(String s)
    {
        char[] HexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        String strEncode = "";
        try
        {
            byte[] strTemp = s.getBytes("UTF-8");
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++)
            {
                byte strTemp2 = md[i];
                str[(k++)] = HexDigits[(strTemp2 >>> 4 & 0xF)];
                str[(k++)] = HexDigits[(strTemp2 & 0xF)];
            }
            strEncode = new String(str);
        }
        catch (NoSuchAlgorithmException e)
        {
            return strEncode;
        }
        catch (UnsupportedEncodingException e)
        {
            return strEncode;
        }
        return strEncode;
    }

    public static boolean eq(String ps, String md5ps)
    {
        boolean is = false;
        if (("".equals(ps)) && ("".equals(md5ps))) {
            return is;
        }
        String mps = encode(ps);
        if (mps.equals(md5ps)) {
            is = true;
        } else {
            is = false;
        }
        return is;
    }
}
