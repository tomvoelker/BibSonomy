/**
 *  
 *  BibSonomy-Rest-Client - The REST-client.
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
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

package org.bibsonomy.rest.client.queries.get;

import org.bibsonomy.model.User;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.RendererFactory;

/**
 * Use this Class to receive details about an user of bibsonomy.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
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
		if (username == null || username.length() == 0) throw new IllegalArgumentException("no username given");

		this.username = username;
	}

	@Override
	public User getResult() throws BadRequestOrResponseException, IllegalStateException {
		if (this.downloadedDocument == null) throw new IllegalStateException("Execute the query first.");
		return RendererFactory.getRenderer(getRenderingFormat()).parseUser(this.downloadedDocument);
	}

	@Override
	protected User doExecute() throws ErrorPerformingRequestException {
		this.downloadedDocument = performGetRequest(URL_USERS + "/" + this.username + "?format=" + getRenderingFormat().toString().toLowerCase());
		return null;
	}
}