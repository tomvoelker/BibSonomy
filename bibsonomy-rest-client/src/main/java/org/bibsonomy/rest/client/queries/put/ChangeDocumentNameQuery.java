package org.bibsonomy.rest.client.queries.put;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.StringWriter;

import org.bibsonomy.model.Document;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.rest.renderer.UrlRenderer;


/**
 * query to rename a document
 * 
 * @author dzo
 * @version $Id$
 */
public class ChangeDocumentNameQuery extends AbstractQuery<String> {
	private String resourceHash;
	private String newFileName;
	
	private Document oldDocument;

	/**
	 * 
	 * @param resourceHash
	 * @param newFileName
	 * @param oldDocument
	 */
	public ChangeDocumentNameQuery(String resourceHash, String newFileName, Document oldDocument) {
		if (!present(newFileName)) throw new IllegalArgumentException("no new name given");
		if (!present(resourceHash)) throw new IllegalArgumentException("no resourceHash given");
		if (!present(oldDocument) || !present(oldDocument.getFileName())) throw new IllegalStateException("no old document name given");
		if (!present(oldDocument.getUserName())) throw new IllegalStateException("no user name given");
		this.resourceHash = resourceHash;
		this.newFileName = newFileName;
		this.oldDocument = oldDocument;
	}

	@Override
	protected String doExecute() throws ErrorPerformingRequestException {
		final Document newDocument = new Document();
		newDocument.setFileName(this.newFileName);
		
		final StringWriter sw = new StringWriter(100);
		this.getRenderer().serializeDocument(sw, newDocument);
		// TODO: use the urlrenderer of this query!
		final String url = new UrlRenderer("").createHrefForResourceDocument(oldDocument.getUserName(), this.resourceHash, this.oldDocument.getFileName());
		this.performRequest(HttpMethod.PUT, url, sw.toString());
		return null;
	}
	
	@Override
	public String getResult() throws BadRequestOrResponseException, IllegalStateException {
		if (this.isSuccess())
			return this.getRenderer().parseResourceHash(this.downloadedDocument);
		return this.getError();
	}
}
