package org.bibsonomy.webapp.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.AuthorResourceCommand;
import org.bibsonomy.webapp.command.RelatedTagCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author daill
 * @version $Id$
 */
public class AuthorPageController extends MultiResourceListController implements MinimalisticController<AuthorResourceCommand>{
	private static final Logger LOGGER = Logger.getLogger(AuthorPageController.class);

	public View workOn(AuthorResourceCommand command) {
		LOGGER.debug(this.getClass().getSimpleName());
		this.startTiming(this.getClass(), command.getFormat());
		
		// if no group given return 
		if (command.getRequestedAuthor() == null) return null;
				
		// set grouping entity and grouping name
		final GroupingEntity groupingEntity = GroupingEntity.VIEWABLE;
		final String authorName = command.getRequestedAuthor();
		final List<String> requTags = command.getRequestedTagsList();
		
		// determine which lists to initalize depending on the output format 
		// and the requested resourcetype
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());
		
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : listsToInitialise) {
			this.setList(command, resourceType, groupingEntity, null, requTags, null, null, authorName, command.getListCommand(resourceType).getEntriesPerPage());
			this.postProcessAndSortList(command, resourceType);
		}		
		
		// html format - retrieve tags and return HTML view
		if ("html".equals(command.getFormat())) {
			this.setTags(command, BibTex.class, groupingEntity, null, null, null, null, 0, 1000, authorName);
			this.endTiming();
			return Views.AUTHORPAGE;			
		}
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(command.getFormat());		
	}
	
	public AuthorResourceCommand instantiateCommand() {
		return new AuthorResourceCommand();
	}
}
