package org.nkjmlab.twitter.crawler;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.nkjmlab.twitter.conn.TwitterConfig;
import org.nkjmlab.twitter.conn.TwitterConnector;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;

public class TweetsBackwardCrawler {

	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager
			.getLogger();

	private ScheduledFuture<?> scheduledTasks;
	private Twitter twitter;

	public TweetsBackwardCrawler(TwitterConfig conf) {
		this.twitter = TwitterConnector.create(conf);
	}

	public TweetsBackwardCrawler(String accessToken, String accessTokenSecret,
			String consumerKey, String consumerSecret) {
		this(new TwitterConfig(accessToken, accessTokenSecret, consumerKey,
				consumerSecret));
	}

	/**
	 * queryのmaxIdを元にtweetsを遡って取得し，取得したツイートに対してActionを実行します．
	 *
	 * @param action
	 * @param query
	 */
	public void crawlTweets(Query query, ProcedureForCollectedTweets action) {

		this.scheduledTasks = Executors.newSingleThreadScheduledExecutor()
				.scheduleWithFixedDelay(new TweetsSearchTask(query, action), 0,
						5, TimeUnit.SECONDS);

	}

	class TweetsSearchTask implements Runnable {
		private ProcedureForCollectedTweets action;
		private Query query;

		public TweetsSearchTask(Query query, ProcedureForCollectedTweets action) {
			this.action = action;
			this.query = query;
		}

		@Override
		public void run() {
			try {

				log.debug(query);
				QueryResult result = twitter.search(query);
				List<Status> tweets = result.getTweets();
				action.apply(query, tweets);

				if (result.hasNext()) {
					query = result.nextQuery();
				} else {
					this.query.setMaxId(tweets.get(tweets.size() - 1).getId());
				}
			} catch (Exception e) {
				log.error("Failed to search tweets: {}.", e.getMessage());
				log.error("Max Id is " + query.getMaxId());
				log.error(query);
				e.printStackTrace();
				scheduledTasks.cancel(true);
			}
		}

	}

}
