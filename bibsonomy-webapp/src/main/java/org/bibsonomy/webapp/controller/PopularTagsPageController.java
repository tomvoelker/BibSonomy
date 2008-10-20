package org.bibsonomy.webapp.controller;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.webapp.command.TagCloudCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for popular tags page /tags
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class PopularTagsPageController extends SingleResourceListController implements MinimalisticController<TagCloudCommand> {
	private static final Logger LOGGER = Logger.getLogger(PopularTagsPageController.class);

	public View workOn(TagCloudCommand command) {
		LOGGER.debug(this.getClass().getSimpleName());
		
		/* set title */
		command.setPageTitle("Tags");
		
		/* fill command with tags */
		command.setTags(this.logic.getTags(Resource.class, GroupingEntity.ALL, null, null, null, null, Order.POPULAR, 0, 100, null, null));
		
		return Views.POPULAR_TAGS;
	}

	public TagCloudCommand instantiateCommand() {
		return new TagCloudCommand();
	}
}
