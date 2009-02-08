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
import org.bibsonomy.webapp.command.FriendsResourceViewCommand;
import org.bibsonomy.webapp.command.UserResourceViewCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Steffen Kress
 * @version $Id: FriendsPageController.java,v 1.1 2009-02-07 05:16:38 steffen
 *          Exp $
 */
public class FriendsPageController extends SingleResourceListControllerWithTags implements MinimalisticController<FriendsResourceViewCommand> {
	private static final Logger LOGGER = Logger.getLogger(FriendsPageController.class);

	public View workOn(final FriendsResourceViewCommand command) {
		LOGGER.debug(this.getClass().getSimpleName());
		this.startTiming(this.getClass(), command.getFormat());

		// set grouping entity, grouping name, tags
		final GroupingEntity groupingEntity = GroupingEntity.FRIEND;
		final String groupingName = command.getRequestedUser();
		final List<String> requTags = command.getRequestedTagsList();

		FilterEntity filter = null;

		// display only posts which have a document attached
		if (command.getFilter().equals("myPDF")) {
			filter = FilterEntity.JUST_PDF;
		}

		// display duplicate entries
		if (command.getFilter().equals("myDuplicates")) {
			filter = FilterEntity.DUPLICATES;
		}

		// determine which lists to initalize depending on the output format
		// and the requested resourcetype
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());

		if (filter == FilterEntity.JUST_PDF || filter == FilterEntity.DUPLICATES) {
			this.listsToInitialise.remove(Bookmark.class);
		}

		Integer totalNumPosts = 1;

		// retrieve and set the requested resource lists, along with total
		// counts
		for (final Class<? extends Resource> resourceType : listsToInitialise) {
			this.setList(command, resourceType, groupingEntity, groupingName, requTags, null, null, filter, null, command.getListCommand(resourceType).getEntriesPerPage());
			this.postProcessAndSortList(command, resourceType);
		}

		// // set page title
		command.setPageTitle("friends");
		// // html format - retrieve tags and return HTML view
		if (command.getFormat().equals("html")) {
			command.setUserFriends(logic.getUserFriends(command.getContext().getLoginUser()));
			command.setFriendsOfUser(logic.getFriendsOfUser(command.getContext().getLoginUser()));
			// log if a user has reached threshold
			if (command.getTagcloud().getTags().size() > 19999) {
				LOGGER.error("User " + groupingName + " has reached threshold of 20000 tags on user page");
			}

			if (requTags.size() > 0) {
				this.setRelatedTags(command, Resource.class, groupingEntity, groupingName, null, requTags, Order.ADDED, 0, 20, null);
				command.getRelatedTagCommand().setTagGlobalCount(totalNumPosts);
				this.endTiming();

				// forward to bibtex page if filter is set
				if (filter == FilterEntity.JUST_PDF || filter == FilterEntity.DUPLICATES) {
					return Views.USERDOCUMENTPAGE;
				} else {
					return Views.USERTAGPAGE;
				}
			}

			/*
			 * add user details to command, if loginUser is admin
			 */
			if (Role.ADMIN.equals(logic.getAuthenticatedUser().getRole())) {
				// command.setUser(logic.getUserDetails(command.getRequestedUser()));
			}

			this.endTiming();

			// forward to bibtex page if filter is set
			if (filter == FilterEntity.JUST_PDF || filter == FilterEntity.DUPLICATES) {
				return Views.USERDOCUMENTPAGE;
			} else {
				return Views.FRIENDSPAGE;
			}
		}
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(command.getFormat());
	}

	public FriendsResourceViewCommand instantiateCommand() {
		return new FriendsResourceViewCommand();
	}
}
