package org.bibsonomy.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * Some methods for handling strings.
 */
public class StringUtils {

	/**
	 * All strings in the collection are concatenated and returned as one single
	 * string, i.e. like <code>[item1,item2,item3,...]</code>.
	 * @param collection collection of strings to be concatenated
	 * @return concatenated string, i.e. like <code>[item1,item2,item3,...]</code>.
	 */
	public static String getStringFromList(final Collection<String> collection) {
		if (collection.isEmpty()) {
			return "[]";
		}
		final StringBuffer s = new StringBuffer("[");
		final Iterator<String> it = collection.iterator();
		while (it.hasNext()) {
			s.append(it.next() + ",");
		}
		s.replace(s.length() - 1, s.length(), "]");
		return s.toString();
	}

	/**
	 * Removes everything, but numbers.
	 * @param str source string
	 * @return result
	 */
	public static String removeNonNumbers(final String str) {
		if (str != null) {
			return str.replaceAll("[^0-9]+", "");
		}
		return "";
	}

	/**
	 * Removes everything which is neither a number nor a letter.
	 * @param str source string
	 * @return result
	 */
	public static String removeNonNumbersOrLetters(final String str) {
		if (str != null) {
			return str.replaceAll("[^0-9\\p{L}]+", "");
		}
		return "";
	}

	/**
	 * Removes everything which is neither a number nor a letter nor a dot (.)
	 * nor space.
	 * @param str source string
	 * @return result
	 */
	public static String removeNonNumbersOrLettersOrDotsOrSpace(final String str) {
		if (str != null) {
			return normalizeWhitespace(str).replaceAll("[^0-9\\p{L}\\. ]+", "");
		}
		return "";
	}

	/**
	 * Removes all whitespace.
	 * @param str source string
	 * @return result
	 */
	public static String removeWhitespace(final String str) {
		if (str != null) {
			return str.replaceAll("\\s+", "");
		}
		return "";
	}

	/**
	 * Substitutes all whitespace with " "
	 * @param str source string
	 * @return result
	 */
	public static String normalizeWhitespace(final String str) {
		if (str != null) {
			return str.replaceAll("\\s+", " ");
		}
		return "";
	}
}