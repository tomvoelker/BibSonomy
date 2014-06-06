package org.bibsonomy.webapp.command.ajax;

import java.util.HashSet;
import java.util.Set;

/**
 * @author dzo
 */
public class EditGoldstandardRelationCommand extends AjaxCommand {
	private String hash;
	private Set<String> references;
	private String relation;
	
	/**
	 * inits the references set
	 */
	public EditGoldstandardRelationCommand() {
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
	 * @param relation the relation to set
	 */
	public void setRelation(String relation) {
		this.relation = relation;
	}
	
	/**
	 * @return the relation
	 */
	public String getRelation() {
		return relation;
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
