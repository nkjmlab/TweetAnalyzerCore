package org.nkjmlab.nlp.tweet.crawler;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import net.sf.persist.Persist;

import org.apache.log4j.Logger;
import org.nkjmlab.util.RDBConnector;

import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TweetsCrawler {

	private static Logger log = Logger.getLogger(TweetsCrawler.class);

	private long maxId;
	private ScheduledFuture<?> scheduledTasks;

	public TweetsCrawler() {
	}

	public void writeTweetsToTable(String tableName, Query query) {
		this.maxId = query.getMaxId();
		scheduledTasks = Executors.newSingleThreadScheduledExecutor()
				.scheduleWithFixedDelay(new TweetsSearchTask(tableName, query),
						0, 5, TimeUnit.SECONDS);

	}

	class TweetsSearchTask implements Runnable {
		private final String tableName;
		private final Query query;

		public TweetsSearchTask(String tableName, Query query) {
			this.tableName = tableName;
			this.query = query;
			this.query.setMaxId(maxId);
		}

		private Twitter prepareTwitter() {
			Twitter twitter = new TwitterFactory().getInstance();
			TwitterConfig conf = new TwitterConfig();
			AccessToken accessToken = new AccessToken(conf.getAccessToken(),
					conf.getAccessTokenSecret());
			twitter.setOAuthConsumer(conf.getConsumerKey(),
					conf.getConsumerSecret());
			twitter.setOAuthAccessToken(accessToken);
			return twitter;
		}

		@Override
		public void run() {
			try {

				QueryResult result = prepareTwitter().search(query);
				List<Status> tweets = result.getTweets();
				if (tweets.size() == 0) {
					log.error("No Result. maxId=" + maxId);
				}

				for (Status tweet : tweets) {
					insert(tweet);
					maxId = tweet.getId() < maxId ? tweet.getId() : maxId;
				}
			} catch (TwitterException e) {
				log.error("Failed to search tweets: " + e.getMessage());
				log.error("Max Id is " + maxId);
				e.printStackTrace();
				scheduledTasks.cancel(true);
			} catch (SQLException e) {
				e.printStackTrace();
				scheduledTasks.cancel(true);
			}
		}

		public void insert(Status tweet) throws SQLException {
			long id = tweet.getId();
			String createdAt = tweet.getCreatedAt().toString();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			createdAt = sdf.format(tweet.getCreatedAt());

			GeoLocation geoLocation = tweet.getGeoLocation();
			double lat = 0.0;
			double lng = 0.0;

			if (geoLocation != null) {
				lat = geoLocation.getLatitude();
				lng = geoLocation.getLongitude();
			}

			String hashEntities = "#";

			for (HashtagEntity hashtag : tweet.getHashtagEntities()) {
				hashEntities += hashtag.getText() + ",#";
			}

			String place = tweet.getPlace() == null ? null : tweet.getPlace()
					.getName();
			String text = tweet.getText();
			String user = tweet.getUser() == null ? null : tweet.getUser()
					.getScreenName();

			String retweetId = tweet.getRetweetedStatus() == null ? null
					: String.valueOf(tweet.getRetweetedStatus().getId());

			try (Connection con = RDBConnector.getConnection()) {
				Persist persist = new Persist(con);
				String match = persist.read(String.class, "(select id FROM "
						+ tableName + " WHERE id=?)", id);
				if (match == null) {
					log.debug(String.valueOf(id));

					String sql = "INSERT INTO " + tableName
							+ " VALUES (?,?,?,?,?,?,?,?,?)";

					persist.executeUpdate(sql, id, createdAt, lat, lng, place,
							user, retweetId, text, hashEntities);
					log.debug("Insert a new tweet.");
				} else {
					log.debug("This tweet has been already inserted. id=" + id);
				}
			}

		}
	}

}
