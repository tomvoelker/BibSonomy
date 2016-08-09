/**
 * BibSonomy-Rest-Client - The REST-client.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.rest.client.worker.impl;

import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.bibsonomy.rest.auth.AuthenticationAccessor;
import org.bibsonomy.rest.client.util.RestClientUtils;
import org.bibsonomy.rest.client.worker.HttpWorker;

/**
 * TODO: merge duplicate code with PostWorker
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public final class PutWorker extends HttpWorker<PutMethod> {

	private static final String CONTENT_TYPE = "multipart/form-data";
	
	/**
	 * @param username
	 * @param apiKey
	 * @param accessor 
	 */
	public PutWorker(final String username, final String apiKey, final AuthenticationAccessor accessor) {
		super(username, apiKey, accessor);
	}

	@Override
	protected PutMethod getMethod(String url, String requestBody) {
		final PutMethod put = new PutMethod(url);
		put.setFollowRedirects(false);

		try {
			put.setRequestEntity(new StringRequestEntity(requestBody, CONTENT_TYPE, RestClientUtils.CONTENT_CHARSET));
		} catch (final UnsupportedEncodingException ex) {
			LOGGER.fatal("Could not encode request entity to UTF-8", ex);
			throw new RuntimeException(ex);
		}
		return put;
	}
}