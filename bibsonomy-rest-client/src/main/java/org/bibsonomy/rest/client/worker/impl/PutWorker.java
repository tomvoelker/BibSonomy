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

import org.apache.http.client.methods.HttpPut;
import org.bibsonomy.rest.client.auth.AuthenticationAccessor;
import org.bibsonomy.rest.client.util.RestClientUtils;
import org.bibsonomy.rest.client.worker.HttpWorker;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public final class PutWorker extends HttpWorker<HttpPut> {
	/**
	 * @param username
	 * @param apiKey
	 * @param accessor 
	 */
	public PutWorker(final String username, final String apiKey, final AuthenticationAccessor accessor) {
		super(username, apiKey, accessor);
	}

	@Override
	protected HttpPut getMethod(String url, String requestBody) {
		final HttpPut put = new HttpPut(url);
		RestClientUtils.prepareHttpMethod(put, requestBody);
		return put;
	}
}