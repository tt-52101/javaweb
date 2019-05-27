package org.javaweb.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;

/**
 * IP 地址转地理位置工具类,使用的是IPIP.NET的城市库(收费版.ipdb格式)
 *
 * @author yz
 */
public class IPLocationUtils {

	private static final Logger LOG = LoggerFactory.getLogger(IPToLocationUtils.class);

	private static int nodeCount;

	private static byte[] data;

	private static JSONObject metaJson;

	private static JSONArray fieldArray;

	private static int v4offset;

	private static int fileSize;

	private static String ipFileName = "mydata4vipday2.ipdb";

	private static File ipFile = new File(FileUtils.getCurrentDirectory(), ipFileName);

	static {
		try {
			byte[]      bytes = null;
			InputStream in    = IPToLocationUtils.class.getClass().getResourceAsStream("/" + ipFileName);

			if (in != null) {
				bytes = IOUtils.toByteArray(in);
			}

			if (bytes == null && ipFile.exists()) {
				bytes = Files.readAllBytes(ipFile.toPath());
			}

			fileSize = bytes.length;

			int    metaLength = bytesToLong(bytes[0], bytes[1], bytes[2], bytes[3]).intValue();
			String metaStr    = new String(Arrays.copyOfRange(bytes, 4, metaLength + 4));
			metaJson = JSON.parseObject(metaStr);

			nodeCount = metaJson.getInteger("node_count");
			fieldArray = metaJson.getJSONArray("fields");

			// 获取IP数据
			data = Arrays.copyOfRange(bytes, metaLength + 4, fileSize);

			if (metaJson.getInteger("ip_version") == 1) {
				int node = 0;

				for (int i = 0; i < 96 && node < nodeCount; i++) {
					if (i >= 80) {
						node = readNode(node, 1);
					} else {
						node = readNode(node, 0);
					}
				}

				v4offset = node;
			}
		} catch (Exception e) {
			LOG.info("IP地址解析异常:" + e, e);
		}
	}

	/**
	 * 查找IP地址对应的数据范围
	 *
	 * @param binary
	 * @return
	 */
	private static int findNode(byte[] binary) {
		int node = 0;

		final int bit = binary.length * 8;

		if (bit == 32) {
			node = v4offset;
		}

		for (int i = 0; i < bit; i++) {
			if (node > nodeCount) {
				break;
			}

			node = readNode(node, 1 & ((0xFF & binary[i / 8]) >> 7 - (i % 8)));
		}

		if (node > nodeCount) {
			return node;
		}

		throw new RuntimeException("IP Address Not Found!");
	}

	/**
	 * 查找IP地址对应的真实地理位置信息
	 *
	 * @param ip
	 * @return
	 */
	public static IPLocation find(String ip) {
		return find(ip, "CN");
	}

	/**
	 * 查找IP地址对应的真实地理位置信息
	 *
	 * @param ip
	 * @param language
	 * @return
	 */
	public static IPLocation find(String ip, String language) {
		try {
			if (StringUtils.isNotEmpty(ip) && StringUtils.isNotEmpty(language) && metaJson != null) {
				JSONObject langObject = metaJson.getJSONObject("languages");

				if (langObject != null && langObject.containsKey(language.toUpperCase())) {
					int    langOffset  = langObject.getInteger(language);
					byte[] ipArray     = textToNumericFormatV4(ip);
					int    node        = findNode(ipArray);
					String locationStr = resolve(node);

					if (locationStr != null) {
						int fieldLength = fieldArray.size();

						String[] locationArr = Arrays.copyOfRange(
								locationStr.split("\t", fieldLength * langObject.size()),
								langOffset, langOffset + fieldLength
						);

						return new IPLocation(
								locationArr[11], locationArr[0], locationArr[0],
								locationArr[1], locationArr[2], null,
								locationArr[4], locationArr[5], locationArr[6]
						);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static String resolve(int node) {
		final int index = node - nodeCount + nodeCount * 8;

		if (index < fileSize) {
			byte b    = 0;
			int  size = Long.valueOf(bytesToLong(b, b, data[index], data[index + 1])).intValue();

			if (data.length > index + 2 + size) {
				return new String(data, index + 2, size, Charset.forName("UTF-8"));
			}
		}

		return null;
	}

	private static int readNode(int node, int index) {
		int off = node * 8 + index * 4;
		return bytesToLong(data[off], data[off + 1], data[off + 2], data[off + 3]).intValue();
	}

	private static Long bytesToLong(byte a, byte b, byte c, byte d) {
		return int2long((((a & 0xff) << 24) | ((b & 0xff) << 16) | ((c & 0xff) << 8) | (d & 0xff)));
	}

	private static Long int2long(int i) {
		Long l = i & 0x7fffffffL;

		if (i < 0) {
			l |= 0x080000000L;
		}

		return l;
	}

	public static byte[] textToNumericFormatV4(String var0) {
		byte[]  var1 = new byte[4];
		long    var2 = 0L;
		int     var4 = 0;
		boolean var5 = true;
		int     var6 = var0.length();

		if (var6 != 0 && var6 <= 15) {
			for (int var7 = 0; var7 < var6; ++var7) {
				char var8 = var0.charAt(var7);

				if (var8 == '.') {
					if (var5 || var2 < 0L || var2 > 255L || var4 == 3) {
						return null;
					}

					var1[var4++] = (byte) ((int) (var2 & 255L));
					var2 = 0L;
					var5 = true;
				} else {
					int var9 = Character.digit(var8, 10);
					if (var9 < 0) {
						return null;
					}

					var2 *= 10L;
					var2 += (long) var9;
					var5 = false;
				}
			}

			if (!var5 && var2 >= 0L && var2 < 1L << (4 - var4) * 8) {
				switch (var4) {
					case 0:
						var1[0] = (byte) ((int) (var2 >> 24 & 255L));
					case 1:
						var1[1] = (byte) ((int) (var2 >> 16 & 255L));
					case 2:
						var1[2] = (byte) ((int) (var2 >> 8 & 255L));
					case 3:
						var1[3] = (byte) ((int) (var2 >> 0 & 255L));
					default:
						return var1;
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

}
