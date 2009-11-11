package org.bibsonomy.webapp.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.SimpleResourceViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for the InboxPage (shows all posts in your inbox)
 * @author sdo
 * @version $Id$
 */
public class InboxPageController extends SingleResourceListController implements MinimalisticController<SimpleResourceViewCommand> {
	private static final Log log = LogFactory.getLog(PopularPageController.class);

	
	public View workOn(final SimpleResourceViewCommand command) {
		/*
		 * FIXME: implement filter=no parameter
		 */
		log.debug(this.getClass().getSimpleName());
		this.startTiming(this.getClass(), command.getFormat());
		
		// determine which lists to initalize depending on the output format 
		// and the requested resourcetype
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());		
		String loginUserName = command.getContext().getLoginUser().getName();
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : listsToInitialise) {
			// disable manual setting of start value for homepage
			command.getListCommand(resourceType).setStart(0);
			setList(command, resourceType, GroupingEntity.INBOX, loginUserName, null, null, null, null, null, 20);
			postProcessAndSortList(command, resourceType);
		}
												
		// html format - retrieve tags and return HTML view
		if (command.getFormat().equals("html")) {
			command.setPageTitle("inbox");
			setTags(command, Resource.class, GroupingEntity.ALL, null, null, null, null, null, 0, 50, null);
			this.endTiming();
			return Views.INBOX;		
		}
		
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(command.getFormat());	
	}
		
	public SimpleResourceViewCommand instantiateCommand() {
		return new SimpleResourceViewCommand();
	}

}
