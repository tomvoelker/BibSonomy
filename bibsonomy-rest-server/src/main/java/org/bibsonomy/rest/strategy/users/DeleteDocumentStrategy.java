package org.bibsonomy.rest.strategy.users;

import org.apache.log4j.Logger;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.rest.strategy.AbstractDeleteStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 *
 * @version $Id$
 * @author  Christian Kramer
 * $Author$
 *
 */
public class DeleteDocumentStrategy extends AbstractDeleteStrategy {
	private static final Logger LOGGER = Logger.getLogger(DeleteDocumentStrategy.class);
	
	private final String userName;
	private final String resourceHash;
	private final String fileName;
	
	/**
	 * @param context
	 * @param userName
	 * @param resourceHash
	 * @param fileName
	 */
	public DeleteDocumentStrategy(final Context context, final String userName, final String resourceHash, final String fileName) {
		super(context);
		this.userName = userName;
		this.resourceHash = resourceHash;
		this.fileName = fileName;
	}
	
	@Override
	public String getContentType() {
		return null;
	}

	@Override
	protected boolean delete() throws InternServerException {
		this.getLogic().deleteDocument(this.userName, this.resourceHash, this.fileName);
		// no exception -> assume success
		return true;
	}
}