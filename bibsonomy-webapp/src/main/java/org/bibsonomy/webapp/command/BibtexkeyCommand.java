package org.bibsonomy.webapp.command;

/**
 * 
 * Bean for providing the bibtexkey
 * 
 * @author Flori
 * @version $Id$
 */
public class BibtexkeyCommand extends ResourceViewCommand{
	
	/** String to search for */
	private String requestKey = "";
	
	/**
	 * @return the requested bibtexKey 
	 */
	public String getRequestKey() {
		return this.requestKey;
	}

	/**
	 * @param requKey set the bibtexKey 
	 */
	public void setRequestKey(String requKey) {
		this.requestKey = requKey;
	}
}
