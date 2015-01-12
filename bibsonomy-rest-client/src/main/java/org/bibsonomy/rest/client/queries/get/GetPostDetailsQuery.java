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

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.util.data.NoDataAccessor;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * Use this Class to receive details about a post of an user.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public final class GetPostDetailsQuery extends AbstractQuery<Post<? extends Resource>> {

	private final String username;
	private final String resourceHash;

	/**
	 * Gets details of a post of an user.
	 * 
	 * @param username
	 *            name of the user
	 * @param resourceHash
	 *            hash of the resource
	 * @throws IllegalArgumentException
	 *             if userName or resourceHash are null or empty
	 */
	public GetPostDetailsQuery(final String username, final String resourceHash) throws IllegalArgumentException {
		if (!present(username)) throw new IllegalArgumentException("no username given");
		if (!present(resourceHash)) throw new IllegalArgumentException("no resourceHash given");

		this.username = username;
		this.resourceHash = resourceHash;
	}

	@Override
	protected Post<? extends Resource> getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		return this.getRenderer().parsePost(this.downloadedDocument, NoDataAccessor.getInstance());
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final String url = this.getUrlRenderer().createHrefForResource(this.username, this.resourceHash);
		this.downloadedDocument = performGetRequest(url);
	}
}