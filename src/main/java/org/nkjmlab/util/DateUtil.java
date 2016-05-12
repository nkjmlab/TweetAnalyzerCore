package org.nkjmlab.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

	private static final SimpleDateFormat defaultFormat = new SimpleDateFormat(
			"EEE MMM dd HH:mm:ss z yyyy", Locale.US);

	private static final SimpleDateFormat timeStampFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static void main(String[] args) {
		// "Sun Nov 23 15:16:53 JST 2014";
		System.out.println(defaultFormat.format(new Date()));

	}

	public static Date parseFromTimeStamp(String dateString) {
		try {
			return timeStampFormat.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Date parseFromDefaultDate(String dateString) {
		try {
			return defaultFormat.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String format(Date date) {
		return timeStampFormat.format(date);
	}

}
