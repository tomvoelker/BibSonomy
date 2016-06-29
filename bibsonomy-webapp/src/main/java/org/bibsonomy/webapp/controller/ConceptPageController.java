/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.webapp.command.ConceptResourceViewCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for concept pages
 *   - concept/tag/CONCEPT
 *   - concept/user/USER/CONCEPT
 *   - concept/group/GROUP/CONCEPT
 * 
 * @author Michael Wagner
 */
public class ConceptPageController extends SingleResourceListController implements MinimalisticController<ConceptResourceViewCommand>{

	@Override
	public View workOn(final ConceptResourceViewCommand command) {
		final String format = command.getFormat();
		this.startTiming(format);
		
		// if no concept given -> error
		if (!present(command.getRequestedTags())) {
			throw new MalformedURLSchemeException("error.concept_page_without_conceptname");
		}
		
		final List<String> requTags = command.getRequestedTagsList();

		final String requUser = command.getRequestedUser();
		final String requGroup = command.getRequestedGroup();
		final String loginUser = command.getContext().getLoginUser().getName();
		
		GroupingEntity groupingEntity = GroupingEntity.ALL;
		String groupingName = null; // the name of the requested user or group
		
		// get the information on tags and concepts; needed for the sidebar
		command.setPostCountForTagsForAll(this.getPostCountForSidebar(GroupingEntity.ALL, "", requTags));
		if (present(requUser)) {
			command.setPostCountForTagsForRequestedUser(this.getPostCountForSidebar(GroupingEntity.USER, requUser, requTags));
			command.setConceptsOfAll(this.getConceptsForSidebar(command, GroupingEntity.ALL, null, requTags));
		} else if (present(requGroup)) {
			command.setPostCountForTagsForGroup(this.getPostCountForSidebar(GroupingEntity.GROUP, requGroup, requTags));
			command.setConceptsOfAll(this.getConceptsForSidebar(command, GroupingEntity.ALL, null, requTags));
		} else if (present(loginUser)) {
			command.setPostCountForTagsForLoginUser(this.getPostCountForSidebar(GroupingEntity.USER, loginUser, requTags));
			command.setConceptsOfLoginUser(this.getConceptsForSidebar(command, GroupingEntity.USER, loginUser, requTags));
		}
		
		/*
		 * "convert" tags to concepts
		 */
		for (int i = 0; i < requTags.size(); i++){
			requTags.set(i, Tag.CONCEPT_PREFIX + requTags.get(i));
		}
		
		/* 
		 * build page title
		 */
		final StringBuilder pageTitle = new StringBuilder("concept :: "); // TODO: i18n
		
		// if URI looks like concept/USER/USERNAME/TAGNAME, change GroupingEntity to USER
		if (present(requUser)) {
			groupingEntity = GroupingEntity.USER;
			groupingName = requUser;
			pageTitle.append(" user :: "); // TODO: i18n
			pageTitle.append(requUser).append(" :: ");
		}
		
		// if URI looks like concept/GROUP/GROUPNAME/TAGNAME, change GroupingEntity to GROUP 
		if (present(requGroup)) {
			groupingEntity = GroupingEntity.GROUP;
			groupingName = requGroup;
			pageTitle.append(" group :: "); // TODO: i18n
			pageTitle.append(requGroup).append(" :: ");
		}
		
		pageTitle.append(StringUtils.implodeStringCollection(requTags, " "));
		command.setPageTitle(pageTitle.toString());
		
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(command)) {
			this.setList(command, resourceType, groupingEntity, groupingName, requTags, null, null, null, null, command.getStartDate(), command.getEndDate(), command.getListCommand(resourceType).getEntriesPerPage());
			this.postProcessAndSortList(command, resourceType);
		}	
		
		// retrieve concepts
		final List<Tag> concepts = new ArrayList<Tag>();
		for (final String requTag : requTags) {
			final Tag conceptDetails = this.logic.getConceptDetails(requTag.substring(2), groupingEntity, groupingName);
			if (present(conceptDetails)){
				concepts.add(conceptDetails);
			}
		}
		
		if (present(concepts)) {
			command.getConcepts().setConceptList(concepts);
		}
		
		// html format - retrieve tags and return HTML view
		if ("html".equals(format)) {
			if (groupingEntity != GroupingEntity.ALL) {
				this.setTags(command, Resource.class, groupingEntity, groupingName, null, null, null, null, 1000, null);
			}
			
			this.endTiming();
			return Views.CONCEPTPAGE;
		}
		
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(format);
	}

	@Override
	public ConceptResourceViewCommand instantiateCommand() {
		return new ConceptResourceViewCommand();
	}
}
