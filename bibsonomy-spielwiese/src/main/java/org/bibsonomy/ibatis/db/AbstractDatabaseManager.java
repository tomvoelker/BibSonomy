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
	 * 
	 * @throws SQLException
	 */
	//@SuppressWarnings("unchecked")
	protected List<Bookmark> bookmarkList(final String query, final BookmarkParam param) {
//		List<Bookmark> rVal = null;
//		try {
//			rVal = this.sqlMap.queryForList(query, param);
//		} catch (final SQLException ex) {
//			log.error("Couldn't queryForList '" + query + "' - throwing RuntimeException");
//			throw new RuntimeException("Couldn't queryForList '" + query + "'", ex);
//		}
//		return rVal;
		return queryForList(query, param);
	}

	/**
	 * Can be used to start a query that retrieves a list of BibTexs.
	 * 
	 * @throws SQLException
	 */
//	@SuppressWarnings("unchecked")
//	protected List<BibTex> bibtexList(final String query, final BibTexParam param) throws SQLException {
	protected List<BibTex> bibtexList(final String query, final BibTexParam param) {
//		return (List<BibTex>) this.sqlMap.queryForList(query, param);
		return queryForList(query, param);
	}

	protected List<Tag> tagList(final String query, final Object param) {
		return queryForList(query, param);
	}

//	private List<Bookmark> queryForList(final String query, final BookmarkParam param) {
//		return queryForList(query, param);//, new Bookmark());
//	}
//
//	private List<BibTex> queryForList(final String query, final BibTexParam param) {
//		return queryForList(query, param);
//	}

	/**
	 * This method calls the <em>queryForList</em>-Method on the sqlMap. We
	 * encapsulate this method here to catch exceptions, namely SQLException,
	 * which can be thrown from that call.<br/>
	 */
	@SuppressWarnings("unchecked")
//	private <T> List<T> queryForList(final String query, final GenericParam param, final T type) {
	private <T> List<T> queryForList(final String query, final Object param) {
		List<T> rVal = null;
		try {
			rVal = this.sqlMap.queryForList(query, param);
		} catch (final SQLException ex) {
			log.error("Couldn't queryForList '" + query + "' - throwing RuntimeException");
			throw new RuntimeException("Couldn't queryForList '" + query + "'", ex);
		}
		return rVal;
	}

	/**
	 * This method calls the <em>queryForObject</em>-Method on the sqlMap. We
	 * encapsulate this method here to catch exceptions, namely SQLException,
	 * which can be thrown from that call.
	 */
	protected Object queryForObject(final String query, final Object param) {
		Object rVal = null; 
		try {
			rVal = this.sqlMap.queryForObject(query, param);
		} catch (final SQLException ex) {
			log.error("Couldn't queryForObject '" + query + "' - throwing RuntimeException");
			throw new RuntimeException("Couldn't queryForObject '" + query + "'", ex);
		}
		return rVal;
	}
}