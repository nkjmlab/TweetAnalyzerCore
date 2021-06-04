package org.nkjmlab.util.twitter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import twitter4j.GeoLocation;
import twitter4j.Status;

public class Tweet {

  public long id;
  public LocalDateTime createdAt;
  public double lat;
  public double lon;
  public String place;
  public String user;
  public long retweetId;
  public String text;
  public String[] hashtagEntities;
  public int favoriteCount;

  public Tweet() {}

  public Tweet(long id, LocalDateTime created, double lat, double lon, String place, String user,
      long retweetId, String text, String[] hashtagEntities, int favoriteCount) {
    this.id = id;
    this.createdAt = created;
    this.lat = lat;
    this.lon = lon;
    this.place = place;
    this.user = user;
    this.retweetId = retweetId;
    this.text = text;
    this.hashtagEntities = hashtagEntities;
    this.favoriteCount = favoriteCount;
  }


  public static List<Tweet> convertStatusListToTweets(Collection<Status> tweets) {
    return tweets.stream().map(s -> convertStatusToTweet(s)).collect(Collectors.toList());
  }

  public static Tweet convertStatusToTweet(Status status) {
    long id = status.getId();
    LocalDateTime created = toLocalDateTime(status.getCreatedAt());
    GeoLocation geoLocation = status.getGeoLocation();
    double lat = geoLocation == null ? 0.0 : geoLocation.getLatitude();
    double lon = geoLocation == null ? 0.0 : geoLocation.getLongitude();

    String place = status.getPlace() == null ? null : status.getPlace().getName();
    String user = status.getUser() == null ? null : status.getUser().getScreenName();
    long retweetId = status.getRetweetedStatus() == null ? -1 : status.getRetweetedStatus().getId();
    String text = status.getText();
    String[] hashtagEntities = Arrays.stream(status.getHashtagEntities())
        .map(hashtag -> "#" + hashtag.getText()).toArray(String[]::new);

    int favoriteCount = status.getFavoriteCount();

    return new Tweet(id, created, lat, lon, place, user, retweetId, text, hashtagEntities,
        favoriteCount);
  }

  public static LocalDateTime toLocalDateTime(Date date) {
    return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
  }

  @Override
  public String toString() {
    return "Tweet [id=" + id + ", createdAt=" + createdAt + ", lat=" + lat + ", lon=" + lon
        + ", place=" + place + ", user=" + user + ", retweetId=" + retweetId + ", text=" + text
        + ", hashtagEntities=" + Arrays.toString(hashtagEntities) + ", favoriteCount="
        + favoriteCount + "]";
  }



}
