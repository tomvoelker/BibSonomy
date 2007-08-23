package org.bibsonomy.rest.client.worker.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Level;

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
		final BufferedReader br = new BufferedReader(new InputStreamReader(responseBodyAsStream));
		int bytesRead = 0;
		String line = null;
		while ((line = br.readLine()) != null) {
			bytesRead += line.length();
			callCallback(bytesRead, responseContentLength);
			sb.append(line);
		}

		return new StringReader(sb.toString());
	}

	private void callCallback(final int bytesRead, final long responseContentLength) {
		if (this.callback != null && responseContentLength > 0) {
			this.callback.setPercent((int) (bytesRead * 100 / responseContentLength));
		}
	}
}