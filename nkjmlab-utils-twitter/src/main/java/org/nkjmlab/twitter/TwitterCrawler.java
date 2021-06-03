package org.nkjmlab.twitter;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;

public class TwitterCrawler {

  private static org.apache.logging.log4j.Logger log =
      org.apache.logging.log4j.LogManager.getLogger();
  private static final ScheduledExecutorService executorService =
      Executors.newSingleThreadScheduledExecutor();

  private final Twitter twitter;


  public TwitterCrawler(Twitter twitter) {
    this.twitter = twitter;
  }

  /**
   * queryのmaxIdを元にtweetsを遡って取得し，取得したツイートに対してActionを実行します．
   *
   * @param action the action to the query and received tweets.
   * @param query the query for crawl
   */
  public void crawl(Query query, Function<Query, Consumer<List<Status>>> action) {
    new TweetsSearchTask(query, action).start();
  }


  public void crawl(DataSource db, String tableName, Query query) {
    log.info("[START] start crawling");
    TweetTable tweetTable = new TweetTable(db, tableName);
    QueryTable queryTable = new QueryTable(db);

    crawl(query, q -> statusList -> {
      List<Tweet> tweets = Tweet.convertStatusListToTweets(statusList);
      tweetTable.mergeTweet(tweets);
      queryTable.insertQuery(q, tweets.stream().map(t -> t.id).collect(Collectors.toList()));
    });

  }


  private class TweetsSearchTask implements Runnable {
    private final Function<Query, Consumer<List<Status>>> action;
    private ScheduledFuture<?> scheduledTasks;
    private Query query;

    public TweetsSearchTask(Query query, Function<Query, Consumer<List<Status>>> action) {
      this.action = action;
      this.query = query;
    }


    public void start() {
      this.scheduledTasks = executorService.scheduleWithFixedDelay(this, 0, 5, TimeUnit.SECONDS);
    }


    @Override
    public void run() {
      try {
        QueryResult result = twitter.search(query);
        List<Status> tweets = result.getTweets();
        log.debug("Read [{}] tweets. query for search = [{}]", tweets.size(), query);
        if (tweets.size() == 0) {
          log.info("[FINISH] There is no tweet for read. Finish backward search.", tweets.size());
          scheduledTasks.cancel(true);
          return;
        }
        action.apply(query).accept(tweets);
        if (result.hasNext()) {
          query = result.nextQuery();
        } else {
          this.query.setMaxId(tweets.get(tweets.size() - 1).getId() - 1);
        }
      } catch (Exception e) {
        log.error("[FAIL] Failed to search tweets: {}, Max Id is {}, Query is {}, ", e.getMessage(),
            query.getMaxId(), query);
        log.error(e, e);
        scheduledTasks.cancel(true);
      }
    }

  }
}
