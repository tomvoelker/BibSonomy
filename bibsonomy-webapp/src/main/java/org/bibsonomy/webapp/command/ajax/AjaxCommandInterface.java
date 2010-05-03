package org.bibsonomy.webapp.command.ajax;

/**
 * Interface for ajax commands.
 * 
 * FIXME: write JavaDoc! 
 * 
 * @author fei
 * @version $Id$
 */
public interface AjaxCommandInterface {
	
	/**
	 * @return the response
	 */
	public String getResponseString();
	
	/**
	 * @param response the response to set
	 */
	public void setResponseString(String response);
}
