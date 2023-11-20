/**
 * BibSonomy-Rest-Client - The REST-client.
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
package org.bibsonomy.rest.client.queries.delete;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.rest.client.AbstractDeleteQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * Use this Class to delete a specified post.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public final class DeletePostQuery extends AbstractDeleteQuery {
	private final String userName;
	private final String resourceHash;

	/**
	 * Deletes a post.
	 * 
	 * @param userName
	 *            the userName owning the post to deleted
	 * @param resourceHash
	 *            hash of the resource connected to the post
	 * @throws IllegalArgumentException
	 *             if userName or groupName are null or empty
	 */
	public DeletePostQuery(final String userName, final String resourceHash) throws IllegalArgumentException {
		if (!present(resourceHash)) throw new IllegalArgumentException("no resourcehash given");

		this.userName = userName;
		this.resourceHash = resourceHash;
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final String resourceUrl = this.getUrlRenderer().createHrefForResource(this.userName, this.resourceHash);
		this.downloadedDocument = performRequest(HttpMethod.DELETE, resourceUrl, null);
	}
}