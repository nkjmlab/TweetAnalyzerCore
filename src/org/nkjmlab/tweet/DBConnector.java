package org.nkjmlab.tweet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.sf.persist.Persist;

public class DBConnector {

	private Connection con;
	private Persist persist;
	private Config conf;

	public static void main(String[] args) {
		new DBConnector(new Config()).getConnection();
	}

	public DBConnector(Config conf) {
		this.conf = conf;
	}

	public void close() {
		try {
			this.con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Persist getPersist() {
		this.persist = new Persist(getConnection());
		return persist;
	}

	private Connection getConnection() {
		Connection con2 = null;
		try {
			con2 = DriverManager.getConnection(
					"jdbc:h2:tcp://localhost/~/tweets", "sa", "");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		this.con = con2;
		return con2;
	}

}
