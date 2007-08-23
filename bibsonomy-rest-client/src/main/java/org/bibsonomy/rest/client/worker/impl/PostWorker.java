package org.bibsonomy.rest.client.worker.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.worker.HttpWorker;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class PostWorker extends HttpWorker {

	public PostWorker(final String username, final String apiKey) {
		super(username, apiKey);
	}

	public Reader perform(final String url, final String requestBody) throws ErrorPerformingRequestException {
		LOGGER.debug("POST: URL: " + url);

		final PostMethod post = new PostMethod(url);
		post.addRequestHeader(HEADER_AUTHORIZATION, encodeForAuthorization());
		post.setDoAuthentication(true);
		post.setFollowRedirects(false);

		post.setRequestEntity(new StringRequestEntity(requestBody));

		try {
			this.httpResult = getHttpClient().executeMethod(post);
			LOGGER.debug("HTTP result: " + this.httpResult);
			LOGGER.debug("XML response:\n" + post.getResponseBodyAsString());
			LOGGER.debug("===================================================");			
			return new StringReader(post.getResponseBodyAsString());
		} catch (final IOException e) {
			LOGGER.debug(e.getMessage(), e);
			throw new ErrorPerformingRequestException(e);
		} finally {
			post.releaseConnection();
		}
	}
}