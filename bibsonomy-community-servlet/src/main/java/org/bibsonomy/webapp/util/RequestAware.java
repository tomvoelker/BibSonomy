package org.bibsonomy.webapp.util;


/**
 * Interface to allow controllers access to the request logic. 
 * 
 * @author rja
 * @version $Id$
 */
public interface RequestAware {

	
	/** 
	 * @param requestLogic
	 */
	public void setRequestLogic(RequestLogic requestLogic);
}
