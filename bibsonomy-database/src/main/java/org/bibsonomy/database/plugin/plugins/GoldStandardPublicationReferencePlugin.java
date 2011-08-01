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
	public Runnable onGoldStandardDelete(final String interhash, final DBSession session) {
		// delete all references of the post
		return new Runnable() {
			
			@Override
			public void run() {
				final GoldStandardReferenceParam param = new GoldStandardReferenceParam();
				param.setHash(interhash);
				param.setRefHash(interhash);
				
				delete("deleteReferencesGoldStandardPublication", param, session);				
				delete("deleteGoldStandardPublicationReferences", param, session);
			}
		};
	}
	
	@Override
	public Runnable onGoldStandardUpdate(final String newInterhash, final String interhash, final DBSession session) {
		// update all references of the post
		return new Runnable() {
			@Override
			public void run() {
				final LoggingParam<String> param = new LoggingParam<String>();
				param.setNewId(newInterhash);
				param.setOldId(interhash);
				
				update("updateGoldStandardPublicationReference", param, session);
				update("updateReferenceGoldStandardPublication", param, session);
				
				/*
				 * move discussion with the gold standard
				 */
				update("updateDiscussion", param, session);
				update("updateReviewRatingCache", param, session);
			}
		};
	}
}