package org.bibsonomy.recommender.item.simple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.SortedSet;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.recommender.item.model.RecommendationUser;
import org.bibsonomy.recommender.item.model.RecommendedPost;
import org.bibsonomy.recommender.item.service.RecommenderMainItemAccess;
import org.bibsonomy.recommender.item.testutil.DummyMainItemAccess;
import org.junit.Test;


public class DummyItemRecommenderTest {
	private static final int RECOMMENDATIONS_TO_CALCULATE = 4;
	
	@Test
	public void testDummyItemRecommender() {
		final RecommenderMainItemAccess<Bookmark> dbAccess = new DummyMainItemAccess<Bookmark>() {
			/* (non-Javadoc)
			 * @see org.bibsonomy.recommender.item.testutil.DummyMainItemAccess#createResource()
			 */
			@Override
			protected Bookmark createResource() {
				return new Bookmark();
			}
			
		};
		
		final DummyItemRecommender<Bookmark> rec = new DummyItemRecommender<Bookmark>();
		rec.setDbAccess(dbAccess);
		
		RecommendationUser entity = new RecommendationUser();
		entity.setUserName("abc");
		SortedSet<RecommendedPost<Bookmark>> recommendations = rec.getRecommendation(entity);
		assertEquals(rec.getNumberOfItemsToRecommend(), recommendations.size());
		
		
		rec.setNumberOfItemsToRecommend(RECOMMENDATIONS_TO_CALCULATE);
		recommendations = rec.getRecommendation(entity);
		
		// check the result attributes not to be null
		for (RecommendedPost<Bookmark> item : recommendations) {
			assertNotNull(item.getRecommendationId());
			assertNotNull(item.getTitle());
		}
		
		// check for correct count of recommendations
		assertEquals(RECOMMENDATIONS_TO_CALCULATE, recommendations.size());
	}
}
