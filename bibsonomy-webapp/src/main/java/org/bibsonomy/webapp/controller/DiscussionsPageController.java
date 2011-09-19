package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.database.systemstags.search.NetworkRelationSystemTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.UserResourceViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for user pages /user/USERNAME
 * 
 * @author Sven Stefani
 * @version $Id$
 */
public class DiscussionsPageController extends SingleResourceListControllerWithTags implements MinimalisticController<UserResourceViewCommand> {
	private static final Log LOGGER = LogFactory.getLog(DiscussionsPageController.class);
	
	@Override
	public View workOn(final UserResourceViewCommand command) {
		LOGGER.debug(this.getClass().getSimpleName());
		final String format = command.getFormat();
		this.startTiming(this.getClass(), format);

		final String groupingName = command.getRequestedUser();
		GroupingEntity groupingEntity = null;
		
		// no user given -> error
		// TODO  ... -> show recently discussed posts
		if (present(groupingName)) {
			//throw new MalformedURLSchemeException("error.user_page_without_username");
			// set grouping entity, grouping name, tags, userSimilarity
			groupingEntity = GroupingEntity.USER;
		} else {
			groupingEntity = GroupingEntity.ALL;
		}
		
		
		
		/*
		 * set filter to get only posts with discussions 
		 */
		final FilterEntity filter = FilterEntity.POSTS_WITH_DISCUSSIONS;
		

		/*
		 * if user is logged in, check if the logged in user follows the requested user
		 */
		final RequestWrapperContext context = command.getContext();
		if (context.isUserLoggedIn()) {
			final List<User> followersOfUser = this.logic.getUsers(null, GroupingEntity.FOLLOWER, null, null, null, null, UserRelation.FOLLOWER_OF, null, 0, 0);
			for (final User u : followersOfUser){
				if (u.getName().equals(groupingName)) {
					command.setFollowerOfUser(true);
					break;
				}
			}
		}
		
		
		// retrieve and set the requested resource lists, along with total counts
		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(format, command.getResourcetype())) {
			final ListCommand<?> listCommand = command.getListCommand(resourceType);
			final int entriesPerPage = listCommand.getEntriesPerPage();
			
			this.setList(command, resourceType, groupingEntity, groupingName, null, null, null, filter, null, entriesPerPage);
			this.postProcessAndSortList(command, resourceType);

			/*
			 * set the post counts
			 */
			this.setTotalCount(command, resourceType, groupingEntity, groupingName, null, null, null, filter, null, entriesPerPage, null);
		}

		// html format - retrieve tags and return HTML view
		if ("html".equals(format)) {
			// set page title
			command.setPageTitle("user :: " + groupingName); // TODO: i18n
			

			/*
			 * For logged in users we check, if he is in a friends or group relation
			 * with the requested user. 
			 */
			final String loginUserName = context.getLoginUser().getName();
			if (context.isUserLoggedIn()) {
				/*
				 * Put the user into command to be able to show some details.
				 * 
				 * The DBLogic checks, if the login user may see the user's 
				 * details. 
				 */
				final User requestedUser = logic.getUserDetails(groupingName);
				command.setUser(requestedUser);
				/*
				 * Has loginUser this user set as friend?
				 */
				command.setOfFriendUser(logic.getUserRelationship(loginUserName, UserRelation.OF_FRIEND, NetworkRelationSystemTag.BibSonomyFriendSystemTag).contains(requestedUser));
				command.setFriendOfUser(logic.getUserRelationship(loginUserName, UserRelation.FRIEND_OF, NetworkRelationSystemTag.BibSonomyFriendSystemTag).contains(requestedUser));
				/*
				 * TODO: we need an adminLogic to access the requested user's groups ...
				 */
			}
			
			this.endTiming();

	
			return Views.DISCUSSIONSPAGE;
		}
		
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(format);
	}

	@Override
	public UserResourceViewCommand instantiateCommand() {
		return new UserResourceViewCommand();
	}
}
