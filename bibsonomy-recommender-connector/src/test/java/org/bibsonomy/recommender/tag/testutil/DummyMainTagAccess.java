/**
 * BibSonomy-Recommendation-Connector - Connector for the recommender framework for tag and resource recommendation
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
package org.bibsonomy.recommender.tag.testutil;

import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.tag.service.RecommenderMainTagAccess;

import recommender.core.model.Pair;

/**
 * @author lha
 */
public class DummyMainTagAccess implements RecommenderMainTagAccess {

	@Override
	public List<Pair<String, Integer>> getMostPopularTagsForUser(
			String username, int range) {
		// do nothing
		return null;
	}

	@Override
	public List<Pair<String, Integer>> getMostPopularTagsForRecommendationEntity(Post<? extends Resource> entity, String hash, int range) {
		// do nothing
		return null;
	}

	@Override
	public Integer getNumberOfTagsForUser(String username) {
		// do nothing
		return null;
	}
	
	@Override
	public int getNumberOfTagAssignmentsForRecommendationEntity(Post<? extends Resource> entity, String hash) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberOfTaggingsForUser(String username) {
		// do nothing
		return 0;
	}
	
	@Override
	public Integer getUserIDByName(String userName) {
		// do nothing
		return null;
	}

	@Override
	public String getUserNameByID(int userID) {
		// do nothing
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tag.service.RecommenderMainTagAccess#getTagsOfPreviousPostsForUser(java.lang.Class, java.lang.String, int)
	 */
	@Override
	public List<Pair<String, Integer>> getTagsOfPreviousPostsForUser(String username, int numberOfPreviousPosts) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tag.service.RecommenderMainTagAccess#getNumberOfTagsOfPreviousPostsForUser(java.lang.String, int)
	 */
	@Override
	public int getNumberOfTagsOfPreviousPostsForUser(String username, int numberOfPreviousPosts) {
		// TODO Auto-generated method stub
		return 0;
	}
}
