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

  public static final String ID = "id";
  public static final String CREATED_AT = "created_at";
  public static final String LAT = "lat";
  public static final String LON = "lon";
  public static final String PLACE = "place";
  public static final String USER = "user";
  public static final String RETWEET_ID = "retweet_id";
  public static final String TEXT = "text";
  public static final String HASHTAG_ENTITIES = "hashtag_entities";
  public static final String FAVORITE_COUNT = "favorite_count";

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



  public List<Tweet> readTweets(String sql, Object... parameters) {
    return orm.readList(Tweet.class, sql, parameters);
  }

  public void mergeTweet(Tweet tweet) {
    orm.mergeOn(schema.getTableName(), tweet);


  }

  public void mergeTweet(List<Tweet> tweets) {
    orm.mergeOn(schema.getTableName(), tweets);
  }

}
