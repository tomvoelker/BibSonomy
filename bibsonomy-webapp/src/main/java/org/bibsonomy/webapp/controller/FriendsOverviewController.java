package org.bibsonomy.webapp.controller;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.database.systemstags.search.NetworkRelationSystemTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.FriendsOverviewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.security.access.AccessDeniedException;

/**
 * controller for showing all friends of a user and all users that have added the
 * loggedin user as friends
 *  - /friendsoverview
 *
 * @author dzo
 */
public class FriendsOverviewController extends MultiResourceListController implements MinimalisticController<FriendsOverviewCommand> {
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.MinimalisticController#instantiateCommand()
	 */
	@Override
	public FriendsOverviewCommand instantiateCommand() {
		return new FriendsOverviewCommand();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.MinimalisticController#workOn(org.bibsonomy.webapp.command.ContextCommand)
	 */
	@Override
	public View workOn(final FriendsOverviewCommand command) {
		final RequestWrapperContext context = command.getContext();
		if (!context.isUserLoggedIn()) {
			throw new AccessDeniedException("please log in");
		}
		initializeDidYouKnowMessageCommand(command);
		
		final String loggedinUser = context.getLoginUser().getName();
		command.setFriends(this.logic.getUserRelationship(loggedinUser, UserRelation.FRIEND_OF, NetworkRelationSystemTag.BibSonomyFriendSystemTag));
		command.setOfFriends(this.logic.getUserRelationship(loggedinUser, UserRelation.OF_FRIEND, NetworkRelationSystemTag.BibSonomyFriendSystemTag));
		
		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(command.getFormat(), command.getResourcetype())) {
			this.addList(command, resourceType, GroupingEntity.VIEWABLE, "friends", null, null, null, null, null, command.getEntriesPerPage());
		}
		
		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(command.getFormat(), command.getResourcetype())) {
			this.addList(command, resourceType, GroupingEntity.FRIEND, null, null, null, null, null, null, command.getEntriesPerPage());
		}
		
		return Views.FRIEND_OVERVIEW;
	}
}
