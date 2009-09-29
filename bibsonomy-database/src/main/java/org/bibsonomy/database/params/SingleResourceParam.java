package org.bibsonomy.database.params;

import org.bibsonomy.model.Resource;

/**
 * 
 * @author dzo
 * @version $Id$
 * @param <R> the resource class of the param
 */
public interface SingleResourceParam<R extends Resource> {
	
	/**
	 * @return the resource of the param
	 */
	public R getResource();
	
	/**
	 * sets the resource
	 * @param resource
	 */
	public void setResource(R resource);
}
