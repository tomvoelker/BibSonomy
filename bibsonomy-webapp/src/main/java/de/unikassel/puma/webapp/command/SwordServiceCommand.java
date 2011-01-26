package de.unikassel.puma.webapp.command;

import org.bibsonomy.webapp.command.ajax.AjaxCommand;

/**
 * @author philipp
 * @version $Id$
 */
public class SwordServiceCommand extends AjaxCommand {

	private String resourceHash;
	
	/**
	 * @param post
	 */
	public void setResourceHash(String resourceHash) {
		this.resourceHash = resourceHash;
	}
	
	public String getResourceHash() {
		return this.resourceHash;
	}
	
	
}
