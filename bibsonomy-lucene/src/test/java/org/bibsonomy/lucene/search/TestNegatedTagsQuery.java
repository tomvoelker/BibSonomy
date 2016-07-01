/**
 * BibSonomy - A blue social bookmark and publication sharing system.
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
package org.bibsonomy.lucene.search;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.enums.SearchType;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.lucene.index.LuceneResourceIndex;
import org.bibsonomy.lucene.index.manager.LuceneResourceManager;
import org.bibsonomy.lucene.util.LuceneSpringContextWrapper;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResultList;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestNegatedTagsQuery extends AbstractDatabaseManagerTest {

	private static LuceneResourceManager<BibTex> manager;
	private static LuceneResourceSearch<BibTex> searcher;
	private static final String BIB_RESOURCE_TYPE 	= "Bibtex";

	/**
	 * generates the gold standard publication index
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void initLucene() throws Exception {
		// delete old indices
		manager = (LuceneResourceManager<BibTex>) LuceneSpringContextWrapper.getBeanFactory().getBean("lucenePublicationManager");
		searcher = manager.getSearcher();
	}

	@Before
	public void resetDatabaseAndIndex() {
		final List<LuceneResourceIndex<BibTex>> resourceIndices = manager.getResourceIndeces();
		for (final LuceneResourceIndex<?> index : resourceIndices) {
			index.deleteIndex();
		}
		/*
		 * reset database
		 */
		initDatabase();

		// create index
		for (int i = 0; i < manager.getIndicesInfos().size(); ++i) {
			manager.regenerateIndex(i, false);
		}

	}

	@Test
	public void test() {
		final List<String> testTags = new LinkedList<String>();
		final List<String> negatedTags = new LinkedList<String>();
		// We have only one document in the test index that contains at most two tags
		// TODO To make this test more meaningful we need more tags in the test documents
		// Check the only available document with two tags.
		// These are: "testbibtex" and "testtag"
		testTags.add("testbibtex");
		testTags.add("testtag");
		ResultList<Post<BibTex>> resList;
		resList = query(searcher, testTags, negatedTags);
		assertEquals(1, resList.size());
		testTags.remove("testtag");
		resList = query(searcher, testTags, negatedTags);
		assertEquals(1, resList.size());
		negatedTags.add("testtag");
		resList = query(searcher, testTags, negatedTags);
		assertEquals(0, resList.size());
		negatedTags.remove("testtag");
		resList = query(searcher, testTags, negatedTags);
		assertEquals(1, resList.size());
		negatedTags.add("google");
		resList = query(searcher, testTags, negatedTags);
		assertEquals(1, resList.size());
	}

	private ResultList<Post<BibTex>> query(final LuceneResourceSearch<BibTex> lsr, final List<String> testTags, final List<String> negatedTags) {
		ResultList<Post<BibTex>> resList;
		resList = (ResultList<Post<BibTex>>) lsr.getPosts(null, null, null, null, Collections.singletonList("public"),SearchType.LOCAL, null, null, null, null, testTags, null, null, null, negatedTags, null, 100, 0);
		return resList;
	}

}
