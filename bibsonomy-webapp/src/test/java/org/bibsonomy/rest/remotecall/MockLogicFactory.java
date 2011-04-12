/*
 * Created on 13.07.2007
 */
package org.bibsonomy.rest.remotecall;

import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.LogicInterfaceFactory;

/**
 * this class is used to test if the system delegates the right login-data
 * to the (this) {@link LogicInterfaceFactory} and for injecting a
 * {@link LogicInterface}-mock-implementation into the system. 
 * 
 * @author Jens Illig
 */
public class MockLogicFactory implements LogicInterfaceFactory {
	private static String requestedLoginName = null;
	private static String requestedApiKey = null;
	private static LogicInterface logic;
	
	protected static void init(LogicInterface li) {
		requestedLoginName = null;
		requestedApiKey = null;
		logic = li;
	}
	
	@Override
	public LogicInterface getLogicAccess(String loginName, String apiKey) {
		requestedLoginName = loginName;
		requestedApiKey = apiKey;
		return logic;
	}

	protected static String getRequestedApiKey() {
		return requestedApiKey;
	}

	protected static String getRequestedLoginName() {
		return requestedLoginName;
	}

}
