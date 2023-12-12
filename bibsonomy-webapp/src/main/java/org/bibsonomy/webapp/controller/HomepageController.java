/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Calendar;
import java.util.Collections;

import lombok.Setter;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.query.PostQuery;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.util.SortUtils;
import org.bibsonomy.webapp.command.HomepageCommand;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for Homepage
 *
 * @author Dominik Benz
 */
@Setter
public class HomepageController extends SingleResourceListController implements MinimalisticController<HomepageCommand> {
	
	private static final int POSTS_PER_RESOURCETYPE_LOGGED_IN = 20;
	private static final int POSTS_PER_RESOURCETYPE = 5;
	
	private String newsGroup;
	private String newsTag;
	private boolean crisEnabled;
	private String college;

	/*
	 * on the homepage, only 50 tags are shown in the tag cloud
	 */
	private static final int MAX_TAGS = 50;

	@Override
	public View workOn(final HomepageCommand command) {
		final RequestWrapperContext context = command.getContext();
		final boolean userLoggedin = context.isUserLoggedIn();

		/*
		 * Show news carousel and latest publication of the configured college in CRIS mode
		 */
		if (this.crisEnabled) {
			/*
			 * Get 10 latest publications
			 */
			ListCommand<Post<GoldStandardPublication>> publications = command.getGoldStandardPublications();
			publications.setEntriesPerPage(10);
			final Calendar calendar = Calendar.getInstance();
			final PostQueryBuilder publicationsQuery = new PostQueryBuilder()
					.college(this.college)
					.entriesStartingAt(publications.getEntriesPerPage(), publications.getStart())
					.setSortCriteria(SortUtils.singletonSortCriteria(SortKey.YEAR))
					.search(String.format("year:[* TO %s]", calendar.get(Calendar.YEAR)));

			publications.setList(this.logic.getPosts(publicationsQuery.createPostQuery(GoldStandardPublication.class)));

			/*
			 * Add news posts (= latest blog posts) for carousel news
			 */
			if (present(this.newsGroup)) {
				this.setNews(command, 5);
			}

			this.endTiming();
			return Views.CRIS_HOMEPAGE;
		}

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
			if (userLoggedin) {
				if (Role.ADMIN.equals(context.getLoginUser().getRole())) {
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
			 * add news posts (= latest blog posts) for sidebar
			 */
			if (present(this.newsGroup)) {
				this.setNews(command, 3);
			}

			this.endTiming();
			return Views.HOMEPAGE;
		}
		
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(format);
	}

	/**
	 * Get the latest news of the configured news group and tag and set it for the command
	 *
	 * @param command the command
	 * @param numOfNews number of news posts
	 */
	private void setNews(final HomepageCommand command, final int numOfNews) {
		final PostQuery<Bookmark> newsQuery = new PostQuery<>(Bookmark.class);
		newsQuery.setGrouping(GroupingEntity.GROUP);
		newsQuery.setGroupingName(this.newsGroup);
		newsQuery.setTags(Collections.singletonList(this.newsTag));
		newsQuery.setStart(0);
		newsQuery.setEnd(numOfNews);
		newsQuery.setSortCriteria(SortUtils.singletonSortCriteria(SortKey.DATE, SortOrder.DESC));
		command.setNews(this.logic.getPosts(newsQuery));
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
