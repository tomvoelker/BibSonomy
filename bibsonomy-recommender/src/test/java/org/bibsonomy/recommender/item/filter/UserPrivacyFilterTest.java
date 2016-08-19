/**
 * BibSonomy Recommendation - Tag and resource recommender.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.recommender.item.filter;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.recommender.item.model.RecommendationUser;
import org.bibsonomy.recommender.item.testutil.DummyCollaborativeMainAccess;
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
		filter.setDbAccess(new DummyCollaborativeMainAccess<Bookmark>() {
			/* (non-Javadoc)
			 * @see org.bibsonomy.recommender.item.testutil.DummyMainItemAccess#createResource()
			 */
			@Override
			protected Bookmark createResource() {
				return new Bookmark();
			}
		});
		
		final RecommendationUser filteredEntity = filter.filterEntity(entity);
		Long parsed = Long.valueOf(0L);
		try {
			parsed = Long.valueOf(filteredEntity.getUserName());
		} catch (NumberFormatException e) {
			Assert.fail("Id was not a valid Long value!");
		}
		
		Assert.assertEquals(filteredEntity.getUserName(), parsed.toString());
	}
}
