package org.bibsonomy.recommender.connector.item.multiplexer;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import junit.framework.Assert;

import org.bibsonomy.model.User;
import org.bibsonomy.recommender.connector.filter.UserPrivacyFilter;
import org.bibsonomy.recommender.connector.model.UserWrapper;
import org.bibsonomy.recommender.connector.testutil.DummyMainItemAccess;
import org.bibsonomy.recommender.connector.testutil.RecommenderTestContext;
import org.junit.BeforeClass;
import org.junit.Test;

import recommender.core.Recommender;
import recommender.core.database.DBLogic;
import recommender.core.interfaces.database.RecommenderMainItemAccess;
import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.impl.database.DBLogConfigItemAccess;
import recommender.impl.item.simple.DummyItemRecommender;
import recommender.impl.model.RecommendedItem;
import recommender.impl.multiplexer.MultiplexingRecommender;
import recommender.impl.multiplexer.strategy.SelectOneWithoutReplacement;

public class ItemMultiplexerTest {

	private static DBLogic<ItemRecommendationEntity, RecommendedItem> dbLogic;
	private static RecommenderMainItemAccess dbAccess;
	
	private static final int RECOMMENDATIONS_TO_CALCULATE = 4;

	@BeforeClass
	public static void setUp() {
		dbLogic = RecommenderTestContext.getBeanFactory().getBean("bibtexRecommenderLogic", DBLogConfigItemAccess.class);
		dbAccess = new DummyMainItemAccess();
	}
	
	/**
	 * tests the item multiplexer for recommending bibtex resources 
	 */
	@Test
	public void testItemMUXBibTex() {
		
		MultiplexingRecommender<ItemRecommendationEntity, RecommendedItem> mux = new MultiplexingRecommender<ItemRecommendationEntity, RecommendedItem>();
		mux.setDbLogic(dbLogic);
		
		List<Recommender<ItemRecommendationEntity, RecommendedItem>> locals = new ArrayList<Recommender<ItemRecommendationEntity, RecommendedItem>>();
		DummyItemRecommender itemRec1 = new DummyItemRecommender();
		itemRec1.setDbAccess(dbAccess);
		itemRec1.setDbLogic(dbLogic);
		DummyItemRecommender itemRec2 = new DummyItemRecommender();
		itemRec2.setDbAccess(dbAccess);
		itemRec2.setDbLogic(dbLogic);
		
		// query at least two recommender
		locals.add(itemRec1);
		locals.add(itemRec2);
		
		UserPrivacyFilter filter = new UserPrivacyFilter();
		filter.setDbAccess(new DummyMainItemAccess());
		
		mux.setPrivacyFilter(filter);
		mux.setLocalRecommenders(locals);
		
		// dummy user
		User lha = new User();
		lha.setName("lha");
		ItemRecommendationEntity entity = new UserWrapper(lha);
		
		SelectOneWithoutReplacement<ItemRecommendationEntity, RecommendedItem> selectionStrategy = new SelectOneWithoutReplacement<ItemRecommendationEntity, RecommendedItem>();
		selectionStrategy.setDbLogic(dbLogic);
		mux.setResultSelector(selectionStrategy);
		
		mux.setNumberOfResultsToRecommend(RECOMMENDATIONS_TO_CALCULATE);
		
		mux.init();
		
		SortedSet<RecommendedItem> result = mux.getRecommendation(entity);
		Assert.assertEquals(RECOMMENDATIONS_TO_CALCULATE, result.size());
		
		for(RecommendedItem item : result) {
			Assert.assertNotNull(item.getId());
		}
		
	}
	
}
