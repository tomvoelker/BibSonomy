package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.webapp.command.SettingsViewCommand;

/**
 * @author cvo
 */
public class JabRefImportCommand extends SettingsViewCommand {
	private static final long serialVersionUID = -2852728956746251923L;

	/**
	 * hash of the layout definition
	 */
	private String hash;

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
}
