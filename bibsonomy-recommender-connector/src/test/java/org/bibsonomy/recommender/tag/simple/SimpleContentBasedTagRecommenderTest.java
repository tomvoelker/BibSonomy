package org.bibsonomy.recommender.tag.simple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.recommender.tag.model.RecommendedTag;
import org.junit.Test;

/**
 * Test of the {@link SimpleContentBasedTagRecommender} on base of
 * BibSonomy's implementation of the recommender's model classes.
 * 
 * @author rja
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
		
		final LinkedList<RecommendedTag> recommendedTags = new LinkedList<RecommendedTag>(recommender.getRecommendation(post));
		
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
	
	@Test
	public void testGetRecommendedTags2() {
		final Post<Bookmark> post = new Post<Bookmark>();
		post.setUser(new User("jaeschke"));
		
		final Bookmark bookmark = new Bookmark();
		post.setResource(bookmark);
		bookmark.setUrl("http://nepomuk.semanticdesktop.org/xwiki/bin/view/Main/FinalReviewNov2008Script");
		/*
		 * german numbers are stop words!
		 */
		bookmark.setTitle("Eins, Zwei, Drei, Vier tolle Wörter und doch nur fünf Recommendations! Main - NEPOMUK The best semantic desktop on earth");

		final SimpleContentBasedTagRecommender recommender = new  SimpleContentBasedTagRecommender();
		
		/*
		 * set size
		 */
		recommender.setNumberOfTagsToRecommend(5);
		
		final LinkedList<RecommendedTag> recommendedTags = new LinkedList<RecommendedTag>(recommender.getRecommendation(post));
		
		/*
		 * check size
		 */
		assertEquals(5, recommendedTags.size());
		
		final String[] testTags = new String[]{"tolle", "wörter", "recommendations", "main", "nepomuk"};

		for (final String s: testTags) {
			assertTrue(recommendedTags.contains(new RecommendedTag(s, 0.0, 0.0)));
		}
	}

}
