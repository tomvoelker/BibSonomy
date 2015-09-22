package org.bibsonomy.common.exceptions;

/**
 * thrown iff the database is in read only mode
 *
 * @author dzo
 */
public class ReadOnlyDatabaseException extends DatabaseException {
	private static final long serialVersionUID = 8661440147680662309L;

	/**
	 * default constructor
	 */
	public ReadOnlyDatabaseException() {
		super("System is currently in read-only mode.");
	}
}
