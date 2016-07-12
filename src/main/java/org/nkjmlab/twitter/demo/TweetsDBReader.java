package org.nkjmlab.twitter.demo;

import java.util.List;

import org.nkjmlab.twitter.model.Tweet;
import org.nkjmlab.twitter.model.TweetDB;
import org.nkjmlab.util.io.FileUtils;

public class TweetsDBReader {

	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager
			.getLogger();

	public static void main(String[] args) {

		TweetDB tweetDB = new TweetDB(FileUtils.createTempFile("tweetsDB"));

		List<Tweet> tweets = tweetDB.readTweets("SELECT * FROM TWEETS LIMIT ?",
				10);

		for (Tweet tweet : tweets) {
			String text = tweet.getUser();
			log.debug(text);
			log.debug(tweet);
		}
	}

}
