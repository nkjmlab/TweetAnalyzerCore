package org.nkjmlab.util.twitter.example;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.nkjmlab.util.twitter.TwitterFactory;
import org.nkjmlab.util.twitter.TwitterReader;
import twitter4j.Query;
import twitter4j.Status;

public class TwitterReaderExample {
  private static org.apache.logging.log4j.Logger log =
      org.apache.logging.log4j.LogManager.getLogger();
  private static TwitterReader twitterReader = new TwitterReader(TwitterFactory.create());


  public static void main(String[] args) {
    search();
    readTimeLine();

  }


  private static void search() {
    Query query = new Query("(コロナ OR covid) ワクチン");
    twitterReader.search(query);

  }


  private static void readTimeLine() {
    List<Long> result = twitterReader.getFollowers("IPSJ_official").stream().filter(uid -> {
      log.debug("uid=" + uid);
      Optional<Status> op = twitterReader.getTimeLine(uid).stream()
          .filter(s -> s.getText().contains("COVID")).findAny();
      return op.isPresent();
    }).collect(Collectors.toList());

    result.forEach(uid -> System.out.println(uid + "\\t" + twitterReader.getFollowers(uid)));

  }

}
