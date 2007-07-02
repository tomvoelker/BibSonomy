package org.bibsonomy.rest;

import org.bibsonomy.model.logic.LogicInterface;

/**
 * @author Jens Illig
 * @version $Id$
 */
public interface LogicInterfaceFactory {
	public LogicInterface getLogicAccess(String loginName, String apiKey);
}