package org.nkjmlab.twitter.demo;

import org.nkjmlab.twitter.crawler.RecordQueryAndResponse;
import org.nkjmlab.twitter.crawler.TweetsBackwardCrawler;
import org.nkjmlab.twitter.crawler.TweetsBackwardCrawlerFactory;
import org.nkjmlab.twitter.model.QueryFactory;
import org.nkjmlab.twitter.model.TweetDB;
import org.nkjmlab.util.io.FileUtils;

import twitter4j.Query;

public class TweetsRecorder {

	protected static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager
			.getLogger();

	public static void main(String[] args) {

		TweetsBackwardCrawler crawler = TweetsBackwardCrawlerFactory.create();

		Query query = QueryFactory.create("人身事故");

		crawler.crawlTweets(query, new RecordQueryAndResponse(
				new TweetDB(FileUtils.createTempFile("tweetsDB")), "TWEETS"));
	}

}
