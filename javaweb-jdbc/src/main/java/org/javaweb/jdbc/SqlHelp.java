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
package org.javaweb.jdbc;

import com.sun.rowset.CachedRowSetImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

public class SqlHelp {

	/**
	 * 执行任意的SQL查询语句并返回结果集
	 *
	 * @param connection 一个已打开的JDBC 连接
	 * @param sql        需要执行的SQL语句
	 * @param prr        SQL语句中参数预编译
	 * @return rs SQL查询结果集
	 * @throws SQLException
	 */
	public static ResultSet executeQuery(Connection connection, String sql, Object... prr) throws SQLException {
		PreparedStatement pstt = null;
		ResultSet         rs   = null;

		try {
			pstt = connection.prepareStatement(sql);
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

			if (rs != null) {
				rs.close();
			}
		}
	}

	/**
	 * 执行任意的SQL更新语句并影响行数
	 *
	 * @param connection 一个已打开的JDBC 连接
	 * @param sql        需要执行的SQL语句
	 * @param prr        SQL语句中参数预编译
	 * @return i SQL更新后的影响行数
	 * @throws SQLException
	 */
	public static int executeUpdate(Connection connection, String sql, Object... prr) throws SQLException {
		PreparedStatement pstt = null;
		try {
			pstt = connection.prepareStatement(sql);

			for (int i = 0; i < prr.length; i++) {
				pstt.setObject(i + 1, prr[i]);
			}

			return pstt.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
			if (pstt != null) {
				pstt.close();
			}
		}
	}

	/**
	 * 批量执行任意的SQL更新语句并影响行数数组,执行后会清空传入的参数List对象
	 *
	 * @param connection 一个已打开的JDBC 连接
	 * @param sql        需要执行的SQL语句
	 * @param parameters SQL语句中参数预编译
	 * @return
	 * @throws SQLException
	 */
	public static int[] executeBatchUpdate(Connection connection, String sql, List<Object[]> parameters) throws SQLException {
		PreparedStatement pstt = null;
		try {
			pstt = connection.prepareStatement(sql);

			for (Iterator<Object[]> it = parameters.iterator(); it.hasNext(); ) {
				Object[] parameter = it.next();

				for (int i = 0; i < parameter.length; i++) {
					pstt.setObject(i + 1, parameter[i]);
				}

				pstt.addBatch();
				it.remove();
			}
			return pstt.executeBatch();
		} catch (SQLException e) {
			throw e;
		} finally {
			if (pstt != null) {
				pstt.close();
			}
		}
	}

}
