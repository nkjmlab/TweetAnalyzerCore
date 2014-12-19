package org.nkjmlab.nlp.tweet.crawler;

import twitter4j.GeoLocation;
import twitter4j.Query;

public class TweetsCrawlerLauncher {

	public static void main(String[] args) {
		TweetsCrawlerLauncher launcher = new TweetsCrawlerLauncher();
		launcher.start();
	}

	private void start() {
		new TweetsCrawler().writeTweetsToTable("TWEETS",
				createQuery("慶応", Long.MAX_VALUE));

	}

	private Query createQuery(String searchWord, long maxId) {
		Query query = new Query(searchWord);
		query.setMaxId(maxId);
		return query;

	}

	private Query createQuery(String searchWord, double lat, double lng,
			double radius, long maxId) {

		Query query = createQuery(searchWord, maxId);
		query.setGeoCode(new GeoLocation(lat, lng), radius, Query.KILOMETERS);

		return query;
	}

}
