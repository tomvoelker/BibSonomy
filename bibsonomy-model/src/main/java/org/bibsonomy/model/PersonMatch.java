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

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * A PersonMatch object contains the id's of two persons which might be equal and a flag if they are equal
 *
 * Merge relations, names and attributes FROM source INTO target. Leaving target as the merged result.
 *
 * @author jhi
 */
@Getter
@Setter
public class PersonMatch implements Serializable {
	private static final long serialVersionUID = -470932185819510145L;

	public static final int MAX_NUMBER_OF_DENIES = 5;
	
	private Person targetPerson;
	private Person sourcePerson;
	private int state; // 0 open, 1 denied, 2 already merged
	private int matchID;
	private List<String> userDenies;
	private List<Post<? extends Resource>> targetPosts;
	private List<Post<? extends Resource>> sourcePosts;
	

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PersonMatch that = (PersonMatch) o;
		return Objects.equals(targetPerson, that.targetPerson) && Objects.equals(sourcePerson, that.sourcePerson) ||
				Objects.equals(targetPerson, that.sourcePerson) && Objects.equals(sourcePerson, that.targetPerson);
	}

	@Override
	public int hashCode() {
		return Objects.hash(targetPerson, sourcePerson);
	}

}
