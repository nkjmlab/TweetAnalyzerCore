package org.nkjmlab.nlp.tweet.crawler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.persist.Persist;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nkjmlab.nlp.tweet.model.Tweet;
import org.nkjmlab.util.RDBConnector;

import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.Status;

public class InsertToTable implements Action {
	private static Logger log = LogManager.getLogger();

	private final String tableName;

	public InsertToTable(String tableName) {
		this.tableName = tableName;
	}

	@Override
	public void procTweets(List<Status> rawTweets) throws SQLException {
		List<Tweet> tweets = convertStatusToTweets(rawTweets);
		insert(tweets);
	}

	public List<Tweet> convertStatusToTweets(List<Status> tweets) {
		List<Tweet> result = new ArrayList<Tweet>();

		for (Status tweet : tweets) {
			long id = tweet.getId();
			Date createdAt = tweet.getCreatedAt();

			GeoLocation geoLocation = tweet.getGeoLocation();
			double lat = 0.0;
			double lng = 0.0;

			if (geoLocation != null) {
				lat = geoLocation.getLatitude();
				lng = geoLocation.getLongitude();
			}

			String hashtagEntities = "#";

			for (HashtagEntity hashtag : tweet.getHashtagEntities()) {
				hashtagEntities += hashtag.getText() + ",#";
			}

			String place = tweet.getPlace() == null ? null : tweet.getPlace()
					.getName();
			String text = tweet.getText();
			String user = tweet.getUser() == null ? null : tweet.getUser()
					.getScreenName();

			long retweetId = tweet.getRetweetedStatus() == null ? -1 : tweet
					.getRetweetedStatus().getId();

			Tweet t = new Tweet(id, createdAt, lat, lng, place, user,
					retweetId, text, hashtagEntities);
			result.add(t);
		}

		return result;

	}

	public void insert(List<Tweet> tweets) throws SQLException {
		for (Tweet tweet : tweets) {
			try (Connection con = RDBConnector.getConnection()) {
				Persist persist = new Persist(con);
				String match = persist.read(String.class, "(select id FROM "
						+ tableName + " WHERE id=?)", tweet.getId());
				if (match == null) {
					persist.insert(tweet);
					log.debug("Insert a new tweet.");
				} else {
					log.debug("This tweet has been already inserted. id="
							+ tweet.getId());
				}
			}
		}
	}

}
