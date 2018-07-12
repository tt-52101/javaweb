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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * IP地址转换成地理位置工具类，使用前如果IP数据文件未初始化的情况下就必须要先调用一次load方法加载数据文件
 *
 * @author yz
 */
public class IPToLocationUtils {

	public static final Map<String, String> COUNTRY_CODE_MAP = new HashMap<String, String>();

	private static final String[] countries = new String[]{"RW=卢旺达", "SO=索马里", "YE=也门", "IQ=伊拉克", "SA=沙特阿拉伯", "IR=伊朗伊斯兰共和国", "CY=塞浦路斯", "TZ=坦桑尼亚", "SY=叙利亚", "AM=亚美尼亚", "KE=肯尼亚", "CD=扎伊尔", "DJ=吉布提", "UG=乌干达", "CF=中非共和国", "SC=塞舌尔群岛", "JO=约旦", "LB=黎巴嫩", "KW=科威特", "OM=阿曼", "QA=卡塔尔", "BH=巴林", "AE=阿拉伯联合酋长国", "IL=以色列", "TR=土耳其", "ET=埃塞俄比亚", "ER=厄立特里亚", "EG=埃及", "SD=苏丹", "GR=希腊", "BI=布隆迪", "EE=爱沙尼亚", "LV=拉脱维亚", "AZ=阿塞拜疆", "LT=立陶宛", "SJ=斯瓦尔巴特和扬马延", "GE=格鲁吉亚", "MD=摩尔多瓦共和国", "BY=白俄罗斯", "FI=芬兰", "AX=奥兰群岛", "UA=乌克兰", "MK=前南斯拉夫马其顿共和国", "HU=匈牙利", "BG=保加利亚", "AL=阿尔巴尼亚", "PL=波兰", "RO=罗马尼亚", "XK=科索沃", "ZW=津巴布韦", "ZM=赞比亚", "KM=科摩罗", "MW=马拉维", "LS=莱索托", "BW=博茨瓦纳", "MU=毛里求斯", "SZ=斯威士兰", "RE=留尼汪", "ZA=南非", "YT=马约特", "MZ=莫桑比克", "MG=马达加斯加", "AF=阿富汗", "PK=巴基斯坦", "BD=孟加拉", "TM=土库曼斯坦", "TJ=塔吉克斯坦", "LK=斯里兰卡", "BT=不丹", "IN=印度", "MV=马尔代夫", "IO=英属印度洋领地", "NP=尼泊尔", "MM=缅甸", "UZ=乌兹别克斯坦", "KZ=哈萨克斯坦", "KG=吉尔吉克斯坦", "TF=法属南部领土", "CC=科科斯群岛", "PW=帕劳", "VN=越南", "TH=泰国", "ID=印度尼西亚", "LA=老挝人民民主共和国", "TW=台湾", "PH=菲律宾", "MY=马来西亚", "CN=中国", "HK=香港", "BN=文莱", "MO=澳门", "KH=柬埔寨", "KR=韩国", "JP=日本", "KP=朝鲜民主共和国", "SG=新加坡", "CK=库克群岛", "TL=东帝汶", "RU=俄罗斯", "MN=蒙古", "AU=澳大利亚", "CX=圣诞岛", "MH=马绍尔群岛", "FM=密克罗尼西亚", "PG=巴布亚新几内亚", "SB=所罗门群岛", "TV=图瓦卢", "NR=瑙鲁", "VU=瓦努阿图", "NC=新喀里多尼亚", "NF=诺福克岛", "NZ=新西兰", "FJ=斐济", "LY=阿拉伯利比亚民众国", "CM=喀麦隆", "SN=塞内加尔", "CG=刚果", "PT=葡萄牙", "LR=利比里亚", "CI=象牙海岸", "GH=加纳", "GQ=赤道几内亚", "NG=尼日利亚", "BF=布基纳法索", "TG=多哥", "GW=几内亚比绍", "MR=毛里塔尼亚", "BJ=贝宁", "GA=加蓬", "SL=塞拉利昂", "ST=圣多美和普林西比", "GI=直布罗陀", "GM=冈比亚", "GN=几内亚", "TD=乍得", "NE=尼日尔", "ML=马里", "TN=突尼斯", "ES=西班牙", "MA=摩洛哥", "MT=马耳他", "DZ=阿尔及利亚", "FO=法罗群岛", "DK=丹麦", "IS=冰岛", "GB=英国", "CH=瑞士", "SE=瑞典", "NL=荷兰", "AT=奥地利", "BE=比利时", "DE=德国", "LU=卢森堡", "IE=爱尔兰", "MC=摩纳哥", "FR=法国", "AD=安道尔", "LI=列支敦士登", "JE=泽西岛", "IM=曼岛", "GG=格恩西岛", "SK=斯洛伐克共和国", "CZ=捷克共和国", "NO=挪威", "VA=圣座（梵蒂冈）", "SM=圣马力诺", "IT=意大利", "SI=斯洛文尼亚", "ME=黑山共和国", "HR=克罗地亚", "BA=波斯尼亚和黑山共和国", "AO=安哥拉", "NA=纳米比亚", "SH=圣赫勒拿", "BB=巴巴多斯", "CV=佛得角", "GY=圭亚那", "GF=法属圭亚那", "SR=苏里南", "PM=圣皮埃尔和密克隆", "GL=格陵兰", "PY=巴拉圭", "UY=乌拉圭", "BR=巴西", "FK=福克兰群岛", "GS=南乔治亚岛和南桑威齐群岛", "JM=牙买加", "DO=多米尼加共和国", "CU=古巴", "MQ=马提尼克群岛", "BS=巴哈马", "BM=百慕大", "AI=安圭拉", "TT=特立尼达和多巴哥", "KN=圣基茨和尼维斯", "DM=多米尼加", "AG=安提瓜和巴布达", "LC=圣卢西亚", "TC=特克斯和凯科斯群岛", "AW=阿鲁巴", "VG=英属维京群岛", "VC=圣文森特和格林纳丁斯", "MS=蒙塞拉特群岛", "MF=圣马丁", "BL=圣巴泰勒米", "GP=瓜德罗普岛", "GD=格林纳达", "KY=开曼群岛", "BZ=伯利兹", "SV=萨尔瓦多", "GT=危地马拉", "HN=洪都拉斯", "NI=尼加拉瓜", "CR=哥斯达黎加", "VE=委内瑞拉", "EC=厄瓜多尔", "CO=哥伦比亚", "PA=巴拿马", "HT=海地", "AR=阿根廷", "CL=智利", "BO=玻利维亚", "PE=秘鲁", "MX=墨西哥", "PF=法属波利尼西亚", "PN=皮特凯恩群岛", "KI=基里巴斯", "TK=托克劳", "TO=汤加", "WF=瓦利斯和富图纳", "WS=萨摩亚", "NU=纽埃", "MP=北马里亚纳群岛", "GU=关岛", "PR=波多黎各", "VI=美属维京群岛", "UM=美国边远小岛", "AS=美属萨摩亚", "CA=加拿大", "US=美国", "PS=巴勒斯坦领土", "RS=塞尔维亚", "AQ=南极洲", "SX=圣马丁岛", "CW=库拉索", "BQ=博奈尔岛、圣尤斯达蒂斯和萨巴", "SS=南苏丹"};

