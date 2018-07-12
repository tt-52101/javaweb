package org.javaweb.jdbc;

/**
 * Created by yz on 2017/7/5.
 */
public class QueryBuilder {

	private final StringBuffer sqlBuilder = new StringBuffer();

	public QueryBuilder build(String sql) {
		sqlBuilder.append(sql);
		return this;
	}

	public String toString() {
		return sqlBuilder.toString();
	}

}