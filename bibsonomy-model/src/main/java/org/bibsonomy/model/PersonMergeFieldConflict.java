/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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

import lombok.Getter;
import lombok.Setter;

/**
 * A PersonMergeFieldConflict contains the field that is raising the conflict and the values of the persons that might be the same
 *
 * @author jhi
 */
@Getter
@Setter
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

}
