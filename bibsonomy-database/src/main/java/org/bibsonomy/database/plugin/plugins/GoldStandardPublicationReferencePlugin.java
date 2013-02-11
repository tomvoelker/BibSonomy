package org.bibsonomy.database.plugin.plugins;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.GoldStandardReferenceParam;
import org.bibsonomy.database.params.LoggingParam;
import org.bibsonomy.database.plugin.AbstractDatabasePlugin;

/**
 * @author dzo
 * @version $Id$
 */
public class GoldStandardPublicationReferencePlugin extends AbstractDatabasePlugin {
	
	@Override
	public void onGoldStandardDelete(final String interhash, final DBSession session) {
		// delete all references of the post
		final GoldStandardReferenceParam param = new GoldStandardReferenceParam();
		param.setHash(interhash);
		param.setRefHash(interhash);
		
		this.delete("deleteReferencesGoldStandardPublication", param, session);				
		this.delete("deleteGoldStandardPublicationReferences", param, session);
	}
	
	@Override
	public void onGoldStandardUpdate(final String newInterhash, final String interhash, final DBSession session) {
		// update all references of the post
		final LoggingParam<String> param = new LoggingParam<String>();
		param.setNewId(newInterhash);
		param.setOldId(interhash);
		
		this.update("updateGoldStandardPublicationReference", param, session);
		this.update("updateReferenceGoldStandardPublication", param, session);
		
		/*
		 * move discussion with the gold standard
		 */
		this.update("updateDiscussion", param, session);
		this.update("updateReviewRatingCache", param, session);
	}
}