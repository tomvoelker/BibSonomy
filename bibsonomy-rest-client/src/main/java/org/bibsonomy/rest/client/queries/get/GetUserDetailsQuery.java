package org.bibsonomy.rest.client.queries.get;

import java.io.Reader;

import org.bibsonomy.model.User;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.RendererFactory;

/**
 * Use this Class to receive details about an user of bibsonomy.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class GetUserDetailsQuery extends AbstractQuery<User> {

	private final String username;
	private Reader downloadedDocument;

	/**
	 * Gets details of a user.
	 * 
	 * @param username
	 *            name of the user
	 * @throws IllegalArgumentException
	 *             if username is null or empty
	 */
	public GetUserDetailsQuery(final String username) throws IllegalArgumentException {
		if (username == null || username.length() == 0) throw new IllegalArgumentException("no username given");

		this.username = username;
	}

	@Override
	public User getResult() throws BadRequestOrResponseException, IllegalStateException {
		if (this.downloadedDocument == null) throw new IllegalStateException("Execute the query first.");
		return RendererFactory.getRenderer(getRenderingFormat()).parseUser(this.downloadedDocument);
	}

	@Override
	protected User doExecute() throws ErrorPerformingRequestException {
		this.downloadedDocument = performGetRequest(URL_USERS + "/" + this.username + "?format=" + getRenderingFormat().toString().toLowerCase());
		return null;
	}
}