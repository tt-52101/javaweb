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

import com.sun.rowset.CachedRowSetImpl;

import java.io.File;
import java.sql.*;

public class MobileLocationUtils {

	private static final String GET_ALL_MOBILE_LOCATION = "select * from mobile_location where 1 = 1 ";

	private static String mobileFileName = "mobile_location.db";

	private static File mobileLocationFile = new File(FileUtils.getCurrentDirectory(), mobileFileName);

	private static Connection connection = null;

	/**
	 * 获取数据库连接
	 *
	 * @return
	 */
	public static Connection getConnection() {
		if (connection == null) {
			synchronized (MobileLocationUtils.class) {
				try {
					Class.forName("org.sqlite.JDBC");
					connection = DriverManager.getConnection("jdbc:sqlite:" + mobileLocationFile.getAbsolutePath());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return connection;
	}

	/**
	 * 执行任意的SQL查询语句并返回结果集
	 *
	 * @param sql 需要执行的SQL语句
	 * @param prr SQL语句中参数预编译
	 * @return rs SQL查询结果集
	 * @throws SQLException
	 */
	public static ResultSet executeQuery(String sql, Object... prr) throws SQLException {
		PreparedStatement pstt = null;
		ResultSet         rs   = null;

		try {
			pstt = getConnection().prepareStatement(sql);
			CachedRowSetImpl cachedRowSetImpl = new CachedRowSetImpl();

			for (int i = 0; i < prr.length; i++) {
				pstt.setObject(i + 1, prr[i]);
			}

			rs = pstt.executeQuery();
			cachedRowSetImpl.populate(rs);

			return cachedRowSetImpl;
		} catch (SQLException e) {
			throw e;
		} finally {
			if (pstt != null) {
				pstt.close();
			}
		}
	}

	/**
	 * 获取手机归属地信息
	 *
	 * @param mobile
	 * @return
	 */
	public static MobileLocation getMobileLocation(String mobile) {
		MobileLocation location     = new MobileLocation();
		String         mobilePrefix = mobile.substring(0, 7);
		ResultSet      rs           = null;

		try {
			rs = executeQuery(GET_ALL_MOBILE_LOCATION + " and mobile_prefix = ? ", mobilePrefix);

			while (rs.next()) {
				location.setPrefix(rs.getInt("prefix"));
				location.setMobilePrefix(rs.getInt("mobile_prefix"));
				location.setProvince(rs.getString("province"));
				location.setCity(rs.getString("city"));
				location.setIsp(rs.getString("isp"));
				location.setPostCode(rs.getInt("post_code"));
				location.setCityCode(rs.getInt("city_code"));
				location.setAreaCode(rs.getInt("area_code"));
				location.setTypes(rs.getString("types"));
			}

			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return location;
	}

}
