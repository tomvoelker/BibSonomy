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

package org.bibsonomy.rest.client.queries.get;

import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.rest.renderer.UrlRenderer;

/**
 * Use this Class to receive information about a specific tag
 * 
 * @author Jens Illig <illig@innofinity.de>
 */
public final class GetTagDetailsQuery extends AbstractQuery<Tag> {
	private final String tagName;

	/**
	 * @param tagName the name of the tag
	 */
	public GetTagDetailsQuery(final String tagName) {
		this.tagName = tagName;
		this.downloadedDocument = null;
	}

	
	@Override
	public Tag getResult() throws BadRequestOrResponseException, IllegalStateException {
		if (this.downloadedDocument == null) throw new IllegalStateException("Execute the query first.");
		return this.getRenderer().parseTag(this.downloadedDocument);
	}

	@Override
	protected Tag doExecute() throws ErrorPerformingRequestException {
		this.downloadedDocument = performGetRequest((new UrlRenderer("").createHrefForTag(tagName)));
		return null;
	}
}