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

import org.bibsonomy.model.User;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.util.StringUtils;

/**
 * Use this Class to create a new user account in bibsonomy.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public final class CreateUserQuery extends AbstractQuery<String> {
	private final User user;

	/**
	 * Creates a new user account in bibsonomy.
	 * 
	 * @param user
	 *            the user to be created
	 * @throws IllegalArgumentException
	 *             if the user is null or the user has neither username nor
	 *             password specified.
	 */
	public CreateUserQuery(final User user) throws IllegalArgumentException {
		if (!present(user)) throw new IllegalArgumentException("no user specified");
		if (!present(user.getName())) throw new IllegalArgumentException("no username specified");
		if (!present(user.getPassword())) throw new IllegalArgumentException("no password specified");

		this.user = user;
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final StringWriter sw = new StringWriter(100);
		this.getRenderer().serializeUser(sw, this.user, null);
		this.downloadedDocument = performRequest(HttpMethod.POST, this.getUrlRenderer().createUrlBuilderForUsers().asString(), StringUtils.toDefaultCharset(sw.toString()));
	}
	
	@Override
	protected String getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		if (this.isSuccess()) {
			return this.getRenderer().parseUserId(this.downloadedDocument);
		}
		return this.getError();
	}
}