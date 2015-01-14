package org.nkjmlab.util;

public class RDBConfig {

	private String jdbcURL;
	private String username;
	private String password;

	public RDBConfig(String jdbcURL, String username, String password) {
		this.jdbcURL = jdbcURL;
		this.username = username;
		this.password = password;
	}

	public String getJdbcURL() {
		return jdbcURL;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

}
