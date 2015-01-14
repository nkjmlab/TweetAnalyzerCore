package org.nkjmlab.nlp.tweet.client;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.manager.CsvEntityManager;

public class TwitterConfigFactory {

	public static TwitterConfig create(File file) {
		try {
			CsvConfig cfg = new CsvConfig();
			cfg.setQuoteDisabled(false);
			cfg.setIgnoreEmptyLines(true);
			cfg.setIgnoreLeadingWhitespaces(true);
			cfg.setIgnoreTrailingWhitespaces(true);
			CsvEntityManager manager = new CsvEntityManager(cfg);

			List<TwitterConfig> conf = manager.load(TwitterConfig.class).from(
					file);
			return conf.get(0);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

}
