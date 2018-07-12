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
package org.javaweb.core.utils;

import java.io.*;
import java.util.zip.DataFormatException;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;

public class IOUtils extends org.apache.commons.io.IOUtils {

	/**
	 * InputStream 转字符串
	 *
	 * @param in
	 * @return
	 * @throws java.io.IOException
	 */
	public static String inputStreamToString(InputStream in) throws IOException {
		int           a;
		byte[]        b  = new byte[1024];
		StringBuilder sb = new StringBuilder();

		try {
			while ((a = in.read(b)) != -1) {
				sb.append(new String(b, 0, a));
			}
		} catch (IOException e) {
			throw e;
		}

		return sb.toString();
	}

	/**
	 * 输入流转输出流
	 *
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static byte[] inputStreamToByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int                   a    = 0;
		byte[]                b    = new byte[1024];

		while ((a = in.read(b)) != -1) {
			baos.write(b, 0, a);
		}

		return baos.toByteArray();
	}

	/**
	 * 读取文件到字符串
	 *
	 * @param path
	 * @return
	 * @throws java.io.IOException
	 */
	public static String readFileToString(String path) throws IOException {
		return inputStreamToString(new FileInputStream(path));
	}

	/**
	 * GZIP 解压
	 *
	 * @param bytes
	 * @return
	 * @throws IOException
	 */
	public static byte[] gunZip(byte[] bytes) throws IOException {
		if (bytes == null || bytes.length == 0) {
			return null;
		}

		ByteArrayInputStream in = new ByteArrayInputStream(bytes);

		try {
			GZIPInputStream unGZip = new GZIPInputStream(in);
			return IOUtils.toByteArray(unGZip);
		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 * Inflater 解压
	 *
	 * @param data
	 * @return
	 * @throws IOException
	 * @throws DataFormatException
	 */
	public static byte[] decompressInflater(byte[] data) throws IOException, DataFormatException {
		Inflater inflater = new Inflater();
		inflater.setInput(data);
		ByteArrayOutputStream out    = new ByteArrayOutputStream(data.length);
		byte[]                buffer = new byte[2048];

		while (!inflater.finished()) {
			int count = inflater.inflate(buffer);
			out.write(buffer, 0, count);
		}

		return out.toByteArray();
	}

}
