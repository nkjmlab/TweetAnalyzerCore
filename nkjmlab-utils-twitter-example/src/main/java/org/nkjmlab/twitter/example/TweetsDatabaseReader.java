package org.nkjmlab.twitter.example;

import java.util.List;
import javax.sql.DataSource;
import org.h2.jdbcx.JdbcConnectionPool;
import org.nkjmlab.twitter.Tweet;
import org.nkjmlab.twitter.TweetTable;
import org.nkjmlab.util.db.FileDatabaseConfig;
import org.nkjmlab.util.db.H2Server;

public class TweetsDatabaseReader {
  private static org.apache.logging.log4j.Logger log =
      org.apache.logging.log4j.LogManager.getLogger();


  public static void main(String[] args) {
    H2Server.startAndWait();

    FileDatabaseConfig config = new FileDatabaseConfig.Builder("~/db/", "tweet-db", "username", "password").build();
    log.debug("File database config is [{}]", config);
    DataSource db =
        JdbcConnectionPool.create(config.getJdbcUrl(), config.getUsername(), config.getPassword());
    TweetTable tweetTable = new TweetTable(db, "tweets");

    List<Tweet> tweets = tweetTable.readTweets("SELECT * FROM TWEETS");
    log.info(tweets);

  }

}
