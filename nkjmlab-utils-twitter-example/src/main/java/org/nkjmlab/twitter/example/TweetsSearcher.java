package org.nkjmlab.twitter.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nkjmlab.twitter.TwitterCrawler;
import org.nkjmlab.twitter.util.TwitterFactory;
import twitter4j.IDs;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.api.TimelinesResources;

public class TweetsSearcher {
  private static int ID_COUNT_PER_REQUEST = 5000;
  private static Logger log = LogManager.getLogger();

  private static AtomicInteger a = new AtomicInteger();

  public static void main(String[] args) {
    Twitter twitter = TwitterFactory.create();
    TwitterCrawler crawler = new TwitterCrawler(twitter);


    List<Long> result = getFollowers(twitter, "prmu").stream().filter(uid -> {
      System.out.println("uid=" + uid);
      List<Status> tmp = getTimeLine(twitter, uid);
      for (Status s : tmp) {
        if (s.getText().contains("COVID")) {
          return true;
        }
      }
      return false;
    }).collect(Collectors.toList());

    result.forEach(uid -> System.out.println(uid + "\t" + getFollowers(twitter, uid)));


    // Query query = QueryHelper.create("\"Society 5.0\"");
    //
    // crawler.crawl(query, (Query q, List<Status> tweets) -> {
    // logger.debug(Tweet.convertStatusToTweets(tweets));
    // });

  }

  private static List<Status> getTimeLine(Twitter twitter, long userId) {
    try {

      TimelinesResources timeline = twitter.timelines();

      Paging paging = new Paging();// Pagingオブジェクトを作成
      paging.setPage(1);// ページ番号を指定
      paging.count(200);// 1ページから取得するツイート数を指定

      ResponseList<Status> tweets = timeline.getUserTimeline(userId, paging);
      return tweets;
    } catch (TwitterException e) {
      log.error(e);
      return Collections.emptyList();
    }
  }

  private static List<Long> getFollowers(Twitter twitter, long userId) {
    try {

      long cursor = -1L;
      IDs ids;
      List<Long> followerIDs = new ArrayList<Long>();

      long page = 1L;
      do {
        System.out.println(String.format("%dページ目取得中。。(%d <= %d)", page,
            ID_COUNT_PER_REQUEST * (page - 1), ID_COUNT_PER_REQUEST * page++));
        ids = twitter.getFollowersIDs(userId, cursor);
        for (long id : ids.getIDs()) {
          followerIDs.add(id);
        }
        cursor = ids.getNextCursor();
      } while (ids.hasNext());
      return followerIDs;
    } catch (Exception e) {
      log.error(e);
      return Collections.emptyList();
    }
  }


  private static List<Long> getFollowers(Twitter twitter, String targetScreenName) {
    try {

      long cursor = -1L;
      IDs ids;
      List<Long> followerIDs = new ArrayList<Long>();

      long page = 1L;
      do {
        System.out.println(String.format("%dページ目取得中。。(%d <= %d)", page,
            ID_COUNT_PER_REQUEST * (page - 1), ID_COUNT_PER_REQUEST * page++));
        ids = twitter.getFollowersIDs(targetScreenName, cursor);
        for (long id : ids.getIDs()) {
          followerIDs.add(id);
        }
        cursor = ids.getNextCursor();
      } while (ids.hasNext());
      return followerIDs;
    } catch (Exception e) {
      log.error(e);
      return Collections.emptyList();
    }
  }

}
