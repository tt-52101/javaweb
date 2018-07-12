package org.javaweb.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlConnection {

	/**
	 * 获取数据库链接
	 *
	 * @param className
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static Connection getSqlConnection(
			String className, String url, String username, String password)
			throws ClassNotFoundException, SQLException {

		Class.forName(className);
		return DriverManager.getConnection(url, username, password);
	}

}