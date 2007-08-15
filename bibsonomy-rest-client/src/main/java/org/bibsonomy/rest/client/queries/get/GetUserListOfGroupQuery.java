package org.bibsonomy.rest.client.queries.get;

import java.io.Reader;
import java.util.List;

import org.bibsonomy.model.User;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.RendererFactory;

/**
 * Use this Class to receive an ordered list of all users belonging to a given
 * group.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class GetUserListOfGroupQuery extends AbstractQuery<List<User>> {

	private final String groupname;
	private final int start;
	private final int end;

	/**
	 * Gets an user list of a group
	 */
	public GetUserListOfGroupQuery(final String groupname) {
		this(groupname, 0, 19);
	}

	/**
	 * Gets an user list of a group.
	 * 
	 * @param start
	 *            start of the list
	 * @param end
	 *            end of the list
	 * @throws IllegalArgumentException
	 *             if the groupname is null or empty
	 */
	public GetUserListOfGroupQuery(final String groupname, int start, int end) throws IllegalArgumentException {
		if (groupname == null || groupname.length() == 0) throw new IllegalArgumentException("no groupname given");
		if (start < 0) start = 0;
		if (end < start) end = start;

		this.groupname = groupname;
		this.start = start;
		this.end = end;
	}

	@Override
	public List<User> getResult() throws BadRequestOrResponseException, IllegalStateException {
		if (this.downloadedDocument == null) throw new IllegalStateException("Execute the query first.");
		return RendererFactory.getRenderer(getRenderingFormat()).parseUserList(this.downloadedDocument);
	}

	@Override
	protected List<User> doExecute() throws ErrorPerformingRequestException {
		this.downloadedDocument = performGetRequest(URL_GROUPS + "/" + this.groupname + "/" + URL_USERS + "?start=" + this.start + "&end=" + this.end + "&format=" + getRenderingFormat().toString().toLowerCase());
		return null;
	}
}