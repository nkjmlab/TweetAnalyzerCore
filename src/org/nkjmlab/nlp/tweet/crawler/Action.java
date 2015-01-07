package org.nkjmlab.nlp.tweet.crawler;

import java.sql.SQLException;
import java.util.List;

import twitter4j.Status;

public interface Action {

	void procTweets(List<Status> tweets) throws SQLException;

}
