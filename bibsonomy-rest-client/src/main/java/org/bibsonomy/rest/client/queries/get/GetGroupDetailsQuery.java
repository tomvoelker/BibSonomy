package org.bibsonomy.rest.client.queries.get;

import java.io.Reader;

import org.bibsonomy.model.Group;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.RendererFactory;

/**
 * Use this Class to receive details about an group of bibsonomy.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class GetGroupDetailsQuery extends AbstractQuery<Group> {

	private final String groupname;
	private Reader downloadedDocument;

	/**
	 * Gets details of a group.
	 * 
	 * @param groupname
	 *            name of the user
	 * @throws IllegalArgumentException
	 *             if groupname is null or empty
	 */
	public GetGroupDetailsQuery(final String groupname) throws IllegalArgumentException {
		if (groupname == null || groupname.length() == 0) throw new IllegalArgumentException("no groupname given");

		this.groupname = groupname;
	}

	@Override
	public Group getResult() throws BadRequestOrResponseException, IllegalStateException {
		if (this.downloadedDocument == null) throw new IllegalStateException("Execute the query first.");
		return RendererFactory.getRenderer(getRenderingFormat()).parseGroup(this.downloadedDocument);
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		this.downloadedDocument = performGetRequest(URL_GROUPS + "/" + this.groupname + "?format=" + getRenderingFormat().toString().toLowerCase());
	}
}