package org.bibsonomy.database;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
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
	/** Communication with the database is done with the sqlMap */
	private final SqlMapClient sqlMap;
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
	 * Initializes the SqlMap
	 */
	public AbstractDatabaseManager() {
		this.sqlMap = DatabaseUtils.getSqlMapClient(log);
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
	 * aborting the transaction.
	 */
	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	/**
	 * Can be used to start a query that retrieves a list of bookmarks.
	 */
	@SuppressWarnings("unchecked")
	public List<Bookmark> bookmarkList(final String query, final BookmarkParam param) {
		return (List<Bookmark>) queryForAnything(query, param, QueryFor.LIST);
	}

	// FIXME return value needs to be changed to org.bibsonomy.model.Post
	@SuppressWarnings("unchecked")
  public List<Post<? extends Resource>> bookmarkList(final String query, final BookmarkParam param, final boolean test) {
		return (List<Post<? extends Resource>>) queryForAnything(query, param, QueryFor.LIST);
	}

	/**
	 * Can be used to start a query that retrieves a list of BibTexs.
	 */
	@SuppressWarnings("unchecked")
	public List<Post<? extends Resource>> bibtexList(final String query, final BibTexParam param) {
		return (List<Post<? extends Resource>>) queryForAnything(query, param, QueryFor.LIST);
	}

	/**
	 * Can be used to start a query that retrieves a list of tags.
	 */
	@SuppressWarnings("unchecked")
	protected List<Tag> tagList(final String query, final Object param) {
		return (List<Tag>) queryForAnything(query, param, QueryFor.LIST);
	}

	/**
	 * Can be used to start a query that retrieves a list of Integers.
	 */
	@SuppressWarnings("unchecked")
	protected List<Integer> intList(final String query, final Object param) {
		return (List<Integer>) queryForAnything(query, param, QueryFor.LIST);
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
		return this.queryForAnything(query, param, QueryFor.OBJECT);
	}

	/**
	 * Inserts an object into the database.
	 */
 public void insert(final String query, final Object param) {
		this.insertUpdateDelete(query, param, StatementType.INSERT);
	}

	/**
	 * Updates an object in the database.
	 */
	protected void update(final String query, final Object param) {
		this.insertUpdateDelete(query, param, StatementType.UPDATE);
	}

	/**
	 * Deletes an object from the database.
	 */
	protected void delete(final String query, final Object param) {
		this.insertUpdateDelete(query, param, StatementType.DELETE);
	}

	/**
	 * This is a convenience method, which calls the <em>queryForObject</em>-
	 * or the <em>queryForList</em>-method on the sqlMap. We encapsulate this
	 * method here to catch exceptions, namely SQLException, which can be thrown
	 * from that call.
	 */
	@SuppressWarnings("unchecked")
	private Object queryForAnything(final String query, final Object param, final QueryFor queryFor) {
		return this.transactionWrapper(query, param, StatementType.SELECT, queryFor);
	}

	/**
	 * This is another convenience method, which executes insert, update or
	 * delete statements.
	 */
	private void insertUpdateDelete(final String query, final Object param, final StatementType statementType) {
		this.transactionWrapper(query, param, statementType, null);
	}

	/**
	 * This method combines all calls to the SqlMap. This way we can catch the
	 * exceptions in one place and surround the queries with transaction
	 * management.<br/>
	 * 
	 * TODO: If AOP is used in the future, especially the management of
	 * transactions could be rewritten as an aspect.
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
	private Object transactionWrapper(final String query, final Object param, final StatementType statementType, final QueryFor queryFor) {
		Object rVal = null;
		try {
			// If the database is readonly we start a transaction, so we can
			// commit/abort it later
			if (this.isReadonly()) this.sqlMap.startTransaction();

			switch (statementType) {
			case SELECT:
				switch (queryFor) {
				case OBJECT:
					rVal = this.sqlMap.queryForObject(query, param);
					break;
				case LIST:
					rVal = this.sqlMap.queryForList(query, param);
					break;
				}
				break;
			case INSERT:
				this.sqlMap.insert(query, param);
				break;
			case UPDATE:
				this.sqlMap.update(query, param);
				break;
			case DELETE:
				this.sqlMap.delete(query, param);
				break;
			}
		} catch (final SQLException ex) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "Couldn't execute query '" + query + "'");
		} finally {
			try {
				// If the database is writeable we commit the transaction
				// FIXME transaction management needs improvement
				// if (!this.isReadonly()) this.sqlMap.commitTransaction();
				if (false) throw new SQLException(); // XXX keeps the try-catch-block intact
			} catch (final SQLException ex) {
				ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "Couldn't commit transaction for query '" + query + "'");
			} finally {
				try {
					// Regardless of the commit we have to call endTransaction
					if (this.isReadonly()) this.sqlMap.endTransaction();
				} catch (final SQLException ex) {
					ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "Couldn't end transaction for query '" + query + "'");
				}
			}
		}
		return rVal;
	}
}