package org.bibsonomy.webapp.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.UserResourceViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * Controller for the InboxPage (shows all posts in your inbox)
 * @author sdo
 * @version $Id$
 */
public class InboxPageController extends SingleResourceListController implements MinimalisticController<UserResourceViewCommand> {
	private static final Log log = LogFactory.getLog(PopularPageController.class);
	private Errors errors;

	
	public View workOn(final UserResourceViewCommand command) {
		/*
		 * FIXME: implement filter=no parameter
		 */
		log.debug(this.getClass().getSimpleName());
		// user has to be logged in
		if (!command.getContext().isUserLoggedIn()){
			log.info("Trying to access inbox without being logged in");
			return new ExtendedRedirectView("/login");
		}
		this.startTiming(this.getClass(), command.getFormat());
		
		// determine which lists to initialize depending on the output format 
		// and the requested resource type
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());		
		String loginUserName = command.getContext().getLoginUser().getName();
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : listsToInitialise) {
			// disable manual setting of start value for homepage
			command.getListCommand(resourceType).setStart(0);
			setList(command, resourceType, GroupingEntity.INBOX, loginUserName, null, null, null, null, null, 20);
			postProcessAndSortList(command, resourceType);
			for (Object p : command.getListCommand(resourceType).getList()){
				if (p instanceof Post<?>){
					Post<?> postItem = (Post<?>)p;
					postItem.setInboxPost(true);
				}
			}
		}
												
		// html format - retrieve tags and return HTML view
		if (command.getFormat().equals("html")) {
			command.setPageTitle("inbox");
			//setTags(command, Resource.class, GroupingEntity.USER, command.getRequestedUser(), null, null, null, null, 0, Integer.MAX_VALUE, null);
			this.endTiming();
			return Views.INBOX;		
		}
		
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(command.getFormat());	
	}
		
	public UserResourceViewCommand instantiateCommand() {
		return new UserResourceViewCommand();
	}

}
