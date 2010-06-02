package org.bibsonomy.database.common;


import java.util.List;

import org.bibsonomy.common.errors.ErrorMessage;

/**
 * This interface represents a session for the database. A session normally
 * corresponds to a database connection.
 * 
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public interface DBSession {
	
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

	/**
	 * MUST be called to release the db-connection
	 */
	public void close();
	
	/**
	 * adds a error message to the provided key
	 * 
	 * @param key
	 * @param errorMessage
	 */
	public void addError(String key, ErrorMessage errorMessage);
	
	/**
	 * 
	 * @param query
	 * @param param
	 * @return the result of the query
	 */
	public Object queryForObject(final String query, final Object param);
	
	/**
	 * queries for an object in the database, the result will be stored in the
	 * provided store
	 * 
	 * @param query
	 * @param param
	 * @param store
	 * @return the result of the query
	 */
	public Object queryForObject(final String query, final Object param, final Object store);
	
	/**
	 * queries for a list in the database
	 *  
	 * @param query
	 * @param param
	 * @return the result list
	 */
	public List<?> queryForList(final String query, final Object param);

	/**
	 * stores the param in the database
	 * 
	 * @param query
	 * @param param
	 */
	public void insert(final String query, final Object param);

	/**
	 * updates the param
	 * 
	 * @param query
	 * @param param
	 */
	public void update(final String query, final Object param);
	
	/**
	 * deletes the param from the database
	 * 
	 * @param query
	 * @param param
	 */
	public void delete(final String query, final Object param);
}