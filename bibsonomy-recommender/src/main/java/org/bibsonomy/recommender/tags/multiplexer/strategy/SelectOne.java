package org.bibsonomy.recommender.tags.multiplexer.strategy;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.comparators.RecommendedTagComparator;
import org.bibsonomy.recommender.tags.multiplexer.MultiplexingTagRecommender;
import org.bibsonomy.recommender.tags.database.DBAccess;

/**
 * This selection strategy selects exactly one recommender.
 *  
 * @author fei
 * @version $Id$
 */
public class SelectOne implements RecommendationSelector {
	private static final Logger log = Logger.getLogger(SelectOne.class);
	private String info = "Strategy for selecting one recommender.";


	/**
	 * Selection strategy which selects recommender (uniform) randomly.
	 * If selected recommender didn't deliver recommendations - a fallback
	 * recommender is chosen. 
	 */
	@Override
	public void selectResult(Long qid, Collection<RecommendedTag> recommendedTags) throws SQLException {
		// TODO Auto-generated method stub
		
		log.debug("Selecting result.");
		
		// get list of recommenders which delivered tags in given query
		final List<Long> listActive = DBAccess.getActiveRecommenderIDs(qid);
		// get list of all recommenders for given query
		final List<Long> listAll    = DBAccess.getAllRecommenderIDs(qid);
		
		
		// if no recommendation available, append nothing
		if( listAll.size()==0 || listActive.size()==0 ) 
			return;
		
		// select recommender
		Long sid = listAll.get(
					new Double(Math.floor((Math.random()*listAll.size()))).intValue()
				);
		// store selection in database
		DBAccess.addSelectedRecommender(qid, sid);
		log.debug("Selected setting " + sid + " out of "+listActive.size()+"/"+listAll.size());
		
		// check if selected recommender delivered tags
		boolean isActive = false;
		for(Iterator<Long> i = listActive.iterator(); i.hasNext(); ) {
			Long next = i.next();
			if( next.equals(sid) ) 
				isActive = true;
		};
		// if not, select a fall back recommender
		if( !isActive ) {
			sid = listActive.get(
					new Double(Math.floor((Math.random()*listActive.size()))).intValue()
				);
			log.debug("Selected setting not active, fall back is " + sid);
		};
		
		// finally get recommended tags
		DBAccess.getRecommendations(qid, sid, recommendedTags);
	}	

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}


	public byte[] getMeta() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setMeta(byte[] meta) {
		// TODO Auto-generated method stub
		
	}


}
