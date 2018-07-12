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

import org.apache.commons.codec.binary.Base64;
import org.javaweb.core.utils.HttpRequestUtils;
import org.javaweb.core.utils.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {

	/**
	 * 远程服务器的响应内容
	 */
	protected transient String data;

	/**
	 * 请求的URL地址
	 */
	private URL url;

	/**
	 * 发送HTTP请求后从服务器获取的响应状态码
	 */
	private int statusCode;

	/**
	 * 发送HTTP请求后从服务器获取的响应消息内容
	 */
	private String statusMessage;

	/**
	 * 发送HTTP请求后从服务器获取的响应类型
	 */
	private String contentType;

	/**
	 * 远程服务器响应内容的base64编码字符串
	 */
	private String base64Data;

	/**
	 * 网站编码类型
	 */
	private String charset;

	/**
	 * 请求处理后发生的异常信息
	 */
	private String exceptionName;

	/**
	 * 请求开始的时间
	 */
	private long requestTime;

	/**
	 * 远程服务器请求结束后响应时间
	 */
	private long responseTime;

	/**
	 * 请求的远程服务器文件的最后修改时间
	 */
	private long lastModified;

	/**
	 * 远程服务器的IP地址
	 */
	private String ip;

	/**
	 * 远程服务器主机名
	 */
	private String hostName;

	/**
	 * 远程服务器别名
	 */
	private String canonicalHostName;

	/**
	 * 远程服务器的域名地址
	 */
	private String domain;

	/**
	 * Cookie Map
	 */
	private Map<String, String> cookies = new LinkedHashMap<String, String>();

	/**
	 * HTTP原始请求对象
	 */
	private HttpRequest request;

	/**
	 * 远程服务器响应的Header信息
	 */
	private Map<String, List<String>> header;

	public HttpResponse(String url) throws MalformedURLException {
		this.url = new URL(url);
		this.domain = this.url.getHost();
	}

	public HttpResponse(URL url) {
		this.url = url;
		this.domain = url.getHost();
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getBase64Data() {
		return base64Data;
	}

	public void setBase64Data(String base64Data) {
		this.base64Data = base64Data;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getExceptionName() {
		return exceptionName;
	}

	public void setExceptionName(String exceptionName) {
		this.exceptionName = exceptionName;
	}

	public long getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(long requestTime) {
		this.requestTime = requestTime;
	}

	public long getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(long responseTime) {
		this.responseTime = responseTime;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getCanonicalHostName() {
		return canonicalHostName;
	}

	public void setCanonicalHostName(String canonicalHostName) {
		this.canonicalHostName = canonicalHostName;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Map<String, List<String>> getHeader() {
		return header;
	}

	public void setHeader(Map<String, List<String>> header) {
		this.header = header;
	}

	public void dnsParse() throws UnknownHostException {
		InetAddress ia = InetAddress.getByName(this.domain);
		this.setIp(ia.getHostAddress());// ip地址
		this.setHostName(ia.getHostAddress());// 主机名
		this.setCanonicalHostName(ia.getCanonicalHostName());// 别名
	}

	/**
	 * 解析HTTP响应正文中的HTML内容为Document树
	 *
	 * @return
	 */
	public Document parse() {
		return Jsoup.parse(parseBody(), this.getUrl().toString());
	}

	protected String parseBody() {
		return parseBody(null);
	}

	/**
	 * 解析HTML为字符串 body 字符串转换可能会有乱码问题,为了避免直接使用解析后的HTML字符串默认隐藏了 HTML
	 * body,仅提供方便序列化为json的base64字符串作为输出
	 *
	 * @param charset
	 * @return
	 */
	protected String parseBody(String charset) {
		if (this.data == null && StringUtils.isNotEmpty(this.getBase64Data())) {
			String htmlCharset = null;
			byte[] body        = Base64.decodeBase64(this.getBase64Data());

			if (charset != null && Charset.isSupported(charset)) {
				htmlCharset = charset;
			} else {
				htmlCharset = HttpRequestUtils.parseHTMLCharset(body);
			}

			// 编码验证,如果是GB2312可能会出现乱码需转为GBK
			if (Charset.isSupported(htmlCharset)) {
				this.setCharset("GB2312".equalsIgnoreCase(htmlCharset) ? "GBK" : htmlCharset);
			} else {
				this.setCharset("UTF-8");
			}

			try {
				this.data = new String(body, this.getCharset());
			} catch (UnsupportedEncodingException e) {
				// 忽略
			}
		}

		return this.data;
	}

	/**
	 * 获取HTTP原始的html编码后的字符串
	 *
	 * @return
	 */
	public String body() {
		return parseBody();
	}

	public String body(String charset) {
		return parseBody(charset);
	}

	/**
	 * 获取服务器设置的Cookie值
	 *
	 * @return
	 */
	public Map<String, String> getCookies() {
		return cookies;
	}

	/**
	 * 设置元素的Cookie值
	 *
	 * @param cookies
	 */
	public void setCookies(Map<String, String> cookies) {
		this.cookies = cookies;
	}

	/**
	 * 设置cookie
	 *
	 * @param cookieName
	 * @param cookieVal
	 */
	public void cookie(String cookieName, String cookieVal) {
		cookies.put(cookieName, cookieVal);
	}

	/**
	 * 移除Cookie值
	 *
	 * @param key
	 */
	public void removeCookie(String key) {
		cookies.remove(key);
	}

	/**
	 * 获取原始请求对象
	 *
	 * @return
	 */
	public HttpRequest getRequest() {
		return request;
	}

	/**
	 * 设置原始请求对象
	 *
	 * @param request
	 */
	public void setRequest(HttpRequest request) {
		this.request = request;
	}

}