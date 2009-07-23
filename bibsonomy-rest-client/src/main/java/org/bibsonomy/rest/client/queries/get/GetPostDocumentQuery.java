package org.bibsonomy.rest.client.queries.get;

import java.io.File;
import java.io.IOException;

import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;

/**
 * Downloads a document for a specific post.
 * @author Waldemar Biller <wbi@cs.uni-kassel.de>
 * @version $Id$
 */
public class GetPostDocumentQuery extends AbstractQuery<File> {

	private File document;
	private final String username;
	private final String resourceHash;
	private boolean fileExists;

	/**
	 * @param username
	 * @param resourceHash the resource hash of a specific post
	 * @param fileName the file name of the document
	 */
	public GetPostDocumentQuery(final String username, final String resourceHash, final String fileName) {

		if ((username == null) || (username.length() == 0)) throw new IllegalArgumentException("no username given");
		if ((resourceHash == null) || (resourceHash.length() == 0)) throw new IllegalArgumentException("no resourceHash given");
		if ((fileName == null) || (fileName.length() == 0)) throw new IllegalArgumentException("no file name given");

		this.username = username;
		this.resourceHash = resourceHash;

		// create the file
		try {

			this.document = new File("bibsonomy_docs/" + fileName);
			this.fileExists = !this.document.createNewFile();
		} catch (final IOException ex) {
			throw new IllegalArgumentException("could not create new file " + this.document.getAbsolutePath());
		}
	}

	@Override
	protected File doExecute() throws ErrorPerformingRequestException {
		if(!this.fileExists)
			this.performFileDownload(URL_USERS + "/" + this.username + "/posts/" + this.resourceHash + "/documents/" + this.document.getName(), this.document);
		else {
			this.setExecuted(true);
			this.setStatusCode(200);
		}
		return this.document;
	}

}
