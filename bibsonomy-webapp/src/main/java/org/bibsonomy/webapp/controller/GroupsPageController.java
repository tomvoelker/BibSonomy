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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.enums.GroupSortKey;
import org.bibsonomy.model.logic.query.GroupQuery;
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

	private String defaultRealnameSearch;

	/**
	 * implementation of {@link MinimalisticController} interface
	 */
	@Override
	public View workOn(final GroupsListCommand command) {
		final String format = command.getFormat();
		final ListCommand<Group> groupListCommand = command.getGroups();
		groupListCommand.setEntriesPerPage(30);

		final boolean isOrganizationPage = command.isOrganizations();

		String userName = null;
		if (command.isMemberOfOnly() && command.getContext().isUserLoggedIn()) {
			// Restrict query to user's group/organization
			userName = command.getContext().getLoginUser().getName();
		}

		/*
		 * get requested groups
		 */
		final String search = command.getSearch();
		final boolean searchPresent = present(search);
		final boolean prefixPresent = present(command.getPrefix());
		final GroupSortKey sortKey = searchPresent ? GroupSortKey.RANK : GroupSortKey.GROUP_REALNAME;
		final SortOrder sortOrder = searchPresent ? SortOrder.DESC : SortOrder.ASC;

		// default realname search case for organizations
		final boolean useDefaultRealnameSearch = isOrganizationPage && !searchPresent && !prefixPresent && !command.isMemberOfOnly() && groupListCommand.getStart() == 0;
		if (useDefaultRealnameSearch && present(defaultRealnameSearch)) {
			ResultList<Group> groups = new ResultList<>();
			for (String groupName : defaultRealnameSearch.split(",")) {
				Group groupDetails = this.logic.getGroupDetails(groupName, false);
				if (present(groupDetails)) {
					groups.add(groupDetails);
				}
			}
			groups.setTotalCount(groups.size());
			groupListCommand.setList(groups);
		} else {
			final GroupQuery groupQuery = GroupQuery.builder()
					.search(search)
					.prefixMatch(true)
					.prefix(command.getPrefix())
					.userName(userName)
					.sortKey(sortKey)
					.sortOrder(sortOrder)
					.pending(false)
					.organization(isOrganizationPage)
					.entriesStartingAt(groupListCommand.getEntriesPerPage(), groupListCommand.getStart())
					.build();
			groupListCommand.setList(this.logic.getGroups(groupQuery));
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

	public void setDefaultRealnameSearch(String defaultRealnameSearch) {
		this.defaultRealnameSearch = defaultRealnameSearch;
	}
}
