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
package org.bibsonomy.rest.client;

import java.io.File;
import java.io.Reader;

import org.apache.commons.httpclient.HttpStatus;
import org.bibsonomy.rest.auth.AuthenticationAccessor;
import org.bibsonomy.rest.client.util.ProgressCallback;
import org.bibsonomy.rest.client.worker.HttpWorker;
import org.bibsonomy.rest.client.worker.impl.DeleteWorker;
import org.bibsonomy.rest.client.worker.impl.GetWorker;
import org.bibsonomy.rest.client.worker.impl.HeadWorker;
import org.bibsonomy.rest.client.worker.impl.PostWorker;
import org.bibsonomy.rest.client.worker.impl.PutWorker;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.rest.renderer.RendererFactory;
import org.bibsonomy.rest.renderer.RenderingFormat;
import org.bibsonomy.rest.renderer.UrlRenderer;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @param <T> 
 */
public abstract class AbstractQuery<T> {
	
	private String apiKey;
	private String username;
	private AuthenticationAccessor accessor;
	private int statusCode = -1;

	private RenderingFormat renderingFormat = RenderingFormat.XML;
	private RendererFactory rendererFactory;
	private ProgressCallback callback;

	protected Reader downloadedDocument;

	private boolean executed = false;

	/**
	 * @return <true> iff the query was executed
	 */
	public boolean isExecuted() {
		return this.executed;
	}

	/**
	 * @return the statusCode
	 */
	public int getStatusCode() {
		return this.statusCode;
	}

	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(final int statusCode) {
		this.statusCode = statusCode;
	}
	
	private void configHttpWorker(final HttpWorker<?> worker) {
		worker.setRenderingFormat(this.renderingFormat);
	}

	protected final Reader performGetRequest(final String url) throws ErrorPerformingRequestException {
		final GetWorker worker = new GetWorker(this.username, this.apiKey, this.accessor, this.callback);
		this.configHttpWorker(worker);
		
		final Reader downloadedDocument = worker.perform(url, null);
		this.statusCode = worker.getHttpResult();
		return downloadedDocument;
	}

	protected final Reader performMultipartPostRequest(final String absoluteUrl, final File file, final String fileName) throws ErrorPerformingRequestException {
		final PostWorker worker = new PostWorker(this.username, this.apiKey, this.accessor);
		this.configHttpWorker(worker);
		final Reader result = worker.perform(absoluteUrl, file, fileName);
		this.statusCode = worker.getHttpResult();

		return result;
	}

	/**
	 * Run GET worker to download a file
	 * @param absoluteUrl
	 * @param file
	 * @throws ErrorPerformingRequestException
	 * @author Waldemar Biller
	 */
	protected final void performFileDownload(final String absoluteUrl, final File file) throws ErrorPerformingRequestException {
		final GetWorker worker = new GetWorker(this.username, this.apiKey, this.accessor, this.callback);
		this.configHttpWorker(worker);
		
		worker.performFileDownload(absoluteUrl, file);
		this.statusCode = worker.getHttpResult();
	}

	protected final Reader performRequest(final HttpMethod method, final String absoluteUrl, final String requestBody) throws ErrorPerformingRequestException {
		final HttpWorker<?> worker;

		switch (method) {
		case POST:
			worker = new PostWorker(this.username, this.apiKey, this.accessor);
			break;
		case DELETE:
			worker = new DeleteWorker(this.username, this.apiKey, this.accessor);
			break;
		case PUT:
			worker = new PutWorker(this.username, this.apiKey, this.accessor);
			break;
		case HEAD:
			worker = new HeadWorker(this.username, this.apiKey, this.accessor);
			break;
		case GET:
			throw new UnsupportedOperationException("use AbstractQuery::performGetRequest( String url)");
		default:
			throw new UnsupportedOperationException("unsupported operation: " + method.toString());
		}
		
		this.configHttpWorker(worker);
		final Reader result = worker.perform(absoluteUrl, requestBody);

		this.statusCode = worker.getHttpResult();
		return result;
	}
	
	/**
	 * Execute this query. The query blocks until a result from the server is
	 * received.
	 *
	 * @param username
	 *            username at bibsonomy.org
	 * @param apiKey
	 *            the user's password
	 * @param accessor
	 * 			  OAuth accessor
	 * @throws ErrorPerformingRequestException
	 *             if something fails, eg an ioexception occurs (see the cause)
	 */
	final void execute(final String username, final String apiKey, final AuthenticationAccessor accessor) throws ErrorPerformingRequestException {
		this.username = username;
		this.apiKey = apiKey;
		this.accessor = accessor;
		this.executed = true;
		this.doExecute();
	}

	/**
	 * @return result of the query
	 * @throws ErrorPerformingRequestException if something fails, eg an ioexception occurs (see the cause).
	 */
	protected abstract void doExecute() throws ErrorPerformingRequestException;

	/**
	 * @return the HTTP status code this query had (only available after
	 *         execution).
	 * @throws IllegalStateException
	 *             if query has not yet been executed.
	 */
	public final int getHttpStatusCode() throws IllegalStateException {
		if (this.statusCode == -1) throw new IllegalStateException("Execute the query first.");
		return this.statusCode;
	}
	
	protected abstract T getResultInternal() throws BadRequestOrResponseException, IllegalStateException;
	
	/**
	 * @return the result of this query, if there is one.
	 * @throws BadRequestOrResponseException
	 *             if the received data is not valid.
	 * @throws IllegalStateException
	 *             if @see #getResult() gets called before 
	 */
	public T getResult() throws BadRequestOrResponseException, IllegalStateException {
		if (!this.executed) {
			throw new IllegalStateException("Execute the query first.");
		}
		
		return this.getResultInternal();
	}
	
	/**
	 * @param renderingFormat
	 *            the {@link RenderingFormat} to use.
	 */
	void setRenderingFormat(final RenderingFormat renderingFormat) {
		this.renderingFormat = renderingFormat;
	}

	/**
	 * @param callback
	 *            the {@link ProgressCallback} to inform
	 */
	void setProgressCallback(final ProgressCallback callback) {
		this.callback = callback;
	}

	/**
	 * @return <code>true</code> iff the request was successful
	 */
	public boolean isSuccess() {
		return this.getHttpStatusCode() == HttpStatus.SC_OK || this.getHttpStatusCode() == HttpStatus.SC_CREATED;
	}
	
	/**
	 * @return error code iff the request was not successful
	 */
	public String getError() {
		if (this.downloadedDocument == null) throw new IllegalStateException("Execute the query first.");
		return this.getRenderer().parseError(this.downloadedDocument);
	}
	
	/**
	 * @return the renderer for the renderingFormat
	 */
	public Renderer getRenderer() {
		return this.rendererFactory.getRenderer(this.renderingFormat);
	}
	
	/**
	 * @return the url renderer
	 */
	public UrlRenderer getUrlRenderer() {
		return this.rendererFactory.getUrlRenderer();
	}

	/**
	 * @param rendererFactory the rendererFactory to set
	 */
	public void setRendererFactory(final RendererFactory rendererFactory) {
		this.rendererFactory = rendererFactory;
	}

}