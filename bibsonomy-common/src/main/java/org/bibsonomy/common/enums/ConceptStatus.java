/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
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
package org.bibsonomy.common.enums;

import org.bibsonomy.common.exceptions.UnsupportedConceptStatusException;
import org.bibsonomy.util.EnumUtils;

/**
 * Defines possible statuses of tag relations.
 * 
 * @author Dominik Benz
 * @author Stefan Stützer
 */
public enum ConceptStatus {
	/** Concept is picked */
	PICKED,
	/** Concept is unpicked */
	UNPICKED,
	/** Concept is all TODO: what does "all" mean? */
	ALL;

	/**
	 * @param conceptStatus -
	 *            name of the ConceptStatus to retrieve
	 * @return the corresponding ConceptStatus-enum
	 */
	public static ConceptStatus getConceptStatus(final String conceptStatus) {
		final ConceptStatus cs = EnumUtils.searchEnumByName(ConceptStatus.values(), conceptStatus);
		if (cs == null) throw new UnsupportedConceptStatusException(conceptStatus);
		return cs;
	}

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
}