package org.bibsonomy.rest.client.util;

/**
 * @author dzo
 * @version $Id$
 */
public interface ProgressCallbackFactory {
	
	/**
	 * @return creates a new progress callback for document download
	 */
	public ProgressCallback createDocumentDownloadProgressCallback();	
}
