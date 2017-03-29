/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
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
package org.bibsonomy.search.es.search.tokenizer;

import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * A very simple tokenizer
 *
 * @author jensi
 */
public class SimpleTokenizer implements Iterable<String> {

	private static final Pattern nonStandardCharsPattern = Pattern.compile("[^\\p{Alpha}\\p{Digit}\\s]");
	private static final Pattern delimiterPattern = Pattern.compile("\\s{1,}");
	
	private final String toBeTokenized;
	
	/**
	 * @param toBeTokenized the string to be tokenized
	 */
	public SimpleTokenizer(final String toBeTokenized) {
		if (toBeTokenized == null) {
			this.toBeTokenized = toBeTokenized;
		} else {
			this.toBeTokenized = nonStandardCharsPattern.matcher(toBeTokenized).replaceAll("");
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<String> iterator() {
		if (this.toBeTokenized == null) {
			return Collections.<String>emptyList().iterator();
		}
		return new Scanner(toBeTokenized).useDelimiter(delimiterPattern);
	}

}
