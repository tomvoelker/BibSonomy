package org.bibsonomy.recommender.multiplexer.evaluation;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.DBAccess;

/**
 * Implements a service for logging recommendation statistics.
 * 
 * @author fei
 * @version $Id$
 */
public class RecommenderStatisticsManagerImpl implements org.bibsonomy.recommender.RecommenderStatisticsManager {
	private static final Logger log = Logger.getLogger(RecommenderStatisticsManagerImpl.class);
	/** indicates that post identifier was not given */
	public static int UNKNOWN_POSTID = -1;
	
	@Override
	public void connectPostWithRecommendation(Post<? extends Resource> post, int postID) {
		try {
			DBAccess.connectWithPost(post, postID);
		} catch (SQLException ex) {
			log.error("Could connect post with recommendations.", ex);
		}
	}

	@Override
	public Integer getUnknownPID() {
		return  UNKNOWN_POSTID;
	}

}
