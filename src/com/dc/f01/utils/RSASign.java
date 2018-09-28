package com.dc.f01.utils;

import org.apache.commons.codec.binary.Base64;

//import javax.persistence.Convert;
import java.io.*;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSASign {
    /**
     * String to hold name of the encryption algorithm.
     */
    public static final String ALGORITHM = "RSA";

    /**
     * String to hold the name of the private key file.
     */
    public static final String PRIVATE_KEY_FILE = "D:/rsa/pkcs8_priv2.pem";

    /**
     * String to hold name of the public key file.
     */
    public static final String PUBLIC_KEY_FILE = "D:/rsa/rsa_public_key.pem";

    /**
     * rsa验签
     *
     * @param content   被签名的内容
     * @param sign      签名后的结果
     * @param publicKey rsa公钥
     * @param charset   字符集
     * @return 验签结果
     * @throws SignatureException 验签失败，则抛异常
     */
    static boolean doCheck(String content, String sign, String publicKey, String charset) throws SignatureException {
        try {
            PublicKey pubKey = getPublicKeyFromX509("RSA", new ByteArrayInputStream(publicKey.getBytes()));

            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initVerify(pubKey);
            signature.update(getContentBytes(content, charset));

            return signature.verify(Base64.decodeBase64(sign.getBytes()));
        } catch (Exception e) {
            throw new SignatureException("RSA验证签名[content = " + content + "; charset = " + charset + "; signature = " + sign + "]发生异常!", e);
        }
    }

    private static PublicKey getPublicKeyFromX509(String algorithm, InputStream ins) throws NoSuchAlgorithmException {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            byte[] encodedKey = StreamTool.readInputStream(ins);

            // 先base64解码
            encodedKey = Base64.decodeBase64(encodedKey);
            return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
        } catch (Exception ex) {
            // 不可能发生
        }
        return null;
    }

    private static byte[] getContentBytes(String content, String charset) throws UnsupportedEncodingException {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        return content.getBytes(charset);
    }


    /**
     * rsa签名
     *
     * @param content    待签名的字符串
     * @param privateKey rsa私钥字符串
     * @param charset    字符编码
     * @return 签名结果
     * @throws Exception 签名失败则抛出异常
     */
    public static String rsaSign(String content, String privateKey, String charset) throws SignatureException {
        try {
            PrivateKey priKey = getPrivateKeyFromPKCS8(ALGORITHM, new ByteArrayInputStream(privateKey.getBytes()));

            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initSign(priKey);
            if (charset == null || "".equals(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }
            byte[] signed = signature.sign();

            return new String(Base64.encodeBase64(signed));
        } catch (Exception e) {
            throw new SignatureException("RSAcontent = " + content + "; charset = " + charset, e);
        }
    }

    public static PrivateKey getPrivateKeyFromPKCS8(String algorithm, InputStream ins) throws Exception {
        if (ins == null || (algorithm == null || "".equals(algorithm))) {
            return null;
        }
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        byte[] encodedKey = StreamTool.readInputStream(ins);
        encodedKey = Base64.decodeBase64(encodedKey);
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
    }


    public static void main(String[] args) throws Exception {
        String ss;

        try {
            ss="<?xml version='1.0' encoding='utf-8'?><message><playerName>FYJamee123</playerName><pwd>9A1E18230CC5A638E4546D4A904EF556</pwd><roomId>0</roomId><merchantId>20008800</merchantId><loginIp>58.60.63.190</loginIp><gameCode>PTG0001</gameCode></message>";
            //byte[] cc ="PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4KPG1lc3NhZ2U+PHBsYXllck5hbWU+dGVzdDwvcGxheWVyTmFtZT48cHdkPjA5OGY2YmNkNDYyMWQzNzNjYWRlNGU4MzI2MjdiNGY2PC9wd2Q+PHJvb21JZD4wPC9yb29tSWQ+PG1lcmNoYW50SWQ+MjAwMDkxMDA8L21lcmNoYW50SWQ+PGxvZ2luSXA+MjE4LjE4LjE1Ny4yMTA8L2xvZ2luSXA+PGdhbWVDb2RlPlBURzAwMDY8L2dhbWVDb2RlPjwvbWVzc2FnZT4K".getBytes();
            //System.out.println(new String(Base64.decodeBase64(cc)));

            String sign="ZW5tYkpyeFpaR0hjSEtJWG5SOFJZTnRFcWtGK1Z4dDJXTEFBaTV1R0pCTWRWNTJKTDZSTnErRnUxYVhaYW1sNlErckF3cVRXYnowSCs5SUdPY3psS0ZiWklLZ1FXVmxFMHhMRXVobUt3VWdia1dTNDRBMWZQdWNUYTZQb3hjZldBdjJJSFdGWmJoVjFER2tBRFNjcFhic2xVQVBaRXYzS0RJNC9aNmRSbFhNPQ==";

            String sing ="enmbJrxZZGHcHKIXnR8RYNtEqkF+Vxt2WLAAi5uGJBMdV52JL6RNq+Fu1aXZaml6Q+rAwqTWbz0H+9IGOczlKFbZIKgQWVlE0xLEuhmKwUgbkWS44A1fPucTa6PoxcfWAv2IHWFZbhV1DGkADScpXbslUAPZEv3KDI4/Z6dRlXM=";
            String xml ="PD94bWwgdmVyc2lvbj0nMS4wJyBlbmNvZGluZz0ndXRmLTgnPz48bWVzc2FnZT48cGxheWVyTmFtZT5GWUphbWVlMTIzPC9wbGF5ZXJOYW1lPjxwd2Q+OUExRTE4MjMwQ0M1QTYzOEU0NTQ2RDRBOTA0RUY1NTY8L3B3ZD48cm9vbUlkPjA8L3Jvb21JZD48bWVyY2hhbnRJZD4yMDAwODgwMDwvbWVyY2hhbnRJZD48bG9naW5JcD41OC42MC42My4xOTA8L2xvZ2luSXA+PGdhbWVDb2RlPlBURzAwMDE8L2dhbWVDb2RlPjwvbWVzc2FnZT4=";
            String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDGnt9667FBKIZWD9OsH9btriIm" +
                    "qqU2btve4rNj7xcQD/7KTow/tA5t26tWIWvL93BSLT6ng8lkvZq2Zuf3HyVtlXOq" +
                    "QPPqi4vGk9HBxQhLKWHcuAX54fc7dOYrG80aiQ6DuNWQ7FEaBZIE+JY2uYpo33sm" +
                    "KNIP36Wsum56UuaDgQIDAQAB";
            byte[] cc = Base64.decodeBase64(sing.getBytes());
            byte[] dd = Base64.decodeBase64(xml.getBytes());
            //System.out.println(new String(cc));
            //System.out.println(new String(dd));
            System.out.println(doCheck(ss, sing, pubKey, "UTF-8"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}