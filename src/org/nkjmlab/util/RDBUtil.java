package org.nkjmlab.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import net.sf.persist.Persist;

import org.nkjmlab.nlp.tweet.model.Tweet;

public class RDBUtil {

	public static List<Tweet> readTweets(String sql, Object... objs) {
		try (Connection con = RDBConnector.getConnection()) {
			Persist persist = new Persist(con);
			List<Tweet> tweets = persist.readList(Tweet.class, sql, objs);
			return tweets;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void drop(String tableName) {
		try (Connection con = RDBConnector.getConnection(new RDBConfig())) {
			Persist persist = new Persist(con);
			persist.executeUpdate("DROP TABLE " + tableName + " IF EXISTS");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void create(String tableName, String schema) {
		try (Connection con = RDBConnector.getConnection(new RDBConfig())) {
			Persist persist = new Persist(con);
			persist.executeUpdate("CREATE TABLE " + tableName + " (" + schema
					+ ")");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
