package org.nkjmlab.util.twitter.example;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nkjmlab.util.twitter.TimeLineReader;
import org.nkjmlab.util.twitter.TwitterFactory;
import twitter4j.Status;
import twitter4j.Twitter;

public class TimeLineReaderExample {
  private static Logger log = LogManager.getLogger();

  public static void main(String[] args) {
    Twitter twitter = TwitterFactory.create();

    TimeLineReader timeLineReader = new TimeLineReader(twitter);

    List<Long> result = timeLineReader.getFollowers("IPSJ_official").stream().filter(uid -> {
      log.debug("uid=" + uid);
      Optional<Status> op = timeLineReader.getTimeLine(uid).stream()
          .filter(s -> s.getText().contains("COVID")).findAny();
      return op.isPresent();
    }).collect(Collectors.toList());

    result.forEach(uid -> System.out.println(uid + "\\t" + timeLineReader.getFollowers(uid)));
  }

}