	public static boolean enableFileWatch = false;

	private static int offset;

	private static int[] index = new int[256];

	private static ByteBuffer dataBuffer;

	private static ByteBuffer indexBuffer;

	private static Long lastModifyTime = 0L;

	private static String ipFileName = "17monipdb.dat";

	private static File ipFile = new File(FileUtils.getCurrentDirectory(), ipFileName);

	private static ReentrantLock lock = new ReentrantLock();

	static {
		for (String country : countries) {
			String[] array = country.split("=");
			COUNTRY_CODE_MAP.put(array[1], array[0]);
		}

		load();
	}

	/**
	 * 需要加载一次文件路径
	 *
	 * @param filePath
	 */
	public static void load(String filePath) {
		ipFile = new File(filePath);
		load();

		if (enableFileWatch) {
			watch();
		}
	}

	public static String[] find(String ip) {
		int  ip_prefix_value = new Integer(ip.substring(0, ip.indexOf(".")));
		long ip2long_value   = ip2long(ip);
		int  start           = index[ip_prefix_value];
		int  max_comp_len    = offset - 1028;
		long index_offset    = -1;
		int  index_length    = -1;
		byte b               = 0;

		for (start = start * 8 + 1024; start < max_comp_len; start += 8) {
			if (int2long(indexBuffer.getInt(start)) >= ip2long_value) {
				index_offset = bytesToLong(b, indexBuffer.get(start + 6), indexBuffer.get(start + 5), indexBuffer.get(start + 4));
				index_length = 0xFF & indexBuffer.get(start + 7);
				break;
			}
		}

		byte[] areaBytes;
		lock.lock();

		try {
			dataBuffer.position(offset + (int) index_offset - 1024);
			areaBytes = new byte[index_length];
			dataBuffer.get(areaBytes, 0, index_length);
		} finally {
			lock.unlock();
		}

		return new String(areaBytes, Charset.forName("UTF-8")).split("\t", -1);
	}

