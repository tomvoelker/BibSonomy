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
