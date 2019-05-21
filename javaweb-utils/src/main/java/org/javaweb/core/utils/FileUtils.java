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
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

public class FileUtils extends org.apache.commons.io.FileUtils {

	protected static FileUtils fileUtils = new FileUtils();

	/**
	 * 获取文件后缀
	 *
	 * @param str
	 * @return
	 */
	public static String getFileSuffix(String str) {
		return str.substring(str.lastIndexOf(".") + 1);
	}

	/**
	 * 获取包下资源文件路径
	 *
	 * @param path
	 * @return
	 */
	public static URL getPackageResourcePath(String path) {
		return fileUtils.getClass().getClassLoader().getResource(path);
	}

	/**
	 * 获取当前文件目录
	 *
	 * @return
	 */
	public static File getCurrentDirectory() {
		File file = new File(".");

		if (file.getParent() != null) {
			return file.getParentFile();
		}

		return new File(System.getProperty("user.dir"));
	}

	/**
	 * 获取包下资源文件输入流
	 *
	 * @param path
	 * @return
	 */
	public static InputStream getPackageResourceAsStream(String path) {
		return fileUtils.getClass().getResourceAsStream(path);
	}

	/**
	 * 读取 Properties 文件
	 *
	 * @param f
	 * @return
	 * @throws FileNotFoundException
	 */
	public static Properties getProperties(File f) throws FileNotFoundException {
		return getProperties(new FileInputStream(f));
	}

	/**
	 * 从jar文件中获取资源输入流对象
	 *
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static InputStream getResourceFromJarFile(URL url) throws IOException {
		URLConnection urlConnection = url.openConnection();

		if (urlConnection instanceof JarURLConnection) {
			JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
			return jarConnection.getInputStream();
		} else {
			throw new IOException("URL 协议类型必须是JarURLConnection.");
		}
	}

	/**
	 * 数据流转换成 Properties 文件
	 *
	 * @param in
	 * @return
	 */
	public static Properties getProperties(InputStream in) {
		Properties p = new Properties();

		try {
			p.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				IOUtils.closeQuietly(in);
			}
		}

		return p;
	}

	/**
	 * 输出整理后的标准文件路径切分
	 *
	 * @param path
	 * @return
	 */
	public static String fileSplitHandle(String path) {
		if (StringUtils.isNotEmpty(path)) {
			return (path.trim().replaceAll("\\\\", "/") + "/").replaceAll("/+", "/");
		} else {
			return path;
		}
	}

}
