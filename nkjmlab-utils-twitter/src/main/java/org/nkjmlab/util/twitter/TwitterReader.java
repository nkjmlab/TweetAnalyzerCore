package org.nkjmlab.util.twitter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nkjmlab.util.lang.Try;
import twitter4j.IDs;
import twitter4j.Paging;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.api.TimelinesResources;

public class TwitterReader {
  private static Logger log = LogManager.getLogger();
  private Twitter twitter;

  public TwitterReader(Twitter twitter) {
    this.twitter = twitter;
  }



  public List<Status> search(twitter4j.Query query) {

    try {
      QueryResult result = twitter.search(query);
      List<Status> tweets = result.getTweets();
      return tweets;
    } catch (TwitterException e) {
      throw Try.rethrow(e);
    }

  }

  public List<Status> getTimeLine(long userId) {
    return Try.getWithCatch(() -> {
      TimelinesResources timeline = twitter.timelines();

      Paging paging = new Paging();
      paging.setPage(1);
      paging.count(200);

      ResponseList<Status> tweets = timeline.getUserTimeline(userId, paging);
      return tweets;
    }, e -> {
      log.error(e);
      return Collections.emptyList();
    });

  }

  public Set<Long> getFollowers(long userId) {
    return getFollowersAux(Try
        .createFunctionWithThrow(cursor -> twitter.getFollowersIDs(userId, cursor), Try::rethrow));
  }

  public Set<Long> getFollowers(String screenName) {
    return getFollowersAux(Try.createFunctionWithThrow(
        cursor -> twitter.getFollowersIDs(screenName, cursor), Try::rethrow));
  }

  private Set<Long> getFollowersAux(Function<Long, IDs> idFunc) {
    return Try.getWithCatch(() -> {
      Set<Long> followerIDs = new HashSet<>();
      IDs ids = null;
      while (true) {
        ids = idFunc.apply(ids == null ? -1 : ids.getNextCursor());
        Arrays.stream(ids.getIDs()).forEach(id -> followerIDs.add(id));
        if (!ids.hasNext()) {
          break;
        }
      }
      return followerIDs;
    }, e -> {
      log.error(e);
      return Collections.emptySet();
    });

  }

}
