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
