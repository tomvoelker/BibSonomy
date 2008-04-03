package org.bibsonomy.common.enums;

import org.bibsonomy.common.exceptions.UnsupportedConceptStatusException;
import org.bibsonomy.util.EnumUtils;

/**
 * The working mode of an automatic classify 
 * algorithm 
 * 
 * @author Stefan Stützer
 *
 */
public enum ClassifierMode {
	/** day mode */
	DAY("D"),
	
	/** night mode */
	NIGHT("N");
	
	private String abbreviation;
		
	private ClassifierMode(String abbr) {
		this.abbreviation = abbr;
	}
	
	public static ClassifierMode getMode(final String mode) {
		if ("D".equals(mode))
			return DAY;
		else 
			return NIGHT;
	}
	/**
	 * @return abbreviation of the specified mode
	 */
	public String getAbbreviation() {
		return abbreviation;
	}
}