/**
 * BibSonomy-Rest-Server - The REST-server.
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

import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.util.UrlUtils;


/**
 * @author wla
 * @author dzo
 */
public class URLDecodingPathTokenizer implements Iterator<String> {
	private String[] tokens;
	private int pos;
	
	/**
	 * 
	 * @param str
	 * @param delim
	 */
	public URLDecodingPathTokenizer(final String str, final String delim) {
		if (str == null) {
			throw new IllegalArgumentException("path is null");
		}
		this.tokens = str.split(delim);
		if (str.startsWith("/")) {
			this.pos = 0;
		} else {
			this.pos = -1;
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return this.pos + 1 < tokens.length;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public String next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		try {
			return UrlUtils.decodePathSegment(this.tokens[++this.pos]);
		} catch (final URISyntaxException e) {
			throw new InternServerException(e);
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @return the number of remaining tokens
	 */
	public int countRemainingTokens() {
		return this.tokens.length - this.pos - 1;
	}

}
