package org.nkjmlab.util.twitter.example;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nkjmlab.util.lang.Try;
import org.nkjmlab.util.twitter.TwitterFactory;
import twitter4j.IDs;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.api.TimelinesResources;

public class TimeLineReader {
  private static Logger log = LogManager.getLogger();

  public static void main(String[] args) {
    Twitter twitter = TwitterFactory.create();

    List<Long> result = getFollowers(twitter, "IPSJ_official").stream().filter(uid -> {
      log.debug("uid=" + uid);
      Optional<Status> op =
          getTimeLine(twitter, uid).stream().filter(s -> s.getText().contains("COVID")).findAny();
      return op.isPresent();
    }).collect(Collectors.toList());

    result.forEach(uid -> System.out.println(uid + "\t" + getFollowers(twitter, uid)));
  }

  private static List<Status> getTimeLine(Twitter twitter, long userId) {
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

  private static Set<Long> getFollowers(Twitter twitter, long userId) {
    return getFollowersAux(twitter, Try
        .createFunctionWithThrow(cursor -> twitter.getFollowersIDs(userId, cursor), Try::rethrow));
  }

  private static Set<Long> getFollowers(Twitter twitter, String screenName) {
    return getFollowersAux(twitter, Try.createFunctionWithThrow(
        cursor -> twitter.getFollowersIDs(screenName, cursor), Try::rethrow));
  }

  private static Set<Long> getFollowersAux(Twitter twitter, Function<Long, IDs> idFunc) {
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
