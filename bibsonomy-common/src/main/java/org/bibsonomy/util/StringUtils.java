/**
 *  
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.util;


import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * Some methods for handling strings.
 * @author 
 * @version $Id$
 */
public class StringUtils {
	
	private static final String DEFAULT_CHARSET = "UTF-8";
	
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
		
		try {
			return HashUtils.getMD5Hash(string.getBytes(DEFAULT_CHARSET));
		} catch (final UnsupportedEncodingException e) {
			return null;
		}
	}
	
	/**
	 * Calculates the SHA-1-Hash of a string.
	 * 
	 * @param string	the string to be hashed
	 * @return the SHA-1 hash
	 */
	public static String getSHA1Hash(final String string) {
		if (string == null) return null;
		
		try {
			return HashUtils.getSHA1Hash(string.getBytes(DEFAULT_CHARSET));
		} catch (final UnsupportedEncodingException e) {
			return null;
		}
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
	 * @return 0 if s1 == null and s2 == null, -1 if s1 == null, 1 if s2 == null
	 */
	public static int secureCompareTo(final String s1, final String s2) {
		// null = s1 = s2 = null
		if (s1 == null && s2 == null) return 0;
		// null = s1 < s2 != null
		if (s1 == null) return -1;
		// null != s1 > s2 = null
		if (s2 == null) return 1;
		// null != s1 ? s2 != null
		return s1.compareToIgnoreCase(s2);
	}
	
	/**
	 * Implode a collection of objects into a single String, delimited by a given delimiter.
	 * 
	 * @param stringList - a list of objects s1, s2, ... - the objects toString() 
	 * 			method is called to create the list
	 * @param delim - a delimiter _d_
	 * @return a concatenated representation of the string collection s1_d_s2_d_...
	 */
	public static String implodeStringCollection(final Collection<? extends Object> stringList, final String delim) {
		if (stringList == null || delim == null) return "";
		int i = 0;
		final StringBuffer sb = new StringBuffer();
		for (final Object elem : stringList) {
			if (i != 0) {
				sb.append(delim);
			}
			sb.append(elem);
			i++;
		} 
		return sb.toString();
	}
	
	/**
	 * Implode a String array into a single string, delimited by a given delimiter
	 * 
	 * @param pieces -
	 * 			an array of strings
	 * @param delim -
	 * 			a delimiter
	 * @return concatenated representation of the string array
	 */
	public static String implodeStringArray(String[] pieces, String delim) {
		return implodeStringCollection(Arrays.asList(pieces), delim);
	}
}