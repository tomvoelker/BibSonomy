/**
 * BibSonomy-Rest-Client - The REST-client.
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
package org.bibsonomy.rest.client.queries.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.model.User;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * Use this Class to receive an ordered list of all users belonging to a given
 * group.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public final class GetUserListOfGroupQuery extends AbstractQuery<List<User>> {

	private final String groupname;
	private final int start;
	private final int end;

	/**
	 * Gets an user list of a group
	 * @param groupname the name of the group
	 */
	public GetUserListOfGroupQuery(final String groupname) {
		this(groupname, 0, 19);
	}

	/**
	 * Gets an user list of a group.
	 * @param groupname the name of the group
	 * 
	 * @param start
	 *            start of the list
	 * @param end
	 *            end of the list
	 * @throws IllegalArgumentException
	 *             if the groupname is null or empty
	 */
	public GetUserListOfGroupQuery(final String groupname, int start, int end) throws IllegalArgumentException {
		if (!present(groupname)) throw new IllegalArgumentException("no groupname given");
		if (start < 0) start = 0;
		if (end < start) end = start;

		this.groupname = groupname;
		this.start = start;
		this.end = end;
	}

	@Override
	protected List<User> getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		return this.getRenderer().parseUserList(this.downloadedDocument);
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final String membersUrl = this.getUrlRenderer().createHrefForGroupMembers(this.groupname, this.start, this.end);
		this.downloadedDocument = performGetRequest(membersUrl);
	}
}