package org.bibsonomy.util.id;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author rja
 * @version $Id$
 */
public class ISBNUtils {
	/*
	 * pattern to match ISBN 10 and 13
	 */
	private static final Pattern isbnPattern = Pattern.compile("(\\d{12}[\\dx]|\\d{9}[\\dx])", Pattern.CASE_INSENSITIVE);

	/**
	 * Search substring with pattern format and returns it.
	 * @param snippet 
	 * @return ISBN, null if no ISBN is available
	 */
	public static String extractISBN(final String snippet){
		if (snippet != null) {
			final Matcher isbnMatcher = isbnPattern.matcher(cleanISBN(snippet));
			if (isbnMatcher.find())
				return isbnMatcher.group(1);
		}
		return null;
	}


	/**
	 * remove seperation signs between numbers
	 * @param snippet from ScrapingContext
	 * @return snippet without " " and "-"
	 */
	public static String cleanISBN(final String snippet){
		return snippet.replace(" ", "").replace("-", "");
	}
}
