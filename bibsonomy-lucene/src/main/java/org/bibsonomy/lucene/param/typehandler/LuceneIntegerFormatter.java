package org.bibsonomy.lucene.param.typehandler;


/**
 * convert date objects to a standardized string representation
 * 
 * @author fei
 */
public class LuceneIntegerFormatter extends AbstractTypeHandler {

	@Override
	public String getValue(Object obj) {
		return obj.toString();
	}

	@Override
	public Object setValue(String str) {
		Integer retVal = null;
		
		try {
			retVal = Integer.parseInt(str);
		} catch( Exception e ) {
			log.error("Error parsing number " + str, e);
		}
				
		return retVal;
	}
}