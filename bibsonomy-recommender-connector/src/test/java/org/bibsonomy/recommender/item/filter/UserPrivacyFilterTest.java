package org.bibsonomy.recommender.item.filter;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.recommender.item.model.RecommendationUser;
import org.bibsonomy.recommender.item.testutil.DummyMainItemAccess;
import org.junit.Assert;
import org.junit.Test;


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
		final RecommendationUser entity = new RecommendationUser();
		entity.setUserName("testuser");
		
		final UserPrivacyFilter filter = new UserPrivacyFilter();
		filter.setDbAccess(new DummyMainItemAccess<Bookmark>(){
			/* (non-Javadoc)
			 * @see org.bibsonomy.recommender.item.testutil.DummyMainItemAccess#createResource()
			 */
			@Override
			protected Bookmark createResource() {
				return new Bookmark();
			}
		});
		
		final RecommendationUser filteredEntity = filter.filterEntity(entity);
		Long parsed = 0L;
		try {
			parsed = Long.parseLong(filteredEntity.getUserName());
		} catch (NumberFormatException e) {
			Assert.fail("Id was not a valid Long value!");
		}
		
		Assert.assertEquals(filteredEntity.getUserName(), parsed.toString());
	}
}
