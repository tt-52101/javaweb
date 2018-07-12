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

import org.javaweb.core.utils.HttpRequestUtils;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class HttpURLRequest extends HttpRequest {

	/**
	 * 最大的body限制
	 */
	private int maxBodySizeBytes = 5 * 1024 * 1024;

	/**
	 * 请求的字符串数据
	 */
	private String requestData;

	public HttpURLRequest() {

	}

	public HttpURLRequest(URL url) {
		this.url = url;
	}

	public HttpURLRequest(String url) throws MalformedURLException {
		this.url = new URL(url);
	}

	public HttpURLRequest maxBodySizeBytes(int maxBodySizeBytes) {
		this.maxBodySizeBytes = maxBodySizeBytes;
		return this;
	}

	public HttpURLRequest method(HttpURLRequest.Method method) {
		super.method(method);
		return this;
	}

	public HttpURLRequest url(URL url) {
		super.url(url);
		return this;
	}

	public HttpURLRequest url(String url) throws MalformedURLException {
		super.url(url);
		return this;
	}

	public HttpURLRequest timeout(int timeout) {
		super.timeout(timeout);
		return this;
	}

	public HttpURLRequest charset(String charset) {
		super.charset(charset);
		return this;
	}

	public HttpURLRequest userAgent(String userAgent) {
		super.userAgent(userAgent);
		return this;
	}

	public HttpURLRequest referer(String referer) {
		super.referer(referer);
		return this;
	}

	public HttpURLRequest followRedirects(boolean followRedirects) {
		super.followRedirects(followRedirects);
		return this;
	}

	public HttpURLRequest header(Map<String, String> requestHeader) {
		super.header(requestHeader);
		return this;
	}

	public HttpURLRequest header(String key, String value) {
		super.header(key, value);
		return this;
	}

	public HttpURLRequest data(String requestData) {
		this.requestData = requestData;
		return this;
	}

	public HttpURLRequest data(String key, String value) {
		this.requestDataMap.put(key, value);
		return this;
	}

	public HttpURLRequest data(InputStream in) {
		super.data(in);
		return this;
	}

	public HttpURLRequest data(byte[] bytes) {
		super.data(bytes);
		return this;
	}

	public HttpURLRequest contentType(String contentType) {
		super.contentType(contentType);
		return this;
	}

	public HttpURLRequest cookie(String cookie) {
		super.cookie(cookie);
		return this;
	}

	public int getMaxBodySizeBytes() {
		return maxBodySizeBytes;
	}

	public String getRequestData() {
		return requestData;
	}

	/**
	 * 发送HTTP GET 请求
	 *
	 * @return
	 */
	public HttpResponse get() {
		this.method(Method.GET);
		return HttpRequestUtils.httpRequest(this);
	}

	/**
	 * 发送HTTP POST 请求
	 *
	 * @return
	 */
	public HttpResponse post() {
		this.method(Method.POST);
		return HttpRequestUtils.httpRequest(this);
	}

	@Override
	public HttpResponse request() {
		return HttpRequestUtils.httpRequest(this);
	}

}
