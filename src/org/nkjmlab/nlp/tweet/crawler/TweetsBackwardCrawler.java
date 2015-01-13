package org.nkjmlab.nlp.tweet.crawler;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TweetsBackwardCrawler {

	private static Logger log = LogManager.getLogger();

	private long maxId;
	private ScheduledFuture<?> scheduledTasks;

	public TweetsBackwardCrawler() {
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
		private final Action action;
		private final Query query;

		public TweetsSearchTask(Query query, Action action) {
			this.action = action;
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
				log.debug(query);
				QueryResult result = prepareTwitter().search(query);
				List<Status> tweets = result.getTweets();
				if (tweets.size() == 0) {
					log.error("No Result. maxId=" + maxId);
				}

				action.procTweets(query, tweets);

				for (Status tweet : tweets) {
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

	}

}
