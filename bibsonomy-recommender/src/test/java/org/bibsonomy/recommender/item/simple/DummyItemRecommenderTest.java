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
package org.bibsonomy.recommender.item.simple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.SortedSet;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.recommender.item.model.RecommendationUser;
import org.bibsonomy.recommender.item.model.RecommendedPost;
import org.bibsonomy.recommender.item.service.RecommenderMainItemAccess;
import org.bibsonomy.recommender.item.testutil.DummyMainItemAccess;
import org.junit.Test;


public class DummyItemRecommenderTest {
	private static final int RECOMMENDATIONS_TO_CALCULATE = 4;
	
	@Test
	public void testDummyItemRecommender() {
		final RecommenderMainItemAccess<Bookmark> dbAccess = new DummyMainItemAccess<Bookmark>() {
			/* (non-Javadoc)
			 * @see org.bibsonomy.recommender.item.testutil.DummyMainItemAccess#createResource()
			 */
			@Override
			protected Bookmark createResource() {
				return new Bookmark();
			}
			
		};
		
		final DummyItemRecommender<Bookmark> rec = new DummyItemRecommender<Bookmark>();
		rec.setDbAccess(dbAccess);
		
		RecommendationUser entity = new RecommendationUser();
		entity.setUserName("abc");
		SortedSet<RecommendedPost<Bookmark>> recommendations = rec.getRecommendation(entity);
		assertEquals(rec.getNumberOfItemsToRecommend(), recommendations.size());
		
		
		rec.setNumberOfItemsToRecommend(RECOMMENDATIONS_TO_CALCULATE);
		recommendations = rec.getRecommendation(entity);
		
		// check the result attributes not to be null
		for (RecommendedPost<Bookmark> item : recommendations) {
			assertNotNull(item.getRecommendationId());
			assertNotNull(item.getTitle());
		}
		
		// check for correct count of recommendations
		assertEquals(RECOMMENDATIONS_TO_CALCULATE, recommendations.size());
	}
}
