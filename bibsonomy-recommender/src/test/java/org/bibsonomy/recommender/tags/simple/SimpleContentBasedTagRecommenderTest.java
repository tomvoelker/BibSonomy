package org.bibsonomy.recommender.tags.simple;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.User;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class SimpleContentBasedTagRecommenderTest {

	@Test
	public void testGetRecommendedTags() {
		final Post<Bookmark> post = new Post<Bookmark>();
		post.setUser(new User("jaeschke"));
		
		final Bookmark bookmark = new Bookmark();
		post.setResource(bookmark);
		bookmark.setUrl("http://nepomuk.semanticdesktop.org/xwiki/bin/view/Main/FinalReviewNov2008Script");
		bookmark.setTitle("FinalReviewNov2008Script - Main - NEPOMUK");

		final SimpleContentBasedTagRecommender recommender = new  SimpleContentBasedTagRecommender();
		
		final LinkedList<RecommendedTag> recommendedTags = new LinkedList<RecommendedTag>(recommender.getRecommendedTags(post));
		
		final String[] testTags = new String[]{"nepomuk", "main", "finalreviewnov2008script"};

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
