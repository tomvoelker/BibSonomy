package org.bibsonomy.database.util;

/**
 * Used to determine the execution of a select, insert, update or delete.
 * 
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public enum StatementType {
	/** for SELECT statements */
	SELECT,
	/** for INSERT statements */
	INSERT,
	/** for UPDATE statements */
	UPDATE,
	/** for DELETE statements */
	DELETE;
}