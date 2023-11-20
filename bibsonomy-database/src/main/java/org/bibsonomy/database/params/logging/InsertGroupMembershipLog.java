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
package org.bibsonomy.database.params.logging;

import org.bibsonomy.database.common.enums.LogReason;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;

import java.util.Date;

/**
 * parameter class for logging a group membership
 *
 * @author ada
 */
public class InsertGroupMembershipLog extends LoggingInfoTrait {

	private final String username;
	private final Group group;

	public InsertGroupMembershipLog(User loggedUser, Date loggedTimestamp, String username, Group group, LogReason logReason) {
		super(loggedUser, loggedTimestamp, logReason);

		this.group = group;
		this.username = username;
	}

	public InsertGroupMembershipLog(User loggedUser, String username, Group group, LogReason logReason) {
		super(loggedUser, logReason);
		this.group = group;
		this.username = username;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the group
	 */
	public Group getGroup() {
		return group;
	}
}
