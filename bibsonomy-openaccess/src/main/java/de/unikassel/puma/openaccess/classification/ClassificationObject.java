/**
 * BibSonomy-OpenAccess - Check Open Access Policies for Publications
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
package de.unikassel.puma.openaccess.classification;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author philipp
 */
public class ClassificationObject {

	private final Map<String , ClassificationObject> children;
	
	private String name;

	private String description;
	
	/**
	 * constructor for setting name and description
	 * @param name	the name
	 * @param description the description
	 */
	public ClassificationObject(final String name, final String description) {
		this.name = name;
		this.description = description;

		this.children = new LinkedHashMap<String, ClassificationObject>();
	}
	
	/**
	 * adds a child 
	 * @param name the name of the child
	 * @param co the child
	 */
	public void addChild(final String name, final ClassificationObject co) {
		this.children.put(name, co);
	}
	
	/**
	 * @return the children
	 */
	public Map<String, ClassificationObject> getChildren() {
		return this.children;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}
}
