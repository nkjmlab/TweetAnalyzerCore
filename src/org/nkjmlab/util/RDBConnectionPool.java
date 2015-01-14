package org.nkjmlab.util;

import java.sql.Connection;
import java.sql.SQLException;

import org.h2.jdbcx.JdbcConnectionPool;

public class RDBConnectionPool {

	private JdbcConnectionPool cp = null;

	public RDBConfig conf;

	public RDBConnectionPool(RDBConfig conf) {
		this.conf = conf;
	}

	public Connection getConnection() throws SQLException {
		if (cp == null) {
			cp = JdbcConnectionPool.create(conf.getJdbcURL(),
					conf.getUsername(), conf.getPassword());
		}
		return cp.getConnection();
	}

}
