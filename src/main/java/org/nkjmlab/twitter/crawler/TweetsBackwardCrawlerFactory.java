package org.nkjmlab.twitter.crawler;

import org.nkjmlab.twitter.conn.TwitterConfig;

public class TweetsBackwardCrawlerFactory {

	public static TweetsBackwardCrawler create() {
		return new TweetsBackwardCrawler(TwitterConfig.create());
	}

}