	private static void watch() {
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				long time = ipFile.lastModified();

				if (time > lastModifyTime) {
					lastModifyTime = time;
					load();
				}
			}
		}, 1000L, 5000L, TimeUnit.MILLISECONDS);
	}

	private static void load() {
		lastModifyTime = ipFile.lastModified();
		FileInputStream fin = null;
		lock.lock();

		try {
			dataBuffer = ByteBuffer.allocate(Long.valueOf(ipFile.length()).intValue());
			fin = new FileInputStream(ipFile);
			int    readBytesLength;
			byte[] chunk = new byte[4096];

			while (fin.available() > 0) {
				readBytesLength = fin.read(chunk);
				dataBuffer.put(chunk, 0, readBytesLength);
			}

			dataBuffer.position(0);
			int    indexLength = dataBuffer.getInt();
			byte[] indexBytes  = new byte[indexLength];
			dataBuffer.get(indexBytes, 0, indexLength - 4);
			indexBuffer = ByteBuffer.wrap(indexBytes);
			indexBuffer.order(ByteOrder.LITTLE_ENDIAN);
			offset = indexLength;

			int loop = 0;

			while (loop++ < 256) {
				index[loop - 1] = indexBuffer.getInt();
			}

			indexBuffer.order(ByteOrder.BIG_ENDIAN);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (fin != null) {
					fin.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			lock.unlock();
		}
	}

	private static long bytesToLong(byte a, byte b, byte c, byte d) {
		return int2long((((a & 0xff) << 24) | ((b & 0xff) << 16) | ((c & 0xff) << 8) | (d & 0xff)));
	}

	private static int str2Ip(String ip) {
		String[] ss = ip.split("\\.");
		int      a, b, c, d;
		a = Integer.parseInt(ss[0]);
		b = Integer.parseInt(ss[1]);
		c = Integer.parseInt(ss[2]);
		d = Integer.parseInt(ss[3]);

		return (a << 24) | (b << 16) | (c << 8) | d;
	}

	private static long ip2long(String ip) {
		return int2long(str2Ip(ip));
	}

	private static long int2long(int i) {
		long l = i & 0x7fffffffL;

		if (i < 0) {
			l |= 0x080000000L;
		}

		return l;
	}

	public static void main(String[] args) {
		Long     st   = System.nanoTime();
		String[] strs = IPToLocationUtils.find("114.253.34.147");
		System.out.println(Arrays.toString(strs));
		System.out.println(COUNTRY_CODE_MAP.get(strs[0]));

		Long et = System.nanoTime();
		System.out.println((et - st) / 1000 / 1000);
	}

}
