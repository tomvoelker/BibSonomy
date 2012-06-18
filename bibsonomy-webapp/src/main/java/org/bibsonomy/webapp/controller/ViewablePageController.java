package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.GroupResourceViewCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.spring.security.exceptions.AccessDeniedNoticeException;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Christian Kramer
 * @version $Id: ViewablePageController.java,v 1.25 2012-02-08 16:19:15 rja Exp
 *          $
 */
public class ViewablePageController extends SingleResourceListControllerWithTags implements MinimalisticController<GroupResourceViewCommand> {
	private static final Log log = LogFactory.getLog(ViewablePageController.class);

	@Override
	public View workOn(GroupResourceViewCommand command) {
		log.debug(this.getClass().getSimpleName());
		final String format = command.getFormat();
		this.startTiming(this.getClass(), format);
		final RequestWrapperContext context = command.getContext();
		
		// we need to be logged in, and a group needs to be present
		if (!context.isUserLoggedIn()){
			throw new AccessDeniedNoticeException("please log in", "error.viewable_page_not_logged_in");
		}
		
		if (!present(command.getRequestedGroup())) {
			throw new MalformedURLSchemeException("error.viewable_page_without_group");
		}
						
		// set grouping entity and grouping name
		final GroupingEntity groupingEntity = GroupingEntity.VIEWABLE;
		final String groupingName = command.getRequestedGroup();
		final List<String> requTags = command.getRequestedTagsList();
		
		// set title
		command.setPageTitle("viewable :: " + groupingName); // TODO: i18n
		
		// handle the case when only tags are requested
		// TODO: max 1000 tags
		this.handleTagsOnly(command, groupingEntity, groupingName, null, null, null, 1000, null);		
		
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(format, command.getResourcetype())) {			
			this.setList(command, resourceType, groupingEntity, groupingName, requTags, null, null, null, null, command.getStartDate(), command.getEndDate(), command.getListCommand(resourceType).getEntriesPerPage());
			this.postProcessAndSortList(command, resourceType);
		}		
		
		// html format - retrieve tags and return HTML view
		if ("html".equals(format)) {
			// only fetch tags if they were not already fetched by handleTagsOnly
			if (command.getTagstype() == null) {
				this.setTags(command, Resource.class, groupingEntity, groupingName, null, null, null, Integer.MAX_VALUE, null);
			}
			this.setGroupDetails(command, groupingName);
			
			if (requTags.size() > 0) {
				this.setRelatedTags(command, Resource.class, groupingEntity, groupingName, null, requTags, command.getStartDate(), command.getEndDate(), null, 0, 20, null);
				this.endTiming();
				return Views.VIEWABLETAGPAGE;
			}
			this.endTiming();
			return Views.VIEWABLEPAGE;			
		}
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(format);		
	}

	/**
	 * Retrieve all members of the given group in dependence of the group
	 * privacy level FIXME: duplicated in GroupPageController!
	 * 
	 * @param cmd
	 *            the command
	 * @param groupName
	 *            the name of the group
	 */
	private void setGroupDetails(final GroupResourceViewCommand cmd, String groupName) {
		final Group group = this.logic.getGroupDetails(groupName);
		if (group != null) {
			group.setUsers(this.logic.getUsers(null, GroupingEntity.GROUP, groupName, null, null, null, null, null, 0, 100));
		}
		cmd.setGroup(group);
	}

	@Override
	public GroupResourceViewCommand instantiateCommand() {
		return new GroupResourceViewCommand();
	}

}
