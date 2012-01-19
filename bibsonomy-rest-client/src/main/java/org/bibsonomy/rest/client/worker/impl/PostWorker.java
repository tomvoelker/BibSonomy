/**
 *
 *  BibSonomy-Rest-Client - The REST-client.
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
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

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import net.oauth.OAuthAccessor;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.worker.HttpWorker;
import org.bibsonomy.rest.utils.HeaderUtils;

/**
 * TODO: merge duplicate code with PostWorker
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class PostWorker extends HttpWorker<PostMethod> {

	private static final String CONTENT_TYPE = "multipart/form-data";

	/**
	 * @param username
	 * @param apiKey
	 */
	public PostWorker(final String username, final String apiKey, OAuthAccessor accessor) {
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
		final PostMethod post = new PostMethod(url);

		post.addRequestHeader(HeaderUtils.HEADER_AUTHORIZATION, HeaderUtils.encodeForAuthorization(this.username, this.apiKey));
		post.addRequestHeader("Content-Type", CONTENT_TYPE);

		try {
			final HttpMethodParams params = new HttpMethodParams();
			params.setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, true);
			final Part[] parts = new Part[]{new FilePart("file", file)};
			final MultipartRequestEntity entity = new MultipartRequestEntity(parts, params);
			post.setRequestEntity(entity);

			this.getHttpClient().getHttpConnectionManager().getParams().setConnectionTimeout(5000);

			this.httpResult = this.getHttpClient().executeMethod(post);
			LOGGER.debug("HTTP result: " + this.httpResult);
			LOGGER.debug("response:\n" + post.getResponseBodyAsString());
			LOGGER.debug("===================================================");
			return new StringReader(post.getResponseBodyAsString());
		} catch (final IOException e) {
			LOGGER.debug(e.getMessage(), e);
			throw new ErrorPerformingRequestException(e);
		} finally {
			post.releaseConnection();
		}
	}
	
	@Override
	protected PostMethod getMethod(final String url, final String requestBody) {
		final PostMethod post = new PostMethod(url);
		post.setFollowRedirects(false);

		try {
			post.setRequestEntity(new StringRequestEntity(requestBody, CONTENT_TYPE, "UTF-8"));
		} catch (final UnsupportedEncodingException ex) {
			LOGGER.fatal("Could not encode request entity to UTF-8", ex);
			throw new RuntimeException(ex);
		}
		
		return post;
	}

	@Override
	protected Reader readResponse(final PostMethod method) throws IOException, ErrorPerformingRequestException {
		return new StringReader(method.getResponseBodyAsString());
	}
}