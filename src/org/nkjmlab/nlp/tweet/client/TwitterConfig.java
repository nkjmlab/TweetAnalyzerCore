package org.nkjmlab.nlp.tweet.client;

import com.orangesignal.csv.annotation.CsvColumn;
import com.orangesignal.csv.annotation.CsvEntity;

@CsvEntity
public class TwitterConfig {

	@CsvColumn(name = "accessToken")
	private String accessToken;
	@CsvColumn(name = "accessTokenSecret")
	private String accessTokenSecret;
	@CsvColumn(name = "consumerKey")
	private String consumerKey;
	@CsvColumn(name = "consumerSecret")
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

}
