package org.bibsonomy.lucene.search;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.database.AbstractDatabaseTest;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.GoldStandardPublicationDatabaseManager;
import org.bibsonomy.database.params.BibtexExtendedParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.lucene.index.LuceneResourceIndex;
import org.bibsonomy.lucene.index.manager.LuceneGoldStandardManager;
import org.bibsonomy.lucene.index.manager.LuceneResourceManager;
import org.bibsonomy.lucene.util.LuceneSpringContextWrapper;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.testutil.TestDatabaseLoader;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestNegatedTagsQuery extends AbstractDatabaseManagerTest {

	private static LuceneResourceManager<BibTex> manager;
	private static LuceneResourceSearch<BibTex> searcher;

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
		resList = lsr.getPosts(null, null, null, Collections.singletonList("public"), null, null, null, testTags, null, null, null, negatedTags, 100, 0);
		return resList;
	}

}
