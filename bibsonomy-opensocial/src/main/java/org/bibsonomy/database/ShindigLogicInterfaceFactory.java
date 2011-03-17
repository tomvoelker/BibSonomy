package org.bibsonomy.database;

import org.apache.shindig.auth.SecurityToken;
import org.bibsonomy.model.logic.LogicInterface;

/**
 * interface for retrieving BibSonomy logic interfaces with authentication via
 * security tokens 
 * 
 * @author fei
 *
 */
public interface ShindigLogicInterfaceFactory {
	public LogicInterface getLogicAccess(SecurityToken st);
}
