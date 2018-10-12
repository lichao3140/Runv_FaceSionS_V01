package com.runvision.utils;

import javax.crypto.Cipher;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * <p>
 * RSA公钥/私钥/签名工具包
 * </p>
 * <p>
 * 罗纳德·李维斯特（Ron [R]ivest）、阿迪·萨莫尔（Adi [S]hamir）和伦纳德·阿德曼（Leonard [A]dleman）
 * </p>
 * <p>
 * 字符串格式的密钥在未在特殊说明情况下都为BASE64编码格式<br/>
 * 由于非对称加密速度极其缓慢，一般文件不使用它来加密而是使用对称加密，<br/>
 * 非对称加密算法可以用来对对称加密的密钥加密，这样保证密钥的安全也就保证了数据的安全
 * </p>
 * 
 * @author xxy
 * @create 2015年8月20日 下午2:19:52
 */
public class RSAUtils {
    
    /**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";
    
    /**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";
    
    /**
     * 获取公钥的key
     */
    private static final String PUBLIC_KEY = "RSAPublicKey";
    
    /**
     * 获取私钥的key
     */
    private static final String PRIVATE_KEY = "RSAPrivateKey";
    
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;
    
    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;
    
    /**
     * <p>
     * 生成密钥对(公钥和私钥)
     * </p>
     * 
     * @return
     * @throws Exception
     */
    public static Map<String, Object> genKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }
    
    /**
     * <p>
     * 用私钥对信息生成数字签名
     * </p>
     * 
     * @param data 已加密数据
     * @param privateKey 私钥(BASE64编码)
     *            
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = new BASE64Decoder().decodeBuffer(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data);
        return new BASE64Encoder().encode(signature.sign());
    }
    
    /**
     * <p>
     * 校验数字签名
     * </p>
     * 
     * @param data 已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign 数字签名
     *            
     * @return
     * @throws Exception
     *             
     */
    public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
        byte[] keyBytes = new BASE64Decoder().decodeBuffer(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(data);
        
        return signature.verify(new BASE64Decoder().decodeBuffer(sign));
    }
    
    /**
     * <P>
     * 私钥解密
     * </p>
     * 
     * @param encryptedData 已加密数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey) throws Exception {
        byte[] keyBytes = new BASE64Decoder().decodeBuffer(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        int inputLen = encryptedData.length;
        
        try {
        	ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            return out.toByteArray();
        }catch(Exception e){
        	e.printStackTrace();
        	return null;
        }
    }
    
    /**
     * <p>
     * 公钥解密
     * </p>
     * 
     * @param encryptedData 已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey) throws Exception {
        byte[] keyBytes = new BASE64Decoder().decodeBuffer(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicK);
        int inputLen = encryptedData.length;
        
        try{
        	ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            return out.toByteArray();
        }catch(Exception e){
        	e.printStackTrace();
        	return null;
        }
    }
    
    /**
     * <p>
     * 公钥加密
     * </p>
     * 
     * @param data 源数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
        byte[] keyBytes = new BASE64Decoder().decodeBuffer(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        
        try{
        	ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段加密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            return out.toByteArray();
        }catch(Exception e){
        	e.printStackTrace();
        	return null;
        }
    }
    
    /**
     * <p>
     * 私钥加密
     * </p>
     * 
     * @param data 源数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = new BASE64Decoder().decodeBuffer(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateK);
        int inputLen = data.length;
        
        try {
        	ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段加密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            return out.toByteArray();
        }catch(Exception e){
        	e.printStackTrace();
        	return null;
        }
    }
    
    /**
     * <p>
     * 获取私钥
     * </p>
     * 
     * @param keyMap 密钥对
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Object> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return new BASE64Encoder().encode(key.getEncoded());
    }
    
    /**
     * <p>
     * 获取公钥
     * </p>
     * 
     * @param keyMap 密钥对
     * @return
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Object> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return new BASE64Encoder().encode(key.getEncoded());
    }
    
    /**
     * 公钥加密
     * 
     * @author xxy
     * @create 2015年8月20日 下午2:09:18
     * @param source 要加密字符串
     * @param publicKey 公钥
     * @return 加密后字符串
     * @throws Exception ex
     */
    public static String getPublicEncode(String source, String publicKey) throws Exception {
        byte[] publickeyEncryptData = RSAUtils.encryptByPublicKey(source.getBytes(), publicKey);
        return new BASE64Encoder().encode(publickeyEncryptData).replaceAll("\r\n", "");
    }
    
    /**
     * 私钥解密
     * 
     * @author xxy
     * @create 2015年8月20日 下午2:02:15
     * @param encodeStr 加密字符串
     * @param privateKey 私钥
     * @return 解密后字符串
     * @throws Exception ex
     */
    public static String getPrivteDecode(String encodeStr, String privateKey) throws Exception {
        byte[] decodeData = new BASE64Decoder().decodeBuffer(encodeStr);
        return new String(RSAUtils.decryptByPrivateKey(decodeData, privateKey));
    }
    
    /**
     * 私钥加密
     * 
     * @author xxy
     * @create 2015年8月20日 下午2:04:04
     * @param source 要加密字符串
     * @param privateKey 私钥
     * @return 加密后字符串
     * @throws Exception
     */
    public static String getPrivateEncode(String source, String privateKey) throws Exception {
        byte[] data = RSAUtils.encryptByPrivateKey(source.getBytes(), privateKey);
        return new BASE64Encoder().encode(data).replaceAll("\r\n", "");
    }
    
    /**
     * 公钥解密
     * 
     * @author xxy
     * @create 2015年8月20日 下午2:05:48
     * @param encodeStr 加密后字符串
     * @param publicKey 公钥
     * @return 解密后字符串
     * @throws IOException
     * @throws Exception
     */
    public static String getPublicDecode(String encodeStr, String publicKey) throws IOException, Exception {
        byte[] data = new BASE64Decoder().decodeBuffer(encodeStr);
        return new String(RSAUtils.decryptByPublicKey(data, publicKey));
    }
    
    /**
     * 验证是否加密
     * 
     * @author xxy
     * @create 2015年8月20日 下午2:08:17
     * @param encodeStr 加密后字符串
     * @param privateKey 私钥
     * @param publicKey 公钥
     * @return true:正确加密 false:错误加密
     * @throws IOException
     * @throws Exception
     */
    public static boolean verify(String encodeStr, String privateKey, String publicKey) throws IOException, Exception {
        byte[] decodeData = new BASE64Decoder().decodeBuffer(encodeStr);
        String sign = RSAUtils.sign(decodeData, privateKey);
        return RSAUtils.verify(decodeData, publicKey, sign);
    }
    
    
    public static void main(String[] args) throws Exception {
		
    	  Map<String, Object> keyMap = genKeyPair();
          if(keyMap != null) {
              /*String privateKey = getPrivateKey(keyMap);
              String publicKey = getPublicKey(keyMap);
              
              System.out.println("privateKey:"+privateKey);
              System.out.println("publicKey:"+publicKey);*/
          }
          String pri="MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAMJWg+jgnW5X4CHvTVDMcnBsFAdh"+
"WNQQMjfOOie1KW+ZpPOqZ4tiUVsfbVReEkET1jEfhGfViwtmgyULonIcfjRFhESZCIDZeaOMikQ3"+
"gL9kf7z3Yza2bxqIgLhb4XTu3OudRHbuo9uytwoDnKtx3lT6XCixklH8AKtLgDL9WV2dAgMBAAEC"+
"gYEAnfWeUGpJ7EeHCW4uBM+48QIYIYuRnQTxHIUGpgHNhUV4WwoWEag/gnZ/8gRoh/bssY7xm0hq"+
"NUEEtdbIGkJonP13O/QAkADgSu/OpZV5H/xP+RFhBaN3OA40/ItOOO1MIb+BwcZrDoZUkoXv1SWi"+
"vUv2QUcAccNg2gbGYwv4A40CQQD4WQHyiviR8OpSlc5BxTg37SKapmXOcQtmpPOvlK99zftSE79+"+
"5mIYfHsSp1HSpfk+VWVt2G7BqZOsNvXZRb77AkEAyFN5JtnIzgSgo7LpooWErYRnoqgblzyKpN1C"+
"XgT2QGnub4PP7khKbUjSmrsoAmwFXvWiPOX/EF0I/Ezgr29SRwJBAKLW2ewLK4mmCj80cxW1F3O0"+
"TahRyxdeEDexmQdb2uYGle/vevTeYxvjI1/Lzl7s7Uzt+Z/Y9maNpoKZVwKsNNkCQQCilczwUTV+"+
"r5bJBX5Fn2PtiFasVw/9kO9dmw4wTIqoANG5xBtQY2+0frQfTOLOBGnfhjCkiG6ZE0klrCd3ezwl"+
"AkBS3Ep38UyAufPmXKRS8l91BRAw4x+Obk8io3PuuRUi+5JwmR9VRzlCaJhbQmTjtXTeg1f2eIBg"+
"+KPvP/5vfOeE";
          String pub="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDCVoPo4J1uV+Ah701QzHJwbBQHYVjUEDI3zjon"+
"tSlvmaTzqmeLYlFbH21UXhJBE9YxH4Rn1YsLZoMlC6JyHH40RYREmQiA2XmjjIpEN4C/ZH+892M2"+
"tm8aiIC4W+F07tzrnUR27qPbsrcKA5yrcd5U+lwosZJR/ACrS4Ay/VldnQIDAQAB";

          /*
          JSONObject json = new JSONObject();
          json.put("name", "xinqingsong");
          json.put("idCard", "3714855988860522");
          
          byte[] ss=json.toString().getBytes();
          
          String sign= sign(ss, pri);
          System.out.println("签名后的sign："+sign);
          
          JSONObject jsonss = new JSONObject();
          jsonss.put("name", "xinqingsong");
          jsonss.put("idCard", "3714855988860523");

          byte[] sss=jsonss.toString().getBytes();
          boolean su=verify(sss, pub, sign);
          
          System.out.println("验证签名是否正确："+su);
    	*/
	}

}
