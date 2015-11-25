/**
 * BibSonomy-Rest-Client - The REST-client.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.rest.client.queries.put;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.StringWriter;

import org.bibsonomy.model.Group;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * Use this Class to change details of an existing group in bibsonomy.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public final class ChangeGroupQuery extends AbstractQuery<String> {
	private final Group group;
	private final String groupName;

	/**
	 * Changes details of an existing group in bibsonomy.
	 * 
	 * @param groupName
	 *            name of the group to be changed
	 * @param group
	 *            new values
	 * @throws IllegalArgumentException
	 *             if groupname is null or empty, or if the group has no name
	 *             specified
	 */
	public ChangeGroupQuery(final String groupName, final Group group) throws IllegalArgumentException {
		if (!present(groupName)) throw new IllegalArgumentException("no groupName given");
		if (!present(group)) throw new IllegalArgumentException("no group specified");
		if (!present(group.getName())) throw new IllegalArgumentException("no groupname specified");

		this.groupName = groupName;
		this.group = group;
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final StringWriter sw = new StringWriter(100);
		this.getRenderer().serializeGroup(sw, group, null);
		final String groupUrl = this.getUrlRenderer().createHrefForGroup(this.groupName);
		this.downloadedDocument = performRequest(HttpMethod.PUT, groupUrl, sw.toString());
	}
	
	@Override
	protected String getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		if (this.isSuccess()) {
			return this.getRenderer().parseGroupId(this.downloadedDocument);
		}
		return this.getError();
	}
}