/**
 *
 *  BibSonomy-Rest-Client - The REST-client.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
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

package org.bibsonomy.rest.client.queries.delete;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.rest.client.AbstractDeleteQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * Use this Class to delete a specified user.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public final class DeleteUserQuery extends AbstractDeleteQuery {
	private final String userName;

	/**
	 * Deletes an account of a user.
	 * 
	 * @param userName
	 *            the userName of the user to be deleted
	 * @throws IllegalArgumentException
	 *             if userName is null or empty
	 */
	public DeleteUserQuery(final String userName) throws IllegalArgumentException {
		if (!present(userName)) throw new IllegalArgumentException("no username given");
		this.userName = userName;
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final String userUrl = this.getUrlRenderer().createHrefForUser(this.userName);
		this.downloadedDocument = performRequest(HttpMethod.DELETE, userUrl, null);
	}
}