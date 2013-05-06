package org.bibsonomy.model.util.data;


/**
 * {@link DataAccessor} which always returns null
 * 
 * @author Jens Illig
 * @version $Id$
 */
public class NoDataAccessor implements DataAccessor {
	private static final NoDataAccessor instance = new NoDataAccessor();
	
	/**
	 * @return a singleton instance
	 */
	public static NoDataAccessor getInstance() {
		return instance;
	}
	
	@Override
	public Data getData(String multipartName) {
		return null;
	}

}
