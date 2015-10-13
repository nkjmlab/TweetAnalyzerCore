package org.nkjmlab.nlp.tweet.tfidf;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.atilika.kuromoji.Token;
import org.atilika.kuromoji.Tokenizer;
import org.nkjmlab.nlp.tweet.model.Tweet;

public class KeywordCalculator {
	private Tokenizer torknizer = Tokenizer.builder().build();
	private String[] ngWordRegexs = new String[0];
	private String[] removeWordRegexs = new String[0];

	public Set<String> extractKeywords(List<Tweet> tweets) {
		Set<String> keywords = new HashSet<>();
		for (Tweet tweet : tweets) {

			String text = tweet.getText();

			if (isIncludeNGWord(text)) {
				continue;
			}

			for (String regex : removeWordRegexs) {
				text = text.replaceAll(regex, "");
			}

			for (Token token : torknizer.tokenize(text)) {
				if (token.getAllFeatures().contains("名詞")) {
					String word = token.getSurfaceForm();
					if (word.length() == 1) {
						continue;
					}
					keywords.add(word);
				}
			}
		}
		return keywords;
	}

	private boolean isIncludeNGWord(String text) {
		for (String regex : ngWordRegexs) {
			if (Pattern.compile(regex, Pattern.DOTALL).matcher(text).find()) {
				return true;
			}
		}
		return false;
	}

	public void setInvalidTweetByWords(String... ngWordRegexs) {
		this.ngWordRegexs = ngWordRegexs;
	}

	public void setRemoveWords(String... removeWordRegexs) {
		this.removeWordRegexs = removeWordRegexs;
	}
}
