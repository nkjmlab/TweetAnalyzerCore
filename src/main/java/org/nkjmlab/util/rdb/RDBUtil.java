package org.nkjmlab.util.rdb;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.sf.persist.Persist;

public class RDBUtil {
	protected static transient Logger log = LogManager.getLogger();

	protected final RDBConfig conf;

	public RDBUtil(RDBConfig conf) {
		this.conf = conf;
		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e) {
			log.error(e, e);
			throw new RuntimeException(e);
		}
		log.debug("RDBConfig is set. {}", conf);
	}

	public <T> T read(Class<T> clazz, String sql, Object... parameters) {
		try (Connection con = getConnection()) {
			return new Persist(con).read(clazz, sql, parameters);
		} catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException(e);
		}
	}

	public <T> T readByPrimaryKey(Class<T> clazz, Object... primaryKeyValues) {
		try (Connection con = getConnection()) {
			return new Persist(con).readByPrimaryKey(clazz, primaryKeyValues);
		} catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException(e);
		}
	}

	public <T> List<T> readList(Class<T> clazz, String sql,
			Object... parameters) {
		try (Connection con = getConnection()) {
			return new Persist(con).readList(clazz, sql, parameters);
		} catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException(e);
		}
	}

	public List<Map<String, Object>> readMapList(String sql,
			Object... parameters) {
		try (Connection con = getConnection()) {
			return new Persist(con).readMapList(sql, parameters);
		} catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException(e);
		}
	}

	public Map<String, Object> readMap(String sql, Object... parameters) {
		try (Connection con = getConnection()) {
			return new Persist(con).readMap(sql, parameters);
		} catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException(e);
		}
	}

	public void executeUpdate(String sql, Object... parameters) {
		try (Connection con = getConnection()) {
			new Persist(con).executeUpdate(sql, parameters);
		} catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException(e);
		}
	}

	public void insert(Object object) {
		try (Connection con = getConnection()) {
			new Persist(con).insert(object);
		} catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException(e);
		}
	}

	public void insertBatch(Object... objects) {
		try (Connection con = getConnection()) {
			new Persist(con).insertBatch(objects);
		} catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException(e);
		}
	}

	public void insertInto(String tableName, String[] columnNames,
			Object... values) {
		if (columnNames.length != values.length) {
			throw new RuntimeException(
					"columnNames length must equals to values length. ");
		}

		executeUpdate("INSERT INTO " + tableName + " ("
				+ String.join(",", columnNames) + ") VALUES "
				+ createPlaceHolder(columnNames.length), values);
	}

	public void insertInto(String tableName,
			List<Map<String, Object>> listOfPairsOfNameAndValue) {
		List<List<Map<String, Object>>> splited = new ArrayList<>();
		int maxSize = 1000;
		for (int i = 0; i <= (listOfPairsOfNameAndValue.size() - 1)
				/ maxSize; i++) {
			splited.add(new ArrayList<>(listOfPairsOfNameAndValue.subList(
					maxSize * i,
					maxSize * (i + 1) <= listOfPairsOfNameAndValue.size()
							? maxSize * (i + 1)
							: listOfPairsOfNameAndValue.size())));
		}

		for (List<Map<String, Object>> t : splited) {
			insertIntoAux(tableName, t);
		}
	}

	public static void main(String[] args) {
		RDBUtil rdbUtil = new RDBUtil(
				new RDBConfig(
						"jdbc:h2:tcp://localhost/"
								+ new File(
										new File(System
												.getProperty("java.io.tmpdir")),
										"latLonIshiyama").getAbsolutePath(),
						"sa", ""));

		rdbUtil.delete("Traffic", "Settings=?", "TEST");
		List<Map<String, Object>> listOfPairsOfNameAndValue = new ArrayList<>();
		for (int i = 0; i < 1; i++) {
			Map<String, Object> tmp = new HashMap<>();
			tmp.put("settings", "TEST");
			tmp.put("vehicle_id", i);
			tmp.put("step", 2);
			listOfPairsOfNameAndValue.add(tmp);
		}

		rdbUtil.insertInto("TRAFFIC", listOfPairsOfNameAndValue);
	}

	private void insertIntoAux(String tableName,
			List<Map<String, Object>> listOfPairsOfNameAndValue) {
		List<String> columnNames = new ArrayList<>(
				listOfPairsOfNameAndValue.get(0).keySet());

		List<String> placeHolders = new ArrayList<>();
		for (int i = 0; i < listOfPairsOfNameAndValue.size(); i++) {
			placeHolders.add(createPlaceHolder(columnNames.size()));
		}

		List<Object> values = new ArrayList<>();

		for (Map<String, Object> pair : listOfPairsOfNameAndValue) {
			for (String key : columnNames) {
				values.add(pair.get(key));
			}
		}

		executeUpdate("INSERT INTO " + tableName + " ("
				+ String.join(",", columnNames) + ") VALUES "
				+ String.join(",", placeHolders), values.toArray());

	}

	private String createPlaceHolder(int size) {
		String[] tmp = new String[size];
		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = "?";
		}
		String p = "(" + String.join(",", tmp) + ")";
		return p;
	}

	public void deleteAll(String tableName) {
		executeUpdate("DELETE FROM " + tableName);
	}

	public void delete(String tableName, String condition,
			Object... parameters) {
		executeUpdate("DELETE FROM " + tableName + " WHERE " + condition,
				parameters);
	}

	public void dropIfExists(String tableName) {
		executeUpdate("DROP TABLE " + tableName + " IF EXISTS");
	}

	public void createTableIfNotExists(String tableSchema) {
		executeUpdate("CREATE TABLE IF NOT EXISTS " + tableSchema);
	}

	public void createIndexIfNotExists(String indexName, String targetTable,
			String... columns) {
		executeUpdate("CREATE INDEX IF NOT EXISTS " + indexName + " ON "
				+ targetTable + " (" + String.join(",", columns) + ");");

	}

	public void renameTableTo(String fromTableName, String toTableName) {
		executeUpdate(
				"ALTER TABLE " + fromTableName + " RENAME TO " + toTableName);
	}

	public boolean checkConnection() {
		try (Connection con = getConnection()) {
			return true;
		} catch (Exception e) {
			log.warn("H2 server has not been started yet.");
			return false;
		}
	}

	@SuppressWarnings("unused")
	private Connection keepConnection;

	public Connection getConnection() throws SQLException {
		Connection con = DriverManager.getConnection(conf.getJdbcURL(),
				conf.getUsername(), conf.getPassword());
		if (conf.getJdbcURL().startsWith("jdbc:h2:mem:")) {
			keepConnection = con;
		}
		return con;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public Optional<Process> useDatabaseServer() {
		return userDatabaseServerWithClasspath(
				System.getProperty("java.class.path"));
	}

	public void useDatabaseServerFromServlet() {
		String h2Classpath = new File(
				new File(RDBUtil.getClasspath().getParent().toFile(), "lib"),
				"*").toString();
		useDatabaseServer(h2Classpath);
	}

	private Optional<Process> useDatabaseServer(String h2Classpath) {
		return userDatabaseServerWithClasspath(
				System.getProperty("java.class.path") + File.pathSeparator
						+ h2Classpath);
	}

	public Optional<Process> userDatabaseServerWithClasspath(String classpath) {
		if (checkConnection()) {
			log.debug("H2 server has been already started.");
			return null;
		}

		String[] args = { "java", "-cp", classpath, "org.h2.tools.Server",
				"-webAllowOthers", "-tcpAllowOthers" };
		log.debug("Try to start h2 server. {}", Arrays.asList(args));
		ProcessBuilder pb = new ProcessBuilder(args);
		Process p = null;
		try {
			p = pb.start();
			Thread.sleep(4000);
		} catch (InterruptedException | IOException e) {
			log.error(e, e);
			throw new RuntimeException(e);
		}

		if (!checkConnection()) {
			return null;
		}
		log.debug("Start h2 server is suceeded.");
		return Optional.of(p);
	}

	public static Path getClasspath() {
		try {
			return Paths.get(Thread.currentThread().getContextClassLoader()
					.getResource("").toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

}
