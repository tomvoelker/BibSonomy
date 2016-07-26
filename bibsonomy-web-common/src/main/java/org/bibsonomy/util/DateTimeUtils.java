/**
 * BibSonomy-Web-Common - Common things for web
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
package org.bibsonomy.util;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Date;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author rja, dzo
 */
public class DateTimeUtils {
	private static final Log log = LogFactory.getLog(DateTimeUtils.class);

	/** used to get RFC 1123 formatted date */
	public static final DateTimeFormatter RFC1123_DATE_TIME_FORMATTER = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'").withZoneUTC().withLocale(Locale.US);

	/**
	 * @see #formatDateRFC1123(DateTime)
	 * 
	 * @param date
	 * @return the formatted date
	 */
	public static String formatDateRFC1123(final Date date) {
		if (present(date)) {
			return formatDateRFC1123(new DateTime(date));
		}
		return "";
	}
	
	/**
	 * Formats the date to RFC 1123, e.g.,  Wed, 30 May 2007 18:47:52 GMT
	 * 
	 * Currently Java's formatter doesn't support this standard therefore we can
	 * not use the fmt:formatDate tag with a pattern
	 * 
	 * @param date
	 * @return the formatted date
	 */
	public static String formatDateRFC1123(final DateTime date) {
		try {
			return RFC1123_DATE_TIME_FORMATTER.print(new DateTime(date));
		} catch (final Exception e) {
			log.error("error while formating date to RFC 1123", e);
			return "";
		}
	}
}
