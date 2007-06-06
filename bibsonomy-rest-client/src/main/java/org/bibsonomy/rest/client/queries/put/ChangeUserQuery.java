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

	private boolean executed = false;
	private String result;
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
	public String getResult() {
		if (!this.executed) throw new IllegalStateException("Execute the query first.");
		return this.result;
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		this.executed = true;
		final StringWriter sw = new StringWriter(100);
		RendererFactory.getRenderer(getRenderingFormat()).serializeUser(sw, this.user, null);
		this.result = performRequest(HttpMethod.PUT, URL_USERS + "/" + this.userName + "?format=" + getRenderingFormat().toString().toLowerCase(), sw.toString());
	}
}