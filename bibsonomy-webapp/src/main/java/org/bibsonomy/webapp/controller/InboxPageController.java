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

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.UserResourceViewCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.Errors;

/**
 * Controller for the InboxPage (shows all posts in your inbox)
 * 
 * @author sdo
 */
public class InboxPageController extends SingleResourceListController implements MinimalisticController<UserResourceViewCommand>, ErrorAware {
	private Errors errors;

	@Override
	public View workOn(final UserResourceViewCommand command) {
		// user has to be logged in
		if (!command.getContext().isUserLoggedIn()){
			throw new AccessDeniedException("please log in");
		}
		
		final String format = command.getFormat();
		this.startTiming(format);
				
		final String loginUserName = command.getContext().getLoginUser().getName();
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(format, command.getResourcetype())) {
			final int entriesPerPage = command.getListCommand(resourceType).getEntriesPerPage();
			this.setList(command, resourceType, GroupingEntity.INBOX, loginUserName, null, null, null, null, null, command.getStartDate(), command.getEndDate(), entriesPerPage);
			postProcessAndSortList(command, resourceType);
			/* 
			 * TODO: move to ibatis result mapping!
			 * mark all posts to be inbox posts (such that the "remove" link appears 
			 */
			for (final Post<? extends Resource> post: command.getListCommand(resourceType).getList()){
				post.setInboxPost(true);
			}
			// the number of items in this user's inbox has already been fetched
			this.setTotalCount(command, resourceType, GroupingEntity.INBOX, loginUserName, null, null, null, null, null, command.getStartDate(), command.getEndDate(), entriesPerPage);
		}
		this.endTiming();

		// html format - retrieve tags and return HTML view
		if ("html".equals(format)) {
			return Views.INBOX;		
		}

		// export - return the appropriate view
		return Views.getViewByFormat(format);	
	}

	@Override
	public UserResourceViewCommand instantiateCommand() {
		return new UserResourceViewCommand();
	}

	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

}
