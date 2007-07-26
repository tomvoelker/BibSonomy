package org.bibsonomy.rest.client.queries.get;

import java.io.Reader;

import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.RendererFactory;

/**
 * Use this Class to receive information about a specific tag
 * 
 * @author Jens Illig <illig@innofinity.de>
 * @version $Id$
 */
public final class GetTagDetailsQuery extends AbstractQuery<Tag> {
	private final String tagName;
	private Reader downloadedDocument;

	public GetTagDetailsQuery(final String tagName) {
		this.tagName = tagName;this.downloadedDocument = null;
	}

	
	@Override
	public Tag getResult() throws BadRequestOrResponseException, IllegalStateException {
		if (this.downloadedDocument == null) throw new IllegalStateException("Execute the query first.");
		return RendererFactory.getRenderer(getRenderingFormat()).parseTag(this.downloadedDocument);
	}

	@Override
	protected Tag doExecute() throws ErrorPerformingRequestException {
		String url = URL_TAGS + "/" + tagName;
		this.downloadedDocument = performGetRequest(url + "?format=" + getRenderingFormat().toString().toLowerCase());
		return null;
	}
}