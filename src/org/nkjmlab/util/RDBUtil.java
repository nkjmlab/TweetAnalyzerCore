package org.nkjmlab.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.persist.Persist;

public class RDBUtil {
	protected final RDBConfig conf;

	public RDBUtil(RDBConfig conf) {
		this.conf = conf;
		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public <T> T read(Class<T> clazz, String sql, Object... objs) {
		try (Connection con = getConnection()) {
			return new Persist(con).read(clazz, sql, objs);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public <T> T readByPrimaryKey(Class<T> clazz, Object primaryKeyValue) {
		try (Connection con = getConnection()) {
			return new Persist(con).readByPrimaryKey(clazz, primaryKeyValue);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public <T> List<T> readList(Class<T> clazz, String sql, Object... objs) {
		try (Connection con = getConnection()) {
			return new Persist(con).readList(clazz, sql, objs);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<T>();
		}
	}

	public List<Map<String, Object>> readMapList(String sql, Object... objs) {
		try (Connection con = getConnection()) {
			return new Persist(con).readMapList(sql, objs);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Map<String, Object>>();
		}
	}

	public Map<String, Object> readMap(String sql, Object... objs) {
		try (Connection con = getConnection()) {
			return new Persist(con).readMap(sql, objs);
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap<String, Object>();
		}
	}

	public void executeUpdate(String sql, Object... objs) {
		try (Connection con = getConnection()) {
			new Persist(con).executeUpdate(sql, objs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteAll(String tableName) {
		try (Connection con = getConnection()) {
			new Persist(con).executeUpdate("DELETE FROM " + tableName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void dropIfExists(String tableName) {
		try (Connection con = getConnection()) {
			new Persist(con).executeUpdate("DROP TABLE " + tableName
					+ " IF EXISTS");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void create(String tableName, String schema) {
		try (Connection con = getConnection()) {
			new Persist(con).executeUpdate("CREATE TABLE " + tableName + " ("
					+ schema + ")");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void insert(Object obj) {
		try (Connection con = getConnection()) {
			new Persist(con).insert(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() throws SQLException {
		try {
			return DriverManager.getConnection(conf.getJdbcURL(),
					conf.getUsername(), conf.getPassword());
		} catch (SQLException e) {
			System.err.println("Cannot connect to h2 database.");
			e.printStackTrace();
			throw e;
		}
	}

}
