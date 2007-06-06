package org.bibsonomy.rest.client.queries.get;

import java.io.Reader;
import java.util.List;

import org.bibsonomy.model.Group;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.RendererFactory;

/**
 * Use this Class to receive an ordered list of all groups bibsonomy has.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class GetGroupListQuery extends AbstractQuery<List<Group>> {

	private final int start;
	private final int end;
	private Reader downloadedDocument;

	/**
	 * Gets bibsonomy's group list
	 */
	public GetGroupListQuery() {
		this(0, 19);
	}

	/**
	 * Gets bibsonomy's group list.
	 * 
	 * @param start
	 *            start of the list
	 * @param end
	 *            end of the list
	 */
	public GetGroupListQuery(int start, int end) {
		if (start < 0) start = 0;
		if (end < start) end = start;

		this.start = start;
		this.end = end;
	}

	@Override
	public List<Group> getResult() throws BadRequestOrResponseException, IllegalStateException {
		if (this.downloadedDocument == null) throw new IllegalStateException("Execute the query first.");
		return RendererFactory.getRenderer(getRenderingFormat()).parseGroupList(this.downloadedDocument);
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		this.downloadedDocument = performGetRequest(URL_GROUPS + "?start=" + this.start + "&end=" + this.end + "&format=" + getRenderingFormat().toString().toLowerCase());
	}
}