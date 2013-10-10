package org.bibsonomy.services.filesystem;

import org.bibsonomy.common.enums.LayoutPart;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.util.file.UploadedFile;

/**
 * @author dzo
 * @version $Id$
 */
public interface JabRefFileLogic {
	
	/**
	 * 
	 * @param username
	 * @param file
	 * @param layoutPart
	 * @return the document representing the jabref layout file
	 * @throws Exception
	 */
	public Document writeJabRefLayout(String username, UploadedFile file, LayoutPart layoutPart) throws Exception;
	
	/**
	 * @param hash
	 * @return <code>true</code> if file was deleted
	 */
	public boolean deleteJabRefLayout(final String hash);
}