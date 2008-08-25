package org.bibsonomy.common.enums;

/**
 * Defines different types of databases the application communicates with. This is
 * mainly used to distinguish to which SqlMapClient the application should connect to.
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public enum DatabaseType {
	/** DB master */
	MASTER,
	/** DB slave */
	SLAVE;
}