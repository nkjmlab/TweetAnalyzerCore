package org.nkjmlab.twitter.crawler;

import java.util.List;

import org.nkjmlab.twitter.model.Tweet;
import org.nkjmlab.twitter.model.TweetDB;

import twitter4j.Query;
import twitter4j.Status;

public class RecordQueryAndResponse implements ProcedureForCollectedTweets {
	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager
			.getLogger();
	private String tableName;
	private TweetDB tweetDB;

	/**
	 *
	 * @param tweetDB
	 *            データを保存するデータベース
	 * @param tableName
	 *            データを保存するテーブル名
	 */
	public RecordQueryAndResponse(TweetDB tweetDB, String tableName) {
		this.tableName = tableName;
		this.tweetDB = tweetDB;
		tweetDB.createTweetTable(tableName);
	}

	@Override
	public void apply(Query query, List<Status> rawTweets) {
		List<Tweet> tweets = Tweet.convertStatusToTweets(rawTweets);
		tweetDB.insertTweets(tableName, tweets);
	}

}
