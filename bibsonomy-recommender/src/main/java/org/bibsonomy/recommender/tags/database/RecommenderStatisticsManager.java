package org.bibsonomy.recommender.tags.database;


/**
 * Interface for gathering usage statistics for (tag) recommendations.
 * 
 * @author fei
 * @version $Id$
 */
public interface RecommenderStatisticsManager {

	/**
	 * Get id which indicates that a recommendation query was not associated with a post.
	 * @return UNKNOWN_POSTID
	 */
	public int getUnknownPID();
	
	/**
	 * @return A new post ID.
	 */
	public int getNewPID();
}
