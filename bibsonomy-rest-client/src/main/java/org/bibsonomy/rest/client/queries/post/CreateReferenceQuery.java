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

import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.util.StringUtils;

/**
 * @author wla
  */
public class CreateReferenceQuery extends AbstractQuery<String> {

	private final static String URL_PREFIX = "/posts/community/";
	private final static String URL_POSTFIX = "/references";

	private final String hash;
	private final String referenceHash;

	public CreateReferenceQuery(final String hash, final String referenceHash) {
		this.hash = hash;
		this.referenceHash = referenceHash;
	}

	@Override
	protected String doExecute() throws ErrorPerformingRequestException {
		final StringWriter sw = new StringWriter(100);
		this.getRenderer().serializeReference(sw, this.referenceHash);

		this.downloadedDocument = performRequest(HttpMethod.POST, URL_PREFIX + hash + URL_POSTFIX, StringUtils.toDefaultCharset(sw.toString()));

		return null;
	}

}
