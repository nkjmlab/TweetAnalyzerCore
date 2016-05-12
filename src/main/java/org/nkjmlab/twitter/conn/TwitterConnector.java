package org.nkjmlab.twitter.conn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONException;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

public class TwitterConnector {
	public static void main(String[] args)
			throws JSONException, FileNotFoundException, IOException {

		TwitterConfig conf = JSON.decode(
				new FileReader(new File("conf/twitter.conf")),
				TwitterConfig.class);

		try {
			Twitter twitter = TwitterConnector.create(conf);
			User user = twitter.verifyCredentials();
			System.out.println(user);
			List<Status> statuses = twitter.getHomeTimeline();
			System.out.println("Showing home timeline.");
			for (Status status : statuses) {
				System.out.println(
						status.getUser().getName() + ":" + status.getText());
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
