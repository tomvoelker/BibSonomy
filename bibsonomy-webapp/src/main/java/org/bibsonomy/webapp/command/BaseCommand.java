package org.bibsonomy.webapp.command;

import org.bibsonomy.webapp.util.RequestWrapperContext;

/**
 * Base class for command objects. Contains request and response fields
 * that are commonly used across a lot of controllers.
 * 
 * Command objects normally contain 
 * all request arguments that a controller needs and all response values,
 * which it creates. Views use the information in commands for rendering.
 * 
 * @author Jens Illig
 * @version $Id$
 */
public class BaseCommand implements ContextCommand {
	
	private RequestWrapperContext context;

	@Deprecated
	private String pageTitle;

	/**
	 * @return the page title
	 */
	@Deprecated // i18n in jspx!
	public String getPageTitle() {
		return this.pageTitle;
	}

	/**
	 * 
	 * @param pageTitle the page title
	 */
	@Deprecated
	public void setPageTitle(final String pageTitle) {
		this.pageTitle = pageTitle;
	}

	/** The context contains the loginUser, the ckey, and other things
	 * which can not be changed by the user.
	 * 
	 * @return The context.
	 */
	@Override
	public RequestWrapperContext getContext() {
		return this.context;
	}

	/** Add a context to this command.
	 * @param context
	 */
	@Override
	public void setContext(final RequestWrapperContext context) {
		this.context = context;
	}
}
