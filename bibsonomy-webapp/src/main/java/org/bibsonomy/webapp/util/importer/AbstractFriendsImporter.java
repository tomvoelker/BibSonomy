/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.util.importer;

import java.util.Collection;

import org.bibsonomy.model.User;

/**
 * base class for different user relation importers
 * 
 * @author fei
 * @param <U> type of the imported user
 */
public abstract class AbstractFriendsImporter<U> {

	/**
	 * user adapter for mapping imported friends to bibsonomy user objects
	 * 
	 * @author fei
	 *
	 * @param <U> type of the imported user
	 */
	public interface UserAdapter<U> {
		/**
		 * @param user the imported user object
		 * @return name representation of the imported user
		 */
		public User getUser(final U user);
	}

	/**
	 * get the user adaptor for the imported user
	 * 
	 * @return a suitable user adaptor 
	 */
	abstract public UserAdapter<U> getUserAdapter();
	
	/**
	 * retrieve a list of users for the given login user
	 * 
	 * @param loginUser BibSonomy's login user
	 * @return list of imported friend objects
	 */
	abstract public Collection<U> getFriends(User loginUser);
}
