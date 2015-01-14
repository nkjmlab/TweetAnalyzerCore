package org.nkjmlab.nlp.tweet.client;

import java.io.File;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

public class TwitterConnector {
	public static void main(String[] args) {

		TwitterConfig conf = TwitterConfigFactory.create(new File(
				"conf/twitter.conf"));

		try {
			Twitter twitter = TwitterConnector.create(conf);
			User user = twitter.verifyCredentials();
			System.out.println(user);
			List<Status> statuses = twitter.getHomeTimeline();
			System.out.println("Showing home timeline.");
			for (Status status : statuses) {
				System.out.println(status.getUser().getName() + ":"
						+ status.getText());
			}
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

	public static Twitter create(TwitterConfig conf) {
		Twitter twitter = new TwitterFactory().getInstance();
		AccessToken accessToken = new AccessToken(conf.getAccessToken(),
				conf.getAccessTokenSecret());
		twitter.setOAuthConsumer(conf.getConsumerKey(),
				conf.getConsumerSecret());
		twitter.setOAuthAccessToken(accessToken);
		return twitter;
	}
}
