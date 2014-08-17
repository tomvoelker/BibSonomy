package org.bibsonomy.recommender.item.testutil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.bibsonomy.recommender.item.service.RecommenderMainItemAccess;

import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.RecommendationItem;
import recommender.impl.test.item.ContentBasedItemRecommenderTest;
import recommender.impl.test.testutil.DummyFactory;

public class DummyMainItemAccess implements RecommenderMainItemAccess {

	public static String[] CF_DUMMY_USERNAMES = {"cfUserA", "cfUserB"};
	public static String[][] CF_DUMMY_USER_ITEMS = {{"recommender systems", "collaborative filtering"},
													{"evaluation trees", "grass green"}};
	
	@Override
	public List<RecommendationItem> getMostActualItems(int count,
			ItemRecommendationEntity entity) {
		
		return getItemsForUser(count, "foo");
	}

	@Override
	public Collection<RecommendationItem> getItemsForContentBasedFiltering(int maxItemsToEvaluate, final ItemRecommendationEntity entity) {
		final List<RecommendationItem> items = new ArrayList<RecommendationItem>();
		for(int i = 0; i < CF_DUMMY_USERNAMES.length; i++) {
			for(int j = 0; j < CF_DUMMY_USER_ITEMS[i].length; j++) {
				items.add(DummyFactory.getInstanceOfRecommendationItemWithTitle(CF_DUMMY_USER_ITEMS[i][j]));
			}
		}
		return items;
	}
	
	@Override
	public List<RecommendationItem> getItemsForUser(int count, String username) {
		for(int i = 0; i < CF_DUMMY_USERNAMES.length; i++) {
			if(username.equals(CF_DUMMY_USERNAMES[i])) {
				final List<RecommendationItem> items = new ArrayList<RecommendationItem>();
				for(int j = 0; j < CF_DUMMY_USER_ITEMS[i].length; j++) {
					items.add(DummyFactory.getInstanceOfRecommendationItemWithTitle(CF_DUMMY_USER_ITEMS[i][j]));
				}
				return items;
			}
		}
		if(username.equals(ContentBasedItemRecommenderTest.DUMMY_CF_USER_NAME)) {
			final List<RecommendationItem> items = new ArrayList<RecommendationItem>();
			for(int j = 0; j < ContentBasedItemRecommenderTest.TEST_USER_ITEMS.length; j++) {
				items.add(DummyFactory.getInstanceOfRecommendationItemWithTitle(ContentBasedItemRecommenderTest.TEST_USER_ITEMS[j]));
			}
			return items;
		}
 		return DummyFactory.getInstancesOfRandomRecommendationItems(count);
	}

	@Override
	public List<RecommendationItem> getTaggedItems(final int maxItemsToEvaluate, final Set<String> tags) {
		// do nothing
		return null;
	}
}
