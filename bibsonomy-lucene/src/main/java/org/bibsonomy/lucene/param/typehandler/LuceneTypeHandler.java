package org.bibsonomy.lucene.param.typehandler;

/**
 * interface for converting objects to corresponding string represantations 
 * for storing into the lucene index
 * 
 * FIXME: make this class generic
 * 
 * @author fei
 *
 */
public interface LuceneTypeHandler {
	
	/**
	 * get given object's string representation
	 */
	String getValue(Object obj);

	/**
	 * get given object's string representation
	 */
	Object setValue(String str);
}
