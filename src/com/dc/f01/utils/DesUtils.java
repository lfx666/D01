package com.dc.f01.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;

public class DesUtils {

    /**
     * DES算法密钥
     */
    private static final byte[] DES_KEY = { 5, 45, -110, 82, -32, -85, -127, -65 };//取值范围是-128~127
    /**
     * 数据加密，算法（DES）
     *
     * @param data
     *            要进行加密的数据
     * @return 加密后的数据
     */
    public static String encryptBasedDes(String data) {
        String enBs64 = null;
        try {
            // DES算法要求有一个可信任的随机数源
            SecureRandom sr = new SecureRandom();
            DESKeySpec deskey = new DESKeySpec(DES_KEY);
            // 创建一个密匙工厂，然后用它把DESKeySpec转换成一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(deskey);
            // 加密对象
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key, sr);
            // 加密，并把字节数组编码成字符串
            String encryptedData = new sun.misc.BASE64Encoder().encode(cipher.doFinal(data.getBytes()));

            enBs64 = new String(Base64.encodeBase64(encryptedData.getBytes("UTF8")));

        } catch (Exception e) {
            //log.error("加密错误，错误信息：", e);
            throw new RuntimeException("DES加密错误，错误信息：", e);
        }
        return enBs64;
    }

    /**
     * 数据解密，算法（DES）
     *
     * @param cryptData
     *            加密数据
     * @return 解密后的数据
     */
    public static String decryptBasedDes(String cryptData) {
        String decryptedData = null;
        try {
            String deBs64 =new String(Base64.decodeBase64(cryptData),"UTF8");

            // DES算法要求有一个可信任的随机数源
            SecureRandom sr = new SecureRandom();
            DESKeySpec deskey = new DESKeySpec(DES_KEY);
            // 创建一个密匙工厂，然后用它把DESKeySpec转换成一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(deskey);
            // 解密对象
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key, sr);
            // 把字符串解码为字节数组，并解密
            decryptedData = new String(cipher.doFinal(new sun.misc.BASE64Decoder().decodeBuffer(deBs64)));
        } catch (Exception e) {
            //log.error("解密错误，错误信息：", e);
            throw new RuntimeException("DES解密错误，错误信息：", e);
        }
        return decryptedData;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        String str="dgggertgergrtgergtert345353245";
        String key = "MjY1MjUyLjQ=OTI1NDE1Ljg=";

        // DES数据加密
        String s1=encryptBasedDes(str);

        System.out.println("加密："+s1);

        // DES数据解密
        String s2=decryptBasedDes(s1);

        System.err.println("解密："+s2);


    }
}
