package org.bibsonomy.lucene;

/**
 * Utility class to provide some often used methods 
 * 
 * @version $Id$
 *
 */

public class Utils {

	/**
	 * remove special lucene characters used in queries like ?*~:()[]{}&|:\
	 * @return the String
	 */
	public static String removeSpecialLuceneChars(String s) {
		s = s.replaceAll("[\\,\\&\\|\\(\\)\\[\\]\\{\\}\\~\\*\\^\\?\\:\\\\]", " ");
		return s;
	}

	
}