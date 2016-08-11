package org.nkjmlab.twitter.crawler;

import java.util.List;

import org.nkjmlab.twitter.model.Tweet;
import org.nkjmlab.twitter.model.TweetsDatabase;

import twitter4j.Query;
import twitter4j.Status;

public class RecordQueryAndResponses implements ProcedureForCollectedTweets {
	protected static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager
			.getLogger();
	private String tableName;
	private TweetsDatabase tweetsDatabase;

	/**
	 * Recording the query and the responses.
	 * @param tweetsDatabase
	 *            データを保存するデータベース
	 * @param tableName
	 *            データを保存するテーブル名
	 */
	public RecordQueryAndResponses(TweetsDatabase tweetsDatabase, String tableName) {
		this.tableName = tableName;
		this.tweetsDatabase = tweetsDatabase;
		tweetsDatabase.createTweetTableIfNotExists(tableName);
	}

	@Override
	public void apply(Query query, List<Status> rawTweets) {
		List<Tweet> tweets = Tweet.convertStatusToTweets(rawTweets);
		tweetsDatabase.insertTweets(tableName, tweets);
	}

}
