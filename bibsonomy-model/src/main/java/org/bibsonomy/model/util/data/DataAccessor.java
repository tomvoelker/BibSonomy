package org.bibsonomy.model.util.data;


/**
 * Abstraction for a collection of data resources with names.
 * 
 * @author Jens Illig
 * @version $Id$
 */
public interface DataAccessor {

	/**
	 * @param multipartName the name of the resource
	 * @return mimetype string of the data
	 */
	public Data getData(String multipartName);
}
