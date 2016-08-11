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
		TweetsBackwardCrawler crawler = new TweetsBackwardCrawler(twitter);
		TweetsDatabase tweetsDatabase = new TweetsDatabase(
				FileUtils.getFileInUserDirectory("tweets/tweetsDB"));

		Query query = QueryHelper.create("人身事故");

		crawler.crawl(query, (q, rawTweets) -> {
			List<Tweet> tweets = Tweet.convertStatusToTweets(rawTweets);
			tweetsDatabase.insertTweets("TWEETS", tweets);
		});

	}

}
