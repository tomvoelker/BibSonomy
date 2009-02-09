package org.bibsonomy.recommender.tags.simple;

import static org.junit.Assert.*;

import java.util.Set;
import java.util.SortedSet;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.User;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class SimpleContentBasedTagRecommenderTest {

	/**
	 * FIXME:
	 * This test does not work because tags in the set are ordered by their score 
	 * and the {@link SortedSet} implementation of {@link Set#contains(Object)} 
	 * uses the comparator to find the tag in the set. Since using contains we want
	 * to use equals(), this does not work.
	 * 
	 * Suggested fix: 
	 */
	// @Test
	@Ignore
	public void testGetRecommendedTags() {
		final Post<Bookmark> post = new Post<Bookmark>();
		post.setUser(new User("jaeschke"));
		
		final Bookmark bookmark = new Bookmark();
		post.setResource(bookmark);
		bookmark.setUrl("http://nepomuk.semanticdesktop.org/xwiki/bin/view/Main/FinalReviewNov2008Script");
		bookmark.setTitle("FinalReviewNov2008Script - Main - NEPOMUK");

		final SimpleContentBasedTagRecommender recommender = new  SimpleContentBasedTagRecommender();
		
		final SortedSet<RecommendedTag> recommendedTags = recommender.getRecommendedTags(post);

		final String[] testTags = new String[]{"nepomuk", "main", "Main", "NEPOMUK"};

		for (final String s: testTags) {
			assertTrue(recommendedTags.contains(new RecommendedTag(s, 0.0, 0.0)));
		}
	}
	
	@Test
	public void simpleTest() {
		final SimpleContentBasedTagRecommender recommender = new  SimpleContentBasedTagRecommender();
		assertNotNull(recommender.getInfo());
	}

}
