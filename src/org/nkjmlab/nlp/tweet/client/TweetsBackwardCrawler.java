package org.nkjmlab.nlp.tweet.client;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;

public class TweetsBackwardCrawler {

	private static Logger log = LogManager.getLogger();

	private long maxId;
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
	public void crawlTweets(Query query, Action action) {
		this.maxId = query.getMaxId() != -1l ? query.getMaxId()
				: Long.MAX_VALUE;
		this.scheduledTasks = Executors.newSingleThreadScheduledExecutor()
				.scheduleWithFixedDelay(new TweetsSearchTask(query, action), 0,
						5, TimeUnit.SECONDS);

	}

	class TweetsSearchTask implements Runnable {
		private Action action;
		private Query query;

		public TweetsSearchTask(Query query, Action action) {
			this.action = action;
			this.query = query;
			this.query.setMaxId(maxId);
		}

		@Override
		public void run() {
			try {

				log.debug(query);
				QueryResult result = twitter.search(query);
				action.procTweets(query, result.getTweets());

				if (result.hasNext()) {
					query = result.nextQuery();
				} else {
					log.info("There is no page.");
					scheduledTasks.cancel(true);
				}

			} catch (Exception e) {
				log.error("Failed to search tweets: " + e.getMessage());
				log.error("Max Id is " + maxId);
				log.error(query);
				e.printStackTrace();
				scheduledTasks.cancel(true);
			}
		}

	}

}
