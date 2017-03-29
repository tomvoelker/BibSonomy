/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.database.params;

import org.bibsonomy.model.enums.GoldStandardRelation;

/**
 * @author dzo
 */
public class GoldStandardReferenceParam {
	private String hash;
	private String refHash;
	private String username;
	private GoldStandardRelation relation;
	
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
	 * @return the refHash
	 */
	public String getRefHash() {
		return this.refHash;
	}
	
	/**
	 * @param refHash the refHash to set
	 */
	public void setRefHash(String refHash) {
		this.refHash = refHash;
	}
	
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
	 * @return the relation
	 */
	public GoldStandardRelation getRelation() {
		return this.relation;
	}
	
	/**
	 * @param relation the relation to set
	 */
	public void setRelation(GoldStandardRelation relation) {
		this.relation = relation;
	}
}
