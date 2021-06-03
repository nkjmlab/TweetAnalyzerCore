package org.nkjmlab.twitter;

import static org.nkjmlab.sorm4j.sql.schema.TableSchemaKeyword.*;
import java.util.List;
import javax.sql.DataSource;
import org.nkjmlab.sorm4j.Orm;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.sql.schema.TableSchema;

public class QueryTable {

  private final Orm orm;
  private final TableSchema schema;
  private static final String ID = "id";
  private static final String CREATED_AT = "created_at";
  private static final String TWEET_IDS = "tweet_ids";
  private static final String QUERY = "query";
  private static final String SINCE_ID = "since_id";
  private static final String MAX_ID = "max_id";
  private static final String SINCE = "since";
  private static final String UNTIL = "until";

  public QueryTable(DataSource dataSource) {
    String tableName = "QUERYS";
    this.orm = Sorm.create(dataSource);
    this.schema = new TableSchema.Builder().setTableName(tableName)
        .addColumnDefinition(ID, BIGINT, PRIMARY_KEY, AUTO_INCREMENT)
        .addColumnDefinition(CREATED_AT, TIMESTAMP).addColumnDefinition(TWEET_IDS, ARRAY)
        .addColumnDefinition(QUERY, VARCHAR).addColumnDefinition(MAX_ID, BIGINT)
        .addColumnDefinition(SINCE_ID, BIGINT).addColumnDefinition(SINCE, VARCHAR)
        .addColumnDefinition(UNTIL, VARCHAR).build();
    schema.createTableIfNotExists(orm);
  }

  public void insertQuery(twitter4j.Query query, List<Long> tweetIds) {
    orm.insert(new org.nkjmlab.twitter.Query(query, tweetIds));
  }



}
