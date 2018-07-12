/*
 * Copyright yz 2017-11-23 Email:admin@javaweb.org.
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
package org.javaweb.core.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA加解密工具类
 */
public class RSAUtils {

	/**
	 * RSA加密
	 *
	 * @param key       公钥或者私钥
	 * @param encrypted 加密内容
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(Key key, byte[] encrypted) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, key);

		return cipher.doFinal(encrypted);
	}

	/**
	 * RSA解密
	 *
	 * @param key       公钥或者私钥
	 * @param encrypted 加密内容
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(Key key, byte[] encrypted) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, key);

		return cipher.doFinal(encrypted);
	}

	/**
	 * 字符串转公钥对象
	 *
	 * @param publicKey
	 * @return
	 * @throws Exception
	 */
	public static PublicKey getPublicKey(String publicKey) throws Exception {
		byte[]             keyBytes   = Base64.decodeBase64(publicKey);
		X509EncodedKeySpec spec       = new X509EncodedKeySpec(keyBytes);
		KeyFactory         keyFactory = KeyFactory.getInstance("RSA");

		return keyFactory.generatePublic(spec);
	}

	/**
	 * 字符串转私钥对象
	 *
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
	public static PrivateKey getPrivateKey(String privateKey) throws Exception {
		byte[]              keyBytes   = Base64.decodeBase64(privateKey);
		PKCS8EncodedKeySpec spec       = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory          keyFactory = KeyFactory.getInstance("RSA");

		return keyFactory.generatePrivate(spec);
	}

	/**
	 * 生成用于RSA加密的公钥和私钥,返回的Map包含了公钥和私钥.可以通过Map
	 * 的KEY获取公私钥的值,其中公钥键为:PUBLIC_KEY,私钥为:PRIVATE_KEY
	 *
	 * @param keySize
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static Map<String, RSAKey> generateKey(int keySize) throws NoSuchAlgorithmException {
		Map<String, RSAKey> keyMap = new HashMap<String, RSAKey>();
		KeyPairGenerator    keygen = KeyPairGenerator.getInstance("RSA");
		SecureRandom        random = new SecureRandom();
		keygen.initialize(keySize, random);
		KeyPair       kp         = keygen.generateKeyPair();
		RSAPrivateKey privateKey = (RSAPrivateKey) kp.getPrivate();
		RSAPublicKey  publicKey  = (RSAPublicKey) kp.getPublic();

		keyMap.put("PRIVATE_KEY", privateKey);
		keyMap.put("PUBLIC_KEY", publicKey);

		return keyMap;
	}

	public static void main(String[] args) throws Exception {
		String              str = "RSA加密测试...";
		Map<String, RSAKey> map = generateKey(2048);

		PrivateKey privateKey = (PrivateKey) map.get("PRIVATE_KEY");
		PublicKey  publicKey  = (PublicKey) map.get("PUBLIC_KEY");

		String privateKeyString = Base64.encodeBase64String(privateKey.getEncoded());
		String publicKeyString  = Base64.encodeBase64String(publicKey.getEncoded());

		System.out.println("privateKeyString:" + privateKeyString);
		System.out.println("-------------------------------------------");
		System.out.println("publicKeyString:" + publicKeyString);
		System.out.println("-------------------------------------------");

		System.out.println(getPrivateKey(privateKeyString));
		System.out.println(getPublicKey(publicKeyString));

		byte[] encrypted = encrypt(getPublicKey(publicKeyString), str.getBytes());

		System.out.println("-------------------------------------------");
		System.out.println(new String(Base64.encodeBase64(encrypted)));
		System.out.println("-------------------------------------------");

		System.out.println(new String(decrypt(getPrivateKey(privateKeyString), encrypted)));
	}

}
