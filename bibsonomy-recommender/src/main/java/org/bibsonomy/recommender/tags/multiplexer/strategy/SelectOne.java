package org.bibsonomy.recommender.tags.multiplexer.strategy;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.recommender.tags.multiplexer.RecommendedTagResultManager;

/**
 * This selection strategy selects exactly one recommender.
 *  
 * @author fei
 * @version $Id$
 */
public class SelectOne extends SimpleSelector {
	private static final Log log = LogFactory.getLog(SelectOne.class);

	/**
	 * Selection strategy which selects recommender (uniform) randomly.
	 * If selected recommender didn't deliver recommendations - a fallback
	 * recommender is chosen. 
	 */
	@Override
	public void selectResult(final Long qid, final RecommendedTagResultManager resultCache, final Collection<RecommendedTag> recommendedTags) throws SQLException {
		log.debug("Selecting result.");
		
		// get list of recommenders which delivered tags in given query
		final List<Long> listActive = dbLogic.getActiveRecommenderIDs(qid);
		// get list of all recommenders for given query
		final List<Long> listAll    = dbLogic.getAllRecommenderIDs(qid);
		
		
		// if no recommendation available, append nothing
		if (listAll.size() == 0 || listActive.size() == 0) {
			log.debug("No results available!");
			return;
		}
		
		// select recommender
		Long sid = listAll.get((int) Math.floor((Math.random() * listAll.size())));
		// store selection in database
		dbLogic.addSelectedRecommender(qid, sid);
		log.debug("Selected setting " + sid + " out of "+listActive.size()+"/"+listAll.size());
		
		// check if selected recommender delivered tags
		boolean isActive = false;
		for (final Iterator<Long> i = listActive.iterator(); i.hasNext(); ) {
			final Long next = i.next();
			if( next.equals(sid) ) 
				isActive = true;
		}
		// if not, select a fall back recommender
		if( !isActive ) {
			sid = listActive.get((int) Math.floor((Math.random() * listActive.size())));
			log.debug("Selected setting not active, fall back is " + sid);
		}
		
		// finally get recommended tags
		dbLogic.getRecommendations(qid, sid, recommendedTags);
	}	

	@Override
	public String getInfo() {
		return "Strategy for selecting one recommender.";
	}
}
