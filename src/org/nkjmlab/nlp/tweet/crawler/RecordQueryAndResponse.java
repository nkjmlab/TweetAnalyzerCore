package org.nkjmlab.nlp.tweet.crawler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nkjmlab.nlp.tweet.model.Tweet;
import org.nkjmlab.nlp.tweet.model.TweetDB;

import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.Query;
import twitter4j.Status;

public class RecordQueryAndResponse implements Action {
	private static Logger log = LogManager.getLogger();
	private String table;

	public RecordQueryAndResponse(String table) {
		this.table = table;
		TweetDB.createTweetTable(table);
	}

	@Override
	public void procTweets(Query query, List<Status> rawTweets)
			throws SQLException {
		List<Tweet> tweets = convertStatusToTweets(rawTweets);
		// insertSearchQuery(query);
		TweetDB.insertTweets(table, tweets);
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

}
