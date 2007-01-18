package org.bibsonomy.ibatis.db;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.ibatis.params.BibTexParam;
import org.bibsonomy.ibatis.params.BookmarkParam;
import org.bibsonomy.ibatis.util.DatabaseUtils;
import org.bibsonomy.ibatis.util.ExceptionUtils;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Tag;

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
public abstract class AbstractDatabaseManager {

	/** Logger */
	protected static final Logger log = Logger.getLogger(AbstractDatabaseManager.class);
	/** Communication with the database is done with the sqlMap */
	private final SqlMapClient sqlMap;
	private boolean readonly;

	/** Used to determine whether we want to retrieve an object or a list */
	private enum QueryFor {
		OBJECT, LIST;
	}

	/**
	 * Initializes the SqlMap
	 */
	public AbstractDatabaseManager() {
		this.sqlMap = DatabaseUtils.getSqlMapClient(log);
		this.readonly = false;
	}

	public boolean isReadonly() {
		return this.readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	/**
	 * Can be used to start a query that retrieves a list of bookmarks.
	 */
	@SuppressWarnings("unchecked")
	protected List<Bookmark> bookmarkList(final String query, final BookmarkParam param) {
		return (List<Bookmark>) queryForAnything(query, param, QueryFor.LIST);
	}

	/**
	 * Can be used to start a query that retrieves a list of BibTexs.
	 */
	@SuppressWarnings("unchecked")
	protected List<BibTex> bibtexList(final String query, final BibTexParam param) {
		return (List<BibTex>) queryForAnything(query, param, QueryFor.LIST);
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
	 * This method calls the <em>queryForObject</em>- or the
	 * <em>queryForList</em>-method on the sqlMap. We encapsulate this method
	 * here to catch exceptions, namely SQLException, which can be thrown from
	 * that call.
	 */
	@SuppressWarnings("unchecked")
	private Object queryForAnything(final String query, final Object param, final QueryFor queryFor) {
		Object rVal = null;
		try {
			this.sqlMap.startTransaction();
			switch (queryFor) {
			case OBJECT:
				rVal = this.sqlMap.queryForObject(query, param);
				break;
			case LIST:
				rVal = this.sqlMap.queryForList(query, param);
				break;
			}
		  
		} catch (final SQLException ex) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "Couldn't execute query '" + query + "'");
		} finally{
			try {
				if(this.isReadonly()){
					this.sqlMap.endTransaction();	
					
				}
				else{
				this.sqlMap.commitTransaction();	
					
				}
			}  catch (final SQLException ex) {
				ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "Couldn't execute query '" + query + "'");
			} 
			}
		return rVal;
	}

	/**
	 * Inserts an object into the database.
	 */
	protected void insert(final String query, final Object param) {
		this.insertUpdateDelete(query, param, InsertUpdateDelete.INSERT);
	}

	/**
	 * Updates an object in the database.
	 */
	protected void update(final String query, final Object param) {
		this.insertUpdateDelete(query, param, InsertUpdateDelete.UPDATE);
	}

	/**
	 * Deletes an object from the database.
	 */
	protected void delete(final String query, final Object param) {
		this.insertUpdateDelete(query, param, InsertUpdateDelete.DELETE);
	}

	/** Used to determine the execution of an insert, update or delete. */
	enum InsertUpdateDelete {
		INSERT, UPDATE, DELETE;
	}

	private void insertUpdateDelete(final String query, final Object param, final InsertUpdateDelete insertUpdateDelete) {
		try {
			this.sqlMap.startTransaction();
			switch (insertUpdateDelete) {
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
		}
		finally{
			try {
				if(this.isReadonly()){
					this.sqlMap.endTransaction();	
					
				}
				else{
				this.sqlMap.commitTransaction();	
					
				}
				
			}  catch (final SQLException ex) {
				ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "Couldn't execute query '" + query + "'");
			} 
			}
	}
}