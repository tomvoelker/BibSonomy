/*
 * Created on 14.10.2007
 */
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
 */
public class BaseCommand implements ContextCommand {
	
	/*
	 * needed for reading value for use in login switch
	 */
	private String loginMethod = "";

	private RequestWrapperContext context;

	private String pageTitle;
	private String requPath;


	/**
	 * @return the requested path
	 */
	public String getRequPath() {
		return this.requPath;
	}

	/**
	 * @param requPath the requested path
	 */
	public void setRequPath(String requPath) {
		this.requPath = requPath;
	}

	/**
	 * @return the page title
	 */
	public String getPageTitle() {
		return this.pageTitle;
	}

	/**
	 * TODO: use localization to resolve page titles
	 * 
	 * @param pageTitle the page title
	 */
	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}
	
	/**
	 * Helper function to expose the name of the current command
	 * to the JSPs
	 * 
	 * @return the class name of the current command
	 */
	public String getCommandName() {
		return this.getClass().getSimpleName();
	}

	/** The context contains the loginUser, the ckey, and other things
	 * which can not be changed by the user.
	 * 
	 * @return The context.
	 */
	public RequestWrapperContext getContext() {
		return this.context;
	}

	/** Add a context to this command.
	 * @param context
	 */
	public void setContext(RequestWrapperContext context) {
		this.context = context;
	}

	/**
	 * @param loginMethod
	 */
	public void setLoginMethod(String loginMethod) {
		this.loginMethod = loginMethod;
	}

	/**
	 * @return loginMethod
	 */
	public String getLoginMethod() {
		return loginMethod;
	}
	
	
}
