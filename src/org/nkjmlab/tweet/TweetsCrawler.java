package org.nkjmlab.tweet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
	private Collection<String> searchedTweets = new ArrayList<String>();

	private String searchWord;

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
		private int counter = 0;
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
					String line = counter + ":" + tweet.getCreatedAt() + "@"
							+ tweet.getUser().getScreenName() + " - "
							+ tweet.getText();
					System.out.println(line);

					System.out.println(tweet.getId());

					searchedTweets.add(line);
					counter++;
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
	}

}
