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

	@Override
	public View workOn(final UserResourceViewCommand command) {
		/*
		 * FIXME: implement filter=no parameter
		 */

		// user has to be logged in
		if (!command.getContext().isUserLoggedIn()){
			errors.reject("error.general.login");
			return Views.ERROR; // TODO: redirect to login page
		}
		
		final String format = command.getFormat();
		this.startTiming(this.getClass(), format);

		// determine which lists to initialize depending on the output format 
		// and the requested resource type
		this.chooseListsToInitialize(format, command.getResourcetype());		
		final String loginUserName = command.getContext().getLoginUser().getName();
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : listsToInitialise) {
			final int entriesPerPage = command.getListCommand(resourceType).getEntriesPerPage();
			this.setList(command, resourceType, GroupingEntity.INBOX, loginUserName, null, null, null, null, null, entriesPerPage);
			postProcessAndSortList(command, resourceType);
			/*
			 * mark all posts to be inbox posts (such that the "remove" link appears 
			 */
			for (final Post<? extends Resource> post: command.getListCommand(resourceType).getList()){
				post.setInboxPost(true);
			}
			// the number of items in this user's inbox has already been fetched
			this.setTotalCount(command, resourceType, GroupingEntity.INBOX, loginUserName, null, null, null, null, null, entriesPerPage, null);
		}
		this.endTiming();

		// html format - retrieve tags and return HTML view
		if ("html".equals(format)) {
			command.setPageTitle("inbox");
			return Views.INBOX;		
		}

		// export - return the appropriate view
		return Views.getViewByFormat(format);	
	}

	@Override
	public UserResourceViewCommand instantiateCommand() {
		return new UserResourceViewCommand();
	}

	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}

}
