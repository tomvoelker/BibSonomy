package org.bibsonomy.database;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author dzo
 * @version $Id$
 */
public class AbstractDatabaseManager extends org.bibsonomy.database.common.AbstractDatabaseManager {
	
	private static final Log log = LogFactory.getLog(AbstractDatabaseManager.class);
	
	/**
	 * if Lucene should be used for full text search or the database 
	 */
	private boolean doLuceneSearch;
	
	@Override
	protected void init() {
		/*
		 * configure search mode
		 * FIXME: don't do this using JNDI ...
		 */
		try {
			doLuceneSearch = "lucene".equals(((Context) new InitialContext().lookup("java:/comp/env")).lookup("searchMode"));
		} catch (NamingException ex) {
			// try to read searchMode from system properties (used by unit tests)
			final String searchMode = System.getProperty("searchMode");
			if (searchMode != null) {
				doLuceneSearch = "lucene".equals(searchMode);
			} else {
				doLuceneSearch = false;
				log.error("Error when trying to read environment variable 'searchmode' via JNDI / System.", ex);
			}
		}
		
		log.info("full text search is done by lucene : " + doLuceneSearch);
	}
	
	/**
	 * @return <code>true</code> iff search via lucene
	 */
	public boolean isDoLuceneSearch() {
		return this.doLuceneSearch;
	}
}
