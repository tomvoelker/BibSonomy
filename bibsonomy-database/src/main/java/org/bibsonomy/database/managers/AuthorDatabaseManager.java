package org.bibsonomy.database.managers;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Author;

/**
 * @author Christian Claus
 * @version $Id$
 */
public class AuthorDatabaseManager extends AbstractDatabaseManager {
	private static final Log LOG = LogFactory.getLog(AuthorDatabaseManager.class);
	private final static AuthorDatabaseManager singleton = new AuthorDatabaseManager();

	/**
	 * @return AuthorDatabaseManager
	 */
	public static AuthorDatabaseManager getInstance() {
		return singleton;
	}
	
	private AuthorDatabaseManager() {	}
	
	/**
	 * TODO: improve documentation
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Author> getAuthors(final DBSession session) {
		return queryForList("getAuthors", null, session);
	}
}
