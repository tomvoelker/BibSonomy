/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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

import java.util.Date;

/**
 * Helper functions for dates
 * 
 * @author Dominik Benz
 */
public class DateUtils {
	/** 
	 * Compares two dates like compareTo but with additional checks, if one of the dates is NULL.
	 * 
	 * @param d1
	 * @param d2
	 * @return 0 if d1 == null and d2 == null, -1 if d1 == null, 1 if d2 == null
	 */
	public static int secureCompareTo(final Date d1, final Date d2) {
		// null = d1 = d2 = null
		if (d1 == null && d2 == null) return 0;
		// null = d1 < d2 != null
		if (d1 == null) return -1;
		// null != d1 > d2 = null
		if (d2 == null) return 1;
		// null != d1 ? d2 != null
		return d1.compareTo(d2);
	}
}
