package org.nkjmlab.twitter.conn;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONException;

public class TwitterConfig {

	private String accessToken;
	private String accessTokenSecret;
	private String consumerKey;
	private String consumerSecret;

	public TwitterConfig() {

	}

	public TwitterConfig(String accessToken, String accessTokenSecret,
			String consumerKey, String consumerSecret) {
		this.accessToken = accessToken;
		this.accessTokenSecret = accessTokenSecret;
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getAccessTokenSecret() {
		return accessTokenSecret;
	}

	public String getConsumerKey() {
		return consumerKey;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public void setAccessTokenSecret(String accessTokenSecret) {
		this.accessTokenSecret = accessTokenSecret;
	}

	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}

	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}

	public static TwitterConfig create() {
		try {
			return JSON.decode(
					new FileReader(new File(TwitterConfig.class.getClassLoader()
							.getResource("twitter.conf").getPath())),
					TwitterConfig.class);
		} catch (JSONException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
