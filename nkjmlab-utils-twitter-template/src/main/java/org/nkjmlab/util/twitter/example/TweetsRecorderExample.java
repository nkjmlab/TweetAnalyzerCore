package org.nkjmlab.util.twitter.example;

import javax.sql.DataSource;
import org.h2.jdbcx.JdbcConnectionPool;
import org.nkjmlab.util.db.FileDatabaseConfig;
import org.nkjmlab.util.db.H2Server;
import org.nkjmlab.util.twitter.TweetRecorder;
import org.nkjmlab.util.twitter.TwitterFactory;
import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.Twitter;

public class TweetsRecorderExample {
  private static org.apache.logging.log4j.Logger log =
      org.apache.logging.log4j.LogManager.getLogger();

  public static void main(String[] args) {

    H2Server.startAndWait();
    Twitter twitter = TwitterFactory.create();
    FileDatabaseConfig config = new FileDatabaseConfig.Builder("~/h2db/", "tweet-db").build();
    DataSource db =
        JdbcConnectionPool.create(config.getJdbcUrl(), config.getUsername(), config.getPassword());
    String tableName = "tweets";

    log.info("Db config={}", config);
    Query query = createQuery("コロナ ワクチン");
    new TweetRecorder(twitter, db, tableName).fetchAndRecord(query);
  }

  /**
   * 検索語を含むツイートを取得するクエリを作成します．
   *
   * @param searchWord
   * @param maxId
   * @return
   */
  public static Query createQuery(String searchWord) {
    return createQuery(searchWord, Long.MAX_VALUE);
  }

  /**
   * 検索語を含むツイートを取得します．maxIdから遡って取得するクエリを作成します．
   *
   * @param searchWord
   * @param maxId
   * @return
   */
  public static Query createQuery(String searchWord, long maxId) {
    Query query = new Query(searchWord);
    query.setCount(100);
    query.setMaxId(maxId);
    return query;
  }

  /**
   * lat,lngを中心として半径radiusキロメートル内の検索語を含むツイートを取得します．maxIdから遡って取得するクエリをを作成します．
   *
   * @param searchWord
   * @param lat
   * @param lon
   * @param radius
   * @param maxId
   * @return
   */
  public static Query createQuery(String searchWord, double lat, double lon, double radius,
      long maxId) {
    Query query = createQuery(searchWord, maxId);
    query.setGeoCode(new GeoLocation(lat, lon), radius, Query.KILOMETERS);
    return query;
  }

}
