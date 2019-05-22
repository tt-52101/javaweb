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
package org.javaweb.core.net;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.javaweb.core.utils.IOUtils;
import org.javaweb.core.utils.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;

/**
 * Http响应解析器
 * Created by yz on 2017/1/5.
 */
public class HttpResponseParser {

	/**
	 * 最大允许读取的Header长度
	 */
	private static final int MAX_HEADER_LENGTH = 1024 * 1000 * 4;

	/**
	 * Http版本号
	 */
	private String httpVersion;

	/**
	 * Http响应状态码
	 */
	private int httpStatus = -1;

	/**
	 * Http响应状态消息
	 */
	private String httpStatusInfo;

	/**
	 * Http响应状态Map
	 */
	private Map<String, Object> headerMap = new CaseInsensitiveMap();

	/**
	 * Http响应状态字符串
	 */
	private String header;

	/**
	 * 响应流
	 */
	private DataInputStream dis;

	/**
	 * 解析的主体输入流
	 */
	private InputStream httpBodyInputStream;

	private static Pattern responsePattern = Pattern.compile("^(HTTP/\\d\\.\\d) (\\d{3}) (.*)$");

	public HttpResponseParser(InputStream in) {
		this.dis = new DataInputStream(in);
		parse();
	}

	/**
	 * 获取最大允许接收的Http响应头长度
	 *
	 * @return
	 */
	public static int getMaxHeaderLength() {
		return MAX_HEADER_LENGTH;
	}

	/**
	 * 获取Http版本号
	 *
	 * @return
	 */
	public String getHttpVersion() {
		return httpVersion;
	}

	/**
	 * 获取Http响应状态码
	 *
	 * @return
	 */
	public int getHttpStatus() {
		return httpStatus;
	}

	/**
	 * Http响应状态消息
	 *
	 * @return
	 */
	public String getHttpStatusInfo() {
		return httpStatusInfo;
	}

	/**
	 * 获取Http响应状态Map
	 *
	 * @return
	 */
	public Map<String, Object> getHeaderMap() {
		return headerMap;
	}

	/**
	 * 获取Http响应状态字符串
	 *
	 * @return
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * 获取Http响应正文输入流
	 *
	 * @return
	 */
	public InputStream getHttpBodyInputStream() {
		return httpBodyInputStream;
	}

	/**
	 * 解析请求响应内容
	 */
	private void parse() {
		try {
			parseHttpResponseHeaderString();
			parseHttpResponseStatus();
			parseHttpResponseHeaderMap();
			parseHttpResponseBodyInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解析Http响应头为字符串
	 *
	 * @throws IOException
	 */
	private void parseHttpResponseHeaderString() {
		boolean       loop = true;
		int           a    = 0;
		StringBuilder sb   = new StringBuilder();
		try {
			String str = null;
			while (loop && (str = dis.readLine()) != null) {
				a += str.length();// 不计换行符长度

				if (a > MAX_HEADER_LENGTH) {
					loop = false;
				} else {
					if (str.length() == 0) {
						loop = false;
					} else {
						sb.append(str).append("\n");
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.header = sb.toString();
	}

	/**
	 * 解析Http响应头为Map
	 *
	 * @throws IOException
	 */
	private void parseHttpResponseHeaderMap() throws IOException {
		if (StringUtils.isNotEmpty(this.header)) {
			BufferedReader br = null;

			try {
				String str = null;
				br = new BufferedReader(new StringReader(this.header));

				while ((str = br.readLine()) != null) {
					int i = str.indexOf(":");

					if (i != -1) {
						String key   = i > 0 ? str.substring(0, i) : "";
						String value = str.length() - 1 > i ? str.substring(i + 1).trim() : "";

						if (!headerMap.containsKey(key)) {
							headerMap.put(key, value);
						} else {
							Object obj = headerMap.get(key);

							if (obj instanceof String) {
								headerMap.put(key, new String[]{(String) obj, value});
							} else {
								List<String> list = new ArrayList<String>();
								list.addAll(Arrays.asList((String[]) obj));
								list.add(value);
								headerMap.put(key, list.toArray(new String[list.size()]));
							}
						}
					}
				}
			} catch (IOException e) {
				throw e;
			} finally {
				IOUtils.closeQuietly(br);
			}
		}
	}

	/**
	 * 解析Http响应状态码
	 *
	 * @throws IOException
	 */
	private void parseHttpResponseStatus() throws IOException {
		try {
			if (StringUtils.isNotEmpty(this.header)) {
				StringReader sr                 = new StringReader(this.header);
				String       responseStatusLine = new BufferedReader(sr).readLine();

				if (StringUtils.isNotEmpty(responseStatusLine)) {
					Matcher m = responsePattern.matcher(responseStatusLine);

					if (m.find() && m.groupCount() == 3) {
						this.httpVersion = m.group(1);
						this.httpStatus = StringUtils.isNum(m.group(2)) ? Integer.parseInt(m.group(2)) : -1;
						this.httpStatusInfo = m.group(3);
					}
				}
			}
		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 * 解析Http响应流 如果响应头中包含了Transfer-Encoding 和 Content-Encoding 尝试解chunked和压缩流
	 *
	 * @throws IOException
	 */
	private void parseHttpResponseBodyInputStream() throws IOException {
		InputStream bodyInputStream = null;

		// 解析chunked
		if (this.headerMap.containsKey("Transfer-Encoding")) {
			String transferEncoding = (String) this.headerMap.get("Transfer-Encoding");

			if (StringUtils.isNotEmpty(transferEncoding) && "chunked".equalsIgnoreCase(transferEncoding)) {
				bodyInputStream = new ChunkedInputStream(dis);
			} else {
				bodyInputStream = dis;
			}
		} else {
			bodyInputStream = dis;
		}

		// 解析gzip和deflate
		if (this.headerMap.containsKey("Content-Encoding")) {
			String      contentEncoding     = (String) this.headerMap.get("Content-Encoding");
			InputStream compressInputStream = null;

			try {
				if ("gzip".equalsIgnoreCase(contentEncoding)) {
					compressInputStream = new GZIPInputStream(bodyInputStream);
				} else if ("deflate".equalsIgnoreCase(contentEncoding)) {
					compressInputStream = new DeflaterInputStream(bodyInputStream);
				}
			} catch (IOException e) {
				throw e;
			}

			if (compressInputStream != null) {
				bodyInputStream = compressInputStream;
			}
		}

		this.httpBodyInputStream = bodyInputStream;
	}

}
