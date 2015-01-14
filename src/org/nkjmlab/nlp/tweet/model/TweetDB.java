package org.nkjmlab.nlp.tweet.model;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nkjmlab.util.DateUtil;
import org.nkjmlab.util.RDBConfig;
import org.nkjmlab.util.RDBUtil;

public class TweetDB {
	private static Logger log = LogManager.getLogger();
	private RDBUtil rdb;

	public void main(String[] args) {

		List<Tweet> tweets = readTweets("CHOSHI_TWEETS",
				DateUtil.parseFromTimeStamp("2014-11-24" + " 00:00:00"),
				DateUtil.parseFromTimeStamp("2014-11-24" + " 23:59:59"));
		System.out.println(tweets);

	}

	public TweetDB(RDBUtil rdb) {
		this.rdb = rdb;
	}

	public TweetDB(RDBConfig conf) {
		this.rdb = new RDBUtil(conf);
	}

	public List<Tweet> readTweets(String table, Date from, Date to) {
		return readTweets("SELECT * FROM " + table
				+ " WHERE CREATEDAT BETWEEN ? AND ?", DateUtil.format(from),
				DateUtil.format(to));
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
				t.getLat(), t.getPlace(), t.getUser(), t.getRetweetId(),
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

	public void drop(String tableName) {
		this.rdb.drop(tableName);
	}

	public <T> List<T> readList(Class<T> clazz, String sql, Object... objs) {
		return this.rdb.readList(clazz, sql, objs);
	}

	public void executeUpdate(String sql, Object... objs) {
		this.rdb.executeUpdate(sql, objs);
	}

	public void create(String tableName, String schema) {
		this.rdb.create(tableName, schema);
	}

}
