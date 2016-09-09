package org.nkjmlab.twitter.demo;

import org.nkjmlab.twitter.model.TweetsDatabase;
import org.nkjmlab.twitter.util.QueryHelper;
import org.nkjmlab.twitter.util.TwitterFactory;
import org.nkjmlab.util.io.FileUtils;

import twitter4j.Query;
import twitter4j.Twitter;

public class TweetsRecorder {

	protected static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager
			.getLogger();

	public static void main(String[] args) {

		Twitter twitter = TwitterFactory.getSingleton();
		Query query = QueryHelper.create("人身事故");

		TweetsDatabase tweetsDatabase = new TweetsDatabase(
				FileUtils.getFileInUserDirectory("h2/tweets"));

		String tableName = "TWEETS";

		tweetsDatabase.crawl(tableName, query, twitter);

	}

}
