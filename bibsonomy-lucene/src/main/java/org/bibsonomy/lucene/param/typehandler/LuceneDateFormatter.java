package org.bibsonomy.lucene.param.typehandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * convert date objects to a standardized string representation
 * 
 * @author fei
 */
public class LuceneDateFormatter extends AbstractTypeHandler {
	
	/** the date formatter */
	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
	
	@Override
	public String getValue(Object obj) {
		return dateFormatter.format((Date)obj);
	}

	@Override
	public Object setValue(String str) {
		Date retVal = null;
		try {
			retVal = dateFormatter.parse(str);
		} catch (ParseException e) {
			log.error("Error parsing date " + str, e);
			retVal = new Date(0);
		}
		
		return retVal;
	}
}