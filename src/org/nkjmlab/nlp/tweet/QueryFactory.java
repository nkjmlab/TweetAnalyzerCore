package org.nkjmlab.nlp.tweet;

import twitter4j.GeoLocation;
import twitter4j.Query;

public class QueryFactory {

	/**
	 * 検索語を含むツイートを取得します．maxIdから遡って取得します．
	 *
	 * @param searchWord
	 * @param maxId
	 * @return
	 */
	public static Query create(String searchWord, long maxId) {
		Query query = new Query(searchWord);
		query.setMaxId(maxId);
		return query;
	}

	/**
	 * lat,lngを中心として半径radiusキロメートル内の検索語を含むツイートを取得します．maxIdから遡って取得します．
	 *
	 * @param searchWord
	 * @param lat
	 * @param lng
	 * @param radius
	 * @param maxId
	 * @return
	 */
	public static Query createQuery(String searchWord, double lat, double lng,
			double radius, long maxId) {

		Query query = create(searchWord, maxId);
		query.setGeoCode(new GeoLocation(lat, lng), radius, Query.KILOMETERS);

		return query;
	}
}
