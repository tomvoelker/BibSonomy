package org.bibsonomy.webapp.controller;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.FriendsResourceViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * 
 * Shows the /friends overview page, i.d. all posts, set to "viewable for friends" 
 * by users which have loginUser as friend. 
 * 
 * @author Steffen Kress
 * @version $Id: FriendsPageController.java,v 1.1 2009-02-07 05:16:38 steffen
 *          Exp $
 */
public class FriendsPageController extends SingleResourceListController implements MinimalisticController<FriendsResourceViewCommand> {
	private static final Logger LOGGER = Logger.getLogger(FriendsPageController.class);

	public View workOn(final FriendsResourceViewCommand command) {
		LOGGER.debug(this.getClass().getSimpleName());
		final String format = command.getFormat();
		this.startTiming(this.getClass(), format);

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
		if (format.equals("html")) {
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

	public FriendsResourceViewCommand instantiateCommand() {
		return new FriendsResourceViewCommand();
	}
}
