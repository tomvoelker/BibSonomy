package org.bibsonomy.database;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.Tag;
import org.bibsonomy.util.ExceptionUtils;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * This is the superclass for all classes that are implementing methods to
 * retrieve data from a database. It provides methods for the interaction with
 * the database, i.e. a lot of convenience methods that return the
 * <em>right</em> results, e.g. not just an Object but a BibTex object or not
 * just a list of Objects but a list of bookmarks. This way a lot of unchecked
 * casting remains in this class and isn't scattered all over the code.
 * 
 * @author Christian Schenk
 */
public class AbstractDatabaseManager {

	/** Logger */
	protected static final Logger log = Logger.getLogger(AbstractDatabaseManager.class);
	/** Defines whether the database should be readonly */
	private boolean readonly;

	/** Used to determine whether we want to retrieve an object or a list */
	public enum QueryFor {
		OBJECT, LIST;
	}

	/** Used to determine the execution of an select, insert, update or delete. */
	private enum StatementType {
		SELECT, INSERT, UPDATE, DELETE;
	}

	/**
	 * The database is writeable by default.
	 */
	public AbstractDatabaseManager() {
		this.readonly = false;
	}

	/**
	 * Defines whether the database should be readonly.
	 * 
	 * @return true if the databse is readonly, false otherwise
	 */
	public boolean isReadonly() {
		return this.readonly;
	}

	/**
	 * If the database should be readonly, i.e. inserts and the like won't be
	 * written to the database, we start a transaction and abort it. If the
	 * database isn't readonly, i.e. writeable, we'll do a commit instead of
	 * aborting the transaction.<br/>
	 * This comes in handy for unit tests. 
	 */
	public void setReadonly() {
		this.readonly = true;
	}

	/**
	 * Can be used to start a query that retrieves a list of tags.
	 * 
	 * @see tagList(final String query, final Object param, final Transaction
	 *      transaction)
	 */
	protected List<Tag> tagList(final String query, final Object param) {
		return this.tagList(query, param, this.getTransaction());
	}

	/**
	 * Can be used to start a query that retrieves a list of tags.
	 * 
	 * @see tagList(final String query, final Object param)
	 */
	@SuppressWarnings("unchecked")
	protected List<Tag> tagList(final String query, final Object param, final Transaction transaction) {
		return (List<Tag>) queryForAnything(query, param, QueryFor.LIST, transaction);
	}

	/**
	 * Can be used to start a query that retrieves a list of Integers.
	 * 
	 * @see intList(final String query, final Object param, final Transaction
	 *      transaction)
	 */
	protected List<Integer> intList(final String query, final Object param) {
		return this.intList(query, param, this.getTransaction());
	}

	/**
	 * Can be used to start a query that retrieves a list of Integers.
	 * 
	 * @see intList(final String query, final Object param)
	 */
	@SuppressWarnings("unchecked")
	protected List<Integer> intList(final String query, final Object param, final Transaction transaction) {
		return (List<Integer>) queryForAnything(query, param, QueryFor.LIST, transaction);
	}

	/**
	 * Can be used to start a query that retrieves a single object like a tag or
	 * bookmark but also an int or boolean.<br/>
	 * 
	 * In this case we break the rule to create one method for every return
	 * type, because with a single object it doesn't result in an unchecked
	 * cast.
	 */
	protected Object queryForObject(final String query, final Object param) {
		return this.queryForObject(query, param, this.getTransaction());
	}

	/**
	 * @see queryForObject(final String query, final Object param)
	 */
	protected Object queryForObject(final String query, final Object param, final Transaction transaction) {
		return this.queryForAnything(query, param, QueryFor.OBJECT, transaction);
	}

	/**
	 * Can be used to retrieve a list of objects.
	 */
	protected List queryForList(final String query, final Object param) {
		return this.queryForList(query, param, this.getTransaction());
	}

	/**
	 * @see queryForList(final String query, final Object param)
	 */
	protected List queryForList(final String query, final Object param, final Transaction transaction) {
		return (List) this.queryForAnything(query, param, QueryFor.LIST, transaction);
	}

