package org.bibsonomy.webapp.controller;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.PageCommand;
import org.bibsonomy.webapp.command.ResourceViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for Homepage * 
 *
 * @version: $Id$
 * @author:  dbenz
 *
 */
public class HomepageController extends MultiResourceListController implements MinimalisticController<ResourceViewCommand> {
	private static final Logger LOGGER = Logger.getLogger(HomepageController.class);

	public View workOn(ResourceViewCommand command) {
		LOGGER.debug(this.getClass().getSimpleName());

		// retrieve and setthe requested resource lists
		for (final Class<? extends Resource> resourceType : listsToInitialise) {
			setList(command, resourceType, GroupingEntity.ALL, null, userSettings.getListItemcount(), 100);
			postProcessList(command, resourceType);
		}
		
		// retrieve and set tags
		setTags(command, Resource.class, GroupingEntity.ALL, null, null, null, 0, 100);
						
		return Views.HOMEPAGE;
	}

	public ResourceViewCommand instantiateCommand() {
		return new ResourceViewCommand();
	}
}