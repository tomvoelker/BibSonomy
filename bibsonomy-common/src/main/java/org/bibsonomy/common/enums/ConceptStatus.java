package org.bibsonomy.common.enums;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.UnsupportedConceptStatusException;

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
	/** Concept is all */
	ALL;

	/**
	 * @param conceptStatus -
	 *            name of the ConceptStatus to retrieve
	 * @return the corresponding ConceptStatus-enum
	 */
	public static ConceptStatus getConceptStatus(final String conceptStatus) {
		if (conceptStatus == null) throw new InternServerException("ConceptStatus is null");
		final String status = conceptStatus.toLowerCase().trim();
		if ("picked".equals(status)) {
			return PICKED;
		} else if ("unpicked".equals(status)) {
			return UNPICKED;
		} else if ("all".equals(status)) {
			return ALL;
		} else {
			throw new UnsupportedConceptStatusException(conceptStatus);
		}
	}

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
}