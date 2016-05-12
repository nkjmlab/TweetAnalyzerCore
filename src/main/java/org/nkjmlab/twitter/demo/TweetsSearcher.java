package org.nkjmlab.twitter.demo;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nkjmlab.twitter.crawler.TweetsBackwardCrawler;
import org.nkjmlab.twitter.crawler.TweetsBackwardCrawlerFactory;
import org.nkjmlab.twitter.model.QueryFactory;
import org.nkjmlab.twitter.model.Tweet;

import twitter4j.Query;
import twitter4j.Status;

public class TweetsSearcher {
	private static Logger log = LogManager.getLogger();

	public static void main(String[] args) {

		TweetsBackwardCrawler crawler = TweetsBackwardCrawlerFactory.create();

		Query query = QueryFactory.create("野球");

		crawler.crawlTweets(query, (Query q, List<Status> tweets) -> {
			log.debug(Tweet.convertStatusToTweets(tweets));
		});
	}

}
