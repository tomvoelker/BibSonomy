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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bibsonomy.search.es.search.tokenizer.SimpleTokenizer;
import org.junit.Assert;
import org.junit.Test;

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public class SimpleTokenizerTest {
	@Test
	public void testIt() {
		List<String >res = new ArrayList<>();
		for (String token : new SimpleTokenizer("Henner Hurz  Schorsche")) {
			res.add(token);
		}
		Assert.assertEquals(Arrays.asList("Henner", "Hurz", "Schorsche"), res);
	}
	
	
	@Test
	public void testIt2() {
		List<String >res = new ArrayList<>();
		for (String token : new SimpleTokenizer("Michael Collins")) {
			res.add(token);
		}
		Assert.assertEquals(Arrays.asList("Michael", "Collins"), res);
	}
	
}
