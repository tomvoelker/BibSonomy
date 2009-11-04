package org.bibsonomy.lucene.param.typehandler;

/**
 * interface for converting objects to corresponding string represantations 
 * for storing into the lucene index
 * 
 * FIXME: this is probably only a hack for handling the oddity, that dates are
 *        represented by a string value
 * 
 * @author fei
 *
 */
public interface LuceneTypeHandler {
	
	/**
	 * get given object's string representation
	 */
	String getValue(Object obj);
}
