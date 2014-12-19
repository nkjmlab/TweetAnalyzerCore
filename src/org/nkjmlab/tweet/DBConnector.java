package org.nkjmlab.tweet;

import java.sql.Connection;
import java.sql.SQLException;

import org.h2.jdbcx.JdbcConnectionPool;

public class DBConnector {

	private static JdbcConnectionPool cp = null;

	public static Config conf = new Config();

	public static Connection getConnection() throws SQLException {
		return getConnection(conf);
	}

	public static Connection getConnection(Config conf) throws SQLException {

		if (cp == null) {
			cp = JdbcConnectionPool.create(conf.getJdbcURL(),
					conf.getUsername(), conf.getPassword());
		}
		return cp.getConnection();
	}

}
