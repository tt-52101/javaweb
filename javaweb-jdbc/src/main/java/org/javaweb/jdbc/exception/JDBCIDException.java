package org.javaweb.jdbc.exception;

import java.sql.SQLException;

/**
 * JDBC主键设置异常
 * Created by yz on 2017/7/4.
 */
public class JDBCIDException extends SQLException {

	public JDBCIDException(String table) {
		super("JDBC字段映射异常,数据表[" + table + "]未找到主键ID.");
	}

	public JDBCIDException(String table, String column) {
		super("JDBC字段映射异常,数据表[" + table + "]检查到重复的主键ID:" + column + ".");
	}

}