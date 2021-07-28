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
package org.bibsonomy.rest.client.queries.get;

import org.apache.http.HttpStatus;
import org.bibsonomy.common.exceptions.InvalidModelException;
import org.bibsonomy.model.User;
import org.bibsonomy.model.user.remote.SamlRemoteUserId;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

import java.util.List;

/**
 * get the user with the provided saml id
 * 
 * @author mho
 */
public final class GetUserBySamlUserIdQuery extends AbstractQuery<String> {

	private SamlRemoteUserId samlRemoteUserId;

	/**
	 * Gets bibsonomy's user list
	 */
	public GetUserBySamlUserIdQuery(SamlRemoteUserId samlRemoteUserId) {
		this.samlRemoteUserId = samlRemoteUserId;
	}

	@Override
	protected String getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		if (this.getHttpStatusCode() == HttpStatus.SC_NOT_FOUND) {
			return null;
		}
		try {
			List<User> userList = this.getRenderer().parseUserList(this.downloadedDocument);

			if (!userList.isEmpty()) {
				return userList.get(0).getName();
			}
		} catch (BadRequestOrResponseException | InvalidModelException ex) {
			// this means, that no user has been found and we can return null
			return null;
		}

		return null;
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final String usersUrl = this.getUrlRenderer().createUrlBuilderForUsers()
				.addParameter(RESTConfig.REMOTE_USER_ID, this.samlRemoteUserId.getUserId())
				.addParameter(RESTConfig.IDENTITY_PROVIDER, this.samlRemoteUserId.getIdentityProviderId())
		.asString();

		this.downloadedDocument = performGetRequest(usersUrl);
	}
}