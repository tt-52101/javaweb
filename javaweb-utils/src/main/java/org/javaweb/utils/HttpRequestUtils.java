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

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.javaweb.net.HttpRequest;
import org.javaweb.net.HttpResponse;
import org.javaweb.net.HttpURLRequest;
import org.javaweb.net.MultipartRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.javaweb.utils.StringUtils.isNotEmpty;

public class HttpRequestUtils {

	private static final Pattern HTML_CHARSET_PATTERN =
			Pattern.compile("(?i)<meta.*\\bcharset\\s*=\\s*(?:\"|')?([^\\s,;\"']*)");

	/**
	 * 获取HTTP请求的文件类型,截取URI后缀部分
	 *
	 * @param url
	 * @return
	 */
	public static String getFileType(URL url) {
		String path = "".equals(url.getPath()) ? "/" : url.getPath();
		String file = path.substring(path.lastIndexOf("/"));

		return file.substring(file.lastIndexOf(".") + 1);
	}

	/**
	 * 循环遍历Map所有元素拼成HTTP QueryString
	 *
	 * @param args
	 * @param encoding
	 * @return
	 */
	public static String toURLParameterString(Map<String, String> args, String encoding) {
		StringBuilder sb = new StringBuilder();

		if (args != null) {
			int i = 0;

			for (String a : args.keySet()) {
				try {
					String value = args.get(a);

					if (i > 0) {
						sb.append("&");
					}

					i++;
					sb.append(a).append("=").append(value != null ? URLEncoder.encode(value, encoding):"");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();
	}

	/**
	 * 从HTML meta 标签里 提取网页编码(charset)
	 *
	 * @param html
	 * @return
	 */
	public static String getCharsetFromHTMLBody(String html) {
		String encoding = null;

		if (html != null) {
			Matcher m = HTML_CHARSET_PATTERN.matcher(html);

			if (m.find()) {
				String charset = m.group(1).trim();
				charset = charset.replace("charset=", "");

				if (charset.length() == 0) {
					return null;
				}

				try {
					if (Charset.isSupported(charset)) {
						encoding = charset.toUpperCase(Locale.ENGLISH);
					}
				} catch (IllegalCharsetNameException e) {
					return null;
				}
			}
		}

		return encoding;
	}

	/**
	 * 解析HTML编码 这个方法只做了从HTML代码里提取出meta标签里面的charset和编码自动识别
	 * 如果需要实现更复杂的编码识别可以参考org.jsoup.helper.DataUtil解析字节流编码
	 *
	 * @param bodyByte
	 * @return
	 */
	public static String parseHTMLCharset(byte[] bodyByte) {
		String encoding = HttpRequestUtils.getCharsetFromHTMLBody(new String(bodyByte));

		if (encoding == null) {
			int code = new BytesEncodingDetect().detectEncoding(bodyByte);
			encoding = BytesEncodingDetect.htmlname[code];
		}

		return encoding != null ? encoding : "UTF-8";
	}

	/**
	 * 设置HTTP请求基本属性 设置HTTP读写超时时间、是否跳转跟随、User-Agent、Referer
	 *
	 * @param connection
	 * @param request
	 * @throws IOException
	 */
	public static void setRequestProperties(HttpURLConnection connection, HttpRequest request) throws IOException {
		connection.setConnectTimeout(request.getTimeout());
		connection.setReadTimeout(request.getTimeout());
		HttpURLConnection.setFollowRedirects(request.isFollowRedirects());

		if (isNotEmpty(request.getMethod()) && !(request instanceof MultipartRequest)) {
			// 非GET请求设置DoInput、DoOutput
			if (!"GET".equalsIgnoreCase(request.getMethod())) {
				connection.setDoInput(true);
				connection.setDoOutput(true);
			}

			connection.setRequestMethod(request.getMethod().toUpperCase());
		}

		// 设置Cookie
		if (isNotEmpty(request.getCookie())) {
			connection.setRequestProperty("Cookie", request.getCookie());
		}

		// 设置User-Agent
		if (isNotEmpty(request.getUserAgent())) {
			connection.setRequestProperty("User-Agent", request.getUserAgent());
		}

		// 设置Referer
		if (isNotEmpty(request.getReferer())) {
			connection.setRequestProperty("Referer", request.getReferer());
		}
	}

	/**
	 * 设置Http请求数据
	 *
	 * @param connection
	 * @param request
	 * @param data
	 * @throws IOException
	 */
	public static void setRequestData(HttpURLConnection connection,
									  HttpURLRequest request, String data) throws IOException {

		// 设置HTTP请求头
		Map<String, String> headers = request.getRequestHeader();

		for (String key : headers.keySet()) {
			connection.setRequestProperty(key, headers.get(key));
		}

		// 设置HTTP非GET请求参数
		if (isNotEmpty(request.getMethod()) && !"GET".equalsIgnoreCase(request.getMethod())) {
			if (data != null || request.getRequestBytes() != null || request.getRequestInputStream() != null) {
				OutputStream out = connection.getOutputStream();

				// 输出请求数据,优先级:byte[]->InputStream->String
				if (request.getRequestBytes() != null) {
					out.write(request.getRequestBytes());
				} else if (request.getRequestInputStream() != null) {
					InputStream in    = request.getRequestInputStream();
					byte[]      bytes = new byte[2048];
					int         a     = 0;

					while ((a = in.read(bytes)) != -1) {
						out.write(bytes, 0, a);
					}
				} else {
					out.write(data.getBytes(request.getCharset()));
				}

				out.flush();
				out.close();
			}
		}
	}

	/**
	 * HTTP请求发送成功后设置HttpResponse
	 *
	 * @param httpURLConnection
	 * @param response
	 * @throws IOException
	 */
	public static void setResponse(HttpURLConnection httpURLConnection, HttpResponse response) throws IOException {
		response.setStatusCode(httpURLConnection.getResponseCode());
		response.setStatusMessage(httpURLConnection.getResponseMessage());
		response.setContentType(httpURLConnection.getContentType());
		response.setHeader(new CaseInsensitiveMap(httpURLConnection.getHeaderFields()));
		response.setLastModified(httpURLConnection.getLastModified());

		// 设置服务器返回的Cookie值
		setCookies(response);
	}

	/**
	 * 设置Cookie
	 *
	 * @param response
	 */
	private static void setCookies(HttpResponse response) {
		String cookieString = response.getRequest().getCookie();

		if (isNotEmpty(cookieString)) {
			// 记录原始的Cookie值字符串到响应的Cookie中，并生成Cookie Map
			String[]            cookies   = cookieString.split(";\\s?");
			Map<String, String> cookieMap = new LinkedHashMap<String, String>();

			for (String cookie : cookies) {
				String[] temp = cookie.split("=");

				if (temp.length == 2) {
					String key = temp[0];
					String val = temp[1];
					cookieMap.put(key, val);
				} else if (temp.length == 1) {
					cookieMap.put(temp[0], "");
				}
			}

			// 设置原始Http请求的Cookie值
			response.setCookies(cookieMap);
		}

		if (response.getHeader() == null) {
			return;
		}

		// 处理服务器返回的Cookie值,删除或者修改
		for (String name : response.getHeader().keySet()) {
			if (StringUtils.isEmpty(name) || !"Set-Cookie".equalsIgnoreCase(name)) {
				continue;
			}

			List<String> values = response.getHeader().get(name);
			for (String value : values) {
				if (value == null) {
					continue;
				}

				String[] strs = value.split(";");

				if (strs.length > 0) {
					String[] temp = strs[0].split("=");

					if (temp.length == 2) {
						String key = temp[0];
						String val = temp[1];

						if (val.equalsIgnoreCase("deleted")) {
							response.removeCookie(key);// 删除Cookie值
						} else {
							response.cookie(key, val);// 添加Cookie值
						}
					} else if (temp.length == 1) {
						response.cookie(temp[0], "");// 添加Cookie值
					}
				}
			}
		}
	}

	/**
	 * 发送HTTP请求
	 *
	 * @param request
	 * @return
	 */
	public static HttpResponse httpRequest(HttpURLRequest request) {
		HttpURLConnection httpURLConnection = null;
		InputStream       in                = null;
		HttpResponse      response          = new HttpResponse(request.getUrl());

		try {
			response.setRequestTime(System.currentTimeMillis());// 请求开始时间

			try {
				String protocol = request.getUrl().getProtocol();// 获取请求协议

				if (!protocol.equals("http") && !protocol.equals("https")) {
					throw new MalformedURLException("Protocol ERROR!");
				} else if ("https".equalsIgnoreCase(protocol)) {
					SslUtils.ignoreSsl();
				}

				String data = null;

				if (request.getRequestDataMap() != null && request.getRequestDataMap().size() > 0) {
					data = HttpRequestUtils.toURLParameterString(request.getRequestDataMap(), request.getCharset());
				} else {
					data = request.getRequestData();
				}

				URL url = request.getUrl();

				if ("GET".equalsIgnoreCase(request.getMethod()) && isNotEmpty(data)) {
					url = new URL(request.getUrl() + (isNotEmpty(request.getUrl().getQuery()) ? "&" : "?") + data);
				}

				httpURLConnection = (HttpURLConnection) url.openConnection();

				// 设置请求信息
				setRequestProperties(httpURLConnection, request);

				// 设置请求参数
				setRequestData(httpURLConnection, request, data);

				httpURLConnection.connect();

				// 处理30X不同协议HttpURLConnection不自定跳转问题
				if (request.isFollowRedirects() && request.getMaxRedirect() < 5) {
					String location = httpURLConnection.getHeaderField("Location");

					if (isNotEmpty(location) && location.toLowerCase().startsWith("http")) {
						URL redirectURL = new URL(location);

						if (!protocol.equals(redirectURL.getProtocol())) {
							return httpRequest(new HttpURLRequest(redirectURL, request.getMaxRedirect() + 1));
						}
					}
				}

				response.setRequest(request);

				// 设置HTTP响应信息
				setResponse(httpURLConnection, response);

				// 获取HTTP请求响应内容
				try {
					in = httpURLConnection.getInputStream();
				} catch (IOException e) {
					in = httpURLConnection.getErrorStream();
				}

				if (in != null) {
					response.setBodyBytes(IOUtils.toByteArray(in));
				}
			} catch (UnknownHostException e) {
				response.setException(e);
			}
		} catch (IOException e) {
			response.setException(e);
		} finally {
			IOUtils.closeQuietly(in);

			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}

			// 请求结束时间
			response.setResponseTime(System.currentTimeMillis());
		}

		return response;
	}

}
