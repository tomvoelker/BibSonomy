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

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.resource.PublicationPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.security.access.AccessDeniedException;

/**
 * Controller for the clipboard page
 * - /clipboard
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 */
public class ClipboardPageController extends SingleResourceListController implements MinimalisticController<PublicationPageCommand> {

	@Override
	public View workOn(final PublicationPageCommand command) {
		final String format = command.getFormat();
		this.startTiming(format);

		// if user is not logged in, redirect him to login page
		if (!command.getContext().isUserLoggedIn()) {
			throw new AccessDeniedException("please log in");
		}

		// set login user name + grouping entity = CLIPBOARD
		final String loginUserName = command.getContext().getLoginUser().getName();
		final GroupingEntity groupingEntity = GroupingEntity.CLIPBOARD;

		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(command)) {
			final ListCommand<?> listCommand = command.getListCommand(resourceType);
			final int entriesPerPage = listCommand.getEntriesPerPage();
			this.setList(command, resourceType, groupingEntity, loginUserName, null, null, null, null, null, null, null, entriesPerPage);
			this.postProcessAndSortList(command, resourceType);

			/*
			 * set all posts from clipboard page to "picked" such that their "pick"
			 * link changes to "unpick"
			 */
			for (final Post<? extends Resource> post : command.getListCommand(resourceType).getList()){
				post.setPicked(true);
			}

			// the number of items in this user's clipboard has already been fetched
			command.getListCommand(resourceType).setTotalCount(command.getContext().getLoginUser().getClipboard().getNumPosts());
		}	

		this.endTiming();
		if ("html".equals(format)) {
			return Views.CLIPBOARDPAGE;
		}

		// export - return the appropriate view
		return Views.getViewByFormat(format);

	}

	@Override
	public PublicationPageCommand instantiateCommand() {
		return new PublicationPageCommand();
	}
}
