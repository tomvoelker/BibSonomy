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
package org.bibsonomy.webapp.command;

import org.bibsonomy.model.Group;

/**
 * command for list of groups.
 * 
 * @author Folke Eisterlehner
 */
public class GroupsListCommand extends EntitySearchAndFilterCommand {
	private final ListCommand<Group> groups = new ListCommand<>(this);

	/** stores the data if a new group is requested. */
	private Group requestedGroup;

	/** filter for only showing organizations */
	private Boolean organizations;

	/** filter for only retrieving groups/organizations the user is a member of */
	private Boolean memberOfOnly;

	/**
	 * @return the groups
	 */
	public ListCommand<Group> getGroups() {
		return this.groups;
	}

	/**
	 * @return the requestedGroup
	 */
	public Group getRequestedGroup() {
		return this.requestedGroup;
	}

	/**
	 * @param requestedGroup the requestedGroup to set
	 */
	public void setRequestedGroup(Group requestedGroup) {
		this.requestedGroup = requestedGroup;
	}

	/**
	 * @return the organizations
	 */
	public Boolean getOrganizations() {
		return organizations;
	}

	/**
	 * @param organizations the organizations to set
	 */
	public void setOrganizations(Boolean organizations) {
		this.organizations = organizations;
	}

	/**
	 * @return the member only filter flag
	 */
	public Boolean isMemberOfOnly() {
		return memberOfOnly;
	}

	/**
	 * @param memberOfOnly the member only flag to set
	 */
	public void setMemberOfOnly(Boolean memberOfOnly) {
		this.memberOfOnly = memberOfOnly;
	}
}