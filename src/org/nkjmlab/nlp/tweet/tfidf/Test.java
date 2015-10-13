package org.nkjmlab.nlp.tweet.tfidf;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	public static void main(String[] args) {
		String regex = "https.*?(\\s|\\Z)";
		String text = "I'm at 銚子市役所 in 銚子市, 千葉県 https://t.co/c8BXlbVvOz a";

		Matcher m = Pattern.compile(regex, Pattern.MULTILINE).matcher(text);
		if (m.find()) {
			System.out.println(m.group(0));
			System.out.println("ok");
		}
	}
}
