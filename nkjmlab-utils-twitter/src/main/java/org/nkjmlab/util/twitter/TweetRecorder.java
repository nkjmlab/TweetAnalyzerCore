package org.nkjmlab.util.twitter;

import javax.sql.DataSource;
import twitter4j.Query;
import twitter4j.Twitter;

public class TweetRecorder {

  private Twitter twitter;
  private DataSource dataSource;
  private String tableName;

  public TweetRecorder(Twitter twitter, DataSource dataSource, String tableName) {
    this.twitter = twitter;
    this.dataSource = dataSource;
    this.tableName = tableName;
  }

  public void fetchAndRecord(Query query) {
    new TwitterCrawler(twitter).crawl(dataSource, tableName, query);
  }

}
