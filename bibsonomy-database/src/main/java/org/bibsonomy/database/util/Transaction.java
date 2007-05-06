package org.bibsonomy.database.util;

import java.io.Closeable;
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
 * @author Christian Schenk
 * @author Jens Illig
 * @version $Id$
 */
public class Transaction implements Closeable {

	/** Logger */
	protected static final Logger log = Logger.getLogger(Transaction.class);
	/** Communication with the database is done with the sqlMap */
	private final SqlMapSession sqlMap;
	/** how many commit-calls have to be made for getting the real transaction to become committed */
	private int transactionDepth;
	private int uncommittedDepth;
	/** if one virtual transaction is aborted, no other virtual transaction will become committed until all virtual transactions are ended */
	private boolean aborted;
	private boolean closed;

	protected Transaction(final SqlMapSession sqlMap) {
		this.sqlMap = sqlMap;
		this.transactionDepth = 0;
		this.transactionDepth = 0;
		this.aborted = false;
		this.closed = false;
	}

	/**
	 * Returns the sqlMap associated with this transaction.
	 */
	public SqlMapExecutor getSqlMapExecutor() {
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
}