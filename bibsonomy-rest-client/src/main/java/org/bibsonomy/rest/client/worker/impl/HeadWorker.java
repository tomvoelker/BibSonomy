/**
 *  
 *  BibSonomy-Rest-Client - The REST-client.
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
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

import org.apache.commons.httpclient.methods.HeadMethod;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.worker.HttpWorker;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class HeadWorker extends HttpWorker {

	public HeadWorker(final String username, final String apiKey) {
		super(username, apiKey);
	}

	public Reader perform(final String url) throws ErrorPerformingRequestException {
		LOGGER.debug("HEAD: URL: " + url);
		
		// dirty but working
		if (this.proxyHost != null){
			getHttpClient().getHostConfiguration().setProxy(this.proxyHost, this.proxyPort);
		}

		final HeadMethod head = new HeadMethod(url);
		head.addRequestHeader(HEADER_AUTHORIZATION, encodeForAuthorization());
		head.setDoAuthentication(true);
		head.setFollowRedirects(true);

		try {
			this.httpResult = getHttpClient().executeMethod(head);
			LOGGER.debug("Result: " + this.httpResult);
			return new StringReader( head.getStatusText() );
		} catch (final IOException e) {
			LOGGER.debug(e.getMessage(), e);
			throw new ErrorPerformingRequestException(e);
		} finally {
			head.releaseConnection();
		}
	}
}