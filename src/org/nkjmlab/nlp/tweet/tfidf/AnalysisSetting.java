package org.nkjmlab.nlp.tweet.tfidf;

public class AnalysisSetting {

	private String tableName;
	private String focusDay;

	public String getTableName() {
		return tableName;
	}

	public String getFocusDay() {
		return focusDay;
	}

	public String[] getDates() {
		return dates;
	}

	private String[] dates;

	public AnalysisSetting(String tableName, String focusDay, String[] dates) {
		this.tableName = tableName;
		this.focusDay = focusDay;
		this.dates = dates;
	}

}
