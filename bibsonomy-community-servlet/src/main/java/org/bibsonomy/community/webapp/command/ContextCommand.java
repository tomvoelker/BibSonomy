package org.bibsonomy.community.webapp.command;

import org.bibsonomy.community.webapp.util.RequestWrapperContext;

/**
 * Defines a command which has access to the RequestWrapperContext
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public interface ContextCommand {

	/**
	 * Get RequestWrapperContext
	 * @return - the request wrapper context
	 */
	public RequestWrapperContext getContext();

	/**
	 * Set RequestWrapperContext
	 * @param context - the request wrapper context
	 */
	public void setContext(RequestWrapperContext context);
}
