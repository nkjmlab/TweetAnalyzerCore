package org.nkjmlab.kuromoji;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import net.sf.persist.Persist;

import org.atilika.kuromoji.Token;
import org.atilika.kuromoji.Tokenizer;
import org.nkjmlab.tweet.Config;
import org.nkjmlab.tweet.DBConnector;

public class TweetTokenizer {

	private Persist persist = new DBConnector(new Config()).getPersist();

	public static void main(String[] args) {
		new TweetTokenizer().run(new File("data/tmp2.txt"), "NOUNS", "名詞");
	}

	private void run(File file, String tableName, String feature) {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			Tokenizer torknizer = Tokenizer.builder().build();
			String tweet;
			while ((tweet = br.readLine()) != null) {
				for (Token token : torknizer.tokenize(tweet)) {
					if (token.getAllFeatures().contains(feature)) {
						String sql = "INSERT INTO " + tableName + " VALUES (?)";
						String word = token.getSurfaceForm();

						int num = persist.read(Integer.class,
								"SELECT COUNT(*) FROM " + tableName
										+ " WHERE WORD=?", word);

						if (num == 0) {
							persist.executeUpdate(sql, word);
						}
						System.out.println(word);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}