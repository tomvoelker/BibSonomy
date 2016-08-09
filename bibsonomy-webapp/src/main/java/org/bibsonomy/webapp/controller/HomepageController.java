/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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

import java.util.Arrays;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.enums.SearchType;
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
 */
public class HomepageController extends SingleResourceListController implements MinimalisticController<HomepageCommand> {
	
	private static final int POSTS_PER_RESOURCETYPE_LOGGED_IN = 20;
	private static final int POSTS_PER_RESOURCETYPE = 5;
	
	
	private String newsGroup = "kde";
	private String newsTag = "bibsonomynews";

	/*
	 * on the homepage, only 50 tags are shown in the tag cloud
	 */
	private static final int MAX_TAGS = 50;

	@Override
	public View workOn(final HomepageCommand command) {
		final String format = command.getFormat();
		this.startTiming(format);
		
		// handle the case when only tags are requested
		this.handleTagsOnly(command, GroupingEntity.ALL, null, null, null, null, MAX_TAGS, null);
		
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(command)) {
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
			setTags(command, Resource.class, GroupingEntity.ALL, null, null, null, null, null, MAX_TAGS, null);
			
			/*
			 * add news posts (= latest blog posts)
			 */
			command.setNews(this.logic.getPosts(Bookmark.class, GroupingEntity.GROUP, newsGroup, Arrays.asList(newsTag), null, null,SearchType.LOCAL, null, null, null, null, 0, 3));
			this.endTiming();
			
			return Views.HOMEPAGE;
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

	/**
	 * @param newsGroup the newsGroup to set
	 */
	public void setNewsGroup(String newsGroup) {
		this.newsGroup = newsGroup;
	}

	/**
	 * @param newsTag the newsTag to set
	 */
	public void setNewsTag(String newsTag) {
		this.newsTag = newsTag;
	}

}
