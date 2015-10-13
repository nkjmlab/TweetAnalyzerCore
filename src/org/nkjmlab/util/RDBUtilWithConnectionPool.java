package org.nkjmlab.util;

import java.sql.Connection;
import java.sql.SQLException;

import org.h2.jdbcx.JdbcConnectionPool;

public class RDBUtilWithConnectionPool extends RDBUtil {

	private final JdbcConnectionPool connectionPool;

	public RDBUtilWithConnectionPool(RDBConfig conf) {
		super(conf);
		this.connectionPool = JdbcConnectionPool.create(conf.getJdbcURL(),
				conf.getUsername(), conf.getPassword());
	}

	@Override
	public Connection getConnection() throws SQLException {
		return connectionPool.getConnection();
	}

}
