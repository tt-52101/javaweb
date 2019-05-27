/*
 * Copyright yz 2018-03-22 Email:admin@javaweb.org.
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlUtils {

	/**
	 * 清除Mysql注释查询语句
	 *
	 * @param sql
	 * @return
	 */
	public static String cleanMysqlCommentQuery(String sql) {
		if (sql != null && !"".equals(sql)) {
			String  prefix  = "/*!";
			String  suffix  = "*/";
			String  regexp  = "(" + Pattern.quote(prefix) + "(.*?)" + Pattern.quote(suffix) + ")";
			Matcher matcher = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE).matcher(sql);

			if (matcher.find()) {
				String temp = sql.substring(0, matcher.start());
				temp += sql.substring(matcher.start() + prefix.length(), matcher.end() - suffix.length());

				return cleanMysqlCommentQuery(temp + sql.substring(matcher.end()));
			} else {
				return sql;
			}
		}

		return sql;
	}

}
