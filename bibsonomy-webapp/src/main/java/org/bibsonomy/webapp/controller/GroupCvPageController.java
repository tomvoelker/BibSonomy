package org.bibsonomy.webapp.controller;

import java.util.Collections;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.GroupResourceViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for the "group cv page":
 * - /cv/group/GROUPNAME
 * 
 * @author wla
 * @version $Id$
 */
public class GroupCvPageController extends ResourceListController implements MinimalisticController<GroupResourceViewCommand>{
	
	/*
	 * the count of publications and bookmarks to show
	 */
	private int entries;
	
	@Override
	public View workOn(GroupResourceViewCommand command) {
		
		String requestedGroup = command.getRequestedGroup();
		Group group = logic.getGroupDetails(requestedGroup);
		final GroupingEntity groupingEntity = GroupingEntity.GROUP;
		command.setDuplicates("no");
		command.setPageTitle("Curriculum vitae");
		
		group.setUsers(this.logic.getUsers(null, groupingEntity, requestedGroup, null, null, null, null, null, 0, 1000));
		command.setGroup(group);
		
		/*
		 * create tag list
		 */
		this.setTags(command, Resource.class, groupingEntity, requestedGroup, null, null, null, Integer.MAX_VALUE, null);
		
		/*
		 *  retrieve and set the requested resource lists
		 */
		for (final Class<? extends Resource> resourceType : listsToInitialise) {
			this.setList(command, resourceType, groupingEntity, requestedGroup, Collections.singletonList(SystemTagsUtil.CV_TAG), null, null, null, null, entries);
		}
		
		/*
		 * remove duplicated posts 
		 */
		postProcessAndSortList(command, command.getBibtex().getList());
		
		return Views.GROUPCVPAGE;
	}
	
	@Override
	public GroupResourceViewCommand instantiateCommand() {
		return new GroupResourceViewCommand();
	}
	
	/**
	 * @return the entries
	 */
	public int getEntries() {
		return this.entries;
	}

	/**
	 * @param entries the count of publications and bookmarks to show
	 */
	public void setEntries(int entries) {
		this.entries = entries;
	}

}
