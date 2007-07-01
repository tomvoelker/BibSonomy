/*
 * Created on 28.06.2007
 */
package org.bibsonomy.rest;

import org.bibsonomy.model.logic.LogicInterface;

public interface LogicInterfaceFactory {
	public LogicInterface getLogicAccess(String loginName, String apiKey);
}
