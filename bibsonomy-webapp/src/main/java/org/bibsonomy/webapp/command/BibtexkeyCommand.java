package org.bibsonomy.webapp.command;

import org.bibsonomy.webapp.command.resource.PublicationPageCommand;

/**
 * 
 * Bean for providing the bibtexkey
 * 
 * @author Flori
 * @version $Id$
 */
public class BibtexkeyCommand extends PublicationPageCommand {
	
	/** String to search for */
	private String requestedKey = "";
	
	/**
	 * @return the requested bibtexKey 
	 */
	public String getRequestedKey() {
		return this.requestedKey;
	}

	/**
	 * @param requKey set the bibtexKey 
	 */
	public void setRequestedKey(final String requKey) {
		this.requestedKey = requKey;
	}
}
