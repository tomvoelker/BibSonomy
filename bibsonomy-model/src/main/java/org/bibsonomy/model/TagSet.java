/**
 *  
 *  BibSonomy-Model - Java- and JAXB-Model.
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.model;

import java.util.List;

/**
 * @author mwa
 * @version $Id$
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
	
	public TagSet() {
		
	}
	
	public TagSet(String setName, List<Tag> tags){
		this.setName = setName;
		this.tags = tags;
	}
	
	public List<Tag> getTags() {
		return this.tags;
	}
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
	public String getSetName() {
		return this.setName;
	}
	public void setSetName(String setName) {
		this.setName = setName;
	}
	
	@Override
	public String toString() {
		return setName + ": " + tags;
	}
	
}
