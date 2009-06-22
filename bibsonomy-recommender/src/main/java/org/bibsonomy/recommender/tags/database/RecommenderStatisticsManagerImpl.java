package org.bibsonomy.recommender.tags.database;

import java.util.Random;

/**
 * Implements a service for logging recommendation statistics.
 * 
 * @author fei
 * @version $Id$
 */
public class RecommenderStatisticsManagerImpl implements org.bibsonomy.recommender.tags.database.RecommenderStatisticsManager {
	/** indicates that post identifier was not given */
	public static int UNKNOWN_POSTID = -1;
	private static final Random rand = new Random();
	
	@Override
	public int getUnknownPID() {
		return  UNKNOWN_POSTID;
	}

	@Override
	public int getNewPID() {
		return rand.nextInt(Integer.MAX_VALUE);
	}

}
