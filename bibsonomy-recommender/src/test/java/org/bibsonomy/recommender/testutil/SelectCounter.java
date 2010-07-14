package org.bibsonomy.recommender.testutil;

import java.sql.SQLException;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.recommender.tags.database.DBLogic;
import org.bibsonomy.recommender.tags.multiplexer.RecommendedTagResultManager;
import org.bibsonomy.recommender.tags.multiplexer.strategy.RecommendationSelector;

/**
 * @author fei
 * @version $Id$
 */
public class SelectCounter implements RecommendationSelector {
	private static final Log log = LogFactory.getLog(SelectCounter.class);


	private String info = "Strategy for selecting all recommended Tags.";
	private DBLogic dbLogic;
	private int recoCounter;

	/**
	 * Selection strategy which simply selects each recommended tag
	 */
	@Override
	public void selectResult(final Long qid, final RecommendedTagResultManager resultCache, final Collection<RecommendedTag> recommendedTags) throws SQLException {
		log.debug("Selecting result.");
		dbLogic.getRecommendations(qid, recommendedTags);
		setRecoCounter(dbLogic.getActiveRecommenderIDs(qid).size());
	}

	@Override
	public String getInfo() {
		return info;
	}

	@Override
	public void setInfo(final String info) {
		this.info = info;
	}

	@Override
	public byte[] getMeta() {
		return null;
	}

	@Override
	public void setMeta(final byte[] meta) {
	}

	/**
	 * @return the dbLogic
	 */
	public DBLogic getDbLogic() {
		return this.dbLogic;
	}

	/**
	 * @param dbLogic the dbLogic to set
	 */
	public void setDbLogic(final DBLogic dbLogic) {
		this.dbLogic = dbLogic;
	}

	/**
	 * @return the recoCounter
	 */
	public int getRecoCounter() {
		return this.recoCounter;
	}

	/**
	 * @param recoCounter the recoCounter to set
	 */
	public void setRecoCounter(final int recoCounter) {
		this.recoCounter = recoCounter;
	}
}
