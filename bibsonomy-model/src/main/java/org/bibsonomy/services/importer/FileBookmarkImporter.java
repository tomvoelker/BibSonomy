package org.bibsonomy.services.importer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;

/**
 * Allows to import lists of bookmarks from a file. 
 * 
 * @author rja
 * @version $Id$
 */
public interface FileBookmarkImporter {

	/**
	 * initializes the file which contains the bookmarks, the current user and his group.
	 * 
	 * @param file 
	 * @param user 
	 * @param groupName 
	 * @throws IOException - if the file could not be opened/read. 
	 * 
	 */
	public void initialize(File file, User user, String groupName) throws IOException;
	
	/**
	 * Returns the bookmarks extracted from the given file (see {@link #setFile(File)}). 
	 * 
	 * @return A list of bookmark posts, extracted from the given file.
	 */
	public List<Post<Bookmark>> getPosts();
	
}
