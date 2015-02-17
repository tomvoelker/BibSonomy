/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
package de.unikassel.puma.webapp.command.ajax;

import org.bibsonomy.webapp.command.ajax.AjaxCommand;

/**
 * @author philipp
 */
public class PublicationClassificationCommand extends AjaxCommand<String> {

	private String classificationName = "";
	private String id = "";
	private String hash = "";
	private String key = "";
	private String value = "";
	
	/**
	 * @param name
	 */
	public void setClassificationName(final String name) {
		this.classificationName = name;
	}
	
	/**
	 * @return  classification name
	 */
	public String getClassificationName() {
		return this.classificationName;
	}
	
	/**
	 * @param id
	 */
	public void setId(final String id) {
		this.id = id;
	}
	
	/**
	 * @return id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @param hash the has to set
	 */
	public void setHash(final String hash) {
		this.hash = hash;
	}

	/**
	 * @return the intrahash
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(final String key) {
		this.key = key;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(final String value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
}
