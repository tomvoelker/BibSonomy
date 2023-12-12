/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.GoldStandardReferenceParam;
import org.bibsonomy.database.params.LoggingParam;
import org.bibsonomy.database.plugin.AbstractDatabasePlugin;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.services.information.InformationService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * handles updates that are necessary when the interhash changes when a community publication is updated
 * e.g. relations and person resource relations
 *
 * or when the publication is deleted
 *
 * @author dzo
 */
public class GoldStandardPublicationPlugin extends AbstractDatabasePlugin {

	private InformationService service;

	@Override
	public void onGoldStandardDelete(final String interhash, User loggedinUser, final DBSession session) {
		// delete all references of the post
		final GoldStandardReferenceParam param = new GoldStandardReferenceParam();
		param.setHash(interhash);
		param.setRefHash(interhash);
		param.setUsername(loggedinUser.getName());

		// delete the references, but before log it (delete and log for both directions)
		this.insert("logDeletedRelationsGoldStandardPublication", param, session);
		this.insert("logDeletedGoldStandardPublicationRelations", param, session);
		this.delete("deleteRelationsGoldStandardPublication", param, session);
		this.delete("deleteGoldStandardPublicationRelations", param, session);

		// delete the person relations, but before log it
		this.insert("logDeletedPersonRelationsByInterhash", param, session);
		this.delete("deletePersonRelationsByInterhash", param, session);
	}

	@Override
	public void onGoldStandardUpdate(final int oldContentId, final int newContentId, final String newInterhash, final String interhash, final DBSession session) {
		// update all references of the post
		final LoggingParam param = new LoggingParam();
		param.setNewHash(newInterhash);
		param.setOldHash(interhash);

		/*
		 * move the relations
		 */
		this.update("updateGoldStandardPublicationRelations", param, session);
		this.update("updateRelationsGoldStandardPublication", param, session);

		/*
		 * move the person resource relations
		 */
		this.update("updatePersonRelationsByInterhash", param, session);

		/*
		 * move discussion with the gold standard
		 */
		this.update("updateDiscussion", param, session);
		this.update("updateReviewRatingCache", param, session);
	}

	@Override
	public void onGoldStandardUpdated(final String interhash, User loggedinUser, final DBSession session) {
		BibTexParam param = new BibTexParam();
		param.setHash(interhash);
		param.setContentType(ConstantID.BIBTEX_CONTENT_TYPE);
		// query for the post and their relationsonGoldStandardUpdate
		Post<? extends Resource> post = (Post<? extends Resource>) this.queryForObject("getGoldStandardByHash", param, session);
		List<ResourcePersonRelation> relations = this.queryForList("getResourcePersonRelationsWithPersonsByInterhash", interhash, ResourcePersonRelation.class, session);
		if (present(relations)) {
			String loggedinUsername = loggedinUser.getName();
			// Create a set of usernames, that have already been mailed
			Set<String> usersMailed = new HashSet<>();
			// inform all the related users, if they aren't the user updating the publication
			for (ResourcePersonRelation relation : relations) {
				String relationUsername = relation.getPerson().getUser();
				if (present(relationUsername) && !usersMailed.contains(relationUsername) && !loggedinUsername.equals(relationUsername)) {
					usersMailed.add(relationUsername);
					service.updatedPost(relationUsername, post);
				}
			}
		}
	}

	/**
	 * Set service to inform of changes in gold standard publications.
	 * For example e-mailing related users.
	 *
	 * @param service the information service to set
	 */
	public void setService(InformationService service) {
		this.service = service;
	}
}