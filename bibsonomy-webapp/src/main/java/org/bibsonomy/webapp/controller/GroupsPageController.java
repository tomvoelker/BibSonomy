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

import org.bibsonomy.model.Group;
import org.bibsonomy.model.enums.GroupOrder;
import org.bibsonomy.model.logic.querybuilder.GroupQueryBuilder;
import org.bibsonomy.webapp.command.GroupsListCommand;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for group overview:
 * - /groups
 * - /organizations (for cris systems)
 * 
 * @author Folke Eisterlehner
 */
public class GroupsPageController extends SingleResourceListController implements MinimalisticController<GroupsListCommand> {

	/**
	 * implementation of {@link MinimalisticController} interface
	 */
	@Override
	public View workOn(final GroupsListCommand command) {
		final String format = command.getFormat();
		final ListCommand<Group> groupListCommand = command.getGroups();
		/*
		 * get requested groups
		 */
		if ("html".equals(format)) {
			final GroupQueryBuilder builder = new GroupQueryBuilder();
			final int start = groupListCommand.getStart();
			final int end = start + groupListCommand.getEntriesPerPage();
			builder.setPending(false)
							.setStart(0).setEnd(end)
							.setOrganization(command.getOrganizations())
							.setPrefix(command.getPrefix())
							.setSearch(command.getSearch())
							.order(GroupOrder.GROUP_REALNAME);
			groupListCommand.setList(this.logic.getGroups(builder.createGroupQuery()));
		} else if ("json".equals(format)) {
			// FIXME:  why does changing the format change the result of the controller?
			groupListCommand.setList(command.getContext().getLoginUser().getGroups());
		}

		// html format - retrieve tags and return HTML view
		if ("html".equals(format)) {
			return Views.GROUPSPAGE;
		}

		// export - return the appropriate view
		return Views.getViewByFormat(format);
	}

	/**
	 * implementation of {@link MinimalisticController} interface
	 */
	@Override
	public GroupsListCommand instantiateCommand() {
		return new GroupsListCommand();
	}
}
