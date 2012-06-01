package org.bibsonomy.lucene.search;

import static org.junit.Assert.*;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.search.Query;
import org.bibsonomy.database.testutil.JNDIBinder;
import org.bibsonomy.lucene.util.LuceneSpringContextWrapper;
import org.bibsonomy.model.BibTex;
import org.junit.Test;

public class LuceneResourceSearchTest {

	@Test
	public void testBuildSearchQuery() throws Exception {
		JNDIBinder.bind();
		LuceneResourceSearch<BibTex> lsr = (LuceneResourceSearch<BibTex>) LuceneSpringContextWrapper.getBeanFactory().getBean("luceneBookmarkSearch");
		List<String> testTags = new LinkedList<String>();
		testTags.add("bibtex");
		testTags.add("1999");
		testTags.add("->Suchmaschine");
		testTags.add("->linux");
		testTags.add("uni");
		testTags.add("laptop");
		Query q = lsr.buildSearchQuery("testuser1", null, null, null, testTags);
		System.out.println(q.toString());

	}
}
