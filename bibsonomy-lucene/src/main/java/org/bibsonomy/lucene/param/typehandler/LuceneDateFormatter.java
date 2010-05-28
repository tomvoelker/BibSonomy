package org.bibsonomy.lucene.param.typehandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * convert date objects to a standardized string representation
 * 
 * @author fei
 * @version $Id$
 */
public class LuceneDateFormatter extends AbstractTypeHandler<Date> {
	
	/** the date formatter */
	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
	
	@Override
	public String getValue(Date obj) {
		return dateFormatter.format(obj);
	}

	@Override
	public Date setValue(String str) {
		try {
			return dateFormatter.parse(str);
		} catch (ParseException e) {
			log.error("Error parsing date " + str, e);
		}
		
		return new Date(0);
	}
}