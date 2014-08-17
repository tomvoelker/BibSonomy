package org.bibsonomy.recommender.item.simple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.SortedSet;

import org.bibsonomy.recommender.item.service.RecommenderMainItemAccess;
import org.bibsonomy.recommender.item.testutil.DummyMainItemAccess;
import org.junit.Test;

import recommender.impl.model.RecommendedItem;
import recommender.impl.test.testutil.DummyFactory;


public class DummyItemRecommenderTest {
	private static final int RECOMMENDATIONS_TO_CALCULATE = 4;
	
	@Test
	public void testDummyItemRecommender() {
		final RecommenderMainItemAccess dbAccess = new DummyMainItemAccess();
		
		final DummyItemRecommender rec = new DummyItemRecommender();
		rec.setDbAccess(dbAccess);
		
		SortedSet<RecommendedItem> recommendations = rec.getRecommendation(DummyFactory.getInstanceOfItemRecommendationEntity());
		assertEquals(rec.getNumberOfItemsToRecommend(), recommendations.size());
		
		
		rec.setNumberOfItemsToRecommend(RECOMMENDATIONS_TO_CALCULATE);
		recommendations = rec.getRecommendation(DummyFactory.getInstanceOfItemRecommendationEntity());
		
		// check the result attributes not to be null
		for (RecommendedItem item : recommendations) {
			assertNotNull(item.getRecommendationId());
			assertNotNull(item.getTitle());
		}
		
		// check for correct count of recommendations
		assertEquals(RECOMMENDATIONS_TO_CALCULATE, recommendations.size());
	}
}
