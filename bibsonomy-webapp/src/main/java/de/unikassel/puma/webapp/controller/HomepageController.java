package de.unikassel.puma.webapp.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.SimpleResourceViewCommand;
import org.bibsonomy.webapp.controller.SingleResourceListController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for Homepage * 
 *
 * @author Dominik Benz
 * @version $Id$
 */
public class HomepageController extends SingleResourceListController implements MinimalisticController<SimpleResourceViewCommand> {
	private static final Log log = LogFactory.getLog(HomepageController.class);

	public View workOn(final SimpleResourceViewCommand command) {
		/*
		 * FIXME: implement filter=no parameter
		 */
		log.debug(this.getClass().getSimpleName());
		log.info("PUMA:" + this.getClass().getSimpleName());
		this.startTiming(this.getClass(), command.getFormat());

		
		// handle the case when only tags are requested
		this.handleTagsOnly(command, GroupingEntity.ALL, null, null, null, null, null, 0, 50, null);
		
		// determine which lists to initialize depending on the output format 
		// and the requested resource type
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());		
		
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : listsToInitialise) {
			// disable manual setting of start value for home page
			command.getListCommand(resourceType).setStart(0);
			setList(command, resourceType, GroupingEntity.ALL, null, null, null, null, null, null, 5);
			postProcessAndSortList(command, resourceType);
		}
												
		// html format - retrieve tags and return HTML view
		if (command.getFormat().equals("html")) {
			command.setPageTitle("home");
			command.setApplicationName("puma");
			setTags(command, Resource.class, GroupingEntity.ALL, null, null, null, null, null, 0, 50, null);
			this.endTiming();
			return Views.PUMAHOMEPAGE;		
		}
		
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(command.getFormat());	
	}

	public SimpleResourceViewCommand instantiateCommand() {
		return new SimpleResourceViewCommand();
	}
}
