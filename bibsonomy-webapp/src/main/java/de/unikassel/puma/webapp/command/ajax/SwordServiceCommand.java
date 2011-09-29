package de.unikassel.puma.webapp.command.ajax;

import org.bibsonomy.webapp.command.ajax.AjaxCommand;

/**
 * @author philipp
 * @version $Id$
 */
public class SwordServiceCommand extends AjaxCommand {

	private String resourceHash;

	/**
	 * @return the resourceHash
	 */
	public String getResourceHash() {
		return this.resourceHash;
	}

	/**
	 * @param resourceHash the resourceHash to set
	 */
	public void setResourceHash(final String resourceHash) {
		this.resourceHash = resourceHash;
	}	
}
