package org.bibsonomy.rest.client;

import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.httpclient.HttpStatus;
import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.worker.HttpWorker;
import org.bibsonomy.rest.client.worker.impl.DeleteWorker;
import org.bibsonomy.rest.client.worker.impl.GetWorker;
import org.bibsonomy.rest.client.worker.impl.HeadWorker;
import org.bibsonomy.rest.client.worker.impl.PostWorker;
import org.bibsonomy.rest.client.worker.impl.PutWorker;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.enums.RenderingFormat;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.RendererFactory;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public abstract class AbstractQuery<T> {
	protected static final String URL_TAGS = RestProperties.getInstance().getTagsUrl();
	protected static final String URL_USERS = RestProperties.getInstance().getUsersUrl();
	protected static final String URL_GROUPS = RestProperties.getInstance().getGroupsUrl();
	protected static final String URL_POSTS = RestProperties.getInstance().getPostsUrl();
	protected static final String URL_POSTS_ADDED = RestProperties.getInstance().getAddedPostsUrl();
	protected static final String URL_POSTS_POPULAR = RestProperties.getInstance().getPopularPostsUrl();
	protected static final String URL_CONCEPTS = RestProperties.getInstance().getConceptUrl();
	
	private String apiKey;
	private String username;
	private String apiURL;
	private int statusCode = -1;
	private RenderingFormat renderingFormat = RenderingFormat.XML;
	private ProgressCallback callback;
	
	protected Reader downloadedDocument;
	
	private T result;
	private boolean executed = false;

	protected final Reader performGetRequest(final String url) throws ErrorPerformingRequestException {
		final GetWorker worker = new GetWorker(this.username, this.apiKey, this.callback);
		final Reader downloadedDocument = worker.perform(this.apiURL + url);
		this.statusCode = worker.getHttpResult();
		return downloadedDocument;
	}

	protected final Reader performRequest(final HttpMethod method, final String url, final String requestBody) throws ErrorPerformingRequestException {
		final HttpWorker worker;
		final Reader result;
		final String absoluteUrl;
		try {
			absoluteUrl = URLEncoder.encode(apiURL + url, "utf-8");
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex.getMessage());
		}


		switch (method) {
		case POST:
			worker = new PostWorker(this.username, this.apiKey);
			result = ((PostWorker) worker).perform(absoluteUrl, requestBody);
			break;
		case DELETE:
			worker = new DeleteWorker(this.username, this.apiKey);
			result = ((DeleteWorker) worker).perform(absoluteUrl);
			break;
		case PUT:
			worker = new PutWorker(this.username, this.apiKey);
			result = ((PutWorker) worker).perform(absoluteUrl, requestBody);
			break;
		case HEAD:
			worker = new HeadWorker(this.username, this.apiKey);
			result = ((HeadWorker) worker).perform(absoluteUrl);
			break;
		case GET:
			throw new UnsupportedOperationException("use AbstractQuery::performGetRequest( String url)");
		default:
			throw new UnsupportedOperationException("unsupported operation: " + method.toString());
		}

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
	 * @throws ErrorPerformingRequestException
	 *             if something fails, eg an ioexception occurs (see the cause)
	 */
	final void execute(final String username, final String apiKey) throws ErrorPerformingRequestException {
		this.username = username;
		this.apiKey = apiKey;
		this.executed = true;
		this.result = doExecute();
	}

	/**
	 * @return result of the query
	 * @throws ErrorPerformingRequestException if something fails, eg an ioexception occurs (see the cause).
	 */
	protected abstract T doExecute() throws ErrorPerformingRequestException;

	/**
	 * @return the HTTP status code this query had (only available after
	 *         execution).
	 * @throws IllegalStateException
	 *             if query has not yet been executed.
	 */
	public final int getHttpStatusCode() throws IllegalStateException {
		if (this.statusCode == -1) throw new IllegalStateException("Execute the query first.");
		return statusCode;
	}

	/**
	 * @return the result of this query, if there is one.
	 * @throws {@link BadRequestOrResponseException}
	 *             if the received data is not valid.
	 * @throws {@link IllegalStateException}
	 *             if
	 * @link {@link #getResult()} gets called before
	 * @link {@link Bibsonomy#executeQuery(AbstractQuery)}
	 */
	public T getResult() throws BadRequestOrResponseException, IllegalStateException {
		if (!this.executed) throw new IllegalStateException("Execute the query first.");
		return this.result;
	}

	/**
	 * @param apiURL
	 *            The apiURL to set.
	 */
	void setApiURL(final String apiURL) {
		this.apiURL = apiURL;
	}

	/**
	 * @return the {@link RenderingFormat} to use.
	 */
	protected RenderingFormat getRenderingFormat() {
		return this.renderingFormat;
	}
    /*
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
	
	public boolean isSuccess() {
		if (this.getHttpStatusCode() == HttpStatus.SC_OK || this.getHttpStatusCode() == HttpStatus.SC_CREATED)
			return true;
		return false;						
	}
	
	public String getError() {
		if (this.downloadedDocument == null) throw new IllegalStateException("Execute the query first.");
		return RendererFactory.getRenderer(getRenderingFormat()).parseError(this.downloadedDocument);
	}	
	
}