package org.nkjmlab.util.twitter;

import static org.nkjmlab.sorm4j.sql.schema.TableSchemaKeyword.*;
import java.util.List;
import javax.sql.DataSource;
import org.nkjmlab.sorm4j.Orm;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.sql.schema.TableSchema;

public class TweetTable {

  private final TableSchema schema;
  private final Orm orm;

  private static final String ID = "ID";
  private static final String CREATED_AT = "created_at";
  private static final String LAT = "lat";
  private static final String LON = "lon";
  private static final String PLACE = "place";
  private static final String USER = "user";
  private static final String RETWEET_ID = "retweet_id";
  private static final String TEXT = "text";
  private static final String HASHTAG_ENTITIES = "HASHTAG_ENTITIES";
  private static final String FAVORITE_COUNT = "favorite_count";

  /**
   *
   * @param dataSource
   * @param tableName
   */
  public TweetTable(DataSource dataSource, String tableName) {
    this.orm = Sorm.create(dataSource);
    this.schema = new TableSchema.Builder().setTableName(tableName)
        .addColumnDefinition(ID, BIGINT, PRIMARY_KEY).addColumnDefinition(CREATED_AT, TIMESTAMP)
        .addColumnDefinition(LAT, DOUBLE).addColumnDefinition(LON, DOUBLE)
        .addColumnDefinition(PLACE, VARCHAR).addColumnDefinition(USER, VARCHAR)
        .addColumnDefinition(RETWEET_ID, BIGINT).addColumnDefinition(TEXT, VARCHAR)
        .addColumnDefinition(HASHTAG_ENTITIES, ARRAY).addColumnDefinition(FAVORITE_COUNT, INT)
        .addIndexColumn(CREATED_AT).addIndexColumn(USER).build();
    schema.createTableIfNotExists(orm);
    schema.createIndexesIfNotExists(orm);
  }



  public List<Tweet> readTweets(String sql, Object... objs) {
    return orm.readList(Tweet.class, sql, objs);
  }

  public void mergeTweet(Tweet tweet) {
    orm.mergeOn(schema.getTableName(), tweet);


  }

  public void mergeTweet(List<Tweet> tweets) {
    orm.mergeOn(schema.getTableName(), tweets);
  }

}
