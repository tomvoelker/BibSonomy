package org.bibsonomy.recommender.tags.multiplexer.strategy;

import java.sql.SQLException;
import java.util.Collection;
import java.util.SortedSet;

import org.apache.log4j.Logger;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.recommender.tags.multiplexer.MultiplexingTagRecommender;
import org.bibsonomy.recommender.tags.database.DBAccess;

/**
 * @author fei
 * @version $Id$
 */
public class SelectAll implements RecommendationSelector {
	private static final Logger log = Logger.getLogger(SelectAll.class);
	private String info = "Strategy for selecting all recommended Tags.";
	
	/**
	 * Selection strategy which simply selects each recommended tag
	 */
	@Override
	public void selectResult(Long qid, Collection<RecommendedTag> recommendedTags) throws SQLException {
		log.debug("Selecting result.");
		DBAccess.getRecommendations(qid, recommendedTags);
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


}
