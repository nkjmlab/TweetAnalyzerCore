package org.nkjmlab.twitter.model;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nkjmlab.util.db.DbClient;
import org.nkjmlab.util.db.DbClientFactory;
import org.nkjmlab.util.db.DbConfig;
import org.nkjmlab.util.db.H2ClientWithConnectionPool;
import org.nkjmlab.util.db.H2ConfigFactory;
import org.nkjmlab.util.db.H2Server;

import twitter4j.Query;

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
		dbClient.createTableIfNotExists(QueryLog.getTableSchema());
	}

	/**
	 * Creating the table named {@code tableName} and creating index too.
	 * @param tableName
	 */
	public void createTweetTableIfNotExists(String tableName) {
		dbClient.createTableIfNotExists(tableName + Tweet.getRelationalSchema());
		dbClient.createIndexIfNotExists(tableName + "_created", tableName, "created");
		dbClient.createIndexIfNotExists(tableName + "_user", tableName, "user");
	}

	public void dropTableIfExists(String tableName) {
		dbClient.dropTableIfExists(tableName);
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
		dbClient.executeUpdate(sql, tweet.getId(), tweet.getCreated(), tweet.getLat(),
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

	/**
	 * Inserting tweets to the table with query for getting the tweets.
	 * @param query
	 * @param tableName
	 *   the table to be written the tweets.
	 * @param tweets
	 *   the tweets to write.
	 */
	public void insertQueryAndTweets(Query query, String tableName, List<Tweet> tweets) {
		tweets.forEach(t -> insertTweet(tableName, t));
		insertQuery(query, tableName,
				tweets.stream().map(t -> t.getId()).collect(Collectors.toList()));
	}

	private void insertQuery(Query query, String tableName, List<Long> tweetIds) {
		tweetIds.forEach(tweetId -> dbClient.insert(new QueryLog(query, tableName, tweetId)));
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
