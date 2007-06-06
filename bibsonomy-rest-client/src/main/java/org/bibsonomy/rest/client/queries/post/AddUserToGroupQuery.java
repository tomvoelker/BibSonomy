package org.bibsonomy.rest.client.queries.post;

import java.io.StringWriter;

import org.bibsonomy.model.User;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.renderer.RendererFactory;

/**
 * Use this Class to add an user to an already existing group.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class AddUserToGroupQuery extends AbstractQuery<String> {

	private boolean executed = false;
	private String result;
	private final User user;
	private final String groupName;

	/**
	 * Adds an user to an already existing group. <p/>note that the user and the
	 * group must exist before this query can be performed
	 * 
	 * @param groupname
	 *            name of the group the user is to be added to. the group must
	 *            exist, else a {@link IllegalArgumentException} is thrown
	 * @param user
	 *            the user to be added
	 * @throws IllegalArgumentException
	 *             if the groupname is null or empty, or if the user is null or
	 *             has no name defined
	 */
	public AddUserToGroupQuery(final String groupName, final User user) throws IllegalArgumentException {
		if (groupName == null || groupName.length() == 0) throw new IllegalArgumentException("no groupName given");
		if (user == null) throw new IllegalArgumentException("no user specified");
		if (user.getName() == null || user.getName().length() == 0) throw new IllegalArgumentException("no username specified");
	
		this.groupName = groupName;
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
		RendererFactory.getRenderer(getRenderingFormat()).serializeUser(sw, user, null);
		this.result = performRequest(HttpMethod.POST, URL_GROUPS + "/" + this.groupName + "/" + URL_USERS + "?format=" + getRenderingFormat().toString().toLowerCase(), sw.toString());
	}
}