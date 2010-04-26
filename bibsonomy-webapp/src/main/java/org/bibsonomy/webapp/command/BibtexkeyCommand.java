package org.bibsonomy.webapp.command;

/**
 * 
 * Bean for providing the bibtexkey
 * 
 * @author Flori
 * @version $Id$
 */
public class BibtexkeyCommand extends TagResourceViewCommand{
	
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
	public void setRequestedKey(String requKey) {
		this.requestedKey = requKey;
	}
}
