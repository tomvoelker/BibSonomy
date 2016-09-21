package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.webapp.command.SettingsViewCommand;

/**
 * TODO: add documentation to this class
 *
 * @author jp
 */
public class CSLImportCommand extends SettingsViewCommand{
	private static final long serialVersionUID = -8231551189396557448L;
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
