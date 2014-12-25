package org.nkjmlab.util;

import java.sql.Connection;
import java.sql.SQLException;

import net.sf.persist.Persist;

public class DBUtil {

	public static void drop(String tableName) {
		try (Connection con = RDBConnector.getConnection(new RDBConfig())) {
			Persist persist = new Persist(con);
			persist.executeUpdate("DROP TABLE " + tableName + " IF EXISTS");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void create(String tableName, String schema) {
		try (Connection con = RDBConnector.getConnection(new RDBConfig())) {
			Persist persist = new Persist(con);
			persist.executeUpdate("CREATE TABLE " + tableName + " (" + schema
					+ ")");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
