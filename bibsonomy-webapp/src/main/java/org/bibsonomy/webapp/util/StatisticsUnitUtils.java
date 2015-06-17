/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.util;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Date;

import org.bibsonomy.common.enums.StatisticsUnit;
import org.joda.time.DateTime;

/**
 * utils class for {@link StatisticsUnit}
 *
 * @author dzo
 */
public class StatisticsUnitUtils {
	private StatisticsUnitUtils() {}

	/**
	 * @param interval
	 * @param unit
	 * @return the date representing the start of the time interval
	 */
	public static Date convertToStartDate(final Integer interval, final StatisticsUnit unit) {
		if (present(interval)) {
			DateTime dateTime = new DateTime();
			final int intervalAsInt = interval.intValue();
			switch (unit) {
			case HOUR:
				dateTime = dateTime.minusHours(intervalAsInt);
				break;
			case DAY:
				dateTime = dateTime.minusDays(intervalAsInt);
				break;
			case MONTH:
				dateTime = dateTime.minusMonths(intervalAsInt);
				break;
			default:
				throw new IllegalArgumentException(unit.toString());
			}
			return dateTime.toDate();
		}
		return null;
	}
}
