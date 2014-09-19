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

import java.io.StringWriter;

import org.bibsonomy.model.enums.GoldStandardRelation;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.util.StringUtils;

/**
 * @author wla
 */
public class CreateRelationQuery extends AbstractQuery<String> {
	private final String hash;
	private final String otherHashString;
	private final GoldStandardRelation relation;
	
	/**
	 * 
	 * @param hash the hash of the community post
	 * @param otherHash the target hash of the other community post
	 * @param relation  the relation between the community post and the reference hash
	 */
	public CreateRelationQuery(final String hash, final String otherHash,final GoldStandardRelation relation) {
		this.hash = hash;
		this.otherHashString = otherHash;
		this.relation = relation;
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final StringWriter sw = new StringWriter(100);
		this.getRenderer().serializeReference(sw, this.otherHashString);
		final String url = this.getUrlRenderer().createHrefForCommunityPostReferences(this.hash, this.relation);
		this.downloadedDocument = performRequest(HttpMethod.POST, url, StringUtils.toDefaultCharset(sw.toString()));
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.client.AbstractQuery#getResultInternal()
	 */
	@Override
	protected String getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

}
