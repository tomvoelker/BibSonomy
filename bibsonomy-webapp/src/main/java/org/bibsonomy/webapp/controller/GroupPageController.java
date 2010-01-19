package org.bibsonomy.webapp.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.systemstags.SystemTags;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.webapp.command.GroupResourceViewCommand;
import org.bibsonomy.webapp.command.ListCommand;
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
public class GroupPageController extends SingleResourceListControllerWithTags implements MinimalisticController<GroupResourceViewCommand> {
	private static final Log log = LogFactory.getLog(GroupPageController.class);

	public View workOn(GroupResourceViewCommand command) {
		log.debug(this.getClass().getSimpleName());
		this.startTiming(this.getClass(), command.getFormat());

		// if no group given -> error
		if (command.getRequestedGroup() == null) {
			log.error("Invalid query /group without roup name");
			throw new MalformedURLSchemeException("error.group_page_without_groupname");
		}				

		// set grouping entity and grouping name
		final GroupingEntity groupingEntity = GroupingEntity.GROUP;
		final String groupingName = command.getRequestedGroup();
		final List<String> requTags = command.getRequestedTagsList();

		//check if system-tag "sys:relevantFor:" exists in taglist
		final boolean isRelevantFor = checkRelevantFor(requTags);
		
		// handle case when only tags are requested
		this.handleTagsOnly(command, groupingEntity, groupingName, null, requTags , null, null, 0, Integer.MAX_VALUE, null);

		// special group given - return empty page
		if (GroupID.isSpecialGroup(groupingName)) return Views.GROUPPAGE;

		// determine which lists to initalize depending on the output format 
		// and the requested resourcetype
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());

		FilterEntity filter = null;

		// display only posts, which have a document attached
		if ("myGroupPDF".equals(command.getFilter())) {
			filter = FilterEntity.JUST_PDF;
			this.listsToInitialise.remove(Bookmark.class);
		}
		
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : listsToInitialise) {			
			final ListCommand<?> listCommand = command.getListCommand(resourceType);
			final int entriesPerPage = listCommand.getEntriesPerPage();
			this.setList(command, resourceType, groupingEntity, groupingName, requTags, null, null, filter, null, entriesPerPage);
			this.postProcessAndSortList(command, resourceType);

			// retrieve resource counts, if no tags are given
			if (requTags.size() == 0 && filter != FilterEntity.JUST_PDF) { 
				this.setTotalCount(command, resourceType, groupingEntity, groupingName, requTags, null, null, null, null, entriesPerPage, null);
			}
		}

		// html format - retrieve tags and return HTML view
		if ("html".equals(command.getFormat())) {


			if (isRelevantFor && filter != FilterEntity.JUST_PDF) {
				/*
				 * handle the "relevant for group" pages
				 */
				command.setPageTitle("relevant for :: " + groupingName);	
				this.setRelatedTags(command, Resource.class, groupingEntity, groupingName, null, requTags, Order.ADDED, 0, 20, null);
				this.endTiming();
				return Views.RELEVANTFORPAGE;
			} 

			// set title
			// TODO: localize
			command.setPageTitle("group :: " + groupingName);	

			// always retrieve all tags of this group
			this.setTags(command, Resource.class, groupingEntity, groupingName, null, null, null, null, 0, Integer.MAX_VALUE, null);
			this.setGroupDetails(command, groupingName);

			if (requTags.size() > 0) {
				this.setRelatedTags(command, Resource.class, groupingEntity, groupingName, null, requTags, Order.ADDED, 0, 20, null);
				this.endTiming();
			}
			this.endTiming();

			// forward to bibtex page if PDF filter is set
			if (filter == FilterEntity.JUST_PDF) {
				return Views.GROUPDOCUMENTPAGE;
			} else if(requTags.size() > 0){
				return Views.GROUPTAGPAGE;	
			} 

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
	 * Retrieve all members of the given group in dependence of the group privacy level
	 * FIXME: duplicated in ViewablePageController!
	 * @param <V> extends ResourceViewCommand, the command
	 * @param cmd the command
	 * @param groupName the name of the group
	 */
	private <V extends GroupResourceViewCommand> void setGroupDetails(V cmd, String groupName) {
		final Group group = this.logic.getGroupDetails(groupName);
		if (group != null) {
			group.setUsers(this.logic.getUsers(null, GroupingEntity.GROUP, groupName, null, null, null, null, null, 0, 100));
		}
		cmd.setGroup(group);
	}

	/**
	 * @FIXME This should be done by the system-tag framework
	 */
	private boolean checkRelevantFor(List<String> tags){
		for(String tag: tags){
			/* 
			 * let's not be too strict here if we find wrong capitalization (e.g. 
			 * 'relevantfor' instead of 'relevantFor'
			 */			
			if(tag.toLowerCase().startsWith(SystemTags.RELEVANTFOR.getPrefix().toLowerCase())){
				log.debug("SYSTEMTAG 'sys:relevantFor:' found --> forward to the relevant-for View");
				return true;
			}
		}
		return false;
	}
}
