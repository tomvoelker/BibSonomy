/*
 * Created on 13.07.2007
 */
package org.bibsonomy.rest.remotecall;

import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.LogicInterfaceFactory;

public class MockLogicFactory implements LogicInterfaceFactory {
	private static String requestedLoginName = null;
	private static String requestedApiKey = null;
	private static LogicInterface logic;
	
	protected static void init(LogicInterface li) {
		requestedLoginName = null;
		requestedApiKey = null;
		logic = li;
	}
	
	public LogicInterface getLogicAccess(String loginName, String apiKey) {
		requestedLoginName = loginName;
		requestedApiKey = apiKey;
		return logic;
	}

	public static LogicInterface getLogic() {
		return logic;
	}

	protected static String getRequestedApiKey() {
		return requestedApiKey;
	}

	protected static String getRequestedLoginName() {
		return requestedLoginName;
	}

}
