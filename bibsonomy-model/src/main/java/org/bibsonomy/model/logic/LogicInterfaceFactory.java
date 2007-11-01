package org.bibsonomy.model.logic;


/**
 * Common interface of factories for objects of a LogicInterface implementations
 * 
 * @author Jens Illig
 * @version $Id$
 */
public interface LogicInterfaceFactory {
	/**
	 * @param loginName name of the user, who wants to access the system
	 * @param apiKey some sort of password
	 * @return a logicinterface implementation, that takes care of the users rights
	 */
	public LogicInterface getLogicAccess(String loginName, String apiKey);
}