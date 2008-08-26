package org.bibsonomy.common.enums;

import org.bibsonomy.common.exceptions.UnsupportedConceptStatusException;
import org.bibsonomy.util.EnumUtils;

/**
 * Defines possible statuses of tag relations.
 * 
 * @author Dominik Benz
 * @author Stefan Stützer
 * @version $Id$
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