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
 * @author MarcelM
 */
public class DeletePostDocumentQuery extends AbstractDeleteQuery {
	
	private final String userName;
	private final String resourceHash;
	private final String fileName;
	
	/**
	 * Deletes a document from a post.
	 * 
	 * @param userName the user name owning the document/post
	 * @param resourceHash hash connected to the post
	 * @param fileName the fileName of the document 
	 */
	public DeletePostDocumentQuery(final String userName, final String resourceHash, final String fileName) {
		if (!present(userName)) throw new IllegalArgumentException("no username given");
		if (!present(resourceHash)) throw new IllegalArgumentException("no resourcehash given");
		if (!present(fileName)) throw new IllegalArgumentException("no file name given");
		
		this.userName = userName;
		this.resourceHash = resourceHash;
		this.fileName = fileName;
	}
	
	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final String documentUrl = this.getUrlRenderer().createHrefForResourceDocument(this.userName, this.resourceHash, this.fileName);
		this.downloadedDocument = performRequest(HttpMethod.DELETE, documentUrl, null); 
	}
}
