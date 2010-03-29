package org.bibsonomy.database;

import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.QueryFor;
import org.bibsonomy.database.util.StatementType;

/**
 * This is the superclass for all classes that are implementing methods to
 * retrieve data from a database. It provides methods for the interaction with
 * the database, i.e. a lot of convenience methods that return the
 * <em>right</em> results, e.g. not just an Object but a BibTex object or not
 * just a list of Objects but a list of bookmarks. This way a lot of unchecked
 * casting remains in this class and isn't scattered all over the code.
 * 
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public class AbstractDatabaseManager {
	private static final Log log = LogFactory.getLog(AbstractDatabaseManager.class);
	
	/**
	 * if Lucene should be used for full text search or the database 
	 */
	protected boolean doLuceneSearch = false;

	public boolean isDoLuceneSearch() {
		return this.doLuceneSearch;
	}
	
	public AbstractDatabaseManager() {
		/*
		 * configure search mode
		 * FIXME: don't do this using JNDI ...
		 */
		try {
			doLuceneSearch = "lucene".equals(((Context) new InitialContext().lookup("java:/comp/env")).lookup("searchMode"));
		} catch (NamingException ex) {
			// try to read searchMode from system properties (used by unit tests)
			if (System.getProperty("searchMode") != null) {
				doLuceneSearch = "lucene".equals(System.getProperty("searchMode"));
			}
			else {
				log.error("Error when trying to read environment variable 'searchmode' via JNDI / System.", ex);
			}
		}
		log.info("full text search is done by lucene : " + doLuceneSearch);
	}
	
	/**
	 * Can be used to start a query that retrieves a list of objectf of a certain type.
	 */
	@SuppressWarnings("unchecked")
	protected <T> List<T> queryForList(final String query, final Object param, @SuppressWarnings("unused") final Class<T> type, final boolean ignoreException, final DBSession session) {
		return (List<T>) this.queryForAnything(query, param, QueryFor.LIST, ignoreException, session);
	}

	protected <T> List<T> queryForList(final String query, final Object param, final Class<T> type, final DBSession session) {
		return queryForList(query, param, type, false, session);
	}

	/**
	 * short form of queryForList without Type argument
	 * 
	 * XXX: do we really want to use these?
	 */
	@SuppressWarnings("unchecked")
	protected List queryForList(final String query, final Object param, final boolean ignoreException, final DBSession session) {
		return queryForList(query, param, Object.class, ignoreException, session);
	}

	@SuppressWarnings("unchecked")
	protected List queryForList(final String query, final Object param, final DBSession session) {
		return queryForList(query, param, Object.class, false, session);
	}

	/**
	 * Can be used to start a query that retrieves a single object like a tag or
	 * bookmark but also an int or boolean.<br/>
	 * 
	 * In this case we break the rule to create one method for every return
	 * type, because with a single object it doesn't result in an unchecked
	 * cast.
	 * 
	 * FIXME: what to do if {@link #queryForAnything(String, Object, QueryFor, boolean, DBSession)} returns null
	 */
	@SuppressWarnings("unchecked")
	protected <T> T queryForObject(final String query, final Object param, @SuppressWarnings("unused") Class<T> type, final boolean ignoreException, final DBSession session) {
		return (T) this.queryForAnything(query, param, QueryFor.OBJECT, ignoreException, session);
	}

	protected <T> T queryForObject(final String query, final Object param, Class<T> type, final DBSession session) {
		return this.queryForObject(query, param, type, false, session);
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T queryForObject(final String query, final Object param, T result, final boolean ignoreException, final DBSession session) {
		return (T) this.queryForAnything(query, param, result, QueryFor.OBJECT, ignoreException, session);
	}
	
	protected <T> T queryForObject(final String query, final Object param, T result, final DBSession session) {
		return this.queryForObject(query, param, result, false, session);
	}
	
	/**
	 * @see #queryForObject(String, Object, DBSession)
	 */
	protected Object queryForObject(final String query, final Object param, final boolean ignoreException, final DBSession session) {
		return this.queryForAnything(query, param, QueryFor.OBJECT, ignoreException, session);
	}

	protected Object queryForObject(final String query, final Object param, final DBSession session) {
		return this.queryForAnything(query, param, QueryFor.OBJECT, false, session);
	}

	/**
	 * This is a convenience method, which calls the <em>queryForObject</em>-
	 * or the <em>queryForList</em>-method on the sqlMap. We encapsulate this
	 * method here to catch exceptions, namely SQLException, which can be thrown
	 * from that call.
	 */
	private Object queryForAnything(final String query, final Object param, final QueryFor queryFor, final boolean ignoreException, final DBSession session) {
		return session.transactionWrapper(query, param, null, StatementType.SELECT, queryFor, ignoreException);
	}
	
	/**
	 * This is a convenience method, which calls the <em>queryForObject</em>-
	 * or the <em>queryForList</em>-method on the sqlMap. We encapsulate this
	 * method here to catch exceptions, namely SQLException, which can be thrown
	 * from that call.
	 */
	private Object queryForAnything(final String query, final Object param, Object result, final QueryFor queryFor, final boolean ignoreException, final DBSession session) {
		return session.transactionWrapper(query, param, result, StatementType.SELECT, queryFor, ignoreException);
	}
	
	/**
	 * Inserts an object into the database.
	 */
	protected void insert(final String query, final Object param, final boolean ignoreException, final DBSession session) {
		this.insertUpdateDelete(query, param, StatementType.INSERT, ignoreException, session);
	}

	protected void insert(final String query, final Object param, final DBSession session) {
		this.insertUpdateDelete(query, param, StatementType.INSERT, false, session);
	}

	/**
	 * Updates an object in the database.
	 */
	protected void update(final String query, final Object param, final boolean ignoreException, final DBSession session) {
		this.insertUpdateDelete(query, param, StatementType.UPDATE, ignoreException, session);
	}

	protected void update(final String query, final Object param, final DBSession session) {
		this.insertUpdateDelete(query, param, StatementType.UPDATE, false, session);
	}

	/**
	 * Deletes an object from the database.
	 */
	protected void delete(final String query, final Object param, final boolean ignoreException, final DBSession session) {
		this.insertUpdateDelete(query, param, StatementType.DELETE, ignoreException, session);
	}

	protected void delete(final String query, final Object param, final DBSession session) {
		this.insertUpdateDelete(query, param, StatementType.DELETE, false, session);
	}

	/**
	 * This is another convenience method, which executes insert, update or
	 * delete statements.
	 */
	private void insertUpdateDelete(final String query, final Object param, final StatementType statementType, final boolean ignoreException, final DBSession session) {
		session.transactionWrapper(query, param, null, statementType, null, ignoreException);
	}
}