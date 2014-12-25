package org.nkjmlab.nlp.tweet.tfidf;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.persist.Persist;

import org.nkjmlab.util.DateUtil;
import org.nkjmlab.util.RDBConfig;
import org.nkjmlab.util.RDBConnector;

public class IDFCalculator {
	public static void main(String[] args) {

		List<String> terms = getKeyWords("CHOSHI_NOUNS");
		String[] dates = { "2014-11-24", "2014-11-25", "2014-11-26",
				"2014-11-27", "2014-11-28", "2014-11-29", "2014-11-30" };
		Map<String, IDF> idfs = calcIDFs("CHOSHI", terms, dates);

	}

	public static Map<String, IDF> calcIDFs(String tableName,
			List<String> terms, String[] dates) {

		Map<String, IDF> idfs = new HashMap<>();
		for (String term : terms) {

			IDF idf = IDFCalculator.calculateIDF(tableName, dates, term);
			idfs.put(term, idf);
		}
		// Collections.sort(idfs);

		return idfs;

	}

	private static List<String> getKeyWords(String targetTableName) {
		try (Connection con = RDBConnector.getConnection(new RDBConfig())) {
			Persist persist = new Persist(con);

			return persist.readList(String.class, "SELECT WORD FROM "
					+ targetTableName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static IDF calculateIDF(String targetTableName, String[] dates,
			String term) {
		int daysOfAppearance = 0;
		for (String date : dates) {
			try (Connection con = RDBConnector.getConnection(new RDBConfig())) {
				Persist persist = new Persist(con);
				String q = "%" + term + "%";

				List<String> tweetsFocus = persist
						.readList(
								String.class,
								"SELECT TEXT FROM "
										+ targetTableName
										+ " WHERE CREATEDAT BETWEEN ? AND ? AND TEXT LIKE ? ",
								DateUtil.parse(date + " 00:00:00").toString(),
								DateUtil.parse(date + " 23:59:59").toString(),
								q);

				daysOfAppearance = tweetsFocus.size() == 0 ? daysOfAppearance
						: daysOfAppearance + 1;

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return new IDF(term, daysOfAppearance, dates.length);

	}

}
