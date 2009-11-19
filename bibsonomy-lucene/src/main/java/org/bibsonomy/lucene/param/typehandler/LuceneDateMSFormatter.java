package org.bibsonomy.lucene.param.typehandler;

import java.util.Date;


/**
 * convert date object's milliseconds to a long value (as string)
 * 
 * @author fei
 */
public class LuceneDateMSFormatter extends AbstractTypeHandler {

	@Override
	public String getValue(Object obj) {
		return new Long(((Date)obj).getTime()).toString();
	}

	@Override
	public Object setValue(String str) {
		Date retVal = null;
		
		try {
			long ms = Long.parseLong(str);
			retVal  = new Date(ms);
		} catch( Exception e ) {
			log.error("Error parsing date " + str, e);
			retVal = new Date(0);
		}
				
		return retVal;
	}
}
