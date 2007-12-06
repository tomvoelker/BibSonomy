package org.bibsonomy.webapp.controller;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.PageCommand;
import org.bibsonomy.webapp.command.ResourceViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for user pages 
 * /user/USERNAME
 *
 * @version: $Id$
 * @author:  dbenz
 * $Author$
 *
 */
public class UserPageController extends MultiResourceListController implements MinimalisticController<ResourceViewCommand> {
	private static final Logger LOGGER = Logger.getLogger(UserPageController.class);

	public View workOn(ResourceViewCommand command) {
		LOGGER.debug(this.getClass().getSimpleName());
		final GroupingEntity groupingEntity;
		final String groupingName;
		if (command.getRequestedUser() != null) {
			groupingEntity = GroupingEntity.USER;
			groupingName = command.getRequestedUser();
		} else {
			groupingEntity = GroupingEntity.ALL;
			groupingName = null;
		}
		
		// retrieve and setthe requested resource lists
		for (final Class<? extends Resource> resourceType : listsToInitialise) {
			setList(command, resourceType, groupingEntity, groupingName, userSettings.getListItemcount(), 100);
			postProcessList(command, resourceType);
		}
		
		// retrieve and set tags
		setTags(command, Resource.class, groupingEntity, groupingName, null, null, 0, 1000);
						
		return Views.USERPAGE;
	}

	public ResourceViewCommand instantiateCommand() {
		return new ResourceViewCommand();
	}
}