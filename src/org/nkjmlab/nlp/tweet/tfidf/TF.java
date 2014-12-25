package org.nkjmlab.nlp.tweet.tfidf;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class TF implements Comparable<TF> {

	private String term;
	private int number;
	private int total;
	private double tf;

	public TF(String term, int number, int total) {
		this.term = term;
		this.number = number;
		this.total = total;
	}

	public double getTF() {
		return ((double) number) / total;
	}

	@Override
	public String toString() {
		this.tf = getTF();
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public int compareTo(TF o) {

		this.tf = getTF();
		double diff = this.tf - o.getTF();

		if (diff > 0) {
			return -1;
		} else if (diff == 0) {
			return 0;
		} else {
			return 1;
		}
	}

	public String getTerm() {
		return term;
	}
}
