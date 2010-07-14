package org.bibsonomy.recommender.tags.multiplexer.strategy;

import java.sql.SQLException;
import java.util.Collection;

import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.recommender.tags.multiplexer.RecommendedTagResultManager;

/**
 * @author fei
 * @version $Id$
 */
public interface RecommendationSelector {
	
	/**
	 * Selects recommendations for given query
	 * 
	 * @param qid
	 * @param resultCache 
	 * @param recommendedTags 
	 * @throws SQLException
	 */
	public void selectResult(Long qid, RecommendedTagResultManager resultCache, Collection<RecommendedTag> recommendedTags) throws SQLException;
	
	/**
	 * selector specific meta informations
	 * @param info 
	 */
	public void setInfo(String info);
	
	/**
	 * @return selector specific meta informations
	 */
	public String getInfo();
	
	/**
	 * short text describing this strategy
	 * @param meta 
	 */
	public void setMeta(byte[] meta);
	
	/**
	 * @return short text describing this strategy
	 */
	public byte[] getMeta();
}
