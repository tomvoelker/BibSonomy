package org.bibsonomy.lucene;

/**
 * Utility class to provide some often used methods 
 * 
 * @version $Id$
 *
 */

public class Utils {

	/**
	 * 
	 * @param s source
	 * @param r replacement - SpecialLuceneCharacters will be replaced by r 
	 * @return
	 */
	
	public static String replaceSpecialLuceneChars(String s, String r) {
		s = s.replaceAll("[\\,\\&\\|\\(\\)\\[\\]\\{\\}\\~\\*\\^\\?\\:\\\\]", r);
		return s;
	}

	
	/**
	 * replace special lucene characters used in queries like ?*~:()[]{}&|:\ with " "
	 * @return the String
	 */
	public static String replaceSpecialLuceneChars(String s) {
		s = replaceSpecialLuceneChars (s, " ");
		return s;
	}

	/**
	 * remove special lucene characters used in queries like ?*~:()[]{}&|:\ 
	 * @return the String
	 */
	public static String resmoveSpecialLuceneChars(String s) {
		s = replaceSpecialLuceneChars (s, "");
		return s;
	}

	
}