/*
 * Created on 14.07.2007
 */
package org.bibsonomy.rest.client;

import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.LogicInterfaceFactory;

public class RestLogicFactory implements LogicInterfaceFactory {
	private final String apiUrl;
	
	public RestLogicFactory() {
		apiUrl = null;
	}
	
	public RestLogicFactory(final String apiUrl) {
		this.apiUrl = apiUrl;
	}
	
	public LogicInterface getLogicAccess(String loginName, String apiKey) {
		if (apiUrl != null) {
			return new RestLogic(loginName, apiKey, apiUrl);
		} else {
			return new RestLogic(loginName, apiKey);
		}
	}

}
