/**
 *
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
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
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lightweight tool for extracting the value of a certain parameter in the querystring of a url.
 * This is probably the n-millionth implementation of a querystring parser. httpcore (of httpclient >= v4.x) has one but we cannot use it since it is only a runtime-dependency.
 * Feel free to replace this if you find something better.
 * 
 * @author jensi
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
		final Matcher m = this.extractParameterValuePattern.matcher(url);
		if (!m.matches()) {
			return null;
		}
		String encodedValue = m.group(2);
		try {
			return URLDecoder.decode(encodedValue, StringUtils.CHARSET_UTF_8);
		} catch (UnsupportedEncodingException ex) {
			throw new IllegalStateException(ex);
		}
	}
}
