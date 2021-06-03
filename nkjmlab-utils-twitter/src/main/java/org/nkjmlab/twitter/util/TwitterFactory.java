package org.nkjmlab.twitter.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;
import org.nkjmlab.twitter.TwitterConfig;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.NullAuthorization;

public class TwitterFactory {
  public static void main(String[] args) throws Exception {
    Twitter twitter = TwitterFactory.create();
    User user = twitter.verifyCredentials();
    System.out.println(user);
    List<Status> statuses = twitter.getHomeTimeline();
    System.out.println("Showing home timeline.");
    for (Status status : statuses) {
      System.out.println(status.getUser().getName() + ":" + status.getText());
    }
  }



  public static Twitter create() {
    Properties props = readPropertiesFromResource("/twitter4j.properties", "MS932");
    return create(new TwitterConfig(props.getProperty("oauth.accessToken"),
        props.getProperty("oauth.accessTokenSecret"), props.getProperty("oauth.consumerKey"),
        props.getProperty("oauth.consumerSecret")));
  }

  public static Twitter create(TwitterConfig conf) {
    Twitter twitter = new twitter4j.TwitterFactory().getInstance();
    if (!(twitter.getAuthorization() instanceof NullAuthorization)) {
      return twitter;
    }

    AccessToken accessToken = new AccessToken(conf.getAccessToken(), conf.getAccessTokenSecret());
    twitter.setOAuthConsumer(conf.getConsumerKey(), conf.getConsumerSecret());
    twitter.setOAuthAccessToken(accessToken);
    return twitter;
  }

  private static Properties readPropertiesFromResource(String name, String charsetName) {
    Properties props = new Properties();
    try (InputStream in = TwitterFactory.class.getResourceAsStream(name);
        InputStreamReader reader = new InputStreamReader(in, charsetName)) {
      props.load(reader);
    } catch (IOException e) {
      throw new RuntimeException();
    }
    return props;
  }
}
