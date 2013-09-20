package org.bibsonomy.recommender.connector.item.simple;

import java.util.SortedSet;

import junit.framework.Assert;

import org.bibsonomy.model.User;
import org.bibsonomy.recommender.connector.model.PostWrapper;
import org.bibsonomy.recommender.connector.model.UserWrapper;
import org.bibsonomy.recommender.connector.testutil.DummyMainItemAccess;
import org.bibsonomy.recommender.connector.testutil.RecommenderTestContext;
import org.junit.BeforeClass;
import org.junit.Test;

import recommender.core.database.DBLogic;
import recommender.core.interfaces.database.RecommenderMainItemAccess;
import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.impl.database.DBLogConfigItemAccess;
import recommender.impl.item.simple.DummyItemRecommender;
import recommender.impl.model.RecommendedItem;

public class DummyItemRecommenderTest {

	private static DBLogic<ItemRecommendationEntity, RecommendedItem> dbLogic;
	private static RecommenderMainItemAccess dbAccess;
	
	private static final int RECOMMENDATIONS_TO_CALCULATE = 4;

	@BeforeClass
	public static void setUp() {
		dbLogic = RecommenderTestContext.getBeanFactory().getBean("bibtexRecommenderLogic", DBLogConfigItemAccess.class);
		dbAccess = new DummyMainItemAccess();
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void testDummyItemRecommender() {
		DummyItemRecommender rec = new DummyItemRecommender();
		rec.setDbLogic(dbLogic);
		rec.setDbAccess(dbAccess);
		
		SortedSet<RecommendedItem> recommendations = rec.getRecommendation(new UserWrapper(new User("foobar")));
		Assert.assertEquals(rec.getNumberOfItemsToRecommend(), recommendations.size());
		
		
		rec.setNumberOfItemsToRecommend(RECOMMENDATIONS_TO_CALCULATE);
		recommendations = rec.getRecommendation(new UserWrapper(new User("foobar")));
		Assert.assertEquals(RECOMMENDATIONS_TO_CALCULATE, recommendations.size());
		
		for(RecommendedItem item : recommendations) {
			Assert.assertNotNull(item.getId());
			if(item.getItem() instanceof PostWrapper) {
				Assert.assertNotNull(((PostWrapper) item.getItem()).getPost().getResource().getTitle());
			}
		}
		
	}
	
}
