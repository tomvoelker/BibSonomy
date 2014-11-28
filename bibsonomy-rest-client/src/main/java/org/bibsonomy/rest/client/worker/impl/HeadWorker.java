/**
 * BibSonomy-Rest-Client - The REST-client.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.httpclient.methods.HeadMethod;
import org.bibsonomy.rest.auth.AuthenticationAccessor;
import org.bibsonomy.rest.client.worker.HttpWorker;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public class HeadWorker extends HttpWorker<HeadMethod> {

	/**
	 * 
	 * @param username
	 * @param apiKey
	 * @param accessor 
	 */
	public HeadWorker(final String username, final String apiKey, final AuthenticationAccessor accessor) {
		super(username, apiKey, accessor);
	}

	@Override
	protected HeadMethod getMethod(String url, String requestBody) {
		final HeadMethod head = new HeadMethod(url);
		head.setFollowRedirects(true);
		return head;
	}

	@Override
	protected Reader readResponse(HeadMethod method) throws IOException, ErrorPerformingRequestException {
		return new StringReader(method.getStatusText());
	}
}