/*
 * Copyright yz 2016-01-14  Email:admin@javaweb.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.javaweb.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

/**
 * 各种常见算法加密解密类
 *
 * @author yz
 */
public class EncryptUtils {

	/**
	 * MD5加密
	 *
	 * @param context
	 * @return
	 */
	public static String md5(String context) {
		return DigestUtils.md5Hex(context);
	}

	/**
	 * MD5加密
	 *
	 * @param bytes
	 * @return
	 */
	public static String md5(byte[] bytes) {
		return DigestUtils.md5Hex(bytes);
	}

	/**
	 * MD5加密
	 *
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String md5(InputStream in) throws IOException {
		return DigestUtils.md5Hex(in);
	}

	/**
	 * Base64编码
	 *
	 * @param str
	 * @return
	 */
	public static String base64Encode(String str) {
		return new String(base64Encode(str.getBytes()));
	}

	/**
	 * Base64编码
	 *
	 * @param bytes
	 * @return
	 */
	public static byte[] base64Encode(byte[] bytes) {
		return Base64.encodeBase64(bytes);
	}

	/**
	 * Base64解码
	 *
	 * @param str
	 * @return
	 */
	public static String base64Decode(String str) {
		return new String(base64Decode(str.getBytes()));
	}

	/**
	 * Base64解码
	 *
	 * @param bytes
	 * @return
	 */
	public static byte[] base64Decode(byte[] bytes) {
		return Base64.decodeBase64(bytes);
	}

	/**
	 * 自动生成AES 密钥
	 *
	 * @param length
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String getAESKey(int length) throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(length);
		SecretKey secretKey = keyGenerator.generateKey();

		return HexUtils.bytes2HexString(secretKey.getEncoded());
	}

	/**
	 * 使用对称密钥进行AES加密 返回加密后的密文
	 *
	 * @param content
	 * @param key
	 * @return
	 * @throws java.lang.Exception
	 */
	public static String encryptionAES(String content, String key) throws Exception {
		SecretKeySpec secretKeySpec = new SecretKeySpec(HexUtils.hex2Bytes(key), "AES");
		Cipher        cipher        = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

		return HexUtils.bytes2HexString(cipher.doFinal(content.getBytes()));
	}

	/**
	 * 使用对称密钥进行AES解密 返回解密后的明文
	 *
	 * @param content
	 * @param key
	 * @return
	 * @throws java.lang.Exception
	 */
	public static String decryptionAES(String content, String key) throws Exception {
		SecretKeySpec sKeySpec = new SecretKeySpec(HexUtils.hex2Bytes(key), "AES");
		Cipher        cipher   = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, sKeySpec);
		byte[] bytes = cipher.doFinal(HexUtils.hex2Bytes(content));

		return new String(bytes);
	}

	/**
	 * RC4解密
	 *
	 * @param data 加密后的内容
	 * @param key  加密key
	 * @return
	 */
	public static String decryptionRC4(byte[] data, String key) {
		if (data == null || key == null) {
			return null;
		}

		return new String(RC4EncryptUtils.rc4(data, key));
	}

	/**
	 * RC4加密
	 *
	 * @param data 加密前的内容
	 * @param key  加密key
	 * @return
	 */
	public static byte[] encryptionRC4Byte(String data, String key) {
		try {
			return encryptionRC4Byte(data.getBytes(StandardCharsets.UTF_8), key);
		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * RC4加密
	 *
	 * @param bytes 加密前的字节
	 * @param key   加密key
	 * @return
	 */
	public static byte[] encryptionRC4Byte(byte[] bytes, String key) {
		if (bytes == null || key == null) {
			return null;
		}

		return RC4EncryptUtils.rc4(bytes, key);
	}

}
