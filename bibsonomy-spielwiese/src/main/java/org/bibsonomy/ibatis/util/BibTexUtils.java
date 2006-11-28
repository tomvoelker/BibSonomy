package org.bibsonomy.ibatis.util;

/**
 * Utils for BibTex.
 *
 * @author Christian Schenk
 */
public class BibTexUtils {

	private final static String[] columns = {"address","annote","booktitle","chapter","crossref","edition",
		"howpublished","institution","journal","bkey","month","note","number","organization",
		"pages","publisher","school","series","type","volume","day","url", 
		"content_id", "description", "bibtexKey", "misc", "bibtexAbstract", "user_name", "date",
		"title","author", "editor", "year", "entrytype"};

	/**
	 * return appropriate select query string for different tables
	 */
	public static String getBibtexSelect(final String table) {
		final StringBuffer rVal = new StringBuffer();
		for (final String col : columns) {
			rVal.append(table + "." + col + ((col.equals(columns[columns.length - 1])) ? "" : ","));
		}
		return rVal.toString();
	}
}