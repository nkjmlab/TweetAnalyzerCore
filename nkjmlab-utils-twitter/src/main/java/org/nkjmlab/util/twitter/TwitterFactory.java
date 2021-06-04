package org.nkjmlab.util.twitter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import twitter4j.Twitter;
import twitter4j.auth.AccessToken;
import twitter4j.auth.NullAuthorization;

public class TwitterFactory {

  public static Twitter create() {
    return create("/twitter4j.properties", "UTF-8");
  }

  public static Twitter create(String name, String charsetName) {
    Twitter twitter = new twitter4j.TwitterFactory().getInstance();
    if (!(twitter.getAuthorization() instanceof NullAuthorization)) {
      return twitter;
    }
    Properties props = readPropertiesFromResource(name, charsetName);
    AccessToken accessToken = new AccessToken(props.getProperty("oauth.accessToken"),
        props.getProperty("oauth.accessTokenSecret"));
    twitter.setOAuthConsumer(props.getProperty("oauth.consumerKey"),
        props.getProperty("oauth.consumerSecret"));
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
