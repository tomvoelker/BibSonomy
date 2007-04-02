package org.bibsonomy.database.util;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * This class wraps the sqlMap for two reasons:
 * <ol>
 * <li>A dependency to SqlMapClient isn't scattered all over the code</li>
 * <li>If reading the sqlMap gets slow, this class can be enhanced easily</li>
 * </ol>
 * 
 * @author Christian Schenk
 */
public class Transaction {

	/** Logger */
	protected static final Logger log = Logger.getLogger(Transaction.class);
	/** Communication with the database is done with the sqlMap */
	private final SqlMapClient sqlMap;
	/** Determines whether a transaction has been started */
	private boolean started;
	/** Determines whether this transaction is finished */
	private boolean finished;
	/**
	 * Determines whether more than one query will be executed. This can be used
	 * to start and end a transaction manually.
	 */
	private boolean batch;

	public Transaction() {
		this.sqlMap = DatabaseUtils.getSqlMapClient(log);
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
	 * Starts a transaction if it hasn't been started yet.
	 */
	public void startTransaction() throws SQLException {
		if (this.started) return;
		this.started = true;
		this.sqlMap.startTransaction();
	}

	/**
	 * Commits the transaction.
	 */
	public void commitTransaction() throws SQLException {
		if (this.finished) return;
		if (!this.started) throw new RuntimeException("Transaction hasn't been started yet");
		this.sqlMap.commitTransaction();
		this.endTransaction();
	}

	/**
	 * Ends the transaction.
	 */
	public void endTransaction() throws SQLException {
		if (this.finished) return;
		this.finished = true;
		this.sqlMap.endTransaction();
	}
}