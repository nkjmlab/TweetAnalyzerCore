package org.nkjmlab.util.twitter;

import java.util.Date;
import java.util.List;

public class Query {

  public long id;
  public Date createdAt;
  public List<Long> tweetIds;
  public String query;
  public long maxId;
  public long sinceId;
  public String since;
  public String until;

  public Query() {}

  public Query(twitter4j.Query query, List<Long> tweetIds) {
    this.tweetIds = tweetIds;
    this.query = query.getQuery();
    this.maxId = query.getMaxId();
    this.since = query.getSince();
    this.sinceId = query.getSinceId();
    this.until = query.getUntil();
    this.createdAt = new Date();
  }



}
