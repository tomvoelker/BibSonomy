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

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.bibsonomy.rest.client.auth.AuthenticationAccessor;
import org.bibsonomy.rest.client.util.ProgressCallback;
import org.bibsonomy.rest.client.worker.HttpWorker;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.rest.utils.HeaderUtils;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public final class GetWorker extends HttpWorker<HttpGet> {
	
	private final ProgressCallback callback;

	/**
	 * 
	 * @param username	the username
	 * @param password	the password (apiKey)
	 * @param accessor	the accessor
	 * @param callback	the callback
	 */
	public GetWorker(final String username, final String password, final AuthenticationAccessor accessor, final ProgressCallback callback) {
		super(username, password, accessor);
		
		this.callback = callback;
	}
	
	@Override
	protected HttpGet getMethod(final String url, final String requestBody) {
		return new HttpGet(url);
	}
	
	/**
	 * Download the file
	 * @param url
	 * @param file
	 * @throws ErrorPerformingRequestException
	 * @author Waldemar Biller
	 */
	public void performFileDownload(final String url, final File file) throws ErrorPerformingRequestException {
		LOGGER.debug("GET: URL: " + url);
		
		final HttpGet get = this.getMethod(url, null);
		get.addHeader(HeaderUtils.HEADER_AUTHORIZATION, HeaderUtils.encodeForAuthorization(this.username, this.apiKey));
		
		try {
			final HttpResponse response = getHttpClient().execute(get);
			this.httpResult = response.getStatusLine().getStatusCode();
			LOGGER.debug("HTTP result: " + this.httpResult);
			LOGGER.debug("Content-Type:" + response.getFirstHeader("Content-Type"));
			final InputStream in = response.getEntity().getContent();
			
			/*
			 * FIXME: check for errors
			 */
			if (in != null) {
				
				// read the file from the source and write it to 
				// the target given by the file parametetr
				
				final DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
				
				int bytesRead = 0;
				int b = 0;
				do {
					b = in.read();
					dos.write(b);
					callCallback(bytesRead++, response.getEntity().getContentLength());
				} while (b > -1);
				
				in.close();
				dos.close();
				
				return;
			}
		} catch (final IOException e) {
			LOGGER.debug(e.getMessage(), e);
			throw new ErrorPerformingRequestException(e);
		} finally {
			get.releaseConnection();
		}
		throw new ErrorPerformingRequestException("No Answer.");
	}

	private void callCallback(final int bytesRead, final long responseContentLength) {
		if (this.callback != null && responseContentLength > 0) {
			this.callback.setPercent((int) (bytesRead * 100 / responseContentLength));
		}
	}
}