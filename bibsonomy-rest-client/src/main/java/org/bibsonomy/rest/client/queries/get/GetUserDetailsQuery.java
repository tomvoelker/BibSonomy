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

import org.bibsonomy.model.User;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * Use this Class to receive details about an user of bibsonomy.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public final class GetUserDetailsQuery extends AbstractQuery<User> {

	private final String username;

	/**
	 * Gets details of a user.
	 * 
	 * @param username
	 *            name of the user
	 * @throws IllegalArgumentException
	 *             if username is null or empty
	 */
	public GetUserDetailsQuery(final String username) throws IllegalArgumentException {
		if (!present(username)) throw new IllegalArgumentException("no username given");
		this.username = username;
	}

	@Override
	protected User getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		return this.getRenderer().parseUser(this.downloadedDocument);
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		this.downloadedDocument = performGetRequest(this.getUrlRenderer().createHrefForUser(this.username));
	}
}