package org.bibsonomy.lucene.param.typehandler;

import java.util.Date;


/**
 * convert date object's milliseconds to a long value (as string)
 * 
 * @author fei
 */
public class LuceneDateMSFormatter implements LuceneTypeHandler {

	@Override
	public String getValue(Object obj) {
		return new Long(((Date)obj).getTime()).toString();
	}
}
