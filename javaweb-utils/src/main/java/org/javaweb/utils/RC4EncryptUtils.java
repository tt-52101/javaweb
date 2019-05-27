package org.javaweb.utils;

import java.nio.charset.Charset;

/**
 * RC4加解密算法实现
 */
public class RC4EncryptUtils {

	private static byte[] initKey(byte[] bytes) {
		byte state[] = new byte[256];

		for (int i = 0; i < 256; i++) {
			state[i] = (byte) i;
		}

		int index1 = 0;
		int index2 = 0;

		if (bytes == null || bytes.length == 0) {
			return null;
		}

		for (int i = 0; i < 256; i++) {
			index2 = ((bytes[index1] & 0xff) + (state[i] & 0xff) + index2) & 0xff;
			byte tmp = state[i];
			state[i] = state[index2];
			state[index2] = tmp;
			index1 = (index1 + 1) % bytes.length;
		}

		return state;
	}

	/**
	 * RC4加密/解密
	 *
	 * @param input
	 * @param key
	 * @return
	 */
	public static byte[] rc4(byte[] input, String key) {
		return rc4(input, key.getBytes(Charset.forName("UTF-8")));
	}

	/**
	 * RC4加密/解密
	 *
	 * @param input
	 * @param key
	 * @return
	 */
	public static byte[] rc4(byte[] input, byte[] key) {
		int    x      = 0;
		int    y      = 0;
		byte[] arr    = initKey(key);
		int    xorIndex;
		byte[] result = new byte[input.length];

		for (int i = 0; i < input.length; i++) {
			x = (x + 1) & 0xff;
			y = ((arr[x] & 0xff) + y) & 0xff;
			byte tmp = arr[x];
			arr[x] = arr[y];
			arr[y] = tmp;
			xorIndex = ((arr[x] & 0xff) + (arr[y] & 0xff)) & 0xff;
			result[i] = (byte) (input[i] ^ arr[xorIndex]);
		}

		return result;
	}

}