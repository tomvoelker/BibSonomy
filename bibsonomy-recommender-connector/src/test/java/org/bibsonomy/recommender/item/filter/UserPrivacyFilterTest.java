package org.bibsonomy.recommender.item.filter;

import org.bibsonomy.model.User;
import org.bibsonomy.recommender.connector.model.UserWrapper;
import org.bibsonomy.recommender.connector.testutil.DummyMainItemAccess;
import org.junit.Assert;
import org.junit.Test;

import recommender.core.interfaces.model.ItemRecommendationEntity;


/**
 * 
 * @author lha
 */
public class UserPrivacyFilterTest {
	
	/**
	 * tests whether usernames are mapped to valid Long ids
	 */
	@Test
	public void testUserPrivacyFilter() {
		final User user = new User("testuser");
		final ItemRecommendationEntity entity = new UserWrapper(user);
		
		final UserPrivacyFilter filter = new UserPrivacyFilter();
		filter.setDbAccess(new DummyMainItemAccess());
		
		final ItemRecommendationEntity filteredEntity = filter.filterEntity(entity);
		Long parsed = 0L;
		try {
			parsed = Long.parseLong(filteredEntity.getRecommendationId());
		} catch (NumberFormatException e) {
			Assert.fail("Id was not a valid Long value!");
		}
		
		Assert.assertEquals(filteredEntity.getRecommendationId(), parsed.toString());
	}
}
