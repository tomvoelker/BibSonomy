package org.bibsonomy.util;

/**
 * Helper method for working with regular expressions
 * 
 * @author dbenz
 * @version $Id$
 */
public class RegexUtil {

	/**
	 * Helper method to quote special characters in a string for usage
	 * within a regular expression:
	 * - backslash \
	 * - curly brackets {}
	 * - circumflex ^
	 * 
	 * @param toQuote - a string to quote
	 * @return the quoted string
	 */
	public static String quoteForRegex(String toQuote) {
		return toQuote.replaceAll("\\\\", "\\\\\\\\")
					  .replaceAll("\\{", "\\\\{")
					  .replaceAll("\\}", "\\\\}")
					  .replaceAll("\\^", "\\\\^");
	}
	
}
