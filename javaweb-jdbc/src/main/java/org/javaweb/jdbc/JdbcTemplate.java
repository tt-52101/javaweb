package org.javaweb.jdbc;

import org.javaweb.core.utils.ReflectionUtils;
import org.javaweb.core.utils.StringUtils;
import org.javaweb.jdbc.annotation.Column;
import org.javaweb.jdbc.annotation.Table;
import org.javaweb.jdbc.exception.IncorrectResultSizeDataAccessException;
import org.javaweb.jdbc.exception.JDBCIDException;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by yz on 2017/7/4.
 */
public class JdbcTemplate {

	private DataSource dataSource;

	private Connection connection;

	private static final Logger LOG = Logger.getLogger("info");

	public JdbcTemplate(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public JdbcTemplate(Connection connection) {
		this.connection = connection;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * 获取数据库链接对象
	 *
	 * @return
	 */
	public Connection getConnection() {
		if (connection == null && dataSource != null) {
			try {
				return dataSource.getConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return connection;
	}

	/**
	 * 查找实体类中的表主键ID映射
	 *
	 * @param table    数据库表名
	 * @param fieldMap 实体类里的所有的字段map
	 * @return
	 * @throws JDBCIDException
	 */
	private static String findId(String table, Map<String, Field> fieldMap) throws JDBCIDException {
		String id = null;

		for (String fieldName : fieldMap.keySet()) {
			Field  field  = fieldMap.get(fieldName);
			Column column = field.getAnnotation(Column.class);

			if (column != null && column.id()) {
				if (id == null) {
					id = fieldName;
				} else {
					throw new JDBCIDException(table, fieldName);
				}
			}
		}

		if (id == null) {
			throw new JDBCIDException(table);
		}
		return id;
	}

	/**
	 * 映射数据库表实现更新实体类即可更新对应的表，需要先查询出对应的column信息后update
	 *
	 * @param <T>
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public <T> int update(Object obj) throws SQLException {
		Class<T> entityClass = (Class<T>) obj.getClass();

		try {
			if (entityClass.isAnnotationPresent(Table.class)) {
				Table               table      = entityClass.getAnnotation(Table.class);
				StringBuilder       sqlBuilder = new StringBuilder("update ");
				Map<String, Method> methodMap  = ReflectionUtils.getMethodsMap(entityClass);
				Map<String, Field>  fieldMap   = ReflectionUtils.getAllFieldsMap(entityClass);
				String              id         = table.id();// 主键字段名称
				String              tableName  = table.table();// 表名

				// 如果Table注解没设置主键名则从实体类成员变量中找Column注解中ID=true
				if (id == null) {
					id = findId(tableName, fieldMap);
				}

				Object       idValue = methodMap.get("get" + table.id()).invoke(obj);
				List<Object> ls      = new ArrayList<Object>();

				if (StringUtils.isNotEmpty(idValue)) {
					throw new JDBCIDException("JDBC字段映射异常,数据表[" + table + "]ID值不能为空.");
				}

				sqlBuilder.append(tableName).append(" set ").append(id).append("=").append("?");
				ls.add(idValue);

				for (String str : fieldMap.keySet()) {
					String field = str.toLowerCase();

					if (!table.id().equals(field) && methodMap.containsKey("set" + field) && methodMap.containsKey("get" + field)) {
						Method  method     = methodMap.get("set" + str);
						String  columnName = field;
						boolean updatable  = true;

						if (method.isAnnotationPresent(Column.class)) {
							Column column = method.getAnnotation(Column.class);
							columnName = column.name();
							updatable = column.updatable();// 是否允许更新
						}

						if (updatable) {
							Method getMethod = methodMap.get("get" + str);
							sqlBuilder.append(", ").append(columnName).append("=").append("?");
							ls.add(getMethod.invoke(obj));
						}
					}
				}

				sqlBuilder.append(" where ").append(table.id()).append("=").append("? ");
				ls.add(idValue);

				return SqlHelp.executeUpdate(
						this.getConnection(),
						sqlBuilder.toString(),
						ls.toArray(new Object[ls.size()])
				);
			} else {
				throw new JDBCIDException("映射实体异常: " + entityClass + "未添加@Table注解.");
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Throwable t) {
			throw new JDBCIDException("映射实体[" + entityClass + "]异常: " + t.toString());
		}
	}

	/**
	 * 反射映射数据库表字段到实体层，需要实体层必须包括成员变量和对应的public set方法 如: private int id; 对应public
	 * void setId(int id);方法 成员变量不考虑大小写，默认会忽略数据库字段下划线。如：user_id 等于userId
	 *
	 * @param <T>
	 * @param entityClass 需要返回的实体类类型
	 * @param sql         参数 sql 查询语句
	 * @param arr         可变参数，有则传，没有可忽略
	 * @return
	 * @throws java.sql.SQLException
	 */
	protected <T> List<T> tableMapping(String sql, Class<T> entityClass, Object... arr) throws SQLException {
		List<T>   ls = new ArrayList<T>();
		ResultSet rs = null;

		try {
			rs = SqlHelp.executeQuery(connection, sql, arr);
			ResultSetMetaData   rsm       = rs.getMetaData();
			Map<String, Method> methodMap = ReflectionUtils.getMethodsMap(entityClass);
			Map<String, Field>  fieldMap  = ReflectionUtils.getAllFieldsMap(entityClass);

			while (rs.next()) {
				try {
					T c = entityClass.newInstance();

					//反射设值
					for (int i = 1; i < rsm.getColumnCount() + 1; i++) {
						String columnName    = rsm.getColumnName(i);
						String fieldStr      = columnName.toLowerCase().replaceAll("_", "");
						String setColumnName = "set" + fieldStr;

						if (methodMap.containsKey(setColumnName) && fieldMap.containsKey(fieldStr)) {
							Method method   = methodMap.get(setColumnName);
							Type[] types    = method.getGenericParameterTypes();
							Object objValue = rs.getObject(columnName);

							if (types.length == 1) {
								try {
									method.invoke(c, objValue);
								} catch (IllegalArgumentException e) {
									LOG.info("方法:" + method + ",值:" + objValue + ",映射异常:" + e);
								}
							}
						}
					}

					ls.add(c);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null) {
				rs.close();
			}
		}

		return ls;
	}

	/**
	 * 查询数据库中一条记录并映射成对应的实体类类型 如果查询结果数大于一条抛出数据访问异常
	 *
	 * @param <T>
	 * @param sql
	 * @param entityClass
	 * @param arr
	 * @return
	 * @throws java.sql.SQLException
	 */
	public <T> T queryForEntity(String sql, Class<T> entityClass, Object... arr) throws SQLException {
		List<T> ls = tableMapping(sql, entityClass, arr);

		if (ls.size() > 1) {
			throw new IncorrectResultSizeDataAccessException(ls.size());
		} else if (ls.size() == 1) {
			return ls.get(0);
		} else {
			return null;
		}
	}

	/**
	 * 查询数据库中任意条记录并映射成对应的实体类集合类型
	 *
	 * @param <T>
	 * @param sql
	 * @param entityClass
	 * @param arr
	 * @return
	 */
	public <T> List<T> queryForList(String sql, Class<T> entityClass, Object... arr) {
		try {
			return tableMapping(sql, entityClass, arr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public int queryForInteger(String sql, Object... objs) throws SQLException {
		ResultSet rs = null;

		try {
			rs = SqlHelp.executeQuery(getConnection(), sql, objs);

			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				rs.close();
			}
		}

		return 0;
	}

	/**
	 * 分页查询
	 *
	 * @param sql
	 * @param entityClass
	 * @param pageNum
	 * @param pageSize
	 * @param objs
	 * @param <T>
	 * @return
	 */
	public <T> Page<T> queryForPage(String sql, Class<T> entityClass,
	                                int pageNum, int pageSize, Object... objs) {
		try {
			int recordCount = queryForInteger(Page.getResultCountSql(sql), objs);
			List<T> ls = tableMapping(
					Page.getPageSql(sql, pageNum, pageSize),
					entityClass,
					objs
			);

			return new Page<T>(pageNum, pageSize, ls, recordCount);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
