package org.nkjmlab.twitter.demo;

import java.util.List;

import org.nkjmlab.twitter.crawler.TweetsBackwardCrawler;
import org.nkjmlab.twitter.model.Tweet;
import org.nkjmlab.twitter.model.TweetsDatabase;
import org.nkjmlab.twitter.util.QueryHelper;
import org.nkjmlab.twitter.util.TwitterFactory;
import org.nkjmlab.util.io.FileUtils;

import twitter4j.Query;
import twitter4j.Twitter;

public class TweetsRecorder {

	protected static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager
			.getLogger();

	public static void main(String[] args) {

		Twitter twitter = TwitterFactory.create("src/main/resources/twitter.conf");
		Query query = QueryHelper.create("人身事故");

		TweetsDatabase tweetsDatabase = new TweetsDatabase(
				FileUtils.getFileInUserDirectory("tweets/tweetsDB"));

		String tableName = "TWEETS";

		new TweetsBackwardCrawler(twitter).crawl(query, (q, rawTweets) -> {
			tweetsDatabase.createTweetTableIfNotExists(tableName);
			List<Tweet> tweets = Tweet.convertStatusToTweets(rawTweets);
			tweetsDatabase.insertQueryAndTweets(q, tableName, tweets);
		});

	}

}
