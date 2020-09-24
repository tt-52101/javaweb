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
package org.javaweb.net;

import org.javaweb.utils.HttpRequestUtils;
import org.javaweb.utils.IOUtils;
import org.javaweb.utils.SslUtils;
import org.javaweb.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static org.javaweb.utils.HttpRequestUtils.setResponse;

/**
 * Created by yz on 2017/7/3.
 */
public class MultipartRequest extends HttpRequest {

	private static final String LINE_FEED = "\r\n";

	private final String boundary;

	private Set<MultipartFileField> fileField = new LinkedHashSet<MultipartFileField>();

	public MultipartRequest(String requestURL) throws IOException {
		// 设置内容分割线
		boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
		URL url = new URL(requestURL);
		this.url = url;

		String protocol = this.url.getProtocol();// 获取请求协议

		if (!protocol.equals("http") && !protocol.equals("https")) {
			throw new MalformedURLException("Protocol ERROR!");
		} else if ("https".equalsIgnoreCase(protocol)) {
			SslUtils.ignoreSsl();
		}
	}

	public MultipartRequest method(MultipartRequest.Method method) {
		super.method(method);
		return this;
	}

	public MultipartRequest url(URL url) {
		super.url(url);
		return this;
	}

	public MultipartRequest url(String url) throws MalformedURLException {
		super.url(url);
		return this;
	}

	public MultipartRequest timeout(int timeout) {
		super.timeout(timeout);
		return this;
	}

	public MultipartRequest charset(String charset) {
		super.charset(charset);
		return this;
	}

	public MultipartRequest userAgent(String userAgent) {
		super.userAgent(userAgent);
		return this;
	}

	public MultipartRequest referer(String referer) {
		super.referer(referer);
		return this;
	}

	public MultipartRequest followRedirects(boolean followRedirects) {
		super.followRedirects(followRedirects);
		return this;
	}

	public MultipartRequest header(Map<String, String> requestHeader) {
		super.header(requestHeader);
		return this;
	}

	public MultipartRequest header(String key, String value) {
		super.header(key, value);
		return this;
	}

	public MultipartRequest data(InputStream in) {
		super.data(in);
		return this;
	}

	public MultipartRequest contentType(String contentType) {
		super.contentType(contentType);
		return this;
	}

	public MultipartRequest cookie(String cookie) {
		super.cookie(cookie);
		return this;
	}

	/**
	 * 设置请求的文件域
	 *
	 * @param fileField
	 * @return
	 */
	public MultipartRequest files(Set<MultipartFileField> fileField) {
		this.fileField.addAll(fileField);
		return this;
	}

	/**
	 * 设置请求的文件域
	 *
	 * @param fileField
	 * @return
	 */
	public MultipartRequest file(MultipartFileField fileField) {
		this.fileField.add(fileField);
		return this;
	}

	public MultipartRequest data(String key, String value) {
		fileField.add(new MultipartFileField(key, value));
		return this;
	}

	@Override
	public MultipartRequest data(Map<String, String> requestDataMap) {
		if (requestDataMap != null) {
			this.requestDataMap = requestDataMap;

			// 复制Map为MultipartFileField
			for (String key : requestDataMap.keySet()) {
				String             value = requestDataMap.get(key);
				MultipartFileField field = new MultipartFileField(key, value);
				fileField.add(field);
			}
		}

		return this;
	}

	/**
	 * 设置Form参数
	 *
	 * @param out
	 */
	private void setRequestFormData(OutputStream out) throws IOException {
		if (StringUtils.isNotEmpty(this.fileField)) {
			for (MultipartFileField field : this.fileField) {
				out.write(("--" + boundary + LINE_FEED).getBytes());

				if (field.getFileName() != null) {
					// 初始化ContentType,如果未设置ContentType利用文件名自动猜测ContentType设值
					if (field.getContentType() == null) {
						field.setContentType(HttpURLConnection.guessContentTypeFromName(field.getFileName()));
					}

					out.write(
							("Content-Disposition: form-data; name=\"" + field.getFieldName() + "\"; "
									+ "filename=\"" + field.getFileName() + "\"" + LINE_FEED).getBytes(charset)
					);

					out.write(("Content-Type: " + field.getContentType() + LINE_FEED).getBytes());
					out.write(("Content-Transfer-Encoding: binary" + LINE_FEED).getBytes());
					out.write(LINE_FEED.getBytes());

					try {
						// 传输文件流
						if (field.getFileInputStream() != null && field.getFileInputStream().available() > 0) {
							byte[] bytes = new byte[4096];
							int    a     = 0;

							while ((a = field.getFileInputStream().read(bytes)) != -1) {
								out.write(bytes, 0, a);
								out.flush();
							}
						}
					} finally {
						IOUtils.closeQuietly(field.getFileInputStream());
					}
				} else {
					// 初始化ContentType
					if (field.getContentType() == null) {
						field.setContentType("text/plain");
					}

					out.write(("Content-Disposition: form-data; name=\"" + field.getFieldName() + "\"" + LINE_FEED).getBytes(charset));
					out.write(("Content-Type: " + field.getContentType() + "; charset=" + charset + LINE_FEED).getBytes());
					out.write(LINE_FEED.getBytes());

					if (StringUtils.isNotEmpty(field.getFieldValue())) {
						out.write(field.getFieldValue().getBytes());
					}
				}

				out.write(LINE_FEED.getBytes());
				out.flush();
			}

			out.write(("--" + boundary + "--" + LINE_FEED).getBytes());
			out.flush();
		}
	}

	@Override
	public HttpResponse request() {
		HttpURLConnection httpURLConnection = null;
		InputStream       in                = null;
		HttpResponse      response          = new HttpResponse(this.url);

		try {
			response.setRequestTime(System.currentTimeMillis());// 请求开始时间
			try {
				// 初始化HttpURLConnection
				httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setUseCaches(false);
				httpURLConnection.setDoInput(true);
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setRequestMethod("POST");
				HttpRequestUtils.setRequestProperties(httpURLConnection, this);// 设置请求Header信息
				httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

				OutputStream out = httpURLConnection.getOutputStream();

				setRequestFormData(out);// 设置Form表单域参数
				httpURLConnection.connect();// 建立HTTP连接
				setResponse(httpURLConnection, response);// 设置HTTP响应信息

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

			response.setResponseTime(System.currentTimeMillis());// 请求结束时间
		}

		return response;
	}

}
