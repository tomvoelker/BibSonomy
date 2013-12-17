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

package org.bibsonomy.rest.client.queries.post;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.File;

import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.util.UrlBuilder;

/**
 * @author wbi
 */
public class CreatePostDocumentQuery extends AbstractQuery<String> {

	private final File file;
	private final String username;
	private final String resourceHash;

	/**
	 * @param username
	 * @param resourceHash
	 * @param file
	 */
	public CreatePostDocumentQuery(final String username, final String resourceHash, final File file) {
		if (!present(username)) throw new IllegalArgumentException("no username given");
		if (!present(resourceHash)) throw new IllegalArgumentException("no resourceHash given");

		this.username = username;
		this.resourceHash = resourceHash;

		this.file = file;
	}

	@Override
	protected String doExecute() throws ErrorPerformingRequestException {
		final String url = new UrlBuilder(RESTConfig.USERS_URL).addPathElement(this.username).addPathElement("posts").addPathElement(this.resourceHash).addPathElement("documents").asString();
		this.downloadedDocument = this.performMultipartPostRequest(url, this.file);
		return null;
	}

	@Override
	public String getResult() throws BadRequestOrResponseException, IllegalStateException {
		if (this.isSuccess()) {
			return this.getRenderer().parseResourceHash(this.downloadedDocument);
		}
		return this.getError();
	}	
}
