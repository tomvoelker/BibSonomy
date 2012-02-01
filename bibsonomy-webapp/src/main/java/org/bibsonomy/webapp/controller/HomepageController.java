package org.bibsonomy.webapp.controller;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.HomepageCommand;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for Homepage
 *
 * @author Dominik Benz
 * @version $Id$
 */
public class HomepageController extends SingleResourceListController implements MinimalisticController<HomepageCommand> {
	private static final int POSTS_PER_RESOURCETYPE_LOGGED_IN = 20;

	private static final int POSTS_PER_RESOURCETYPE = 5;

	private static final Log log = LogFactory.getLog(HomepageController.class);

	/*
	 * on the homepage, only 50 tags are shown in the tag cloud
	 */
	private static final int MAX_TAGS = 50;

	@Override
	public View workOn(final HomepageCommand command) {
		log.debug(this.getClass().getSimpleName());
		final String format = command.getFormat();
		this.startTiming(this.getClass(), format);
		
		// handle the case when only tags are requested
		this.handleTagsOnly(command, GroupingEntity.ALL, null, null, null, null, MAX_TAGS, null);
				
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(format, command.getResourcetype())) {
			// disable manual setting of start value for homepage
			final ListCommand<?> listCommand = command.getListCommand(resourceType);
			listCommand.setStart(0);
			/*
			 * logged in users see 20 posts, other users 5
			 * admins may see as many posts as they like 
			 */
			final int entriesPerPage;
			if (command.getContext().isUserLoggedIn()) {
				if (Role.ADMIN.equals(command.getContext().getLoginUser().getRole())) {
					entriesPerPage = listCommand.getEntriesPerPage();
				} else {
					entriesPerPage = POSTS_PER_RESOURCETYPE_LOGGED_IN;	
				}
			} else {
				entriesPerPage = POSTS_PER_RESOURCETYPE;
			}			
			setList(command, resourceType, GroupingEntity.ALL, null, null, null, null, command.getFilter(), null, command.getStartDate(), command.getEndDate(), entriesPerPage);
			postProcessAndSortList(command, resourceType);
		}
												
		// html format - retrieve tags and return HTML view
		if ("html".equals(format)) {
			command.setPageTitle("home"); // TODO: i18n
			setTags(command, Resource.class, GroupingEntity.ALL, null, null, null, null, MAX_TAGS, null);
			
			/*
			 * add news posts (= latest blog posts) FIXME: make configurable
			 */
			command.setNews(this.logic.getPosts(Bookmark.class, GroupingEntity.GROUP, "kde", Arrays.asList("bibsonomynews"), null, null, null, null, null, null, 0, 3));
			this.endTiming();
			
			return Views.HOMEPAGE; // TODO: make configurable 
		}
		
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(format);	
	}

	/**
	 * Enforce maximal 50 tags on 
	 * @see org.bibsonomy.webapp.controller.ResourceListController#getFixedTagMax(int)
	 */
	@Override
	protected int getFixedTagMax(int tagMax) {
		return MAX_TAGS;
	}
	
	@Override
	public HomepageCommand instantiateCommand() {
		return new HomepageCommand();
	}
}
