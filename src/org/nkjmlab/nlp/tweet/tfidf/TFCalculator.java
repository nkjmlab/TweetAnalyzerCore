package org.nkjmlab.nlp.tweet.tfidf;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.persist.Persist;

import org.atilika.kuromoji.Token;
import org.atilika.kuromoji.Tokenizer;
import org.nkjmlab.util.DateUtil;
import org.nkjmlab.util.RDBConfig;
import org.nkjmlab.util.RDBConnector;

public class TFCalculator {

	public static void main(String[] args) {
		List<String> terms = getKeyWords("CHOSHI_NOUNS");
		String focusDay = "2014-11-24";
		Map<String, TF> tfs = calcTFs("CHOSHI", focusDay, terms);
		System.out.println(tfs);
	}

	public static Map<String, TF> calcTFs(String tableName, String focusDay,
			List<String> terms) {

		Map<String, TF> tfs = new HashMap<>();
		for (String term : terms) {

			TF tf = TFCalculator.calculateTf(tableName,
					DateUtil.parse(focusDay + " 00:00:00"),
					DateUtil.parse(focusDay + " 23:59:59"), term);
			tfs.put(term, tf);
		}
		// Collections.sort(tfs);

		return tfs;

	}

	public static List<String> getKeyWords(String targetTableName) {
		try (Connection con = RDBConnector.getConnection(new RDBConfig())) {
			Persist persist = new Persist(con);

			return persist.readList(String.class, "SELECT WORD FROM "
					+ targetTableName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static TF calculateTf(String targetTableName, Date from, Date to,
			String term) {
		try (Connection con = RDBConnector.getConnection(new RDBConfig())) {
			Persist persist = new Persist(con);
			String query = "%" + term + "%";

			List<TweetText> tweets = persist
					.readList(
							TweetText.class,
							"SELECT ID, TEXT, CREATEDAT FROM "
									+ targetTableName
									+ " WHERE CREATEDAT BETWEEN ? AND ? AND TEXT LIKE ? ",
							from.toString(), to.toString(), query);

			return new TF(term, tweets.size(), getNumOfAllNouns(tweets));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

	private static int allNouns = Integer.MIN_VALUE;

	private static int getNumOfAllNouns(List<TweetText> tweets) {
		if (allNouns != Integer.MIN_VALUE) {
			return allNouns;
		}
		int counter = 0;
		Tokenizer torknizer = Tokenizer.builder().build();
		for (TweetText tweet : tweets) {
			for (Token token : torknizer.tokenize(tweet.getText())) {
				if (token.getAllFeatures().contains("名詞")) {
					counter++;
				}
			}
		}
		allNouns = counter;
		return allNouns;
	}

}
