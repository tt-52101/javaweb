package org.javaweb.utils;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class RsaTest {

	@Test
	public void shouldGenerateRSAKey() throws NoSuchAlgorithmException {
		KeyPair keyPair = RSAUtils.generateKey(2048);

		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey  publicKey  = keyPair.getPublic();

		Assert.assertTrue(privateKey != null);
		Assert.assertTrue(publicKey != null);
	}

	@Test
	public void rsaEncryptTest() throws Exception {
		String  str     = "RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试RSA加密测试爱爱爱...";
		KeyPair keyPair = RSAUtils.generateKey(2048);

		PrivateKey privateKey       = keyPair.getPrivate();
		PublicKey  publicKey        = keyPair.getPublic();
		String     privateKeyString = Base64.encodeBase64String(privateKey.getEncoded());
		String     publicKeyString  = Base64.encodeBase64String(publicKey.getEncoded());

		System.out.println("privateKeyString:" + privateKeyString);
		System.out.println("-------------------------------------------");
		System.out.println("publicKeyString:" + publicKeyString);
		System.out.println("-------------------------------------------");

		PrivateKey priKey = RSAUtils.getPrivateKey(privateKeyString);
		PublicKey  pubKey = RSAUtils.getPublicKey(publicKeyString);

		byte[] encrypted = RSAUtils.encrypt(str.getBytes("UTF-8"), pubKey);

		System.out.println("-------------------------------------------");
		System.out.println(new String(Base64.encodeBase64(encrypted)));
		System.out.println("-------------------------------------------");

		String decryptStr = new String(RSAUtils.decrypt(encrypted, RSAUtils.getPrivateKey(privateKeyString)));

		System.out.println(decryptStr);

		String signStr = RSAUtils.sign(encrypted, priKey);

		System.out.println(signStr);
		System.out.println("签名校验:" + RSAUtils.verify(encrypted, pubKey, signStr));
	}

}
