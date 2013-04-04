package org.bibsonomy.rest.client.queries.delete;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.enums.Status;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * @author MarcelM
 * @version $Id$
 */
public class DeletePostDocumentQuery extends AbstractQuery<String>{
	
	private final String userName;
	private final String resourceHash;
	private final String fileName;
	
	/**
	 * Deletes a document from a post.
	 * 
	 * @param username, the user name owning the document/post
	 * @param resourceHash, hash connected to the post
	 * @param fileName, the fileName of the document 
	 */
	public DeletePostDocumentQuery(final String userName, final String resourceHash, final String fileName) {
		if (!present(userName)) throw new IllegalArgumentException("no username given");
		if (!present(resourceHash)) throw new IllegalArgumentException("no resourcehash given");
		if (!present(fileName)) throw new IllegalArgumentException("no file name given");
		
		this.userName = userName;
		this.resourceHash = resourceHash;
		this.fileName = fileName;
	}
	
	@Override
	protected String doExecute() throws ErrorPerformingRequestException {
		this.downloadedDocument = performRequest(HttpMethod.DELETE, RESTConfig.USERS_URL + "/" + userName + "/" + 
												 RESTConfig.POSTS_URL + "/" + resourceHash + "/" + 
												 RESTConfig.DOCUMENTS_SUB_PATH + "/" + fileName,null); 
		return null;
	}
	
	@Override
	public String getResult() throws BadRequestOrResponseException, IllegalStateException {
		if (this.isSuccess())
			return Status.OK.getMessage();
		return this.getError();
	}

}
