package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.Author;

/**
 * @author Christian Claus
 * @version $Id$
 */
public class AuthorDatabaseManager extends AbstractDatabaseManager {
	private final static AuthorDatabaseManager singleton = new AuthorDatabaseManager();

	/**
	 * @return AuthorDatabaseManager
	 */
	public static AuthorDatabaseManager getInstance() {
		return singleton;
	}
	
	private AuthorDatabaseManager() {
	}
	
	/**
	 * TODO: improve documentation
	 * 
	 * @param session
	 * @return list of authors
	 */
	@SuppressWarnings("unchecked")
	public List<Author> getAuthors(final DBSession session) {
		return queryForList("getAuthors", null, session);
	}
}
