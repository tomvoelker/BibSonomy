package webdav.db.exceptions;

/**
 * All exceptions which could occur during interaction with the database are encapsulated in this
 * exception to ease the handling of the database-facade, i.e. only one exception has to be caught.
 * 
 * @author Christian Schenk
 */
public class DbException extends RuntimeException {
	public DbException(final String message) {
		super(message);
	}

	public DbException(final String message, final Throwable cause) {
		super(message, cause);
	}
}