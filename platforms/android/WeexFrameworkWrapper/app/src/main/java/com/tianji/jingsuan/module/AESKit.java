package com.tianji.jingsuan.module;

import com.umeng.socialize.net.utils.Base64;

import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * @Description: TODO
 * @author: iSuperMeng
 * @date: 2018年4月11日
 * 
 */
public class AESKit {

	// 加密
	public static String AesCbcEncrypt(String content, String key, String iv) throws Exception {
		if(!StringUtils.isNotBlank(content)){
			throw new Exception("请输入加密内容值");
		}
		
		if(!StringUtils.isNotBlank(key)){
			throw new Exception("请输入加密Key值");
		}
		
		if(!StringUtils.isNotBlank(iv)){
			throw new Exception("请输入加密IV值");
		}
		
		byte[] raw = key.getBytes();
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// "算法/模式/补码方式"
		IvParameterSpec ivp = new IvParameterSpec(iv.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivp);
		byte[] encrypted = cipher.doFinal(content.getBytes());
		String ret  = Base64.encodeBase64String(encrypted);// 此处使用BASE64做转码功能，同时能起到2次加密的作用
		return ret;
	}

	// 解密
	public static String AesCbcDecrypt(String content, String key, String iv) throws Exception {

		if(!StringUtils.isNotBlank(content)){
			throw new Exception("请输入解密内容值");
		}
		
		if(!StringUtils.isNotBlank(key)){
			throw new Exception("请输入解密Key值");
		}
		
		if(!StringUtils.isNotBlank(iv)){
			throw new Exception("请输入解密IV值");
		}
		
		byte[] raw = key.getBytes("utf-8");
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		IvParameterSpec ivp = new IvParameterSpec(iv.getBytes());
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivp);
		byte[] encrypted1 = Base64.decodeBase64(content);// 先用base64解密
		byte[] original = cipher.doFinal(encrypted1);
		String retStr = new String(original);
		return retStr;
	}

	public static void main(String[] args) throws Exception {
		/*
		 * 加密用的Key 可以用26个字母和数字组成，最好不要用保留字符，虽然不会错，至于怎么裁决，个人看情况而定
		 * 此处使用AES-128-CBC加密模式，key需要为16位。
		 */
		String cKey = "1234567890123456";
		// 需要加密的字串
		String cSrc = "Email : arix04@xxx.com";
		String iv = "abcdefghijkkm21p";
		System.out.println(cSrc);
		// 加密
		long lStart = System.currentTimeMillis();
		String enString = AESKit.AesCbcEncrypt(cSrc, cKey, iv);
		System.out.println("加密后的字串是：" + enString);

		long lUseTime = System.currentTimeMillis() - lStart;
		System.out.println("加密耗时：" + lUseTime + "毫秒");
		// 解密
		lStart = System.currentTimeMillis();
		String DeString = AESKit.AesCbcDecrypt(enString, cKey, iv);
		System.out.println("解密后的字串是：" + DeString);
		lUseTime = System.currentTimeMillis() - lStart;
		System.out.println("解密耗时：" + lUseTime + "毫秒");
	}
}
