package org.bibsonomy.lucene.param.typehandler;


/**
 * convert date objects to a standardized string representation
 * 
 * @author fei
 * @version $Id$
 */
public class LuceneIntegerFormatter extends AbstractTypeHandler<Integer> {

	@Override
	public String getValue(Integer obj) {
		return obj.toString();
	}

	@Override
	public Integer setValue(String str) {
		try {
			return Integer.parseInt(str);
		} catch( Exception e ) {
			log.error("Error parsing number " + str, e);
		}
				
		return null;
	}
}