package org.bibsonomy.webapp.util;

import javax.servlet.http.HttpServletRequest;

/**
 * @author rja
 * @version $Id$
 */
public interface RequestAware {

	
	public void setRequest(HttpServletRequest request);
}
