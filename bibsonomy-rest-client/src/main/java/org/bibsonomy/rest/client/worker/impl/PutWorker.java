package org.bibsonomy.rest.client.worker.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Level;

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