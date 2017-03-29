/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.Author;

/**
 * @author Christian Claus
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
		// noop
	}
	
	/**
	 * TODO: improve documentation
	 * 
	 * @param session
	 * @return list of authors
	 */
	public List<Author> getAuthors(final DBSession session) {
		return queryForList("getAuthors", null, Author.class, session);
	}
}
