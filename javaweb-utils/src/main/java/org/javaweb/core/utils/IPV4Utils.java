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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class IPV4Utils {

	public static final String PATTERN_255 = "(?:25[0-5]|2[0-4][0-9]|[1]?[0-9][0-9]?)";

	public static final Pattern PATTERN_IPV4 = Pattern.compile("^(?:" + PATTERN_255 + "\\.){3}" + PATTERN_255 + "$");

	private static final Logger LOG = Logger.getLogger("info");

	/**
	 * 判断是否是内网IP
	 *
	 * @param ip
	 * @return
	 */
	public static boolean isLanIp(String ip) {
		try {
			return InetAddress.getByName(ip).isSiteLocalAddress();
		} catch (UnknownHostException ex) {
			return false;
		}
	}

	/**
	 * IPV4地址转long
	 *
	 * @param ip
	 * @return
	 */
	public static long ipToLong(String ip) {
		String[] strs = ip.split("\\.");
		return (Long.parseLong(strs[0]) << 24)
				+ (Integer.parseInt(strs[1]) << 16)
				+ (Integer.parseInt(strs[2]) << 8) + Integer.parseInt(strs[3]);
	}

	/**
	 * long 转IPV4
	 *
	 * @param longIp
	 * @return
	 */
	public static String longToIP(long longIp) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.valueOf((longIp >>> 24))).append(".");
		sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16)).append(".");
		sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8)).append(".");
		sb.append(String.valueOf((longIp & 0x000000FF)));

		return sb.toString();
	}

	/**
	 * 判断是否是一个合法的IP地址
	 *
	 * @param ip
	 * @return
	 */
	public static boolean isValid(String ip) {
		if (StringUtils.isNotEmpty(ip)) {
			return PATTERN_IPV4.matcher(ip).matches();
		}

		return false;
	}

	/**
	 * 获取本地IP地址 主机名无法ping通的情况下可以用此方法获取本地IP地址。
	 *
	 * @return InetAddress
	 * @throws UnknownHostException
	 */
	public static InetAddress getLocalHostLANAddress() throws UnknownHostException {
		try {
			InetAddress candidateAddress = null;
			for (Enumeration<?> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
				NetworkInterface iface = (NetworkInterface) ifaces.nextElement();

				for (Enumeration<?> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
					InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();

					if (!inetAddr.isLoopbackAddress()) {
						if (inetAddr.isSiteLocalAddress()) {
							return inetAddr;
						} else if (candidateAddress == null) {
							candidateAddress = inetAddr;
						}
					}
				}
			}
			if (candidateAddress != null) {
				return candidateAddress;
			}

			InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();

			if (jdkSuppliedAddress == null) {
				throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
			}

			return jdkSuppliedAddress;
		} catch (Exception e) {
			UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
			unknownHostException.initCause(e);
			throw unknownHostException;
		}
	}

	/**
	 * 获取本机MAC地址
	 *
	 * @return
	 */
	public static String getLocalMacAddress() {
		StringBuilder sb = new StringBuilder();
		try {
			byte[] mac = NetworkInterface.getByInetAddress(getLocalHostLANAddress()).getHardwareAddress();

			for (int i = 0; i < mac.length; i++) {
				if (i > 0) {
					sb.append("-");
				}

				int    temp = mac[i] & 0xff;
				String str  = Integer.toHexString(temp);

				if (str.length() == 1) {
					sb.append("0").append(str);
				} else {
					sb.append(str);
				}
			}
		} catch (Exception e) {
			LOG.info("无法获取本机MAC地址,MAC地址为空.");
		}

		return sb.toString();
	}

}