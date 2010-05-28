package org.bibsonomy.lucene.param.typehandler;

/**
 * interface for converting objects to corresponding string representations 
 * for storing into the lucene index
 * 
 * @author fei
 * @version $Id$
 * @param <T> the type to handle
 */
public interface LuceneTypeHandler<T> {
	
	/**
	 * get given object's string representation
	 * @param obj 
	 * @return the string representation of obj
	 */
	public String getValue(T obj);

	/**
	 * 
	 * @param str 
	 * @return the object to the str value
	 */
	public T setValue(String str);
}
