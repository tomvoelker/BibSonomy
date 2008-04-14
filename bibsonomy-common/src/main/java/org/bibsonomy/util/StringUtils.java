package org.bibsonomy.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Iterator;

/**
 * Some methods for handling strings.
 */
public class StringUtils {

	/**
	 * Calculates the MD5-Hash of a String s and returns it encoded as a hex
	 * string of 32 characters length.
	 * 
	 * @param string
	 *            the string to be hashed
	 * @return the MD5 hash of s as a 32 character string
	 */
	public static String getMD5Hash(final String string) {
		if (string == null) return null;

		final String charset = "UTF-8";
		try {
			final MessageDigest md = MessageDigest.getInstance("MD5");
			return toHexString(md.digest(string.getBytes(charset)));
		} catch (final UnsupportedEncodingException e) {
			return null;
		} catch (final NoSuchAlgorithmException e) {
			return null;
		}
	}

	/**
	 * Converts a buffer of bytes into a string of hex values.
	 * 
	 * @param buffer
	 *            array of bytes which should be converted
	 * @return hex string representation of buffer
	 */
	public static String toHexString(byte[] buffer) {
		final StringBuffer result = new StringBuffer();
		for (int i = 0; i < buffer.length; i++) {
			String hex = Integer.toHexString(buffer[i]);
			if (hex.length() == 1) {
				hex = "0" + hex;
			}
			result.append(hex.substring(hex.length() - 2));
		}
		return result.toString();
	}

	/**
	 * Checks if a given string has one of the chosen extensions.
	 * 
	 * @param string
	 *            String to test
	 * @param extensions
	 *            Extensions to match.
	 * @return true if String matches with extension
	 */
	public static boolean matchExtension(final String string, final String... extensions) {
		for (final String extension : extensions) {
			if (string.length() >= extension.length() && string.substring(string.length() - extension.length(), string.length()).equalsIgnoreCase(extension)) return true;
		}
		return false;
	}

	/**
	 * All strings in the collection are concatenated and returned as one single
	 * string, i.e. like <code>[item1,item2,item3,...]</code>.
	 * 
	 * @param collection
	 *            collection of strings to be concatenated
	 * @return concatenated string, i.e. like
	 *         <code>[item1,item2,item3,...]</code>.
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
	 * 
	 * @param str
	 *            source string
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
	 * 
	 * @param str
	 *            source string
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
	 * 
	 * @param str
	 *            source string
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
	 * 
	 * @param str
	 *            source string
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
	 * 
	 * @param str
	 *            source string
	 * @return result
	 */
	public static String normalizeWhitespace(final String str) {
		if (str != null) {
			return str.replaceAll("\\s+", " ");
		}
		return "";
	}

	/**
	 * Crops a string s to length if it is longer than length
	 * 
	 * @param s
	 *            string to crop
	 * @param length
	 *            maximum length
	 * @return the string s which may be cropped
	 */
	public static String cropToLength(final String s, final int length) {
		if (s != null && s.length() > length) {
			return s.substring(0, length);
		}
		return s;
	}
	
	/** 
	 * Compares two Strings like compareTo but with additional checks, if one of the strings is NULL.
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static int secureCompareTo (String s1, String s2) {
		if (s1 == null) {
			if (s2 == null) {
				/*
				 * null = s1 = s2 = null
				 */
				return 0;
			} 
			/*
			 * null = s1 < s2 != null
			 */
			return -1;

		}

		if (s2 == null) {
			/*
			 * null != s1 > s2 = null
			 */
			return 1;
		}
		/*
		 * null != s1 ? s2 != null
		 */
		return s1.compareToIgnoreCase(s2);
	}	
}