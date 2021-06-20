package org.nkjmlab.util.twitter.example;

import java.sql.Date;
import java.util.List;
import javax.sql.DataSource;
import org.h2.jdbcx.JdbcConnectionPool;
import org.nkjmlab.util.db.FileDatabaseConfig;
import org.nkjmlab.util.db.H2Server;
import org.nkjmlab.util.twitter.Tweet;
import org.nkjmlab.util.twitter.TweetTable;

public class TweetTableReaderExample {
  private static org.apache.logging.log4j.Logger log =
      org.apache.logging.log4j.LogManager.getLogger();


  public static void main(String[] args) {
    H2Server.startAndWait();

    FileDatabaseConfig config = new FileDatabaseConfig.Builder("~/h2db/", "tweet-db").build();
    log.debug("File database config is [{}]", config);
    DataSource db =
        JdbcConnectionPool.create(config.getJdbcUrl(), config.getUsername(), config.getPassword());
    TweetTable tweetTable = new TweetTable(db, "tweets");

    {
      List<Tweet> tweets =
          tweetTable.readTweets("SELECT * FROM TWEETS WHERE " + TweetTable.CREATED_AT + " >?",
              Date.valueOf("2020-06-04"));
      log.info(tweets);
    }
    {
      List<Tweet> tweets = tweetTable.readTweets("SELECT * FROM TWEETS");
      log.info(tweets);
    }

  }

}
