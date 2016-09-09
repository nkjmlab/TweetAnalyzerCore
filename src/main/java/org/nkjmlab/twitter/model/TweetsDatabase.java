package org.nkjmlab.twitter.model;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.nkjmlab.twitter.crawler.TweetsBackwardCrawler;
import org.nkjmlab.util.db.Database;
import org.nkjmlab.util.db.DbClient;
import org.nkjmlab.util.db.DbConfig;
import org.nkjmlab.util.db.H2ConfigFactory;

import twitter4j.Query;
import twitter4j.Twitter;

public class TweetsDatabase extends Database {

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
		super(conf);
		createTableIfNotExists(QueryLog.getSchema());
	}

	/**
	 * Creating the table named {@code tableName} and creating index too.
	 * @param tableName
	 */
	public void createTweetTableIfNotExists(String tableName) {
		createTableIfNotExists(Tweet.getSchema(tableName));
		createIndexIfNotExists(tableName + "_created", tableName, "created");
		createIndexIfNotExists(tableName + "_user", tableName, "user");
	}

	public void dropTableIfExists(String tableName) {
		dropTableIfExists(tableName);
	}

	/**
	 * Deligating {@link DbClient#read(Class, String, Object...)}.
	 */
	public List<Tweet> readTweets(String sql, Object... objs) {
		return readList(Tweet.class, sql, objs);
	}

	public void insertTweet(String tableName, Tweet tweet) {
		if (read(Tweet.class, "SELECT * FROM " + tableName + " WHERE ID=?",
				tweet.getId()) != null) {
			return;
		}
		String sql = "INSERT INTO " + tableName + " VALUES (?,?,?,?,?,?,?,?,?)";
		executeUpdate(sql, tweet.getId(), tweet.getCreated(), tweet.getLat(),
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
		tweetIds.forEach(tweetId -> insert(new QueryLog(query, tableName, tweetId)));
	}

	public void crawl(String tableName, Query query, Twitter twitter) {
		new TweetsBackwardCrawler(twitter).crawl(query, (q, rawTweets) -> {
			createTweetTableIfNotExists(tableName);
			List<Tweet> tweets = Tweet.convertStatusToTweets(rawTweets);
			insertQueryAndTweets(q, tableName, tweets);
		});
	}

}
