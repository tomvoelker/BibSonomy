/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.Privlevel;
import org.bibsonomy.common.enums.TagsType;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.database.systemstags.search.NetworkRelationSystemTag;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.util.EnumUtils;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.UserResourceViewCommand;
import org.bibsonomy.webapp.config.Parameters;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for user pages /user/USERNAME
 * 
 * @author Dominik Benz
 */
public class UserPageController extends SingleResourceListControllerWithTags implements MinimalisticController<UserResourceViewCommand> {
	private static final Log LOGGER = LogFactory.getLog(UserPageController.class);
	
	@Override
	public View workOn(final UserResourceViewCommand command) {
		final String format = command.getFormat();
		this.startTiming(format);
		
		initializeDidYouKnowMessageCommand(command);

		final String groupingName = command.getRequestedUser();
		
		// no user given -> error
		if (!present(groupingName)) {
			throw new MalformedURLSchemeException("error.user_page_without_username");
		}
		
		// set grouping entity, grouping name, tags, userSimilarity
		final GroupingEntity groupingEntity = GroupingEntity.USER;
		
		final List<String> requTags = command.getRequestedTagsList();
		final UserRelation userRelation = EnumUtils.searchEnumByName(UserRelation.values(), command.getUserSimilarity());
		
		// wrong user similarity requested -> error
		if (!present(userRelation)) {
			throw new MalformedURLSchemeException("error.user_page_with_wrong_user_similarity");
		}
		
		/*
		 * handle case when only tags are requested
		 */
		if (TagsType.PREFIX.equals(command.getTagstype())) {
			// TODO: "%" is MySQL specific and should be moved to the database logic implementation
			final String regex = command.getRequestedTags() + "%";
			this.handleTagsOnly(command, groupingEntity, groupingName, regex, requTags, null, Integer.MAX_VALUE, null);
		} else {
			this.handleTagsOnly(command, groupingEntity, groupingName, null, requTags, null, Integer.MAX_VALUE, null);
		}
		
		/*
		 * extract filter
		 */
		final boolean publicationFilter = this.isPublicationFilter(command.getFilter());
		if (publicationFilter) {
			this.supportedResources.remove(Bookmark.class);
		}
		final RequestWrapperContext context = command.getContext();
		// "redirect" to user-user-page controller if requested
		// TODO: better to this via Spring URL mapping
		if (context.isUserLoggedIn() && command.isPersonalized()) {
			final UserUserPageController uupc = new UserUserPageController();
			uupc.supportedResources = this.supportedResources;
			uupc.logic = this.logic;
			uupc.userSettings = this.userSettings;
			return uupc.workOn(command);
		}

		int totalNumPosts = 1;

		// retrieve and set the requested resource lists, along with total
		// counts
		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(format, command.getResourcetype())) {
			final ListCommand<?> listCommand = command.getListCommand(resourceType);
			final int entriesPerPage = listCommand.getEntriesPerPage();
			
			this.setList(command, resourceType, groupingEntity, groupingName, requTags, null, null, command.getFilter(), null, command.getStartDate(), command.getEndDate(), entriesPerPage);
			this.postProcessAndSortList(command, resourceType);

			/*
			 * set the post counts
			 */
			if (!publicationFilter) {
				this.setTotalCount(command, resourceType, groupingEntity, groupingName, requTags, null, null, null, null, command.getStartDate(), command.getEndDate(), entriesPerPage);
				totalNumPosts += listCommand.getTotalCount();
			}
		}
		
