package org.bibsonomy.webapp.command.ajax;

/**
 * @author dzo
 * @version $Id$
 */
public class MarkReviewAjaxCommand extends AjaxCommand {
	
	private String username;
	private String hash;
	private boolean helpful;
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return this.username;
	}
	
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * @return the hash
	 */
	public String getHash() {
		return this.hash;
	}
	
	/**
	 * @param hash the hash to set
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}
	
	/**
	 * @return the helpful
	 */
	public boolean isHelpful() {
		return this.helpful;
	}
	
	/**
	 * @param helpful the helpful to set
	 */
	public void setHelpful(boolean helpful) {
		this.helpful = helpful;
	}
	
}
