package org.bibsonomy.rest.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author wla
 * @version $Id$
 */
public class RestSyncUtils {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
	
	public static Date parseDate(final String dateString) throws ParseException {
		return sdf.parse(dateString);
	}
	
	public static String serializeDate(final Date date) {
		return sdf.format(date);
	}
}
