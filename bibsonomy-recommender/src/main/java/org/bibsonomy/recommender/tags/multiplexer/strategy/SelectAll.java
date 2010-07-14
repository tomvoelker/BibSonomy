package org.bibsonomy.recommender.tags.multiplexer.strategy;

import java.sql.SQLException;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.recommender.tags.database.DBLogic;
import org.bibsonomy.recommender.tags.multiplexer.RecommendedTagResultManager;

/**
 * @author fei
 * @version $Id$
 */
public class SelectAll implements RecommendationSelector {
	private static final Log log = LogFactory.getLog(SelectAll.class);
	private String info = "Strategy for selecting all recommended Tags.";
	
	private DBLogic dbLogic;
	
	/**
	 * Selection strategy which simply selects each recommended tag
	 */
	@Override
	public void selectResult(Long qid, RecommendedTagResultManager resultCache, Collection<RecommendedTag> recommendedTags) throws SQLException {
		log.debug("Selecting result.");
		dbLogic.getRecommendations(qid, recommendedTags);
	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return info;
	}

	@Override
	public void setInfo(String info) {
		this.info = info;
	}


	@Override
	public byte[] getMeta() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMeta(byte[] meta) {
		// TODO Auto-generated method stub
		
	}

	public DBLogic getDbLogic() {
		return this.dbLogic;
	}

	public void setDbLogic(DBLogic dbLogic) {
		this.dbLogic = dbLogic;
	}


}
