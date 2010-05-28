package org.bibsonomy.webapp.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.FriendsResourceViewCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * 
 * Shows the /friends overview page, i.d. all posts, set to "viewable for friends" 
 * by users which have loginUser as friend. 
 * 
 * @author Steffen Kress
 * @version $Id$
 */
public class FriendsPageController extends SingleResourceListController implements MinimalisticController<FriendsResourceViewCommand> {
	private static final Log log = LogFactory.getLog(FriendsPageController.class);

	@Override
	public View workOn(final FriendsResourceViewCommand command) {
		log.debug(this.getClass().getSimpleName());
		final String format = command.getFormat();
		this.startTiming(this.getClass(), format);

		// we need to be logged in
		if (!command.getContext().isUserLoggedIn()) {
			throw new MalformedURLSchemeException("error.friends_page_not_logged_in"); // TODO: redirect to login?!
		}
		
		// set grouping entity
		final GroupingEntity groupingEntity = GroupingEntity.FRIEND;

		// determine which lists to initalize depending on the output format
		// and the requested resourcetype
		this.chooseListsToInitialize(format, command.getResourcetype());

		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : listsToInitialise) {
			this.setList(command, resourceType, groupingEntity, null, null, null, null, null, null, command.getListCommand(resourceType).getEntriesPerPage());
			this.postProcessAndSortList(command, resourceType);
		}

		// set page title
		command.setPageTitle("friends");
		// html format - retrieve tags and return HTML view
		if ("html".equals(format)) {
			command.setUserFriends(logic.getUserFriends(command.getContext().getLoginUser()));
			command.setFriendsOfUser(logic.getFriendsOfUser(command.getContext().getLoginUser()));
			// log if a user has reached threshold

			this.endTiming();
			return Views.FRIENDSPAGE;
		}
		
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(format);
	}

	@Override
	public FriendsResourceViewCommand instantiateCommand() {
		return new FriendsResourceViewCommand();
	}
}
