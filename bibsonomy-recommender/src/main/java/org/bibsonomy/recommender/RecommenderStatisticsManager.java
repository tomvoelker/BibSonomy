package org.bibsonomy.recommender;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * Interface for gathering usage statistics for (tag) recommendations.
 * 
 * @author fei
 * @version $Id$
 */
public interface RecommenderStatisticsManager {

	/**
	 * Connect postID with recommendation.
	 *    For each post process an unique id is generated. This is used for mapping 
	 *    posts to recommendations and vice verca.
	 *      
	 * @param post the post as stored in bibsonomy
	 * @param post's random id as generated when the post process started
	 */
	public void connectPostWithRecommendation(Post<? extends Resource> post, int postID);
	
	/**
	 * Get id which indicates that a recommendation query was not associated with a post.
	 * @return UNKNOWN_POSTID
	 */
	public Integer getUnknownPID();
}
