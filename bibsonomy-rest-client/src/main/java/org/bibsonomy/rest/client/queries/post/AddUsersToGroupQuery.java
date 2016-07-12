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
package org.bibsonomy.rest.client.queries.post;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.enums.Status;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * Use this Class to add an user to an already existing group.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public final class AddUsersToGroupQuery extends AbstractQuery<String> {
	private final List<User> users;
	private final String groupName;

	/**
	 * Adds an user to an already existing group. <p/>note that the user and the
	 * group must exist before this query can be performed
	 * 
	 * @param groupName
	 *            name of the group the user is to be added to. the group must
	 *            exist, else a {@link IllegalArgumentException} is thrown
	 * @param memberships
	 *            the memberships to be added
	 * @throws IllegalArgumentException
	 *             if the group name is null or empty, or if the user is null or
	 *             has no name defined
	 */
	public AddUsersToGroupQuery(final String groupName, final List<GroupMembership> memberships) throws IllegalArgumentException {
		if (!present(groupName)) throw new IllegalArgumentException("no groupName given");
		if (!present(memberships)) throw new IllegalArgumentException("no membership relation specified");
		
		this.groupName = groupName;
		this.users = new LinkedList<>();
		for (GroupMembership ms : memberships) {
			this.users.add(ms.getUser());
		}
		
	}
	
	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final StringWriter sw = new StringWriter(100);
		this.getRenderer().serializeUsers(sw, this.users, null);
		final String groupUrl = this.getUrlRenderer().createHrefForGroupMembers(this.groupName);
		this.downloadedDocument = performRequest(HttpMethod.POST, groupUrl, sw.toString());
	}
	
	@Override
	protected String getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		if (this.isSuccess()) {
			return Status.OK.getMessage();
		}
		return this.getError();
	}
}