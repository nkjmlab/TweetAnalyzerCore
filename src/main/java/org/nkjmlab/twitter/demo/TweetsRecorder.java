package org.nkjmlab.twitter.demo;

import java.io.File;

import org.nkjmlab.twitter.crawler.RecordQueryAndResponse;
import org.nkjmlab.twitter.crawler.TweetsBackwardCrawler;
import org.nkjmlab.twitter.crawler.TweetsBackwardCrawlerFactory;
import org.nkjmlab.twitter.model.QueryFactory;
import org.nkjmlab.twitter.model.TweetDB;

import twitter4j.Query;

public class TweetsRecorder {

	protected static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager
			.getLogger();

	public static void main(String[] args) {

		TweetsBackwardCrawler crawler = TweetsBackwardCrawlerFactory.create();

		Query query = QueryFactory.create("人身事故", 729809002255687680L);

		crawler.crawlTweets(query,
				new RecordQueryAndResponse(new TweetDB(new File(
						System.getProperty("java.io.tmpdir"), "tweetsDB")),
						"TWEETS"));
	}

}
