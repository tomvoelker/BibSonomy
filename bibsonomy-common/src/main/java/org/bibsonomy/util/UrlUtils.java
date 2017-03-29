/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.util;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.BitSet;

/**
 * Convenience methods to work with URLs
 *
 * @author Dominik Benz
 */
public class UrlUtils {

	/**
	 * mark = "-" | "_" | "." | "!" | "~" | "*" | "'" | "(" | ")"
	 */
	private final static BitSet MARK = new BitSet();

	/** lowalpha = "a" - "z" */
	private final static BitSet LOW_ALPHA = new BitSet();
	
	/** upalpha = "A" - "Z" */
	private final static BitSet UP_ALPHA = new BitSet();
	
	/** alpha = lowalpha | upalpha */
	private final static BitSet ALPHA = new BitSet();
	
	/** digit = "0" - "9" */
	private final static BitSet DIGIT = new BitSet();
	
	/** alphanum = alpha | digit */
	private final static BitSet ALPHANUM = new BitSet();
	
	/** unreserved = alphanum | '-' | '_' | '.' | '~' */
	private final static BitSet UNRESERVED = new BitSet();
	
	private final static BitSet SUB_DELIMS = new BitSet();
	
	private final static BitSet PATH = new BitSet();
	
	/**
	 * builds the bitset for allowed uri syntax see http://www.ietf.org/rfc/rfc3986.txt
	 */
	static {
		// sub-delims
		SUB_DELIMS.set('!');
		SUB_DELIMS.set('$');
		SUB_DELIMS.set('&');
		SUB_DELIMS.set('\'');
		SUB_DELIMS.set('(');
		SUB_DELIMS.set(')');
		SUB_DELIMS.set('*');
		SUB_DELIMS.set('+');
		SUB_DELIMS.set(',');
		SUB_DELIMS.set(';');
		SUB_DELIMS.set('=');
		
		for (char c = 'a'; c <= 'z'; c++) {
			LOW_ALPHA.set(c);
		}
		
		for (char c = 'A'; c <= 'Z'; c++) {
			UP_ALPHA.set(c);
		}
		
		for (char c = '0'; c <= '9'; c++) {
			DIGIT.set(c);
		}
		
		// set alpha
		ALPHA.or(LOW_ALPHA);
		ALPHA.or(UP_ALPHA);
		
		// set alphanum
		ALPHANUM.or(ALPHA);
		ALPHANUM.or(DIGIT);
		
		// set unreserved
		UNRESERVED.or(ALPHANUM);
		UNRESERVED.or(MARK);
		UNRESERVED.set('-');
		UNRESERVED.set('_');
		UNRESERVED.set('.');
		UNRESERVED.set('~');
		
		PATH.or(UNRESERVED);
		PATH.or(SUB_DELIMS);
		// : @
		PATH.set(':');
		PATH.set('@');
	}

	/** https schema */
	private static final String HTTPS_SCHEMA = "https://";

	private static final String BIBTEX_URL_COMMAND = "\\url{";

	private static final int MAX_LEN_URL = 6000;

	/**
	 * TODO: improve documentation
	 */
	public static final String BROKEN_URL = "/brokenurl#";

	/**
	 * Cleans a URL and makes it valid. This includes
	 * <ul>
	 * <li>checking if it starts with a known protocol (http, ftp, gopher,
	 * https) or {@link #BROKEN_URL} (which means it is valid, too),
	 * <li>checking if it is a <em>BibTeX</em> URL and removing the surrounding
	 * macro,
	 * <li>checking if it is an empty string or
	 * <li>else just returning it marked as a broken url (see
	 * {@link #BROKEN_URL}).
	 * </ul>
	 * 
	 * @param url
	 *            the URL which should be checked and cleaned
	 * @return the checked and cleaned URL
	 */
	public static String cleanUrl(String url) {
		if (url == null) {
			return null;
		}

		// remove linebreaks, etc.
		url = url.replaceAll("\\n|\\r", "");
		// this should be the most common case: a valid URL
		if (url.startsWith("http://") || url.startsWith("ftp://") || url.startsWith("file://") || url.startsWith(BROKEN_URL) || url.startsWith("gopher://") || url.startsWith("https://")) {
			return StringUtils.cropToLength(url, MAX_LEN_URL);
		} else if (url.contains(BIBTEX_URL_COMMAND)) {
			return StringUtils.cropToLength(cleanBibTeXUrl(url), MAX_LEN_URL);
		} else if (url.trim().equals("")) {
			// handle an empty URL
			return "";
		}

		// URL is neither empty nor valid: mark it as broken
		return StringUtils.cropToLength(BROKEN_URL + url, MAX_LEN_URL);
	}

