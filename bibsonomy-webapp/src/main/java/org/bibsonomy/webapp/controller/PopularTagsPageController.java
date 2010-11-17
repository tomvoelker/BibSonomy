package org.bibsonomy.webapp.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.logic.LogicInterface;
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
public class PopularTagsPageController implements MinimalisticController<TagCloudCommand> {
	private static final Log log = LogFactory.getLog(PopularTagsPageController.class);

	private LogicInterface logic;
	
	@Override
	public View workOn(TagCloudCommand command) {
		log.debug(this.getClass().getSimpleName());
		
		/* set title */
		command.setPageTitle("Tags"); // TODO: i18n
		
		/* fill command with tags */
		command.setTags(this.logic.getTags(Resource.class, GroupingEntity.ALL, null, null, null, null, Order.POPULAR, 0, 100, null, null));
		
		return Views.POPULAR_TAGS;
	}

	@Override
	public TagCloudCommand instantiateCommand() {
		return new TagCloudCommand();
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
}
