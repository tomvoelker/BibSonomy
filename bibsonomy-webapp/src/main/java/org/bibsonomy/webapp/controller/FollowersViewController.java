package org.bibsonomy.webapp.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.webapp.command.FollowersViewCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Christian Kramer
 * @version $Id$
 */
public class FollowersViewController extends SingleResourceListController implements MinimalisticController<FollowersViewCommand>{
	private static final Log log = LogFactory.getLog(FollowersViewController.class);

	public View workOn(FollowersViewCommand command) {
		log.debug(this.getClass().getSimpleName());
		final String format = command.getFormat();
		this.startTiming(this.getClass(), format);

		// you have to be logged in
		if (command.getContext().isUserLoggedIn() == false) {
			throw new MalformedURLSchemeException("error.general.login");
		}
		
//		// determine which lists to initalize depending on the output format
//		// and the requested resourcetype
//		this.chooseListsToInitialize(format, command.getResourcetype());

//		// retrieve and set the requested resource lists
//		for (final Class<? extends Resource> resourceType : listsToInitialise) {
//			this.setList(command, resourceType, groupingEntity, null, null, null, null, null, null, command.getListCommand(resourceType).getEntriesPerPage());
//			this.postProcessAndSortList(command, resourceType);
//		}

		// html format - retrieve tags and return HTML view
		if (format.equals("html")) {
			command.setFollowersOfUser(logic.getUsers(null, GroupingEntity.FOLLOWER, null, null, null, null, UserRelation.FOLLOWER_OF, null, 0, 0));
			command.setUserIsFollowing(logic.getUsers(null, GroupingEntity.FOLLOWER, null, null, null, null, UserRelation.OF_FOLLOWER, null, 0, 0));
			// log if a user has reached threshold

			this.endTiming();
			return Views.FOLLOWERS;
		}
		
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(format);
	}

	public FollowersViewCommand instantiateCommand() {
		return new FollowersViewCommand();
	}
}
