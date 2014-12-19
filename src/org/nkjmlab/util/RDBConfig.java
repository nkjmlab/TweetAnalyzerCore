package org.nkjmlab.tweet;

public class Config {

	private String accessToken = "";
	private String accessTokenSecret = "";
	private String consumerKey = "";
	private String consumerSecret = "";

	private String jdbcURL = "jdbc:h2:tcp://localhost/./tweets";
	private String username = "sa";
	private String password = "";

	public String getJdbcURL() {
		return jdbcURL;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
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
