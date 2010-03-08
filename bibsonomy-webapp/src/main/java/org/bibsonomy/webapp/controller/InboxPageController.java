package org.bibsonomy.webapp.controller;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.UserResourceViewCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * Controller for the InboxPage (shows all posts in your inbox)
 * 
 * @author sdo
 * @version $Id$
 */
public class InboxPageController extends SingleResourceListController implements MinimalisticController<UserResourceViewCommand>, ErrorAware {
	private Errors errors;

	public View workOn(final UserResourceViewCommand command) {
		/*
		 * FIXME: implement filter=no parameter
		 */

		// user has to be logged in
		if (!command.getContext().isUserLoggedIn()){
			errors.reject("error.general.login");
			return Views.ERROR;
		}
		this.startTiming(this.getClass(), command.getFormat());

		// determine which lists to initialize depending on the output format 
		// and the requested resource type
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());		
		final String loginUserName = command.getContext().getLoginUser().getName();
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : listsToInitialise) {
			// disable manual setting of start value for homepage
			command.getListCommand(resourceType).setStart(0);
			setList(command, resourceType, GroupingEntity.INBOX, loginUserName, null, null, null, null, null, 20);
			postProcessAndSortList(command, resourceType);

			/*
			 * mark all posts to be inbox posts (such that the "remove" link appears 
			 */
			for (final Post<? extends Resource> post: command.getListCommand(resourceType).getList()){
				post.setInboxPost(true);
			}
		}
		this.endTiming();

		// html format - retrieve tags and return HTML view
		if ("html".equals(command.getFormat())) {
			command.setPageTitle("inbox");
			return Views.INBOX;		
		}

		// export - return the appropriate view
		return Views.getViewByFormat(command.getFormat());	
	}

	public UserResourceViewCommand instantiateCommand() {
		return new UserResourceViewCommand();
	}

	public Errors getErrors() {
		return this.errors;
	}

	public void setErrors(Errors errors) {
		this.errors = errors;
	}

}
