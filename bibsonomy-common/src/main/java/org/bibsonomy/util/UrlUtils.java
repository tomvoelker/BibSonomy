/**
 *  
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *   
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group, 
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
import java.net.URLEncoder;

/**
 * Convenience methods to work with URLs
 *
 * @author Dominik Benz
 * @version $Id$
 */
public class UrlUtils {

	private static final int MAX_LEN_URL   = 6000;
	
	/**
	 * TODO: improve documentation
	 */
	public static final String BROKEN_URL = "/brokenurl#";	

	/**
	 * Cleans a URL and makes it valid. This includes 
	 * <ul>
	 * <li> checking if it starts with a known protocol (http, ftp, gopher, https) or {@link #BROKEN_URL} (which means it is valid, too),
	 * <li> checking if it is a <em>BibTeX</em> URL and removing the surrounding macro,
	 * <li> checking if it is an empty string or 
	 * <li> else just returning it marked as a broken url (see {@link #BROKEN_URL}).
	 * </ul>
	 * 
	 * @param url the URL which should be checked and cleaned
	 * @return the checked and cleaned URL
	 */
	public static String cleanUrl(String url) {
		if (url == null) {
			return null;
		}
		
		// remove linebreaks, etc.
		url = url.replaceAll("\\n|\\r", "");
		// this should be the most common case: a valid URL
		if (url.startsWith("http://") || 
			url.startsWith("ftp://") ||
			url.startsWith("file://") ||
			url.startsWith(BROKEN_URL) ||
			url.startsWith("gopher://") ||
			url.startsWith("https://")) {
			return StringUtils.cropToLength(url, MAX_LEN_URL);
		} else if (url.startsWith("\\url{") && url.endsWith("}")) {
			// remove \\url{...}
			return StringUtils.cropToLength(url.substring(5, url.length() - 1), MAX_LEN_URL);
		} else if (url.trim().equals("")){
			// handle an empty URL
			return "";
		} 
		
		// URL is neither empty nor valid: mark it as broken
		return StringUtils.cropToLength(BROKEN_URL + url, MAX_LEN_URL);
	}
	
	/**
	 * Check whether given url is valid.
	 * @param url The url to test
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
		if (url == null || url.equals("http://") || url.startsWith(UrlUtils.BROKEN_URL)) 
			return false;
		return true;
	}
	
	/**
	 * Set a parameter in a given URL-String
	 * 
	 * ATTENTION: to ease parsing, fragment identifiers are not supported
	 * 
	 * @param urlString 
	 * 				- the URL string
	 * @param paramName 
	 * 				- the parameter name
	 * @param paramValue 
	 * 				- the parameter value
	 * @return the given URL string with the parameter set
	 */
	public static String setParam(final String urlString, final String paramName, final String paramValue) {
		if (paramName == null) return urlString;
		if (urlString.matches(".*([&\\?])" + paramName +  "\\=[^&#$]+.*")) {
			// parameter is already present - replace its value
			return urlString.replaceAll("([&\\?])" + paramName +  "\\=[^&#$]+", "$1" + paramName + "=" + paramValue);
		}

		if (urlString.matches(".*\\?.*")) {
			// parameter not present, but query present in url -> append &param=value;
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
	 * 				- the URL String
	 * @param paramName
	 * 				- the parameter to be removed
	 * @return the given URL string with the parameter removed
	 */
	public static String removeParam(final String urlString, final String paramName) {
		if (paramName == null) return urlString;
		if (urlString.matches(".*([&\\?])" + paramName +  "\\=[^&#$]+.*")) {
			// parameter is present - remove it
			String urlWithParamRemoved = urlString.replaceAll("([&\\?])" + paramName +  "\\=[^&#$]+", "");
			// make sure first param is initialized with ?
			return urlWithParamRemoved.replaceFirst("([&\\?])([^\\=]+)\\=([^&#$]+)", "?$2=$3");
		}
		// parameter not present - return URL as it is
		return urlString;
	}
	
	/**
	 * When using URLEncoder.encode, also 'reserved' characters defining the structure of
	 * a URL are encoded; these are:
	 * 
	 * $ & + , / : ; ? @
	 *   
	 * This is a helper function to directly encode URLs, while retaining those characters
	 * in order to enable parsing of the encoded URL string
	 * 
	 * @param url an URl string
	 * @return an encoded URL string with reserved characters ($&+,/:;?@) preserved
	 */
	public static String encodeURLExceptReservedChars(final String url) {
		try {
			final String encodedURL = URLEncoder.encode(url, "UTF-8");
			return encodedURL.replaceAll("\\%24", "\\$").
			 				  replaceAll("\\%26", "\\&").
			 				  replaceAll("\\%2B", "\\+").
			 				  replaceAll("\\%2C", "\\,").
			 				  replaceAll("\\%2F", "\\/").
			 				  replaceAll("\\%3A", "\\:").
			 				  replaceAll("\\%3B", "\\;").
			 				  replaceAll("\\%3D", "\\=").
			 				  replaceAll("\\%3F", "\\?").
			 				  replaceAll("\\%40", "\\@");
		} catch (final UnsupportedEncodingException ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}
}