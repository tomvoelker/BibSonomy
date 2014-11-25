/**
 * BibSonomy-Lucene - Fulltext search facility of BibSonomy
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
package org.bibsonomy.lucene.search;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.search.Query;
import org.bibsonomy.lucene.util.LuceneSpringContextWrapper;
import org.bibsonomy.model.BibTex;
import org.junit.Test;

/**
 * @author mba
 */
public class LuceneResourceSearchTest {

	@Test
	public void testBuildSearchQuery() throws Exception {
		final LuceneResourceSearch<BibTex> lsr = (LuceneResourceSearch<BibTex>) LuceneSpringContextWrapper.getBeanFactory().getBean("luceneBookmarkSearch");
		final List<String> testTags = new LinkedList<String>();
		testTags.add("bibtex");
		testTags.add("1999");
		testTags.add("->Suchmaschine");
		testTags.add("->linux");
		testTags.add("uni");
		testTags.add("laptop");
		final Query q = lsr.buildSearchQuery("testuser1", null, null, null);
//		assertEquals("+(+tas:bibtex +tas:1999 +(tas:suchmaschine tas:fireball tas:google tas:yahoo) +(tas:linux tas:debian tas:opensuse tas:ubuntu) +tas:uni +tas:laptop)", q.toString());
	}
}
