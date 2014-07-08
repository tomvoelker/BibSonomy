package org.bibsonomy.lucene.search;

import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.search.Query;
import org.bibsonomy.lucene.util.LuceneSpringContextWrapper;
import org.bibsonomy.model.BibTex;
import org.junit.Test;

/**
 * FIXME
 * 
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
