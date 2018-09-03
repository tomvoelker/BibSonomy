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

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.util.EntityUtils;
import org.bibsonomy.rest.client.auth.AuthenticationAccessor;
import org.bibsonomy.rest.client.util.RestClientUtils;
import org.bibsonomy.rest.client.worker.HttpWorker;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.rest.renderer.RenderingFormat;
import org.bibsonomy.rest.utils.HeaderUtils;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public final class PostWorker extends HttpWorker<HttpPost> {

	/**
	 * @param username
	 * @param apiKey
	 * @param accessor 
	 */
	public PostWorker(final String username, final String apiKey, final AuthenticationAccessor accessor) {
		super(username, apiKey, accessor);
	}
	
	/**
	 * @param url
	 * @param file
	 * @return the reader
	 * @throws ErrorPerformingRequestException
	 */
	public Reader perform(final String url, final File file) throws ErrorPerformingRequestException {
		LOGGER.debug("POST Multipart: URL: " + url);
		final HttpPost post = new HttpPost(url);

		final RenderingFormat renderingFormat = this.getRenderingFormat();
		if (renderingFormat != null) {
			post.addHeader("Accept", renderingFormat.getMimeType());
		}
		post.addHeader(HeaderUtils.HEADER_AUTHORIZATION, HeaderUtils.encodeForAuthorization(this.username, this.apiKey));

		final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addBinaryBody("file", file);
		post.setEntity(builder.build());

		final RequestConfig.Builder requestConfigBuilder = RestClientUtils.createRequestConfigBuilder();
		requestConfigBuilder.setExpectContinueEnabled(true);

		final HttpClient httpClient = RestClientUtils.buildClient(requestConfigBuilder.build());

		try {
			final HttpResponse response = httpClient.execute(post);
			this.httpResult = response.getStatusLine().getStatusCode();
			final String stringResponse = EntityUtils.toString(response.getEntity());
			
			LOGGER.debug("HTTP result: " + this.httpResult);
			LOGGER.debug("response:\n" + stringResponse);
			LOGGER.debug("===================================================");
			return new StringReader(stringResponse);
		} catch (final IOException e) {
			LOGGER.debug(e.getMessage(), e);
			throw new ErrorPerformingRequestException(e);
		} finally {
			post.releaseConnection();
		}
	}
	
	@Override
	protected HttpPost getMethod(final String url, final String requestBody) {
		final HttpPost post = new HttpPost(url);
		RestClientUtils.prepareHttpMethod(post, requestBody);
		return post;
	}
}