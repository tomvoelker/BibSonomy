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

import java.util.List;

import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * Returns a list of users which either the requested user has in his friend list,
 * or all users, which have the requested user in his friend list.
 * 
 * TODO: Should be replaced by a generic strategy which allows to fetch users
 *  based on the different {@link UserRelation}s.
 * 
 */
@Deprecated
public final class GetFriendsQuery extends AbstractQuery<List<User>> {

	private final int start;
	private final int end;
	private final String relation;
	private final String username;

	/**
	 * Gets bibsonomy's user list.
	 * @param username 
	 * @param relation 
	 * @param start
	 *            start of the list
	 * @param end
	 *            end of the list
	 */
	public GetFriendsQuery(final String username, String relation, int start, int end) {
		if (start < 0) start = 0;
		if (end < start) end = start;
		
		this.username = username;

		if (relation == null) {
			relation = RESTConfig.DEFAULT_ATTRIBUTE_VALUE_RELATION;
		}
		
		this.relation = relation;
		this.start = start;
		this.end = end;
	}

	@Override
	protected List<User> getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		return this.getRenderer().parseUserList(this.downloadedDocument);
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final String friendsUrl = this.getUrlRenderer().createHrefForFriends(this.username, this.relation, this.start, this.end);
		this.downloadedDocument = performGetRequest(friendsUrl);
	}
}