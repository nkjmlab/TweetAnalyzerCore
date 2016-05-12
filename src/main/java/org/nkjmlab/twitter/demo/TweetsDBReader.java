package org.nkjmlab.twitter.demo;

import java.io.File;
import java.util.List;

import org.nkjmlab.twitter.model.Tweet;
import org.nkjmlab.twitter.model.TweetDB;

public class TweetsDBReader {

	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager
			.getLogger();

	public static void main(String[] args) {

		TweetDB tweetDB = new TweetDB(
				new File(System.getProperty("java.io.tmpdir"), "tweetsDB"));

		List<Tweet> tweets = tweetDB.readTweets("SELECT * FROM TWEETS LIMIT ?",
				10);

		for (Tweet tweet : tweets) {
			String text = tweet.getUser();
			log.debug(text);
			log.debug(tweet);
		}
	}

}
