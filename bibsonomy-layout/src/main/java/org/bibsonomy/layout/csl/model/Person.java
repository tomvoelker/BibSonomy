/**
 * BibSonomy-Layout - Layout engine for the webapp.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.layout.csl.model;


/**
 * Models a Person according to CSL input specs. See
 * http://gsl-nagoya-u.net/http/pub/citeproc-doc.html#names
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 */
public class Person {
	
	// family name
	private String family;
	// given name
	private String given;
	// dropping particle
	private String dropping_particle;
	// non-dropping particle
	private String non_dropping_particle;
	// literal version of name
	private String literal;
	// name suffix
	private String suffix;
	// comma suffix
	private String comma_suffix;
	// whether to use static ordering or not
	private Integer static_ordering;
	
	/**
	 * @return the family
	 */
	public String getFamily() {
		return this.family;
	}
	
	/**
	 * @param family the family to set
	 */
	public void setFamily(String family) {
		this.family = family;
	}
	
	/**
	 * @return the given
	 */
	public String getGiven() {
		return this.given;
	}
	
	/**
	 * @param given the given to set
	 */
	public void setGiven(String given) {
		this.given = given;
	}
	
	/**
	 * @return the dropping_particle
	 */
	public String getDropping_particle() {
		return this.dropping_particle;
	}
	
	/**
	 * @param dropping_particle the dropping_particle to set
	 */
	public void setDropping_particle(String dropping_particle) {
		this.dropping_particle = dropping_particle;
	}
	
	/**
	 * @return the non_dropping_particle
	 */
	public String getNon_dropping_particle() {
		return this.non_dropping_particle;
	}
	
	/**
	 * @param non_dropping_particle the non_dropping_particle to set
	 */
	public void setNon_dropping_particle(String non_dropping_particle) {
		this.non_dropping_particle = non_dropping_particle;
	}
	
	/**
	 * @return the literal
	 */
	public String getLiteral() {
		return this.literal;
	}
	
	/**
	 * @param literal the literal to set
	 */
	public void setLiteral(String literal) {
		this.literal = literal;
	}
	
	/**
	 * @return the suffix
	 */
	public String getSuffix() {
		return this.suffix;
	}
	
	/**
	 * @param suffix the suffix to set
	 */
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	/**
	 * @return the comma_suffix
	 */
	public String getComma_suffix() {
		return this.comma_suffix;
	}
	
	/**
	 * @param comma_suffix the comma_suffix to set
	 */
	public void setComma_suffix(String comma_suffix) {
		this.comma_suffix = comma_suffix;
	}
	
	/**
	 * @return the static_ordering
	 */
	public Integer getStatic_ordering() {
		return this.static_ordering;
	}
	
	/**
	 * @param static_ordering the static_ordering to set
	 */
	public void setStatic_ordering(Integer static_ordering) {
		this.static_ordering = static_ordering;
	}
}
