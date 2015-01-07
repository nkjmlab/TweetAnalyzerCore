package org.nkjmlab.nlp.tweet.tfidf;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import net.sf.persist.Persist;

import org.atilika.kuromoji.Token;
import org.atilika.kuromoji.Tokenizer;
import org.nkjmlab.util.DateUtil;
import org.nkjmlab.util.RDBConfig;
import org.nkjmlab.util.RDBConnector;
import org.nkjmlab.util.RDBUtil;

public class KeywordExtractor {

	public static void main(String[] args) {

		updateKeywords("CHOSHI", "2014-11-30");

	}

	public static void updateKeywords(String targetTable, String date) {
		String keywordTable = targetTable + "_NOUNS";

		RDBUtil.drop(keywordTable);
		RDBUtil.create(keywordTable, "WORD VARCHAR PRIMARY KEY");
		List<TweetText> tweets = extractTweets(targetTable,
				DateUtil.parse(date + " 00:00:00"),
				DateUtil.parse(date + " 23:59:59"));
		extractAndInsert(keywordTable, tweets);
	}

	private static List<TweetText> extractTweets(String targetTable, Date from,
			Date to) {
		try (Connection con = RDBConnector.getConnection(new RDBConfig())) {
			Persist persist = new Persist(con);
			List<TweetText> tweets = persist.readList(TweetText.class,
					"SELECT ID, TEXT, CREATEDAT FROM " + targetTable
							+ " WHERE CREATEDAT BETWEEN ? AND ?",
					from.toString(), to.toString());
			return tweets;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void extractAndInsert(String keywordTable,
			List<TweetText> tweets) {

		try (Connection con = RDBConnector.getConnection(new RDBConfig())) {
			Persist persist = new Persist(con);

			Tokenizer torknizer = Tokenizer.builder().build();
			for (TweetText tweet : tweets) {
				for (Token token : torknizer.tokenize(tweet.getText())) {
					if (token.getAllFeatures().contains("名詞")) {
						String sql = "INSERT INTO " + keywordTable
								+ " VALUES (?)";
						String word = token.getSurfaceForm();

						if (persist.read(Integer.class, "SELECT COUNT(*) FROM "
								+ keywordTable + " WHERE WORD=?", word) == 0) {
							persist.executeUpdate(sql, word);
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}