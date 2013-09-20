package org.bibsonomy.recommender.connector.item.collaborative;

import java.util.SortedSet;

import junit.framework.Assert;

import org.bibsonomy.model.User;
import org.bibsonomy.recommender.connector.model.UserWrapper;
import org.bibsonomy.recommender.connector.testutil.DummyMainItemAccess;
import org.bibsonomy.recommender.connector.testutil.RecommenderTestContext;
import org.junit.BeforeClass;
import org.junit.Test;

import recommender.core.database.DBLogic;
import recommender.core.interfaces.database.RecommenderMainItemAccess;
import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.impl.database.DBLogConfigItemAccess;
import recommender.impl.item.collaborative.CollaborativeItemRecommender;
import recommender.impl.model.RecommendedItem;

public class CollaborativeItemRecommenderTest {

	private static DBLogic<ItemRecommendationEntity, RecommendedItem> dbLogic;
	private static RecommenderMainItemAccess dbAccess;
	
	private static final int RECOMMENDATIONS_TO_CALCULATE = 4;

	@BeforeClass
	public static void setUp() {
		dbLogic = RecommenderTestContext.getBeanFactory().getBean("bibtexRecommenderLogic", DBLogConfigItemAccess.class);
		dbAccess = new DummyMainItemAccess();
	}
	
	@Test
	public void testCollaborativeIemRecommender() {
		
		CollaborativeItemRecommender reco = new CollaborativeItemRecommender();
		reco.setDbAccess(dbAccess);
		reco.setDbLogic(dbLogic);
		reco.setNumberOfItemsToRecommend(RECOMMENDATIONS_TO_CALCULATE);
		
		User u = new User("foo");
		
		SortedSet<RecommendedItem> recommendations = reco.getRecommendation(new UserWrapper(u));
		
		Assert.assertEquals(recommendations.size(), RECOMMENDATIONS_TO_CALCULATE);
		
	}
	
}
