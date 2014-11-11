package org.bibsonomy.webapp.command.ajax;

import java.util.HashSet;
import java.util.Set;

import org.bibsonomy.model.enums.GoldStandardRelation;

/**
 * @author lka
 */
public class EditGoldstandardRelationCommand extends AjaxCommand {
	private String hash;
	private Set<String> references;
	private GoldStandardRelation relation;
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
		String tRelation =  relation.toUpperCase();
		if(tRelation.contains("_MENU")){
			tRelation = tRelation.replaceAll("_MENU", "");
		}
		if(tRelation.contains(" ")){
			for(int i=0;i<tRelation.length();i++){
				if(tRelation.charAt(i)==' '){
					tRelation=tRelation.substring(0, i)+"_"+tRelation.substring(i+1);
				}
			}
		}
		for(GoldStandardRelation r: GoldStandardRelation.values()){
			if(r.name().equalsIgnoreCase(tRelation)){
				this.relation = r;
				break;
			}
		}
	}
	/**
	 * @return the relation
	 */
	public GoldStandardRelation getRelation() {
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
