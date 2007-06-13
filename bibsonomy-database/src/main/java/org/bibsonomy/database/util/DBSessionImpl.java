package org.bibsonomy.database.util;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.bibsonomy.util.ExceptionUtils;

import com.ibatis.sqlmap.client.SqlMapExecutor;
import com.ibatis.sqlmap.client.SqlMapSession;

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
public class DBSessionImpl implements DBSession {

	protected static final Logger log = Logger.getLogger(DBSessionImpl.class);
	/** Communication with the database is done with the sqlMap */
	private final SqlMapSession sqlMap;
	/** how many commit-calls have to be made for getting the real transaction to become committed */
	private int transactionDepth;
	private int uncommittedDepth;
	/** if one virtual transaction is aborted, no other virtual transaction will become committed until all virtual transactions are ended */
	private boolean aborted;
	private boolean closed;

	protected DBSessionImpl(final SqlMapSession sqlMap) {
		this.sqlMap = sqlMap;
		this.transactionDepth = 0;
		this.transactionDepth = 0;
		this.aborted = false;
		this.closed = false;
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
	 * Marks the current (virtual) transaction as having been sucessfully completed.
	 * If the transaction isn't virtual commits the real transaction.
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
					if  (this.aborted == false) {
						try {
							this.sqlMap.commitTransaction();
							log.info("committed");
						} catch (final SQLException ex) {
							ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "Couldn't commit transaction");
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

	/** MUST be called to release the db-connection */
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
		// try to take care of other peoples mistakes. it may take a while before this is called,
		// but it's better than nothing.
		if (this.closed == false) {
			log.error(this.getClass().getName() + " not closed");
			this.sqlMap.close();
		}
		super.finalize();
	}

	public void somethingWentWrong() {
		if (this.transactionDepth > 0) {
			this.aborted = true;
		}
	}

	public boolean isAborted() {
		return this.aborted;
	}

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
	public Object transactionWrapper(final String query, final Object param, final StatementType statementType, final QueryFor queryFor, final boolean ignoreException) {
		try {
			return this.executeQuery(this.getSqlMapExecutor(), query, param, statementType, queryFor);
		} catch (final Exception ex) {
			String msg = "Couldn't execute query '" + query + "'";
			if (ignoreException == false) {
				this.somethingWentWrong();
				ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "Couldn't execute query '" + query + "'");
			} else {
				msg += " (ignored): " + ex.getMessage();
				log.debug(msg);
				throw new RuntimeException(msg);
			}
		}
		return null; // unreachable
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
}