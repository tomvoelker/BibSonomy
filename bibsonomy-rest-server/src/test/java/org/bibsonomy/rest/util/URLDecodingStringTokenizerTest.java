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

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author dzo
 */
public class URLDecodingStringTokenizerTest {
	
	/**
	 * tests {@link URLDecodingStringTokenizer#nextToken()}
	 */
	@Test
	public void whiteSpace() {
		final URLDecodingStringTokenizer tokenizer = new URLDecodingStringTokenizer("/test/test%20123.txt", "/");
		assertEquals("test", tokenizer.nextToken());
		assertEquals("test 123.txt", tokenizer.nextToken());
	}
	
	/**
	 * tests {@link URLDecodingStringTokenizer#nextToken()} with an url
	 */
	@Test
	public void uri() {
		final URLDecodingStringTokenizer tokenizer = new URLDecodingStringTokenizer("/test/http%3A%2F%2Fwww.bibsonomy.org%2F", "/");
		assertEquals("test", tokenizer.nextToken());
		assertEquals("http://www.bibsonomy.org/", tokenizer.nextToken());
	}
	
	/**
	 * tests {@link URLDecodingStringTokenizer#nextToken()} with + in the url
	 */
	@Test
	@Ignore // @see #1934
	public void plus() {
		final URLDecodingStringTokenizer tokenizer = new URLDecodingStringTokenizer("2009_Science_Discher_GF+ForceInfluenceOnSC.pdf", "/");
		assertEquals("2009_Science_Discher_GF+ForceInfluenceOnSC.pdf", tokenizer.nextToken());
	}
}
