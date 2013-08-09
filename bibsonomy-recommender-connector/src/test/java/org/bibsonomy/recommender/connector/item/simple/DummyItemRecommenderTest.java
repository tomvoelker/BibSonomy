package org.bibsonomy.recommender.connector.item.simple;

import java.util.SortedSet;

import junit.framework.Assert;

import org.bibsonomy.model.User;
import org.bibsonomy.recommender.connector.database.RecommenderBibTexDBLogic;
import org.bibsonomy.recommender.connector.model.ResourceWrapper;
import org.bibsonomy.recommender.connector.model.UserWrapper;
import org.bibsonomy.recommender.connector.testutil.DummyDBAccess;
import org.bibsonomy.recommender.connector.testutil.RecommenderTestContext;
import org.junit.BeforeClass;
import org.junit.Test;

import recommender.core.database.DBLogic;
import recommender.core.interfaces.database.RecommenderDBAccess;
import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.RecommendedItem;
import recommender.impl.database.DBLogConfigItemAccess;
import recommender.impl.item.simple.DummyItemRecommender;

public class DummyItemRecommenderTest {

	private static DBLogic<ItemRecommendationEntity, RecommendedItem> dbLogic;
	private static RecommenderDBAccess dbAccess;
	
	private static final int RECOMMENDATIONS_TO_CALCULATE = 4;

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUp() {
		dbLogic = RecommenderTestContext.getBeanFactory().getBean(DBLogConfigItemAccess.class);
		dbAccess = new DummyDBAccess();
	}
	
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
			Assert.assertNotNull(item.getItem().getId());
			if(item.getItem() instanceof ResourceWrapper) {
				Assert.assertNotNull(((ResourceWrapper) item.getItem()).getResource().getTitle());
			}
		}
		
	}
	
}
