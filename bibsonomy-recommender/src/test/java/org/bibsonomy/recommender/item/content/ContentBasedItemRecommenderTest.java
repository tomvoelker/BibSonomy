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
package org.bibsonomy.recommender.item.content;

import static org.junit.Assert.assertEquals;

import java.util.SortedSet;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.recommender.item.model.RecommendationUser;
import org.bibsonomy.recommender.item.model.RecommendedPost;
import org.bibsonomy.recommender.item.service.RecommenderMainItemAccess;
import org.bibsonomy.recommender.item.testutil.DummyMainItemAccess;
import org.junit.Test;

/**
 * This test checks the integrity of the {@link ContentBasedItemRecommender}.
 * It tests the calculation of similarity and the handling of already known resources. 
 * 
 * @author lukas
 */
public class ContentBasedItemRecommenderTest {

	private static final int RECOMMENDATIONS_TO_CALCULATE = 4;
	public static final String DUMMY_CF_USER_NAME = "testcfitem";
	public static final String[] TEST_USER_ITEMS = {"evaluation test", "recommender systems"};
	
	@Test
	public void testContentBasedItemRecommender() {
		final RecommenderMainItemAccess<Bookmark> dbAccess = new DummyMainItemAccess<Bookmark>() {
			/* (non-Javadoc)
			 * @see org.bibsonomy.recommender.item.testutil.DummyMainItemAccess#createResource()
			 */
			@Override
			protected Bookmark createResource() {
				return new Bookmark();
			}
		};
		ContentBasedItemRecommender<Bookmark> reco = new ContentBasedItemRecommender<Bookmark>();
		reco.setDbAccess(dbAccess);
		reco.setNumberOfItemsToRecommend(RECOMMENDATIONS_TO_CALCULATE);
		reco.setMaxItemsToEvaluate(4);
		final RecommendationUser user = new RecommendationUser();
		user.setUserName(DUMMY_CF_USER_NAME);
		SortedSet<RecommendedPost<Bookmark>> recommendations = reco.getRecommendation(user);
		
		// should be one less, because already known items are not recommended
		assertEquals(RECOMMENDATIONS_TO_CALCULATE, recommendations.size());
		
		// the first element should be 'evaluation trees', because it has one same token
		// as 'evaluation test'
		assertEquals(DummyMainItemAccess.CF_DUMMY_USER_ITEMS[1][0], recommendations.first().getTitle());
	}
}
