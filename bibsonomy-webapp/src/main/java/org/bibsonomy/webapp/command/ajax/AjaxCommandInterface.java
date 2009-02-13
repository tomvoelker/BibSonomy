package org.bibsonomy.webapp.command.ajax;

/**
 * Interface for ajax commands.
 * 
 * @author fei
 * @version $Id$
 */
public interface AjaxCommandInterface {
	public String getResponseString();
	
	public void setResponseString(String response);
}
