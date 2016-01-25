/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.database.plugin.plugins;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.GoldStandardReferenceParam;
import org.bibsonomy.database.params.LoggingParam;
import org.bibsonomy.database.plugin.AbstractDatabasePlugin;

/**
 * @author dzo
 */
public class GoldStandardPublicationReferencePlugin extends AbstractDatabasePlugin {
	
	@Override
	public void onGoldStandardDelete(final String interhash, final DBSession session) {
		// delete all references of the post
		final GoldStandardReferenceParam param = new GoldStandardReferenceParam();
		param.setHash(interhash);
		param.setRefHash(interhash);
		
		this.delete("deleteRelationsGoldStandardPublication", param, session);
		this.delete("deleteGoldStandardPublicationRelations", param, session);
	}
	
	@Override
	public void onGoldStandardUpdate(final int oldContentId, final int newContentId, final String newInterhash, final String interhash, final DBSession session) {
		// update all references of the post
		final LoggingParam<String> param = new LoggingParam<String>();
		param.setNewId(newInterhash);
		param.setOldId(interhash);
		
		this.update("updateGoldStandardPublicationRelations", param, session);
		this.update("updateRelationsGoldStandardPublication", param, session);
		
		/*
		 * move discussion with the gold standard
		 */
		this.update("updateDiscussion", param, session);
		this.update("updateReviewRatingCache", param, session);
	}
}