package org.nkjmlab.nlp.tweet.tfidf;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.persist.Persist;

import org.nkjmlab.util.RDBUtil;
import org.nkjmlab.util.RDBConfig;
import org.nkjmlab.util.RDBConnector;

public class TFIDFCalculator {

	public static void main(String[] args) {

		List<AnalysisSetting> settings = create();

		for (AnalysisSetting setting : settings) {

			String tableName = setting.getTableName();
			String focusDay = setting.getFocusDay();

			String[] dates = setting.getDates();

			KeywordExtractor.updateKeywords(tableName, focusDay);

			List<String> terms = TFCalculator.getKeyWords(tableName + "_NOUNS");

			Map<String, IDF> idfs = IDFCalculator.calcIDFs(tableName, terms,
					dates);
			System.out.println(idfs);

			List<String> termsWithValidIDF = new ArrayList<>();

			for (IDF idf : idfs.values()) {
				if (idf.getIDF() == 0.0) {
					continue;
				}
				termsWithValidIDF.add(idf.getTerm());
			}

			Map<String, TF> tfs = TFCalculator.calcTFs(tableName, focusDay,
					termsWithValidIDF);
			System.out.println(tfs);

			writeResult(tableName, tfs, idfs, termsWithValidIDF);
		}

	}

	private static void writeResult(String tableName, Map<String, TF> tfs,
			Map<String, IDF> idfs, List<String> termsWhichHasValidIDF) {
		String resultTable = tableName + "_RESULT";

		RDBUtil.drop(resultTable);
		RDBUtil.create(resultTable,
				" WORD VARCHAR PRIMARY KEY, TFIDF DOUBLE, TF DOUBLE, IDF DOUBLE");

		try (Connection con = RDBConnector.getConnection(new RDBConfig())) {
			Persist persist = new Persist(con);

			for (String term : termsWhichHasValidIDF) {
				TF tf = tfs.get(term);
				IDF idf = idfs.get(term);
				double tfIdf = tf.getTF() * idf.getIDF();

				if (tf.getTF() == 0.0) {
					continue;
				}

				persist.executeUpdate("INSERT INTO " + resultTable
						+ " VALUES (?,?,?,?)", term, tfIdf, tf.getTF(),
						idf.getIDF());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static List<AnalysisSetting> create() {

		List<AnalysisSetting> settings = new ArrayList<>();

		{
			// 学園祭
			String tableName = "KEIO";
			String focusDay = "2014-11-24";

			String[] dates = { "2014-11-24", "2014-11-25", "2014-11-26",
					"2014-11-27", "2014-11-28", "2014-11-29", "2014-11-30" };

			settings.add(new AnalysisSetting(tableName, focusDay, dates));

		}

		return settings;
	}
}
