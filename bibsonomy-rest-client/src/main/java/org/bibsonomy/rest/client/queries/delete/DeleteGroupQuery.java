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
 * Use this Class to delete a specified group.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public final class DeleteGroupQuery extends AbstractDeleteQuery {
	private final String groupName;

	/**
	 * Deletes the specified group.
	 * 
	 * @param groupName
	 *            the groupName of the group to be deleted
	 * @throws IllegalArgumentException
	 *             if the groupName is null or empty
	 */
	public DeleteGroupQuery(final String groupName) throws IllegalArgumentException {
		if (!present(groupName)) throw new IllegalArgumentException("no groupname given");
		this.groupName = groupName;
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		this.downloadedDocument = performRequest(HttpMethod.DELETE, this.getUrlRenderer().createHrefForGroup(this.groupName), null);
	}
}