		// html format - retrieve tags and return HTML view
		if ("html".equals(format)) {
			// set page title
			command.setPageTitle("user :: " + groupingName); // TODO: i18n
			if (present(requTags)) {
				// add the tags to the title
				command.setPageTitle(command.getPageTitle() + " :: " + StringUtils.implodeStringCollection(requTags, "+"));
			}
			
			// only fetch tags if they were not already fetched by handleTagsOnly
			if (command.getTagstype() == null) {
				this.setTags(command, Resource.class, groupingEntity, groupingName, null, null, null, Integer.MAX_VALUE, null);
			}

			// retrieve concepts
			final List<Tag> concepts = this.logic.getConcepts(null, groupingEntity, groupingName, null, null, ConceptStatus.PICKED, 0, Integer.MAX_VALUE);
			command.getConcepts().setConceptList(concepts);
			
			// log if a user has reached threshold
			if (command.getTagcloud().getTags().size() >= Parameters.TAG_THRESHOLD) {
				LOGGER.debug("User " + groupingName + " has reached threshold of " + Parameters.TAG_THRESHOLD + " tags on user page");
			}
			
			// retrieve similar users, by the given user similarity measure
			final List<User> similarUsers = this.logic.getUsers(null, GroupingEntity.USER, groupingName, null, null, null, userRelation, null, 0, 10);	
			command.getRelatedUserCommand().setRelatedUsers(similarUsers);
			
			if (present(requTags)) {
				this.setRelatedTags(command, Resource.class, groupingEntity, groupingName, null, requTags, command.getStartDate(), command.getEndDate(), Order.ADDED, 0, 20, null);
				command.getRelatedTagCommand().setTagGlobalCount(totalNumPosts);
				this.endTiming();

				// forward to publication page if filter is set
				if (publicationFilter) {
					return Views.USERDOCUMENTPAGE;
				}
				
				// get the information needed for the sidebar
				command.setConceptsOfRequestedUser(this.getConceptsForSidebar(command, GroupingEntity.USER, groupingName, requTags));
				command.setConceptsOfAll(this.getConceptsForSidebar(command, GroupingEntity.ALL, null, requTags));
				command.setPostCountForTagsForAll(this.getPostCountForSidebar(GroupingEntity.ALL, "", requTags));
				
				return Views.USERTAGPAGE;
			}
			
			/*
			 * For logged users we check, if she is in a friends or group relation
			 * with the requested user. 
			 */
			final String loginUserName = context.getLoginUser().getName();
			
			/*
			 * Put the user into command to be able to show some details.
			 * 
			 * The DBLogic checks, if the login user may see the user's 
			 * details. 
			 */
			final User requestedUser = this.logic.getUserDetails(groupingName);
			command.setUser(requestedUser);
			if (context.isUserLoggedIn()) {
				/*
				 * has loginUser this user set as friend?
				 */
				command.setOfFriendUser(this.logic.getUserRelationship(loginUserName, UserRelation.OF_FRIEND, NetworkRelationSystemTag.BibSonomyFriendSystemTag).contains(requestedUser));
				command.setFriendOfUser(this.logic.getUserRelationship(loginUserName, UserRelation.FRIEND_OF, NetworkRelationSystemTag.BibSonomyFriendSystemTag).contains(requestedUser));
				
				/*
				 * has loginUser this user set as follower?
				 */
				final List<User> followersOfUser = this.logic.getUsers(null, GroupingEntity.FOLLOWER, null, null, null, null, UserRelation.FOLLOWER_OF, null, 0, 0);
				for (final User u : followersOfUser){
					if (u.getName().equals(groupingName)) {
						command.setFollowerOfUser(true);
						break;
					}
				}
				/*
				 * TODO: we need an adminLogic to access the requested user's groups ...
				 */
				
				final List<Group> loginUserNameGroups = context.getLoginUser().getGroups();
				List<Group> sharedGroups =  new LinkedList<Group>();

				for (Group g : loginUserNameGroups) {
					// only add a group if the member list is visible
					if (g.getPrivlevel() == Privlevel.PUBLIC || g.getPrivlevel() == Privlevel.MEMBERS) {
						Group groupDetails = logic.getGroupDetails(g.getName());
						for (GroupMembership m : groupDetails.getMemberships()) {
							if (m.getUser().equals(requestedUser)) {
								sharedGroups.add(g);
							}
						}
					}
				}
				if (sharedGroups.isEmpty()) {
					sharedGroups = null;
				}
				command.setSharedGroups(sharedGroups);
				
			}
			
			this.endTiming();
			
			// forward to bibtex page if filter is set
			if (publicationFilter) {
				return Views.USERDOCUMENTPAGE;
			}
			
			// if user does not exist, trigger 404
			if (!present(requestedUser.getName())) {
				throw new ObjectNotFoundException(groupingName);
			}
			
			return Views.USERPAGE;
		}
		
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(format);
	}

	private boolean isPublicationFilter(final FilterEntity filter) {
		return FilterEntity.JUST_PDF.equals(filter) || FilterEntity.DUPLICATES.equals(filter);
	}

	@Override
	public UserResourceViewCommand instantiateCommand() {
		return new UserResourceViewCommand();
	}
}
