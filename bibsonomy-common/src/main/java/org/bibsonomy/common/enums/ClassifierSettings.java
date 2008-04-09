package org.bibsonomy.common.enums;

import org.bibsonomy.util.EnumUtils;

/**
 * Defines different settings to control the behaviour of the classifier
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public enum ClassifierSettings {
	/** the classification algorithm */
	ALGORITHM,

	/** the classifier mode (currently DAY or NIGHT) */
	MODE,

	/** classifier training interval in seconds */
	TRAINING_PERIOD,

	/** classify interval in seconds */
	CLASSIFY_PERIOD,

	/** probability to seperate sure from unsure classifications */
	PROBABILITY_LIMIT,

	/** testing mode will not effect user table */
	TESTING;

	/**
	 * @param setting
	 *            name of the setting enum to retrieve
	 * @return the corresponding enum object
	 */
	public static ClassifierSettings getClassifierSettings(final String setting) {
		final ClassifierSettings cs = EnumUtils.searchEnumByName(ClassifierSettings.values(), setting);
		if (cs == null) throw new UnsupportedOperationException();
		return cs;
	}

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
}