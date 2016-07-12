package org.nkjmlab.twitter.crawler;

import java.util.List;

import twitter4j.Query;
import twitter4j.Status;

@FunctionalInterface
public interface ProcedureForCollectedTweets {

	abstract void apply(Query query, List<Status> tweets);

}
