package org.nkjmlab.util;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.persist.Persist;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RDBUtil {
	private static Logger log = LogManager.getLogger();

	public static <T> T read(Class<T> clazz, String sql, Object... objs) {
		try (Connection con = RDBConnector.getConnection()) {
			Persist persist = new Persist(con);

			return persist.read(clazz, sql, objs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> List<T> readList(Class<T> clazz, String sql,
			Object... objs) {
		try (Connection con = RDBConnector.getConnection()) {
			Persist persist = new Persist(con);

			return persist.readList(clazz, sql, objs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<T>();
	}

	public static List<Map<String, Object>> readMapList(String sql,
			Object... objs) {
		try (Connection con = RDBConnector.getConnection()) {
			Persist persist = new Persist(con);

			return persist.readMapList(sql, objs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<Map<String, Object>>();
	}

	public static Map<String, Object> readMap(String sql, Object... objs) {
		try (Connection con = RDBConnector.getConnection()) {
			Persist persist = new Persist(con);

			return persist.readMap(sql, objs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new HashMap<String, Object>();
	}

	public static void executeUpdate(String sql, Object... objs) {
		try (Connection con = RDBConnector.getConnection()) {
			Persist persist = new Persist(con);
			persist.executeUpdate(sql, objs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void drop(String tableName) {
		try (Connection con = RDBConnector.getConnection()) {
			Persist persist = new Persist(con);
			persist.executeUpdate("DROP TABLE " + tableName + " IF EXISTS");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void create(String tableName, String schema) {
		try (Connection con = RDBConnector.getConnection()) {
			Persist persist = new Persist(con);
			persist.executeUpdate("CREATE TABLE " + tableName + " (" + schema
					+ ")");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insert(Object obj) {
		try (Connection con = RDBConnector.getConnection()) {
			Persist persist = new Persist(con);
			persist.insert(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
