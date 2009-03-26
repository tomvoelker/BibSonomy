/**
 *  
 *  BibSonomy-Rest-Client - The REST-client.
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
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
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Level;

import org.apache.commons.httpclient.ProxyHost;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.bibsonomy.rest.client.ProgressCallback;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.worker.HttpWorker;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class DeleteWorker extends HttpWorker {

	public DeleteWorker(final String username, final String apiKey, final String proxyHost, final int proxyPort) {
		super(username, apiKey, proxyHost ,proxyPort);
	}

	public Reader perform(final String url) throws ErrorPerformingRequestException {
		LOGGER.debug("DELETE: URL: " + url);

		final DeleteMethod delete = new DeleteMethod(url);
		delete.addRequestHeader(HEADER_AUTHORIZATION, encodeForAuthorization());
		delete.setDoAuthentication(true);
		delete.setFollowRedirects(true);

		try {
			this.httpResult = getHttpClient().executeMethod(delete);
			LOGGER.debug("HTTP result: " + this.httpResult);
			LOGGER.debug("XML response:\n" + delete.getResponseBodyAsString());
			LOGGER.debug("===================================================");
			return new StringReader(delete.getResponseBodyAsString());
		} catch (final IOException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ErrorPerformingRequestException(e);
		} finally {
			delete.releaseConnection();
		}
	}
}