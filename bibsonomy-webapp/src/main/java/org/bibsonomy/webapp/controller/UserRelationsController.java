/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.webapp.command.UserRelationCommand;
import org.bibsonomy.webapp.config.Parameters;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * controller to display the relations of requested user
 * - /concepts/USER
 * 
 * @author Christian Voigtmann
 */
public class UserRelationsController extends SingleResourceListControllerWithTags implements MinimalisticController<UserRelationCommand> {
	private static final Log LOGGER = LogFactory.getLog(AuthorPageController.class);

	@Override
	public View workOn(final UserRelationCommand command) {
		this.startTiming(command.getFormat());
		
		// no user given -> error
		if (!present(command.getRequestedUser())) {
			/*
			 * FIXME: wrong error message, should be /relations/ without user
			 */
			throw new MalformedURLSchemeException("error.user_page_without_username");
		}

		// set grouping entity, grouping name, tags
		final GroupingEntity groupingEntity = GroupingEntity.USER;
		final String groupingName = command.getRequestedUser();

		//query for the number of relations of a user
		final int numberOfRelations = this.logic.getTagStatistics(null, groupingEntity, groupingName, null, null, ConceptStatus.ALL, null, null, null, 0, Integer.MAX_VALUE);

		// retrieving concepts
		final List<Tag> concepts = this.logic.getConcepts(null, groupingEntity, groupingName, null, null, ConceptStatus.ALL, 0, Integer.MAX_VALUE);

		command.getConcepts().setConceptList(concepts);
		command.getConcepts().setTotalCount(numberOfRelations);

		// set page title
		// TODO: internationalize
		command.setPageTitle("relations :: " + groupingName);
		
		if ("html".equals(command.getFormat())) {
			this.setTags(command, Resource.class, groupingEntity, groupingName, null, null, null, null, 20000, null);

			// log if a user has reached threshold
			if (command.getTagcloud().getTags().size() >= Parameters.TAG_THRESHOLD) {
				LOGGER.error("User " + groupingName + " has reached threshold of " + Parameters.TAG_THRESHOLD + " tags on user page");
			}
		}

		this.endTiming();
		// export - return the appropriate view
		return Views.USERRELATED;
	}

	@Override
	public UserRelationCommand instantiateCommand() {
		return new UserRelationCommand();
	}
}