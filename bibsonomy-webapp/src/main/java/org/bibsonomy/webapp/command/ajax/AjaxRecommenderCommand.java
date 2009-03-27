package org.bibsonomy.webapp.command.ajax;

import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.actions.EditPostCommand;



/**
 * Command for recommendation ajax requests.
 * 
 * @author fei
 * @version $Id$
 * 
 * @param <RESOURCE> the type of resource this command handles 
 * 
 */
public class AjaxRecommenderCommand<RESOURCE extends Resource> extends EditPostCommand<RESOURCE> implements AjaxCommandInterface {
	private String responseString;

	public String getResponseString() {
		return this.responseString;
	}

	public void setResponseString(String response) {
		this.responseString = response;
	}
}
