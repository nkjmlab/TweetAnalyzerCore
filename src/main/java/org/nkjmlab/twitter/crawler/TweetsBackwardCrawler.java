package org.nkjmlab.twitter.crawler;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;

public class TweetsBackwardCrawler {

	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager
			.getLogger();

	private Twitter twitter;

	private ScheduledExecutorService executorService;
	private ScheduledFuture<?> scheduledTasks;

	public TweetsBackwardCrawler(Twitter twitter) {
		this.twitter = twitter;
	}

	/**
	 * queryのmaxIdを元にtweetsを遡って取得し，取得したツイートに対してActionを実行します．
	 *
	 * @param action
	 * @param query
	 */
	public void crawl(Query query, ProcedureForCollectedTweets action) {
		this.executorService = Executors.newSingleThreadScheduledExecutor();
		this.scheduledTasks = executorService.scheduleWithFixedDelay(
				new TweetsSearchTask(query, action), 0, 5, TimeUnit.SECONDS);

	}

	private class TweetsSearchTask implements Runnable {
		private ProcedureForCollectedTweets action;
		private Query query;

		public TweetsSearchTask(Query query,
				ProcedureForCollectedTweets action) {
			this.action = action;
			this.query = query;
		}

		@Override
		public void run() {
			try {
				log.debug("query is {}", query);
				QueryResult result = twitter.search(query);
				List<Status> tweets = result.getTweets();
				if (tweets.size() == 0) {
					log.info("Finish backward search.");
					scheduledTasks.cancel(true);
					executorService.shutdown();
					return;
				}

				action.apply(query, tweets);
				if (result.hasNext()) {
					query = result.nextQuery();
				} else {
					this.query.setMaxId(
							tweets.get(tweets.size() - 1).getId() - 1);
				}
			} catch (Exception e) {
				log.error("Failed to search tweets: {}.", e.getMessage());
				log.error("Max Id is {}", query.getMaxId());
				log.error("Query is {}", query);
				log.error(e, e);
				scheduledTasks.cancel(true);
				executorService.shutdown();
			}
		}

	}

}
