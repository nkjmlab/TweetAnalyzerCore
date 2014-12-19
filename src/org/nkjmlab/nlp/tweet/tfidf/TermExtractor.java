package org.nkjmlab.nlp.tweet.tfidf;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.sf.persist.Persist;

import org.apache.log4j.Logger;
import org.atilika.kuromoji.Token;
import org.atilika.kuromoji.Tokenizer;
import org.nkjmlab.util.RDBConfig;
import org.nkjmlab.util.RDBConnector;

public class TermExtractor {
	private static Logger log = Logger.getLogger(TermExtractor.class);

	public static void main(String[] args) {
		TermExtractor extractor = new TermExtractor();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			extractor.extract(sdf.parse("2014-11-30 00:00:00"),
					sdf.parse("2014-11-30 23:59:59"), "CHOSHI");
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	private void run() {
		SimpleDateFormat sdfBefore = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			log.debug(sdfBefore.parse(""));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public TermExtractor() {
	}

	private void extract(Date from, Date to, String tableName) {
		String outputTable = tableName + "_NOUNS";

		try (Connection con = RDBConnector.getConnection(new RDBConfig())) {
			Persist persist = new Persist(con);
			List<TweetText> tweets = persist.readList(TweetText.class,
					"SELECT ID, TEXT, CREATEDAT FROM " + tableName
							+ " WHERE CREATEDAT BETWEEN ? AND ?",
					from.toString(), to.toString());

			Tokenizer torknizer = Tokenizer.builder().build();
			for (TweetText tweet : tweets) {
				for (Token token : torknizer.tokenize(tweet.getText())) {
					if (token.getAllFeatures().contains("名詞")) {
						String sql = "INSERT INTO " + outputTable
								+ " VALUES (?)";
						String word = token.getSurfaceForm();

						if (persist.read(Integer.class, "SELECT COUNT(*) FROM "
								+ outputTable + " WHERE WORD=?", word) == 0) {
							persist.executeUpdate(sql, word);
						}
						System.out.println(word);
					}
				}
			}

		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
}