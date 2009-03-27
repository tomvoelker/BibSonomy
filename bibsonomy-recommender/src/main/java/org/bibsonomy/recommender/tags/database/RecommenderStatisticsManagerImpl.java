package org.bibsonomy.recommender.tags.database;

import java.sql.SQLException;
import java.util.Random;

import org.apache.log4j.Logger;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * Implements a service for logging recommendation statistics.
 * 
 * @author fei
 * @version $Id$
 */
public class RecommenderStatisticsManagerImpl implements org.bibsonomy.recommender.tags.database.RecommenderStatisticsManager {
	private static final Logger log = Logger.getLogger(RecommenderStatisticsManagerImpl.class);
	/** indicates that post identifier was not given */
	public static int UNKNOWN_POSTID = -1;
	private static final Random rand = new Random();
	
	@Override
	public void connectPostWithRecommendation(Post<? extends Resource> post, int postID) {
		try {
			DBAccess.connectWithPost(post, postID);
		} catch (SQLException ex) {
			log.error("Could connect post with recommendations.", ex);
		}
	}

	@Override
	public int getUnknownPID() {
		return  UNKNOWN_POSTID;
	}

	@Override
	public int getNewPID() {
		return rand.nextInt(Integer.MAX_VALUE);
	}
	
	

}
