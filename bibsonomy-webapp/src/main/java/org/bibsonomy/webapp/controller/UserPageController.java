package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.util.EnumUtils;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.UserResourceViewCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for user pages /user/USERNAME
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class UserPageController extends SingleResourceListControllerWithTags implements MinimalisticController<UserResourceViewCommand> {
	private static final Log LOGGER = LogFactory.getLog(UserPageController.class);

	public View workOn(final UserResourceViewCommand command) {
		LOGGER.debug(this.getClass().getSimpleName());
		this.startTiming(this.getClass(), command.getFormat());

		// no user given -> error
		if (command.getRequestedUser() == null) {
			LOGGER.warn("Invalid query /user without username");
			throw new MalformedURLSchemeException("error.user_page_without_username");
		}
		
		// set grouping entity, grouping name, tags, userSimilarity
		final GroupingEntity groupingEntity = GroupingEntity.USER;
		final String groupingName = command.getRequestedUser();
		final List<String> requTags = command.getRequestedTagsList();
		final UserRelation userRelation = EnumUtils.searchEnumByName(UserRelation.values(), command.getUserSimilarity());
		
		// wrong user similarity requested -> error
		if (userRelation == null) {
			LOGGER.error("Invalid user similarity requested: " + command.getUserSimilarity());
			throw new MalformedURLSchemeException("error.user_page_with_wrong_user_similarity");			
		}
		
		// handle case when only tags are requested
		this.handleTagsOnly(command, groupingEntity, groupingName, null, requTags, null, Integer.MAX_VALUE, null);
				
		FilterEntity filter = null;

		// display only posts which have a document attached
		if (command.getFilter().equals("myPDF")) {
			filter = FilterEntity.JUST_PDF;
		}

		// display duplicate entries
		if (command.getFilter().equals("myDuplicates")) {
			filter = FilterEntity.DUPLICATES;
		}
		
		// if user is logged in fetch if the logged in user follows the requested
		if (command.getContext().getUserLoggedIn()){
			List<User> followersOfUser = this.logic.getUsers(null, GroupingEntity.FOLLOWER, null, null, null, null, UserRelation.FOLLOWER_OF, null, 0, 0);
			for (User u : followersOfUser){
				if (u.getName().equals(command.getRequestedUser())){
					command.setFollowerOfUser(true);
					break;
				}
			}
		}

		// determine which lists to initalize depending on the output format
		// and the requested resourcetype
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());

		if (filter == FilterEntity.JUST_PDF || filter == FilterEntity.DUPLICATES) {
			this.listsToInitialise.remove(Bookmark.class);
		}
		
		// "redirect" to user-user-page controller if requested
		// TODO: better to this via Spring URL mapping
		if (command.getContext().isUserLoggedIn() &&  command.isPersonalized()) {
			UserUserPageController uupc = new UserUserPageController();
			uupc.listsToInitialise = this.listsToInitialise;
			uupc.logic = this.logic;
			uupc.userSettings = this.userSettings;
			return uupc.workOn(command);
		}		

		int totalNumPosts = 1;

		// retrieve and set the requested resource lists, along with total
		// counts
		for (final Class<? extends Resource> resourceType : listsToInitialise) {
			final ListCommand<?> listCommand = command.getListCommand(resourceType);
			final int entriesPerPage = listCommand.getEntriesPerPage();
			
			this.setList(command, resourceType, groupingEntity, groupingName, requTags, null, null, filter, null, entriesPerPage);
			this.postProcessAndSortList(command, resourceType);

			/*
			 * set the post counts
			 */
			if (filter != FilterEntity.JUST_PDF && filter != FilterEntity.DUPLICATES) {
				this.setTotalCount(command, resourceType, groupingEntity, groupingName, requTags, null, null, filter, null, entriesPerPage, null);
				totalNumPosts += listCommand.getTotalCount();
			}
		}


		// html format - retrieve tags and return HTML view
		if (command.getFormat().equals("html")) {

			// set page title
			// TODO: i18n
			command.setPageTitle("user :: " + groupingName);
			if (present(requTags)) {
				// add the tags to the title
				command.setPageTitle(command.getPageTitle()+" :: "+ requTags.get(0));
				for (int i=1; i<requTags.size(); i++){
					command.setPageTitle(command.getPageTitle() + "+" + requTags.get(i));
				}
			}
			
			this.setTags(command, Resource.class, groupingEntity, groupingName, null, null, null, Integer.MAX_VALUE, null);

			// retrieve concepts
			final List<Tag> concepts = this.logic.getConcepts(null, groupingEntity, groupingName, null, null, ConceptStatus.PICKED, 0, Integer.MAX_VALUE);
			command.getConcepts().setConceptList(concepts);
			command.getConcepts().setNumConcepts(concepts.size());

			// log if a user has reached threshold
			if (command.getTagcloud().getTags().size() >= 20000) {
				LOGGER.debug("User " + groupingName + " has reached threshold of 20000 tags on user page");
			}
			
			// retrieve similar users, by the given user similarity measure
			List<User> similarUsers = this.logic.getUsers(null, GroupingEntity.USER, groupingName, null, null, null, userRelation, null, 0, 10);	
			command.getRelatedUserCommand().setRelatedUsers(similarUsers);
			
			if (requTags.size() > 0) {
				this.setRelatedTags(command, Resource.class, groupingEntity, groupingName, null, requTags, Order.ADDED, 0, 20, null);
				command.getRelatedTagCommand().setTagGlobalCount(totalNumPosts);
				this.endTiming();

				// forward to bibtex page if filter is set
				if (filter == FilterEntity.JUST_PDF || filter == FilterEntity.DUPLICATES) {
					return Views.USERDOCUMENTPAGE;
				}
				
				return Views.USERTAGPAGE;
			}

			/*
			 * add user details to command, if loginUser is admin
			 */
			if (Role.ADMIN.equals(logic.getAuthenticatedUser().getRole())) {
				command.setUser(logic.getUserDetails(command.getRequestedUser()));
			}

			this.endTiming();

			// forward to bibtex page if filter is set
			if (filter == FilterEntity.JUST_PDF || filter == FilterEntity.DUPLICATES) {
				return Views.USERDOCUMENTPAGE;
			} 
			
			return Views.USERPAGE;
		}
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(command.getFormat());
	}

	public UserResourceViewCommand instantiateCommand() {
		return new UserResourceViewCommand();
	}
}
