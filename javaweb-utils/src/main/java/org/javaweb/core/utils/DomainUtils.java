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

import java.io.*;
import java.net.IDN;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 域名工具类,域名分配 gTLDs、ccTLDs
 *
 * @author yz
 * @iana http://www.iana.org/domains/root/db
 */
public class DomainUtils {

	/**
	 * ccTLDs 国家顶级域名List，包含全球有效的国家顶级域名
	 * https://en.wikipedia.org/wiki/Country_code_top-level_domain
	 */
	public static final List<String> COUNTRY_LIST = Arrays.asList(new String[]{"ac", "ad", "ae", "af", "ag", "ai", "al", "am", "an", "ao", "aq", "ar", "as", "at", "au", "aw", "ax", "az", "ba", "bb", "bd", "be", "bf", "bg", "bh", "bi", "bj", "bl", "bm", "bn", "bo", "bq", "br", "bs", "bt", "bv", "bw", "by", "bz", "ca", "cc", "cd", "cf", "cg", "ch", "ci", "ck", "cl", "cm", "cn", "co", "cr", "cu", "cv", "cw", "cx", "cy", "cz", "de", "dj", "dk", "dm", "do", "dz", "ec", "ee", "eg", "eh", "er", "es", "et", "eu", "fi", "fj", "fk", "fm", "fo", "fr", "ga", "gb", "gd", "ge", "gf", "gg", "gh", "gi", "gl", "gm", "gn", "gp", "gq", "gr", "gs", "gt", "gu", "gw", "gy", "hk", "hm", "hn", "hr", "ht", "hu", "id", "ie", "il", "im", "in", "io", "iq", "ir", "is", "it", "je", "jm", "jo", "jp", "ke", "kg", "kh", "ki", "km", "kn", "kp", "kr", "kw", "ky", "kz", "la", "lb", "lc", "li", "lk", "lr", "ls", "lt", "lu", "lv", "ly", "ma", "mc", "md", "me", "mf", "mg", "mh", "mk", "ml", "mm", "mn", "mo", "mp", "mq", "mr", "ms", "mt", "mu", "mv", "mw", "mx", "my", "mz", "na", "nc", "ne", "nf", "ng", "ni", "nl", "no", "np", "nr", "nu", "nz", "om", "pa", "pe", "pf", "pg", "ph", "pk", "pl", "pm", "pn", "pr", "ps", "pt", "pw", "py", "qa", "re", "ro", "rs", "ru", "rw", "sa", "sb", "sc", "sd", "se", "sg", "sh", "si", "sj", "sk", "sl", "sm", "sn", "so", "sr", "ss", "st", "su", "sv", "sx", "sy", "sz", "tc", "td", "tf", "tg", "th", "tj", "tk", "tl", "tm", "tn", "to", "tp", "tr", "tt", "tv", "tw", "tz", "ua", "ug", "uk", "um", "us", "uy", "uz", "va", "vc", "ve", "vg", "vi", "vn", "vu", "wf", "ws", "xn--3e0b707e", "xn--45brj9c", "xn--54b7fta0cc", "xn--80ao21a", "xn--90a3ac", "xn--90ais", "xn--clchc0ea0b2g2a9gcd", "xn--d1alf", "xn--fiqs8s", "xn--fiqz9s", "xn--fpcrj9c3d", "xn--fzc2c9e2c", "xn--gecrj9c", "xn--h2brj9c", "xn--j1amh", "xn--j6w193g", "xn--kprw13d", "xn--kpry57d", "xn--l1acc", "xn--lgbbat1ad8j", "xn--mgb9awbf", "xn--mgba3a4f16a", "xn--mgbaam7a8h", "xn--mgbai9azgqp6j", "xn--mgbayh7gpa", "xn--mgbbh1a71e", "xn--mgbc0a9azcg", "xn--mgberp4a5d4ar", "xn--mgbpl2fh", "xn--mgbtx2b", "xn--mgbx4cd0ab", "xn--node", "xn--o3cw4h", "xn--ogbpf8fl", "xn--p1ai", "xn--pgbs0dh", "xn--s9brj9c", "xn--wgbh1c", "xn--wgbl6a", "xn--xkc2al3hye2a", "xn--xkc2dl3a5ee0h", "xn--y9a3aq", "xn--yfro4i67o", "xn--ygbi2ammx", "ye", "yt", "za", "zm", "zw"});

