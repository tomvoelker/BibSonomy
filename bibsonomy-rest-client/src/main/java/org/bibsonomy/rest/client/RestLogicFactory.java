package org.bibsonomy.rest.client;

import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.LogicInterfaceFactory;

public class RestLogicFactory implements LogicInterfaceFactory {

	private final String apiUrl;

	public RestLogicFactory() {
		this.apiUrl = null;
	}

	public RestLogicFactory(final String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public LogicInterface getLogicAccess(final String loginName, final String apiKey) {
		if (this.apiUrl != null) return new RestLogic(loginName, apiKey, this.apiUrl);
		return new RestLogic(loginName, apiKey);
	}
}