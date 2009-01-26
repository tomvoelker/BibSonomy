/**
 *  
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
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
import java.util.Collection;
import java.util.Iterator;

import it.unimi.dsi.fastutil.chars.CharOpenHashSet; 

/**
 * Some methods for handling strings.
 */
public class StringUtils {
	
	// the following is used by the escapeControlCharacters-methods below	
	private static final CharOpenHashSet illegalChars;
	/** replacement character for illegal characters */
	public static final char ILLEGAL_CHAR_SUBSTITUTE = '\uFFFD'; 	
	
    static {
        final String escapeString = "\u0000\u0001\u0002\u0003\u0004\u0005" +
            "\u0006\u0007\u0008\u000B\u000C\u000E\u000F\u0010\u0011\u0012" +
            "\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001A\u001B\u001C" +
            "\u001D\u001E\u001F\uFFFE\uFFFF";

        illegalChars = new CharOpenHashSet();
        for (int i = 0; i < escapeString.length(); i++) {
            illegalChars.add(escapeString.charAt(i));
        }
    } 	

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
			return HashUtils.getMD5Hash(string.getBytes(charset));
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
     * Substitutes all illegal characters in the given string by the value of
     * {@link StringUtils#ILLEGAL_CHAR_SUBSTITUTE}. 
     *
     * @param string
     * @return a string with control characters replaced
     */
    public static String escapeControlCharacters(String string) {
    	if (string == null) return string;    	
    	return StringUtils.escapeControlCharacters(string.toCharArray()).toString();    	
    }
    
    /**
     * Substitutes all illegal characters in the given char array by the value of
     * {@link StringUtils#ILLEGAL_CHAR_SUBSTITUTE}. If no illegal characters
     * were found, no copy is made and the given char array
     * 
     * @param ch a char array
     * @return a char array with control characters replaced
     */
    public static char[] escapeControlCharacters(char[] ch) {
    	if (ch == null) return ch;
        char[] copy = null;
        boolean copied = false;
        for (int i = 0; i < ch.length; i++) {
            if (StringUtils.illegalChars.contains(ch[i])) {
                if (!copied) {
                    copy = ch;
                    copied = true;
                }
                copy[i] = StringUtils.ILLEGAL_CHAR_SUBSTITUTE;
            }
        }
        return copied ? copy : ch;
    }
    
    /**
     * Checks if a given char c is a control charcter; if yes, a replacement
     * is returned, if no, the char c is returned
     * 
     * @param c a char
     * @return a char with control characters replaced
     */
    public static char escapeControlCharacter(char c) {
    	if (StringUtils.illegalChars.contains(c)) {
    		return StringUtils.ILLEGAL_CHAR_SUBSTITUTE;
    	}
    	return c;
    }
    
}