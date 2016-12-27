package org.nkjmlab.twitter.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.sf.persist.annotations.NoTable;
import twitter4j.GeoLocation;
import twitter4j.Status;

@NoTable
public class Tweet {

	private long id;

	private Date createdAt;
	private double lat;
	private double lon;
	private String place;
	private String user;
	private long retweetId;
	private String text;
	private String hashtagEntities;

	public Tweet() {
	}

	public Tweet(long id, Date created, double lat, double lon, String place, String user,
			long retweetId, String text, String hashtagEntities) {
		this.id = id;
		this.createdAt = created;
		this.lat = lat;
		this.lon = lon;
		this.place = place;
		this.user = user;
		this.retweetId = retweetId;
		this.text = text;
		this.hashtagEntities = hashtagEntities;
	}

	public static String getSchema(String tableName) {
		return tableName + "(id BIGINT PRIMARY KEY," + "created_at TIMESTAMP," + "lat DOUBLE,"
				+ "lon DOUBLE," + "place VARCHAR," + "user VARCHAR," + "retweet_id BIGINT, "
				+ "text VARCHAR," + "hashtag_entities VARCHAR)";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date created) {
		this.createdAt = created;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public long getRetweetId() {
		return retweetId;
	}

	public void setRetweetId(long retweetId) {
		this.retweetId = retweetId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getHashtagEntities() {
		return hashtagEntities;
	}

	public void setHashtagEntities(String hashtagEntities) {
		this.hashtagEntities = hashtagEntities;
	}

	public static List<Tweet> convertStatusToTweets(Collection<Status> tweets) {
		return tweets.stream().map(s -> convertStatusToTweet(s)).collect(Collectors.toList());
	}

	public static Tweet convertStatusToTweet(Status status) {
		long id = status.getId();
		Date created = status.getCreatedAt();
		GeoLocation geoLocation = status.getGeoLocation();
		double lat = geoLocation == null ? 0.0 : geoLocation.getLatitude();
		double lon = geoLocation == null ? 0.0 : geoLocation.getLongitude();

		String place = status.getPlace() == null ? null : status.getPlace().getName();
		String user = status.getUser() == null ? null : status.getUser().getScreenName();
		long retweetId = status.getRetweetedStatus() == null ? -1
				: status.getRetweetedStatus().getId();
		String text = status.getText();
		List<String> hashtagEntities = Arrays.stream(status.getHashtagEntities())
				.map(hashtag -> "#" + hashtag.getText()).collect(Collectors.toList());

		return new Tweet(id, created, lat, lon, place, user, retweetId,
				text, String.join(" ", hashtagEntities));
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}
