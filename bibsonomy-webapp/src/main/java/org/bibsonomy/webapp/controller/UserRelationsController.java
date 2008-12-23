package org.bibsonomy.webapp.controller;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.webapp.command.BaseCommand;
import org.bibsonomy.webapp.command.ContextCommand;
import org.bibsonomy.webapp.command.UserRelationCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Christian Voigtmann
 * @version $Id$
 */
public class UserRelationsController extends SingleResourceListControllerWithTags implements MinimalisticController<UserRelationCommand> {
	private static final Logger LOGGER = Logger.getLogger(AuthorPageController.class);

	public View workOn(UserRelationCommand command) {
		LOGGER.debug(this.getClass().getSimpleName());

		this.startTiming(this.getClass(), command.getFormat());
		
		// no user given -> error
		if (command.getRequestedUser() == null) {
			LOGGER.error("Invalid query /user without username");
			throw new MalformedURLSchemeException("error.user_page_without_username");
		}

		// set grouping entity, grouping name, tags
		final GroupingEntity groupingEntity = GroupingEntity.USER;
		final String groupingName = command.getRequestedUser();

		// retrieve concepts
		List<Tag> concepts = this.logic.getConcepts(null, groupingEntity, groupingName, null, null, ConceptStatus.PICKED, 0, Integer.MAX_VALUE);

		int entriesPerPage = command.getConcepts().getEntriesPerPage();

		System.out.println(command.getConcepts().getEntriesPerPage());

		int start = command.getConcepts().getStart();

		if (entriesPerPage > concepts.size() - start) {
			entriesPerPage = concepts.size() - start;
		}

		List<Tag> conceptsToShow = new LinkedList<Tag>();

		/*
		 * calculation of the relations which should by displayed on the page.
		 * This manually calculation has to be replaced by an database query.
		 * This db query doesn't exist and has to be implement
		 */
		for (int i = start; i < entriesPerPage + start; i++) {
			conceptsToShow.add(concepts.get(i));
		}

		command.getConcepts().setConceptList(conceptsToShow);

		command.getConcepts().setNumConcepts(conceptsToShow.size());
		command.getConcepts().setTotalCount(concepts.size());

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

			this.endTiming();
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