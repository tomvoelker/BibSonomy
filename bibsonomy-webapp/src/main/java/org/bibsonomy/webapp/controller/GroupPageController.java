package org.bibsonomy.webapp.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.webapp.command.GroupMemberCommand;
import org.bibsonomy.webapp.command.GroupResourceViewCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for Grouppages
 * 
 * group/GROUP and group/GROUP/TAGS
 * 
 * @author Stefan Stuetzer
 * @version $Id$
 */
public class GroupPageController extends MultiResourceListControllerWithTags implements MinimalisticController<GroupResourceViewCommand> {
	private static final Logger LOGGER = Logger.getLogger(GroupPageController.class);

	public View workOn(GroupResourceViewCommand command) {
		LOGGER.debug(this.getClass().getSimpleName());
		this.startTiming(this.getClass(), command.getFormat());
		
		// if no group given -> error
		if (command.getRequestedGroup() == null) {
			LOGGER.error("Invalid query /group without roup name");
			throw new MalformedURLSchemeException("error.group_page_without_groupname");
		}				

		// set grouping entity and grouping name
		final GroupingEntity groupingEntity = GroupingEntity.GROUP;
		final String groupingName = command.getRequestedGroup();
		final List<String> requTags = command.getRequestedTagsList();
		FilterEntity filter = null;
		
		// retrieve only tags
		if (!command.getRestrictToTags().equals("false")) {
			this.setTags(command, Resource.class, groupingEntity, groupingName, null, null, null, null, 0, 1000, null);
			
			// TODO: other output formats
			return Views.JSON;
		}		
		
		// display only posts, which have a document attached
		if (command.getFilter().equals("myGroupPDF")) {
			filter = FilterEntity.JUST_PDF;
		}	
		
		// set title
		// TODO: localize
		command.setPageTitle("group :: " + groupingName);		
		
		// special group given - return empty page
		if (GroupID.isSpecialGroup(groupingName)) return Views.GROUPPAGE;
		
		// determine which lists to initalize depending on the output format 
		// and the requested resourcetype
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());
		
		if (filter == FilterEntity.JUST_PDF) {
			this.listsToInitialise.remove(Bookmark.class);
		}
		
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : listsToInitialise) {			
			this.setList(command, resourceType, groupingEntity, groupingName, requTags, null, null, filter, null, command.getListCommand(resourceType).getEntriesPerPage());
			this.postProcessAndSortList(command, resourceType);
			
			// retrieve resource counts, if no tags are given
			if (requTags.size() == 0 && filter != FilterEntity.JUST_PDF) { 
				//int totalCount = this.logic.getStatistics(resourceType, groupingEntity, groupingName, null, null, null);
				int start = command.getListCommand(resourceType).getStart();
				int totalCount = this.logic.getPostStatistics(resourceType, GroupingEntity.GROUP, groupingName, requTags, null, null, filter, start, start + command.getListCommand(resourceType).getEntriesPerPage(), null, null);
				
				command.getListCommand(resourceType).setTotalCount(totalCount);				
			}
		}		
		
		// html format - retrieve tags and return HTML view
		if ("html".equals(command.getFormat())) {
			this.setTags(command, Resource.class, groupingEntity, groupingName, null, null, null, null, 0, 1000, null);
			this.setGroupMembers(command, groupingName);
			
			if (requTags.size() > 0) {
				this.setRelatedTags(command, Resource.class, groupingEntity, groupingName, null, requTags, Order.ADDED, 0, 20, null);
				this.endTiming();
				

				// forward to bibtex page if PDF filter is set
				if (filter == FilterEntity.JUST_PDF) {
					return Views.GROUPDOCUMENTPAGE;
				} else {
					return Views.GROUPTAGPAGE;
				}
				
			}
			this.endTiming();
			
			// forward to bibtex page if PDF filter is set
			if (filter == FilterEntity.JUST_PDF) {
				return Views.GROUPDOCUMENTPAGE;
			} else {
				return Views.GROUPPAGE;			
			}
		}		
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(command.getFormat());		
	}

	public GroupResourceViewCommand instantiateCommand() {
		return new GroupResourceViewCommand();
	}	
	
	
	/**
	 * Retrieve all members of the given group in dependece of the group privacy level
	 * @param <V> extends ResourceViewCommand, the command
	 * @param cmd the command
	 * @param groupName the name of the group
	 */
	protected <V extends GroupResourceViewCommand> void setGroupMembers(V cmd, String groupName) {
		GroupMemberCommand memberCommand = cmd.getMemberCommand();
		List<User> members = this.logic.getUsers(groupName, 0, 100);
		memberCommand.setGroup(groupName);
		for (User u: members) {
			memberCommand.addMember(u.getName());
		}
	}
}
