/**
 *  
 *  BibSonomy-Rest-Client - The REST-client.
 *   
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group, 
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

package org.bibsonomy.rest.client.worker.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.worker.HttpWorker;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class PutWorker extends HttpWorker {

	public PutWorker(final String username, final String apiKey) {
		super(username, apiKey);
	}

	public Reader perform(final String url, final String requestBody) throws ErrorPerformingRequestException {
		LOGGER.debug("PUT: URL: " + url);
		
		// dirty but working
		if (this.proxyHost != null){
			getHttpClient().getHostConfiguration().setProxy(this.proxyHost, this.proxyPort);
		}

		final PutMethod put = new PutMethod(url);
		put.addRequestHeader(HEADER_AUTHORIZATION, encodeForAuthorization());
		put.setDoAuthentication(true);
		put.setFollowRedirects(false);

		put.setRequestEntity(new StringRequestEntity(requestBody));

		try {
			this.httpResult = getHttpClient().executeMethod(put);
			LOGGER.debug("Result: " + this.httpResult);
			LOGGER.debug("XML response:\n" + put.getResponseBodyAsString());
			LOGGER.debug("===================================================");			
			return new StringReader(put.getResponseBodyAsString());
		} catch (final IOException e) {
			LOGGER.debug(e.getMessage(), e);
			throw new ErrorPerformingRequestException(e);
		} finally {
			put.releaseConnection();
		}
	}
}