	/**
	 * Removes \\url{} from the URL. If the URL does not contain this command,
	 * the trimmed URL is returned.
	 * 
	 * @param url
	 * @return The cleaned URL
	 */
	public static String cleanBibTeXUrl(final String url) {
		if (present(url)) {
			final String trimmedUrl = url.trim();
			if (trimmedUrl.startsWith(BIBTEX_URL_COMMAND) && trimmedUrl.endsWith("}")) {
				// remove \\url{...}
				return trimmedUrl.substring(5, trimmedUrl.length() - 1);
			}
			return trimmedUrl;
		}
		return url;
	}

	/**
	 * Check whether given url is valid.
	 * 
	 * @param url
	 *            The url to test
	 * @return <true> iff the given url is valid
	 */
	public static boolean isValid(String url) {
		/*
		 * clean url
		 */
		url = UrlUtils.cleanUrl(url);

		/*
		 * check url
		 */
		return !(url == null || url.equals("http://") || url.startsWith(UrlUtils.BROKEN_URL));
	}

	/**
	 * Extracts the first element of a "/" delimited-path. E.g., "a" for
	 * "/a/b/c".
	 * 
	 * @param path
	 * @return The first element of the path.
	 */
	public static String getFirstPathElement(final String path) {
		int start = 0;
		int end = path.length();
		if (path.startsWith("/")) {
			start = 1;
		}
		final int indexOf = path.indexOf("/", start);
		if (indexOf > 0) {
			end = indexOf;
		}
		return path.substring(start, end);
	}

	/**
	 * Set a parameter in a given URL-String
	 * 
	 * ATTENTION: to ease parsing, fragment identifiers are not supported
	 * 
	 * @param urlString
	 *            - the URL string
	 * @param paramName
	 *            - the parameter name
	 * @param paramValue
	 *            - the parameter value
	 * @return the given URL string with the parameter set
	 */
	public static String setParam(final String urlString, final String paramName, final String paramValue) {
		if (paramName == null) return urlString;
		if (urlString.matches(".*([&\\?])" + paramName + "\\=[^&#$]+.*")) {
			// parameter is already present - replace its value
			return urlString.replaceAll("([&\\?])" + paramName + "\\=[^&#$]+", "$1" + paramName + "=" + paramValue);
		}

		if (urlString.matches(".*\\?.*")) {
			// parameter not present, but query present in url -> append
			// &param=value;
			return urlString + "&" + paramName + "=" + paramValue;
		}

		// no query at all present in url -> append ?param=value
		return urlString + "?" + paramName + "=" + paramValue;
	}

	/**
	 * Remove a parameter from a given URL string
	 * 
	 * ATTENTION: to ease parsing, fragment identifiers are not supported
	 * 
	 * @param urlString
	 *            - the URL String
	 * @param paramName
	 *            - the parameter to be removed
	 * @return the given URL string with the parameter removed
	 */
	public static String removeParam(final String urlString, final String paramName) {
		if (paramName == null) return urlString;
		if (urlString.matches(".*([&\\?])" + paramName + "\\=[^&#$]+.*")) {
			// parameter is present - remove it
			String urlWithParamRemoved = urlString.replaceAll("([&\\?])" + paramName + "\\=[^&#$]+", "");
			// make sure first param is initialized with ?
			return urlWithParamRemoved.replaceFirst("([&\\?])([^\\=]+)\\=([^&#$]+)", "?$2=$3");
		}
		// parameter not present - return URL as it is
		return urlString;
	}

