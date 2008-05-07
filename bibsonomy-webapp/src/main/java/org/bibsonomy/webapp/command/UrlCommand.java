package org.bibsonomy.webapp.command;

/**
 * 
 * Bean for providing the Url
 * 
 * @author Flori
 * @version $Id$
 */
public class UrlCommand extends ResourceViewCommand{
	
	/** String to search for requUrl */
	private String requUrl = "";
	
	/** String to search for requUrlHash */
	private String requUrlHash = "";

	/**
	 * @return the requested url 
	 */
	public String getRequUrl() {
		return this.requUrl;
	}

	/**
	 * @param requUrl set the url 
	 */
	public void setRequUrl(String requUrl) {
		this.requUrl = requUrl;
	}

	/**
	 * @return the requested url hash
	 */
	public String getRequUrlHash() {
		return this.requUrlHash;
	}

	/**
	 * @param requUrlHash set the url as hash 
	 */
	public void setRequUrlHash(String requUrlHash) {
		this.requUrlHash = requUrlHash;
	}
}
