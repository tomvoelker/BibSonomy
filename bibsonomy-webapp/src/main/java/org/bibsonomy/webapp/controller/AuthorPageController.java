/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.SearchType;
import org.bibsonomy.database.systemstags.SystemTagsExtractor;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.database.systemstags.search.AuthorSystemTag;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.webapp.command.AuthorResourceCommand;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * controller for author pages
 *    - /author/<AUTHOR>
 *    - /author/<AUTHOR>/<TAG(S)>
 * 
 * @author daill
 */
public class AuthorPageController extends SingleResourceListControllerWithTags implements MinimalisticController<AuthorResourceCommand>{
	private static final Log log = LogFactory.getLog(AuthorPageController.class);

	@Override
	public View workOn(final AuthorResourceCommand command) {
		log.debug(this.getClass().getSimpleName());
		final String format = command.getFormat();
		this.startTiming(format);

		// get author query - it might still contain some system tags at this point!
		String authorQuery = command.getRequestedAuthor();

		// if no author given throw error 
		if (!present(authorQuery)) {
			throw new MalformedURLSchemeException("error.author_page_without_authorname");
		}
		
		// set grouping entity = ALL
		final GroupingEntity groupingEntity = GroupingEntity.ALL;
		
		final List<String> requTags = command.getRequestedTagsList();
		/*
		 * remember if tags were given by user - if so, forward to special page
		 * (this also checks of only systemtags are contained) 
		 */		
		final boolean hasTags = (SystemTagsUtil.countNonSystemTags(requTags) > 0);	
		
		// check for further system tags
		// FIXME: how may this happen? http://www.bibsonomy.org/author<tag>/tag
		final List<String> sysTags = SystemTagsExtractor.extractSearchSystemTagsFromString(authorQuery, " ");
		if (sysTags.size() > 0) {
			// remove them from the query
			authorQuery = this.removeSystemtagsFromQuery(authorQuery, sysTags);
			// add them to the tags list
			requTags.addAll(sysTags);
		}
		sysTags.addAll(SystemTagsExtractor.extractSystemTags(requTags));
				
		// add the requested author as a system tag
		final String sysAuthor = SystemTagsUtil.buildSystemTagString(AuthorSystemTag.NAME, authorQuery);
		requTags.add(sysAuthor);
		sysTags.add(sysAuthor);
		
		//sets the search type
		final SearchType searchType;
		if (command.getScope() == null) {
			searchType = SearchType.LOCAL;
		} else {
			searchType = command.getScope();
		}
		
		// handle case when only tags are requested
		this.handleTagsOnly(command, groupingEntity, null, null, requTags, null, 1000, null);
		
		int totalNumPosts = 0;
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(command)) {
			final ListCommand<?> listCommand = command.getListCommand(resourceType);
			this.setList(command, resourceType, groupingEntity, null, requTags, null, null, searchType, null, null, command.getStartDate(), command.getEndDate(), listCommand.getEntriesPerPage());
			
			this.postProcessAndSortList(command, resourceType);
			totalNumPosts += listCommand.getTotalCount();
		}
		
		// html format - retrieve tags and return HTML view
		if ("html".equals(format)) {
			// only fetch tags if they were not already fetched by handleTagsOnly
			if (command.getTagstype() == null) {
				this.setTags(command, BibTex.class, groupingEntity, null, null, sysTags, null, 1000, null, searchType);
			}
			this.endTiming();
			if (hasTags) {
				this.setRelatedTags(command, BibTex.class, groupingEntity, null, null, requTags, command.getStartDate(), command.getEndDate(), Order.ADDED, 0, 20, null);
				command.getRelatedTagCommand().setTagGlobalCount(totalNumPosts);
				return Views.AUTHORTAGPAGE;
			}
			return Views.AUTHORPAGE;
		}
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(format);
	}
	
	@Override
	public AuthorResourceCommand instantiateCommand() {
		return new AuthorResourceCommand();
	}
	
	private static String removeSystemtagsFromQuery(String authorQuery, final List<String> sysTags) {
		for (final String sysTag : sysTags) {
			// remove them from author query string
			authorQuery = authorQuery.replace(sysTag, "");
		}
		return authorQuery;
	}
}
