package org.bibsonomy.recommender.testutil;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

import org.apache.log4j.Logger;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.recommender.tags.multiplexer.MultiplexingTagRecommender;
import org.bibsonomy.recommender.tags.multiplexer.strategy.RecommendationSelector;
import org.bibsonomy.recommender.tags.database.DBAccess;
import org.bibsonomy.recommender.tags.database.DBLogic;

/**
 * @author fei
 * @version $Id$
 */
public class SelectCounter implements RecommendationSelector {
	private static final Logger log = Logger.getLogger(SelectCounter.class);
	private String info = "Strategy for selecting all recommended Tags.";
	
	private DBLogic dbLogic;
	
	private int recoCounter;
	
	/**
	 * Selection strategy which simply selects each recommended tag
	 */
	public void selectResult(Long qid, Collection<RecommendedTag> recommendedTags) throws SQLException {
		log.debug("Selecting result.");
		dbLogic.getRecommendations(qid, recommendedTags);
		setRecoCounter(dbLogic.getActiveRecommenderIDs(qid).size());
	}

	public String getInfo() {
		// TODO Auto-generated method stub
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

	public DBLogic getDbLogic() {
		return this.dbLogic;
	}

	public void setDbLogic(DBLogic dbLogic) {
		this.dbLogic = dbLogic;
	}

	public void setRecoCounter(int recoCounter) {
		this.recoCounter = recoCounter;
	}

	public int getRecoCounter() {
		return recoCounter;
	}


}
