/**
 * BibSonomy-Database - Database for BibSonomy.
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.database.params;

import org.bibsonomy.common.enums.GroupLevelPermission;
import org.bibsonomy.model.GroupMembership;

/**
 * Parameters that are specific for groups.
 *
 * @author Christian Schenk
 */
public class GroupParam extends GenericParam {
	
	private GroupMembership membership;
	
	private GroupLevelPermission groupLevelPermission;
	
	private String grantedByUser;
	
	public GroupParam() {
		
	}

	public GroupMembership getMembership() {
		return membership;
	}

	public void setMembership(GroupMembership membership) {
		this.membership = membership;
	}

	public GroupLevelPermission getGroupLevelPermission() {
		return this.groupLevelPermission;
	}

	public void setGroupLevelPermission(GroupLevelPermission groupLevelPermission) {
		this.groupLevelPermission = groupLevelPermission;
	}

	public String getGrantedByUser() {
		return this.grantedByUser;
	}

	public void setGrantedByUser(String grantedByUser) {
		this.grantedByUser = grantedByUser;
	}
	
}