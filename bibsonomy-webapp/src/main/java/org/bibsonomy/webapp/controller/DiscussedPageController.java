package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.DiscussedViewCommand;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for pages /discussed, /discussed/user/USERNAME and /discussed/group/GROUPNAME
 * 
 * @author Sven Stefani
 * @version $Id$
 */
public class DiscussedPageController extends SingleResourceListControllerWithTags implements MinimalisticController<DiscussedViewCommand> {
	private static final Log LOGGER = LogFactory.getLog(DiscussedPageController.class);
	
	@Override
	public View workOn(final DiscussedViewCommand command) {
		LOGGER.debug(this.getClass().getSimpleName());
		final String format = command.getFormat();
		this.startTiming(this.getClass(), format);

		final String groupingName; 
		final GroupingEntity groupingEntity;
		
		if (present(command.getRequestedUser())) {
			// show posts discussed by the requested User
			groupingEntity = GroupingEntity.USER;
			groupingName = command.getRequestedUser();
		} else if (present(command.getRequestedGroup())){
			groupingEntity = GroupingEntity.GROUP;
			groupingName = command.getRequestedGroup();
		} else {
			// show posts discussed by anyone
			groupingEntity = GroupingEntity.ALL;
			groupingName = null;
		}
		
	

//		/*
//		 * if user is logged in, check if the logged in user follows the requested user
//		 */
//		final RequestWrapperContext context = command.getContext();
//		if (context.isUserLoggedIn()) {
//			final List<User> followersOfUser = this.logic.getUsers(null, GroupingEntity.FOLLOWER, null, null, null, null, UserRelation.FOLLOWER_OF, null, 0, 0);
//			for (final User u : followersOfUser){
//				if (u.getName().equals(groupingName)) {
//					command.setFollowerOfUser(true);
//					break;
//				}
//			}
//		}
		
		
		// if filter is set to POSTS_WITH_DISCUSSIONS_UNCLASSIFIED_USER alle posts of both users, positive classified users and not classified users, will be retrieved.
		// add to url: ?filter==POSTS_WITH_DISCUSSIONS_UNCLASSIFIED_USER
		final FilterEntity filter = present(command.getFilter()) ? command.getFilter() : FilterEntity.POSTS_WITH_DISCUSSIONS; 
		
		// retrieve and set the requested resource lists, along with total counts
		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(format, command.getResourcetype())) {
			final ListCommand<?> listCommand = command.getListCommand(resourceType);
			final int entriesPerPage = listCommand.getEntriesPerPage();
			
			this.setList(command, resourceType, groupingEntity, groupingName, null, null, null, filter, null, command.getStartDate(), command.getEndDate(), entriesPerPage);
			this.postProcessAndSortList(command, resourceType);

			/*
			 * set the post counts
			 */
			this.setTotalCount(command, resourceType, groupingEntity, groupingName, null, null, null, filter, null, null, command.getStartDate(), command.getEndDate(), entriesPerPage);
		}

		// get discussion statistics
		command.setDiscussionsStatistic(this.logic.getPostStatistics(Resource.class, groupingEntity, groupingName, null, null, null, FilterEntity.POSTS_WITH_DISCUSSIONS, null, null, command.getStartDate(), command.getEndDate(), 0, 0));
		
		
		// html format - retrieve tags and return HTML view
		if ("html".equals(format)) {
			// set page title
//			command.setPageTitle("user :: " + groupingName); // TODO: i18n
			

			/*
			 * For logged in users we check, if he is in a friends or group relation
			 * with the requested user. 
//			 */
//			final String loginUserName = context.getLoginUser().getName();
//			if (context.isUserLoggedIn()) {
//				/*
//				 * Put the user into command to be able to show some details.
//				 * 
//				 * The DBLogic checks, if the login user may see the user's 
//				 * details. 
//				 */
//				final User requestedUser = logic.getUserDetails(groupingName);
//				command.setUser(requestedUser);
//				/*
//				 * Has loginUser this user set as friend?
//				 */
//				command.setOfFriendUser(logic.getUserRelationship(loginUserName, UserRelation.OF_FRIEND, NetworkRelationSystemTag.BibSonomyFriendSystemTag).contains(requestedUser));
//				command.setFriendOfUser(logic.getUserRelationship(loginUserName, UserRelation.FRIEND_OF, NetworkRelationSystemTag.BibSonomyFriendSystemTag).contains(requestedUser));
//				/*
//				 * TODO: we need an adminLogic to access the requested user's groups ...
//				 */
//			}
			
			this.endTiming();

	
			return Views.DISCUSSEDPAGE;
		}
		
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(format);
	}

	@Override
	public DiscussedViewCommand instantiateCommand() {
		return new DiscussedViewCommand();
	}
}
