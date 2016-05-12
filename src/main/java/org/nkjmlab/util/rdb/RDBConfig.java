package org.nkjmlab.util.rdb;

import java.io.File;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class RDBConfig {

	private String jdbcURL;
	private String username;
	private String password;

	public RDBConfig(String jdbcURL, String username, String password) {
		this.jdbcURL = jdbcURL;
		this.username = username;
		this.password = password;
	}

	public RDBConfig(String jdbcURL) {
		this(jdbcURL, "sa", "");
	}

	/**
	 * System.getProperty("java.io.tmpdir") でtemporalなディレクトリがとれる．
	 *
	 * @param dbFile
	 */
	public RDBConfig(File dbFile) {
		this(dbFile, "sa", "");
	}

	public RDBConfig(File dbFile, String username2, String password2) {
		this("jdbc:h2:tcp://localhost/" + dbFile.toString(), username2,
				password2);
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
