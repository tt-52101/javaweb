/*
 * Copyright yz 2017-12-20 Email:admin@javaweb.org.
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 简单的图片处理工具类,只支持JPG、GIF、PNG、BMP、TIFF格式的图片
 */
public class ImageUtils {

	/**
	 * 获取图片基本信息
	 *
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static ImageInfo getImageInfo(File file) throws IOException {
		return new ImageInfo(file);
	}

	/**
	 * 获取图片基本信息
	 *
	 * @param bytes
	 * @return
	 * @throws IOException
	 */
	public static ImageInfo getImageInfo(byte[] bytes) throws IOException {
		return new ImageInfo(bytes);
	}

	/**
	 * 获取图片基本信息
	 *
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static ImageInfo getImageInfo(InputStream in) throws IOException {
		return new ImageInfo(in);
	}

	/**
	 * 判断是否是图片
	 *
	 * @param file
	 * @return
	 */
	public static boolean isImage(File file) {
		try {
			ImageInfo info = new ImageInfo(file);
			return info.getWidth() > 0 || info.getHeight() > 0;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * 判断是否是图片
	 *
	 * @param bytes
	 * @return
	 */
	public static boolean isImage(byte[] bytes) {
		try {
			ImageInfo info = new ImageInfo(bytes);
			return info.getWidth() > 0 || info.getHeight() > 0;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * 判断是否是图片
	 *
	 * @param in
	 * @return
	 */
	public static boolean isImage(InputStream in) {
		try {
			ImageInfo info = new ImageInfo(in);
			return info.getWidth() > 0 || info.getHeight() > 0;
		} catch (IOException e) {
			return false;
		}
	}

}
