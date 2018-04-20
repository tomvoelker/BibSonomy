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

/**
 * A PersonMergeFieldConflict contains the field that is raising the conflict and the values of the persons that might be the same
 *
 * @author jhi
 */
public class PersonMergeFieldConflict {
	
	private String fieldName;
	private String person1Value;
	private String person2Value;
	
	/**
	 * A PersonMergeFieldConflict contains the field that is raising the conflict and the values of the persons that might be the same
	 * 
	 * @param fieldName
	 * @param person1Value
	 * @param person2Value
	 */
	public PersonMergeFieldConflict(String fieldName, String person1Value, String person2Value){
		this.fieldName = fieldName;
		this.person1Value = person1Value;
		this.person2Value = person2Value;
	}
	
	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return this.fieldName;
	}
	/**
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	/**
	 * @return the person1Value
	 */
	public String getPerson1Value() {
		return this.person1Value;
	}
	/**
	 * @param person1Value the person1Value to set
	 */
	public void setPerson1Value(String person1Value) {
		this.person1Value = person1Value;
	}
	/**
	 * @return the person2Value
	 */
	public String getPerson2Value() {
		return this.person2Value;
	}
	/**
	 * @param person2Value the person2Value to set
	 */
	public void setPerson2Value(String person2Value) {
		this.person2Value = person2Value;
	}
}
