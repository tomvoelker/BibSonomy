package org.bibsonomy.database.util;

import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.exceptions.QueryTimeoutException;
import org.bibsonomy.common.exceptions.database.DatabaseException;
import org.bibsonomy.util.ExceptionUtils;

import com.ibatis.common.jdbc.exception.NestedSQLException;
import com.ibatis.sqlmap.client.SqlMapExecutor;
import com.ibatis.sqlmap.client.SqlMapSession;
import com.mysql.jdbc.exceptions.MySQLTimeoutException;

/**
 * This class wraps the iBatis SqlMap and manages database sessions. Transactions are virtual,
 * which means a counter is used to emulate nested transactions.
 * 
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public class DBSessionImpl implements DBSession {

	protected static final Log log = LogFactory.getLog(DBSessionImpl.class);
	/** Communication with the database is done with the sqlMap */
	private final SqlMapSession sqlMap;
	/** how many commit-calls have to be made for getting the real transaction to become committed */
	private int transactionDepth;
	private int uncommittedDepth;
	/** if one virtual transaction is aborted, no other virtual transaction will become committed until all virtual transactions are ended */
	private boolean aborted;
	private boolean closed;
	private final DatabaseException databaseException;
	
	protected DBSessionImpl(final SqlMapSession sqlMap) {
		this.sqlMap = sqlMap;
		this.transactionDepth = 0;
		this.uncommittedDepth = 0;
		this.aborted = false;
		this.closed = false;
		this.databaseException=new DatabaseException();
	}

	/**
	 * Returns the sqlMap associated with this transaction.
	 */
	protected SqlMapExecutor getSqlMapExecutor() {
		return this.sqlMap;
	}

	/**
	 * Starts a virtual transaction (a real one if no real transaction has been
	 * started yet). Either transactionSuccess or transactionFailure MUST be
	 * called hereafter.
	 */
	public void beginTransaction() {
		if (this.aborted == true) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "real transaction already aborted");
		}
		if (this.transactionDepth == 0) {
			try {
				this.sqlMap.startTransaction();
			} catch (final SQLException ex) {
				ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "Couldn't start transaction");
			}
		}
		++this.transactionDepth;
		++this.uncommittedDepth;
	}

	/**
	 * Marks the current (virtual) transaction as having been sucessfully
	 * completed. If the transaction isn't virtual a following call to
	 * endTransaction will do a commit on the real transaction.
	 */
	public void commitTransaction() {
		if (this.uncommittedDepth > 0) {
			--this.uncommittedDepth;
		} else {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "No transaction open");
		}
	}

	/**
	 * If this is called before the current (virtual) transaction has been
	 * committed, the transaction-stack is marked as failed. This causes the
	 * real transaction (with all N virtual nested transactions) to abort.<br/>
	 * 
	 * This should always be called after each transaction, that has begun with
	 * beginTransaction, sometimes with a preceeding call to commitTransaction,
	 * sometimes (in case of an exception) without.
	 */
	public void endTransaction() {
		if (this.transactionDepth > 0) {
			--this.transactionDepth;
			if (this.transactionDepth < this.uncommittedDepth) {
				// endTransaction was called before commitTransaction => abort
				this.aborted = true;
			}
			if (this.transactionDepth == 0) {
				if (this.uncommittedDepth == 0) {
					if (this.aborted == false) {
						if (!this.databaseException.hasErrorMessages()){
							// everything went well during the whole session
							try {
								this.sqlMap.commitTransaction();
								log.info("committed");
							} catch (final SQLException ex) {
								ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "Couldn't commit transaction");
							}
						} else {
							log.info("Couldn't commit transaction due to errors during the session");
							throw databaseException;
						}
					}
				}
				this.uncommittedDepth = 0;
				this.aborted = false;
				try {
					this.sqlMap.endTransaction();
					log.debug("ended");
				} catch (final SQLException ex) {
					ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "Couldn't end transaction");
				}
			}
		} else {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "No transaction open");
		}
	}

	/**
	 * MUST be called to release the db-connection
	 */
	public void close() {
		try {
			this.sqlMap.endTransaction();
			log.debug("ended");
		} catch (final SQLException ex) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "Couldn't end transaction");
		}
		this.sqlMap.close();
		this.closed = true;
	}

	@Override
	protected void finalize() throws Throwable {
		// Try to take care of other peoples mistakes. It may take a while
		// before this is called, but it's better than nothing.
		if (this.closed == false) {
			log.error(this.getClass().getName() + " not closed");
			this.sqlMap.close();
		}
		super.finalize();
	}

	/**
	 * marks this session to have a failed job
	 */
	public void somethingWentWrong() {
		if (this.transactionDepth > 0) {
			this.aborted = true;
		}
	}

	/**
	 * @return whether this session has an aborted transaction 
	 */
	public boolean isAborted() {
		return this.aborted;
	}

	/*
	 * @see org.bibsonomy.database.util.DBSession.transactionWrapper(String, Object, StatementType, QueryFor, boolean)
	 */
	public Object transactionWrapper(final String query, final Object param, final Object result, final StatementType statementType, final QueryFor queryFor, final boolean ignoreException) {
		try {
			// determine, whether a result object was supplied which should be populated by ibatis
			if (result==null) {
				return this.executeQuery(this.getSqlMapExecutor(), query, param, statementType, queryFor);
			}
			
			// ... or a new result object should be returned
			return this.executeQuery(this.getSqlMapExecutor(), query, param, result, statementType, queryFor);
			
		} catch (final NestedSQLException ex) {
			if (this.databaseException.hasErrorMessages()) {
				if ("22001".equals(ex.getSQLState())) {
					/*
					 * 22001 (string too long for the column)
					 * ignore exception because a FieldLengthErrorMessage was added to databaseException
					 * and the "commit" method will throw the databaseException
					 */
					return null;
				}
			}
			
			this.logException(query, ignoreException, ex);
		} catch (final Exception ex) {
			/*
			 * catch exception that happens because of
			 * query interruption due to time limits
			 * 
			 * Error code 1317: "Query execution was interrupted"
			 * Error code 1028: "Sort aborted"
			 * (see http://dev.mysql.com/doc/refman/5.1/en/error-messages-server.html) 
			 * 
			 */
			if (ex.getCause() != null && ex.getCause().getClass().equals(SQLException.class) && 1317 == ((SQLException)ex.getCause()).getErrorCode()) {
				log.info("Query timeout for query: " + query);
				throw new QueryTimeoutException(ex, query);
			}
			if (ex.getCause() != null && ex.getCause().getClass().equals(SQLException.class) && 1028 == ((SQLException)ex.getCause()).getErrorCode()) {
				log.info("Sort aborted for query: " + query);
				throw new QueryTimeoutException(ex, query);
			}
			if (ex.getCause() != null && ex.getCause().getClass().equals(MySQLTimeoutException.class)) {
				log.info("MySQL Query timeout for query " + query);
				throw new QueryTimeoutException(ex, query);
			}
						
			/*
			 * Here we catch the wonderful "unknown error" (code 1105) exception of MySQL.
			 * On 2008-04-21 we found that it occurs, when a statement is killed during its
			 * "statistics" phase (mysql version 5.0.45). We filed a bug report 
			 * <http://bugs.mysql.com/bug.php?id=36230> and as workaround added this if- 
			 * block.
			 * 
			 * http://dev.mysql.com/doc/refman/5.1/en/error-messages-server.html
			 */
			if (ex.getCause() != null && ex.getCause().getClass().equals(SQLException.class) && 1105 == ((SQLException)ex.getCause()).getErrorCode()) {
				log.info("Hit MySQL bug 36230. (with query: " + query + "). See <http://bugs.mysql.com/bug.php?id=36230> for more information.");
				throw new QueryTimeoutException(ex, query);
			}
			
			this.logException(query, ignoreException, ex);
		}
		
		return null; // unreachable
	}

	/**
	 * @param query
	 * @param ignoreException
	 * @param ex
	 */
	private void logException(final String query, final boolean ignoreException, final Exception ex) {
		String msg = "Couldn't execute query '" + query + "'";
				
		if (ignoreException) {
			msg += " (ignored): " + ex.getMessage();
			log.debug(msg);
			throw new RuntimeException(msg);
		}
		
		this.somethingWentWrong();
		ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, msg);
	}

	/**
	 * Executes a query.
	 */
	private Object executeQuery(final SqlMapExecutor sqlMap, final String query, final Object param, final StatementType statementType, final QueryFor queryFor) throws SQLException {
		Object rVal = null;
		switch (statementType) {
		case SELECT:
			switch (queryFor) {
			case OBJECT:
				rVal = sqlMap.queryForObject(query, param);
				break;
			case LIST:
				rVal = sqlMap.queryForList(query, param);
				break;
			}
			break;
		case INSERT:
			sqlMap.insert(query, param);
			break;
		case UPDATE:
			sqlMap.update(query, param);
			break;
		case DELETE:
			sqlMap.delete(query, param);
			break;
		default:
			throw new UnsupportedOperationException();
		}
		return rVal;
	}
	
	/**
	 * Executes a query and fills given result object.
	 * 
	 * @param sqlMap Ibatis query executor
	 * @param query string identifying the query (this is the id as used in the sqlmap.xml file)
	 * @param param querie's parameter object
	 * @param statementType MUST BE SELECT - otherwise UnsupportedOperationException() is thrown
	 * @param queryFor MUST BE Object - otherwise UnsupportedOperationException() is thrown
	 * @return the result object, eventually filled with values by Ibatis
	 * @throws SQLException
	 */
	private Object executeQuery(final SqlMapExecutor sqlMap, final String query, final Object param, final Object result, final StatementType statementType, final QueryFor queryFor) throws SQLException {
		Object rVal = null;
		switch (statementType) {
		case SELECT:
			switch (queryFor) {
			case OBJECT:
				rVal = sqlMap.queryForObject(query, param, result);
				break;
			}
			break;
		default:
			throw new UnsupportedOperationException();
		}
		return rVal;
	}
	
	@Override
	public void addError(String key, ErrorMessage errorMessage) {
		this.databaseException.addToErrorMessages(key, errorMessage);
		
	}
}