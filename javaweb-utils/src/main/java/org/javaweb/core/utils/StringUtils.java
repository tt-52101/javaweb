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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils extends org.apache.commons.lang.StringUtils {

	/**
	 * 对象值非空判断,判断输入对象是否是空值
	 *
	 * @param obj
	 * @return
	 */
	public static boolean isNotEmpty(Object obj) {
		if (obj == null) {
			return false;
		}

		return !"".equals(String.valueOf(obj).trim());
	}

	/**
	 * 验证输入的串是否是数字
	 *
	 * @param str
	 * @return
	 */
	public static boolean isNum(String str) {
		return isNotEmpty(str) ? str.trim().replaceAll("[0-9]+", "").length() == 0 : false;
	}

	/**
	 * 数组切割，把一个数组用分隔符切分成字符串
	 *
	 * @param array
	 * @param separator
	 * @return
	 */
	public static String join(Object[] array, String separator) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < array.length; i++) {
			if (i > 0) {
				sb.append(separator);
			}
			if (array[i] != null) {
				sb.append(array[i]);
			}
		}

		return sb.toString();
	}

	/**
	 * List切割，把一个数组用分隔符切分成字符串
	 *
	 * @param ls
	 * @param separator
	 * @return
	 */
	public static String join(List<?> ls, String separator) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < ls.size(); i++) {
			if (i > 0) {
				sb.append(separator);
			}

			if (ls.get(i) != null) {
				sb.append(ls.get(i));
			}
		}

		return sb.toString();
	}

	/**
	 * 简单的数组包含判断
	 *
	 * @param array
	 * @param objectToFind
	 * @return
	 */
	public static boolean arrayContains(Object[] array, Object objectToFind) {
		for (Object array1 : array) {
			if (array1 != null && ("" + array1).contains("" + objectToFind)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 获取系统换行分割线
	 *
	 * @return
	 */
	public static String getSeparator() {
		if ("\r".equals(System.getProperty("line.separator"))) {
			return "\\r";//linux
		} else if ("\r\n".equals(System.getProperty("line.separator"))) {
			return "\\r\\n";//windows
		} else {
			return "\\n";//mac
		}
	}

	/**
	 * 获取uuid
	 *
	 * @return
	 */
	public static synchronized String getUUID() {
		return UUID.randomUUID().toString().toLowerCase().replaceAll("-", "");
	}

	/**
	 * unicode 转换成 utf-8
	 *
	 * @param str
	 * @return
	 */
	public static String unicodeToUtf8(String str) {
		if (str == null || str.length() < 1 || !Pattern.compile("\\\\u").matcher(str).find()) {
			return str;
		}

		char          aChar;
		int           len       = str.length();
		StringBuilder outBuffer = new StringBuilder(len);

		for (int x = 0; x < len; ) {
			aChar = str.charAt(x++);
			if (aChar == '\\') {
				aChar = str.charAt(x++);
				if (aChar == 'u') {
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = str.charAt(x++);
						switch (aChar) {
							case '0':
							case '1':
							case '2':
							case '3':
							case '4':
							case '5':
							case '6':
							case '7':
							case '8':
							case '9':
								value = (value << 4) + aChar - '0';
								break;
							case 'a':
							case 'b':
							case 'c':
							case 'd':
							case 'e':
							case 'f':
								value = (value << 4) + 10 + aChar - 'a';
								break;
							case 'A':
							case 'B':
							case 'C':
							case 'D':
							case 'E':
							case 'F':
								value = (value << 4) + 10 + aChar - 'A';
								break;
							default:
								throw new IllegalArgumentException(
										"Malformed   \\uxxxx   encoding.");
						}
					}

					outBuffer.append((char) value);
				} else {
					switch (aChar) {
						case 't':
							aChar = '\t';
							break;
						case 'r':
							aChar = '\r';
							break;
						case 'n':
							aChar = '\n';
							break;
						case 'f':
							aChar = '\f';
							break;
						default:
							break;
					}
					outBuffer.append(aChar);
				}
			} else {
				outBuffer.append(aChar);
			}
		}

		return outBuffer.toString();
	}

	/**
	 * 获取当前系统时间
	 *
	 * @return
	 */
	public static String getCurrentTime() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	/**
	 * 帐号正则验证
	 *
	 * @param account
	 * @return
	 */
	public static boolean accountValidate(String account) {
		return Pattern.compile("^[a-zA-Z0-9|-|_]{2,20}$").matcher(account).find();
	}

	/**
	 * 邮箱正则验证
	 *
	 * @param mail
	 * @return
	 */
	public static boolean mailValidate(String mail) {
		return Pattern.compile("^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$").matcher(mail).find();
	}

	/**
	 * 手机正则验证
	 *
	 * @param cellPhone
	 * @return
	 */
	public static boolean cellPhoneValidate(String cellPhone) {
		return Pattern.compile("^1[3|4|5|7|8][0-9]\\d{8}$").matcher(cellPhone).find();
	}

	/**
	 * 字符串首字母大写
	 *
	 * @param str
	 * @return
	 */
	public static String toUpperCaseFirstOne(String str) {
		if (isNotEmpty(str)) {
			char f = str.charAt(0);

			if (Character.isUpperCase(f)) {
				return str;
			} else {
				return str.replaceFirst(String.valueOf(f), String.valueOf(f).toUpperCase());
			}
		}

		return str;
	}

	/**
	 * Exception 转换成字符串
	 *
	 * @param e
	 * @return
	 */
	public static String exceptionToString(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw, true));

		return sw.toString();
	}

	public static String getSpecialRandomString(int length) {
		String[] words = new String[]{"1", "I", "i", "l", "L", "0", "O", "o"};
		return randomString(length, words);
	}

	public static String getRandomString(int length) {
		return randomString(length);
	}

	/**
	 * 产生随机字符串
	 *
	 * @param length 生成的字符串的长度
	 * @param words  不需要包含的字符
	 * @return
	 */
	public static String randomString(int length, String... words) {
		if (length < 1) {
			return null;
		}

		String str = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

		if (words.length > 0) {
			for (String word : words) {
				str = str.replace(word, "");
			}
		}

		Random random = new Random();
		char[] strs   = str.toCharArray();
		char[] chars  = new char[length];

		for (int i = 0; i < chars.length; i++) {
			chars[i] = strs[random.nextInt(strs.length)];
		}

		return new String(chars);
	}

	/**
	 * 计算某个字符串出现的次数
	 *
	 * @param content 字符串片段
	 * @param sub     搜索条件
	 * @param regex   是否支持正则表达式
	 * @return
	 */
	public static int textIndexOfCount(String content, String sub, boolean regex) {
		Matcher matcher = Pattern.compile(regex ? sub : Pattern.quote(sub)).matcher(content);
		int     i       = 0;

		while (matcher.find()) {
			i++;
		}

		return i;
	}

}
