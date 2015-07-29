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

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.database.systemstags.markup.MyOwnSystemTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.RelatedUserCommand;
import org.bibsonomy.webapp.command.TagResourceViewCommand;
import org.bibsonomy.webapp.config.Parameters;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;


/**
 * Controller for tag pages
 * /tag/TAGNAME
 * 
 * @author Michael Wagner
 */
public class TagPageController extends SingleResourceListControllerWithTags implements MinimalisticController<TagResourceViewCommand>{
	
	@Override
	public View workOn(final TagResourceViewCommand command) {
		final String format = command.getFormat();
		this.startTiming(format);
		
		// if no tags given return
		if (!present(command.getRequestedTags())) {
			throw new MalformedURLSchemeException("error.tag_page_without_tag");
		}
		
		final List<String> requTags = command.getRequestedTagsList();
		
		// count number of non system tags
		int tagCount = SystemTagsUtil.countNonSystemTags(requTags);
		
		// special handling for 'myown' tag (is counted as system tag above, but 
		// we want to have related/similar tags for it)
		if (tagCount == 0 && requTags.size() == 1 && MyOwnSystemTag.NAME.equalsIgnoreCase(requTags.get(0))) {
			tagCount = 1;
		}
			
			
		// handle case when only tags are requested
		// FIXME we can only retrieve 1000 tags here
		this.handleTagsOnly(command, GroupingEntity.ALL, null, null, requTags, null, 1000, null);
		
		// get the information on tags and concepts needed for the sidebar
		command.setConceptsOfAll(this.getConceptsForSidebar(command, GroupingEntity.ALL, null, requTags));
		final String loginUser = command.getContext().getLoginUser().getName();
		if (present(loginUser)) {
			command.setConceptsOfLoginUser(this.getConceptsForSidebar(command, GroupingEntity.USER, loginUser, requTags));
			command.setPostCountForTagsForLoginUser(this.getPostCountForSidebar(GroupingEntity.USER, loginUser, requTags));
		}
		
		// requested order
		final Order order = command.getOrder();
		int totalNumPosts = 1; 
		
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(command)) {
			final ListCommand<?> listCommand = command.getListCommand(resourceType);
			final int entriesPerPage = listCommand.getEntriesPerPage();

			this.setList(command, resourceType, GroupingEntity.ALL, null, requTags, null, null, null, order, command.getStartDate(), command.getEndDate(), entriesPerPage);
			this.postProcessAndSortList(command, resourceType);
			
			this.setTotalCount(command, resourceType, GroupingEntity.ALL, null, requTags, null, null, null, null, command.getStartDate(), command.getEndDate(), entriesPerPage);
			totalNumPosts += listCommand.getTotalCount();
		}	
		
		/*
		 *  if order = folkrank - retrieve related users
		 *  
		 *  TODO: in practice, this is (currently) only neccessary for HTML and SWRC. The related
		 *  users will be ignored by all other views.
		 *   
		 *  (burst, publrss, swrc) related pages
		 */
		if (order.equals(Order.FOLKRANK)) {
			this.setRelatedUsers(command, GroupingEntity.ALL, requTags, order, UserRelation.FOLKRANK, 0, Parameters.NUM_RELATED_USERS);
		}
		
		// html format - retrieve related tags and return HTML view
		if ("html".equals(format)) {
			command.setPageTitle("tag :: " + StringUtils.implodeStringCollection(requTags, " "));
			if (tagCount > 0) {
				this.setRelatedTags(command, Resource.class, GroupingEntity.ALL, null, null, requTags, command.getStartDate(), command.getEndDate(), order, 0, Parameters.NUM_RELATED_TAGS, null);
			}
			// similar tags only make sense for a single requested tag
			if (tagCount == 1) {
				this.setSimilarTags(command, Resource.class, GroupingEntity.ALL, null, null, requTags, order, command.getStartDate(), command.getEndDate(), 0, Parameters.NUM_RELATED_TAGS, null);
			}
			// set total nr. of posts 
			command.getRelatedTagCommand().setTagGlobalCount(totalNumPosts);
			this.endTiming();
			return Views.TAGPAGE;
		}
		
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(format);
		
	}
	
	@Override
	public TagResourceViewCommand instantiateCommand() {
		return new TagResourceViewCommand();
	}
		
	/**
	 * retrieve related user by tag
	 * 
	 * @param cmd
	 * @param tags
	 * @param order
	 * @param start
	 * @param end
	 */
	protected void setRelatedUsers(final TagResourceViewCommand cmd, final GroupingEntity grouping, final List<String> tags, final Order order, final UserRelation relation, final int start, final int end) {
		final RelatedUserCommand relatedUserCommand = cmd.getRelatedUserCommand();
		relatedUserCommand.setRelatedUsers(this.logic.getUsers(null, grouping, null, tags, null, order, relation, null, start, end));
	}
	
}
