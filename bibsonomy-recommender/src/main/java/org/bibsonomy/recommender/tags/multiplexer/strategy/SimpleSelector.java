package org.bibsonomy.recommender.tags.multiplexer.strategy;

import org.bibsonomy.recommender.tags.database.DBLogic;

/**
 * @author dzo
 * @version $Id$
 */
public abstract class SimpleSelector implements RecommendationSelector {
	
	protected DBLogic dbLogic;
	
	@Override
	public void setInfo(final String info) {
		// noop
	}

	@Override
	public byte[] getMeta() {
		return null;
	}

	@Override
	public void setMeta(final byte[] meta) {
		// noop
	}

	/**
	 * @param dbLogic the dbLogic to set
	 */
	public void setDbLogic(final DBLogic dbLogic) {
		this.dbLogic = dbLogic;
	}
}
