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
package org.bibsonomy.webapp.command.admin;

import java.util.LinkedList;
import java.util.List;
import org.bibsonomy.common.enums.AdminGroupOperation;
import org.bibsonomy.model.Group;
import org.bibsonomy.webapp.command.BaseCommand;


/**
 * Command bean for admin page 
 * 
 * @author bsc
 */
public class AdminGroupViewCommand extends BaseCommand {
	
	/** specific action for admin page */
	private AdminGroupOperation action;
	
	private String adminResponse;
	private Group group;
	
	@Deprecated
	// TODO a more general field should be Set<GroupLevelPermission>
	private boolean communityPostInspectionPermission;
	private boolean permissionsUpdated;
	/**
	 * list of pending groups
	 */
	private List<Group> pendingGroups;
	
	public AdminGroupViewCommand() {
		this.group = new Group();
	}

	/**
	 * @return the group
	 */
	public Group getGroup() {
		return this.group;
	}

	/**
	 * @param group the group to set 
	 */
	public void setGroup(final Group group) {
		this.group = group;
	}

	/**
	 * @return the action
	 */
	public AdminGroupOperation getAction() {
		return this.action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(final AdminGroupOperation action) {
		this.action = action;
	}

	/**
	 * @param adminResponse
	 */
	public void setAdminResponse(final String adminResponse) {
		this.adminResponse = adminResponse;
	}

	/**
	 * @return the admin response
	 */
	public String getAdminResponse() {
		return adminResponse;
	}

	/**
	 * @return the pendingGroups
	 */
	public List<Group> getPendingGroups() {
		if (this.pendingGroups == null)
			this.pendingGroups = new LinkedList<Group>();
		return this.pendingGroups;
	}

	/**
	 * @param pendingGroups the pendingGroups to set
	 */
	public void setPendingGroups(List<Group> pendingGroups) {
		this.pendingGroups = pendingGroups;
	}

	@Deprecated
	public boolean isCommunityPostInspectionPermission() {
		return this.communityPostInspectionPermission;
	}

	@Deprecated
	public void setCommunityPostInspectionPermission(boolean communityPostInspectionPermission) {
		this.communityPostInspectionPermission = communityPostInspectionPermission;
	}

	public boolean isPermissionsUpdated() {
		return this.permissionsUpdated;
	}

	public void setPermissionsUpdated(boolean permissionsUpdated) {
		this.permissionsUpdated = permissionsUpdated;
	}
}
