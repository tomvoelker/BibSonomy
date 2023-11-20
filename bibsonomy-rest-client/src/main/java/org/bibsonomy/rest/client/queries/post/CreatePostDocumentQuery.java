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
package org.bibsonomy.rest.client.queries.post;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.Document;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * @author wbi
 */
public class CreatePostDocumentQuery extends AbstractQuery<String> {

	private final Document document;
	private final String resourceHash;

	/**
	 * @param document
	 * @param resourceHash
	 */
	public CreatePostDocumentQuery(final Document document, final String resourceHash) {
		if (!present(document.getUserName())) throw new IllegalArgumentException("no username given");
		if (!present(document.getFile())) throw new IllegalArgumentException("no file given");
		if (!present(resourceHash)) throw new IllegalArgumentException("no resourceHash given");

		this.document = document;
		this.resourceHash = resourceHash;
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final String url = this.getUrlRenderer().createHrefForResourceDocuments(this.document.getUserName(), this.resourceHash);
		this.downloadedDocument = this.performMultipartPostRequest(url, this.document.getFile());
	}

	@Override
	protected String getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		if (this.isSuccess()) {
			return this.getRenderer().parseResourceHash(this.downloadedDocument);
		}
		return this.getError();
	}
}
