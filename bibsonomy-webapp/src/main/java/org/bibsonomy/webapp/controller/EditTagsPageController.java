package org.bibsonomy.webapp.controller;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.EditTagsPageViewCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;


/**
 * Controller for edit tags
 * 
 * @author Henrik Bartholmai
 */

public class EditTagsPageController extends SingleResourceListControllerWithTags implements MinimalisticController<EditTagsPageViewCommand> {
	private static final Logger LOGGER = Logger.getLogger(AuthorPageController.class);

	public View workOn(EditTagsPageViewCommand command) {
		LOGGER.debug(this.getClass().getSimpleName());

		this.startTiming(this.getClass(), command.getFormat());
		
		// no user given -> error
		if (command.getContext().isUserLoggedIn() == false) {
			LOGGER.error("Invalid query /user without username");
			throw new MalformedURLSchemeException("error.user_page_without_username");
		}

		// set grouping entity, grouping name, tags
		final GroupingEntity groupingEntity = GroupingEntity.USER;
		final String groupingName = command.getContext().getLoginUser().getName();

		command.setPageTitle("edit tags :: " + groupingName);
		
		if (command.getFormat().equals("html")) {
			this.setTags(command, Resource.class, groupingEntity, groupingName, null, null, null, null, 0, 20000, null);

			// log if a user has reached threshold
			if (command.getTagcloud().getTags().size() > 19999) {
				LOGGER.error("User " + groupingName + " has reached threshold of 20000 tags on user page");
			}

		}

		this.endTiming();
		// export - return the appropriate view
		return Views.EDIT_TAGS;
	}

	public EditTagsPageViewCommand instantiateCommand() {
		return new EditTagsPageViewCommand();
	}
}