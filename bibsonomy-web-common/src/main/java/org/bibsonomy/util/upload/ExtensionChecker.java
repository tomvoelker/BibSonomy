package org.bibsonomy.util.upload;

import java.util.Collection;

/**
 * @author dzo
 * @version $Id$
 */
public interface ExtensionChecker {
	
	/**
	 * checks extension
	 * @param extension
	 * @return <code>true</code> iff the extension can be uploaded
	 */
	public boolean checkExtension(final String extension);
	
	/**
	 * @return all allowed extensions
	 */
	public Collection<String> getAllowedExtensions();
}
