package org.bibsonomy.services.importer;

import java.io.IOException;
import java.util.List;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

/**
 * Allows to import lists of bookmarks from remote services. 
 * 
 * @author rja
 * @version $Id$
 */
public interface RemoteServiceBookmarkImporter {

	/**
	 * Sets the credentials used to authenticate the user against the remote
	 * service.
	 * 
	 * @param userName 
	 * @param password - could be also an API key or the like.
	 */
	public void setCredentials(final String userName, final String password);
	
	/**
	 * Returns the bookmarks retrieved from the remote service with the given 
	 * credentials (see {@link #setCredentials(String, String)}).
	 * 
	 * @return A list of bookmark posts, queried from the service.
	 * @throws IOException - if the remote service could not be called.
	 */
	public List<Post<Bookmark>> getPosts() throws IOException;
		
}
