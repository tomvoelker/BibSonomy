package org.bibsonomy.rest.client.worker.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Level;

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
		LOGGER.log(Level.INFO, "HEAD: URL: " + url);

		final HeadMethod head = new HeadMethod(url);
		head.addRequestHeader(HEADER_AUTHORIZATION, encodeForAuthorization());
		head.setDoAuthentication(true);
		head.setFollowRedirects(true);

		try {
			this.httpResult = getHttpClient().executeMethod(head);
			LOGGER.log(Level.INFO, "Result: " + this.httpResult);
			return new StringReader( head.getStatusText() );
		} catch (final IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw new ErrorPerformingRequestException(e);
		} finally {
			head.releaseConnection();
		}
	}
}