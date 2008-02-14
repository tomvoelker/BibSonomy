package org.bibsonomy.common.enums;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.UnsupportedConceptStatusException;
import org.bibsonomy.common.exceptions.UnsupportedGroupingException;

/**
 * defines possible statusses of tag relations
 * 
 * @author Dominik Benz
 * @author Stefan Stützer
 * @version $Id$
 */
public enum ConceptStatus {
	PICKED, UNPICKED, ALL;
	
	/**
	 * @param conceptStatus - name of the ConceptStatus to retrieve
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
		if (this.equals(PICKED)) {
			return "picked";
		} else if (this.equals(UNPICKED)) {
			return "unpicked";
		} else if (this.equals(ALL)) {
			return "all";
		}
		return super.toString();
	}	
}