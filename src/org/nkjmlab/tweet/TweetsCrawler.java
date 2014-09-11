package org.nkjmlab.tweet;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.sf.persist.Persist;
import twitter4j.HashtagEntity;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TweetsCrawler implements Runnable {

	private Twitter twitter = new TwitterFactory().getInstance();

	private ScheduledExecutorService searchExecutor;

	private String searchWord;

	private Persist persist = new DBConnector(new Config()).getPersist();

	public static void main(String[] args) {
		new TweetsCrawler("巨人").run();
	}

	public TweetsCrawler(String searchWord) {
		this.searchWord = searchWord;
	}

	@Override
	public void run() {
		prepareTwitter();

		searchExecutor = Executors.newSingleThreadScheduledExecutor();
		searchExecutor.scheduleWithFixedDelay(new TweetsSearchTask(searchWord),
				0, 5, TimeUnit.SECONDS);

	}

	private void prepareTwitter() {
		Config conf = new Config();
		AccessToken accessToken = new AccessToken(conf.getAccessToken(),
				conf.getAccessTokenSecret());
		twitter.setOAuthConsumer(conf.getConsumerKey(),
				conf.getConsumerSecret());
		twitter.setOAuthAccessToken(accessToken);

	}

	class TweetsSearchTask implements Runnable {

		private Query query;
		private long maxId = Long.MAX_VALUE;

		public TweetsSearchTask(String searchWord) {
			this.query = new Query(searchWord);
		}

		@Override
		public void run() {
			query.setCount(100);

			try {
				QueryResult result = twitter.search(query);
				List<Status> tweets = result.getTweets();
				for (Status tweet : tweets) {

					System.out.println(tweet.getId());
					insert(tweet);
					maxId = tweet.getId();
				}

				query.setMaxId(maxId);

				if (query == null) {
					System.err.println("Query is null. Max Id is " + maxId);
					searchExecutor.shutdownNow();
				}

			} catch (TwitterException te) {
				te.printStackTrace();
				System.err.println("Failed to search tweets: "
						+ te.getMessage());
				System.err.println("Max Id is " + maxId);
			}
		}

		public void insert(Status tweet) {
			try {
				String sql = "INSERT INTO TWEETS VALUES (?,?,?,?,?,?,?,?)";

				long id = tweet.getId();
				String createdAt = tweet.getCreatedAt().toString();
				String geoLocation = tweet.getGeoLocation() == null ? null
						: tweet.getGeoLocation().toString();
				String hashEntities = "#";

				for (HashtagEntity hashtag : tweet.getHashtagEntities()) {
					hashEntities += hashtag.getText() + ",#";
				}

				String place = tweet.getPlace() == null ? null : tweet
						.getPlace().getName();
				String text = tweet.getText();
				String user = tweet.getUser() == null ? null : tweet.getUser()
						.getScreenName();

				String retweetId = tweet.getRetweetedStatus() == null ? null
						: String.valueOf(tweet.getRetweetedStatus().getId());

				String match = persist.read(String.class,
						"(select id FROM TWEETS WHERE id=?)", id);
				if (match == null) {
					persist.executeUpdate(sql, id, createdAt, geoLocation,
							hashEntities, place, user, retweetId, text);
					System.out.println("Insert a new tweet.");
				} else {
					System.out
							.println("This tweet has been already inserted. id="
									+ id);
				}

			} catch (Throwable e) {
				e.printStackTrace();
			}

		}
	}

}
