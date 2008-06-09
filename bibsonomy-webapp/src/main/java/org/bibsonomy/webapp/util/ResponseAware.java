package org.bibsonomy.webapp.util;

import javax.servlet.http.HttpServletResponse;

/**
 * @author rja
 * @version $Id$
 */
public interface ResponseAware {

	
	public void setResponse(HttpServletResponse response);
}
