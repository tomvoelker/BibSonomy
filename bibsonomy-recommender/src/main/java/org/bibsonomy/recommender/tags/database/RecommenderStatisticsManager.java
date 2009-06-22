package org.bibsonomy.recommender.tags.database;

import java.util.Random;

/**
 * Implements a service for logging recommendation statistics.
 * 
 * @author fei
 * @version $Id$
 */
public class RecommenderStatisticsManager {
	/** indicates that post identifier was not given */
	public static int UNKNOWN_POSTID = -1;
	private static final Random rand = new Random();
	
	/**
	 * Get id which indicates that a recommendation query was not associated with a post.
	 * @return UNKNOWN_POSTID
	 */	
	public static int getUnknownPID() {
		return  UNKNOWN_POSTID;
	}
	
	/**
	 * @return A new post ID.
	 */
	public static int getNewPID() {
		return rand.nextInt(Integer.MAX_VALUE);
	}

}
