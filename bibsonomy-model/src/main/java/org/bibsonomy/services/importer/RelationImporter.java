package org.bibsonomy.services.importer;

import java.io.IOException;
import java.util.List;

import org.bibsonomy.model.Tag;

/**
 * Imports relations from a remote service or file. Additional interface, classes
 * can implement together with {@link RemoteServiceBookmarkImporter} or {@link FileBookmarkImporter}.
 * 
 * @author rja
 * @version $Id$
 */
public interface RelationImporter {

	/**
	 * @return The imported relations. 
	 * @throws IOException - if an error opening the file/remote service occured.
	 */
	public List<Tag> getRelations() throws IOException;
	
	/**
	 * Sets the credentials used to authenticate the user against the remote
	 * service.
	 * 
	 * @param userName 
	 * @param password - could be also an API key or the like.
	 */
	public void setCredentials(final String userName, final String password);
	
}
