package org.bibsonomy.model.logic;


/**
 * @author Jens Illig
 * @version $Id$
 */
public interface LogicInterfaceFactory {
	public LogicInterface getLogicAccess(String loginName, String apiKey);
}