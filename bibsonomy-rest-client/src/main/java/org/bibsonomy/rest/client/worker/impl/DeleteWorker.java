package org.bibsonomy.rest.client.worker.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Level;

import org.apache.commons.httpclient.methods.DeleteMethod;
import org.bibsonomy.rest.client.ProgressCallback;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.worker.HttpWorker;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class DeleteWorker extends HttpWorker {

	public DeleteWorker(final String username, final String apiKey) {
		super(username, apiKey);
	}

	public Reader perform(final String url) throws ErrorPerformingRequestException {
		LOGGER.log(Level.INFO, "DELETE: URL: " + url);

		final DeleteMethod delete = new DeleteMethod(url);
		delete.addRequestHeader(HEADER_AUTHORIZATION, encodeForAuthorization());
		delete.setDoAuthentication(true);
		delete.setFollowRedirects(true);

		try {
			this.httpResult = getHttpClient().executeMethod(delete);
			LOGGER.log(Level.INFO, "Result: " + this.httpResult);
			return new StringReader(delete.getResponseBodyAsString());
		} catch (final IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw new ErrorPerformingRequestException(e);
		} finally {
			delete.releaseConnection();
		}
	}
}