package org.nkjmlab.twitter.model;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nkjmlab.util.db.DbClient;
import org.nkjmlab.util.db.DbClientFactory;
import org.nkjmlab.util.db.DbConfig;
import org.nkjmlab.util.db.H2ClientWithConnectionPool;
import org.nkjmlab.util.db.H2ConfigFactory;
import org.nkjmlab.util.db.H2Server;

public class TweetsDatabase {
	protected static Logger log = LogManager.getLogger();
	protected H2ClientWithConnectionPool dbClient;

	static {
		H2Server.start();
	}

	/**
	 * Tweets database object related with dbFile.
	 * @param dbFile
	 *  the file accumulating tweets.
	 */
	public TweetsDatabase(File dbFile) {
		this(H2ConfigFactory.create(dbFile));
	}

	/**
	 * Tweets database object using the database config.
	 * @param conf
	 *  the conf to access database.
	 */
	public TweetsDatabase(DbConfig conf) {
		this.dbClient = DbClientFactory.createH2ClientWithConnectionPool(conf);
	}

	/**
	 * Creating the table named {@code tableName} and creating index too.
	 * @param tableName
	 */
	public void createTweetTableIfNotExists(String tableName) {
		dbClient.createTableIfNotExists(tableName + Tweet.getRelationalSchema());
		dbClient.createIndexIfNotExists(tableName + "_createdAt", tableName, "createdAt");
		dbClient.createIndexIfNotExists(tableName + "_user", tableName, "user");

	}

	/**
	 * Deligating {@link DbClient#read(Class, String, Object...)}.
	 */
	public List<Tweet> readTweets(String sql, Object... objs) {
		return dbClient.readList(Tweet.class, sql, objs);
	}

	public void insertTweet(String table, Tweet tweet) {
		if (dbClient.read(Tweet.class, "SELECT * FROM " + table + " WHERE ID=?",
				tweet.getId()) != null) {
			return;
		}
		String sql = "INSERT INTO " + table + " VALUES (?,?,?,?,?,?,?,?,?)";
		dbClient.executeUpdate(sql, tweet.getId(), tweet.getCreatedAt(), tweet.getLat(),
				tweet.getLon(), tweet.getPlace(), tweet.getUser(), tweet.getRetweetId(),
				tweet.getText(), tweet.getHashtagEntities());
	}

	/**
	 * Inserting tweets to the table.
	 * @param table
	 *   the table to be written the tweets.
	 * @param tweets
	 *   the tweets to write.
	 */
	public void insertTweets(String table, List<Tweet> tweets) {
		tweets.forEach(t -> insertTweet(table, t));
	}

	public H2ClientWithConnectionPool getDbClient() {
		return dbClient;
	}

	/**
	 * Closing db connection.
	 */
	public void close() {
		dbClient.dispose();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		dbClient.dispose();
	}

}
