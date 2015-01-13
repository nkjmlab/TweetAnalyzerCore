package org.nkjmlab.nlp.tweet.model;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nkjmlab.util.DateUtil;
import org.nkjmlab.util.RDBUtil;

public class TweetDB {
	private static Logger log = LogManager.getLogger();

	public static void main(String[] args) {

		List<Tweet> tweets = readTweets("CHOSHI_TWEETS",
				DateUtil.parseFromTimeStamp("2014-11-24" + " 00:00:00"),
				DateUtil.parseFromTimeStamp("2014-11-24" + " 23:59:59"));
		System.out.println(tweets);

	}

	public static List<Tweet> readTweets(String table, Date from, Date to) {
		return readTweets("SELECT * FROM " + table
				+ " WHERE CREATEDAT BETWEEN ? AND ?", DateUtil.format(from),
				DateUtil.format(to));
	}

	public static List<Tweet> readTweets(String sql, Object... objs) {
		List<Tweet> tweets = RDBUtil.readList(Tweet.class, sql, objs);
		return tweets;
	}

	public static void insertTweet(String table, Tweet t) {
		String sql = "INSERT INTO " + table + " VALUES (?,?,?,?,?,?,?,?,?)";
		RDBUtil.executeUpdate(sql, t.getId(), t.getCreatedAt(), t.getLat(),
				t.getLat(), t.getPlace(), t.getUser(), t.getRetweetId(),
				t.getText(), t.getHashtagEntities());
	}

	public static void insertSearchQuery(SearchQuery searchQuery) {
		RDBUtil.insert(searchQuery);
	}

	public static void createTweetTable(String tableName) {
		String schema = "id long PRIMARY KEY," + "createdAt TIMESTAMP,"
				+ "lat DOUBLE," + "lng DOUBLE," + "place VARCHAR,"
				+ "user VARCHAR," + "retweetId long, " + "text VARCHAR,"
				+ "hashtagEntities VARCHAR, ";
		RDBUtil.executeUpdate("CREATE TABLE " + tableName + " (" + schema + ")");

		RDBUtil.executeUpdate("CREATE INDEX " + tableName + "_createdAt ON "
				+ tableName + "(createdAt)");

	}

	public static void insertTweets(List<Tweet> tweets) {
		for (Tweet t : tweets) {
			RDBUtil.insert(t);
			log.debug(t.getId());
		}
	}

}