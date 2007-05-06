package org.bibsonomy.database;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.util.ExceptionUtils;

import com.ibatis.sqlmap.client.SqlMapExecutor;

/**
 * This is the superclass for all classes that are implementing methods to
 * retrieve data from a database. It provides methods for the interaction with
 * the database, i.e. a lot of convenience methods that return the
 * <em>right</em> results, e.g. not just an Object but a BibTex object or not
 * just a list of Objects but a list of bookmarks. This way a lot of unchecked
 * casting remains in this class and isn't scattered all over the code.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class AbstractDatabaseManager {

	/** Logger */
	protected static final Logger log = Logger.getLogger(AbstractDatabaseManager.class);

	/** Used to determine whether we want to retrieve an object or a list */
	private enum QueryFor {
		OBJECT, LIST;
	}

	/** Used to determine the execution of an select, insert, update or delete. */
	private enum StatementType {
		SELECT, INSERT, UPDATE, DELETE;
	}

	/**
	 * Can be used to start a query that retrieves a list of Integers.
	 * 
	 * @see intList(final String query, final Object param, final Transaction
	 *      transaction)
	 */
	@SuppressWarnings("unchecked")
	protected <T> List<T> queryForList(final String query, final Object param, final Class<T> type, final Transaction transaction) {
		return (List<T>) this.queryForAnything(query, param, QueryFor.LIST, transaction);
	}

	/**
	 * short form of queryForList without Type argument
	 */
	protected List queryForList(final String query, final Object param, final Transaction transaction) {
		return queryForList(query, param, Object.class, transaction);
	}

	/**
	 * Can be used to start a query that retrieves a single object like a tag or
	 * bookmark but also an int or boolean.<br/>
	 * 
	 * In this case we break the rule to create one method for every return
	 * type, because with a single object it doesn't result in an unchecked
	 * cast.
	 */
	@SuppressWarnings("unchecked")
	protected <T> T queryForObject(final String query, final Object param, Class<T> type, final Transaction transaction) {
		return (T) this.queryForAnything(query, param, QueryFor.OBJECT, transaction);
	}

	/**
	 * @see queryForObject(final String query, final Object param)
	 */
	protected Object queryForObject(final String query, final Object param, final Transaction transaction) {
		return this.queryForAnything(query, param, QueryFor.OBJECT, transaction);
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
	protected void insert(final String query, final Object param, final Transaction transaction) {
		this.insertUpdateDelete(query, param, StatementType.INSERT, transaction);
	}

	/**
	 * Updates an object in the database.
	 */
	protected void update(final String query, final Object param, final Transaction transaction) {
		this.insertUpdateDelete(query, param, StatementType.UPDATE, transaction);
	}

	/**
	 * Deletes an object from the database.
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
	private Object transactionWrapper(final String query, final Object param, final StatementType statementType, final QueryFor queryFor, final Transaction transaction) {
		try {
			return this.executeQuery(transaction.getSqlMapExecutor(), query, param, statementType, queryFor);
		} catch (final Exception ex) {
			transaction.somethingWentWrong();
			ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "Couldn't execute query '" + query + "'");
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
		}
		return rVal;
	}
}