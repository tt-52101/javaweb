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

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.HexDump;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 十六进制工具类， String、Hex、byte转换
 *
 * @author yz
 */
public class HexUtils extends Hex {

	/**
	 * byte数组转十六进制字符串
	 *
	 * @param bytes byte[]
	 * @return String
	 */
	public static String bytes2HexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			sb.append(hex.length() == 1 ? '0' + hex : hex.toUpperCase());
		}

		return sb.toString();
	}

	/**
	 * String转十六进制字符串
	 *
	 * @param s
	 * @return
	 */
	public static String string2Hex(String s) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < s.length(); i++) {
			sb.append(Integer.toHexString((int) s.charAt(i)));
		}

		return sb.toString();
	}

	/**
	 * 十六进制字符串转ascii字符
	 *
	 * @param str
	 * @return
	 */
	public static String hex2String(String str) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < str.length() - 1; i += 2) {
			String s = String.valueOf(str.charAt(i)) + str.charAt(i + 1);
			sb.append((char) Integer.parseInt(s, 16));
		}

		return sb.toString();
	}

	/**
	 * 将十六进制字符串转换为字节数组
	 *
	 * @param s
	 * @return
	 */
	public static byte[] hex2Bytes(String s) {
		byte[] bytes = new byte[s.length() / 2];

		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2), 16);
		}

		return bytes;
	}

	/**
	 * Hexdump 输出规范的十六进制和ASCII码
	 *
	 * @param data
	 * @return
	 */
	public static byte[] hexDump(byte[] data) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			HexDump.dump(data, data.length, baos, 0);
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return baos.toByteArray();
	}

	/**
	 * SqlServer hexSQL语句中的时间转字符串
	 * 如:select CAST(0x00009EB001628DF4 AS DateTime) 转换为
	 * select CAST('2011-03-24 21:30:53.613' AS DateTime)
	 *
	 * @param sqlLine
	 * @return
	 * @throws ParseException
	 */
	public static String replaceHexWithDate(String sqlLine) throws ParseException {
		Pattern castPattern = Pattern.compile("(CAST\\()(0x[A-Fa-f0-9]{16})( AS DateTime\\))");
		Matcher m           = castPattern.matcher(sqlLine);

		while (m.find()) {
			String s = m.group(2);
			sqlLine = sqlLine.replace(s, "'" + sqlServerHexToSqlDate(s) + "'");
		}

		return sqlLine;
	}

	/**
	 * SqlServer hex时间转字符串
	 *
	 * @param hexString
	 * @return
	 * @throws ParseException
	 */
	public static String sqlServerHexToSqlDate(String hexString) throws ParseException {
		return sqlServerHexToSqlDate(hexString, "yyyy-MM-dd HH:mm:ss.SSS");
	}

	/**
	 * SqlServer hex时间转字符串
	 * 参考:http://stackoverflow.com/questions/4946292/how-to-cast-the-hexadecimal-to-varchardatetime
	 *
	 * @param hexString
	 * @param dateFormat 时间格式化 如:yyyy-MM-dd HH:mm:ss
	 * @return
	 * @throws ParseException
	 */
	public static String sqlServerHexToSqlDate(String hexString, String dateFormat) throws ParseException {
		String           hexNumber    = hexString.substring(2);// 删除0x
		String           dateHex      = hexNumber.substring(0, 8);
		String           timeHex      = hexNumber.substring(8, 16);
		long             daysToAdd    = Long.parseLong(dateHex, 16);// 1900年1月1日至今天的天数
		long             millisToAdd  = Long.parseLong(timeHex, 16) * 10 / 3;
		SimpleDateFormat sdf          = new SimpleDateFormat(dateFormat);
		Calendar         startingCal  = Calendar.getInstance();
		String           startingDate = "1900-01-01 00:00:00.000";

		startingCal.setTime(sdf.parse(startingDate));

		Calendar convertedCal = Calendar.getInstance();
		convertedCal.setTime(sdf.parse(startingDate));
		convertedCal.add(Calendar.DATE, (int) daysToAdd);
		convertedCal.setTimeInMillis(convertedCal.getTimeInMillis() + millisToAdd);

		return sdf.format(convertedCal.getTime());
	}

}
