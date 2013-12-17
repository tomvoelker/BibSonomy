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

package org.bibsonomy.rest.client.queries.put;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.StringWriter;

import org.bibsonomy.model.Document;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.rest.renderer.UrlRenderer;


/**
 * query to rename a document
 * 
 * @author dzo
 */
public class ChangeDocumentNameQuery extends AbstractQuery<String> {
	private String resourceHash;
	private String newFileName;
	
	private Document oldDocument;

	/**
	 * 
	 * @param resourceHash
	 * @param newFileName
	 * @param oldDocument
	 */
	public ChangeDocumentNameQuery(String resourceHash, String newFileName, Document oldDocument) {
		if (!present(newFileName)) throw new IllegalArgumentException("no new name given");
		if (!present(resourceHash)) throw new IllegalArgumentException("no resourceHash given");
		if (!present(oldDocument) || !present(oldDocument.getFileName())) throw new IllegalStateException("no old document name given");
		if (!present(oldDocument.getUserName())) throw new IllegalStateException("no user name given");
		this.resourceHash = resourceHash;
		this.newFileName = newFileName;
		this.oldDocument = oldDocument;
	}

	@Override
	protected String doExecute() throws ErrorPerformingRequestException {
		final Document newDocument = new Document();
		newDocument.setFileName(this.newFileName);
		
		final StringWriter sw = new StringWriter(100);
		this.getRenderer().serializeDocument(sw, newDocument);
		// TODO: use the urlrenderer of this query!
		final String url = new UrlRenderer("").createHrefForResourceDocument(oldDocument.getUserName(), this.resourceHash, this.oldDocument.getFileName());
		this.performRequest(HttpMethod.PUT, url, sw.toString());
		return null;
	}
	
	@Override
	public String getResult() throws BadRequestOrResponseException, IllegalStateException {
		if (this.isSuccess())
			return this.getRenderer().parseResourceHash(this.downloadedDocument);
		return this.getError();
	}
}
