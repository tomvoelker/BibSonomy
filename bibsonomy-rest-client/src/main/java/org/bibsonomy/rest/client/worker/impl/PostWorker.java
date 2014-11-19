/**
 *
 *  BibSonomy-Rest-Client - The REST-client.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
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
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.FilePartSource;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.rest.auth.AuthenticationAccessor;
import org.bibsonomy.rest.client.util.RestClientUtils;
import org.bibsonomy.rest.client.worker.HttpWorker;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.rest.utils.HeaderUtils;

/**
 * TODO: merge duplicate code with PostWorker
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public final class PostWorker extends HttpWorker<PostMethod> {

	private static final String CONTENT_TYPE = "multipart/form-data";
	
	private static final Log log = LogFactory.getLog(PostWorker.class);

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
	 * @param fileName the file name to be used
	 * @return the reader
	 * @throws ErrorPerformingRequestException
	 */
	public Reader perform(final String url, final File file, final String fileName) throws ErrorPerformingRequestException {
		LOGGER.debug("POST Multipart: URL: " + url);
		final PostMethod post = new PostMethod(url);

		if (this.getRenderingFormat() != null) {
			post.addRequestHeader("Accept", this.getRenderingFormat().getMimeType());
		}
		post.addRequestHeader(HeaderUtils.HEADER_AUTHORIZATION, HeaderUtils.encodeForAuthorization(this.username, this.apiKey));
		
		post.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, true);

		try {
			final HttpMethodParams params = new HttpMethodParams();
			final FilePart filePart = new FilePart("file", new FilePartSource(fileName, file)) {
				/**
				 * TODO: remove as soon as the http-client is updated to 4.0
				 * method hacked to get this fixed:
				 * https://issues.apache.org/jira/browse/HTTPCLIENT-293
				 */
				@Override
				protected void sendDispositionHeader(final OutputStream out) throws IOException {
					log.trace("enter sendDispositionHeader(OutputStream out)");
					out.write(CONTENT_DISPOSITION_BYTES);
					out.write(QUOTE_BYTES);
					out.write(EncodingUtil.getAsciiBytes(this.getName()));
					out.write(QUOTE_BYTES);

					final byte[] FILE_NAME_BYTES = EncodingUtil.getAsciiBytes(FILE_NAME);
					final String filename = this.getSource().getFileName();
					if (filename != null) {
						out.write(FILE_NAME_BYTES);

						out.write(QUOTE_BYTES);
						// the actual change is a one-liner. We replace
						// out.write(EncodingUtil.getAsciiBytes(filename));
						// by:
						out.write(EncodingUtil.getBytes(filename, this.getCharSet()));

						out.write(QUOTE_BYTES);
					}
				}
			};
			filePart.setCharSet(Charset.defaultCharset().name());
			final Part[] parts = new Part[] { filePart };
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
			post.setRequestEntity(new StringRequestEntity(requestBody, CONTENT_TYPE, RestClientUtils.CONTENT_CHARSET));
		} catch (final UnsupportedEncodingException ex) {
			LOGGER.fatal("Could not encode request entity to UTF-8", ex);
			throw new RuntimeException(ex);
		}
		
		return post;
	}
}