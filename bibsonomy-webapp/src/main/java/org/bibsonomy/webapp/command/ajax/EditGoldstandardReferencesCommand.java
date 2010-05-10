package org.bibsonomy.webapp.command.ajax;

import java.util.HashSet;
import java.util.Set;

/**
 * @author dzo
 * @version $Id$
 */
public class EditGoldstandardReferencesCommand extends AjaxCommand {
	private String hash;
	private Set<String> references;
	
	/**
	 * inits the references set
	 */
	public EditGoldstandardReferencesCommand() {
		this.references = new HashSet<String>();
	}
	
	/**
	 * @param hash the hash to set
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}
	
	/**
	 * @return the hash
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * @param references the references to set
	 */
	public void setReferences(Set<String> references) {
		this.references = references;
	}

	/**
	 * @return the references
	 */
	public Set<String> getReferences() {
		return references;
	}
}