	/**
	 * This is a convenience method, which calls the <em>queryForObject</em>-
	 * or the <em>queryForList</em>-method on the sqlMap. We encapsulate this
	 * method here to catch exceptions, namely SQLException, which can be thrown
	 * from that call.
	 */
	@SuppressWarnings("unchecked")
	private Object queryForAnything(final String query, final Object param, final QueryFor queryFor, final Transaction transaction) {
		return this.transactionWrapper(query, param, StatementType.SELECT, queryFor, transaction);
	}

	/**
	 * Inserts an object into the database.
	 */
	protected void insert(final String query, final Object param) {
		this.insert(query, param, this.getTransaction());
	}

	/**
	 * @see insert(final String query, final Object param)
	 */
	protected void insert(final String query, final Object param, final Transaction transaction) {
		this.insertUpdateDelete(query, param, StatementType.INSERT, transaction);
	}

	/**
	 * Updates an object in the database.
	 */
	protected void update(final String query, final Object param) {
		this.update(query, param, this.getTransaction());
	}

	/**
	 * @see update(final String query, final Object param)
	 */
	protected void update(final String query, final Object param, final Transaction transaction) {
		this.insertUpdateDelete(query, param, StatementType.UPDATE, transaction);
	}

	/**
	 * Deletes an object from the database.
	 */
	protected void delete(final String query, final Object param) {
		this.delete(query, param, this.getTransaction());
	}

	/**
	 * @see delete(final String query, final Object param)
	 */
	protected void delete(final String query, final Object param, final Transaction transaction) {
		this.insertUpdateDelete(query, param, StatementType.DELETE, transaction);
	}

	/**
	 * This is another convenience method, which executes insert, update or
	 * delete statements.
	 */
	private void insertUpdateDelete(final String query, final Object param, final StatementType statementType, final Transaction transaction) {
		this.transactionWrapper(query, param, statementType, null, transaction);
	}

	/**
	 * Returns a new transaction object.
	 */
	protected Transaction getTransaction() {
		return this.getTransaction(false);
	}

	/**
	 * Convenience method for getTransaction(). Transaction can be set to batch.
	 */
	protected Transaction getTransaction(final boolean batch) {
		final Transaction transaction = new Transaction(this.isReadonly());
		if (batch) transaction.setBatch();
		return transaction;
	}

	/**
	 * This method combines all calls to the SqlMap. This way we can catch the
	 * exceptions in one place and surround the queries with transaction
	 * management.<br/>
	 * 
	 * TODO: If AOP is used in the future, especially the management of
	 * transactions could be rewritten as an aspect.
	 * 
	 * TODO: This should be moved to a TransactionManager class.
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
	private Object transactionWrapper(final String query, final Object param, final StatementType statementType, final QueryFor queryFor, final Transaction transaction) {
		try {
			// If the database is readonly we start a transaction, so we can
			// commit/abort it later
			if (this.isReadonly() || transaction.isBatch()) transaction.startTransaction();
			// Execute the query
			return this.executeQuery(transaction.getSqlMap(), query, param, statementType, queryFor);
		} catch (final SQLException ex) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "Couldn't execute query '" + query + "'");
			// If an error occured we abort the transaction
			transaction.endTransaction();
		} finally {
			// If this transaction isn't a batch
			if (!transaction.isBatch()) {
				// If the database is writeable we commit the transaction
				transaction.commitTransaction();
//				if (!this.isReadonly()) transaction.commitTransaction();
//				// Regardless of the commit we have to call endTransaction
//				transaction.endTransaction();
			}
		}
		return null; // unreachable
	}

	/**
	 * Executes a query.
	 * 
	 * XXX: This should be moved to a TransactionManager class.
	 */
	private Object executeQuery(final SqlMapClient sqlMap, final String query, final Object param, final StatementType statementType, final QueryFor queryFor) throws SQLException {
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
		}
		return rVal;
	}
}