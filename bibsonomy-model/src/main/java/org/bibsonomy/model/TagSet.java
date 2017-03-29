/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
package org.bibsonomy.model;

import java.util.List;

/**
 * @author mwa
 */
public class TagSet {

	/**
	 * List of tags in the set
	*/
	private List<Tag> tags;
	
	/**
	 * Name of the set
	*/
	private String setName;
	
	/**
	 * default constructor
	 */
	public TagSet() {	
	}
	
	/**
	 * sets setName and tags
	 * @param setName
	 * @param tags
	 */
	public TagSet(String setName, List<Tag> tags){
		this.setName = setName;
		this.tags = tags;
	}
	
	/**
	 * @return the tags
	 */
	public List<Tag> getTags() {
		return this.tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	/**
	 * @return the setName
	 */
	public String getSetName() {
		return this.setName;
	}

	/**
	 * @param setName the setName to set
	 */
	public void setSetName(String setName) {
		this.setName = setName;
	}

	@Override
	public String toString() {
		return setName + ": " + tags;
	}
	
}
