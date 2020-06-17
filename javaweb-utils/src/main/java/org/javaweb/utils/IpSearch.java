package org.javaweb.utils;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;

/**
 * qqzeng IP解析库 https://www.qqzeng.com/
 */
public class IpSearch {

	private static int[] prefStart = new int[256];

	private static int[] prefEnd = new int[256];

	private static long[] endArr;

	private static String[] addrArr;

	private static byte[] data;

	private static String ipFileName = "qqzeng-ip-3.0-ultimate.dat";

	private static File ipFile = new File(FileUtils.getCurrentDirectory(), ipFileName);

	static {
		try {
			InputStream in = IPLocationUtils.class.getClass().getResourceAsStream("/" + ipFileName);
			if (in != null) {
				data = IOUtils.toByteArray(in);
			}

			if (data == null && ipFile.exists()) {
				data = FileUtils.readFileToByteArray(ipFile);
			}

			for (int i = 0; i < 256; i++) {
				int j = i * 8 + 4;

				prefStart[i] = (int) bytesToLong(data[j], data[j + 1], data[j + 2], data[j + 3]);
				prefEnd[i] = (int) bytesToLong(data[j + 4], data[j + 5], data[j + 6], data[j + 7]);
			}

			int recordSize = (int) bytesToLong(data[0], data[1], data[2], data[3]);
			endArr = new long[recordSize];
			addrArr = new String[recordSize];

			for (int i = 0; i < recordSize; i++) {
				int  p        = 2052 + (i * 8);
				long endIpNum = bytesToLong(data[p], data[1 + p], data[2 + p], data[3 + p]);

				int offset = (int) bytesToLong3(data[4 + p], data[5 + p], data[6 + p]);
				int length = data[7 + p] & 0xff;

				endArr[i] = endIpNum;

				addrArr[i] = new String(Arrays.copyOfRange(data, offset, offset + length));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查找IP地址对应的真实地理位置信息
	 *
	 * @param ip
	 * @return
	 */
	public static IPLocation find(String ip) {
		if (IPV4Utils.isValid(ip)) {
			String[] ips      = ip.split("\\.");
			int      prefix   = Integer.valueOf(ips[0]);
			long     val      = ipToLong(ip);
			int      low      = prefStart[prefix];
			int      high     = prefEnd[prefix];
			long     cur      = low == high ? low : binarySearch(low, high, val);
			String   location = addrArr[(int) cur];

			if (location != null) {
				String[] strs = location.split("\\|", -1);

				if (strs.length == 11) {
					return new IPLocation(
							strs[8], strs[7], strs[1], strs[2], strs[3],
							strs[4], strs[5], strs[9], strs[10]
					);
				}
			}
		}

		return null;
	}

	private static int binarySearch(int low, int high, long k) {
		int m = 0;
		while (low <= high) {
			int mid = (low + high) / 2;

			long endIpNum = endArr[mid];
			if (endIpNum >= k) {
				m = mid;
				if (mid == 0) {
					break;
				}

				high = mid - 1;
			} else {
				low = mid + 1;
			}
		}

		return m;
	}

	private static long bytesToLong(byte a, byte b, byte c, byte d) {
		return (a & 0xFFL) | ((b << 8) & 0xFF00L) | ((c << 16) & 0xFF0000L) | ((d << 24) & 0xFF000000L);

	}

	private static long bytesToLong3(byte a, byte b, byte c) {
		return (a & 0xFFL) | ((b << 8) & 0xFF00L) | ((c << 16) & 0xFF0000L);
	}

	public static long ipToLong(String ip) {
		long     result = 0;
		String[] d      = ip.split("\\.");

		for (String b : d) {
			result <<= 8;
			result |= Long.parseLong(b) & 0xff;
		}

		return result;
	}

}