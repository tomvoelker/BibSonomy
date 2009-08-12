package org.bibsonomy.webapp.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.webapp.command.UserRelationCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Christian Voigtmann
 * @version $Id$
 */
public class UserRelationsController extends SingleResourceListControllerWithTags implements MinimalisticController<UserRelationCommand> {
	private static final Log LOGGER = LogFactory.getLog(AuthorPageController.class);

	public View workOn(UserRelationCommand command) {
		LOGGER.debug(this.getClass().getSimpleName());

		this.startTiming(this.getClass(), command.getFormat());
		
		// no user given -> error
		if (command.getRequestedUser() == null) {
			/*
			 * FIXME: wrong error message, should be /relations/ without user
			 */
			LOGGER.error("Invalid query /user without username");
			throw new MalformedURLSchemeException("error.user_page_without_username");
		}

		// set grouping entity, grouping name, tags
		final GroupingEntity groupingEntity = GroupingEntity.USER;
		final String groupingName = command.getRequestedUser();

		//query for the number of relations of a user
		int numberOfRelations = this.logic.getTagStatistics(null, groupingEntity, groupingName, null, null, ConceptStatus.ALL, 0, Integer.MAX_VALUE);

		int limit = command.getConcepts().getEntriesPerPage();
		int offset = command.getConcepts().getStart();
		
		// retrieving concepts
		List<Tag> concepts = this.logic.getConcepts(null, groupingEntity, groupingName, null, null, ConceptStatus.ALL, offset, limit + offset);

		command.getConcepts().setConceptList(concepts);


		command.getConcepts().setNumConcepts(concepts.size());
		command.getConcepts().setTotalCount(numberOfRelations);

		// set page title
		// TODO: internationalize
		command.setPageTitle("relations :: " + groupingName);
		
		if (command.getFormat().equals("html")) {
			this.setTags(command, Resource.class, groupingEntity, groupingName, null, null, null, null, 0, 20000, null);

			// log if a user has reached threshold
			if (command.getTagcloud().getTags().size() > 19999) {
				LOGGER.error("User " + groupingName + " has reached threshold of 20000 tags on user page");
			}

			/*
			 * add user details to command, if loginUser is admin
			 */
			if (Role.ADMIN.equals(logic.getAuthenticatedUser().getRole())) {
				command.setUser(logic.getUserDetails(command.getRequestedUser()));
			}
		}

		this.endTiming();
		// export - return the appropriate view
		return Views.USERRELATED;
	}

	public UserRelationCommand instantiateCommand() {
		// TODO Auto-generated method stub
		return new UserRelationCommand();
	}
}