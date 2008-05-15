package org.bibsonomy.util;

import java.util.Date;

/**
 * Helper functions for dates
 * 
 * @author Dominik Benz
 * @version $Id$
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
