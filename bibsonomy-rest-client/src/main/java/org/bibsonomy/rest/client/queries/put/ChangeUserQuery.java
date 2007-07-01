package org.bibsonomy.rest.client.queries.put;

import java.io.StringWriter;

import org.bibsonomy.model.User;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.renderer.RendererFactory;

/**
 * Use this Class to change details of an existing user account.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class ChangeUserQuery extends AbstractQuery<String> {
	private final User user;
	private final String userName;

	/**
	 * Changes details of an existing user account.
	 * 
	 * @param username
	 *            the user to change
	 * @param user
	 *            new values
	 * @throws IllegalArgumentException
	 *             if the username is null or empty, or if the user hat no name
	 *             specified.
	 */
	public ChangeUserQuery(final String userName, final User user) throws IllegalArgumentException {
		if (userName == null || userName.length() == 0) throw new IllegalArgumentException("no username given");
		if (user == null) throw new IllegalArgumentException("no user specified");
		if (user.getName() == null || user.getName().length() == 0) throw new IllegalArgumentException("no username specified");

		this.userName = userName;
		this.user = user;
	}

	@Override
	protected String doExecute() throws ErrorPerformingRequestException {
		final StringWriter sw = new StringWriter(100);
		RendererFactory.getRenderer(getRenderingFormat()).serializeUser(sw, this.user, null);
		return performRequest(HttpMethod.PUT, URL_USERS + "/" + this.userName + "?format=" + getRenderingFormat().toString().toLowerCase(), sw.toString());
	}
}