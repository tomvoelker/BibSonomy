package org.bibsonomy.database.common;

import java.util.LinkedList;
import java.util.List;

/** 
 * This is the superclass for all classes that are implementing methods to
 * retrieve data from a database. It provides methods for the interaction with
 * the database, i.e. a lot of convenience methods that return the
 * <em>right</em> results, e.g. not just an Object but a User object or not
 * just a list of Objects but a list of bookmarks. This way a lot of unchecked
 * casting remains in this class and isn't scattered all over the code.
 * 
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public abstract class AbstractDatabaseManager {

	/**
	 * Can be used to start a query that retrieves a list of objects of a certain type.
	 * PLEASE NOTE: this methods never returns null, only an empty list if the queryForList returns null
	 */
	@SuppressWarnings("unchecked")
	protected <T> List<T> queryForList(final String query, final Object param, final Class<T> type, final DBSession session) {
		final List<T> list = (List<T>) session.queryForList(query, param);
		return list != null ? list : new LinkedList<T>();
	}
	
	/**
	 * short form of queryForList without Type argument
	 */
	@SuppressWarnings("unchecked")
	protected List queryForList(final String query, final Object param, final DBSession session) {
		return queryForList(query, param, Object.class, session);
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
	protected <T> T queryForObject(final String query, final Object param, final Class<T> type, final DBSession session) {
		return (T) session.queryForObject(query, param);
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T queryForObject(final String query, final Class<T> type, final DBSession session) {
		return (T) session.queryForObject(query, null);
	}

	/**
	 * 
	 * @param query
	 * @param param
	 * @param session
	 * @return
	 */
	protected Object queryForObject(final String query, final Object param, final DBSession session) {
		return this.queryForObject(query, param, Object.class, session);
	}
	
	/**
	 * 
	 * @param <T>
	 * @param query
	 * @param param
	 * @param store
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> T queryForObject(final String query, final Object param, final T store, final DBSession session) {
		return (T) session.queryForObject(query, param, store);
	}
	
	/**
	 * Inserts an object into the database.
	 */
	protected Object insert(final String query, final Object param, final DBSession session) {
		return session.insert(query, param);
	}

	/**
	 * Updates an object in the database.
	 */
	protected void update(final String query, final Object param, final DBSession session) {
		session.update(query, param);
	}

	/**
	 * Deletes an object from the database.
	 */
	protected void delete(final String query, final Object param, final DBSession session) {
		session.delete(query, param);
	}
}