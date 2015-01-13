package org.nkjmlab.nlp.tweet.tfidf;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atilika.kuromoji.Token;
import org.atilika.kuromoji.Tokenizer;
import org.nkjmlab.nlp.tweet.model.Tweet;
import org.nkjmlab.nlp.tweet.model.TweetDB;
import org.nkjmlab.util.DateUtil;

public class TFCalculator {
	private static Logger log = LogManager.getLogger();

	public static void main(String[] args) {
		String tableName = "CHOSHI_TWEETS";
		String focusDay = "2014-11-30";

		List<Tweet> tweets = TweetDB.readTweets(tableName,
				DateUtil.parseFromTimeStamp(focusDay + " 00:00:00"),
				DateUtil.parseFromTimeStamp(focusDay + " 23:59:59"));
		log.info("Start extract keywords ...");

		Set<String> keywords = new KeywordCalculator().extractKeywords(tweets);
		log.info("Start calculate idfs ...");

		Map<String, TF> tfs = new TFCalculator().calcValidTFs(tableName,
				focusDay, keywords);
		log.info("Start write result ...");
		log.debug(tfs);
	}

	public Map<String, TF> calcValidTFs(String tableName, String focusDay,
			Set<String> terms) {
		Map<String, TF> result = new HashMap<>();

		for (TF tf : calcTFs(tableName, focusDay, terms).values()) {
			if (tf.getTF() < 0.0001) {
				continue;
			}
			result.put(tf.getTerm(), tf);
		}
		return result;

	}

	public Map<String, TF> calcTFs(String tableName, String focusDay,
			Collection<String> terms) {
		Map<String, TF> tfs = new HashMap<>();

		String from = focusDay + " 00:00:00";
		String to = focusDay + " 23:59:59";
		List<Tweet> allTweets = TweetDB.readTweets("SELECT * FROM " + tableName
				+ " WHERE CREATEDAT BETWEEN ? AND ? ", from, to);
		int allNouns = calcNumOfAllNouns(allTweets);
		for (String term : terms) {
			TF tf = calculateTf(tableName, from, to, term, allNouns);
			tfs.put(term, tf);
		}
		// Collections.sort(tfs);

		return tfs;
	}

	public TF calculateTf(String targetTableName, String from, String to,
			String term, int allNouns) {
		String query = "%" + term + "%";

		List<Tweet> tweets = TweetDB.readTweets("SELECT * FROM "
				+ targetTableName
				+ " WHERE CREATEDAT BETWEEN ? AND ? AND TEXT LIKE ? ", from,
				to, query);

		return new TF(term, tweets.size(), allNouns);

	}

	private Tokenizer torknizer = Tokenizer.builder().build();

	private int calcNumOfAllNouns(List<Tweet> tweets) {
		int counter = 0;
		for (Tweet tweet : tweets) {
			for (Token token : torknizer.tokenize(tweet.getText())) {
				if (token.getAllFeatures().contains("名詞")) {
					counter++;
				}
			}
		}
		return counter;
	}

}
