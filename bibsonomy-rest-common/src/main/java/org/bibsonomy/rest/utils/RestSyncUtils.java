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
	
	static {
	}
	public static Date parseDate(final String dateString) {
		Date date = null;
		try {
			date = sdf.parse(dateString);
		} catch (final ParseException ex) {
			ex.printStackTrace();
		}
		return date;
	}
	
	public static String serializeDate(final Date date) {
		
		final String formattedDate = sdf.format(date);
		return formattedDate;
	}
}
