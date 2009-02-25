package org.bibsonomy.webapp.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.webapp.command.FriendResourceViewCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Steffen Kress
 * @version $Id$
 */
public class FriendPageController extends SingleResourceListControllerWithTags implements MinimalisticController<FriendResourceViewCommand> {
	private static final Logger LOGGER = Logger.getLogger(UserPageController.class);

	public View workOn(final FriendResourceViewCommand command) {
		LOGGER.debug(this.getClass().getSimpleName());
		this.startTiming(this.getClass(), command.getFormat());

		// no user given -> error
		if (command.getRequestedUser() == null) {
			LOGGER.error("Invalid query /friend without friendname");
			throw new MalformedURLSchemeException("error.user_page_without_username");
		}

		// set grouping entity, grouping name, tags
		final GroupingEntity groupingEntity = GroupingEntity.FRIEND;//GroupingEntity.FRIEND;
		final String groupingName = command.getRequestedUser();
		final List<String> requTags = command.getRequestedTagsList();

		FilterEntity filter = null;

		// display only posts which have a document attached
//		if (command.getFilter().equals("myPDF")) {
//			filter = FilterEntity.JUST_PDF;
//		}

		// display duplicate entries
//		if (command.getFilter().equals("myDuplicates")) {
//			filter = FilterEntity.DUPLICATES;
//		}

		// determine which lists to initalize depending on the output format
		// and the requested resourcetype
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());

//		if (filter == FilterEntity.JUST_PDF || filter == FilterEntity.DUPLICATES) {
//			this.listsToInitialise.remove(Bookmark.class);
//		}

//		Integer totalNumPosts = 1;

		// retrieve and set the requested resource lists, along with total
		// counts
		for (final Class<? extends Resource> resourceType : listsToInitialise) {
			this.setList(command, resourceType, groupingEntity, groupingName, requTags, null, null, filter, null, command.getListCommand(resourceType).getEntriesPerPage());
			this.postProcessAndSortList(command, resourceType);

//			if (filter != FilterEntity.JUST_PDF && filter != FilterEntity.DUPLICATES) {
//				// int totalCount = this.logic.getStatistics(resourceType,
//				// groupingEntity, groupingName, null, null, requTags);
//				int start = command.getListCommand(resourceType).getStart();
//				int totalCount = this.logic.getPostStatistics(resourceType, groupingEntity, groupingName, requTags, null, null, filter, start, start + command.getListCommand(resourceType).getEntriesPerPage(), null, null);
//
//				command.getListCommand(resourceType).setTotalCount(totalCount);
//				totalNumPosts += totalCount;
//			}
		}
		// set page title
		command.setPageTitle("friend :: " + groupingName);

		// html format - retrieve tags and return HTML view
		if (command.getFormat().equals("html")) {
			this.setTags(command, Resource.class, groupingEntity, groupingName, null, null, null, null, 0, 20000, null);

			// log if a user has reached threshold
			if (command.getTagcloud().getTags().size() > 19999) {
				LOGGER.error("User " + groupingName + " has reached threshold of 20000 tags on friend page");
			}

//			if (requTags.size() > 0) {
//				this.setRelatedTags(command, Resource.class, groupingEntity, groupingName, null, requTags, Order.ADDED, 0, 20, null);
//				command.getRelatedTagCommand().setTagGlobalCount(totalNumPosts);
//				this.endTiming();
//
//				// forward to bibtex page if filter is set
//				if (filter == FilterEntity.JUST_PDF || filter == FilterEntity.DUPLICATES) {
//					return Views.USERDOCUMENTPAGE;
//				}
				
				//return Views.FRIENDPAGE;
//			}
			/*
			 * add user details to command, if loginUser is admin
			 */
			if (Role.ADMIN.equals(logic.getAuthenticatedUser().getRole())) {
				command.setFriend(logic.getUserDetails(command.getRequestedUser()));
			}

			this.endTiming();
			/*
			// forward to bibtex page if filter is set
			if (filter == FilterEntity.JUST_PDF || filter == FilterEntity.DUPLICATES) {
				return Views.USERDOCUMENTPAGE;
			} */
			return Views.FRIENDPAGE; 
		}
		this.endTiming();
		// export - return the appropriate view
		//return Views.getViewByFormat(command.getFormat());
		
		return Views.FRIENDSPAGE;
	}

	public FriendResourceViewCommand instantiateCommand() {
		return new FriendResourceViewCommand();
	}
}
