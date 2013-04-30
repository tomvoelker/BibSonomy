package org.bibsonomy.webapp.util.spring.controller;

/**
 * Sets the request mime-type
 * 
 * @author Jens Illig
 * @version $Id$
 */
public interface MimeTypeCommand {
	
	/**
	 * @param mimeType
	 */
	public void setMimeType(String mimeType);
}
