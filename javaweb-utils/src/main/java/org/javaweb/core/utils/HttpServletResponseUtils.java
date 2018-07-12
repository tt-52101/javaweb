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

import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

public class HttpServletResponseUtils {

	public static void responseJson(HttpServletResponse response, String text) {
		response(response, "application/json;charset=UTF-8", text);
	}

	public static void responseJson(HttpServletResponse response, Object obj) {
		response(response, "application/json;charset=UTF-8", JSON.toJSONString(obj));
	}

	public static void responseXml(HttpServletResponse response, String text) {
		response(response, "text/xml;charset=UTF-8", text);
	}

	public static void responseJS(HttpServletResponse response, String text) {
		response(response, "application/javascript;charset=UTF-8", text);
	}

	public static void responseRc4Json(HttpServletResponse response, Object obj, String RC4Key) {
		try {
			response(response, "application/json;charset=UTF-8", new String(Base64.encodeBase64(EncryptUtils.encryptionRC4Byte(JSON.toJSONString(obj), RC4Key)), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public static void responseHTML(HttpServletResponse response, String text) {
		response(response, "text/html;charset=UTF-8", text);
	}

	public static void responseText(HttpServletResponse response, String text) {
		response(response, "text/plain;charset=UTF-8", text);
	}

	public static void download(HttpServletResponse response, File file) throws IOException {
		download(response, file, null);
	}

	public static void download(HttpServletResponse response, File file, String fileName) throws IOException {
		FileInputStream in = null;

		try {
			if (file.exists() && file.canRead()) {
				FileInputStream fis  = null;
				ImageInfo       info = null;

				try {
					fis = new FileInputStream(file);
					info = ImageUtils.getImageInfo(fis);
				} catch (IOException e) {
					// 忽略
				} finally {
					IOUtils.closeQuietly(fis);
				}

				fileName = (fileName != null ? fileName : file.getName());

				if (info != null && (info.getHeight() > 0 || info.getWidth() > 0)) {
					response.setContentType(info.getMimeType());
				} else {
					response.setContentType("application/octet-stream");
					response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
				}

				in = new FileInputStream(file);
				OutputStream out   = response.getOutputStream();
				byte[]       bytes = new byte[4096];
				int          a     = -1;

				while ((a = in.read(bytes)) != -1) {
					out.write(bytes, 0, a);
				}

				out.flush();
				IOUtils.closeQuietly(out);
			} else {
				responseText(response, "文件不存在!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	/**
	 * 响应二进制字节内容
	 *
	 * @param response
	 * @param contentType
	 * @param bytes
	 * @throws IOException
	 */
	public static void response(HttpServletResponse response, String contentType, byte[] bytes) throws IOException {
		response.setContentType(contentType);
		OutputStream out = response.getOutputStream();
		out.write(bytes);
		out.flush();
		IOUtils.closeQuietly(out);
	}

	/**
	 * 响应输入流内容
	 *
	 * @param response
	 * @param contentType
	 * @param in
	 * @throws IOException
	 */
	public static void response(HttpServletResponse response, String contentType, InputStream in) throws IOException {
		try {
			response.setContentType(contentType);
			OutputStream out   = response.getOutputStream();
			byte[]       bytes = new byte[4096];
			int          a     = -1;

			while ((a = in.read(bytes)) != -1) {
				out.write(bytes, 0, a);
			}

			out.flush();
			IOUtils.closeQuietly(out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	/**
	 * 响应字符串内容渲染
	 *
	 * @param response
	 * @param contentType
	 * @param text
	 */
	public static void response(HttpServletResponse response, String contentType, String text) {
		response.setContentType(contentType);

		if (text != null) {
			PrintWriter out = null;

			try {
				out = response.getWriter();
				out.write(text);
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
