package org.bibsonomy.rest.client.queries.post;

import java.io.File;
import java.io.IOException;

import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;

/**
 * @author wbi
 * @version $Id$
 */
public class CreatePostDocumentQuery extends AbstractQuery<String> {

	private final File file;

	private final String username;

	private final String resourceHash;


	public CreatePostDocumentQuery(final String username, final String resourceHash, final File file) throws IOException {

		if ((username == null) || (username.length() == 0)) throw new IllegalArgumentException("no username given");
		if ((resourceHash == null) || (resourceHash.length() == 0)) throw new IllegalArgumentException("no resourceHash given");

		this.username = username;
		this.resourceHash = resourceHash;

		this.file = file;
	}

	@Override
	protected String doExecute() throws ErrorPerformingRequestException {

		this.downloadedDocument = this.performMultipartPostRequest(URL_USERS + "/" + this.username + "/posts/" + this.resourceHash + "/documents", this.file);
		return null;
	}

}
