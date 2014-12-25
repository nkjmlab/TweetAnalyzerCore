package org.nkjmlab.nlp.tweet.tfidf;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class IDF implements Comparable<IDF> {

	private String term;
	private int number;
	private int total;
	private double idf;

	public IDF(String term, int number, int total) {
		this.term = term;
		this.number = number;
		this.total = total;
	}

	public double getIDF() {
		return Math.log(((double) total) / number);
	}

	@Override
	public String toString() {
		this.idf = getIDF();
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public int compareTo(IDF o) {

		this.idf = getIDF();
		double diff = this.idf - o.getIDF();

		if (diff > 0) {
			return -1;
		} else if (diff == 0) {
			return 0;
		} else {
			return 1;
		}
	}

	public String getTerm() {
		return this.term;
	}
}
