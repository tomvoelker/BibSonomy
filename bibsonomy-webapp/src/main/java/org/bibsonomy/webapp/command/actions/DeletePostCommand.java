package org.bibsonomy.webapp.command.actions;

import java.io.Serializable;

import org.bibsonomy.webapp.command.BaseCommand;

/**
 * @author Christian Kramer
 * @version $Id$
 */
public class DeletePostCommand extends BaseCommand implements Serializable{
	private static final long serialVersionUID = -6623936347565283765L;
	
	private String resourceHash;

	/**
	 * @return resourceHash
	 */
	public String getResourceHash() {
		return this.resourceHash;
	}

	/**
	 * @param resourceHash
	 */
	public void setResourceHash(final String resourceHash) {
		this.resourceHash = resourceHash;
	}

	
	
}
