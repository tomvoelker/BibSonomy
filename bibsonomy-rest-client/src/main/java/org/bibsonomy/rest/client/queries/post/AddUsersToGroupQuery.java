/**
 *
 *  BibSonomy-Rest-Client - The REST-client.
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.rest.client.queries.post;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.StringWriter;
import java.util.List;

import org.bibsonomy.common.enums.Status;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * Use this Class to add an user to an already existing group.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
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
	 * @param users
	 *            the users to be added
	 * @throws IllegalArgumentException
	 *             if the group name is null or empty, or if the user is null or
	 *             has no name defined
	 */
	public AddUsersToGroupQuery(final String groupName, final List<User> users) throws IllegalArgumentException {
		if (!present(groupName)) throw new IllegalArgumentException("no groupName given");
		if (!present(users)) throw new IllegalArgumentException("no users specified");
	
		this.groupName = groupName;
		this.users = users;
	}
	
	@Override
	protected String doExecute() throws ErrorPerformingRequestException {
		final StringWriter sw = new StringWriter(100);
		this.getRenderer().serializeUsers(sw, this.users, null);
		this.downloadedDocument = performRequest(HttpMethod.POST, RESTConfig.GROUPS_URL + "/" + this.groupName + "/" + RESTConfig.USERS_URL, sw.toString());
		return null;
	}
	
	@Override
	public String getResult() throws BadRequestOrResponseException, IllegalStateException {				
		if (this.isSuccess())
			return Status.OK.getMessage();
		return this.getError();
	}
}