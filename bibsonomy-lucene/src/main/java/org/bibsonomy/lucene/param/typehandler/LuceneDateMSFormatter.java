package org.bibsonomy.lucene.param.typehandler;

import java.util.Date;


/**
 * convert date object's milliseconds to a long value (as string)
 * 
 * @author fei
 * @version $Id$
 */
public class LuceneDateMSFormatter extends AbstractTypeHandler<Date> {

	@Override
	public String getValue(Date obj) {
		return String.valueOf(obj.getTime());
	}

	@Override
	public Date setValue(String str) {
		try {
			long ms = Long.parseLong(str);
			return new Date(ms);
		} catch (final Exception e) {
			log.error("Error parsing date " + str, e);
		}
				
		return new Date(0);
	}
}
