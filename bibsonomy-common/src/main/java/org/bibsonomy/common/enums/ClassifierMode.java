package org.bibsonomy.common.enums;

/**
 * The working mode of an automatic classify algorithm
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public enum ClassifierMode {
	/** day mode */
	DAY("D"),

	/** night mode */
	NIGHT("N");

	private String abbreviation;

	private ClassifierMode(final String abbreviation) {
		this.abbreviation = abbreviation;
	}

	/**
	 * @param mode
	 * @return the corresponding enum
	 */
	public static ClassifierMode getMode(final String mode) {
		if ("D".equals(mode)) return DAY;
		return NIGHT;
	}

	/**
	 * @return abbreviation of the specified mode
	 */
	public String getAbbreviation() {
		return abbreviation;
	}
}