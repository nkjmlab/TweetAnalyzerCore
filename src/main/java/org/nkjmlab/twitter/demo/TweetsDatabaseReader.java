package org.nkjmlab.twitter.demo;

import java.util.List;

import org.nkjmlab.twitter.model.Tweet;
import org.nkjmlab.twitter.model.TweetsDatabase;
import org.nkjmlab.util.io.FileUtils;

public class TweetsDatabaseReader {

	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager
			.getLogger();

	public static void main(String[] args) {

		TweetsDatabase tweetsDatabase = new TweetsDatabase(
				FileUtils.getFileInUserDirectory("h2/tweets"));

		List<Tweet> tweets = tweetsDatabase.readTweets("SELECT * FROM TWEETS LIMIT ?", 10);

		for (Tweet tweet : tweets) {
			String text = tweet.getUser();
			log.debug(text);
			log.debug(tweet);
		}
	}

}
