package org.nkjmlab.nlp.tweet.crawler;

public class TwitterConfig {

	private String accessToken;
	private String accessTokenSecret;
	private String consumerKey;
	private String consumerSecret;

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

}
