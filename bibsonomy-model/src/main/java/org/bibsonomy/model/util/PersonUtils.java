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
package org.bibsonomy.model.util;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * util methods for {@link Person}
 *
 * @author dzo
 */
public final class PersonUtils {
	private PersonUtils() {}
	
	/**
	 * generates the base of person identifier
	 * @param person
	 * @return the base of the person identifier
	 */
	public static String generatePersonIdBase(final Person person) {
		final String firstName = person.getMainName().getFirstName();
		final String lastName  = person.getMainName().getLastName();
		
		if (!present(lastName)) {
			throw new IllegalArgumentException("lastName may not be empty");
		}
		
		final StringBuilder sb = new StringBuilder();
		if (present(firstName)) {
			sb.append(normName(firstName).charAt(0));
			sb.append('.');
		}
		sb.append(normName(lastName));
	
		return sb.toString();
	}

	/**
	 * @param name
	 * @return
	 */
	private static String normName(final String name) {
		return StringUtils.foldToASCII(name.trim().toLowerCase().replaceAll("\\s", "_"));
	}

	/**
	 * finds the top resource relations that should be displayed for the person
	 *
	 * @param relations
	 * @return
	 */
	public static ResourcePersonRelation findTopRelation(final List<ResourcePersonRelation> relations) {
		if (!present(relations)) {
			return null;
		}

		// prefer a kind of thesis
		for (final String type : Arrays.asList(BibTexUtils.PHD_THESIS, BibTexUtils.MASTERS_THESIS, BibTexUtils.THESIS)) {
			final ResourcePersonRelation relationByType = findRelationByType(type, relations);
			if (present(relationByType)) {
				return relationByType;
			}
		}

		// prefer authors
		findRelationByRelationType(PersonResourceRelationType.AUTHOR, relations);

		// fall back
		return relations.get(0);
	}

	private static ResourcePersonRelation findRelationByRelationType(PersonResourceRelationType relationType, List<ResourcePersonRelation> relations) {
		for (ResourcePersonRelation relation : relations) {
			if (relationType.equals(relation.getRelationType())) {
				return relation;
			}
		}

		return null;
	}

	private static ResourcePersonRelation findRelationByType(String type, List<ResourcePersonRelation> relations) {
		for (ResourcePersonRelation relation : relations) {
			if (type.equals(relation.getPost().getResource().getType())) {
				return relation;
			}
		}

		return null;
	}
}
