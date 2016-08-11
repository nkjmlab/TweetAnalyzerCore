package org.nkjmlab.twitter.util;

import twitter4j.GeoLocation;
import twitter4j.Query;

public class QueryHelper {

	/**
	 * 検索語を含むツイートを取得するクエリを作成します．
	 *
	 * @param searchWord
	 * @param maxId
	 * @return
	 */
	public static Query create(String searchWord) {
		return create(searchWord, Long.MAX_VALUE);
	}

	/**
	 * 検索語を含むツイートを取得します．maxIdから遡って取得するクエリを作成します．
	 *
	 * @param searchWord
	 * @param maxId
	 * @return
	 */
	public static Query create(String searchWord, long maxId) {
		Query query = new Query(searchWord);
		query.setCount(100);
		query.setMaxId(maxId);
		return query;
	}

	/**
	 * lat,lngを中心として半径radiusキロメートル内の検索語を含むツイートを取得します．maxIdから遡って取得するクエリをを作成します．
	 *
	 * @param searchWord
	 * @param lat
	 * @param lon
	 * @param radius
	 * @param maxId
	 * @return
	 */
	public static Query createQuery(String searchWord, double lat, double lon,
			double radius, long maxId) {
		Query query = create(searchWord, maxId);
		query.setGeoCode(new GeoLocation(lat, lon), radius, Query.KILOMETERS);
		return query;
	}
}
