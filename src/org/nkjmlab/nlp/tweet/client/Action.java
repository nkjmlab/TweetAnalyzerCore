package org.nkjmlab.nlp.tweet.client;

import java.util.List;

import twitter4j.Query;
import twitter4j.Status;

public interface Action {

	void procTweets(Query query, List<Status> tweets);

}
