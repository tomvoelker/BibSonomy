package org.bibsonomy.recommender.tags.multiplexer;

import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.RecommendedTag;

/**
 * speeding up the recommender multiplexer by caching recommendations
 *  
 * @author fei
 * @version $Id$
 */
public class RecommendedTagResultManager {
	private static final Log log = LogFactory.getLog(RecommendedTagResultManager.class);
	
	/** 
	 * we store a list of recommendations for each recommender 
	 * mapping recommender ids to corresponding result sets 
	 */
	ConcurrentHashMap<
		Long,
		ConcurrentHashMap<Long, SortedSet<RecommendedTag>>
		> resultStore;
	
	/** 
	 * we cache only those tags, which are received before timeout -
	 * when a given query timed out, it is flaged by false
	 */
	ConcurrentHashMap<Long, Boolean> monitorFlag;
	
	/**
	 * Constructor
	 */
	public RecommendedTagResultManager() {
		this.resultStore = new ConcurrentHashMap<Long, ConcurrentHashMap<Long,SortedSet<RecommendedTag>>>();
		this.monitorFlag = new ConcurrentHashMap<Long, Boolean>();
	}
	
	
	/**
	 * Indicate that given query just started. Results will be added,
	 * until stopQuery(qid) is called
	 * @param qid
	 */
	public void startQuery(Long qid) {
		if( resultStore.containsKey(qid) )
			log.error("Query reinitialized");
		else {
			resultStore.put(qid,new ConcurrentHashMap<Long, SortedSet<RecommendedTag>>());
			monitorFlag.put(qid, true);
		}
	}
	

	/** 
	 * stop caching results for given query - all further added results to this query
	 * are discarded
	 * @param qid
	 */
	public void stopQuery(Long qid) {
		if(qid == null || !resultStore.containsKey(qid) )
			log.error("Tried to stop non-existant query " + qid);
		else {
			monitorFlag.put(qid, false);
		}
	}
	
	/** 
	 * remove all informations concerning given query
	 * @param qid
	 */
	public void releaseQuery(Long qid) {
		if( !resultStore.containsKey(qid) )
			log.error("Tried to remove non-existant query");
		else {
			resultStore.remove(qid);
			monitorFlag.remove(qid);
		}
	}
	
	/**
	 * tests whether given query is still monitored
	 */
	private boolean isActive(Long qid) {
		
		boolean flag1 = resultStore.containsKey(qid);
		boolean flag2 = monitorFlag.containsKey(qid)&&monitorFlag.get(qid)==true;
		return flag1&&flag2;
	}

	/**
	 * tests whether given query is still cached
	 */
	private boolean isCached(Long qid) {
		return resultStore.containsKey(qid);
	}

	
	/**
	 * cache result for given query - if this query is still active
	 * ONLY NON-EMPTY RESULTS ARE STORED
	 * @param qid
	 */
	public void addResult(Long qid, Long sid, SortedSet<RecommendedTag> result) {
		if( isActive(qid) ) {
			ConcurrentHashMap<Long, SortedSet<RecommendedTag>> queryStore = resultStore.get(qid);
			if( (queryStore!=null) && (result != null && result.size()>0) )
				queryStore.put(sid, result);
		}

	}
	
	/**
	 * Returns all results cached for given query. If the query is not cached, null is 
	 * returned
	 * 
	 * @param qid 
	 * @return
	 */
	public Collection<SortedSet<RecommendedTag>> getResultForQuery(Long qid) {
		if( isCached(qid) ) {
			ConcurrentHashMap<Long, SortedSet<RecommendedTag>> queryStore = resultStore.get(qid);
			if( queryStore!=null )
				return queryStore.values();
		}
		return null;
	}
	
	/**
	 * get all recommended tags from given recommender in given query, if exists
	 * otherwise null is returned
	 * 
	 * @param qid query id
	 * @param sid recommender's setting id
	 * @return
	 */
	public SortedSet<RecommendedTag> getResults(Long qid, Long sid) {
		if( isCached(qid) ) {
			return resultStore.get(qid).get(sid);
		} else
			return null;
	}
	
	/**
	 * Returns ids of those recommenders which delivered tag for given query - if the query
	 * is cached, otherwise null.
	 * 
	 * @param qid
	 * @return
	 */
	public Set<Long> getActiveRecommender(Long qid) {
		if( isCached(qid) ) {
			ConcurrentHashMap<Long, SortedSet<RecommendedTag>> queryStore = resultStore.get(qid);
			if( queryStore!=null )
				return queryStore.keySet();
		}
		return null;
	}
	
	public int getNrOfCachedQueries() {
		return resultStore.size();
	}
}
