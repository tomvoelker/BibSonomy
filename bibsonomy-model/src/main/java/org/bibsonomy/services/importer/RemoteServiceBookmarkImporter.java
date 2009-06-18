package org.bibsonomy.services.importer;

import java.io.File;
import java.util.List;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

/**
 * Allows to import lists of bookmarks from a file. 
 * 
 * @author rja
 * @version $Id$
 */
public interface RemoteServiceBookmarkImporter {

	/**
	 * Sets the file which contains the bookmarks.
	 * 
	 * @param file 
	 * 
	 */
	public void setFile(File file);
	
	/**
	 * Returns the bookmarks extracted from the given file (see {@link #setFile(File)}). 
	 * 
	 * @return A list of bookmark posts, extracted from the given file.
	 */
	public List<Post<Bookmark>> getPosts();
	
}
