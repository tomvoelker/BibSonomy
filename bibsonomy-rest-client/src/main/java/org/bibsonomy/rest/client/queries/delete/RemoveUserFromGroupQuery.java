/**
 * BibSonomy-Rest-Client - The REST-client.
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
package org.bibsonomy.rest.client.queries.delete;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.rest.client.AbstractDeleteQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * Use this Class to remove an user from a group.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public final class RemoveUserFromGroupQuery extends AbstractDeleteQuery {
	private final String userName;
	private final String groupName;

	/**
	 * Remove an user from a group.
	 * 
	 * @param userName
	 *            the userName to be removed from the group
	 * @param groupName
	 *            group from which the user is to be removed
	 * @throws IllegalArgumentException
	 *             if userName or groupName are null or empty
	 */
	public RemoveUserFromGroupQuery(final String userName, final String groupName) throws IllegalArgumentException {
		if (!present(userName)) throw new IllegalArgumentException("no username given");
		if (!present(groupName)) throw new IllegalArgumentException("no groupname given");

		this.userName = userName;
		this.groupName = groupName;
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final String groupMemberUrl = this.getUrlRenderer().createHrefForGroupMember(this.groupName, this.userName);
		this.downloadedDocument = performRequest(HttpMethod.DELETE, groupMemberUrl, null);
	}
}