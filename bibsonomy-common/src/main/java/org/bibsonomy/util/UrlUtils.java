package org.bibsonomy.util;

/**
 * Convenience methods to work with URLs
 *
 * @author Dominik Benz
 * @version $Id$
 */
public class UrlUtils {
	
	private static final int MAX_LEN_TITLE       = 6000;
	private static final int MAX_LEN_URL         = 6000;
	private static final String BROKEN_URL       = "/brokenurl#";	

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
	public static String cleanUrl (String url) {
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
		} else {
			// URL is neither empty nor valid: mark it as broken
			return StringUtils.cropToLength(BROKEN_URL + url, MAX_LEN_URL);
		}
	}
	
	/**
	 * Set a parameter in a given URL-String
	 * 
	 * @param urlString the URL string
	 * @param paramName the parameter name
	 * @param paramValue the parameter value
	 * @return the given URL string with the parameter set
	 */
	public static String setParam(String urlString, String paramName, String paramValue) {
					
		if (urlString.matches(".*([&\\?])" + paramName +  "\\=[^&#$]+.*")) {
			// parameter is already present - replace its value
			return urlString.replaceAll("([&\\?])" + paramName +  "\\=[^&#$]+", "$1" + paramName + "=" + paramValue);
		}
		else if (urlString.matches(".*\\?.*")) {
			// parameter not presetn, but query present in url -> append &param=value;
			return urlString + "&" + paramName + "=" + paramValue;
		}
		else {
			// no query at all present in url -> append ?param=value
			return urlString + "?" + paramName + "=" + paramValue;
		}
	}	
}