	/**
	 * When using URLEncoder.encode, also 'reserved' characters defining the
	 * structure of a URL are encoded; these are:
	 * 
	 * $ & + , / : ; ? @
	 * 
	 * This is a helper function to directly encode URLs, while retaining those
	 * characters in order to enable parsing of the encoded URL string
	 * 
	 * @param url
	 *            an URl string
	 * @return an encoded URL string with reserved characters ($&+,/:;?@)
	 *         preserved
	 */
	public static String encodeURLExceptReservedChars(final String url) {
		try {
			final String encodedURL = URLEncoder.encode(url, StringUtils.CHARSET_UTF_8);
			return encodedURL.replaceAll("\\%24", "\\$").replaceAll("\\%26", "\\&").replaceAll("\\%2B", "\\+").replaceAll("\\%2C", "\\,").replaceAll("\\%2F", "\\/").replaceAll("\\%3A", "\\:").replaceAll("\\%3B", "\\;").replaceAll("\\%3D", "\\=").replaceAll("\\%3F", "\\?").replaceAll("\\%40", "\\@");
		} catch (final UnsupportedEncodingException ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}

	/**
	 * 
	 * @param pathSegment
	 * @return the encoded path segment using UTF-8 encoding
	 */
	public static String encodePathSegment(final String pathSegment) {
		try {
			return encodePathSegment(pathSegment, "UTF-8");
		} catch (final UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * encodes a path segment
	 * 
	 * @param pathSegment
	 * @param charset
	 * @return the encoded path segment
	 * @throws UnsupportedEncodingException
	 */
	public static String encodePathSegment(final String pathSegment, final String charset) throws UnsupportedEncodingException {
		if (pathSegment == null) {
			return null;
		}
		// start at *3 for the worst case when everything is %encoded on one byte
		final StringBuilder encoded = new StringBuilder(pathSegment.length() * 3);
		final char[] toEncode = pathSegment.toCharArray();
		for (int i = 0; i < toEncode.length; i++) {
			char c = toEncode[i];
			if (PATH.get(c)) {
				encoded.append(c);
			} else {
				final byte[] bytes = String.valueOf(c).getBytes(charset);
				for (int j = 0; j < bytes.length; j++) {
					byte b = bytes[j];
					// make it unsigned
					final int u8 = b & 0xFF;
					encoded.append("%");
					if (u8 < 16) {
						encoded.append("0");
					}
					encoded.append(Integer.toHexString(u8));
				}
			}
		}
		return encoded.toString();
	}
	
	/**
	 * 
	 * @param path
	 * @return the utf-8 encoded string
	 * @throws URISyntaxException
	 */
	public static String decodePathSegment(final String path) throws URISyntaxException {
		return decodePathSegment(path, "UTF-8");
	}
	
	/**
	 * decodes a path part string
	 * @param path
	 * @param charset
	 * @return the decoded path
	 * @throws URISyntaxException
	 */
	public static String decodePathSegment(final String path, final String charset) throws URISyntaxException {
		try{
			byte[] ascii = path.getBytes("ASCII");
			byte[] decoded = new byte[ascii.length];
			int j = 0;
			for(int i = 0; i < ascii.length; i++, j++){
				if (ascii[i] == '%') {
					if (i + 2 >= ascii.length) {
						throw new URISyntaxException(path, "Invalid URL-encoded string at char "+i);
					}
					// get the next two bytes
					byte first = ascii[++i];
					byte second = ascii[++i];
					decoded[j] = (byte) ((hexToByte(first) * 16) + hexToByte(second));
				} else {
					decoded[j] = ascii[i];
				}
			}
			// now decode
			return new String(decoded, 0, j, charset);
		} catch (final UnsupportedEncodingException e){
			throw new URISyntaxException(path, "Invalid encoding: " + charset);
		} catch (final IllegalArgumentException e) {
			throw new URISyntaxException(path, "Invalid encoded value: " + e.getMessage());
		}
	}
	
	private static byte hexToByte(byte b) throws IllegalArgumentException {
		switch(b){
			case '0': return 0;
			case '1': return 1;
			case '2': return 2;
			case '3': return 3;
			case '4': return 4;
			case '5': return 5;
			case '6': return 6;
			case '7': return 7;
			case '8': return 8;
			case '9': return 9;
			case 'a':
			case 'A': return 10;
			case 'b':
			case 'B': return 11;
			case 'c':
			case 'C': return 12;
			case 'd':
			case 'D': return 13;
			case 'e':
			case 'E': return 14;
			case 'f':
			case 'F': return 15;
		}
		
		throw new IllegalArgumentException(String.valueOf(b) + " not a hex string");
	}

	/**
	 * Encodes the given String with URLEncoder. If that fails, returns the
	 * string.
	 * 
	 * @param s
	 * @return the encoded string (if that fails, returns the string)
	 */
	public static String safeURIEncode(final String s) {
		if (!present(s)) {
			return s; // nothing to do, if empty string
		}

		try {
			return URLEncoder.encode(s, StringUtils.CHARSET_UTF_8);
		} catch (UnsupportedEncodingException ex) {
			return s;
		}
	}

	/**
	 * 
	 * Decodes a string with {@link URLDecoder#decode(String, String)} with
	 * UTF-8.
	 * 
	 * @param s
	 * @return the decoded string (if that fails, returns the string)
	 */
	public static String safeURIDecode(final String s) {
		if (!present(s)) {
			return s; // nothing to do, if empty string
		}

		try {
			return URLDecoder.decode(s, StringUtils.CHARSET_UTF_8);
		} catch (UnsupportedEncodingException ex) {
			return s;
		}
	}

	/**
	 * Normalizes the URL by trimming whitespace and appending "http://", if it
	 * is not present.
	 * 
	 * FIXME: the URL is converted to lower case - this might break some URLs.
	 * 
	 * This is mainly for normalizing the OpenID of a user for matching.
	 * 
	 * @param url
	 *            - the URL that shall be normalized.
	 * @return normalized URL
	 */
	public static String normalizeURL(String url) {
		/*
		 * do nothing if url is empty
		 */
		if (!present(url)) {
			return url;
		}

		/*
		 * remove leading and trailing whitespaces
		 */
		url = url.trim();

		/*
		 * append http suffix if not set
		 */
		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			url = "http://" + url;
		}

		/*
		 * convert to lower case FIXME: This could break some URLs!
		 */
		url = url.toLowerCase();

		return url;
	}

	/**
	 * @param url
	 * @return <code>true</code> if the url is a https url
	 */
	public static boolean isHTTPS(final String url) {
		return present(url) && url.startsWith(HTTPS_SCHEMA);
	}
}