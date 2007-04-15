package org.bibsonomy.database.util;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.bibsonomy.util.ExceptionUtils;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * This class wraps the sqlMap for two reasons:
 * <ol>
 * <li>A dependency to SqlMapClient isn't scattered all over the code</li>
 * <li>If reading the sqlMap gets slow, this class can be enhanced easily</li>
 * </ol>
 * See org.bibsonomy.database.AbstractDatabaseManager.transactionWrapper() for
 * further explanation and use of this class.
 * 
 * @author Christian Schenk
 */
public class Transaction {

	/** Logger */
	protected static final Logger log = Logger.getLogger(Transaction.class);
	/** Communication with the database is done with the sqlMap */
	private final SqlMapClient sqlMap;
	/** If this transaction is readonly it won't be committed */
	private final boolean readonly;
	/** Determines whether a transaction has been started */
	private boolean started;
	/** Determines whether this transaction is finished */
	private boolean finished;
	/**
	 * Determines whether more than one query will be executed. This can be used
	 * to start and end a transaction manually.
	 */
	private boolean batch;

	public Transaction(final boolean readonly) {
		this.sqlMap = DatabaseUtils.getSqlMapClient(log);
		this.readonly = readonly;
		this.started = false;
		this.finished = false;
		this.batch = false;
	}

	/**
	 * Returns the sqlMap associated with this transaction.
	 */
	public SqlMapClient getSqlMap() {
		return this.sqlMap;
	}

	/**
	 * Enables manual transaction management.
	 */
	public void setBatch() {
		this.batch = true;
	}

	/**
	 * Returns true if this transaction object is managed manually, otherwise
	 * false.
	 */
	public boolean isBatch() {
		return this.batch;
	}

	/**
	 * Starts a transaction if it hasn't been started yet. There's no need to
	 * call this manually if batch is set to true.
	 */
	public void startTransaction() {
		if (this.started) return;
		this.started = true;
		try {
			this.sqlMap.startTransaction();
		} catch (final SQLException ex) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "Couldn't start transaction");
		}
	}

	/**
	 * Commits the transaction. Once a transaction has been commited no further
	 * queries are allowed.
	 */
	public void commitTransaction() {
		if (!this.started || this.finished) return;
		try {
			if (!this.readonly) this.sqlMap.commitTransaction();
			this.endTransaction();
		} catch (final SQLException ex) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "Couldn't commit transaction");
		}
	}

	/**
	 * Ends the transaction. If the transaction hasn't been commited the
	 * transaction will be aborted.
	 */
	public void endTransaction() {
		if (!this.started || this.finished) return;
		this.finished = true;
		try {
			this.sqlMap.endTransaction();
		} catch (SQLException ex) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "Couldn't end transaction");
		}
	}
}