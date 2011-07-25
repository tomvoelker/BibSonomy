package org.bibsonomy.webapp.command;

import java.util.List;

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

	private String messageKey;
	
	private List<String> messageParams;
	

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

	/**
	 * @param messageKey
	 */
	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	/**
	 * @return a message-key to display a message on any jspx
	 */
	public String getMessageKey() {
		return messageKey;
	}
	
	
	
	/**
	 * @return Message Params
	 */
	public List<String> getMessageParams() {
		return this.messageParams;
	}

	/**
	 * @param messageParams
	 */
	public void setMessageParams(List<String> messageParams) {
		this.messageParams = messageParams;
	}

	public void setMessage(String key, List<String> params) {
		this.setMessageKey(key);
		this.setMessageParams(params);
	}
	
}
