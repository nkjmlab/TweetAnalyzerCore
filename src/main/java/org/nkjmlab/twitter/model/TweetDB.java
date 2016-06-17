package org.nkjmlab.twitter.model;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nkjmlab.util.db.DbClient;
import org.nkjmlab.util.db.DbClientFactory;
import org.nkjmlab.util.db.DbConfig;
import org.nkjmlab.util.db.H2ConfigFactory;
import org.nkjmlab.util.db.H2Server;

public class TweetDB {
	private static Logger log = LogManager.getLogger();
	private DbClient rdb;

	/**
	 *
	 * @param dbName
	 *            データベースファイル
	 */
	public TweetDB(File dbFile) {
		this(H2ConfigFactory.create(dbFile));
	}

	public TweetDB(DbConfig conf) {
		this.rdb = DbClientFactory.createSimpleClient(conf);
		H2Server.start();
	}

	public List<Tweet> readTweets(String table, Timestamp from, Timestamp to) {
		return readTweets(
				"SELECT * FROM " + table
						+ " WHERE CREATEDAT BETWEEN ? AND ? ORDER BY CREATEDAT",
				from, to);
	}

	public List<Tweet> readTweets(String sql, Object... objs) {
		List<Tweet> tweets = rdb.readList(Tweet.class, sql, objs);
		return tweets;
	}

	public void insertTweet(String table, Tweet t) {
		if (rdb.read(Tweet.class, "SELECT * FROM " + table + " WHERE ID=?",
				t.getId()) != null) {
			return;
		}
		String sql = "INSERT INTO " + table + " VALUES (?,?,?,?,?,?,?,?,?)";
		rdb.executeUpdate(sql, t.getId(), t.getCreatedAt(), t.getLat(),
				t.getLng(), t.getPlace(), t.getUser(), t.getRetweetId(),
				t.getText(), t.getHashtagEntities());
	}

	public void createTweetTable(String tableName) {
		String schema = "id long PRIMARY KEY," + "createdAt TIMESTAMP,"
				+ "lat DOUBLE," + "lng DOUBLE," + "place VARCHAR,"
				+ "user VARCHAR," + "retweetId long, " + "text VARCHAR,"
				+ "hashtagEntities VARCHAR, ";
		rdb.executeUpdate("CREATE TABLE IF NOT EXISTS " + tableName + " ("
				+ schema + ")");

		rdb.executeUpdate("CREATE INDEX IF NOT EXISTS " + tableName
				+ "_createdAt ON " + tableName + "(createdAt)");

		rdb.executeUpdate("CREATE INDEX IF NOT EXISTS " + tableName
				+ "_user ON " + tableName + "(user)");

	}

	public void insertTweets(String table, List<Tweet> tweets) {
		for (Tweet t : tweets) {
			insertTweet(table, t);
			log.debug(t.getId());
		}
	}

	public void dropTableIfExists(String tableName) {
		this.rdb.dropTableIfExists(tableName);
	}

	public <T> List<T> readList(Class<T> clazz, String sql, Object... objs) {
		return this.rdb.readList(clazz, sql, objs);
	}

	public <T> T readByPrimaryKey(Class<T> clazz, Object primaryKeyValue) {
		return this.rdb.readByPrimaryKey(clazz, primaryKeyValue);
	}

	public void executeUpdate(String sql, Object... objs) {
		this.rdb.executeUpdate(sql, objs);
	}

	public void createTableIfNotExists(String schema) {
		this.rdb.createTableIfNotExists(schema);
	}

	public List<Map<String, Object>> readMapList(String sql, Object... objs) {
		return this.rdb.readMapList(sql, objs);
	}

	public void insert(Object obj) {
		this.rdb.insert(obj);
	}

	public <T> T read(Class<T> clazz, String sql, Object... objs) {
		return rdb.read(clazz, sql, objs);
	}

}
