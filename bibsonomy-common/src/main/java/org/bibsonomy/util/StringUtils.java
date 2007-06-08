package org.bibsonomy.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * Some methods to handle string.
 */
public class StringUtils {

	/**
	 * All strings in the collection are concatenated and returned as one single
	 * string.
	 */
	public static String getStringFromList(final Collection<String> collection) {
		if (collection.isEmpty()) {
			return "[]";
		} else {
			// returns [item1,item2,item3,...]
			final StringBuffer s = new StringBuffer("[");
			final Iterator it = collection.iterator();
			while (it.hasNext()) {
				s.append(it.next() + ",");
			}
			s.replace(s.length() - 1, s.length(), "]");
			return s.toString();
		}
	}

	/**
	 * Remove everything, but numbers.
	 */
	public static String removeNonNumbers(final String str) {
		if (str != null) {
			return str.replaceAll("[^0-9]+", "");
		}
		return "";
	}

	/**
	 * Removes everything which is neither a number nor a letter.
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
	 */
	public static String removeNonNumbersOrLettersOrDotsOrSpace(final String str) {
		if (str != null) {
			return normalizeWhitespace(str).replaceAll("[^0-9\\p{L}\\. ]+", "");
		}
		return "";
	}

	/**
	 * Removes all whitespace.
	 */
	public static String removeWhitespace(final String str) {
		if (str != null) {
			return str.replaceAll("\\s+", "");
		}
		return "";
	}

	/**
	 * Substitutes all whitespace with " "
	 */
	public static String normalizeWhitespace(final String str) {
		if (str != null) {
			return str.replaceAll("\\s+", " ");
		}
		return "";
	}
}