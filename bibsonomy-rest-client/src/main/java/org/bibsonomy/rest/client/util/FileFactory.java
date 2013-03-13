package org.bibsonomy.rest.client.util;

import java.io.File;

/**
 * Decides where to store files.
 * 
 * @author Jens Illig
 * @version $Id$
 */
public interface FileFactory {
	/**
	 * @param fileName simple fileName without directory path
	 * @return the file (including directory)
	 */
	public File getFile(String fileName);
}
