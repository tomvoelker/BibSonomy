package org.bibsonomy.webapp.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.GroupMemberCommand;
import org.bibsonomy.webapp.command.GroupResourceViewCommand;
import org.bibsonomy.webapp.command.RelatedTagCommand;
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
public class GroupPageController extends MultiResourceListController implements MinimalisticController<GroupResourceViewCommand> {
	private static final Logger LOGGER = Logger.getLogger(GroupPageController.class);

	public View workOn(GroupResourceViewCommand command) {
		LOGGER.debug(this.getClass().getSimpleName());
		this.startTiming(this.getClass(), command.getFormat());
		
		// if no group given return 
		if (command.getRequestedGroup() == null) return null;
		

		// set grouping entity and grouping name
		final GroupingEntity groupingEntity = GroupingEntity.GROUP;
		final String groupingName = command.getRequestedGroup();
		final List<String> requTags = command.getRequestedTagsList();

		// set title
		// TODO: localize
		command.setPageTitle("group :: " + groupingName);		
		
		// special group given - return empty page
		if (GroupID.isSpecialGroup(groupingName)) return Views.GROUPPAGE;
		
		// determine which lists to initalize depending on the output format 
		// and the requested resourcetype
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());
		
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : listsToInitialise) {			
			this.setList(command, resourceType, groupingEntity, groupingName, requTags, null, null, null, command.getListCommand(resourceType).getEntriesPerPage());
			this.postProcessList(command, resourceType);
			
			// retrieve resource counts, if no tags are given
			if (requTags.size() == 0) { 
				int totalCount = this.logic.getStatistics(resourceType, groupingEntity, groupingName, null, null, null);
				command.getListCommand(resourceType).setTotalCount(totalCount);				
			}
		}		
		
		// html format - retrieve tags and return HTML view
		if ("html".equals(command.getFormat())) {
			this.setTags(command, Resource.class, groupingEntity, groupingName, null, null, null, 0, 1000, null);
			this.setGroupMembers(command, groupingName);
			
			if (requTags.size() > 0) {
				this.setRelatedTags(command, Resource.class, groupingEntity, groupingName, null, requTags, 0, 20, null);
				this.endTiming();
				return Views.GROUPTAGPAGE;
			}
			this.endTiming();
			return Views.GROUPPAGE;			
		}
		
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(command.getFormat());		
	}

	public GroupResourceViewCommand instantiateCommand() {
		return new GroupResourceViewCommand();
	}	
	
	/**
     * Retrieve a set of related tags to a list of given tags 
     * from the database logic and add them to the command object
     * 
	 * @param <T> extends Resource, the resource type
	 * @param <V> extends ResourceViewCommand, the command
	 * @param cmd the command
	 * @param resourceType the resource type
	 * @param groupingEntity the grouping entity
	 * @param groupingName the grouping name
	 * @param regex regular expression for tag filtering
	 * @param tags list of tags
	 * @param start start parameter
	 * @param end end parameter
	 * 
	 * TODO: move this in MultiResourceListController?
	 */
	protected <T extends Resource, V extends GroupResourceViewCommand> void setRelatedTags(V cmd, Class<T> resourceType, GroupingEntity groupingEntity, String groupingName, String regex, List<String> tags, int start, int end, String search) {
		RelatedTagCommand relatedTagCommand = cmd.getRelatedTagCommand();
		relatedTagCommand.setRelatedTags(this.logic.getTags(resourceType, groupingEntity, groupingName, regex, tags, null, null, start, end, search));		
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
