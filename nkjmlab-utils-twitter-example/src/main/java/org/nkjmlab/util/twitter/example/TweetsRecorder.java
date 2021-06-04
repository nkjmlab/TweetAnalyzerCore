package org.nkjmlab.util.twitter.example;

import javax.sql.DataSource;
import org.h2.jdbcx.JdbcConnectionPool;
import org.nkjmlab.util.db.FileDatabaseConfig;
import org.nkjmlab.util.db.H2Server;
import org.nkjmlab.util.twitter.TwitterCrawler;
import org.nkjmlab.util.twitter.TwitterFactory;
import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.Twitter;

public class TweetsRecorder {

  public static void main(String[] args) {
    Twitter twitter = TwitterFactory.create();
    H2Server.startAndWait();

    FileDatabaseConfig config = new FileDatabaseConfig.Builder("~/db/", "tweet-db", "", "").build();
    DataSource db =
        JdbcConnectionPool.create(config.getJdbcUrl(), config.getUsername(), config.getPassword());

    String tableName = "tweets";
    Query query = createQuery("東京オリンピック");

    new TwitterCrawler(twitter).crawl(db, tableName, query);

  }

  /**
   * 検索語を含むツイートを取得するクエリを作成します．
   *
   * @param searchWord
   * @param maxId
   * @return
   */
  private static Query createQuery(String searchWord) {
    return createQuery(searchWord, Long.MAX_VALUE);
  }

  /**
   * 検索語を含むツイートを取得します．maxIdから遡って取得するクエリを作成します．
   *
   * @param searchWord
   * @param maxId
   * @return
   */
  private static Query createQuery(String searchWord, long maxId) {
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
  private static Query createQuery(String searchWord, double lat, double lon, double radius,
      long maxId) {
    Query query = createQuery(searchWord, maxId);
    query.setGeoCode(new GeoLocation(lat, lon), radius, Query.KILOMETERS);
    return query;
  }
}
