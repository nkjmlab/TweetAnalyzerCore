package org.nkjmlab.util;

import java.sql.Connection;
import java.sql.SQLException;

import org.h2.jdbcx.JdbcConnectionPool;

public class RDBConnector {

	private static JdbcConnectionPool cp = null;

	public static RDBConfig conf;

	public static void setConf(RDBConfig conf) {
		RDBConnector.conf = conf;
	}

	public static Connection getConnection() throws SQLException {
		if (cp == null) {
			cp = JdbcConnectionPool.create(conf.getJdbcURL(),
					conf.getUsername(), conf.getPassword());
		}
		return cp.getConnection();
	}

}
