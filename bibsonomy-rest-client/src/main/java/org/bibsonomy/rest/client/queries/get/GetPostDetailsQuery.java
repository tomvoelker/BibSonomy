package org.bibsonomy.rest.client.queries.get;

import java.io.Reader;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.RendererFactory;

/**
 * Use this Class to receive details about a post of an user.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class GetPostDetailsQuery extends AbstractQuery<Post<? extends Resource>> {

	private final String username;
	private final String resourceHash;
	private Reader downloadedDocument;

	/**
	 * Gets details of a post of an user.
	 * 
	 * @param username
	 *            name of the user
	 * @param resourceHash
	 *            hash of the resource
	 * @throws IllegalArgumentException
	 *             if userName or resourceHash are null or empty
	 */
	public GetPostDetailsQuery(final String username, final String resourceHash) throws IllegalArgumentException {
		if (username == null || username.length() == 0) throw new IllegalArgumentException("no username given");
		if (resourceHash == null || resourceHash.length() == 0) throw new IllegalArgumentException("no resourceHash given");

		this.username = username;
		this.resourceHash = resourceHash;
	}

	@Override
	public Post<? extends Resource> getResult() throws BadRequestOrResponseException, IllegalStateException {
		if (this.downloadedDocument == null) throw new IllegalStateException("Execute the query first.");
		return RendererFactory.getRenderer(getRenderingFormat()).parsePost(this.downloadedDocument);
	}

	@Override
	protected Post<? extends Resource> doExecute() throws ErrorPerformingRequestException {
		this.downloadedDocument = performGetRequest(URL_USERS + "/" + this.username + "/" + URL_POSTS + "/" + this.resourceHash + "?format=" + getRenderingFormat().toString().toLowerCase());
		return null;
	}
}