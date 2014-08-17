package org.bibsonomy.recommender.item.content;

import static org.junit.Assert.assertEquals;

import java.util.SortedSet;

import org.bibsonomy.recommender.item.service.RecommenderMainItemAccess;
import org.bibsonomy.recommender.item.testutil.DummyMainItemAccess;
import org.junit.Test;

import recommender.impl.model.RecommendedItem;
import recommender.impl.test.testutil.DummyFactory;

/**
 * This test checks the integrity of the {@link ContentBasedItemRecommender}.
 * It tests the calculation of similarity and the handling of already known resources. 
 * 
 * @author lukas
 */
public class ContentBasedItemRecommenderTest {

	private static final int RECOMMENDATIONS_TO_CALCULATE = 4;
	public static final String DUMMY_CF_USER_NAME = "testCFItem";
	public static final String[] TEST_USER_ITEMS = {"evaluation test", "recommender systems"};
	
	@Test
	public void testContentBasedItemRecommender() {
		final RecommenderMainItemAccess dbAccess = new DummyMainItemAccess();
		ContentBasedItemRecommender reco = new ContentBasedItemRecommender();
		reco.setDbAccess(dbAccess);
		reco.setNumberOfItemsToRecommend(RECOMMENDATIONS_TO_CALCULATE);
		reco.setMaxItemsToEvaluate(4);
		
		SortedSet<RecommendedItem> recommendations = reco.getRecommendation(DummyFactory.getInstanceOfItemRecommendationEntityWithUsername(DUMMY_CF_USER_NAME));
		
		// should be one less, because already known items are not recommended
		assertEquals(RECOMMENDATIONS_TO_CALCULATE - 1, recommendations.size());
		
		// the first element should be 'evaluation trees', because it has one same token
		// as 'evaluation test'
		assertEquals(DummyMainItemAccess.CF_DUMMY_USER_ITEMS[1][0], recommendations.first().getTitle());
	}
}
