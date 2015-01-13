package org.nkjmlab.nlp.tweet.tfidf;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nkjmlab.util.RDBUtil;

public class IDFCalculator {
	private static Logger log = LogManager.getLogger();

	public static void main(String[] args) {

		List<String> terms = RDBUtil.readList(String.class,
				"SELECT WORD FROM CHOSHI_NOUNS");
		String[] dates = { "2014-11-24", "2014-11-25", "2014-11-26",
				"2014-11-27", "2014-11-28", "2014-11-29", "2014-11-30" };
		Map<String, IDF> idfs = new IDFCalculator().calcIDFs("CHOSHI", dates,
				terms);
		log.debug(idfs);
	}

	public Map<String, IDF> calcValidIDFs(String tableName, String[] dates,
			Collection<String> terms) {

		Map<String, IDF> result = new HashMap<String, IDF>();

		for (IDF idf : calcIDFs(tableName, dates, terms).values()) {
			if (idf.getIDF() == 0.0) {
				continue;
			}
			result.put(idf.getTerm(), idf);
		}
		return result;
	}

	public Map<String, IDF> calcIDFs(String tableName, String[] dates,
			Collection<String> terms) {

		Map<String, IDF> idfs = new HashMap<>();
		for (String term : terms) {

			IDF idf = calculateIDF(tableName, dates, term);
			idfs.put(term, idf);
		}
		return idfs;
	}

	public IDF calculateIDF(String tableName, String[] dates, String term) {
		int daysOfAppearance = 0;
		String q = "%" + term + "%";

		for (String date : dates) {
			List<String> tweetsFocus = RDBUtil
					.readList(
							String.class,
							"SELECT TEXT FROM "
									+ tableName
									+ " WHERE CREATEDAT BETWEEN ? AND ? AND TEXT LIKE ? ",
							date + " 00:00:00", date + " 23:59:59", q);

			daysOfAppearance = tweetsFocus.size() == 0 ? daysOfAppearance
					: daysOfAppearance + 1;

		}
		return new IDF(term, daysOfAppearance, dates.length);
	}

}
