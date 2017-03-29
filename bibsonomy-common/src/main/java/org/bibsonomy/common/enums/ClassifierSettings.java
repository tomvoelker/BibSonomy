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

import org.bibsonomy.util.EnumUtils;

/**
 * Defines different settings to control the behaviour of the classifier
 * 
 * @author Stefan Stützer
 * @author Beate Krause
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

	/** probability to separate sure from unsure classifications */
	PROBABILITY_LIMIT,

	/** TODO remove, testing mode will not effect user table */
	TESTING,
	
	/** last classification date to track changes in user profiles */
	LASTCLASSIFICATION,
	
	/** classify cost for cost sensitive classifiers */
	CLASSIFY_COST,
	
	/** expression to add to whitelist */
	WHITELIST_EXP;

	/**
	 * @param setting
	 *            name of the setting enum to retrieve
	 * @return the corresponding enum object
	 */
	@Deprecated // TODO: use enum binding
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