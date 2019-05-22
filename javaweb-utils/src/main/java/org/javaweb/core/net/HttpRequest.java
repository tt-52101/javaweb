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

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by yz on 2017/7/3.
 */
public abstract class HttpRequest {

	/**
	 * 请求方式,默认GET
	 */
	protected String method = "GET";

	/**
	 * 请求的URL地址
	 */
	protected URL url;

	/**
	 * 请求超时设置(读取和写入) 默认15秒
	 */
	protected int timeout = 15000;

	/**
	 * 编码 默认UTF-8
	 */
	protected String charset = "UTF-8";

	/**
	 * 请求的User-Agent
	 */
	protected String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36";

	/**
	 * 请求的来源 Referer地址
	 */
	protected String referer;

	/**
	 * 是否跟随30X跳转
	 */
	protected boolean followRedirects = true;

	/**
	 * 请求头具体的参数
	 */
	protected Map<String, String> requestHeader = new LinkedHashMap<String, String>();

	/**
	 * 请求具体的参数
	 */
	protected Map<String, String> requestDataMap = new LinkedHashMap<String, String>();

	/**
	 * 请求内容的二进制
	 */
	protected byte[] requestBytes;

	/**
	 * 请求内容的流
	 */
	protected InputStream requestInputStream;

	/**
	 * 内容类型
	 */
	protected String contentType;

	/**
	 * Cookie
	 */
	protected String cookie;

	public HttpRequest method(HttpURLRequest.Method method) {
		this.method = method.name();
		return this;
	}

	public HttpRequest url(URL url) {
		this.url = url;
		return this;
	}

	public HttpRequest url(String url) throws MalformedURLException {
		this.url = new URL(url);
		return this;
	}

	public HttpRequest timeout(int timeout) {
		this.timeout = timeout;
		return this;
	}

	public HttpRequest charset(String charset) {
		this.charset = charset;
		return this;
	}

	public HttpRequest userAgent(String userAgent) {
		this.userAgent = userAgent;
		return this;
	}

	public HttpRequest referer(String referer) {
		this.referer = referer;
		return this;
	}

	public HttpRequest followRedirects(boolean followRedirects) {
		this.followRedirects = followRedirects;
		return this;
	}

	public HttpRequest header(Map<String, String> requestHeader) {
		this.requestHeader.putAll(requestHeader);
		return this;
	}

	public HttpRequest header(String key, String value) {
		this.requestHeader.put(key, value);
		return this;
	}

	public HttpRequest data(InputStream in) {
		this.requestInputStream = in;
		return this;
	}

	public HttpRequest data(Map<String, String> requestDataMap) {
		if (requestDataMap != null) {
			this.requestDataMap.putAll(requestDataMap);
		}

		return this;
	}

	public HttpRequest data(byte[] bytes) {
		this.requestBytes = bytes;
		return this;
	}

	public HttpRequest contentType(String contentType) {
		this.requestHeader.put("Content-Type", contentType);
		this.contentType = contentType;

		return this;
	}

	public HttpRequest cookie(String cookie) {
		this.cookie = cookie;
		return this;
	}

	public String getMethod() {
		return method;
	}

	public URL getUrl() {
		return url;
	}

	public int getTimeout() {
		return timeout;
	}

	public String getCharset() {
		return charset;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public String getReferer() {
		return referer;
	}

	public boolean isFollowRedirects() {
		return followRedirects;
	}

	public Map<String, String> getRequestHeader() {
		return requestHeader;
	}

	public Map<String, String> getRequestDataMap() {
		return requestDataMap;
	}

	public byte[] getRequestBytes() {
		return requestBytes;
	}

	public InputStream getRequestInputStream() {
		return requestInputStream;
	}

	public String getContentType() {
		return contentType;
	}

	public String getCookie() {
		return cookie;
	}

	public abstract HttpResponse request();

	public enum Method {
		GET, POST, HEAD, TRACE, PUT, DELETE, OPTIONS, CONNECT
	}

}
