package org.bibsonomy.rest.strategy.users;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Document;
import org.bibsonomy.rest.strategy.AbstractDeleteStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 *
 * 
 * @author  Christian Kramer
 * $Author$
 * @version $Id$
 */
public class DeleteDocumentStrategy extends AbstractDeleteStrategy {
	
	private final Document document;
	private final String resourceHash;
	
	/**
	 * @param context
	 * @param userName
	 * @param resourceHash
	 * @param fileName
	 */
	public DeleteDocumentStrategy(final Context context, final String userName, final String resourceHash, final String fileName) {
		super(context);
		this.document = new Document();
		this.document.setUserName(userName);
		this.document.setFileName(fileName);
		this.resourceHash = resourceHash;
	}
	
	@Override
	public String getContentType() {
		return null;
	}

	@Override
	protected boolean delete() throws InternServerException {
		this.getLogic().deleteDocument(this.document, this.resourceHash);
		// no exception -> assume success
		return true;
	}
}