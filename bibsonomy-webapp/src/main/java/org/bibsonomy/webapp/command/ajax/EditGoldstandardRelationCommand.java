/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
