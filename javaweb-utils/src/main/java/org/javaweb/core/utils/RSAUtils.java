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
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA加解密工具类
 */
public class RSAUtils {

	/**
	 * 算法名称
	 */
	private static final String ALGORITHM = "RSA";

	/**
	 * RSA签名算法,在java.security.Signature类有定义多种签名算法
	 */
	private static final String SIGNATURE_ALGORITHM = "SHA512withRSA";

	/**
	 * RSA加密
	 *
	 * @param encrypted 加密内容
	 * @param key       公钥或者私钥
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] encrypted, Key key) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, key);

		return segmentEncrypt(encrypted, cipher, Cipher.ENCRYPT_MODE, ((RSAKey) key).getModulus().bitLength());
	}

	/**
	 * RSA解密
	 *
	 * @param encrypted 加密内容
	 * @param key       公钥或者私钥
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] encrypted, Key key) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, key);

		return segmentEncrypt(encrypted, cipher, Cipher.DECRYPT_MODE, ((RSAKey) key).getModulus().bitLength());
	}

	/**
	 * RSA分段加密
	 *
	 * @param data
	 * @param cipher
	 * @param mode
	 * @param keySize
	 * @return
	 * @throws Exception
	 */
	private static byte[] segmentEncrypt(byte[] data, Cipher cipher, int mode, int keySize) throws Exception {
		int maxBlock = 0;

		if (mode == Cipher.DECRYPT_MODE) {
			maxBlock = keySize / 8;
		} else {
			maxBlock = keySize / 8 - 11;
		}

		int                   offSet = 0;
		int                   index  = 0;
		ByteArrayOutputStream out    = new ByteArrayOutputStream();

		while (data.length > offSet) {
			if (data.length - offSet > maxBlock) {
				out.write(cipher.doFinal(data, offSet, maxBlock));
			} else {
				out.write(cipher.doFinal(data, offSet, data.length - offSet));
			}

			offSet = ++index * maxBlock;
		}

		return out.toByteArray();
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
		KeyFactory         keyFactory = KeyFactory.getInstance(ALGORITHM);

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
		KeyFactory          keyFactory = KeyFactory.getInstance(ALGORITHM);

		return keyFactory.generatePrivate(spec);
	}

	/**
	 * 生成用于RSA加密的公钥和私钥
	 *
	 * @param keySize
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static KeyPair generateKey(int keySize) throws NoSuchAlgorithmException {
		KeyPairGenerator keygen = KeyPairGenerator.getInstance(ALGORITHM);
		SecureRandom     random = new SecureRandom();
		keygen.initialize(keySize, random);

		return keygen.generateKeyPair();
	}

	/**
	 * RSA私钥签名
	 *
	 * @param data 加密数据
	 * @param key  私钥
	 * @return
	 * @throws Exception
	 */
	public static String sign(byte[] data, Key key) throws Exception {
		byte[]              keyBytes     = key.getEncoded();
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory          keyFactory   = KeyFactory.getInstance(key.getAlgorithm());
		PrivateKey          privateK     = keyFactory.generatePrivate(pkcs8KeySpec);
		Signature           signature    = Signature.getInstance(SIGNATURE_ALGORITHM);

		signature.initSign(privateK);
		signature.update(data);

		return Base64.encodeBase64String(signature.sign());
	}

	/**
	 * RSA公钥签名验证
	 *
	 * @param data 加密数据
	 * @param key  公钥
	 * @param sign 签名Base64字符串
	 * @return
	 * @throws Exception
	 */
	public static boolean verify(byte[] data, Key key, String sign) throws Exception {
		X509EncodedKeySpec keySpec    = new X509EncodedKeySpec(key.getEncoded());
		KeyFactory         keyFactory = KeyFactory.getInstance(key.getAlgorithm());
		PublicKey          publicK    = keyFactory.generatePublic(keySpec);
		Signature          signature  = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initVerify(publicK);
		signature.update(data);

		return signature.verify(Base64.decodeBase64(sign));
	}

}
