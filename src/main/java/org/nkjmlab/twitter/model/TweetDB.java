package org.nkjmlab.twitter.model;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
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
	private DbClient dbClient;

	static {
		H2Server.start();
	}

	/**
	 *
	 * @param dbFile
	 */
	public TweetDB(File dbFile) {
		this(H2ConfigFactory.create(dbFile));
	}

	public TweetDB(DbConfig conf) {
		this.dbClient = DbClientFactory.createSimpleClient(conf);
	}

	public List<Tweet> readTweetsBetween(String table, Timestamp from, Timestamp to) {
		return readTweets("SELECT * FROM " + table + " WHERE CREATEDAT BETWEEN ? AND ? ORDER BY CREATEDAT", from, to);
	}

	public List<Tweet> readTweets(String sql, Object... objs) {
		List<Tweet> tweets = dbClient.readList(Tweet.class, sql, objs);
		return tweets;
	}

	public void insertTweet(String table, Tweet tweet) {
		if (dbClient.read(Tweet.class, "SELECT * FROM " + table + " WHERE ID=?", tweet.getId()) != null) {
			return;
		}
		String sql = "INSERT INTO " + table + " VALUES (?,?,?,?,?,?,?,?,?)";
		dbClient.executeUpdate(sql, tweet.getId(), tweet.getCreatedAt(), tweet.getLat(), tweet.getLng(),
				tweet.getPlace(), tweet.getUser(), tweet.getRetweetId(), tweet.getText(), tweet.getHashtagEntities());
	}

	public void createTweetTable(String tableName) {
		dbClient.createTableIfNotExists(tableName + "(id long PRIMARY KEY," + "createdAt TIMESTAMP," + "lat DOUBLE,"
				+ "lng DOUBLE," + "place VARCHAR," + "user VARCHAR," + "retweetId long, " + "text VARCHAR,"
				+ "hashtagEntities VARCHAR)");

		dbClient.createIndexIfNotExists(tableName + "_createdAt", tableName, "createdAt");

		dbClient.createIndexIfNotExists(tableName + "_user", tableName, "user");

	}

	public void insertTweets(String table, List<Tweet> tweets) {
		List<Long> ids = new ArrayList<>();
		for (Tweet t : tweets) {
			insertTweet(table, t);
			ids.add(t.getId());
		}
		log.debug("try to insert tweet ids =>{}", ids);
	}

	public void dropTableIfExists(String tableName) {
		this.dbClient.dropTableIfExists(tableName);
	}

	public <T> List<T> readList(Class<T> clazz, String sql, Object... objs) {
		return this.dbClient.readList(clazz, sql, objs);
	}

	public <T> T readByPrimaryKey(Class<T> clazz, Object primaryKeyValue) {
		return this.dbClient.readByPrimaryKey(clazz, primaryKeyValue);
	}

	public void executeUpdate(String sql, Object... objs) {
		this.dbClient.executeUpdate(sql, objs);
	}

	public void createTableIfNotExists(String schema) {
		this.dbClient.createTableIfNotExists(schema);
	}

	public List<Map<String, Object>> readMapList(String sql, Object... objs) {
		return this.dbClient.readMapList(sql, objs);
	}

	public void insert(Object obj) {
		this.dbClient.insert(obj);
	}

	public <T> T read(Class<T> clazz, String sql, Object... objs) {
		return dbClient.read(clazz, sql, objs);
	}

}
