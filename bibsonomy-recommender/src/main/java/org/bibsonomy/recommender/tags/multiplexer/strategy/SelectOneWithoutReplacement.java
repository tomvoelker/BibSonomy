package org.bibsonomy.recommender.tags.multiplexer.strategy;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.recommender.tags.multiplexer.RecommendedTagResultManager;

/**
 * This selection strategy selects exactly one recommender.
 *  
 * @author fei
 * @version $Id$
 */
public class SelectOneWithoutReplacement extends SimpleSelector {
	private static final Log log = LogFactory.getLog(SelectOneWithoutReplacement.class);
	
	/**
	 * Selection strategy which selects recommender (uniform) randomly.
	 * If selected recommender didn't deliver recommendations - a fallback
	 * recommender is chosen. 
	 */
	@Override
	public void selectResult(final Long qid, final RecommendedTagResultManager resultCache, final Collection<RecommendedTag> recommendedTags) throws SQLException {
		log.debug("(" + qid + ")Selecting result.");
		
		// get list of recommenders which delivered tags in given query
		// TODO: use the set interface
		final List<Long> listActive = new ArrayList<Long>(resultCache.getActiveRecommender(qid));
		
		// final List<Long> listActive = dbLogic.getActiveRecommenderIDs(qid);
		// log.debug("Result cache check for query "+qid+" : "+ listActive.size() +" / " + resultCache.getActiveRecommender(qid).size());

		log.debug("(" + qid + ")Selecting result #1");
		// get list of all recommenders for this post process with corresponding number of 
		// queries where they were selected
		final List<Pair<Long,Long>> selectionCount = dbLogic.getRecommenderSelectionCount(qid);
		
		/*
		 * create list of all recommenders from which the next one shall be drawn
		 */
		final Vector<Long> listAll = new Vector<Long>();
		
		// id of last recommender
		long last = -1;
		if (!selectionCount.isEmpty()) {
			last = selectionCount.get(0).getSecond();
		}
			
		// collect those recommenders which were selected least 
		log.debug("(" + qid + ")Selecting result #2");
		while (!selectionCount.isEmpty() && (selectionCount.get(0).getSecond() == last)) {
			listAll.add(selectionCount.get(0).getFirst());
			selectionCount.remove(0);
		}
		log.debug("(" + qid + ")Selecting result #3");

		// if no recommendation available, append nothing
		if (listAll.size() == 0 || listActive.size() == 0) {
			log.debug("(" + qid + ")No results available!");
			return;
		}
		
		// select recommender
		Long sid = listAll.get((int) Math.floor((Math.random() * listAll.size())));
		
		// store selection in database
		dbLogic.addSelectedRecommender(qid, sid);
		log.debug("(" + qid + ")Selected setting " + sid + " out of " + listActive.size() + "/" + listAll.size());
		
		// check if selected recommender delivered tags
		boolean isActive = false;
		for (final Long i : listAll) {
			if (i.equals(sid)) { 
				isActive = true;
			}
		}
		// if not, select a fall back recommender
		if (!isActive) {
			sid = listActive.get((int) Math.floor((Math.random()*listActive.size())));
			log.debug("(" + qid + ")Selected setting not active, fall back is " + sid);
		}
		
		// finally get recommended tags
		final SortedSet<RecommendedTag> cachedResult = resultCache.getResults(qid,sid);
		if (cachedResult != null) {
			recommendedTags.addAll(cachedResult);
		} else {
			// this shouldn't happen!
			log.error("(" + qid + ")Selected result not cached -> fetching it from database");
			dbLogic.getRecommendations(qid, sid, recommendedTags);
		}
	}	

	@Override
	public String getInfo() {
		return "Strategy for selecting one recommender.";
	}
}
