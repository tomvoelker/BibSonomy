package org.bibsonomy.database.util;

import java.io.Closeable;

/**
 * This class wraps the sqlMap.<br/>
 * 
 * See org.bibsonomy.database.AbstractDatabaseManager.transactionWrapper() for
 * further explanation and use of this class.
 * 
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public interface Transaction extends Closeable {
	/**
	 * Starts a virtual transaction (a real one if no real transaction has been
	 * started yet). Either transactionSuccess or transactionFailure MUST be
	 * called hereafter.
	 */
	public void beginTransaction();

	/**
	 * Marks the current (virtual) transaction as having been sucessfully completed.
	 * If the transaction isn't virtual commits the real transaction.
	 */
	public void commitTransaction();

	/**
	 * If this is called before the current (virtual) transaction has been
	 * committed, the transaction-stack is marked as failed. This causes the
	 * real transaction (with all N virtual nested transactions) to abort.<br/>
	 * This should always be called after each transaction, that has begun with
	 * beginTransaction, sometimes with a preceeding call to commitTransaction,
	 * sometimes (in case of an exception) without.
	 */
	public void endTransaction();
	
	/** MUST be called to release the db-connection */
	public void close();


	/**
	 * This method combines all calls to the SqlMap. This way we can catch the
	 * exceptions in one place and surround the queries with transaction
	 * management.
	 * 
	 * @param query
	 *            The SQL query which should be executed.
	 * @param param
	 *            A parameter object
	 * @param statementType
	 *            Defines whether it sould be a select, insert, update or delete
	 * @param queryFor
	 *            Defines whether we want to retrieve an object or a list from a
	 *            select
	 * @return An object in case of a select statement, null otherwise
	 */
	public Object transactionWrapper(final String query, final Object param, final StatementType statementType, final QueryFor queryFor, final boolean ignoreException);
	
}