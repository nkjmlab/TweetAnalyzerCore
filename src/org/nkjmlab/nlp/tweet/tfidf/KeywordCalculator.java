package org.nkjmlab.nlp.tweet.tfidf;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.atilika.kuromoji.Token;
import org.atilika.kuromoji.Tokenizer;
import org.nkjmlab.nlp.tweet.model.Tweet;
import org.nkjmlab.util.RDBUtil;

public class KeywordCalculator {
	private Tokenizer torknizer = Tokenizer.builder().build();

	public Set<String> extractKeywords(List<Tweet> tweets) {
		Set<String> keywords = new HashSet<>();
		for (Tweet tweet : tweets) {
			for (Token token : torknizer.tokenize(tweet.getText())) {
				if (token.getAllFeatures().contains("名詞")) {
					String word = token.getSurfaceForm();
					keywords.add(word);
				}
			}
		}
		return keywords;
	}

	public void write(String keywordTable, List<String> keywords) {
		RDBUtil.drop(keywordTable);
		RDBUtil.create(keywordTable, "WORD VARCHAR PRIMARY KEY");

		for (String keyword : keywords) {
			RDBUtil.executeUpdate(
					"INSERT INTO " + keywordTable + " VALUES (?)", keyword);
		}
	}

}