	/**
	 * gTLDs 如.com,.edu,.gov
	 * https://en.wikipedia.org/wiki/Generic_top-level_domain
	 */
	public static final List<String> GENERIC_LIST = Arrays.asList(new String[]{"dog", "love", "abogado", "ac", "academy", "accountants", "active", "actor", "ad", "adult", "ae", "aero", "af", "ag", "agency", "ai", "airforce", "al", "allfinanz", "alsace", "am", "amsterdam", "an", "android", "ao", "apartments", "aq", "aquarelle", "ar", "archi", "army", "arpa", "as", "asia", "associates", "at", "attorney", "au", "auction", "audio", "autos", "aw", "ax", "axa", "az", "ba", "band", "bank", "bar", "barclaycard", "barclays", "bargains", "bayern", "bb", "bd", "be", "beer", "berlin", "best", "bf", "bg", "bh", "bi", "bid", "bike", "bingo", "bio", "biz", "bj", "bl", "black", "blackfriday", "bloomberg", "blue", "bm", "bmw", "bn", "bnpparibas", "bo", "boo", "boutique", "bq", "br", "brussels", "bs", "bt", "budapest", "build", "builders", "business", "buzz", "bv", "bw", "by", "bz", "bzh", "ca", "cab", "cal", "camera", "camp", "cancerresearch", "canon", "capetown", "capital", "caravan", "cards", "care", "career", "careers", "cartier", "casa", "cash", "cat", "catering", "cbn", "cc", "cd", "center", "ceo", "cern", "cf", "cg", "ch", "channel", "chat", "cheap", "christmas", "chrome", "church", "ci", "citic", "city", "ck", "cl", "claims", "cleaning", "click", "clinic", "clothing", "club", "cm", "cn", "co", "coach", "codes", "coffee", "college", "cologne", "com", "community", "company", "computer", "condos", "construction", "consulting", "contractors", "cooking", "cool", "coop", "country", "cr", "credit", "creditcard", "cricket", "crs", "cruises", "cu", "cuisinella", "cv", "cw", "cx", "cy", "cymru", "cz", "dabur", "dad", "dance", "dating", "day", "dclk", "de", "deals", "degree", "delivery", "democrat", "dental", "dentist", "desi", "design", "dev", "diamonds", "diet", "digital", "direct", "directory", "discount", "dj", "dk", "dm", "dnp", "do", "docs", "domains", "doosan", "durban", "dvag", "dz", "eat", "ec", "edu", "education", "ee", "eg", "eh", "email", "emerck", "energy", "engineer", "engineering", "enterprises", "equipment", "er", "es", "esq", "estate", "et", "eu", "eurovision", "eus", "events", "everbank", "exchange", "expert", "exposed", "fail", "farm", "fashion", "feedback", "fi", "finance", "financial", "firmdale", "fish", "fishing", "fit", "fitness", "fj", "fk", "flights", "florist", "flowers", "flsmidth", "fly", "fm", "fo", "foo", "forsale", "foundation", "fr", "frl", "frogans", "fund", "furniture", "futbol", "ga", "gal", "gallery", "garden", "gb", "gbiz", "gd", "gdn", "ge", "gent", "gf", "gg", "ggee", "gh", "gi", "gift", "gifts", "gives", "gl", "glass", "gle", "global", "globo", "gm", "gmail", "gmo", "gmx", "gn", "goldpoint", "goog", "google", "gop", "gov", "gp", "gq", "gr", "graphics", "gratis", "green", "gripe", "gs", "gt", "gu", "guide", "guitars", "guru", "gw", "gy", "hamburg", "hangout", "haus", "healthcare", "help", "here", "hermes", "hiphop", "hiv", "hk", "hm", "hn", "holdings", "holiday", "homes", "horse", "host", "hosting", "house", "how", "hr", "ht", "hu", "ibm", "id", "ie", "ifm", "il", "im", "immo", "immobilien", "in", "industries", "info", "ing", "ink", "institute", "insure", "int", "international", "investments", "io", "iq", "ir", "irish", "is", "it", "iwc", "jcb", "je", "jetzt", "jm", "jo", "jobs", "joburg", "jp", "juegos", "kaufen", "kddi", "ke", "kg", "kh", "ki", "kim", "kitchen", "kiwi", "km", "kn", "koeln", "kp", "kr", "krd", "kred", "kw", "ky", "kyoto", "kz", "la", "lacaixa", "land", "lat", "latrobe", "lawyer", "lb", "lc", "lds", "lease", "legal", "lgbt", "li", "lidl", "life", "lighting", "limited", "limo", "link", "lk", "loans", "london", "lotte", "lotto", "lr", "ls", "lt", "ltda", "lu", "luxe", "luxury", "lv", "ly", "ma", "madrid", "maison", "management", "mango", "market", "marketing", "marriott", "mc", "md", "me", "media", "meet", "melbourne", "meme", "memorial", "menu", "mf", "mg", "mh", "miami", "mil", "mini", "mk", "ml", "mm", "mn", "mo", "mobi", "moda", "moe", "monash", "money", "mormon", "mortgage", "moscow", "motorcycles", "mov", "mp", "mq", "mr", "ms", "mt", "mu", "museum", "mv", "mw", "mx", "my", "mz", "na", "nagoya", "name", "navy", "nc", "ne", "net", "network", "neustar", "new", "nexus", "nf", "ng", "ngo", "nhk", "ni", "nico", "ninja", "nl", "no", "np", "nr", "nra", "nrw", "ntt", "nu", "nyc", "nz", "okinawa", "om", "one", "ong", "onl", "ooo", "org", "organic", "osaka", "otsuka", "ovh", "pa", "paris", "partners", "parts", "party", "pe", "pf", "pg", "ph", "pharmacy", "photo", "photography", "photos", "physio", "pics", "pictures", "pink", "pizza", "pk", "pl", "place", "plumbing", "pm", "pn", "pohl", "poker", "porn", "post", "pr", "praxi", "press", "pro", "prod", "productions", "prof", "properties", "property", "ps", "pt", "pub", "pw", "py", "qa", "qpon", "quebec", "re", "realtor", "recipes", "red", "rehab", "reise", "reisen", "reit", "ren", "rentals", "repair", "report", "republican", "rest", "restaurant", "reviews", "rich", "rio", "rip", "ro", "rocks", "rodeo", "rs", "rsvp", "ru", "ruhr", "rw", "ryukyu", "sa", "saarland", "sale", "samsung", "sarl", "saxo", "sb", "sc", "sca", "scb", "schmidt", "schule", "schwarz", "science", "scot", "sd", "se", "services", "sew", "sexy", "sg", "sh", "shiksha", "shoes", "shriram", "si", "singles", "sj", "sk", "sky", "sl", "sm", "sn", "so", "social", "software", "sohu", "solar", "solutions", "soy", "space", "spiegel", "sr", "ss", "st", "style", "su", "supplies", "supply", "support", "surf", "surgery", "suzuki", "sv", "sx", "sy", "sydney", "systems", "sz", "taipei", "tatar", "tattoo", "tax", "tc", "td", "technology", "tel", "temasek", "tennis", "tf", "tg", "th", "tienda", "tips", "tires", "tirol", "tj", "tk", "tl", "tm", "tn", "to", "today", "tokyo", "tools", "top", "toshiba", "town", "toys", "tp", "tr", "trade", "training", "travel", "trust", "tt", "tui", "tv", "tw", "tz", "ua", "ug", "uk", "um", "university", "uno", "uol", "us", "uy", "uz", "va", "vacations", "vc", "ve", "vegas", "ventures", "versicherung", "vet", "vg", "vi", "viajes", "video", "villas", "vision", "vlaanderen", "vn", "vodka", "vote", "voting", "voto", "voyage", "vu", "wales", "wang", "watch", "webcam", "website", "wed", "wedding", "wf", "whoswho", "wien", "wiki", "williamhill", "wme", "work", "works", "world", "ws", "wtc", "wtf", "xn--0zwm56d", "xn--11b5bs3a9aj6g", "xn--1qqw23a", "xn--3bst00m", "xn--3ds443g", "xn--3e0b707e", "xn--45brj9c", "xn--45q11c", "xn--4gbrim", "xn--54b7fta0cc", "xn--55qw42g", "xn--55qx5d", "xn--6frz82g", "xn--6qq986b3xl", "xn--80adxhks", "xn--80akhbyknj4f", "xn--80ao21a", "xn--80asehdb", "xn--80aswg", "xn--90a3ac", "xn--90ais", "xn--9t4b11yi5a", "xn--b4w605ferd", "xn--c1avg", "xn--cg4bki", "xn--clchc0ea0b2g2a9gcd", "xn--czr694b", "xn--czrs0t", "xn--czru2d", "xn--d1acj3b", "xn--d1alf", "xn--deba0ad", "xn--fiq228c5hs", "xn--fiq64b", "xn--fiqs8s", "xn--fiqz9s", "xn--flw351e", "xn--fpcrj9c3d", "xn--fzc2c9e2c", "xn--g6w251d", "xn--gecrj9c", "xn--h2brj9c", "xn--hgbk6aj7f53bba", "xn--hlcj6aya9esc7a", "xn--hxt814e", "xn--i1b6b1a6a2e", "xn--io0a7i", "xn--j1amh", "xn--j6w193g", "xn--jxalpdlp", "xn--kgbechtv", "xn--kprw13d", "xn--kpry57d", "xn--kput3i", "xn--l1acc", "xn--lgbbat1ad8j", "xn--mgb9awbf", "xn--mgba3a4f16a", "xn--mgbaam7a8h", "xn--mgbab2bd", "xn--mgbai9azgqp6j", "xn--mgbayh7gpa", "xn--mgbbh1a71e", "xn--mgbc0a9azcg", "xn--mgberp4a5d4ar", "xn--mgbpl2fh", "xn--mgbtx2b", "xn--mgbx4cd0ab", "xn--ngbc5azd", "xn--node", "xn--nqv7f", "xn--nqv7fs00ema", "xn--o3cw4h", "xn--ogbpf8fl", "xn--p1acf", "xn--p1ai", "xn--pgbs0dh", "xn--q9jyb4c", "xn--qcka1pmc", "xn--rhqv96g", "xn--s9brj9c", "xn--ses554g", "xn--unup4y", "xn--vermgensberater-ctb", "xn--vermgensberatung-pwb", "xn--vhquv", "xn--wgbh1c", "xn--wgbl6a", "xn--xhq521b", "xn--xkc2al3hye2a", "xn--xkc2dl3a5ee0h", "xn--y9a3aq", "xn--yfro4i67o", "xn--ygbi2ammx", "xn--zckzah", "xn--zfr164b", "xxx", "xyz", "yachts", "yandex", "ye", "yodobashi", "yoga", "yokohama", "youtube", "yt", "za", "zip", "zm", "zone", "zuerich", "zw"});

