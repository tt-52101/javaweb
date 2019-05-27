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

import javax.servlet.http.HttpServletRequest;

public class CaptchaUtils {

	/**
	 * 验证码存储在Session中的key
	 */
	public static final String ATR_SESSION_TOKEN = "captcha_token";

	/**
	 * 创建一个验证码并放在session中,每次请求都会更新原始的验证码.
	 * Cage默认的的生成的验证码长度是8位，实在是太长了于是替换成了自定义的随机数生成方法.
	 *
	 * @param request
	 * @return
	 */
	public static String generateToken(HttpServletRequest request) {
		String token = StringUtils.getSpecialRandomString(4);
		request.getSession().setAttribute(ATR_SESSION_TOKEN, token);

		return token;
	}

	/**
	 * 从session中取出验证码
	 *
	 * @param request
	 * @return
	 */
	private static String getToken(HttpServletRequest request) {
		return (String) request.getSession().getAttribute(ATR_SESSION_TOKEN);
	}

	/**
	 * 检测验证码是否合法
	 *
	 * @param request
	 * @param token
	 * @return check result
	 */
	public static boolean checkToken(HttpServletRequest request, String token) throws IllegalArgumentException {
		if (StringUtils.isEmpty(token)) {
			return false;
		}

		boolean isValid = token.equalsIgnoreCase(getToken(request));
		request.getSession().removeAttribute(ATR_SESSION_TOKEN);

		return isValid;
	}

}
