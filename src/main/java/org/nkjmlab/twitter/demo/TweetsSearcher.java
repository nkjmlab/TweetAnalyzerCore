package org.nkjmlab.twitter.demo;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nkjmlab.twitter.crawler.TweetsBackwardCrawler;
import org.nkjmlab.twitter.model.Tweet;
import org.nkjmlab.twitter.util.QueryHelper;
import org.nkjmlab.twitter.util.TwitterFactory;

import twitter4j.Query;
import twitter4j.Status;
import twitter4j.Twitter;

public class TweetsSearcher {
	private static Logger log = LogManager.getLogger();

	public static void main(String[] args) {
		Twitter twitter = TwitterFactory.create("src/main/resources/twitter.conf");
		TweetsBackwardCrawler crawler = new TweetsBackwardCrawler(twitter);

		Query query = QueryHelper.create("野球");

		crawler.crawl(query, (Query q, List<Status> tweets) -> {
			log.debug(Tweet.convertStatusToTweets(tweets));
		});
	}

}
