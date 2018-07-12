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

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 精简版文字高亮
 * Created by yz on 2016/12/3.
 */
public class HighlightUtils {

	public static String highlight(String content, String keyword, int maxlen) {
		if (StringUtils.isNotEmpty(content) && StringUtils.isNotEmpty(keyword) && maxlen > 0) {

			keyword = keyword.trim();

			// 提取需要高亮的搜索结果
			content = searchResultExtraction(HttpServletRequestUtils.htmlSpecialChars(content), keyword, maxlen);

			// 需要高亮的词组
			Set<String> ls = new HashSet<String>();
			ls.add(keyword);
			ls.add(EncryptUtils.md5(keyword));

			if (keyword.matches("\\W+")) {
				String[] strs = keyword.split("\\W+");
				for (String temp : strs) {
					ls.add(temp.trim());
				}
			}

			for (String s : ls) {
				content = content.replace(s, "<em>" + s + "</em>");
			}

			return content;
		} else {
			return content;
		}
	}

	/**
	 * 搜索结果内容提取
	 *
	 * @param content
	 * @param keyword
	 * @param maxlen
	 * @return
	 */
	public static String searchResultExtraction(String content, String keyword, int maxlen) {
		if (StringUtils.isNotEmpty(content) && StringUtils.isNotEmpty(keyword) && content.length() > maxlen) {
			Matcher matcher = Pattern.compile(Pattern.quote(keyword), Pattern.DOTALL | Pattern.CASE_INSENSITIVE).matcher(content);

			if (matcher.find()) {
				// 截取字符串出现位置前半部分
				String start = content.substring(0, matcher.end());
				String end   = content.substring(start.length());

				start = start.contains("\n") ? start.substring(start.indexOf("\n") + 1) : start;
				StringBuilder sb = new StringBuilder();

				if (start.length() > maxlen) {
					int prePosition = 0;

					// 找关键字前面的自然段或断句
					Matcher preMatcher = Pattern.compile("\r?\n|。").matcher(start);
					if (preMatcher.find()) {
						prePosition = preMatcher.end();
					}

					// 自然段长度
					int preLength = start.length() - prePosition;

					if (preLength > maxlen) {
						// 如果自然段依旧超过最大长度,直接取最大长度的1/2
						prePosition = start.length() - maxlen / 2;
					}

					sb.append(start.substring(prePosition));
				} else {
					sb.append(start);
				}

				sb.append(end);

				return sb.length() > maxlen ? sb.substring(0, maxlen) + "..." : sb.toString();
			} else {
				return content.substring(0, maxlen) + "...";
			}
		}

		return content;
	}

}
