/**
 * BibSonomy-Rest-Common - Common things for the REST-client and server.
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
package org.bibsonomy.rest.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author wla
 */
public class RestSyncUtils {
	
	private static final String DATE_FORMAT_STRING = "yyyy-MM-DD HH:mm:ss";
	
	public static Date parseDate(final String dateString) throws ParseException {
		DateFormat fmt = new SimpleDateFormat(DATE_FORMAT_STRING);
		return fmt.parse(dateString);
	}
	
	public static String serializeDate(final Date date) {
		DateFormat fmt = new SimpleDateFormat(DATE_FORMAT_STRING);
		return fmt.format(date);
	}
}
