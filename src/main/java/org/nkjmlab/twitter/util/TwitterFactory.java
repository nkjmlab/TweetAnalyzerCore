package org.nkjmlab.twitter.util;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import org.nkjmlab.twitter.conf.TwitterConfig;
import org.nkjmlab.util.io.FileUtils;
import org.nkjmlab.util.json.JsonUtils;

import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONException;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.NullAuthorization;

public class TwitterFactory {
	public static void main(String[] args) throws Exception {

		TwitterConfig conf = JSON.decode(
				new FileReader(new File("conf/twitter.conf")), TwitterConfig.class);

		Twitter twitter = TwitterFactory.create(conf);
		User user = twitter.verifyCredentials();
		System.out.println(user);
		List<Status> statuses = twitter.getHomeTimeline();
		System.out.println("Showing home timeline.");
		for (Status status : statuses) {
			System.out.println(
					status.getUser().getName() + ":" + status.getText());
		}
	}

	public static Twitter create(TwitterConfig conf) {
		Twitter twitter = new twitter4j.TwitterFactory().getInstance();
		if (!(twitter.getAuthorization() instanceof NullAuthorization)) {
			return twitter;
		}

		AccessToken accessToken = new AccessToken(conf.getAccessToken(),
				conf.getAccessTokenSecret());
		twitter.setOAuthConsumer(conf.getConsumerKey(),
				conf.getConsumerSecret());
		twitter.setOAuthAccessToken(accessToken);
		return twitter;
	}

	public static Twitter create(String fileName) {
		try {
			return create(JsonUtils.decode(FileUtils.getFileReader(fileName), TwitterConfig.class));
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
}