	private static final String DOMAIN_PATTERN_STRING = "^\\b((?=[a-z0-9-]{1,63}\\.)(xn--)?[a-z0-9]+(-[a-z0-9]+)*\\.)+[a-z\\-A-Z0-9]{2,63}\\b$";

	private static final Pattern DIMAIN_PATTERN = Pattern.compile(DOMAIN_PATTERN_STRING);

	/**
	 * 验证输入域名是否是一个格式合法的域名,支持域名IDN解析 (排除域名中不带.的,毕竟不带任何后缀的很少)
	 *
	 * @param domainName
	 * @return
	 */
	public static boolean isValidDomainName(String domainName) {
		domainName = domainName.toLowerCase();
		String domainNameAscii = IDN.toASCII(domainName);
		String suffix          = domainName.contains(".") ? domainNameAscii.substring(domainNameAscii.lastIndexOf(".") + 1) : "";

		return DIMAIN_PATTERN.matcher(domainNameAscii).find()
				&& (COUNTRY_LIST.contains(suffix) || GENERIC_LIST.contains(suffix));
	}

	/**
	 * 快速判断是否是主域名
	 *
	 * @param domainName
	 * @return
	 */
	public static boolean isRootDomain(String domainName) {
		String   domainNameAscii = IDN.toASCII(domainName.toLowerCase());
		String[] domainArrays    = domainNameAscii.split("\\.");

		if (domainArrays.length > 2) {
			String country = domainArrays[domainArrays.length - 1];
			String generic = domainArrays[domainArrays.length - 2];
			return domainArrays.length < 4 && GENERIC_LIST.contains(generic) && COUNTRY_LIST.contains(country);
		} else {
			return false;
		}
	}

	/**
	 * 根据 <a href="http://www.ietf.org/rfc/rfc3490.txt">RFC 3490</a> 的 ToASCII
	 * 操作中的定义， 将字符串从 Unicode 转换为 ASCII 兼容性编码 (ACE)。
	 *
	 * @param domain 需要处理的字符串
	 * @return
	 */
	public static String toAscii(String domain) {
		return IDN.toASCII(domain.toLowerCase());
	}

	public static void main(String[] args) {
		try {
			BufferedReader br  = new BufferedReader(new InputStreamReader(new FileInputStream("/Users/yz/Downloads/domain.txt")));
			BufferedWriter bw  = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/Users/yz/Downloads/domains.txt", true)));
			String         str = null;

			while ((str = br.readLine()) != null) {
				try {
					if (isValidDomainName(str)) {
						bw.write(str + "\r\n");
						bw.flush();
					}
				} catch (Exception e) {
					System.err.println(str);
				}
			}

			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
