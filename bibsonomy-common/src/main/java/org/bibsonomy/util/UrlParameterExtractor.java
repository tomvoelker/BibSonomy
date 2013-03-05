package org.bibsonomy.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lightweight tool for extracting the value of a certain parameter in the querystring of a url.
 * This is probably the n-millionth implementation of a querystring parser. httpcore (of httpclient >= v4.x) has one but we cannot use it since it is only a runtime-dependency.
 * Feel free to replace this if you find something better.
 * 
 * @author jensi
 * @version $Id$
 */
public class UrlParameterExtractor {
	private final Pattern extractParameterValuePattern;

	/**
	 * @param parameterName name of the parameter to be extracted
	 */
	public UrlParameterExtractor(String parameterName) {
		this.extractParameterValuePattern = Pattern.compile(".*\\?(.*&)?" + parameterName + "=([^&]*).*");
	}
	
	/**
	 * @param url the url to be parsed
	 * @return the parameter
	 */
	public String parseParameterValueFromUrl(String url) {
		Matcher m = this.extractParameterValuePattern.matcher(url);
		if (m.matches() == false) {
			return null;
		}
		String encodedValue = m.group(2);
		try {
			return URLDecoder.decode(encodedValue, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			throw new IllegalStateException(ex);
		}
	}
}
