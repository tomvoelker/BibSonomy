package org.bibsonomy.database;

import org.apache.shindig.auth.SecurityToken;
import org.bibsonomy.model.logic.LogicInterface;

/**
 * interface for retrieving {@link LogicInterface} with authentication via
 * security tokens 
 * 
 * @author fei
 *
 */
public interface ShindigLogicInterfaceFactory {
	
	/**
	 * @param st
	 * @return the logic interface
	 */
	public LogicInterface getLogicAccess(SecurityToken st);
}
