package org.bibsonomy.rest.client.queries.get;

import java.io.Reader;
import java.util.List;

import org.bibsonomy.model.User;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.RendererFactory;

/**
 * Use this Class to receive an ordered list of all users bibsonomy has.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class GetUserListQuery extends AbstractQuery<List<User>> {

	private final int start;
	private final int end;
	private Reader downloadedDocument;

	/**
	 * Gets bibsonomy's user list
	 */
	public GetUserListQuery() {
		this(0, 19);
	}

	/**
	 * Gets bibsonomy's user list.
	 * 
	 * @param start
	 *            start of the list
	 * @param end
	 *            end of the list
	 */
	public GetUserListQuery(int start, int end) {
		if (start < 0) start = 0;
		if (end < start) end = start;

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
		this.downloadedDocument = performGetRequest(URL_USERS + "?start=" + this.start + "&end=" + this.end + "&format=" + getRenderingFormat().toString().toLowerCase());
		return null;
	}
}