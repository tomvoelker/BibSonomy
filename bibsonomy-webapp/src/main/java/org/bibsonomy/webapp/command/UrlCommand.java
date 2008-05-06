package org.bibsonomy.webapp.command;

/**
 * 
 * Bean for providing the Url
 * 
 * @author Flori
 * @version $Id$
 */
public class UrlCommand extends ResourceViewCommand{
	
	/** String to search for requestUrl */
	private String requestUrl = "";
	
	/** String to search for requestUrlHash */
	private String requestUrlHash = "";

	/**
	 * @return the requested url 
	 */
	public String getRequestUrl() {
		return this.requestUrl;
	}

	/**
	 * @param requestUrl set the url 
	 */
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	/**
	 * @return the requested url hash
	 */
	public String getRequestUrlHash() {
		return this.requestUrlHash;
	}

	/**
	 * @param requestUrlHash set the url as hash 
	 */
	public void setRequestUrlHash(String requestUrlHash) {
		this.requestUrlHash = requestUrlHash;
	}
}
