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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.httpclient.methods.GetMethod;
import org.bibsonomy.rest.client.ProgressCallback;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.worker.HttpWorker;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class GetWorker extends HttpWorker {

	private final ProgressCallback callback;

	public GetWorker(final String username, final String password, final ProgressCallback callback) {
		super(username, password);
		
		this.callback = callback;
	}

	public Reader perform(final String url) throws ErrorPerformingRequestException {
		LOGGER.debug("GET: URL: " + url);
		
		// dirty but working
		if (this.proxyHost != null){
			getHttpClient().getHostConfiguration().setProxy(this.proxyHost, this.proxyPort);
		}

		final GetMethod get = new GetMethod(url);
		get.addRequestHeader(HEADER_AUTHORIZATION, encodeForAuthorization());
		get.setDoAuthentication(true);
		get.setFollowRedirects(true);

		try {
			this.httpResult = getHttpClient().executeMethod(get);
			LOGGER.debug("HTTP result: " + this.httpResult);
			LOGGER.debug("XML response:\n" + get.getResponseBodyAsString());
			LOGGER.debug("===================================================");			
			if (get.getResponseBodyAsStream() != null) {
				return performDownload(get.getResponseBodyAsStream(), get.getResponseContentLength());
			}
		} catch (final IOException e) {
			LOGGER.debug(e.getMessage(), e);
			throw new ErrorPerformingRequestException(e);
		} finally {
			get.releaseConnection();
		}
		throw new ErrorPerformingRequestException("No Answer.");
	}

	private Reader performDownload(final InputStream responseBodyAsStream, final long responseContentLength) throws ErrorPerformingRequestException, IOException {
		if (responseContentLength > Integer.MAX_VALUE) throw new ErrorPerformingRequestException("The response is to long: " + responseContentLength);

		final StringBuilder sb = new StringBuilder((int) responseContentLength);
		final BufferedReader br = new BufferedReader(new InputStreamReader(responseBodyAsStream, "UTF-8"));
		int bytesRead = 0;
		String line = null;
		while ((line = br.readLine()) != null) {
			bytesRead += line.length();
			callCallback(bytesRead, responseContentLength);
			sb.append(line);
		}

		return new StringReader(sb.toString());
	}
	
	/**
	 * Download the file
	 * @param url
	 * @param file
	 * @throws ErrorPerformingRequestException
	 * @author Waldemar Biller
	 */
	public void performFileDownload(final String url, File file) throws ErrorPerformingRequestException {
		
		LOGGER.debug("GET: URL: " + url);
		
		// dirty but working
		if (this.proxyHost != null){
			getHttpClient().getHostConfiguration().setProxy(this.proxyHost, this.proxyPort);
		}
		
		final GetMethod get = new GetMethod(url);
		get.addRequestHeader(HEADER_AUTHORIZATION, encodeForAuthorization());
		get.setDoAuthentication(true);
		get.setFollowRedirects(true);
		
		try {
			this.httpResult = getHttpClient().executeMethod(get);
			LOGGER.debug("HTTP result: " + this.httpResult);
			LOGGER.debug("Content-Type:" + get.getRequestHeaders("Content-Type"));
			LOGGER.debug("===================================================");			
			if (get.getResponseBodyAsStream() != null) {
				
				// read the file from the source and write it to 
				// the target given by the file parametetr
				InputStream in = get.getResponseBodyAsStream();
				DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
				
				int bytesRead = 0;
				int b = 0;
				do {
					b = in.read();
					dos.write(b);
					callCallback(bytesRead++, get.getResponseContentLength());
				} while(b > -1);
				
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