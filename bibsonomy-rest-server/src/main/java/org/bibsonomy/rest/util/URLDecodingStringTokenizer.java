/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.util;

import static org.bibsonomy.util.UrlUtils.safeURIDecode;

import java.util.StringTokenizer;


/**
 * @author wla
 */
public class URLDecodingStringTokenizer extends StringTokenizer {

	/**
	 * 
	 * @param str
	 */
	public URLDecodingStringTokenizer(final String str) {
		super(str);
	}
	
	/**
	 * 
	 * @param str
	 * @param delim
	 */
	public URLDecodingStringTokenizer(final String str, final String delim) {
		super(str, delim);
	}
	
	/**
	 * @param str
	 * @param delim
	 * @param returnDelims
	 */
	public URLDecodingStringTokenizer(final String str, final String delim, final boolean returnDelims) {
		super(str, delim, returnDelims);
	}

	@Override
	public String nextToken() {
		final String token = super.nextToken();
		return safeURIDecode(token);
	}

}
