package org.bibsonomy.ibatis.db;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.ibatis.params.BibTexParam;
import org.bibsonomy.ibatis.params.BookmarkParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Tag;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * This is the superclass for all classes that are implementing methods to
 * retrieve data from a database. It provides methods for the interaction with
 * the database.
 * 
 * @author Christian Schenk
 */
public abstract class AbstractDatabaseManager {

	/** Logger */
	protected static final Logger log = Logger.getLogger(AbstractDatabaseManager.class);
	/** Communication with the database is done with the sqlMap */
	private final SqlMapClient sqlMap;

	/** Determines whether we retrieve an object or a list */
	private enum QueryFor {
		OBJECT, LIST;
	}

	/**
	 * Initializes the SqlMap
	 */
	public AbstractDatabaseManager() {
		try {
			final String resource = "SqlMapConfig.xml";
			final Reader reader = Resources.getResourceAsReader(resource);
			this.sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
		} catch (final IOException ex) {
			log.error("Couldn't initialize SqlMap - throwing RuntimeException");
			throw new RuntimeException("Couldn't initialize SqlMap", ex);
		}
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
	 * Can be used to start a query that retrieves a single object.
	 */
	protected Object queryForObject(final String query, final Object param) {
		return this.queryForAnything(query, param, QueryFor.OBJECT);
	}

	/**
	 * This method calls the <em>queryForObject</em>- or the
	 * <em>queryForList</em>-Method on the sqlMap. We encapsulate this method
	 * here to catch exceptions, namely SQLException, which can be thrown from
	 * that call.<br/>
	 */
	@SuppressWarnings("unchecked")
	private Object queryForAnything(final String query, final Object param, final QueryFor queryFor) {
		Object rVal = null;
		try {
			switch (queryFor) {
			case OBJECT:
				rVal = this.sqlMap.queryForObject(query, param);
				break;
			case LIST:
				rVal = this.sqlMap.queryForList(query, param);
				break;
			}
		} catch (final SQLException ex) {
			final String error = "Couldn't execute query '" + query + "'";
			log.error(error + " - throwing RuntimeException");
			throw new RuntimeException(error, ex);
		}
		return rVal;
	}
}