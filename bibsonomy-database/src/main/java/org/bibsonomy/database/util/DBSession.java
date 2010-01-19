package org.bibsonomy.database.util;

import java.io.Closeable;

import org.bibsonomy.common.errors.ErrorMessage;

/**
 * This interface represents a session for the database. A session normally
 * corresponds to a database connection.
 * 
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public interface DBSession extends Closeable {
	/**
	 * Starts a virtual transaction (a real one if no real transaction has been
	 * started yet). At least endTransaction (probably also commitTransaction
	 * before) must be called hereafter.
	 */
	public void beginTransaction();

	/**
	 * Marks the current transaction as having been sucessfully completed.
	 * However, the real commit may not be called before endTransaction has been
	 * called.
	 */
	public void commitTransaction();

	/**
	 * If this is called before the current transaction has been committed, the
	 * transaction is marked as failed. This causes the transaction to abort.<br/>
	 * 
	 * This should always be called after each transaction, that has begun with
	 * beginTransaction, sometimes with a preceeding call to commitTransaction,
	 * sometimes (in case of an exception) without.
	 */
	public void endTransaction();

	/** MUST be called to release the db-connection */
	public void close();

	/**
	 * This method combines all calls to the database. This way exceptions are
	 * catched in one place and queries are surrounded by transaction
	 * management.
	 * 
	 * @param query
	 *            The SQL query which should be executed.
	 * @param param
	 *            A parameter object
	 * @param result
	 *            An optional result object instance that should be populated 
	 *            with result data (if null, a new object will be returned)
	 * @param statementType
	 *            Defines whether it should be a select, insert, update or delete
	 * @param queryFor
	 *            Defines whether we want to retrieve an object or a list from a
	 *            select
	 * @param ignoreException
	 *            if this argument is set to true, no rollback will be triggered
	 *            in case of an exception
	 * @return An object in case of a select statement which is the populated 
	 *            result object if supplied, null otherwise
	 */
	public Object transactionWrapper(final String query, final Object param, Object result, final StatementType statementType, final QueryFor queryFor, final boolean ignoreException);

	/**
	 * TODO: Add java-doc comment
	 * @param key
	 * @param errorMessage
	 */
	public void addError(String key, ErrorMessage errorMessage);
}