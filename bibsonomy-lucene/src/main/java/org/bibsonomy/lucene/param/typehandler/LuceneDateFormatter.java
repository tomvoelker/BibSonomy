package org.bibsonomy.lucene.param.typehandler;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * convert date objects to a standardized string representation
 * 
 * @author fei
 */
public class LuceneDateFormatter extends AbstractTypeHandler<Date> {
	
	/** the date formatter */
	private static final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SS");
	
	@Override
	public String getValue(Date obj) {
		return dateFormatter.print(new DateTime(obj));
	}

	@Override
	public Date setValue(String str) {
		try {
			return dateFormatter.parseDateTime(str).toDate();
		} catch (Exception e) {
			return new Date(0);
		}
	}
}