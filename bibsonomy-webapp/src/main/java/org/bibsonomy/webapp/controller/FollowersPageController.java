package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.util.EnumUtils;
import org.bibsonomy.webapp.command.FollowersViewCommand;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.config.Parameters;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RankingUtil;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Christian Kramer
 * @version $Id$
 */
public class FollowersPageController extends SingleResourceListController implements MinimalisticController<FollowersViewCommand>{
	private static final Log log = LogFactory.getLog(FollowersPageController.class);

	public View workOn(FollowersViewCommand command) {
		log.debug(this.getClass().getSimpleName());
		final String format = command.getFormat();
		this.startTiming(this.getClass(), format);

		// you have to be logged in
		if (command.getContext().isUserLoggedIn() == false) {
			throw new MalformedURLSchemeException("error.general.login");
		}
		
		// set params
		final UserRelation userRelation = EnumUtils.searchEnumByName(UserRelation.values(), command.getUserSimilarity());
		GroupingEntity groupingEntity = GroupingEntity.FOLLOWER;
		String groupingName = null;
		if (present(command.getRequestedUser())) {
			groupingEntity = GroupingEntity.USER;
			groupingName = command.getRequestedUser();
		}
		
		// ranking settings
		Integer start = command.getRanking().getPeriod() * Parameters.NUM_RESOURCES_FOR_PERSONALIZED_RANKING;
		command.getRanking().setPeriodStart(start + 1);
		command.getRanking().setPeriodEnd(start + Parameters.NUM_RESOURCES_FOR_PERSONALIZED_RANKING);		
		
		
		// handle case when only tags are requested
		this.handleTagsOnly(command, groupingEntity, groupingName, null, null, null, Integer.MAX_VALUE, null);
		
		// personalization settings
		final int entriesPerPage = Parameters.NUM_RESOURCES_FOR_PERSONALIZED_RANKING;
		command.setSortPage("ranking");
		command.setSortPageOrder("desc");
		command.setPersonalized(true);
		command.setDuplicates("no");
		
		
		// determine which lists to initalize depending on the output format
		// and the requested resourcetype
		this.chooseListsToInitialize(format, command.getResourcetype());
		
		// fetch all tags of logged-in user
		List<Tag> loginUserTags = this.logic.getTags(Resource.class, GroupingEntity.USER, command.getContext().getLoginUser().getName(), null, null, null, null, 0, Integer.MAX_VALUE, null, null);
		
		// fetch all tags of followed users TODO implement...
		List<Tag> targetUserTags = this.logic.getTags(Resource.class, GroupingEntity.USER, command.getContext().getLoginUser().getName(), null, null, null, null, 0, Integer.MAX_VALUE, null, null);		
		
		// retrieve and set the requested resource lists, along with total
		// counts
		for (final Class<? extends Resource> resourceType : listsToInitialise) {
			final ListCommand<?> listCommand = command.getListCommand(resourceType);
						
			final int origEntriesPerPage = listCommand.getEntriesPerPage();
			final int origStart = listCommand.getStart();
			listCommand.setStart(start);
			this.setList(command, resourceType, groupingEntity, groupingName, null, null, null, null, null, entriesPerPage);
			listCommand.setEntriesPerPage(origEntriesPerPage);
			listCommand.setStart(origStart);
										
			// compute the ranking for each post in the list
			RankingUtil.computeRanking(loginUserTags, targetUserTags, command.getListCommand(resourceType).getList(), command.getRanking().getMethodObj(), command.getRanking().getNormalize());

			// post-process & sort
			this.postProcessAndSortList(command, resourceType);

			// show only the top ranked resources for each resource type
			if (command.getListCommand(resourceType).getList().size() > origEntriesPerPage) {
				this.restrictResourceList(command, resourceType, listCommand.getStart(), listCommand.getStart() + origEntriesPerPage);				
			}
			
			// set total count
			//this.setTotalCount(command, resourceType, groupingEntity, null, null, null, null, null, null, origEntriesPerPage, null);
		}		
		

		// html format - retrieve tags and return HTML view
		if (format.equals("html")) {
			command.setFollowersOfUser(logic.getUsers(null, GroupingEntity.FOLLOWER, null, null, null, null, UserRelation.FOLLOWER_OF, null, 0, 0));
			command.setUserIsFollowing(logic.getUsers(null, GroupingEntity.FOLLOWER, null, null, null, null, UserRelation.OF_FOLLOWER, null, 0, 0));

			// retrieve similar users, by the given user similarity measure
			List<User> similarUsers = this.logic.getUsers(null, GroupingEntity.USER, command.getContext().getLoginUser().getName(), null, null, null, userRelation, null, 0, 10);	
			command.getRelatedUserCommand().setRelatedUsers(similarUsers);			
			
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
