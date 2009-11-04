package org.bibsonomy.lucene.param.typehandler;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * convert date objects to a standardized string representation
 * 
 * @author fei
 */
public class LuceneDateFormatter implements LuceneTypeHandler {
	/** the date formatter */
	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
	
	@Override
	public String getValue(Object obj) {
		return dateFormatter.format((Date)obj);
	}
}
